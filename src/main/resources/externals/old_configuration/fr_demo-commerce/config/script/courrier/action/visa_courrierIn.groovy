import com.digitech.courrier.common.controller.VisaController
import com.digitech.courrier.common.model.VisaModel
import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel;
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.jcorbairs.User

import static CourrierScriptUtils

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on visa : visaVisibility_courrierIn.groovy --- Start");

VisaController visaController = CourrierUtils.getVisaController();
VisaModel visaModel = visaController.getModel();
String comment = visaModel.getComment();
String visaResponse = visaModel.getSelectedVisaType();

// Ajouter le pièce jointe générée modifiée
if (visaModel.isModified()) {
  AttachmentModel selectionAttachmentModel = visaModel.getAttachmentOutModel();
  IAttachment attachment = (IAttachment) selectionAttachmentModel.getCurrentAttachment(false);
  DocumentUtils.editAttachment(theDocument, attachment);
}

boolean visaOk = false;
String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
CourrierAdvancedAuditType evtType = CourrierAdvancedAuditType.ADV_EVENT_COURRIER_REFUSED;
Integer visaStateId = CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_REFUSE"));
if (Boolean.TRUE.equals(Boolean.valueOf(visaResponse))) {
  visaStateId = CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_ACCEPTE"));
  evtType = CourrierAdvancedAuditType.ADV_EVENT_COURRIER_ACCEPTED;
  visaOk = true;
}

// Mise à jour de l'état du document...
FieldUtils.setValue(theDocument, fieldCode, visaStateId);
if (visaOk) {
  // ... et de la date d'acceptation
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_ACCEPTATION"), new Date());
}
CourrierScriptUtils.saveDocument(usrContext, theDocument, evtType);

// Ajout du commentaire
AbstractSharingModel sharingModel = CourrierScriptUtils.getSharingModel(Boolean.valueOf(visaModel.isPublic()), theDocument);
DocumentUtils.addComment(theDocument, visaModel.getComment(), sharingModel);

// Mail notification
if (Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")))) {
  // Notifier le propriétaire
  File templateFile = new File(CourrierUtils.getCourrierTemplateFolderPath(), "changementEtat.htm");
  IUser userMgr = (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  
  Map<String, Object> customPropertyMap = new HashMap<String, Object>();
  customPropertyMap.put("state", visaOk ? CourrierAdvancedAuditType.ADV_EVENT_COURRIER_ACCEPTED : CourrierAdvancedAuditType.ADV_EVENT_COURRIER_REJECTED);
  User user = userMgr.getUser((Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue());
  String subject = visaOk ? BundleUtils.getTranslation("mail_subject_courrier_state_accepted") : BundleUtils.getTranslation("mail_subject_courrier_state_rejected");
  ApplicationUtils.sendMail(usrContext, Arrays.asList(theDocument), templateFile, new ReportPerson(usrContext, user), subject, customPropertyMap);
}

// On affiche la page des résultats de recherche
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));

log.debug("Script triggered on visa : visaVisibility_courrierIn.groovy --- End");

