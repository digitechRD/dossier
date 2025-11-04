// Check if this mail can be restored 

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;
import org.apache.commons.lang.StringUtils;

import static CourrierScriptUtils;

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on restore visibility : restoreMailVisible_courrierOut.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;

ScriptResultValueDisplayRule restoreVisible = CourrierScriptUtils.isRestoreVisible(usrContext, theDocument);

if (restoreVisible.valid) {
    if (StringUtils.isBlank(Utils.getMailController().getModel().getFrom())) {
        restoreVisible.setValid(false);
    }
}

outputParam.setValue(restoreVisible);

log.debug("Script triggered on restore visibility : restoreMailVisible_courrierOut.groovy --- End");