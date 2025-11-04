import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.debug("Script triggered on before save: beforeUpdate_courrierOut.groovy --- Start");

String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
Integer etatCourrant = (Integer)theDocument.getField(etatCourrierFieldCode).getValue();
Integer signer = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")).getValue();

if(etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")))) {
  String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
  if (signer != null) {
    Integer visaState = (Integer)theDocument.getField(etatVisaFieldCode).getValue();
    if (CourrierScriptUtils.getTermID(theDocument, etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_REFUSE")).equals(visaState)) {
      // Resets the visa state
      FieldUtils.setValue(theDocument, etatVisaFieldCode, CourrierScriptUtils.getTermID(theDocument, etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_INDEFINI")));
    }
  }

  String oldSigner = theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")).getAirsValue();
  if (oldSigner == null && signer != null || oldSigner != null && signer == null || oldSigner != null && signer != null && !signer.equals(Integer.valueOf(oldSigner))) {
    // If the signer is changed, resets the visa state
    FieldUtils.setValue(theDocument, etatVisaFieldCode, CourrierScriptUtils.getTermID(theDocument, etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_INDEFINI")));
  }
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered on before save: beforeUpdate_courrierOut.groovy --- End");
