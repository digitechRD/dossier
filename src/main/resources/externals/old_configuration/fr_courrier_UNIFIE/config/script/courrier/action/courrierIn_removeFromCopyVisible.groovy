import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;

import static CourrierScriptUtils;

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on removing from copy visibility : courrierIn_removeFromCopyVisible.groovy--- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;

ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
result.setValid(CourrierScriptUtils.canMarkAsRead(usrContext, theDocument) && CourrierScriptUtils.isDocumentAvailableForUser(usrContext, theDocument)
      && !CourrierScriptUtils.alreadyMarkAsRead(usrContext, theDocument));
outputParam.setValue(result);

log.debug("Script triggered on removing from copy visibility : courrierIn_removeFromCopyVisible.groovy --- End");