import org.slf4j.Logger;

import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.utils.MessageUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.dossier.common.model.backend.MessageModel;
import com.digitech.dossier.common.model.backend.MessagesModel;
import com.digitech.dossier.common.model.backend.MessageModel.EnumMessageType;
import com.digitech.dossier.common.utils.DocumentUtils;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.debug("Script triggered on after save: afterCreate_courrierOut.groovy --- Start");

if (Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")))) {
  Integer owner = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue();
  if (!usrContext.getUserId().equals(owner)) {
    // Notifier le propri�taire
    CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_DIFFUSED, "FIELD_CODE_U_PROPRIETAIRE",
      "mail_subject_courrier_state_toWrite", "changementEtat.htm", true);
  }
}

// The courrier OUT is directly added
boolean outAttachmentExisting = CourrierScriptUtils.isAttachmentOutExisting(usrContext, theDocument);

if (outAttachmentExisting) {
    CourrierScriptUtils.addResponse(usrContext, theDocument, true);
	DocumentUtils.saveDocument(theDocument);
	
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

log.debug("Script triggered on after save: afterCreate_courrierOut.groovy --- End");