import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.controller.document.DocumentCreationController;
import com.digitech.dossier.common.model.backing.document.DocumentCreationModel;
import com.digitech.dossier.common.Utils;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger logger = scriptLogger;
ScriptResultModel theOutput = output;

logger.debug("Script triggered on change contentType: courrierIn_beforeChangeSelectType.groovy --- Start");

DocumentCreationModel model = Utils.getDocumentCreationController().getModel();

Map<String, Object> correspondance = new HashMap<String, Object>();
correspondance.put("COU_COURRIER_IN", "COU_RECU");
correspondance.put("COU_COURRIER_OUT", "COU_EMIS");

model.setCurrentAttachmentType(correspondance.get(model.getSelectedContentType()));

ScriptResultValueChecker scriptResult = new ScriptResultValueChecker();
scriptResult.setValid(true);
theOutput.setValue(scriptResult);

logger.debug("Script triggered on change contentType: courrierIn_beforeChangeSelectType.groovy --- End");