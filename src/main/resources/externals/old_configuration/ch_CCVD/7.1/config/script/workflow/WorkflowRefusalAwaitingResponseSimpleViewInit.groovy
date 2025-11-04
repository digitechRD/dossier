import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.workflow.model.IWFTaskModel
import com.digitech.dossier.workflow.model.impl.WFTask.WFActor

/*************************************************************************************************
 * 				    Validation étape workflow Refusé en attente de réponse - INIT
 **************************************************************************************************
 Date : 12.11.2014
 Auteur : MTO

 Description : Permet la validation du workflow en vue simple de la tâche Refusé en attente de réponse
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW REFUSAL AWAITING RESPONSE SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
Map<Integer, IDocument> documents = null
Map<Integer, IWFTaskModel> tasks = null
Map<Integer, String> outputs = null
Map<Integer, String> states = null

// Variable locale
String TASKS_VALIDATION_NAME = "AValiderAC"

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

  documents = new HashMap<Integer, IDocument>()
  tasks = new HashMap<Integer, IWFTaskModel>()
  outputs = new HashMap<Integer, String>()
  states = new HashMap<Integer, String>()

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - WorkflowRefusalAwaitingResponseSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(docs.size() == 0) {
    Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - Aucun document sélectionné", false)
    return
  }

  for(IDocument currentDoc : docs) {
    documents.put(currentDoc.getAirsRefId(), currentDoc)
    if(Constants.DOC_LOCKED_BY_OTHER == currentDoc.getLockType()) states.put(currentDoc.getAirsRefId(), "Document vérouillé")
    else {
      List<IWFTaskModel> wfTasks = Methods.getWFSearchService().getTasksFromAirsId((UserCoreContext) userContext, currentDoc.getAirsRefId(), DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue())
      states.put(currentDoc.getAirsRefId(), "Aucune instance workflow active")
      for(IWFTaskModel iwfTaskModel : wfTasks) {
        // Si la tache appartient au taches de validation
        if(TASKS_VALIDATION_NAME.contains(iwfTaskModel.getName())) {
          // Pour chaque acteur de la tache
          for(WFActor actor : iwfTaskModel.getActors()) {
            // Si acteur Utilisateur ou acteur Organisation
            if(actor.getId().startsWith("airs") && tasks.get(currentDoc.getAirsRefId()) == null) {
              // Si acteur est l'utilisateur courant
              if(actor.getId().substring(5).equalsIgnoreCase(userContext.getUser().getId().toString())) {
                // Soumission de la tache
                tasks.put(currentDoc.getAirsRefId(), iwfTaskModel)
                outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_REFUSAL_AWAITING_RESPONSE)
                states.put(currentDoc.getAirsRefId(), "Le document peut etre validé pour le refus")
                break
              }
              else {
                states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
              }
            }
            else if(userContext.getCurrentOrgId().toString().equals(actor.getId()) && tasks.get(currentDoc.getAirsRefId()) == null) {
              // Soumission de la tache
              tasks.put(currentDoc.getAirsRefId(), iwfTaskModel)
              outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_REFUSAL_AWAITING_RESPONSE)
              states.put(currentDoc.getAirsRefId(), "Le document peut etre validé pour le refus")
              break
            }
            else {
              states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
            }
          }
        }
        else {
          states.put(currentDoc.getAirsRefId(), "Le document ne peut pas etre validé pour le refus")
        }
      }
    }
  }

  data.put("DOCUMENTS", documents)
  data.put("TASKS", tasks)
  data.put("OUTPUTS", outputs)
  data.put("STATES", states)
  data.put("LIST_DOC_ID", data.get("DOCUMENTS").keySet().toArray())
  data.put("MSG_NB_VALID_DOC", "Documents valides : " + new Integer(tasks.size()).toString() + "/" + new Integer(docs.size()).toString())

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - WorkflowRefusalAwaitingResponseSimpleViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW REFUSAL AWAITING RESPONSE SIMPLE VIEW INIT - END")