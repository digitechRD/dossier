import org.slf4j.Logger

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backing.document.DocumentCreationModel;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.Organization;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;

import static CourrierScriptUtils;

// Auteur : JMU 
// Date de création : 17/03/14
// But du script : Positionnement du service traitant sur un courrier sortant si le courrier est ouvert en modification (en fonction de l'utilisateur traitant)
/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'userContext
// IDocument document le document courant
/************************************************/

String SCRIPT_NAME = "O_ORGANIZATION_INIT_CourrierOut";
Integer treatUserId = null;
Integer treatOrgaId = null;
User user = null;
Organization orga = null;



scriptLogger.debug("Script field init : "+SCRIPT_NAME+".groovy --- Start");

// Récupération du contenu du service traitant du courrier
treatOrgaId = FieldUtils.getValue(document, CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"));

// Si pas de service traitant
if(treatOrgaId == null)
{
	// Vérification qu'un utilisateur traitant est positionné
	treatUserId = FieldUtils.getValue(document, CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE"));
	
	if(treatUserId!=null)
	{
		// Récupération l'utilisateur traitant
		user = getUserMgr().getUser(treatUserId);
		
		if(user!=null)
		{
			//Récupération du premier service de l'utilisateur traitant
			orga = user.getOrganizations().get(0);
			
			//Affectation du service traitant au courrier sortant
			FieldUtils.setValue(document, CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"), orga.getProperties().getId());
		}
	}
}

scriptLogger.debug("Script field init : "+SCRIPT_NAME+".groovy --- End");

private static IUser getUserMgr() 
{
	return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}