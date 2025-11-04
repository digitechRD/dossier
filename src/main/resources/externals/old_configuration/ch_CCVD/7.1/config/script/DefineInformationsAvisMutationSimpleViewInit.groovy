import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin

import javax.faces.model.SelectItem

/*************************************************************************************************
 * 					Définition de l'informations de l'avis de mutation - INIT
 **************************************************************************************************
 Date : 14.11.2018
 Auteur : MTO

 Description : Définit l'informations de l'avis de mutation selon la liste d'autorité
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE INFORMATIONS AVIS MUTATION SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/

CustomActionController customActionController = null
List<SelectItem> items = new ArrayList<SelectItem>()

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement", false)
  _scriptLogger.error("[CUSTOM ACTION] - DefineInformationsAvisMutationSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  items.add(new SelectItem(0, "Choisir une information"))
  for(AuthorityListTermAdmin authorityListTermAdmin : AuthorityListTermAdmin.loadTerms(DossierCoreContext.getAdminJeton(), Constants.LIST_CODE_INFORMATIONS_ID)) {
    items.add(new SelectItem(authorityListTermAdmin.getId(), authorityListTermAdmin.getValue1()))
  }
  data.put("informations", items)
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement", false)
  _scriptLogger.error("[CUSTOM ACTION] - DefineInformationsAvisMutationSimpleViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE INFORMATIONS AVIS MUTATION SIMPLE VIEW INIT - END")

