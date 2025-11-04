import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
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

log.debug("Script triggered on before save: beforeSave_courrierIn.groovy --- Start");

String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
Integer signer = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")).getValue();

Integer etatCourrant = (Integer)theDocument.getField(etatCourrierFieldCode).getValue();
// CREATION, INDEXATION
if (etatCourrant == null || etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER")))) {
  // Adds the document creator
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_CREAT"), usrContext.getUser().getId());
  
  // Generates the final chrono number

  String societeCode = "";
  String fieldCodeSociete = CourrierScriptUtils.getConstant("FIELD_CODE_G_SOCIETE");
  Integer societeId = FieldUtils.getValue(theDocument, fieldCodeSociete);

  if (societeId == null){
      throw new IllegalStateException("No Fiels with CODE  " + fieldCodeSociete);
  }
  log.debug("Script triggered on before save: beforeCreate_courrierin.groovy --- CLE societeId " + societeId);


  //Integer serviceId = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
  String numChrono = CourrierScriptUtils.generateNumChronoActis(societeId, usrContext, false);
  if (numChrono != null){
    FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"), numChrono);
  }
}

// CREATION, INDEXATION, REJECTED
if (etatCourrant == null || etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER"))) ||
etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REJECT")))) {
  Integer idValideur = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR")).getValue();
  // If no validator is defined, the T_ETAT_COURRIER must be set to "Diffusé"
  if (idValideur == null) {
    // If an attachment OUT is existing, the T_ETAT_COURRIER must be set to "Repondu"
    boolean outAttachmentExisting = CourrierScriptUtils.isAttachmentOutExisting(usrContext, theDocument);
    if (outAttachmentExisting) {
      FieldUtils.setValue(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")));
    }
    else {
      FieldUtils.setValue(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")));
    }
  } else {
    // If a validator is defined, the T_ETAT_COURRIER must be set to "A Valider"
    FieldUtils.setValue(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_VALIDER")));
  }
}
// RESPONDED
else if(etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")))) {
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

log.debug("Script triggered on before save: beforeSave_courrierIn.groovy --- End");
