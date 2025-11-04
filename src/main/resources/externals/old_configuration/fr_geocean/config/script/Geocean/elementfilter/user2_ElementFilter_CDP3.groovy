/** Fichier : UG_user_ElementFilter.groovy
* 	Auteur  : JMU
* 	Date 	: 04/12/12
* 	But     : Filtre une liste d'utilisateurs en fonction de leur profils (ils doivent faire partie de l'UG sélectionnée dans le document
* 			  champ : FAC_UG)
*/
import java.util.List;

import com.digitech.jcorbairs.admin.ProfilAdmin;
import java.util.ArrayList;

import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.admin.UsersManager;

import java.lang.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.digitech.jcorbairs.*;
import com.digitech.dossier.common.utils.*;
import com.digitech.dossier.common.model.backend.DossierCoreContext;

import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.Utils;

import com.digitech.dossier.common.model.backing.factory.*;
import javax.faces.component.html.*;
import javax.faces.component.*;
import javax.faces.model.*;
import javax.faces.model.SelectItem;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.resources.BundleUtils;
import org.apache.commons.beanutils.BeanComparator;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceConstants;

import static ScriptUtils;
 
/********************* PARAM ********************/
// Logger scriptLogger 
// UserContext userContext 
// fieldmap elements
/************************************************/

String SCRIPT_NAME = "user2_ElementFilter_CDP3.groovy";
String SCRIPT_TYPE = "Dependency Filter";

scriptLogger.debug("Script "+SCRIPT_TYPE+" : "+SCRIPT_NAME+" --- Start");

//On récupère le champ FAC_UG (la dépendance)
//IField UGField = fieldMap.get("FACT_ETAT");
//On récupère le champ FAC_UG_USER (le champ à filtrer)
IField UGUserField = fieldMap.get("FACT_USR_CDP3");

Integer PROFILE_ID = 112;
//Chargement du profil
ProfilAdmin currentProfil = com.digitech.jcorbairs.admin.ProfilsManager.load(DossierCoreContext.getAdminJeton(), PROFILE_ID);

//Récupération de la liste des utilisateurs ayant le profil 
List<UserAdmin>userAdmList =  currentProfil.getUsers();

//Initialisation de la liste d'item a présenter en IHM
List<SelectItem> selectItems = new ArrayList<SelectItem>();

//On remplit la liste
//Ajout de tous les user de l'orga sélectionnée
//CLE MODIF pour champ vide pas de user
selectItems.add(new SelectItem(null, ""));

for( UserAdmin usrAdm : userAdmList )
{
	User usrToAdd = getUserMgr().getUser(usrAdm.getId());
	selectItems.add(new SelectItem(usrToAdd.getId(), BundleUtils.getTitle(usrToAdd)));
} 

Collections.sort(selectItems, new BeanComparator("label"));

//Ajout de la liste au composant html 
HtmlSelectOneMenu component = ((HtmlSelectOneMenu)  ((UIComponent)UGUserField.getComponent()));
component.getChildren().get(0).setValue(selectItems );

scriptLogger.debug("Script "+SCRIPT_TYPE+" : "+SCRIPT_NAME+" --- End");


private static IUser getUserMgr() {
  return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}