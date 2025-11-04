import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.jcorbairs.admin.OrganizationUserAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager
import com.digitech.jcorbairs.admin.ProfilAdmin
import com.digitech.jcorbairs.admin.UserAdmin

import javax.faces.model.SelectItem
/*************************************************************************************************
 *   					    			Distribution - INIT
 **************************************************************************************************
 Date : 23.02.2016
 Auteur : MTO

 Description : Permet la prise en compte ou la distribution des documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTION SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<SelectItem> items = new ArrayList<SelectItem>()

try {
	customActionController = Utils.getCustomActionController()
	data = customActionController.getModel().getModalPanelModel()
} catch (Exception e) {
	Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
	scriptLogger.error("[CUSTOM ACTION] - DistributionSimpleViewInit - ERREUR : ", e)
	return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{

	if (Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() == 0) {
		Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
		return
	}


	HashMap<Integer, String> userLoginsMap = new HashMap<Integer, String>()
	HashMap<Integer, String> userNamesMap = new HashMap<Integer, String>()
	HashMap<Integer, String> userFirstnamesMap = new HashMap<Integer, String>()
	HashMap<Integer, Boolean> userCheckedMap = new HashMap<Integer, Boolean>()
	try {
		IDocument document = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)

		if (String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue()))) {
			Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_error_status"), false)
			return
		}


		UserAdmin userAdmin = null
		if (document.getSecretLevel() == Constants.SECRET_LEVEL_CONFIDENTIEL) {
			for (OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_OAI_ID).getUsers()) {
				userAdmin = user.getUser()
				if (userAdmin.getActive() && !userAdmin.getLogin().toUpperCase().contains("AIRS") && !userAdmin.getLogin().toUpperCase().contains("CONFIDENTIEL") && userAdmin.getProfilIds().contains(Constants.AIRS_PROFIL_CONFIDENTIEL)) {
					userCheckedMap.put(userAdmin.getId(), false)
					userLoginsMap.put(userAdmin.getId(), userAdmin.getLogin())
					userNamesMap.put(userAdmin.getId(), userAdmin.getName())
					userFirstnamesMap.put(userAdmin.getId(), userAdmin.getFirstName())
				}
			}
		}else if(document.getSecretLevel() == Constants.SECRET_LEVEL_LFA){
			for(OrganizationUserAdmin user: OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_OAI_ID).getUsers()) {
				userAdmin = user.getUser()
				if (userAdmin.getActive() && !userAdmin.getLogin().toUpperCase().contains("AIRS") && !userAdmin.getLogin().toUpperCase().contains("CONFIDENTIEL") && userAdmin.getProfilIds().contains(Constants.AIRS_PROFIL_LFA)) {
					userCheckedMap.put(userAdmin.getId(), false)
					userLoginsMap.put(userAdmin.getId(), userAdmin.getLogin())
					userNamesMap.put(userAdmin.getId(), userAdmin.getName())
					userFirstnamesMap.put(userAdmin.getId(), userAdmin.getFirstName())
				}
			}
		}else{
			if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_OAI_ID) {
				for (OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_OAI_ID).getUsers()) {
					userAdmin = user.getUser()
					if (userAdmin.getActive() && /*!userAdmin.getLogin().toUpperCase().contains("AIRS") &&*/!userAdmin.getLogin().toUpperCase().contains("CONFIDENTIEL") && (Constants.AIRS_PROFILS_WORKFLOW.isEmpty() || !Collections.disjoint(user.getUser().getProfilIds(), Constants.AIRS_PROFILS_WORKFLOW))) {
						userCheckedMap.put(userAdmin.getId(), false)
						userLoginsMap.put(userAdmin.getId(), userAdmin.getLogin())
						userNamesMap.put(userAdmin.getId(), userAdmin.getName())
						userFirstnamesMap.put(userAdmin.getId(), userAdmin.getFirstName())
					}
				}
			}else {
				String currentProfilCode = null
				for (ProfilAdmin profilAdmin : userContext.getProfiles()) {
					if (profilAdmin.getCode().startsWith("SERVICE_")) {
						currentProfilCode = profilAdmin.getCode()
						for (UserAdmin user : profilAdmin.getUsers()) {
							if (user.getActive() && !user.getLogin().toUpperCase().contains("AIRS")) {
								userCheckedMap.put(user.getId(), false)
								userLoginsMap.put(user.getId(), user.getLogin())
								userNamesMap.put(user.getId(), user.getName())
								userFirstnamesMap.put(user.getId(), user.getFirstName())
							}
						}
					}
				}

				for (OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_AVS_ID).getUsers()) {
					userAdmin = user.getUser()
					if (user.getUser().getActive() && !user.getUser().getName().equalsIgnoreCase(currentProfilCode.replace("SERVICE_", "")) && "Service".equalsIgnoreCase(user.getUser().getFirstName()) && !user.getUser().getLogin().toUpperCase().contains("AIRS") && user.getActive()) {
						userCheckedMap.put(userAdmin.getId(), false)
						userLoginsMap.put(userAdmin.getId(), userAdmin.getLogin())
						userNamesMap.put(userAdmin.getId(), userAdmin.getName())
						userFirstnamesMap.put(userAdmin.getId(), userAdmin.getFirstName())
					}
				}
			}
		}

	} catch (Exception e) {
		scriptLogger.error("[CUSTOM ACTION] - DistributionSimpleViewInit - Erreur definition des utilisateurs : ", e)
	}
	data.put("DATA_USERS_ID", userNamesMap.keySet().toArray())
	data.put("DATA_USERS_LOGINS", userLoginsMap)
	data.put("DATA_USERS_NAMES", userNamesMap)
	data.put("DATA_USERS_FIRSTNAMES", userFirstnamesMap)
	data.put("DATA_USERS_CHECKED", userCheckedMap)

	data.put("DATA_STATUS", Constants.LIST_WK_STATUT_ARCHIVE)
	data.put("DATA_COMMENT", "")

}catch(Exception e) {
	Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
	scriptLogger.error("[CUSTOM ACTION] - DistributionSimpleViewInit - ERREUR : ", e)
	return
}

scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTION SIMPLE VIEW INIT - END")