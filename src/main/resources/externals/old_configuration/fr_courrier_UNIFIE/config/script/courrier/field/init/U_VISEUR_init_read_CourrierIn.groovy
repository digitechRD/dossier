import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/
UserContext usrContext = userContext;
Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: U_VISEUR_init_read_CourrierIn.groovy --- Start");

CourrierScriptUtils.setVisaReadOnlyInputState(theDocument, theOutput);

CourrierScriptUtils.markFieldAsReadOnly(usrContext, logger, theDocument, theOutput);

logger.debug("Script field initialization: U_VISEUR_init_read_CourrierIn.groovy --- End");
