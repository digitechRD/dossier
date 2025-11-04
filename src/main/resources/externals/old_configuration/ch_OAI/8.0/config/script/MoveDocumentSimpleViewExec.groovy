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

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/*************************************************************************************************
 * 							Déplacement d'un document - EXEC
 **************************************************************************************************
 Date : 15.03.2016
 Auteur : MTO

 Description : Permet de déplacer un document entre dossier
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - MOVE DOCUMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
String nss = null
String ndem = null
String name = null
IRight rightMgr = null
FileInputStream file = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
org.w3c.dom.Document xmlDocument = null
Properties conf = new Properties()
InputStreamReader inputStreamReader = null
String errorDocuments = null

try {
    result = output.getValue()
    result.setMessageSummary(BundleUtils.getTranslation("groovy_move_action"))

    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

    // Chargement du XML Configuration
    file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
    builderFactory = DocumentBuilderFactory.newInstance()
    builder = builderFactory.newDocumentBuilder()
    xmlDocument = builder.parse(file)

    inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
    conf.load(inputStreamReader)

}catch(Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewExec - ERREUR : ", e)
    return
}
/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
        return
    }

    if(data.get("DATA_NSS") == null || data.get("DATA_NSS").toString().isEmpty()) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_nss_empty"))
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
        return
    }else {
        nss = data.get("DATA_NSS").toString().replaceAll("[^0-9\\*\\+]", "")
        // Recherche du nom - prénom
        String names = Methods.getInformationFromWebAI(conf.getProperty("webai.url.name.by.nss"), conf.getProperty("webai.json.request.name.by.nss").replace("##NSS##", Methods.formatString(nss, Constants.NSS_MASK)), conf.getProperty("webai.json.request.name.by.nss.information"))
        name = (names.isEmpty()) ? "" : names
        if (name.length() > 25) name = name.substring(0, 25)

        if (nss.length() != Constants.NSS_COUNT_CARACTERS || name.isEmpty()) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_move_nss_incorrect"))
            scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
            return
        }
    }

    if (!Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_NDEM_LIST).contains(data.get("DATA_NDEM")) && "1".equalsIgnoreCase(Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GLOBAL_USE_NDEM_LIMITED_LIST))) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_ndem_incorrect"))
        return
    }

    if (data.get("DATA_NDEM") != null && "1".equals(data.get("DATA_USE_NDEM").toString()) && (data.get("DATA_NDEM") == null || data.get("DATA_NDEM").toString().isEmpty())) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_ndem_empty"))
        return
    } else if ("0".equals(data.get("DATA_USE_NDEM").toString())) {
        ndem = data.get("DATA_DEFAULT_NDEM").toString()
    } else {
        ndem = data.get("DATA_NDEM").toString()
    }
    for(IDocument iDocument : docs) {
        Document docToUpdate = new Document(DossierCoreContext.getAdminJeton(), iDocument.getAirsRefId())
        DocumentGED documentGED = new DocumentGED("moveDocument", nss, iDocument.getAirsRefId(), ndem, Methods.getFieldValue(docToUpdate, Constants.LIST_TYPES_DOCUMENT_CODE),
                Methods.getFieldValue(docToUpdate, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(docToUpdate, Constants.LIST_WK_STATUT_CODE),
                Methods.getFieldValue(docToUpdate, Constants.FIELD_GESTIONNAIRE_CODE),
                Methods.getFieldValue(docToUpdate, Constants.FIELD_GESTIONNAIRES_CODE),
                Methods.getFieldValue(docToUpdate, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
                Methods.getFieldValue(docToUpdate, Constants.FIELD_CREATEUR_CODE), data.get("DATA_REFRESH_WORKFLOW"), name, Methods.getFieldValue(docToUpdate, Constants.FIELD_PRIORITE_CODE),
                Methods.getFieldValue(docToUpdate, Constants.FIELD_DATE_DOCUMENT_CODE))

        ObjectMapper mapper = new ObjectMapper()
        String jsonString = mapper.writeValueAsString(documentGED)

        try {
            String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Document")
            if (outputResFromAPI.contains("200")) {
                result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
                if (iDocument != null) {
                    DocumentAction documentAction = new DocumentAction(userContext.getJeton(), Constants.ADV_EVENT_FIELDCHANGE)
                    documentAction.setUsrId(userContext.getUserId())
                    iDocument.getAirsDocument().addAction(documentAction)
                }
            } else {
                scriptLogger.debug("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
                if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                isInError = true
            }

        } catch (Exception ex) {
            scriptLogger.debug("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
            isInError = true
        }
    }


    if (errorDocuments == null) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
    } else {
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments)
    }

    Utils.getSearchResultTableController().refreshAndKeepFilter()


}catch(Exception e) {
    scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewExec - ERREUR : ", e)
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    return
}finally{
    if(file != null) {
        try{
            file.close()
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - MoveDocumentSimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ", e)
        }
    }
}

scriptLogger.debug("[CUSTOM ACTION] - MOVE DOCUMENT SIMPLE VIEW EXEC - END")