import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import org.w3c.dom.Document

import javax.faces.model.SelectItem;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;

import Constants;
import Methods

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.text.SimpleDateFormat

/*************************************************************************************************
 *							    ExportDocumentToWebAISimpleView - INIT
 **************************************************************************************************
 Date : 22.03.2016
 Auteur : MTO

 Description : Export document vers Web@AI
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
String request = null;
String login = null;
List<SelectItem> filter = new ArrayList<SelectItem>();
FileInputStream file = null;
DocumentBuilderFactory builderFactory = null;
DocumentBuilder builder = null;
Document xmlDocument = null;
List<SelectItem> filtersList = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
    request = userContext.getCurrentSearchModel().getRequest();
    login = userContext.getLoggedUser().getLogin();

    // Chargement du XML Configuration
    file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH));
    builderFactory = DocumentBuilderFactory.newInstance();
    builder =  builderFactory.newDocumentBuilder();
    xmlDocument = builder.parse(file);

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
    scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (request.contains(Constants.FIELD_NSS_CODE+"=\"")) {
        String nss = request.replace(Constants.FIELD_NSS_CODE+"=\"", "");
        int index = nss.indexOf("\"");
        nss = nss.substring(0, index).replace("(","").replace(")","");
        
		// Modification pour effectuer des tests avec WebAI 3 et les logins de test
		File xml2 = new File(Constants.APPLICATION_WEBAI_FOLDER + nss + "." + login + ".xml");
		File xml = new File(Constants.APPLICATION_WEBAI_FOLDER + nss + "." + login + "W3.xml");
        if (xml.exists()){
            data.put("DATA_FILE", xml);
            data.put("DATA_NSS", Methods.formatString(nss, Constants.NSS_MASK));
            data.put("DATA_BEGIN_DATE", "01.01.0100");
            if(Constants.DATE_EXPORT_END_DEFAULT != null) {
                data.put("DATA_END_DATE", Constants.DATE_EXPORT_END_DEFAULT);
            }else data.put("DATA_END_DATE", new SimpleDateFormat(Constants.DATE_FORMAT_SWISS).format(new Date()));
            if(Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() > 0)
                filtersList = Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_TITLE_ALL);
            else filtersList = Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_TITLE_WITHOUT_SELECTED_DOCUMENTS);
            if(filtersList == null || filtersList.isEmpty()){
                scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewInit - ERREUR : Liste des filtres vide");
                Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
            }
            data.put("DATA_FILTERS", filtersList);
            data.put("DATA_FILTER", "");

            // Récupération des informations du fichier XML
            InputStream fileWebAI = new FileInputStream(xml);
            Document xmlWebAI = builder.parse(fileWebAI);
            Map<String, String> informations = new HashMap();
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_LOT_ID", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_LOT_ID));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_UID", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_UID));
            data.put("DATA_INFORMATIONS_WEBAI", informations);
        }
		else if (xml2.exists()){
            data.put("DATA_FILE", xml2);
            data.put("DATA_NSS", Methods.formatString(nss, Constants.NSS_MASK));
            data.put("DATA_BEGIN_DATE", "01.01.0100");
            if(Constants.DATE_EXPORT_END_DEFAULT != null) {
                data.put("DATA_END_DATE", Constants.DATE_EXPORT_END_DEFAULT);
            }else data.put("DATA_END_DATE", new SimpleDateFormat(Constants.DATE_FORMAT_SWISS).format(new Date()));
            if(Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() > 0)
                filtersList = Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_TITLE_ALL);
            else filtersList = Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_TITLE_WITHOUT_SELECTED_DOCUMENTS);
            if(filtersList == null || filtersList.isEmpty()){
                scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewInit - ERREUR : Liste des filtres vide");
                Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
            }
            data.put("DATA_FILTERS", filtersList);
            data.put("DATA_FILTER", "");

            // Récupération des informations du fichier XML
            InputStream fileWebAI = new FileInputStream(xml2);
            Document xmlWebAI = builder.parse(fileWebAI);
            Map<String, String> informations = new HashMap();
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_LOT_ID", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_LOT_ID));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE));
            informations.put("XML_WEBAI_REQUEST_EXPORT_WEBAI_UID", Methods.getContent(xmlWebAI, Constants.XML_WEBAI_REQUEST_EXPORT_WEBAI_UID));
            data.put("DATA_INFORMATIONS_WEBAI", informations);
        }
        else{
            Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_export_webai_xml_no_exist"), false);
            return;
        }
    }
    else {
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_export_not_search"), false);
        return;
    }

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
    scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewInit - ERREUR : ",e);
    return;
}finally{
    if(file != null) {
        try{
            file.close();
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ",e);
        }
    }
}

scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW INIT - END");
