// Check if this mail can be restored 

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;

import static CourrierScriptUtils;

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on restore visibility : restoreMailVisible_courrierIn.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
 
outputParam.setValue(CourrierScriptUtils.isRestoreVisible(usrContext, theDocument));

log.debug("Script triggered on restore visibility : restoreMailVisible_courrierIn.groovy --- End");