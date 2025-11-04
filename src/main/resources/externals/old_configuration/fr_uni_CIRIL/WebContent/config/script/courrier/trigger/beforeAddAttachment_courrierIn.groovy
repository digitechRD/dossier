import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

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

log.debug("Script triggered before adding attachment: beforeAddAttachment_courrierIn.groovy --- Start");

String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
Integer etatCourrant = (Integer)theDocument.getField(etatCourrierFieldCode).getValue();

if( etatCourrant != null && !etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER")))) {
  // This code is for the response adding (not direct attachment tab edition)

  log.debug("Response has been added, refresh attachments view...");

  List<IAttachment> newAttachments = new ArrayList<IAttachment>();
  for (IAttachment attachment : theDocument.getAttachments(usrContext)) {
    if(attachment.getMode() == com.digitech.dossier.common.model.backend.airs.IDocument.MODE_ADD && CourrierScriptUtils.getConstant("ATTACHMENT_TYPE_COURRIER_OUT").equals(attachment.getType())) {
      CourrierScriptUtils.addResponse(usrContext, theDocument, false);
      newAttachments.add(attachment);
    }
  }

  if (!newAttachments.isEmpty()) {
    List<IAttachment> finalAttachments = theDocument.getAttachments(usrContext);
    finalAttachments.addAll(newAttachments);

    // On raffiche la page courante et on rafraichit la page des résultats de recherche
    NavigationUtils.gotoCurrentPage(true, false, theDocument);
  }
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered before adding attachment: beforeAddAttachment_courrierIn.groovy --- End");