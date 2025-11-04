import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document

/*************************************************************************************************
 * 							Check Sedex Documents Simple View - EXEC
 **************************************************************************************************
 Date : 04.03.2016
 Auteur : MTO

 Description : Contrôle un document de la corbeille Sedex
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - CHECK SEDEX DOCUMENTS SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
String errorDocuments = null
List<IDocument> docs = null

try {
  result = output.getValue()
  result.setMessageSummary(BundleUtils.getTranslation("groovy_checkSedexDocuments_action"))

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch (Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/

try {
  if (docs.size() == 0) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_selected_documents_empty"))
    return
  }

  for (IDocument document : docs) {
    try {
      if (document.getLockType() == Constants.DOC_LOCKED_BY_OTHER) {
        if (errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
      } else {
        String status = document.getField(Constants.LIST_WK_STATUT_CODE).getValue().toString()
        Document doc = new Document(userContext.getJeton(), document.getAirsRefId())

        if (String.valueOf(Constants.LIST_WK_STATUT_AVERIFICATION_LOT).equals(status) || String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equals(status)) {
          List<Integer> validedUsers =
                  (document.getField(Constants.FIELD_GESTIONNAIRES_CODE).getValues() == null) ? new ArrayList<Integer>() :
                          (List<Integer>) document.getField(Constants.FIELD_GESTIONNAIRES_CODE).getValues()
          if (!validedUsers.isEmpty()) {
            Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_PEC))
            try {
              if (Constants.WORKFLOW_SEDEX_REDEFINED_GESTIONNAIERS) {
                String gestionnaires = Methods.getActors(Methods.getFieldValue(doc, Constants.FIELD_NSS_CODE))
                Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_CODE, gestionnaires)
                scriptLogger.debug("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - Document Sedex avec redéfinition de l'attribution : " + gestionnaires)
              }
            } catch (Exception e) {
              scriptLogger.
                      warn("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - Document Sedex n°" + document.getAirsRefId() + " n'a pas été redistribué")
            }
          } else {
            boolean isConfidentiel = Methods.isWSConfidentiel(Methods.getFieldValue(doc, Methods.formatString(Constants.NSS_MASK, Constants.FIELD_NSS_CODE)))
            if (isConfidentiel) {
              Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_CODE, Constants.USER_GENERIC_CONFIDENTIEL_ID)
              Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_PEC))
              doc.setSecretLevel(Constants.SECRET_LEVEL_CONFIDENTIEL)
            } else {
              Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_GEST_INCONNU))
            }
            //Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE));
          }
          Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation(
                  "groovy_checkSedexDocuments_history_sedex_succes"))

          scriptLogger.debug("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - Document Sedex validé n°" + document.getAirsRefId())

        } else if (String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_DOSSIER).equals(status)) {
          Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE))
          Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation(
                  "groovy_checkSedexDocuments_history_transfert_succes"))

          scriptLogger.debug("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - Document DA-Dossier validé n°" + document.getAirsRefId())
        } else {
          if (errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
          else
            errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
        }
        doc.updateContent()
      }
    } catch (Exception e) {
      scriptLogger.error("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - DOC n°" + document.getAirsRefId() + " - ERREUR : ", e)
      if (errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
    }
  }

  Utils.getSearchResultController().replay()
  Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

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
  scriptLogger.error("[CUSTOM ACTION] - CheckSedexDocumentsSimpleViewExec - ERREUR : ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - CHECK SEDEX DOCUMENTS SIMPLE VIEW EXEC - END")