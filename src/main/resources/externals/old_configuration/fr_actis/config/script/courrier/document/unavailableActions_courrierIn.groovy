import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions.Action;

import java.util.List;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions.Action;

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IFieldValue
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// Map<String, IFieldValue> fieldValueMap map des valeurs de champs (clé de map = code du champ)
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
Map<String, IFieldValue> theFieldValueMap = fieldValueMap;

ScriptResultModel<ScriptResultValueDocumentActions> theOutput = output;


logger.debug("Script document unavailable actions: unavailableActions_courrierIn.groovy --- Start");

Boolean haveNoAction = true;
com.digitech.jcorbairs.User usr = usrContext.getUser();
Integer userId = usr.getId();



if (UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_DIR"))){
  haveNoAction = !UserUtils.isInOrganization(usrContext, theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue(), true);
} else {
  boolean owner, validator, signer = false;
  Integer docUserOwner = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue();
  if (docUserOwner != null){
    owner = docUserOwner.equals(userId);
  }
  else {
    Integer docOrgOwner = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
    owner = UserUtils.isInOrganization(usrContext, docOrgOwner, true);
  }


  Integer docUserValidator = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR")).getValue();
  if (docUserValidator != null){
    validator = docUserValidator.equals(userId);
  }
  
  Integer docUserSigner = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")).getValue();
  if (docUserSigner != null){
    signer = docUserSigner.equals(userId);
  }
  
  haveNoAction = !owner && !validator && !signer;
}

List<Action> unavailableActions = new ArrayList<Action>();
unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
// Every body can add a comment
unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_COMMENT);
if (!haveNoAction) {
  unavailableActions.clear();
}


String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
Integer courrierState = theFieldValueMap.get(etatCourrierFieldCode).getValue();
// Is the mail in indexation?
if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER")).equals(courrierState)) {
  unavailableActions.clear();
}

// Is the mail refused?
if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REJECT")).equals(courrierState)) {
  Integer docUserCreator = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_CREAT")).getValue();
  if (docUserCreator != null && docUserCreator.equals(userId)){
    unavailableActions.clear();
  }
}


// Check the visa state
String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
Integer visaState = theFieldValueMap.get(etatVisaFieldCode).getValue();
if (CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_ACCEPTE")).equals(visaState)) {
  unavailableActions.clear();
  unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
  if(!UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
    unavailableActions.remove(Action.EDIT);
    unavailableActions.remove(Action.SAVE);
  }
  else {
    unavailableActions.clear();
  }
}


// Is the mail closed?
if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE")).equals(courrierState)) {
  unavailableActions.clear();
  unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
  if(UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
    unavailableActions.clear();
  }
}


theOutput.getValue().getUnavailableActions().addAll(unavailableActions);

logger.debug("Script document unavailable actions: unavailableActions_courrierIn.groovy --- End");