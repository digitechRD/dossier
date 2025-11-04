import Constants
import DocumentGED
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.DocumentAction
import com.fasterxml.jackson.databind.ObjectMapper

/*************************************************************************************************
 * 							Copier / Coller d'un document - EXEC
 **************************************************************************************************
 Date : 04.03.2016
 Auteur : MTO

 Description : Permet de copier / coller un document entre dossier
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
String errorDocuments = null
List<IDocument> docs = null
String nss = null
String ndem = null
String name = null
IRight rightMgr = null
Properties conf = new Properties()
InputStreamReader inputStreamReader = null
try {
    result = output.getValue()
    result.setMessageSummary(BundleUtils.getTranslation("groovy_copypaste_action"))

    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

    inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
    conf.load(inputStreamReader)
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : ", e)
    return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
Document document = null
try {
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW EXEC - END")
        return
    }

    if (data.get("DATA_NSS") == null || data.get("DATA_NSS").toString().isEmpty()) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_empty"))
        return
    } else {
        nss = data.get("DATA_NSS").toString().replaceAll("[^0-9\\*\\+]", "")

        String names = Methods.getInformationFromWebAI(conf.getProperty("webai.url.name.by.nss"), conf.getProperty("webai.json.request.name.by.nss").replace("##NSS##", Methods.formatString(nss, Constants.NSS_MASK)), conf.getProperty("webai.json.request.name.by.nss.information"))
        name = (names.isEmpty()) ? "" : names
        if (name.length() > 25) name = name.substring(0, 25)

        if (nss.length() != Constants.NSS_COUNT_CARACTERS || name.isEmpty()) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_incorrect"))
            scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW EXEC - END")
            return
        }


        if (nss.length() != Constants.NSS_COUNT_CARACTERS) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_incorrect"))
            return
        }
    }
    if (data.get("DATA_NDEM") != null && "1".equals(data.get("DATA_USE_NDEM").toString()) && (data.get("DATA_NDEM") == null
            || data.get("DATA_NDEM").toString().isEmpty())) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_ndem_empty"))
        return
    } else if ("0".equals(data.get("DATA_USE_NDEM").toString())) {
        ndem = data.get("DATA_DEFAULT_NDEM").toString()
    } else {
        ndem = data.get("DATA_NDEM").toString()
    }


    IDocument iDocument = docs.get(0)
    Document documentAirs = new Document(DossierCoreContext.getAdminJeton(), iDocument.getAirsRefId())
    DocumentGED documentGED = new DocumentGED("copyDocument", nss, iDocument.getAirsRefId(), ndem, data.get("DATA_LIST_DOCUMENT_TYPE").toString(),
            "", String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE),
            "",
            "",
            "",
            String.valueOf(userContext.getUserId()), false, name, Methods.getFieldValue(documentAirs, Constants.FIELD_PRIORITE_CODE),
            Methods.getFieldValue(documentAirs, Constants.FIELD_DATE_DOCUMENT_CODE))

    ObjectMapper mapper = new ObjectMapper()
    String jsonString = mapper.writeValueAsString(documentGED)
    try {
        String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Document")
        if (outputResFromAPI.contains("200")) {
            if (document != null) {
                DocumentAction documentAction = new DocumentAction(userContext.getJeton(), Constants.ADV_EVENT_DOCCREATE)
                documentAction.setUsrId(userContext.getUserId())
                document.addAction(documentAction)
            }

            result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))

            Utils.getSearchResultTableController().refreshAndKeepFilter()
        } else {
            scriptLogger.debug("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
            return
        }

    } catch (Exception ex) {
        scriptLogger.debug("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
        return
    }


} catch (Exception e) {
    if (document != null) {
        document.destroy()
    }
    scriptLogger.error("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : ", e)
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    return
}

scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW EXEC - END")
 