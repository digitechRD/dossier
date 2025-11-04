import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import java.util.*;

import Constants;
import Methods;

/*************************************************************************************************
 *								Suppression des pièces jointes de format WORD
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la suppression des pièces jointes au format WORD de l'ensemble des documents sélectionnés
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DELETE WORD FILES SIMPLE VIEW - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
List<IDocument> docs = null;
String errorDocuments = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION SUPPRESSION PIECES JOINTES AU FORMAT WORD : ");

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DeleteWordFilesSimpleView - ERREUR : ",e);
    return;
}


/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    for(IDocument doc:docs){
        // Si le document est deverrouille on regarde l'extension de sa piece jointe et si on en est le taxateur et si il s'agit d'un .doc on le supprime avec le jeton d'admin
        if(Constants.UNLOCK_TYPE.equals(doc.getLockType())){
            /*if(doc.getField(Constants.FIELD_TAXING_USER_CODE).getValues() == null){
                if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
                else errorDocuments += ", "+doc.getAirsRefId().toString();
            }else if(userContext.getUserId() == doc.getField(Constants.FIELD_TAXING_USER_CODE).getValue()){*/
				if(doc.getAttachments().size() > 0){
					String fileExtension = doc.getAttachments().get(0).getFileName().substring(doc.getAttachments().get(0).getFileName().lastIndexOf('.'));
					if(fileExtension.toUpperCase().equalsIgnoreCase(Constants.APPLICATION_WORD_EXTENSION)){
						Methods.getDocumentMgr().deleteDocument(new UserCoreContext(DossierCoreContext.getAdminJeton()), doc.getAirsRefId());
						//Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_FIELDCHANGE, "Suppression de la pièce jointe Word n°"+doc.getAttachments().get(0).getId());
						//doc.getAirsDocument().updateContents();
					}
				}else{
					if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
					else errorDocuments += ", "+doc.getAirsRefId().toString();
				}
            /*}
            /*else{
                if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
                else errorDocuments += ", "+doc.getAirsRefId().toString();
            }*/
        }
        else{
            if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
            else errorDocuments += ", "+doc.getAirsRefId().toString();
        }
    }

    Utils.getSearchResultController().replay();
    Utils.getSimpleViewAttachmentController().getModel().refreshDocument();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

    if(errorDocuments == null){
	 result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
        result.setMessageSummary("INFORMATION - Les pièces jointes des documents ont été supprimées avec succès");
    }else if(errorDocuments != null && errorDocuments.indexOf(",") != -1){
	 result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
        result.setMessageSummary("ATTENTION - Les pièces jointes des documents :"+ errorDocuments +"n'ont pas été supprimées car le document est bloqué");
    }else{
        result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
        result.setMessageSummary("ATTENTION - La pièce jointe du document :"+ errorDocuments +"n'a pas été supprimée car le document est bloqué");
    }
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - La suppression des pièces jointes est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DeleteWordFilesSimpleView - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - DELETE WORD FILES SIMPLE VIEW - END");