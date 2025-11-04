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
 *							Définir priorité d'un document - EXEC
 **************************************************************************************************
 Date : 15.03.2016
 Auteur : MTO

 Description : Définir la priorité un document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DEFINE PRIORITY DOCUMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
String nss=null
String ndem=null
String name = null
IRight rightMgr = null
FileInputStream file = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
org.w3c.dom.Document xmlDocument = null
Properties conf = new Properties()
InputStreamReader inputStreamReader = null
IDocument iDocument = null

try {
    result = output.getValue()
	result.setMessageSummary(BundleUtils.getTranslation("groovy_move_action"))
	customActionController = Utils.getCustomActionController()
	data = customActionController.getModel().getModalPanelModel()
	docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
	scriptLogger.error("[CUSTOM ACTION] - DefinePriorityDocumentSimpleViewExec - ERREUR : ",e)
	return
}
/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - DEFINE PRIORITY DOCUMENT SIMPLE VIEW EXEC - END")
		return
	}
	iDocument = docs.get(0)
    Document document = new Document(DossierCoreContext.getAdminJeton(), iDocument.getAirsRefId())
	DocumentGED documentGED = new DocumentGED("definePriorityDocument",Methods.getFieldValue(document, Constants.FIELD_NSS_CODE),
		iDocument.getAirsRefId(),
		Methods.getFieldValue(document, Constants.FIELD_DEM_CODE),
		Methods.getFieldValue(document, Constants.LIST_TYPES_DOCUMENT_CODE),
		Methods.getFieldValue(document, Constants.LIST_GROUPES_DOCUMENT_CODE),Methods.getFieldValue(document, Constants.LIST_WK_STATUT_CODE),
		Methods.getFieldValue(document, Constants.FIELD_GESTIONNAIRE_CODE),
		Methods.getFieldValue(document, Constants.FIELD_GESTIONNAIRES_CODE),
		Methods.getFieldValue(document, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
		Methods.getFieldValue(document, Constants.FIELD_CREATEUR_CODE),false,
		Methods.getFieldValue(document, Constants.FIELD_NAME_CODE),
		((data.get("DATA_PRIORITY")!=null && !data.get("DATA_PRIORITY").toString().isEmpty())?data.get("DATA_PRIORITY").toString():null),
		Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE))

	ObjectMapper mapper = new ObjectMapper()
	String jsonString = mapper.writeValueAsString(documentGED)
	try{
		String outputResFromAPI = Methods.sendRequestAPI(jsonString,"Document")
		if(outputResFromAPI.contains("200")){
			if(iDocument != null) {
				DocumentAction documentAction = new DocumentAction(userContext.getJeton(), Constants.ADV_EVENT_FIELDCHANGE)
				documentAction.setUsrId(userContext.getUserId())
				iDocument.getAirsDocument().addAction(documentAction)
			}

		  result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
		  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))

		  Utils.getSearchResultTableController().refreshAndKeepFilter()
		}else{
			scriptLogger.debug("[CUSTOM ACTION] - DefinePriorityDocumentSimpleViewExec - ERREUR : "+outputResFromAPI)
			result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
			result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
			return
		}
		
	}catch(Exception ex){
		scriptLogger.debug("[CUSTOM ACTION] - DefinePriorityDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : "+jsonString)
		result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
		result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
		return
	}

}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewExec - ERREUR : ",e)
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
	return
}
scriptLogger.debug("[CUSTOM ACTION] - DEFINE PRIORITY DOCUMENT DOCUMENT SIMPLE VIEW EXEC - END")
