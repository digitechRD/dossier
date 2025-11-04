import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer

/*************************************************************************************************
 * 					Définition de l'informations de l'avis de mutation - EXEC
 **************************************************************************************************
 Date : 14.11.2014
 Auteur : MTO

 Description : Définit l'informations de l'avis de mutation selon la liste d'autorité
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE INFORMATIONS AVIS MUTATION SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
String lockedDocuments = null
CustomActionController customActionController = null
Map<String, Object> data = new HashMap<String, Object>()

try {
  result = output.getValue()
  result.setMessageSummary("ACTION DEFINITION INFORMATIONS : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DefineInformationsAvisMutationSimpleViewExec - ERREUR : ", e.localizedMessage)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - DEFINE INFORMATIONS AVIS MUTATION SIMPLE VIEW EXEC - END")
    return
  }

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

  if(!"0".equalsIgnoreCase(data.get("information").toString())) {
    for(IDocument doc : docs) {
      if(Constants.UNLOCK_TYPE.equals(doc.getLockType())) {
        Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.LIST_CODE_INFORMATIONS_CODE, data.get("information").toString())
        doc.getAirsDocument().updateContents()

      }
      else {
        if(lockedDocuments == null) lockedDocuments = doc.getAirsRefId().toString()
        else lockedDocuments += ", " + doc.getAirsRefId().toString()
      }
    }
  }

  Utils.getSearchResultController().replay()
  Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

  if(lockedDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail("INFORMATION - L'indexation a été réalisée sur tous les documents avec succès.")
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Les documents suivants n'ont pas été mis à jour car ils sont bloqués :" + lockedDocuments + ".")
  }
}
catch(Exception e) {
  _scriptLogger.error("[CUSTOM ACTION] - DefineInformationsAvisMutationSimpleViewExec - ERREUR : ", e)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur")
}

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE INFORMATIONS AVIS MUTATION SIMPLE VIEW EXEC - END")
