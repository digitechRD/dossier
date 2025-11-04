import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel
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

log.debug("Script triggered on validate : validate_courrierIn.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

// Mise à jour de l'état du document...
String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE") ));
// ... et de la date de validation
FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_VALIDATION"), new Date());

CourrierScriptUtils.saveDocument(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_VALIDATED, true);

// Ajout du commentaire
AbstractSharingModel sharingModel = CourrierScriptUtils.getSharingModel((Boolean)customActionModel.getModalPanelModel().get("public"), theDocument);
DocumentUtils.addComment(theDocument, (String)customActionModel.getModalPanelModel().get("comment"), sharingModel);

// Mail notification
if (Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")))) {
  // Notifier le propriétaire
  File templateFile = new File(CourrierUtils.getCourrierTemplateFolderPath(), "changementEtat.htm");
  IUser userMgr = (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  
  Map<String, Object> customPropertyMap = new HashMap<String, Object>();
  customPropertyMap.put("state", CourrierAdvancedAuditType.ADV_EVENT_COURRIER_VALIDATED);
  User user = userMgr.getUser((Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue());
  ApplicationUtils.sendMail(usrContext, Arrays.asList(theDocument), templateFile, new ReportPerson(usrContext, user), BundleUtils.getTranslation("mail_subject_courrier_state_validated"), customPropertyMap);
}

// On affiche la page des résultats de recherche
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));

log.debug("Script triggered on validate : validate_courrierIn.groovy --- End");

