import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/** **********************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.debug("Script triggered on before save: beforeUpdate_courrierOut.groovy --- Start");

String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
Integer etatCourrant = (Integer) theDocument.getField(etatCourrierFieldCode).getValue();

// Auto Visa is disabled
/*
if (etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")))) {
    CourrierScriptUtils.prepareVisa(theDocument);
}*/

// Reset Boolean, new mail will be send
CourrierScriptUtils.markDocumentToNotifyUser(theDocument);
log.debug("Document [{}] has been mark to notified owner by mail.", theDocument.getAirsRefId());

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered on before save: beforeUpdate_courrierOut.groovy --- End");