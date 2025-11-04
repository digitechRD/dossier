// Vérifier que le courrier est dans l'état "CLOTURE"

// et c'est good'

import com.digitech.dossier.common.model.backend.Constants;
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule

import static CourrierScriptUtils

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on restore visibility : restoreMailVisible_courrierIn.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

List<String> states = new ArrayList<String>()
states.add(CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE"));

// Visible if the document is in state "CLOTURE" and logged user is admin
boolean visible = CourrierScriptUtils.hasState(usrContext, theDocument, states) && UserUtils.hasProfile(usrContext, "DOS_ADMIN");
boolean documentLockedByOther = theDocument.getLockType() == Constants.DOC_LOCKED_BYOTHER;

result.setValid(visible && !documentLockedByOther);
 
outputParam.setValue(result);

log.debug("Script triggered on restore visibility : restoreMailVisible_courrierIn.groovy --- End");