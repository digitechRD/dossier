import org.slf4j.Logger

import com.digitech.airs3dossiers.airs.AirsFile;
import com.digitech.airs3dossiers.airs.AirsDocument;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backing.document.DocumentCreationModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.resources.BundleUtils;

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

logger.debug("Script field initialization: U_COPIES_LUES_markAsRead_CourrierIn.groovy --- Start");

// Ce script n'est plus utilisé, les documents ne sont plus marqués comme lu dès qu'ils sont consultés
//CourrierScriptUtils.markAsRead(usrContext, theDocument, theOutput);

logger.debug("Script field initialization: U_COPIES_LUES_markAsRead_CourrierIn.groovy --- End");
