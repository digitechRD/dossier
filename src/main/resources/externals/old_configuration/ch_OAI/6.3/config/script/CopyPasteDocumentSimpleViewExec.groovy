import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.DocumentAction;
import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.PrimaryDocument;

import Constants;
import Methods

/*************************************************************************************************
 *							Copier / Coller d'un document - EXEC
 **************************************************************************************************
 Date : 04.03.2016
 Auteur : MTO

 Description : Permet de copier / coller un document entre dossier
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;
String errorDocuments = null;
List<IDocument> docs = null;
String nss = null;
String ndem = null;
IRight rightMgr = null;

try {
	result = output.getValue();
	result.setMessageSummary(BundleUtils.getTranslation("groovy_copypaste_action"));

	customActionController = Utils.getCustomActionController();
	data = customActionController.getModel().getModalPanelModel();

	docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
}catch(Exception e){
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
	scriptLogger.error("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : ",e);
	return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
Document document = null;
try {
	if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
		scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END");
		return
	}

	if (data.get("DATA_NSS") == null || data.get("DATA_NSS").toString().isEmpty()) {
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_empty"));
		return;
	} else {
		nss = data.get("DATA_NSS").toString().replaceAll("[^0-9\\*\\+]", "");
		if (nss.length() != Constants.NSS_COUNT_CARACTERS) {
			result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
			result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_incorrect"));
			return;
		}
	}

	if (data.get("DATA_DEM") != null && "1".equals(data.get("DATA_USE_NDEM").toString()) && (data.get("DATA_DEM") == null
			|| data.get("DATA_DEM").toString().isEmpty())) {
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail(BundleUtils.getTranslation("groovy_move_ndem_empty"));
		return;
	} else if ("0".equals(data.get("DATA_USE_NDEM").toString())) {
		ndem = data.get("DATA_DEFAULT_NDEM").toString();
	} else {
		ndem = data.get("DATA_NDEM").toString();
	}

	IDocument iDocument = docs.get(0);

	rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);
	if (!rightMgr.isAuthorizedToEditDocument(userContext, iDocument)) {
		UserCoreContext userContextAdmin = new UserCoreContext(DossierCoreContext.getAdminJeton());
		Domain domain = new Domain(DossierCoreContext.getAdminJeton(), Constants.CTY_DOCUMENT_ASSURE);
		document = new Document(DossierCoreContext.getAdminJeton(), domain, Constants.SECRET_LEVEL_DEFAULT);
		document.updateContent();

		// Recherche du nom - prénom
		String name = null;
		try {
			List<String> names = Methods.getRequestInWebAI(Methods.getFieldValue(document, Constants.FIELD_NSS_CODE), "name");
			name = (names.isEmpty()) ? "" : names.get(0);
			if (name.length() > 25) name = name.substring(0, 25);
		}catch(Exception e){
			scriptLogger.warn("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ATTENTION :  ",e);
		}

		Methods.defineDocumentIndex(document, Constants.FIELD_NSS_CODE, nss);
		Methods.defineDocumentIndex(document, Constants.FIELD_DEM_CODE, ndem);
		Methods.defineDocumentIndex(document, Constants.LIST_TYPES_DOCUMENT_CODE, data.get("DATA_LIST_DOCUMENT_TYPE").toString());
		Methods.defineDocumentIndex(document, Constants.FIELD_DATE_DOCUMENT_CODE, Methods.convertDateForAIRS(data.get("DATA_DATE").toString()));
		Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, data.get("DATA_COUNT_PAGES").toString());
		Methods.defineDocumentIndex(document, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE));
		Methods.defineDocumentIndex(document, Constants.FIELD_CREATEUR_CODE, String.valueOf(userContext.getUserId()));
		Methods.defineDocumentIndex(document, Constants.FIELD_NAME_CODE, name);
		IDocument idoc = Methods.getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), document.getId())
		Methods.getAuditMgr().addDocumentEvent(userContext, idoc, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("groovy_copypaste_historisation") + iDocument.getAirsRefId());
		File exportPath = new File(ExportUtils.getExportPDFDirectory());
		if (!exportPath.exists()) exportPath.mkdirs();
		for (IAttachment attachment : iDocument.getAttachments(userContextAdmin)) {
			iDocument.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), ExportUtils.getExportPDFDirectory());
			PrimaryDocument primaryDoc = new PrimaryDocument(attachment.getFileName(), attachment.getFileName());
			document.addOrUpdatePrimaryDocument(primaryDoc, exportPath.getAbsolutePath());
		}

		if(Constants.USE_GROUP_LIST){
			Methods.defineDocumentIndex(document, Constants.LIST_GROUPES_DOCUMENT_CODE,Methods.getGroupeByTypeDoc(data.get("DATA_LIST_DOCUMENT_TYPE").toString()));
		}


		document.updateContent();
	} else {
		Domain domain = new Domain(userContext.getJeton(), Constants.CTY_DOCUMENT_ASSURE);
		document = new Document(userContext.getJeton(), domain, Constants.SECRET_LEVEL_DEFAULT);
		document.updateContent();

		// Recherche du nom - prénom
		String name = null;
		try {
			List<String> names = Methods.getRequestInWebAI(nss, "name");
			name = (names.isEmpty()) ? "" : names.get(0);
			if (name.length() > 25) name = name.substring(0, 25);
		}catch(Exception e){
			scriptLogger.warn("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ATTENTION :  ",e);
		}

		Methods.defineDocumentIndex(document, Constants.FIELD_NSS_CODE, nss);
		Methods.defineDocumentIndex(document, Constants.FIELD_DEM_CODE, data.get("DATA_DEM").toString());
		Methods.defineDocumentIndex(document, Constants.LIST_TYPES_DOCUMENT_CODE, data.get("DATA_LIST_DOCUMENT_TYPE").toString());
		Methods.defineDocumentIndex(document, Constants.FIELD_DATE_DOCUMENT_CODE, Methods.convertDateForAIRS(data.get("DATA_DATE").toString()));
		Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, data.get("DATA_COUNT_PAGES").toString());
		Methods.defineDocumentIndex(document, Constants.LIST_WK_STATUT_CODE, String.valueOf(Constants.LIST_WK_STATUT_ARCHIVE));
		Methods.defineDocumentIndex(document, Constants.FIELD_CREATEUR_CODE, String.valueOf(userContext.getUserId()));
		Methods.defineDocumentIndex(document, Constants.FIELD_NAME_CODE, name);

		IDocument idoc = Methods.getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), document.getId())
		Methods.getAuditMgr().addDocumentEvent(userContext, idoc, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("groovy_copypaste_historisation") + iDocument.getAirsRefId());
		File exportPath = new File(ExportUtils.getExportPDFDirectory());
		if (!exportPath.exists()) exportPath.mkdirs();
		for (IAttachment attachment : iDocument.getAttachments(userContext)) {
			iDocument.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), ExportUtils.getExportPDFDirectory());
			PrimaryDocument primaryDoc = new PrimaryDocument(attachment.getFileName(), attachment.getFileName());
			document.addOrUpdatePrimaryDocument(primaryDoc, exportPath.getAbsolutePath());
		}
		if(Constants.USE_GROUP_LIST){
			Methods.defineDocumentIndex(document, Constants.LIST_GROUPES_DOCUMENT_CODE,Methods.getGroupeByTypeDoc(data.get("DATA_LIST_DOCUMENT_TYPE").toString()));
		}
		document.updateContent();
	}

	if (document != null) {
		DocumentAction documentAction = new DocumentAction(userContext.getJeton(), Constants.ADV_EVENT_DOCCREATE);
		documentAction.setUsrId(userContext.getUserId());
		document.addAction(documentAction);
	}

	result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"));

	Utils.getSearchResultTableController().refreshAndKeepFilter();

}catch(Exception e){
	if(document != null){
		document.destroy();
	}
	scriptLogger.error("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewExec - ERREUR : ",e);
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
	return;
}

scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW EXEC - END");
