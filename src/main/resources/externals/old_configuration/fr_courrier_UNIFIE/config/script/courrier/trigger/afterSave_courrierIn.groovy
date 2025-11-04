import org.slf4j.Logger;

import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.MessageModel;
import com.digitech.dossier.common.model.backend.MessagesModel;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.MessageModel.EnumMessageType;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.utils.MessageUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.jcorbairs.Term;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger logger = scriptLogger;
String templateFileName = "changementEtat.htm";

logger.debug("Script triggered on after save: afterSave_courrierIn.groovy --- Start");

logger.debug("notif : ");
logger.debug("notif : " + Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")));

if (Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")))) {
  String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
  Integer etatCourrant = (Integer)theDocument.getField(etatCourrierFieldCode).getValue();

  Term etatTerm = etatCourrant != null && etatCourrant > 0 ? CourrierScriptUtils.getAuthorityListService().getTerm(etatCourrant) : null;
  logger.debug("docId=[" + theDocument.getAirsRefId() + "] T_ETAT_COURRIER=[" + (etatTerm == null ? "" : etatTerm.getPreferedValue()) + "]");

  if (CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_VALIDER")).equals(etatCourrant)) {
    // Notifier le valideur
    CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_VALIDATE, "FIELD_CODE_U_VALIDEUR",
      "mail_subject_courrier_state_to_validate", templateFileName, true);
  }
  else if (CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")).equals(etatCourrant)) {
    // Notifier le propriétaire
    CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_DIFFUSED, "FIELD_CODE_U_PROPRIETAIRE",
      "mail_subject_courrier_state_diffused", templateFileName, true);
  }
  else if (CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")).equals(etatCourrant)) {
    // Notifier le viseur
    CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_SIGN, "FIELD_CODE_U_VISEUR",
      "mail_subject_courrier_state_to_sign", templateFileName, true);
  }
}

// Removes the previous success messages
Iterator<MessageModel> iter = MessagesModel.getInstance().getPersistantFacesMessages().iterator();
while(iter.hasNext()) {
  MessageModel msgModel = iter.next();
  if (EnumMessageType.SUCCESS.equals(msgModel.getType())) {
    iter.remove();
  }
}

// Adds the new success message
Object[] args = [theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO")).getValue()] as Object[];
MessageUtils.setSuccessMessage(Utils.getFacesContext(), null, BundleUtils.getTranslation("msg_success_courrier_saved", args), null, true);

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

logger.debug("Script triggered on after save: afterSave_courrierIn.groovy --- End");