import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule
import org.slf4j.Logger

UserContext usrContext = userContext;
IDocument theDocument = document;
final String METHOD = "courrierIn_addUserInCopyVisible";


getLog().info("Script triggered on closing : " + METHOD + " --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
outputParam.setValue(result);

boolean isValid = !Utils.getViewUnitController().getModel().isAuthorizedToSave();
isValid = isValid && Constants.DOC_LOCKED_BYOTHER != theDocument.getLockType();

result.setValid(isValid);
result.setValid(true);

getLog().info("Script triggered on closing : " + METHOD + " --- Stop");


private Logger getLog() {
    return (Logger) scriptLogger;
}
