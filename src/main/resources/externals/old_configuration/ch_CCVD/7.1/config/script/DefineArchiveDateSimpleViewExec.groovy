import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Note

import java.text.SimpleDateFormat

/*************************************************************************************************
 * 								Définition de la date d'archivage - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la définition de l'index date d'archivage des documents sélectionnés
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE ARCHIVE DATE SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null

try {
  result = output.getValue()
  result.setMessageSummary("ACTION DEFINITION DATE ARCHIVAGE : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DefineArchiveDateUnitViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  // Vérifie que l'initialisation de l'action n'a pas rencontrée d'erreur
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - DEFINE ARCHIVE DATE SIMPLE VIEW EXEC - END")
    return
  }

  SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT)
  Date d = data.get("date")
  String formatDate = ""
  if(d != null) formatDate = sdf.format(d)
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail("ATTENTION - Veuillez saisir une date")
    return
  }

  for(IDocument document : docs) {
    if(Constants.UNLOCK_TYPE.equals(document.getLockType())) {
      //Définition date d'échéance
      Document doc = document.getAirsDocument().getInnerDocument()
      Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_ARCHIVE_CODE, formatDate)

      //Définition commentaire
      String comment = data.get("comment")
      if(!comment.equalsIgnoreCase("")) {
        Note newNote = new Note(Constants.AIRS_NOTE_ID)
        newNote.setText(comment)
        newNote.setPublic()
        document.getAirsDocument().addComment(newNote)
      }
      doc.updateContent()

      //Définition de l'historique
      String historic = "Date d'archivage définie : " + formatDate
      Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_FIELDCHANGE, historic)
      _scriptLogger.debug("[CUSTOM ACTION] - DefineArchiveDateUnitViewExec - DEBUG - La date d'archivage suivante " + formatDate + " a été enregistré avec succès pour le document : " + doc.getId())
    }
  }

  //Utils.getSearchResultController().replay();
  //Utils.getSimpleViewAttachmentController().getModel().refreshDocument();
  //Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);
  result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
  result.setMessageDetail("INFORMATION - La date d'archivage suivante " + formatDate + " a été enregistré avec succès")

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DefineArchiveDateUnitViewExec - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE ARCHIVE DATE SIMPLE VIEW EXEC - END")