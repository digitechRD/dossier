import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/
UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;
ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: D_VALIDATION_init_read_CourrierIn.groovy --- Start");

boolean isInState = CourrierScriptUtils.hasState(usrContext, theDocument, Arrays.asList(CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE"), CourrierScriptUtils.getConstant("STATE_CODE_REPONDU"), CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE")));
theOutput.getValue().getProperties().put(FieldProperty.DISPLAYED, Boolean.valueOf(!isInState).toString());

logger.debug("Script field initialization: D_VALIDATION_init_read_CourrierIn.groovy --- End");

