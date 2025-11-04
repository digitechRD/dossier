import java.util.*;
import org.apache.commons.lang.*;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.Organization;

import static CourrierScriptUtils

// Auteur : JMU 
// Date de création : 12/03/14
// 
/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext le contexte utilisateur
// IField updatedField le champ de référence pour la dépendance
// IField fieldToUpdate le champ à mettre à jour
/************************************************/
String SCRIPT_NAME = "courrierOut_dependency_updateOrgaOwner";
User user = null;
Organization orga = null;

scriptLogger.debug("Script field dependency : "+SCRIPT_NAME+".groovy --- Start");

// Récupération l'utilisateur traitant
user = getUserMgr().getUser((Integer)updatedField.getValue());

if(user!=null)
{
	// Récupération de la première organisation de l'utilisateur traitant
	orga = user.getOrganizations().get(0);
	
	if(orga!=null)
	{
		// Affectation de la valeur dans le champ service traitant
		fieldToUpdate.setValue(orga.getProperties().getId());
	}
	else
	{
		scriptLogger.debug("Script field dependency : "+SCRIPT_NAME+".groovy  --- Impossible de récupérer le service de l'utilisateur traitant");
	}
}
else
{
	scriptLogger.debug("Script field dependency : "+SCRIPT_NAME+".groovy  --- Impossible de récupérer l'utilisateur traitant");
}





scriptLogger.debug("Script field dependency : "+SCRIPT_NAME+".groovy --- End");


private static IUser getUserMgr() 
{
	return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}