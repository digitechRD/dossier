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


logger.debug("Script document unavailable actions: unavailableActions_courrierOut.groovy --- Start");

try{
        // Get all infos we need
		com.digitech.jcorbairs.User usr = usrContext.getUser();
        Integer userId = usr.getId();
        Object orgaProprietaire = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"));
        Object userProprietaire = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE"));
        String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
        Object etatCourrierField = theFieldValueMap.get(etatCourrierFieldCode);
        Integer courrierState = etatCourrierField == null ? null : etatCourrierField.getValue();
        Term etatTerm = CourrierScriptUtils.getAuthorityListService().getTerm(courrierState);	
		String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
        Integer visaState = theFieldValueMap.get(etatVisaFieldCode).getValue();
		
		// Create the action table
		List<Action> unavailableActions = new ArrayList<Action>();

        logger.debug("userId=[" + userId + "] courrierState=[" + (etatTerm == null ? "" : etatTerm.getPreferedValue()) + "] orgaProprietaire=[" + (orgaProprietaire == null ? "" : orgaProprietaire)
                + "] userProprietaire=[" + (userProprietaire == null ? "" : userProprietaire) + "]");
				
		
		// If current user is an admin => he can do what he wants
		if(UserUtils.hasProfile(usrContext, "DOS_ADMIN_CLIENT") || UserUtils.hasProfile(usrContext, "DOS_ADMIN")){
			unavailableActions.clear();
		}
		// If the document is waiting for visa nobody can change it except the current visa user
		else if (CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_INDEFINI")).equals(visaState) && CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")).equals(courrierState)){
			logger.debug("Le courrier est à viser");
			// To know if current user is current visa user
			boolean signer = false;
			Object viseur = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR"));
            Integer docUserSigner;
			
			logger.debug("viseur "+viseur);
			
			if(viseur == null){
				docUserSigner = null;
			}
			else{
				docUserSigner =	viseur.getValue();
			}
			
            if (docUserSigner != null) {
                signer = docUserSigner.equals(userId);
            }
			// If not visa user then we restrict all actions on the document
			if(!signer){
				logger.debug("L'utilisateur courant n'est pas le viseur");
				unavailableActions.clear();
				unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
				unavailableActions.remove(ScriptResultValueDocumentActions.Action.PRINT);
			}
			else{
				unavailableActions.clear();
			}
			
			logger.debug("docUserSigner=[" + docUserSigner + "]")
		}
		// If the document has been accepted in visa => the only action availables are ADD_PJ and ADD_COMMENT
		else if (CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_ACCEPTE")).equals(visaState)){
			logger.debug("Le courrier a été accepté en visa");
			unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
			unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_PJ);
			unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_COMMENT);
		}
		// If the document is closed => we restrict all actions on the document
		else if(CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE")).equals(courrierState)){
			logger.debug("Le courrier est clôturé");
			unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
			unavailableActions.remove(ScriptResultValueDocumentActions.Action.PRINT);
		}
		// Otherwise the document is on free access
		else{
			// If the user is in copy => he can't modify the document
			
			Integer ownerId = userProprietaire.getValue();
			Integer orgaOwnerId = orgaProprietaire.getValue();
			

			
			if(ownerId!=null && ownerId.equals(userId) || (orgaOwnerId!=null && orgaOwnerId.equals(usrContext.getCurrentOrgId())) && !UserUtils.hasProfile(usrContext, "DOS_AGENT")){
				logger.debug("Le courrier est libre d'accès");
				unavailableActions.clear();
			}
			else{
				unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
				unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_COMMENT);
			}
		}
			
		theOutput.getValue().getUnavailableActions().addAll(unavailableActions);
}
catch(Exception e){
	logger.error(e.getLocalizedMessage());
}

logger.debug("Script document unavailable actions: unavailableActions_courrierOut.groovy --- End");