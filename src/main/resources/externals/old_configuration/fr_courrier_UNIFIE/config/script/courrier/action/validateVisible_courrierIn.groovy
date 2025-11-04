import java.util.ArrayList
import java.util.List

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;

import static CourrierScriptUtils;

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on validate visibility : validateVisible_courrierIn.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

result.setValid(CourrierScriptUtils.canValidate(theDocument, usrContext));

outputParam.setValue(result);


log.debug("Script triggered on validate visibility : validateVisible_courrierIn.groovy --- End");