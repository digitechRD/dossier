package workflow

import Constants
import Methods
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
import com.digitech.jcorbairs.admin.ProfilAdmin

import java.text.SimpleDateFormat

/*************************************************************************************************
 *   					    			PEC - EXEC
 **************************************************************************************************
 Date : 24.02.2016
 Auteur : MTO

 Description : Permet la prise en compte ou la distribution des documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - PEC SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = output.getValue()
result.setMessageSummary(BundleUtils.getTranslation("groovy_postman_action"))
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> selectedDocs = null
String errorDocuments = null
ProfilAdmin profilAdmin = null
IRight rightMgr = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  selectedDocs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR)
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_exec_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
    return
  }

  Document doc = null
  for(IDocument document : selectedDocs) {
    boolean isInError = false
    try {
      if(!rightMgr.isAuthorizedToEditDocument(userContext, document)) {
        document = Methods.getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
      }

      doc = document.getAirsDocument().getInnerDocument()

      if(Constants.UNLOCK_TYPE.equals(document.getLockType())) {
        List<Object> listUsers = Methods.getFieldMultiValue(document, Constants.FIELD_GESTIONNAIRES_CODE)
        // Prise en compte
        scriptLogger.debug("[CUSTOM ACTION] - PECSimpleViewExec - DEBUG Prise en compte (DocID : " + doc.getId() + ") : Statut --> " + String.valueOf(
            document.getField(Constants.LIST_WK_STATUT_CODE).getValue()) + " / Validateur --> " + userContext.getUserId())
        if(!String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_PEC).equalsIgnoreCase(String.valueOf(
            document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) &&
            !(String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) &&
                Constants.WORKFLOW_SEDEX_TO_ARCHIVE_AUTHORIZED)) {
          if(errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
          else
            errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
          isInError = true
        }
        else if(String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) &&
            Constants.WORKFLOW_SEDEX_TO_ARCHIVE_AUTHORIZED) {
          Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE))
          Methods.defineDocumentIndex(doc, Constants.FIELD_FLAG_DATE_ARCHIVE_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
          Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_CODE, null)
        }
        else if(listUsers != null && listUsers.contains(userContext.getUserId())) {
          String users = Methods.removeValueInFieldMultiValue(document, Constants.FIELD_GESTIONNAIRES_CODE, userContext.getUserId())
          Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_CODE, users)
          Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE, Methods.addValueInFieldMultiValue(document, Constants.
              FIELD_GESTIONNAIRES_HISTORIQUE_CODE, (Integer) userContext.getUserId()))
          Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                                 BundleUtils.getTranslation("groovy_postman_history_pec"))
          if(users.isEmpty()) {
            Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE))
            Methods.defineDocumentIndex(doc, Constants.FIELD_FLAG_DATE_ARCHIVE_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
          }
        }
        else if((profilAdmin = Methods.hasActorGeneric(listUsers, userContext.getUserId())) != null) {
          //if (profilAdmin.getUserIds().contains(userContext.getUserId())) {
          Integer usurpedUserId = Methods.getUserIdBasketByProfil(profilAdmin, listUsers)
          Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE, Methods.addValueInFieldMultiValue(document, Constants.
              FIELD_GESTIONNAIRES_HISTORIQUE_CODE, usurpedUserId))
          String users = Methods.removeValueInFieldMultiValue(document, Constants.FIELD_GESTIONNAIRES_CODE, usurpedUserId)
          Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_CODE, users)
          Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                                 BundleUtils.getTranslation("groovy_postman_history_pec") + " (" + Methods.getUserName(usurpedUserId) + ")")
          if(users.isEmpty()) {
            Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE))
            Methods.defineDocumentIndex(doc, Constants.FIELD_FLAG_DATE_ARCHIVE_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
          }
          /*} else {
            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")";
            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")";
            isInError = true;
          }*/
        }
        else if(userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch() != null && !
            userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch().isEmpty()) {
          String lastSearch = userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch().last().getAirsRequest()
          String fieldToCheck = Constants.FIELD_GESTIONNAIRES_CODE + ".USR="
          if(lastSearch.indexOf(fieldToCheck) > -1) {
            String usurpedUserId = lastSearch.substring(lastSearch.indexOf(fieldToCheck) + fieldToCheck.length() + 1)
            usurpedUserId = usurpedUserId.substring(0, usurpedUserId.indexOf("\""))
            if(listUsers.contains(Integer.parseInt(usurpedUserId))) {
              Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE, Methods.
                  addValueInFieldMultiValue(document, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE, Integer.parseInt(usurpedUserId)))
              String users = Methods.removeValueInFieldMultiValue(document, Constants.FIELD_GESTIONNAIRES_CODE, Integer.parseInt(usurpedUserId))
              Methods.defineDocumentIndex(doc, Constants.FIELD_GESTIONNAIRES_CODE, users)
              Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                                     BundleUtils.getTranslation("groovy_postman_history_pec") + " (" +
                                                         Methods.getUserName(Integer.parseInt(usurpedUserId)) + ")")
              if(users.isEmpty()) {
                Methods.defineDocumentIndex(doc, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE))
                Methods.defineDocumentIndex(doc, Constants.FIELD_FLAG_DATE_ARCHIVE_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
              }
            }
            else {
              if(errorDocuments == null)
                errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
              else
                errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
              isInError = true
            }
          }
          else {
            if(errorDocuments == null)
              errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
            else
              errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
            isInError = true
          }
        }
        else {
          if(errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
          else
            errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
          isInError = true
        }


        // Commentaire
        if(data.get("DATA_COMMENT").toString().length() > 0 && !BundleUtils.getTranslation("groovy_comment_default_message").equals(data.get("DATA_COMMENT").
            toString())) {
          Note newNote = new Note(Constants.AIRS_NOTE_ID)
          newNote.setText(data.get("DATA_COMMENT").toString())
          newNote.setUserId(userContext.getUserId())
          document.getAirsDocument().addComment(newNote)
        }
        if(!isInError)
          document.getAirsDocument().updateContents()
      }
      else {
        if(errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
      }
    } catch(Exception e) {
      scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewExec - ERREUR (DocID - " + doc.getId() + ") : ", e)
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
    }
  }

  if(errorDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments)
  }

  Utils.getSearchResultTableController().refreshAndKeepFilter()

} catch(Exception ex) {
  scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewExec - ERREUR : ", ex)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
}

scriptLogger.debug("[CUSTOM ACTION] - PEC SIMPLE VIEW EXEC - END")