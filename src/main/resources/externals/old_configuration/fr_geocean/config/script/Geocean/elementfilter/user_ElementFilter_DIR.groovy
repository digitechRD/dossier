/** Fichier : user_ElementFilter_Compta.groovy
* 	Auteur  : JMU
* 	Date 	: 12/04/13
* 	But     : Filtre une liste d'utilisateurs en fonction de leur profils
*/
import java.util.List;

import com.digitech.jcorbairs.admin.ProfilAdmin;

import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.admin.UsersManager;

import java.lang.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.digitech.jcorbairs.*;
import com.digitech.dossier.common.utils.*;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;

import static ScriptUtils;
 
 
 /********************* PARAM ********************/
// Logger scriptLogger 
// UserContext userContext 
// List<User> elements
/************************************************/

String PROFILE_CODE = "PF_DIR";
Integer PROFILE_ID = 114;
String SCRIPT_NAME="user_ElementFilter_Direction.groovy";

scriptLogger.debug("Script ElementFilter : "+SCRIPT_NAME+" --- Start");

elements.clear();

//Chargement du profil
ProfilAdmin currentProfil = com.digitech.jcorbairs.admin.ProfilsManager.load(DossierCoreContext.getAdminJeton(), PROFILE_ID);

//Récupération de la liste des utilisateurs ayant le profil 
List<UserAdmin>userAdmList =  currentProfil.getUsers();

//On remplit la liste
for( UserAdmin usrAdm : userAdmList )
{
   elements.add(getUserMgr().getUser(usrAdm.getId()));
   scriptLogger.debug("Script ElementFilter : "+SCRIPT_NAME+" --- Add:"+usrAdm.getId());
} 

scriptLogger.debug("Script ElementFilter : "+SCRIPT_NAME+" --- End");

  private static IUser getUserMgr() {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }

