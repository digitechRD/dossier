import Constants
import DocumentGED
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
import com.digitech.jcorbairs.DocumentAction
import com.fasterxml.jackson.databind.ObjectMapper

import java.text.SimpleDateFormat

/*************************************************************************************************
 * 							EditIndexDocumentSimpleViewExec - EXEC
 **************************************************************************************************
 Date : 12.08.2016
 Auteur : MTO

 Description : Export l'édition de certains index par des personnes ayant un profil de consultation
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EDIT INDEX DOCUMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
IDocument doc = null
IRight rightMgr = null
try {
    result = output.getValue()
    result.setMessageSummary(BundleUtils.getTranslation("groovy_edit_action"))

    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()

} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - EditIndexDocumentSimpleViewExec - ERREUR : ", e)
    return
}
/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
        return
    }

    Document docToUpdate = null
    doc = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)
    rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR)

    if (!rightMgr.isAuthorizedToEditDocument(userContext, doc)) {
        docToUpdate = new Document(DossierCoreContext.getAdminJeton(), doc.getAirsRefId())
    } else {
        docToUpdate = new Document(userContext.getJeton(), doc.getAirsRefId())
    }
    String nss = ""
    for (String field : Arrays.asList(data.get("DATA_FIELDS_VISIBLE").toString().split("::"))) {
        if (data.get("DATA_FIELD_" + field) == null || String.valueOf(data.get("DATA_FIELD_" + field)).isEmpty()) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_field_empty"))
            return
        } else if (Constants.FIELD_NSS_CODE.equals(field)) {
            nss = data.get("DATA_FIELD_" + Constants.FIELD_NSS_CODE).toString().replaceAll("[^0-9\\*\\+]", "")
            if (nss.length() != Constants.NSS_COUNT_CARACTERS) {
                result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_nss_incorrect"))
                scriptLogger.error("[CUSTOM ACTION] - EditIndexDocumentSimpleViewExec - NSS incorect : " + nss)
                return
            } else
                Methods.defineDocumentIndex(docToUpdate, field, nss)
        } else if (Constants.FIELD_DATE_DOCUMENT_CODE.equals(field)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_INPUT, Locale.ENGLISH)
            Date date = simpleDateFormat.parse(data.get("DATA_FIELD_" + Constants.FIELD_DATE_DOCUMENT_CODE).toString())
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS, Locale.ENGLISH)
            Methods.defineDocumentIndex(docToUpdate, field, formatter.format(date))
        } else
            Methods.defineDocumentIndex(docToUpdate, field, String.valueOf(data.get("DATA_FIELD_" + field)))

        scriptLogger.debug(field + " / " + String.valueOf(data.get("DATA_FIELD_" + field)))
    }
    DocumentGED documentGED = new DocumentGED("editIndexDocument", (nss.isEmpty() ? Methods.getFieldValue(docToUpdate, Constants.FIELD_NSS_CODE) : nss), doc.getAirsRefId(), Methods.getFieldValue(docToUpdate, Constants.FIELD_DEM_CODE), Methods.getFieldValue(docToUpdate, Constants.LIST_TYPES_DOCUMENT_CODE),
            Methods.getFieldValue(docToUpdate, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(docToUpdate, Constants.LIST_WK_STATUT_CODE),
            Methods.getFieldValue(docToUpdate, Constants.FIELD_GESTIONNAIRE_CODE),
            Methods.getFieldValue(docToUpdate, Constants.FIELD_GESTIONNAIRES_CODE),
            Methods.getFieldValue(docToUpdate, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
            Methods.getFieldValue(docToUpdate, Constants.FIELD_CREATEUR_CODE), false, Methods.getFieldValue(docToUpdate, Constants.FIELD_NAME_CODE), Methods.getFieldValue(docToUpdate, Constants.FIELD_PRIORITE_CODE),
            Methods.getFieldValue(docToUpdate, Constants.FIELD_DATE_DOCUMENT_CODE))

    ObjectMapper mapper = new ObjectMapper()
    String jsonString = mapper.writeValueAsString(documentGED)
    try {
        String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Document")
        if (outputResFromAPI.contains("200")) {
            if (doc != null) {
                DocumentAction documentAction = new DocumentAction(userContext.getJeton(), Constants.ADV_EVENT_FIELDCHANGE)
                documentAction.setUsrId(userContext.getUserId())
                doc.getAirsDocument().addAction(documentAction)
            }

            result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))

            Utils.getSearchResultTableController().refreshAndKeepFilter()
        } else {
            scriptLogger.debug("[CUSTOM ACTION] - EditIndexDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
            return
        }
    } catch (Exception ex) {
        scriptLogger.debug("[CUSTOM ACTION] - EditIndexDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
        return
    }
} catch (Exception e) {
    scriptLogger.error("[CUSTOM ACTION] - EditIndexDocumentSimpleViewExec - ERREUR : ", e)
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    return
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT INDEX DOCUMENT SIMPLE VIEW EXEC - END")