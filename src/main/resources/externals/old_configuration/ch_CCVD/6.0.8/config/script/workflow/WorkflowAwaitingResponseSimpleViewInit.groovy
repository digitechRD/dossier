import com.digitech.dossier.common.model.backend.*;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.workflow.model.IWFTaskModel;
import com.digitech.dossier.workflow.model.impl.WFTask.WFActor;

import java.util.*;

import Constants;
import Methods;

/*************************************************************************************************
 *							    Validation étape workflow En attente de réponse - INIT
 **************************************************************************************************
 Date : 12.11.2014
 Auteur : MTO

 Description : Permet la validation du workflow en vue simple de la tâche En attente de réponse
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW AWAITING RESPONSE SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
Map<Integer, IDocument> documents = null;
Map<Integer, IWFTaskModel> tasks = null;
Map<Integer, String> outputs = null;
Map<Integer, String> states = null;

// Variable locale
String TASKS_VALIDATION_NAME = "AValiderAC,TraiterPC,TraiterREC,TraiterCTE,ArchiveCTE";

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

    documents = new HashMap<Integer, IDocument>();
    tasks = new HashMap<Integer, IWFTaskModel>();
    outputs = new HashMap<Integer, String>();
    states = new HashMap<Integer, String>();

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - WorkflowAwaitingResponseSimpleViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if(docs.size() == 0){
        Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - Aucun document sélectionné", false);
        return;
    }

    for(IDocument currentDoc : docs) {
        documents.put(currentDoc.getAirsRefId(), currentDoc);
        if (Constants.DOC_LOCKED_BY_OTHER == currentDoc.getLockType()) states.put(currentDoc.getAirsRefId(), "Document vérouillé");
        else if(Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(currentDoc.getDomain().getCode())){
            if(String.valueOf(Constants.LIST_STATUS_ITEM_TO_TREAT_ID).equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString())){
                if(Integer.parseInt(currentDoc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString()) == userContext.getUser().getId()){
                    outputs.put(currentDoc.getAirsRefId(), Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID.toString());
                    states.put(currentDoc.getAirsRefId(), "Le document peut être mis en attente de réponse");
                }else {
                    states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche");
                }
            }else{
                states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être mis en attente de réponse");
            }
        } else {
            List<IWFTaskModel> wfTasks = Methods.getWFSearchService().getTasksFromAirsId((UserCoreContext) userContext, currentDoc.getAirsRefId(), DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue());
            states.put(currentDoc.getAirsRefId(), "Aucune instance workflow active");
            for (IWFTaskModel iwfTaskModel : wfTasks) {
                // Si la tache appartient au taches de validation
                if (TASKS_VALIDATION_NAME.contains(iwfTaskModel.getName())) {
                    // Pour chaque acteur de la tache
                    for (WFActor actor : iwfTaskModel.getActors()) {
                        // Si acteur Utilisateur ou acteur Organisation
                        if (actor.getId().startsWith("airs") && tasks.get(currentDoc.getAirsRefId()) == null) {
                            // Si acteur est l'utilisateur courant
                            if (actor.getId().substring(5).equalsIgnoreCase(userContext.getUser().getId().toString()) || Methods.isResponsableActor(userContext.getUser().getId())) {
                                // Soumission de la tache
                                tasks.put(currentDoc.getAirsRefId(), iwfTaskModel);
                                outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_AWAITING_RESPONSE);
                                states.put(currentDoc.getAirsRefId(), "Le document peut être mis en attente de réponse");
								break;
                            } else {
                                states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche");
                            }
                        } else if (userContext.getCurrentOrgId().toString().equals(actor.getId()) && tasks.get(currentDoc.getAirsRefId()) == null) {
                            // Soumission de la tache
                            tasks.put(currentDoc.getAirsRefId(), iwfTaskModel);
                            outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_AWAITING_RESPONSE);
                            states.put(currentDoc.getAirsRefId(), "Le document peut être mis en attente de réponse");
							break;
                        } else {
                            states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche");
                        }
                    }
                } else {
                    states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être mis en attente de réponse");
                }
            }
        }
    }

    data.put("DOCUMENTS", documents);
    data.put("TASKS", tasks);
    data.put("OUTPUTS", outputs);
    data.put("STATES", states);
    data.put("LIST_DOC_ID", data.get("DOCUMENTS").keySet().toArray());
    data.put("MSG_NB_VALID_DOC", "Documents valides : " + new Integer(tasks.size()).toString() + "/" + new Integer(docs.size()).toString());

} catch (Exception e) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - WorkflowAwaitingResponseSimpleViewInit - ERREUR : ", e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW AWAITING RESPONSE SIMPLE VIEW INIT - END");