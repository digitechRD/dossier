import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.common.Utils;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.debug("Script triggered before adding attachment: afterAddAttachment_courrierOut.groovy --- Start");
try{
	


for (IAttachment attachment : theDocument.getAttachments(usrContext)) {

  if(/*attachment.getMode() == com.digitech.dossier.common.model.backend.airs.IDocument.MODE_ADD &&*/ CourrierScriptUtils.getConstant("ATTACHMENT_TYPE_COURRIER_OUT").equals(attachment.getType())) {
	  
	  log.debug("Ajout de la réponse");
	  CourrierScriptUtils.addResponse(usrContext, theDocument, true);
    
    // On raffiche la page courante et on rafraichit la page courante
    NavigationUtils.gotoCurrentPage(false, false, theDocument);
	 //Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));
    break;
  }
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

}
catch(Exception e){
	log.error(e.getStackTrace());
}

log.debug("Script triggered before adding attachment: afterAddAttachment_courrierOut.groovy --- End");
