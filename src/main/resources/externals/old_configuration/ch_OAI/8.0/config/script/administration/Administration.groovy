import Constants
import Methods
import com.aspose.slides.Presentation
import com.aspose.words.License
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Domain
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

/*************************************************************************************************
 *   					    			SetSecretLevelForLFATask - EXEC
 **************************************************************************************************
 Date : 02.08.2016
 Auteur : MTO

 Description : Permet de mettre à jour le niveau de secret pour la visualisation des documents LFA
 **************************************************************************************************/



/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null
List<Integer> listLFADocument = null

/**
 * TRAITEMENT
 **************************************************************************************************/
scriptLogger.debug("[CUSTOM ACTION] - SET UPDATE TOKEN WEBAI - START");

/**
 * INITIALISATION
 **************************************************************************************************/
String token = null;
String query = null;

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
	
	// Récupération du Token
	token = Methods.getToken();
	if(!"".equals(token)){
		query = Constants.DB_AIRS_REQUEST_UPDATE_WEBAI_TOKEN.replaceAll("##token##", token);
		if(Methods.executeRequest(query) > 0) scriptLogger.debug("[CUSTOM ACTION] - UpdateTokenWebAI - Token mis à jour");
		else scriptLogger.error("[CUSTOM ACTION] - UpdateTokenWebAI - Token non mis à jour : "+query);
	}
}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - UpdateTokenWebAI - ERROR :  "+query ,e);
}

scriptLogger.debug("[CUSTOM ACTION] - SET UPDATE TOKEN WEBAI - END");