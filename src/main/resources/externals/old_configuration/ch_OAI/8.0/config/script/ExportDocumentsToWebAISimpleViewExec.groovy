import Constants
import Methods
import WebAIGED
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.fasterxml.jackson.databind.ObjectMapper
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.text.SimpleDateFormat
import java.util.stream.Collectors

/*************************************************************************************************
 * 							    ExportDocumentToWebAISimpleView - EXEC
 **************************************************************************************************
 Date : 02.08.2016
 Auteur : MTO

 Description : Export document vers Web@AI
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
String errorDocuments = null
String warnDocuments = null
FileInputStream file = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
Document xmlDocument = null
List<Integer> listDocumentsExport = new ArrayList()
int successExport = 0

try {
    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()

    // Chargement du XML Configuration
    file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
    builderFactory = DocumentBuilderFactory.newInstance()
    builder = builderFactory.newDocumentBuilder()
    xmlDocument = builder.parse(file)

    result = output.getValue()
    result.setMessageSummary(BundleUtils.getTranslation("groovy_export_webai_action"))

} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR : ", e)
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

    boolean isExportFolder = Methods.isContentExist(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_IS_EXPORT_FOLDER_BY_ID.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER"))))
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - FILTRE ID " + String.valueOf(data.get("DATA_FILTER") + " / " + Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_BY_ID_CODE.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER")))))
    Map<String, List<String>> filters = Methods.getContentsMapWithFormat(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_BY_ID_CODE_MAP.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER"))), "%05d")
    String folder = data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH").toString()
    File folderFile = new File(folder)
    File folderTempFile = new File(ExportUtils.getExportPDFDirectory() + new Date().getTime())
    if (!folderTempFile.exists()) folderTempFile.mkdirs()
    scriptLogger.debug("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - DEBUG - Création du dossier temp : " + folderTempFile.exists() + " / " + folderTempFile.getAbsolutePath())
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - PATH XML : " + data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH").toString())
    File xml = new File(folderTempFile.getAbsolutePath() + File.separator + "temp.xml")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS)
    SimpleDateFormat simpleDateFormatSwiss = new SimpleDateFormat(Constants.DATE_FORMAT_SWISS)
    Date beginDate = simpleDateFormatSwiss.parse(data.get("DATA_BEGIN_DATE"))
    Date endDate = simpleDateFormatSwiss.parse(data.get("DATA_END_DATE"))
    if (isExportFolder && !filters.isEmpty()) {
        scriptLogger.debug(String.valueOf(data.get("DATA_NSS")) + " " + " " + filters + " " + simpleDateFormat.format(beginDate) + " " + simpleDateFormat.format(endDate))
        listDocumentsExport = Methods.getDocumentsListIdByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), filters, (Date) data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false, xmlDocument, String.valueOf(data.get("DATA_FILTER")), Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_INCLUDE_FILTERS_MAP)
    } else if (isExportFolder && filters.isEmpty()) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - DEBUG : DOSSIER COMPLET")
        listDocumentsExport = Methods.getDocumentsListIdByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), null, (Date) data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false, xmlDocument, String.valueOf(data.get("DATA_FILTER")), Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_INCLUDE_FILTERS_MAP)
    } else {
        docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
        for (IDocument document : docs) {
            boolean toAdd = true
            Date dateDocument = (Date) document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue()
            if (filters.isEmpty() && (((dateDocument.after(beginDate) && dateDocument.before(endDate)) || (dateDocument.compareTo(beginDate) == 0) || (dateDocument.compareTo(endDate) == 0)))) {
                scriptLogger.debug("	--> Sans Filtre Filtre")
                listDocumentsExport.add(document.getAirsRefId())
            } else {
                Set cles = filters.keySet()
                Iterator it = cles.iterator()
                while (it.hasNext()) {
                    String field = (String) it.next()
                    boolean hasExcluded = ("0".equals(Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_INCLUDE_FILTERS_MAP.replace("##REPLACE_VALUE##", field).replace("##ID##", String.valueOf(data.get("DATA_FILTER"))).toString())) ? true : false)
                    String values = (List<String>) filters.get(field)
                    if (toAdd && ((!hasExcluded && !values.contains(String.format("%05d", Integer.valueOf(String.valueOf(document.getField(field).getValue()))))) || (hasExcluded && values.contains(String.format("%05d", Integer.valueOf(String.valueOf(document.getField(field).getValue())))))) && (((dateDocument.after(beginDate) && dateDocument.before(endDate)) || (dateDocument.compareTo(beginDate) == 0) || (dateDocument.compareTo(endDate) == 0)))) {
                        toAdd = false
                    }

                }
                if (toAdd) {
                    listDocumentsExport.add(document.getAirsRefId())
                }
            }
        }
    }
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - COUNT DOCUMENT : " + listDocumentsExport.size())

    if (!listDocumentsExport.isEmpty()) {
        WebAIGED webAIGed = new WebAIGED("exportWebAIDocument",
                String.valueOf(data.get("DATA_NSS")).replaceAll("\\.", ""),
                listDocumentsExport.stream().map(String::valueOf).collect(Collectors.joining(";")),
                data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE").toString(),
                data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_UID").toString(),
                "\\\\AI0RMMSS2\\ExportWebAI_Test\\annexe\\"//data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH").toString()
        )

        ObjectMapper mapper = new ObjectMapper()
        String jsonString = mapper.writeValueAsString(webAIGed)
        try {
            String outputResFromAPI = Methods.sendRequestAPI(jsonString, "WebAI")
            if (outputResFromAPI.contains("200")) {
                result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success") + " - " + listDocumentsExport.size() + " " + BundleUtils.getTranslation("groovy_export_webai_export_documents"))
            } else {
                scriptLogger.debug("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR : " + outputResFromAPI)
                result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
                return
            }

        } catch (Exception ex) {
            scriptLogger.debug("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
            return
        }


    } else {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_list_file"))
    }

} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR : ", e)
    return
} finally{
    if(file != null) {
        try{
            file.close()
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ATTENTION - Fichier XML de configuration non cloturé : ", e)
        }
    }
}
scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
