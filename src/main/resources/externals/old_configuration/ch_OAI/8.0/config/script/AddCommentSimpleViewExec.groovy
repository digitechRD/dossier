import Constants
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
 * 							Ajout d'un commentaire - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'ajout d'un commentaire à un ensemble de documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - ADD COMMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
String errorDocuments = null
List<IDocument> docs = null
IRight rightMgr = null

try {
  result = output.getValue()
  result.setMessageSummary(BundleUtils.getTranslation("groovy_comment_action"))

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch (Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - AddCommentSimpleViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    scriptLogger.debug("[CUSTOM ACTION] - ADD COMMENT SIMPLE VIEW EXEC - END")
    return
  }

  if (data.get("DATA_COMMENT").toString().length() == 0 || (
          data.get("DATA_COMMENT").toString().length() > 0 && BundleUtils.getTranslation("groovy_comment_default_message").equals(data.get("DATA_COMMENT").
                  toString()))) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_comment_default_message"))
    return
  }

  rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR)

  for (IDocument document : docs) {
    try {
      if (!Constants.UNLOCK_TYPE.equals(document.getLockType())) {
        if (errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
      } else {
        Note newNote = new Note(Constants.AIRS_NOTE_ID)
        String texte = data.get("DATA_COMMENT").toString()
        String page = data.get("DATA_PAGE").toString()
        scriptLogger.debug("--->Page : " + page)
        if (page.length() > 0)
          texte += "\n" + BundleUtils.getTranslation("groovy_comment_num_page") + " :" + page
        newNote.setText(texte)

        if (!rightMgr.isAuthorizedToEditDocument(userContext, document)) {
          Document doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
          newNote.setUserId(userContext.getUserId())
          doc.addNote(newNote)
          doc.updateContent()
          scriptLogger.debug(
                  "[CUSTOM ACTION] - AddCommentSimpleViewExec - Utilisateur avec profil consultation : " + newNote.getUserName() + " - " + newNote.getUserId())
        } else {
          newNote.setUserId(userContext.getUserId())
          document.getAirsDocument().addComment(newNote)
          document.getAirsDocument().getInnerDocument().updateContent()
        }
      }
    } catch (Exception e) {
      scriptLogger.error("[CUSTOM ACTION] - AddCommentSimpleViewExec - DOC n°" + document.getAirsRefId() + " - ERREUR : ", e)
      if (errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
    }
  }

  Utils.getSearchResultTableController().refreshAndKeepFilter()

  if (errorDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
  } else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments)
  }
} catch (Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - AddCommentSimpleViewExec - ERREUR : ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - ADD COMMENT SIMPLE VIEW EXEC - END")