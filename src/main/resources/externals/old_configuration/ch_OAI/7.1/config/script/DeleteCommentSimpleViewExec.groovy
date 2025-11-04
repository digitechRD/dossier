import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Note

/*************************************************************************************************
 * 							Suppression des commentaires - EXEC
 **************************************************************************************************
 Date : 06.07.2016
 Auteur : MTO

 Description : Permet de supprimer tous les commentaires d'un document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DELETE COMMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
IDocument document = null
Map<String, Object> data = null
CustomActionController customActionController = null
String errorDocuments = null
IRight rightMgr = null
Document doc = null

try {
  result = output.getValue()
  result.setMessageSummary(BundleUtils.getTranslation("groovy_comment_action"))
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - DeleteCommentSimpleViewExec - ERREUR : ", e)
  return
}


/**
 * TRAITEMENT
 **************************************************************************************************/

try {

  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    scriptLogger.debug("[CUSTOM ACTION] - ADD COMMENT SIMPLE VIEW EXEC - END")
    return
  }

  rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR)
  document = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)

  if(!rightMgr.isAuthorizedToEditDocument(userContext, document)) {
    doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
  }
  else
    doc = document.getAirsDocument().getInnerDocument()

  if("1".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
    try {
      for(Note note : doc.getNotes()) {
        doc.deleteNote(note)
      }
      Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_COMMENT, BundleUtils.getTranslation("groovy_comment_event_deleted_all"))
      doc.updateContent()
    } catch(Exception e) {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      scriptLogger.error("[CUSTOM ACTION] - DeleteCommentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
    }
  }
  else {
    Map<Integer, Boolean> noteChecksMap = (Map<Integer, Boolean>) data.get("DATA_NOTES_CHECKED")
    // Vérification de la sélection des utilisateurs
    if(!noteChecksMap.containsValue(true)) {
      scriptLogger.warn("[CUSTOM ACTION] - DeleteCommentSimpleViewExec - ATTENTION : Distribution - Aucun commentaire sélectionné")
      result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
      result.setMessageDetail(BundleUtils.getTranslation("groovy_comment_no_note_seleted"))
      return
    }

    try {
      for(Note note : doc.getNotes()) {
        for(Map.Entry<Integer, Boolean> entry : noteChecksMap.entrySet()) {
          if(entry.getValue() && entry.getKey().equals(note.getId()))
            doc.deleteNote(note)
        }
      }
      Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_COMMENT, BundleUtils.getTranslation(
          "groovy_comment_event_deleted_unitary"))
      doc.updateContent()
    } catch(Exception e) {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      scriptLogger.error("[CUSTOM ACTION] - DeleteCommentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
    }
  }

  Utils.getSearchResultTableController().refreshAndKeepFilter()

  if(errorDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments)
  }

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - DeleteCommentSimpleViewExec - ERREUR : ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - DELETE COMMENT SIMPLE VIEW EXEC - END")