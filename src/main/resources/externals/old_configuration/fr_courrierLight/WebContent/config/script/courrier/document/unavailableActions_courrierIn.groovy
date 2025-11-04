import java.util.List;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IFieldValue;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions.Action;
import com.digitech.jcorbairs.Term;

import static CourrierScriptUtils;

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

IFieldValue orgaProprietaire = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"));
IFieldValue userProprietaire = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE"));

String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
IFieldValue etatCourrierField = theFieldValueMap.get(etatCourrierFieldCode);
Integer courrierState = etatCourrierField == null ? null : etatCourrierField.getValue();
Term etatTerm = CourrierScriptUtils.getAuthorityListService().getTerm(courrierState);

logger.debug("userId=[" + userId + "] courrierState=[" + (etatTerm == null ? "" : etatTerm.getPreferedValue()) + "] orgaProprietaire=[" + (orgaProprietaire == null ? "" : orgaProprietaire.getValue())
    + "] userProprietaire=[" + (userProprietaire == null ? "" : userProprietaire.getValue()) + "]");

if (UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_DIR"))){
  haveNoAction = orgaProprietaire == null ? Boolean.TRUE : !UserUtils.isInOrganization(usrContext, orgaProprietaire.getValue(), true);
} else {
  boolean owner, validator, signer = false;
  if(userProprietaire != null) {
    Integer docUserOwner = userProprietaire.getValue();
    if (docUserOwner != null) {
      owner = docUserOwner.equals(userId);
    }
    else {
      if(orgaProprietaire!=null) {
        owner = orgaProprietaire == null ? Boolean.FALSE : UserUtils.isInOrganization(usrContext, orgaProprietaire.getValue(), true);
      }
    }
  }

  IFieldValue valideur = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR"));
  Integer docUserValidator = valideur == null ? null : valideur.getValue();
  if (docUserValidator != null){
    validator = docUserValidator.equals(userId);
  }

  IFieldValue viseur = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR"));
  Integer docUserSigner = viseur == null ? null : viseur.getValue();
  if (docUserSigner != null){
    signer = docUserSigner.equals(userId);
  }

  logger.debug("docUserValidator=[" + docUserValidator + "] docUserSigner=[" + docUserSigner + "]")

  haveNoAction = !owner && !validator && !signer;
}

List<Action> unavailableActions = new ArrayList<Action>();
unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
// Every body can add a comment
unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_COMMENT);
if (!haveNoAction) {
  unavailableActions.clear();
}

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
// Si l'utilsiateur est un administrateur, toutes les actions sont permises tant que le visa n'est pas validé
else if(UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN")))
{
	unavailableActions.clear();
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