import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController;
import com.digitech.dossier.common.service.*;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.workflow.model.IWFProcessModel;
import com.digitech.dossier.workflow.model.IWFTaskModel;
import com.digitech.dossier.workflow.service.IWFProcessService;
import com.digitech.dossier.workflow.ConstantsWF;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import com.digitech.jcorbairs.exception.XmlException;
import com.digitech.jcorbairs.admin.ProfilsManager;

import com.akazi.flowmind.api.*;

import java.util.*;

import Constants;
import Methods

/**************************************************************************************************
 *   					        Réinitialisation des workflows de type RENTES
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet de réinitialiser le workflow de type Rentes
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - REINITIALIZE WORKFLOW RENTE SIMPLE VIEW - START");

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null;
List<IDocument> docs = null;
String errorDocuments = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION DE REINITIALISATION DU WORKFLOW : ");

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

}catch(Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - ReinitializeWorkflowRenteSimpleView - ERREUR : ", e.localizedMessage);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try
{
    if(docs.size() == 0) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Aucun document n'a été selectionné");
        return;
    }

    for(IDocument doc : docs){
        try {
            String taxateur = null;
            List<IWFTaskModel> wfTasks = Methods.getWFSearchService().getTasksFromAirsId(UserContext.getInstance(), doc.getAirsRefId(), DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue());
            if(wfTasks.size() >= 1){
                if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
                else errorDocuments += ", "+doc.getAirsRefId().toString();
                scriptLogger.warn("[CUSTOM ACTION] - ReinitializeWorkflowRenteSimpleView - Attention workflow déjà existant (AIRSID : "+ doc.getAirsRefId());
            }else{
                taxateur = doc.getField(Constants.FIELD_TAXING_USER_CODE).getValue();
                if(taxateur != null && taxateur.equalsIgnoreCase(userContext.getUserId())){
                    Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_TREAT_ID);
                    doc.getAirsDocument().updateContents();

                    // Lancement d'un workflow
                    int docId = doc.getAirsRefId();
                    Map<String, Object> infosForWF = new HashMap<String, Object>();
                    String sdocID = "" + docId;
                    String sctyID = String.valueOf(doc.getDomain().getId());
                    infosForWF.put("taxateur", taxateur);
                    infosForWF.put("etat", "A traiter");
                    infosForWF.put("service", "REN");
                    infosForWF.put("responsable", ProfilsManager.load(UserContext.getInstance().getJeton(),19).getUsers().get(0).getId().toString());
                    IWFProcessService IWFprocSer = Methods.getWkfMgr();
                    List<IWFProcessModel> ListProcModel = IWFprocSer.getProcesses(UserContext.getInstance());
                    IWFProcessModel ProcModelGood = null;
                    for( IWFProcessModel ProcModel : ListProcModel){
                        if(ProcModel.getName().equals(Constants.AKAZI_NAME_PROCESS)){
                            ProcModelGood = ProcModel;
                            break;
                        }
                    }

                    if( ProcModelGood != null ){
                        Object source = ProcModelGood.getSource();
                        Process fmProcess = (Process)source;
                        DataSet initialDataSet = fmProcess.getInitialDataSet();
                        Methods.updateDataSet(initialDataSet, infosForWF);
                        Map<String, String> customAttribute = new HashMap<String, String>();
                        customAttribute.put(ConstantsWF.FM_CATR_AIRSID, sdocID);
                        customAttribute.put(ConstantsWF.FM_CATR_CTYID, sctyID);
                        fmProcess.start(initialDataSet, customAttribute);
                    }
                }else{
                    if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
                    else errorDocuments += ", "+doc.getAirsRefId().toString();
                    scriptLogger.warn("[CUSTOM ACTION] - ReinitializeWorkflowRenteSimpleView - Attention taxateur non défini ou différent de l'utilisateur ayant effectué l'action (AIRSID : "+ doc.getAirsRefId());
                }
            }

        }
        catch(XmlException e) {
            if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString();
            else errorDocuments += ", "+doc.getAirsRefId().toString();
            scriptLogger.error("[CUSTOM ACTION] - ReinitializeWorkflowRenteSimpleView - ERREUR lors de la réinitialisation (AIRSID : "+ dos.getAirsRefId() +") : ", e.localizedMessage);
        }
    }

    if(errorDocuments != null) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Les documents n° :" + errorDocuments +"n'ont pas eu leur workflow réinitialisé");
    }else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
        result.setMessageDetail("INFORMATION - L'ensemble des documents ont eu workflow réinitialisé avec succès");
    }

    Utils.getSearchResultTableController().getModel().clear();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);
}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - ReinitializeWorkflowRenteSimpleView - ERREUR : ",e);
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - La réinitialisation est impossible. Veuillez contacter votre administrateur");
}

scriptLogger.debug("[CUSTOM ACTION] - REINITIALIZE WORKFLOW RENTE SIMPLE VIEW - END");