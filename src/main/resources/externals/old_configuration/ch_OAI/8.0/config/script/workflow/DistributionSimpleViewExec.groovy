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
import com.digitech.jcorbairs.Note
import com.digitech.jcorbairs.admin.ProfilAdmin
import com.fasterxml.jackson.databind.ObjectMapper

import java.util.stream.Collectors

/*************************************************************************************************
 *   					    			Distribution - EXEC
 **************************************************************************************************
 Date : 24.02.2016
 Auteur : MTO

 Description : Permet la prise en compte ou la distribution des documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTION SIMPLE VIEW EXEC - START")

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
	scriptLogger.error("[CUSTOM ACTION] - DistribuitionSimpleViewExec - ERREUR : ", e)
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
				Map<Integer, Boolean> userChecksMap = (Map<Integer, Boolean>) data.get("DATA_USERS_CHECKED")

				// Vérification de la sélection des utilisateurs
				if (!userChecksMap.containsValue(true)) {
					scriptLogger.warn("[CUSTOM ACTION] - DistributionSimpleViewExec - ATTENTION : Distribution - Aucun utilisateur sélectionné")
					result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
					result.setMessageDetail(BundleUtils.getTranslation("groovy_postman_users_empty"))
					return
				}

				String users = null
				for (Map.Entry<Integer, Boolean> entry : userChecksMap.entrySet()) {
					if (users == null && entry.getValue()) users = Methods.getUserName(entry.getKey())
					else if (entry.getValue()) users += " - " + Methods.getUserName(entry.getKey())
				}

				if (users == null) {
					scriptLogger.warn("[CUSTOM ACTION] - DistributionSimpleViewExec - ATTENTION : Distribution - Aucun utilisateur sélectionné")
					result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
					result.setMessageDetail(BundleUtils.getTranslation("groovy_postman_users_empty"))
					return
				}

				// Ajout de la sélection des utilisateurs
				scriptLogger.debug("[CUSTOM ACTION] - DistributionSimpleViewExec - DEBUG Distribution (DocID : " + doc.getId() + ") : Statut --> " + String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue()) + " / Validateur --> " + userContext.getUserId() + " / Gestionnaires --> " + users)
				if (String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue()))) {
					if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
					else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_error_status") + ")"
					isInError = true
				} else {
					List<String> usersToAdd = new ArrayList()
					for (Map.Entry<String, String> entry : userChecksMap.entrySet()) {
						if ((listUsers == null || !listUsers.contains(entry.getKey())) && entry.getValue()) {
							usersToAdd.add(entry.getKey())
						}
					}
					String lastSearch = ""
					if (userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch() != null && !userContext.getUserSpace().getPersonnalSpace().getHistorics().getSearch().isEmpty()) {
						ISearchModel curSearch = userContext.getCurrentSearchModel()
						lastSearch = curSearch.getRequest()
					}
					WorkflowGED workflowGED = new WorkflowGED("distributeDocument", document.getAirsRefId(), Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE),
							Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(doc, Constants.LIST_WK_STATUT_CODE),
							Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRE_CODE),
							Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_CODE),
							Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
							Methods.getFieldValue(doc, Constants.FIELD_CREATEUR_CODE),
							userContext.getUserId().toString(),
							lastSearch,
							usersToAdd.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(";", "", "")))
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

				}


				// Commentaire
				if (data.get("DATA_COMMENT").toString().length() > 0 && !BundleUtils.getTranslation("groovy_comment_default_message").equals(data.get("DATA_COMMENT").toString())) {
					Note newNote = new Note(Constants.AIRS_NOTE_ID)
					newNote.setText(data.get("DATA_COMMENT").toString())
					newNote.setUserId(userContext.getUserId())
					document.getAirsDocument().addComment(newNote)
				}

			} else {
				if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
				else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_block_document") + ")"
			}
		}catch(Exception e) {
			scriptLogger.error("[CUSTOM ACTION] - DistributionSimpleViewExec - ERREUR (DocID - " + doc.getId() + ") : ", e)
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

} catch (Exception ex) {
	scriptLogger.error("[CUSTOM ACTION] - DistributionSimpleViewExec - ERREUR : ", ex)
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
}

scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTION SIMPLE VIEW EXEC - END")

