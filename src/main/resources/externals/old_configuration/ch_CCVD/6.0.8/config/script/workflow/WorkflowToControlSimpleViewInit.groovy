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
 *							    Validation étape workflow A Contrôler - INIT
 **************************************************************************************************
 Date : 12.11.2014
 Auteur : MTO

 Description : Permet la validation du workflow en vue simple de la tâche A Controler
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW TO CONTROL SIMPLE VIEW INIT - START");

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
String TASKS_VALIDATION_NAME = "TraiterAC,TraiterPC,TraiterREN,TraiterCTE,AValiderAC,MutationCTE,ArchiveCTE";

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
    scriptLogger.error("[CUSTOM ACTION] - WorkflowToControlSimpleView - ERREUR : ",e);
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
            if(Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString())
            && currentDoc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString().equalsIgnoreCase(userContext.getUser().getId().toString()))
            {
                outputs.put(currentDoc.getAirsRefId(), Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString());
                states.put(currentDoc.getAirsRefId(), "Le document peut être envoyé pour contrôle");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, Constants.CCVD_DAY_NUMBER_DATE_DUE_2);
                data.put("DATE", calendar.getTime());
				scriptLogger.debug("Date par défaut :" +calendar.getTime());
            }else{
				Methods.addStateMessage(data, "DATA_WARN_MSG", "Le document ne peut pas être envoyé pour contrôle", false);
                outputs.put(currentDoc.getAirsRefId(), null);
                states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être envoyé pour contrôle");
            }

        }else if(Constants.FLAG_WORKFLOW_FINANCE.toString().equalsIgnoreCase(currentDoc.getField(Constants.FIELD_FILENAME).getValue().toString())){
			// Etape 3.1 du schéma
            if(Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString())) {
                outputs.put(currentDoc.getAirsRefId(), Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString());
                states.put(currentDoc.getAirsRefId(), "Le document peut être envoyé pour contrôle");
				//data.put("DATA_METIER_TO_FINANCE", false);
            }else{
				outputs.put(currentDoc.getAirsRefId(), null);
                states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être envoyé pour contrôle");
            }
        }else {
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
                            if (actor.getId().substring(5).equalsIgnoreCase(userContext.getUser().getId().toString())) {
                                // Soumission de la tache
                                tasks.put(currentDoc.getAirsRefId(), iwfTaskModel);
                                outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_TO_CONTROL);
                                states.put(currentDoc.getAirsRefId(), "Le document peut être envoyé pour contrôle");
								break;
                            } else {
                                states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche");
                            }
                        } else if (userContext.getCurrentOrgId().toString().equals(actor.getId()) && tasks.get(currentDoc.getAirsRefId()) == null) {
                            // Soumission de la tache
                            tasks.put(currentDoc.getAirsRefId(), iwfTaskModel);
                            outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_TO_CONTROL);
                            states.put(currentDoc.getAirsRefId(), "Le document peut être envoyé pour contrôle");
							break;
                        } else {
                            states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche");
                        }
                    }
                } else {
                    states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être envoyé pour contrôle");
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
    scriptLogger.error("[CUSTOM ACTION] - WorkflowToControlSimpleView - ERREUR : ", e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW TO CONTROL SIMPLE VIEW INIT - END");