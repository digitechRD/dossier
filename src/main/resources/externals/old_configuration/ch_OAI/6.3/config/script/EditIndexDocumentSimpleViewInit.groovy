import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.sun.msv.verifier.jarv.Const
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
 *							    EditIndexDocumentSimpleViewInit - INIT
 **************************************************************************************************
 Date : 11.08.2016
 Auteur : MTO

 Description : Export l'édition de certains index par des personnes ayant un profil de consultation
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EDIT INDEX DOCUMENT SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
IDocument document = null;
String request = null;
String login = null;
List<SelectItem> filter = new ArrayList<SelectItem>();
FileInputStream file = null;
DocumentBuilderFactory builderFactory = null;
DocumentBuilder builder = null;
Document xmlDocument = null;
String fieldsList = null;

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
    scriptLogger.error("[CUSTOM ACTION] - EditIndexDocumentSimpleViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
    if(docs.size() == 0){
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false);
        return;
    }else if(docs.size() > 1){
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_one_document_only"), false);
        return;
    }

    fieldsList = Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_INDEXATION_DOCUMENTS);
    document = docs.get(0);
    for(String field : Arrays.asList(fieldsList.split("::"))){
        if(field.startsWith("D_")) {
            data.put("DATA_FIELD_" + field, new SimpleDateFormat(Constants.DATE_FORMAT_SWISS).format(docs.get(0).getField(field).getValue()));
        }else data.put("DATA_FIELD_"+field, docs.get(0).getField(field).getValue());

        if(field.equalsIgnoreCase(Constants.LIST_TYPES_DOCUMENT_CODE)){
            if (Constants.USE_GROUP_LIST) {
                data.put("DATA_LIST_DOCUMENT_GROUPS", Methods.getAuthorityListOfSelectItem(Constants.LIST_GROUPES_DOCUMENT_ID));
                data.put("DATA_AIRSDOSSIER_URL", Constants.APPLICATION_AIRSDOSSIER_URL+"rest/DocSeries/al/11/link/10/"+userContext.getUser().getLogin()+"/##ID_GROUP##");
            }else data.put("DATA_LIST_DOCUMENT_TYPES", Methods.getAuthorityListOfSelectItem(Constants.LIST_TYPES_DOCUMENT_ID));
        }else if(field.equalsIgnoreCase(Constants.FIELD_DEM_CODE)) {
            data.put("DATA_LIST_NDEM", Methods.listToSelectItem(Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_NDEM_LIST)));
        }
    }

    data.put("DATA_FIELDS_VISIBLE", fieldsList);

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
    scriptLogger.error("[CUSTOM ACTION] - EditIndexDocumentSimpleViewInit - ERREUR : ",e);
    return;
}finally{
    if(file != null) {
        try{
            file.close();
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - EditIndexDocumentSimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ",e);
        }
    }
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT INDEX DOCUMENT SIMPLE VIEW INIT - END");

