import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IFieldValue
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions.Action;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// Map<String, IFieldValue> fieldValueMap map des valeurs de champs (clÃ© de map = code du champ)
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
Map<String, IFieldValue> theFieldValueMap = fieldValueMap;

ScriptResultModel<ScriptResultValueDocumentActions> theOutput = output;


logger.debug("Script document unavailable actions: unavailableActions_courrierOut.groovy --- Start");

Boolean haveNoAction = true;
com.digitech.jcorbairs.User usr = usrContext.getUser();
if (UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_DIR"))){
  haveNoAction = !UserUtils.isInSameOrganization(usrContext, theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue(), true);
} else {
  Integer userId = usr.getId();
  
  boolean owner, signer = false;
  Integer docUserOwner = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue();
  if (docUserOwner != null){
    owner = docUserOwner.equals(userId);
  }
  
  Integer docUserSigner = null;
  if( theFieldValueMap.containsKey(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")) ) {
    docUserSigner = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")).getValue();
    if (docUserSigner != null){
      signer = docUserSigner.equals(userId);
    }
  }
  
  haveNoAction = !owner && !signer;
}

List<Action> unavailableActions = new ArrayList<Action>();
unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
// Every body can add a comment
unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_COMMENT);
if (!haveNoAction) {
  unavailableActions.clear();
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

// Si l'utilisateur est un administrateur, toutes les actions sont permises tant que le visa n'est pas accepté
else if(UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN")))
{
	unavailableActions.clear();
}

// Is the mail closed?
String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
Integer courrierState = theFieldValueMap.get(etatCourrierFieldCode).getValue();
if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE")).equals(courrierState)) {
  unavailableActions.clear();
  unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
  if(UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
    unavailableActions.clear();
  }
}

theOutput.getValue().getUnavailableActions().addAll(unavailableActions);

logger.debug("Script document unavailable actions: unavailableActions_courrierOut.groovy --- End");