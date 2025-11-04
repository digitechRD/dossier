import org.slf4j.Logger;

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

log.debug("Script triggered on before save: N_CHRONO_init_insert_CourrierIn.groovy --- Start");

DocumentCreationModel dcc = Utils.getDocumentCreationController().getModel();
if( dcc != null && dcc.isKeepIndex() ) {
  // Keep index mode : generate a new chrono number

  if( theDocument.getAirsRefId() == 0 ) {
    Integer serviceId = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
    if( serviceId != null && serviceId.intValue() > 0 ) {
      String numChrono = CourrierScriptUtils.generateNumChrono(serviceId, usrContext, true);
      if (numChrono != null){
        FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"), numChrono);
        log.debug("KeepIndex mode : docId=[" + theDocument.getAirsRefId() + "] generate_TEMP_NumChrono=[" + numChrono + "]");
      }
    }
  }
}

log.debug("Script triggered on before save: N_CHRONO_init_insert_CourrierIn.groovy --- End");