import java.util.*

import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
/************************************************/

UserContext userContext = userContext
Logger log = scriptLogger;
ScriptResultModel theOutput = output;

log.debug("Script mail action: closingSimpleView_CourrierIn.groovy --- Start");

CourrierScriptUtils.closingSimpleView(userContext, theOutput, true);

log.debug("Script mail action: closingSimpleView_CourrierIn.groovy --- End");