import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.jcorbairs.Document
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.jcorbairs.DocumentAction
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import Constants;
import Methods


/*************************************************************************************************
 *							Déplacement d'un document - EXEC
 **************************************************************************************************
 Date : 15.03.2016
 Auteur : MTO

 Description : Permet de déplacer un document entre dossier
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - MOVE DOCUMENT SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
String nss=null;
String ndem=null;
IRight rightMgr = null;
FileInputStream file = null;
DocumentBuilderFactory builderFactory = null;
DocumentBuilder builder = null;
org.w3c.dom.Document xmlDocument = null;

try {
    result = output.getValue();
    result.setMessageSummary(BundleUtils.getTranslation("groovy_move_action"));

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
	
	// Chargement du XML Configuration
    file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH));
    builderFactory = DocumentBuilderFactory.newInstance();
    builder =  builderFactory.newDocumentBuilder();
    xmlDocument = builder.parse(file);
	
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
    scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewExec - ERREUR : ",e);
    return;
}
/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END");
        return;
    }

    if(data.get("DATA_NSS") == null || data.get("DATA_NSS").toString().isEmpty()){
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_nss_empty"));
        return;
    }else{
        nss = data.get("DATA_NSS").toString().replaceAll("[^0-9\\*\\+]", "");
        if(nss.length() != Constants.NSS_COUNT_CARACTERS){
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
            result.setMessageDetail(BundleUtils.getTranslation("groovy_move_nss_incorrect"));
            return;
        }
    }

	if(!Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_NDEM_LIST).contains(data.get("DATA_NDEM")) && "1".equalsIgnoreCase(Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GLOBAL_USE_NDEM_LIMITED_LIST))){
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_ndem_incorrect"));
        return;
	}

    // Recherche du nom - prénom
    String name = null;
    List<String> names = Methods.getRequestInWebAI(nss, "name");
    name = (names.isEmpty())?"":names.get(0);
    if(name.length()>25) name = name.substring(0,25);

    if(data.get("DATA_NDEM") != null && "1".equals(data.get("DATA_USE_NDEM").toString()) && (data.get("DATA_NDEM") == null || data.get("DATA_NDEM").toString().isEmpty())){
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail(BundleUtils.getTranslation("groovy_move_ndem_empty"));
        return;
    }else if("0".equals(data.get("DATA_USE_NDEM").toString())) {
        ndem = data.get("DATA_DEFAULT_NDEM").toString();
    }else{
        ndem = data.get("DATA_NDEM").toString();
    }

    rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);

    for(IDocument iDocument : docs){
        if (!rightMgr.isAuthorizedToEditDocument(userContext, iDocument)) {
            Document docToUpdate = new Document(DossierCoreContext.getAdminJeton(), iDocument.getAirsRefId());
            Methods.defineDocumentIndex(docToUpdate, Constants.FIELD_NSS_CODE, nss);
            if (ndem != null) Methods.defineDocumentIndex(docToUpdate, Constants.FIELD_DEM_CODE, ndem);
            Methods.defineDocumentIndex(docToUpdate, Constants.FIELD_NAME_CODE, name);
			Methods.getAuditMgr().addDocumentEvent(userContext, iDocument, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("groovy_move_historisation") + " : " + Methods.formatString(iDocument.getField(Constants.FIELD_NSS_CODE).getValue().toString(), Constants.NSS_MASK));
            docToUpdate.updateContent();
        }else {
            Document docToUpdate = new Document(userContext.getJeton(), iDocument.getAirsRefId());
            Methods.defineDocumentIndex(docToUpdate, Constants.FIELD_NSS_CODE, nss);
            if (ndem != null) Methods.defineDocumentIndex(docToUpdate, Constants.FIELD_DEM_CODE, ndem);
            Methods.defineDocumentIndex(docToUpdate, Constants.FIELD_NAME_CODE, name);
			Methods.getAuditMgr().addDocumentEvent(userContext, iDocument, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("groovy_move_historisation") + " : " + Methods.formatString(iDocument.getField(Constants.FIELD_NSS_CODE).getValue().toString(), Constants.NSS_MASK));
            docToUpdate.updateContent();
        }
        DocumentAction documentAction = new DocumentAction(userContext.getJeton(), Constants.ADV_EVENT_FIELDCHANGE);
        documentAction.setUsrId(userContext.getUserId());
        iDocument.getAirsDocument().addAction(documentAction);
    }


    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"));

    Utils.getSearchResultTableController().refreshAndKeepFilter();

}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewExec - ERREUR : ",e);
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
    return;
}finally{
    if(file != null) {
        try{
            file.close();
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - MoveDocumentSimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ",e);
        }
    }
}

scriptLogger.debug("[CUSTOM ACTION] - MOVE DOCUMENT SIMPLE VIEW EXEC - END");