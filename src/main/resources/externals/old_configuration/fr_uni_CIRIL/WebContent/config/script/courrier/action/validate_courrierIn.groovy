import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.NavigationUtils;

import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on validate : validate_courrierIn.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

String currentFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR"), listFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEURS");

boolean validationProcessComplete = CourrierScriptUtils.isLastValidator(theDocument, currentFieldCode, listFieldCode);

if( !validationProcessComplete ) {
  Integer nextValidator = CourrierScriptUtils.getNextValidator(theDocument, currentFieldCode, listFieldCode);
  log.debug("docId [" + theDocument.getAirsRefId()  + "] jump to next validator [" + nextValidator  + "]");

  FieldUtils.setValue(theDocument, currentFieldCode, nextValidator);
}
else {
  log.debug("Validation docId [" + theDocument.getAirsRefId()  + "] complete.");

  // Mise à jour de l'état du document...
  String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
  FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")));
  // ... et de la date de validation
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_VALIDATION"), new Date());
}

CourrierScriptUtils.saveDocument(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_VALIDATED, true);

// Ajout du commentaire
AbstractSharingModel sharingModel = CourrierScriptUtils.getSharingModel((Boolean)customActionModel.getModalPanelModel().get("public"), theDocument);
DocumentUtils.addComment(theDocument, (String)customActionModel.getModalPanelModel().get("comment"), sharingModel);

// Mail notification
if( Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED"))) ) {
  String templateFileName = "changementEtat.htm";
  if( validationProcessComplete ) {
    // Notifier le propriétaire
    CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_VALIDATED, "FIELD_CODE_U_PROPRIETAIRE",
      "mail_subject_courrier_state_validated", templateFileName, true);
  } else {
    // Notifier le prochain valideur
    CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_VALIDATE, "FIELD_CODE_U_VALIDEUR",
      "mail_subject_courrier_state_to_validate", templateFileName, true);
  }
}

// On affiche la page des résultats de recherche
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));

log.debug("Script triggered on validate : validate_courrierIn.groovy --- End");