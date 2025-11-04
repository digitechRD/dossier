import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backing.document.DocumentCreationModel;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

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

log.debug("Script triggered on before save: beforeCreate_courrierIn.groovy --- Start");

DocumentCreationModel dcc = Utils.getDocumentCreationController().getModel();
if( dcc != null && dcc.isKeepIndex() ) {
  // Kepp index mode : generate a new chrono number

  if( theDocument.getAirsRefId() == 0 ) {
    Integer serviceId = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
    if( serviceId != null && serviceId.intValue() > 0 ) {
      String numChrono = CourrierScriptUtils.generateNumChrono(serviceId, usrContext, true);
      if (numChrono != null){
        IField field = theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"));
        field.setValue(numChrono);

        /*if( dcc.getMapInitialFieldValue() != null && dcc.getMapInitialFieldValue().containsKey(CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO")) ) {
        IField field = dcc.getMapInitialFieldValue().get(CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"));
        field.setValue(numChrono);
        }*/

        log.debug("KeepIndex mode : generateNumChrono=[" + numChrono + "]");
      }
    }
  }
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered on before save: beforeCreate_courrierIn.groovy --- End");