import Constants
import Methods
import WorkflowGED
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.admin.ProfilAdmin
import com.fasterxml.jackson.databind.ObjectMapper

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
} catch (Exception e) {
	Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_exec_error"), false)
	scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewExec - ERREUR : ", e)
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

	if (Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() == 0) {
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
		result.setMessageDetail(BundleUtils.getTranslation("groovy_selected_documents_empty"))
		scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END")
		return
	}

	Document doc = null
	for (IDocument document : selectedDocs) {
		boolean isInError = false
		try {
			if (!rightMgr.isAuthorizedToEditDocument(userContext, document)) {
				document = Methods.getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
			}

			doc = document.getAirsDocument().getInnerDocument()

			if (Constants.UNLOCK_TYPE.equals(document.getLockType())) {
				List<Object> listUsers = Methods.getFieldMultiValue(document, Constants.FIELD_GESTIONNAIRES_CODE)
				// Prise en compte
				scriptLogger.debug("[CUSTOM ACTION] - PECSimpleViewExec - DEBUG Prise en compte (DocID : " + doc.getId() + ") : Statut --> " + String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue()) + " / Validateur --> " + userContext.getUserId())
				if (!String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_PEC).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) &&
						!(String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) && Constants.WORKFLOW_SEDEX_TO_ARCHIVE_AUTHORIZED)) {
					if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
					else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
					isInError = true
				}
				String lastSearch = ""
				if (userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch() != null && !userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch().isEmpty()) {
					ISearchModel curSearch = userContext.getCurrentSearchModel()
					lastSearch = curSearch.getRequest()
				}

				WorkflowGED workflowGED = new WorkflowGED("pecDocument", document.getAirsRefId(), Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE),
						Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(doc, Constants.LIST_WK_STATUT_CODE),
						Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRE_CODE),
						Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_CODE),
						Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
						Methods.getFieldValue(doc, Constants.FIELD_CREATEUR_CODE),
						userContext.getUserId().toString(),
						lastSearch,
						"")

				ObjectMapper mapper = new ObjectMapper()
				String jsonString = mapper.writeValueAsString(workflowGED)
				try {
					String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Workflow")
					if (outputResFromAPI.contains("200")) {
						result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
						result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
						Utils.getSearchResultTableController().refreshAndKeepFilter()
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
			} else {
				if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
				else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
			}
		}catch(Exception e) {
			scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewExec - ERREUR (DocID - " + doc.getId() + ") : ", e)
			if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
			else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")"
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

} catch(Exception ex) {
	scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewExec - ERREUR : ", ex)
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
}

scriptLogger.debug("[CUSTOM ACTION] - PEC SIMPLE VIEW EXEC - END")