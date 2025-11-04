import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.jcorbairs.Note;
import com.digitech.jcorbairs.admin.AuthorityListsManager;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.workflow.model.IWFTaskModel;
import com.digitech.dossier.workflow.service.IWFSearchService;
import com.digitech.dossier.workflow.service.IWFProcessService;
import com.digitech.dossier.common.controller.NavigationController;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.IScriptResultValueModel;

import com.digitech.jcorbairs.admin.OrganizationsManager;
import com.digitech.jcorbairs.admin.AuthorityListsManager;

import java.util.*;

import Constants;
import Methods;

/*************************************************************************************************
 *							Renvoie du document au service scan - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet le renvoi du document au service Scan
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION TO SCAN SERVICE SIMPLE VIEW - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
IWFProcessService wfProcMgr = null;
IWFSearchService wfSearchMgr = null;
String errorDocuments = null;
List<IDocument> docs = null;
IRight rightMgr = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION RETOUR AU SCAN : ");

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'initialisation du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - RetourToScanServiceSimpleView - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if(docs.size() == 0){
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Aucun document n'a été sélectionné");
        return;
    }

    wfProcMgr = Methods.getWkfMgr();
    wfSearchMgr = Methods.getWFSearchService();
    rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);

    for(IDocument doc : docs)
    {
        try {

            if(!rightMgr.isAuthorizedToEditDocument(userContext, doc)){
                scriptLogger.error("[CUSTOM ACTION] - RetourToScanServiceSimpleView - DOC n°"+doc.getAirsRefId()+" - Droit de modification non autorisé");
                if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString()+" (Droit insuffisant)";
                else errorDocuments += ", "+doc.getAirsRefId().toString()+" (Droit insuffisant)";
            }else{
                Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.LIST_SERVICE_CODE, Constants.LIST_SERVICE_ITEM_SCAN_ID.toString());
                Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID.toString());
                Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, Constants.ORGANIZATION_SCAN_ID.toString());

                //check des instances de workflow existantes sur le document qu'on supprime toutes
                List<IWFTaskModel> wfTasks = wfSearchMgr.getTasksFromAirsId(userContext, doc.getAirsRefId(), DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue());
                for (IWFTaskModel taskmodel : wfTasks) {
                    wfProcMgr.deleteProcess(UserContext.getInstance(), taskmodel);
                }

                //ajout commentaire
                Note myNote = new Note(1);
                int service = (int) doc.getField(Constants.LIST_SERVICE_CODE).getValue();
                myNote.setText("Document renvoyé au scan par le service " + OrganizationsManager.load(DossierCoreContext.getAdminJeton(), userContext.getCurrentOrgId()).getDescription());
                myNote.setPublic();
                doc.getAirsDocument().getInnerDocument().addNote(myNote);

                //definition de l'historique
                String historic = "Document renvoyé au scan par " + userContext.getUser().getLogin();
                Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_FIELDCHANGE, historic);

                doc.getAirsDocument().updateContents();
            }
        }catch(Exception e){
            scriptLogger.error("[CUSTOM ACTION] - RetourToScanServiceSimpleView - DOC n°"+doc.getAirsRefId()+" - ERREUR : ",e);
            if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString()+" (Erreur de traitement)";
            else errorDocuments += ", "+doc.getAirsRefId().toString()+" (Erreur de traitement)";
        }
    }
	
	Utils.getSearchResultController().replay();
    Utils.getSimpleViewAttachmentController().getModel().refreshDocument();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

    if(errorDocuments == null){
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
        result.setMessageDetail("INFORMATION - Document(s) redirigé(s) avec succès.");
    }else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
        result.setMessageDetail("ATTENTION - Les documents suivants n'ont pas été redirigés : "+ errorDocuments +". Veuillez contacter votre administrateur");
    }
}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - RetourToScanServiceSimpleView - ERREUR : ",e);
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur");
}

scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION TO SCAN SERVICE SIMPLE VIEW - END");