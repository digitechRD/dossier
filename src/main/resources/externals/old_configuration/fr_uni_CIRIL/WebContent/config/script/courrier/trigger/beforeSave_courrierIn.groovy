import java.util.*;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.jcorbairs.Term;

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

log.debug("Script triggered on before save: beforeSave_courrierIn.groovy --- Start");

String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");

Integer etatCourrant = (Integer)theDocument.getField(etatCourrierFieldCode).getValue();

// CREATION, INDEXATION
if (etatCourrant == null || etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER")))) {
  log.debug("docId=[" + document.getAirsRefId() + "] etat=[STATE_CODE_A_INDEXER]");

  // Adds the document creator
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_CREAT"), usrContext.getUser().getId());

  // Set the first validator
  FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR"), (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEURS")).getValue());

  // Generates the final chrono number
  Integer serviceId = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
  String numChrono = CourrierScriptUtils.generateNumChrono(serviceId, usrContext, false);
  if (numChrono != null){
    log.debug("docId=[" + theDocument.getAirsRefId() + "] generate_FINAL_NumChrono=[" + numChrono + "]");
    FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"), numChrono);
  }
  else
    log.error("docId=[" + theDocument.getAirsRefId() + "] chrono generation failed !");
}

// CREATION, INDEXATION, REJECTED
if (etatCourrant == null || etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER")))
  || etatCourrant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REJECT")))) {

  Integer idValideur = (Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR")).getValue();
  log.debug("docId=[" + document.getAirsRefId() + "] etat=[null || STATE_CODE_A_INDEXER || STATE_CODE_REJECT] idValideur=[" + idValideur + "]");

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
  CourrierScriptUtils.prepareVisa(theDocument);
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered on before save: beforeSave_courrierIn.groovy --- End");