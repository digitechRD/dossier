import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.jcorbairs.Document;
import com.digitech.jcorbairs.Note;

import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer

import java.text.SimpleDateFormat

/*************************************************************************************************
 *							    Validation étape workflow - EXEC
 **************************************************************************************************
 Date : 12.11.2014
 Auteur : MTO

 Description : Permet la validation du workflow en vue simple
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION WORKFLOW : ");

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - WorkflowSimpleViewExec - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW SIMPLE VIEW EXEC - END");
        return
    }

	Document doc = null;
	String historic = null;
    for (Integer docId : data.get("OUTPUTS").keySet()) {
		historic = null;
	    IDocument document = data.get("DOCUMENTS").get(docId);
		doc = new Document(userContext.getJeton(), docId);
		if(Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(doc.getDomain().getCode())){
			if(data.get("OUTPUTS").get(docId) != null) {
				if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID.toString())){
					Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(data.get("OUTPUTS").get(docId)));
					historic = "Document mis en attente de traitement";
				}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString())) {
					Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(Constants.LIST_STATUS_ITEM_ARCHIVE_ID));
					if (document.getField(Constants.FIELD_NAVISMUTATION_CODE) != null && document.getField(Constants.FIELD_NAVISMUTATION_CODE).getValue() != null) {
						String error = Methods.synchronizeAvisMutation(document);
						if (error != null) {
							result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
							result.setMessageDetail(error);
							scriptLogger.debug("[CUSTOM ACTION] - WorkflowSimpleViewExec - ERROR : NIP : "+data.get("NIP").toString()+" / Service : "+data.get("ORGA_WKF").toString())
							return;
						}
					} /*else {
						result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
						result.setMessageDetail("ATTENTION - Le numéro d'avis de mutation ne peut être vide");
						return;
					}*/

					scriptLogger.debug("[CUSTOM ACTION] - WorkflowSimpleViewExec - DEBUG : NIP : "+data.get("NIP").toString()+" / Service : "+data.get("ORGA_WKF").toString())

					// NIP vide et Service vide alors on archive
					if(data.get("NIP").toString().trim().isEmpty() && data.get("ORGA_WKF") == null){
						//Methods.createAffiliateDocument(document, Constants.ORGANIZATION_SCAN_ID, null, false, null);
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(data.get("OUTPUTS").get(docId)));
						Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_DUE_CODE, null);
						//Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, "Document tranmis au service SCAN pour NIP à créer");
						Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, "Document archivé");

					// NIP vide et Service non vide alors envoi au scan avec commentaire
					}else if(data.get("NIP").toString().trim().isEmpty() && data.get("ORGA_WKF") != null) {
						String comment = null;
						if(data.get("COMM") != null && !data.get("COMM").toString().equalsIgnoreCase("")) comment = data.get("COMM").toString();
						Methods.createAffiliateDocument(document, Integer.parseInt(data.get("ORGA_WKF").toString()), data.get("NIP").toString(), true, comment);
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(data.get("OUTPUTS").get(docId)));
						Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_DUE_CODE, null);
						Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, "Document tranmis au service");

					// NIP non vide et Service non vide alors on envoi au service
					}else if(!data.get("NIP").toString().trim().isEmpty() && data.get("ORGA_WKF") != null){
						String comment = null;
						if(data.get("COMM") != null && !data.get("COMM").toString().equalsIgnoreCase("")) comment = data.get("COMM").toString();
						Methods.createAffiliateDocument(document, Integer.parseInt(data.get("ORGA_WKF").toString()), data.get("NIP").toString(), false, comment);
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(data.get("OUTPUTS").get(docId)));
						Methods.defineDocumentIndex(doc, Constants.FIELD_AFF_CODE, data.get("NIP").toString());
						Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_DUE_CODE, null);
						Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, "Document tranmis au service");

					// NIP non vide et Service vide alors on archive sans envoi
					}else if(!data.get("NIP").toString().trim().isEmpty() && data.get("ORGA_WKF") == null){
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(data.get("OUTPUTS").get(docId)));
						Methods.defineDocumentIndex(doc, Constants.FIELD_AFF_CODE, data.get("NIP").toString());
						Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_DUE_CODE, null);
						Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, "Document archivé");
					}else {
						result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
						result.setMessageDetail("ATTENTION - Problème lors de la lecture des informations saisies");
						return;
					}
				}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString())) {
					if(data.get("DATE") != null && !"".equalsIgnoreCase(data.get("DATE").toString())){
						SimpleDateFormat sdf=new SimpleDateFormat(Constants.DATE_FORMAT);
						Date d = data.get("DATE");
						String formatDate = "";
						if(d != null){
							Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, String.valueOf(data.get("OUTPUTS").get(docId)));
							formatDate = sdf.format(d);
							Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_DUE_CODE, formatDate);
						}else{
							result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
							result.setMessageDetail("ATTENTION - Veuillez saisir une date");
							return;
						}
					}else{
						result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
						result.setMessageDetail("ATTENTION - Veuillez saisir une date");
						return;
					}
				}
				doc.updateContent();
			}
		}
        else if(Constants.FLAG_WORKFLOW_FINANCE.toString().equalsIgnoreCase(document.getField(Constants.FIELD_FILENAME).getValue().toString())) {
			if(data.get("OUTPUTS").get(docId) != null){
				doc = new Document(userContext.getJeton(), docId);
				if(userContext.getCurrentOrgId() != Constants.ORGANIZATION_FINANCE_ID){
					// Etape 2.1 du schéma (Voir documentation projet)
					if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString()) &&
						data.get("DATA_METIER_TO_FINANCE")){
						Methods.defineDocumentIndex(doc, Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, Constants.ORGANIZATION_FINANCE_ID.toString());
						//doc.setSecretLevel(Integer.parseInt(Constants.MAP_SERVICE_SECRET_LEVEL.get(Constants.LIST_SERVICE_ITEM_FIN_ID)));
						Methods.defineDocumentIndex(doc, Constants.FIELD_AL_ORGA_WKF_OLD_CODE, userContext.getCurrentOrgId().toString());
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						if(data.get("DATA_CONTROLE_FINANCE_VALUE")) Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						else Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString());
						historic = "Document validé";
					// Etape 2.2 du schéma
					}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString())){
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						historic = "Document refusé";
					// Etape 3.1 du schéma
					}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString()) &&
						!data.get("DATA_METIER_TO_FINANCE")){
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						historic = "Document envoyé pour contrôle";
					}


				}else if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_FINANCE_ID){
					// Etape 6 du schéma

					if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString())){
						// Converti le document XLS --> PDF si necessaire
						IAttachment attachment = (IAttachment)document.getAttachments().get(0);
						File downloadFolderPath = new File(com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory().replaceAll(Constants.PATH_APPLICATION_DOWNLOAD, Constants.PATH_APPLICATION_DOWNLOAD_LINK));
						document.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), downloadFolderPath.getParent());
						File fileOffice = new File(downloadFolderPath.getParent()+"/"+attachment.getFileName());
						if(fileOffice.getName().toUpperCase().contains(Constants.APPLICATION_EXCEL_EXTENSION) || fileOffice.getName().toUpperCase().contains(Constants.APPLICATION_WORD_EXTENSION)){
							scriptLogger.debug("FICHIER PDF : "+downloadFolderPath.getParent()+"/"+Methods.getPDFFileName(fileOffice.getName()));
							Methods.convertAttachment(document, fileOffice);
							Methods.defineDocumentIndex(doc, Constants.FIELD_STATUS_WORKFLOW_CODE, Constants.VALUE_CONVERT_PDF);
							document.getAirsDocument().deletePrimaryDoc(attachment.getAirsAttachment());
						}
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						historic = "Document archivé";
					// Etape 4.1 du schéma
					}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_VALID_ID.toString())){
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						historic = "Document contrôlé"
					// Etape 4.2 du schéma
					}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString()) &&
						!data.get("DATA_IS_REJECTED")){
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						historic = "Document validé"
					// Etape 5.1 et 5.2 du schéma
					}else if(data.get("OUTPUTS").get(docId).toString().equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString()) &&
						data.get("DATA_IS_REJECTED")){
						Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, data.get("OUTPUTS").get(docId).toString());
						Methods.defineDocumentIndex(doc, Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, document.getField(Constants.FIELD_AL_ORGA_WKF_OLD_CODE).getValue().toString());
						//doc.setSecretLevel(Integer.parseInt(Constants.MAP_SERVICE_SECRET_LEVEL.get(Constants.LIST_SERVICE_ITEM_PC_ID)));
						historic = "Document refusé"
					}
				}

				Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, historic);
				if(data.get("commentaire") != null && !data.get("commentaire").equalsIgnoreCase("")){
					Note myNote = new Note(Constants.AIRS_NOTE_ID);
					myNote.setText(data.get("commentaire").toString());
					myNote.setPublic();
					doc.addNote(myNote);
				}
				doc.updateContent();
			}
        }else {
            try{
				Methods.submitTask(data.get("TASKS").get(docId), data.get("OUTPUTS").get(docId), document, data.get("GROOVYNAME"));
			}catch(Exception e){
				scriptLogger.warn("[CUSTOM ACTION] - WorkflowSimpleViewExec - ERREUR SubmitTask: ",e);
			}
			if(data.get("commentaire") != null && !data.get("commentaire").equalsIgnoreCase("")){
				Note myNote = new Note(Constants.AIRS_NOTE_ID);
				myNote.setText(data.get("commentaire").toString());
				myNote.setPublic();
				document.getAirsDocument().getInnerDocument().addNote(myNote);
			}
            if (userContext.getCurrentOrgId() == Constants.ORGANIZATION_PCI_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_1_ID
			|| userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_2_ID) {
                Date d = (Date) data.get("echeance");
                if (d != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
                    String formatedDate = sdf.format(d);
                    document.getField(Constants.FIELD_DATE_DUE_CODE).setValue(formatedDate);
					document.getAirsDocument().updateContents();
                }
            }
			if(data.get("STATES").get(docId).toString().contains("directement")) {
				Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, "Document archivé directement");
				Methods.defineDocumentIndex(document.getAirsDocument().getInnerDocument(), Constants.FIELD_STATUS_WORKFLOW_CODE, null);
				document.getAirsDocument().getInnerDocument().updateContent();
			}
        }

		scriptLogger.debug("[CUSTOM ACTION] - WorkflowSimpleViewExec - DEBUG : "+ document.getField(Constants.LIST_STATUS_CODE).getValue().toString()+" ["+document.getAirsRefId()+"]");
    }
	

    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
    result.setMessageDetail("INFORMATION - L'opération a été effectuée avec succès");

    Utils.getSearchResultController().getModel().replay();
    Utils.getAttachmentController().getModel().refreshDocument();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

}
catch(Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - La validation de cette étape du processus est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - WorkflowSimpleViewExec - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW SIMPLE VIEW EXEC - END");
