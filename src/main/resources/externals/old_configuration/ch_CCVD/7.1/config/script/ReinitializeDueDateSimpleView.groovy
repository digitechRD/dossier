import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.exception.XmlException

/**************************************************************************************************
 *   					    Distribution des documents - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet de réinitialiser la date d’échéance de tous les documents sélectionnés
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - REINITIALIZE DUE DATE SIMPLE VIEW - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
String errorDocuments = null

try {
  result = output.getValue()
  result.setMessageSummary("ACTION DE REINITIALISATION DE LA DATE D'ECHEANCE : ")

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - ReinitializeDueDateSimpleView - ERREUR : ", e.localizedMessage)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(docs.size() == 0) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Aucun document n'a été selectionné")
    return
  }

  for(IDocument dos : docs) {
    try {
      Methods.defineDocumentIndex(dos.getAirsDocument().getInnerDocument(), Constants.FIELD_DATE_DUE_CODE, null)
      dos.getAirsDocument().getInnerDocument().updateContent()
    }
    catch(XmlException e) {
      if(errorDocuments == null) errorDocuments = dos.getAirsRefId().toString()
      else errorDocuments += ", " + dos.getAirsRefId().toString()
      _scriptLogger.error("[CUSTOM ACTION] - ReinitializeDueDateSimpleView - ERREUR lors de la réinitialisation (AIRSID : " + dos.getAirsRefId() + ") : ", e.localizedMessage)
    }
  }

  if(errorDocuments != null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Les documents n° :" + errorDocuments + "n'ont pas été réinitialisés")
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail("INFORMATION - L'ensemble des documents ont eu leur date d'échéance réinitialisée avec succès")
  }

  Utils.getSearchResultTableController().getModel().clear()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)
} catch(Exception e) {
  _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - ERREUR : ", e)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - La réinitialisation est impossible. Veuillez contacter votre administrateur")
}
_scriptLogger.debug("[CUSTOM ACTION] - REINITIALIZE DUE DATE SIMPLE VIEW - END")