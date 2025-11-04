import org.slf4j.Logger

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backing.document.DocumentCreationModel;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

log.debug("Script triggered on before save: T_ETAT_COURRIER_init_insert_CourrierIn.groovy --- Start");

DocumentCreationModel dcc = Utils.getDocumentCreationController().getModel();
if( dcc != null && dcc.isKeepIndex() && theDocument.getAirsRefId() == 0 ) {
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER"), null);
}

log.debug("Script triggered on before save: T_ETAT_COURRIER_init_insert_CourrierIn.groovy --- End");