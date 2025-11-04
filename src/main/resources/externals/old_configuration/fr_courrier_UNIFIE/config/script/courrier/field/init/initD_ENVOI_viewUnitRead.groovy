import java.util.*;

import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty;
import com.digitech.dossier.common.model.backing.validator.TwoDateValidator;
import com.digitech.dossier.common.Constants;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: initD_ENVOI_viewUnitRead.groovy --- Start");

Map<String, String> paramsValidator = new HashMap<String, String>();
paramsValidator.put(TwoDateValidator.OTHER_COMPONENT_NAME, "D_RECEPTION");

theOutput.getValue().setValidatorAttributs(paramsValidator);

logger.debug("Script field initialization: initD_ENVOI_viewUnitRead.groovy --- End");