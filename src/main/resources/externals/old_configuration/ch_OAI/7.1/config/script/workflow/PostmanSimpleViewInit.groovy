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
 *   					    			Postman - INIT
 **************************************************************************************************
 Date : 23.02.2016
 Auteur : MTO

 Description : Permet la prise en compte ou la distribution des documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - POSTMAN SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<SelectItem> items = new ArrayList<SelectItem>()

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - PostmanSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {

  if(Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() == 0) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
    return
  }

  List<SelectItem> statusList = new ArrayList<SelectItem>()
  statusList.add(new SelectItem(String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE), BundleUtils.getTranslation("groovy_postman_pec")))
  statusList.add(new SelectItem(String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_PEC), BundleUtils.getTranslation("groovy_postman_distribution")))

  HashMap<Integer, String> userLoginsMap = new HashMap<Integer, String>()
  HashMap<Integer, String> userNamesMap = new HashMap<Integer, String>()
  HashMap<Integer, String> userFirstnamesMap = new HashMap<Integer, String>()
  HashMap<Integer, Boolean> userCheckedMap = new HashMap<Integer, Boolean>()
  try {
    IDocument document = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)
    if(document.getSecretLevel() == Constants.SECRET_LEVEL_CONFIDENTIEL) {
      for(OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_OAI_ID).getUsers()) {
        if(!user.getUser().getLogin().toUpperCase().contains("AIRS") && !user.getUser().getLogin().toUpperCase().contains("CONFIDENTIEL") &&
            user.getUser().getProfilIds().contains(Constants.AIRS_PROFIL_CONFIDENTIEL)) {
          userCheckedMap.put(user.getUser().getId(), false)
          userLoginsMap.put(user.getUser().getId(), user.getUser().getLogin())
          userNamesMap.put(user.getUser().getId(), user.getUser().getName())
          userFirstnamesMap.put(user.getUser().getId(), user.getUser().getFirstName())
        }
      }
    }
    else if(document.getSecretLevel() == Constants.SECRET_LEVEL_LFA) {
      for(OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_OAI_ID).getUsers()) {
        if(!user.getUser().getLogin().toUpperCase().contains("AIRS") && !user.getUser().getLogin().toUpperCase().contains("CONFIDENTIEL") &&
            user.getUser().getProfilIds().contains(Constants.AIRS_PROFIL_LFA)) {
          userCheckedMap.put(user.getUser().getId(), false)
          userLoginsMap.put(user.getUser().getId(), user.getUser().getLogin())
          userNamesMap.put(user.getUser().getId(), user.getUser().getName())
          userFirstnamesMap.put(user.getUser().getId(), user.getUser().getFirstName())
        }
      }
    }
    else {
      if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_OAI_ID) {
        for(OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_OAI_ID).getUsers()) {
          if(!user.getUser().getLogin().toUpperCase().contains("AIRS") && !user.getUser().getLogin().toUpperCase().contains("CONFIDENTIEL") && (
              Constants.AIRS_PROFILS_WORKFLOW.isEmpty() || !Collections.disjoint(user.getUser().getProfilIds(), Constants.AIRS_PROFILS_WORKFLOW))) {
            userCheckedMap.put(user.getUser().getId(), false)
            userLoginsMap.put(user.getUser().getId(), user.getUser().getLogin())
            userNamesMap.put(user.getUser().getId(), user.getUser().getName())
            userFirstnamesMap.put(user.getUser().getId(), user.getUser().getFirstName())
          }
        }
      }
      else {
        String currentProfilCode = null
        for(ProfilAdmin profilAdmin : userContext.getProfiles()) {
          if(profilAdmin.getCode().startsWith("SERVICE_")) {
            currentProfilCode = profilAdmin.getCode()
            for(UserAdmin user : profilAdmin.getUsers()) {
              if(!user.getLogin().toUpperCase().contains("AIRS")) {
                userCheckedMap.put(user.getId(), false)
                userLoginsMap.put(user.getId(), user.getLogin())
                userNamesMap.put(user.getId(), user.getName())
                userFirstnamesMap.put(user.getId(), user.getFirstName())
              }
            }
          }
        }

        for(OrganizationUserAdmin user : OrganizationsManager.load(DossierCoreContext.getAdminJeton(), Constants.ORGANIZATION_AVS_ID).getUsers()) {
          if(!user.getUser().getName().equalsIgnoreCase(currentProfilCode.replace("SERVICE_", "")) && "Service".
              equalsIgnoreCase(user.getUser().getFirstName()) && !user.getUser().getLogin().toUpperCase().contains("AIRS") && user.getActive()) {
            userCheckedMap.put(user.getUser().getId(), false)
            userLoginsMap.put(user.getUser().getId(), user.getUser().getLogin())
            userNamesMap.put(user.getUser().getId(), user.getUser().getName())
            userFirstnamesMap.put(user.getUser().getId(), user.getUser().getFirstName())
          }
        }
      }
    }

  } catch(Exception e) {
    scriptLogger.error("[CUSTOM ACTION] - PostmanSimpleViewInit - Erreur definition des utilisateurs : ", e)
  }
  data.put("DATA_USERS_ID", userNamesMap.keySet().toArray())
  data.put("DATA_USERS_LOGINS", userLoginsMap)
  data.put("DATA_USERS_NAMES", userNamesMap)
  data.put("DATA_USERS_FIRSTNAMES", userFirstnamesMap)
  data.put("DATA_USERS_CHECKED", userCheckedMap)

  data.put("DATA_LIST_STATUS", statusList)
  data.put("DATA_STATUS", Constants.LIST_WK_STATUT_ARCHIVE)
  data.put("DATA_COMMENT", BundleUtils.getTranslation("groovy_comment_default_message"))
  data.put("DATA_IS_DISTRIBUTION", false)
  data.put("DATA_IS_DISTRIBUTION_SERVICE", false)

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - PostmanSimpleViewInit - ERREUR : ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - POSTMAN SIMPLE VIEW INIT - END")