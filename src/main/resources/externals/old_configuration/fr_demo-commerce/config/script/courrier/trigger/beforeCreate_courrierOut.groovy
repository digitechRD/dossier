import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker

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

log.debug("Script triggered on before save: beforeCreate_courrierOut.groovy --- Start");

// Generates the final chrono number
String numChrono = CourrierScriptUtils.generateNumChrono(usrContext.getCurrentOrgId(), usrContext, false);
if (numChrono != null){
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"), numChrono);
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered on before save: beforeCreate_courrierOut.groovy --- End");
