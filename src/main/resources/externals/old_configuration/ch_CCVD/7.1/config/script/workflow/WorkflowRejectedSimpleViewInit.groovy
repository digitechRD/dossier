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
 * 							    Validation étape workflow Rejeté - INIT
 **************************************************************************************************
 Date : 12.11.2014
 Auteur : MTO

 Description : Permet la validation du workflow en vue simple de la tâche Rejeté
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW REJECTED SIMPLE VIEW INIT - START")

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
String TASKS_VALIDATION_NAME = "TraiterAC,AValiderAC,MutationCTE,AControlerPC,AControlerREN"

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
  _scriptLogger.error("[CUSTOM ACTION] - WorkflowRejectedSimpleView - ERREUR : ", e)
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
    else if(Constants.FLAG_WORKFLOW_FINANCE.toString().equalsIgnoreCase(currentDoc.getField(Constants.FIELD_FILENAME).getValue().toString())) {
      _scriptLogger.debug(userContext.getCurrentOrgId().toString())
      if(userContext.getCurrentOrgId() != Constants.ORGANIZATION_FINANCE_ID) {
        // Etape 2.2 du schéma
        if(Methods.isValidActor(userContext.getUser().getId(), userContext.getCurrentOrgId()) && Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString())) {
          outputs.put(currentDoc.getAirsRefId(), Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString())
          states.put(currentDoc.getAirsRefId(), "Le document peut être refusé")
          data.put("DATA_IS_REJECTED", true)
        }
        else {
          outputs.put(currentDoc.getAirsRefId(), null)
          states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être rejeté")
        }
      }
      else if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_FINANCE_ID) {
        // Etape 5.1 ou 5.2 du schéma
        //if(((Methods.isActorInProfil(userContext.getUser().getId(), Constants.PROFIL_RESPONSABLE_FINANCE_ID) || Methods.isActorInProfil(userContext.getUser().getId(), Constants.PROFIL_RESPONSABLE_DIRECTEUR_ID))) && (Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString()) || Constants.LIST_STATUS_ITEM_TO_VALID_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString()))){
        if(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString()) || Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString().equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString())) {
          outputs.put(currentDoc.getAirsRefId(), Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString())
          states.put(currentDoc.getAirsRefId(), "Le document peut être rejeté")
          data.put("DATA_IS_REJECTED", true)
          data.put("commentaire", "")
        }
        else {
          outputs.put(currentDoc.getAirsRefId(), Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString())
          states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être rejeté")
        }
      }
      else {
        outputs.put(currentDoc.getAirsRefId(), null)
        states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
      }
    }
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
              if(actor.getId().substring(5).equalsIgnoreCase(userContext.getUser().getId().toString()) || Methods.isResponsableActor(userContext.getUser().getId())) {
                // Soumission de la tache
                tasks.put(currentDoc.getAirsRefId(), iwfTaskModel)
                outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_REJECTED)
                states.put(currentDoc.getAirsRefId(), "Le document peut être rejeté")
                break
              }
              else {
                states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
              }
            }
            else if(userContext.getCurrentOrgId().toString().equals(actor.getId()) && tasks.get(currentDoc.getAirsRefId()) == null) {
              // Soumission de la tache
              tasks.put(currentDoc.getAirsRefId(), iwfTaskModel)
              outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_REJECTED)
              states.put(currentDoc.getAirsRefId(), "Le document peut être rejeté")
              break
            }
            else {
              states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
            }
          }
        }
        else {
          states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être rejeté")
        }
      }
    }
  }

  data.put("DOCUMENTS", documents)
  data.put("TASKS", tasks)
  data.put("OUTPUTS", outputs)
  data.put("STATES", states)
  data.put("LIST_DOC_ID", data.get("DOCUMENTS").keySet().toArray())
  data.put("MSG_NB_VALID_DOC", "Documents valides : " + new Integer(outputs.size()).toString() + "/" + new Integer(docs.size()).toString())

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - WorkflowRejectedSimpleView - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW REJECTED SIMPLE VIEW INIT - END")