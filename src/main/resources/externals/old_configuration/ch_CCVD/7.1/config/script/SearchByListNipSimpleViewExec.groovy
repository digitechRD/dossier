import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer

/*************************************************************************************************
 * 								Recherche par NIP - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet une recherche en saisissant une liste de NIP
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - SEARCH BY LIST NIP SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
ScriptResultValueDocumentInitializer result = null
String listnip = null
String query = ""

try {
  result = output.getValue()
  result.setMessageSummary("ACTION RECHERCHE PAR NIP(s) : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "L'initialisation du traitement est impossible. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - SearchByListNipSimpleVieweExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - SEARCH BY LIST NIP SIMPLE VIEW EXEC - END")
    return
  }

  listnip = data.get("listnip")
  if(listnip.length() >= Constants.CCVD_NIP_MIN_SIZE && listnip.matches(Constants.CCVD_NIP_REGEX)) {
    String[] tabnip = listnip.split(";")
    if(tabnip.length >= 1) {
      int tabnipSize = tabnip.length
      for(int i = 0; i < tabnipSize; i++) {
        if(tabnip[i].startsWith(Constants.CCVD_NSS_CARACTERES_START)) {
          query += "N_NSS=\"" + tabnip[i] + "\""
        }
        else {
          query += "N_AFF=\"" + tabnip[i] + "\""
        }

        if(i < tabnipSize - 1) {
          query += " OR "
        }
      }

      Utils.clearBackingModels()
      Utils.getSearchResultTableController().getModel().clear()
      Utils.getSearchController().getModel().clear()
      Utils.getSearchResultTableController().getModel().clear()

      Utils.getSearchAdvancedController().getModel().setContentTypeSelectedCode(Constants.CTY_AFFILIATED_DOCUMENT)
      Utils.getSearchAdvancedController().getModel().setRequest(query)
      Utils.getSearchAdvancedController().search()
      Utils.getSearchResultTableController().refresh()

      Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

      result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
      result.setMessageDetail("INFORMATION - La recherche a été effectuée avec succes.")
    }
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - La recherche sur " + listnip + " semble mauvaise. Veuillez corriger les parametres.")
  }
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution de la recherche est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - SearchByListNipSimpleVieweExec - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - SEARCH BY LIST NIP SIMPLE VIEW EXEC - END")



