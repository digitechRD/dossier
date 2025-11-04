import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.workflow.model.IWFTaskModel
import com.digitech.dossier.workflow.model.impl.WFTask.WFActor

import javax.faces.model.SelectItem

/*************************************************************************************************
 * 							    Validation étape workflow Archivé - INIT
 **************************************************************************************************
 Date : 12.11.2014
 Auteur : MTO

 Description : Permet la validation du workflow en vue simple de la tâche Archivé
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW TO ARCHIVE SIMPLE VIEW INIT - START")

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
List<SelectItem> orgs = new ArrayList<SelectItem>()

// Variable locale
String TASKS_VALIDATION_NAME = "MutationCTE,TraiterCTE,TraiterAC,TraiterAF,TraiterPC,AValiderAC,TraiterREC,ArchiverPC,ArchiverREN,TraiterREN,TraiterAFPSA,TraiterAMF"

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
  _scriptLogger.error("[CUSTOM ACTION] - WorkflowToArchiveSimpleView - ERREUR : ", e)
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


  if(Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(docs.get(0).getDomain().getCode())) {
    IDocument currentDoc = docs.get(0)
    if(docs.size() > 1) {
      Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - Veuillez sélectionner un seul document", false)
      return
    }
    else if(String.valueOf(Constants.LIST_STATUS_ITEM_TO_TREAT_ID).equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString()) ||
        String.valueOf(Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID).equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString()) ||
        String.valueOf(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID).equalsIgnoreCase(currentDoc.getField(Constants.LIST_STATUS_CODE).getValue().toString())) {
      if(Integer.parseInt(currentDoc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString()) == userContext.getUser().getId()) {
        orgs.add(new SelectItem("", ""))
        orgs.add(new SelectItem(Constants.ORGANIZATION_PSA_1_ID, "PSA 1"))
        orgs.add(new SelectItem(Constants.ORGANIZATION_PSA_2_ID, "PSA 2"))
        orgs.add(new SelectItem(Constants.ORGANIZATION_PCI_ID, "PCI"))
        orgs.add(new SelectItem(Constants.ORGANIZATION_EMPLOYEURS_ID, "Employeurs"))
        if(currentDoc.getField(Constants.FIELD_AFF_CODE).getValue() != null)
          data.put("NIP", currentDoc.getField(Constants.FIELD_AFF_CODE).getValue().toString())
        outputs.put(currentDoc.getAirsRefId(), String.valueOf(Constants.LIST_STATUS_ITEM_ARCHIVE_ID))
        states.put(currentDoc.getAirsRefId(), "Le document peut être archivé")
        documents.put(currentDoc.getAirsRefId(), currentDoc)
      }
      else {
        Methods.addStateMessage(data, "DATA_WARN_MSG", "Vous n'êtes pas acteur de cette tâche", false)
      }
    }
    else {
      Methods.addStateMessage(data, "DATA_WARN_MSG", "Le document ne peut pas être archivé", false)
    }
  }
  else {
    for(IDocument currentDoc : docs) {
      documents.put(currentDoc.getAirsRefId(), currentDoc)
      if(Constants.DOC_LOCKED_BY_OTHER == currentDoc.getLockType()) states.put(currentDoc.getAirsRefId(), "Document vérouillé")
      else {
        // Pour chaque tache du document
        List<IWFTaskModel> wfTasks = Methods.getWFSearchService().getTasksFromAirsId((UserCoreContext) userContext, currentDoc.getAirsRefId(), DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue())

        // Si on est en AFFILITATION, qu'il n'y a pas de tâches workflow active pour le document et que l'état du document est "a distribuer"
        // On pourra archiver directement
        int valueStateWkf = 0
        try {
          valueStateWkf = currentDoc.getField(Constants.LIST_STATUS_CODE).getValue()
        } catch(Exception e) {
          states.put(currentDoc.getAirsRefId(), "Aucune instance workflow active et état non conforme pour archivage")
        }

        if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_AFFILIATION_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PCI_ID
            || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_1_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_2_ID
            || userContext.getCurrentOrgId() == Constants.ORGANIZATION_EMPLOYEURS_ID) {
          //Methods.defineDocumentIndex(currentDoc.getAirsDocument().getInnerDocument(), Constants.LIST_STATUS_CODE, Constants.);
          //currentDoc.getAirsDocument().updateContents();
          states.put(currentDoc.getAirsRefId(), "Le document peut être archivé directement")
          outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_ARCHIVE)
        }
        else if((((userContext.getCurrentOrgId() == Constants.ORGANIZATION_RFM_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PC_ID) && valueStateWkf == Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID) || userContext.getCurrentOrgId() == Constants.ORGANIZATION_AFFAIRES_FAMILIALES_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID) && Methods.isResponsableActor(userContext.getUser().getId())) {
          states.put(currentDoc.getAirsRefId(), "Le document peut être archivé directement")
          outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_ARCHIVE)
        }
        else {
          states.put(currentDoc.getAirsRefId(), "Aucune instance workflow activé")
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
                    outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_ARCHIVE)
                    states.put(currentDoc.getAirsRefId(), "Le document peut être archivé")
                    break
                  }
                  else if(userContext.getUser().getId().toString().equals(currentDoc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString())) {
                    // Soumission de la tache
                    tasks.put(currentDoc.getAirsRefId(), iwfTaskModel)
                    outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_ARCHIVE)
                    states.put(currentDoc.getAirsRefId(), "Le document peut être archivé")
                    break
                  }
                  else states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
                }
                else if(userContext.getUser().getId().toString().equals(currentDoc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString())) {
                  // Soumission de la tache
                  tasks.put(currentDoc.getAirsRefId(), iwfTaskModel)
                  outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_ARCHIVE)
                  states.put(currentDoc.getAirsRefId(), "Le document peut être archivé")
                  break
                }
                else states.put(currentDoc.getAirsRefId(), "Vous n'êtes pas acteur de cette tâche")
                /*else{
states.put(currentDoc.getAirsRefId(), "Le document peut être archive");
outputs.put(currentDoc.getAirsRefId(), Constants.AKAZI_OUTPUT_ARCHIVE);
}*/
              }
            }
            else states.put(currentDoc.getAirsRefId(), "Le document ne peut pas être archivé")
          }
        }
      }
    }
  }

  data.put("ORGS", orgs)
  data.put("COMM", "")
  data.put("ORGA_WKF", "")
  data.put("ORGS", orgs)
  data.put("ORGA_WKF", "")
  data.put("DOCUMENTS", documents)
  data.put("TASKS", tasks)
  data.put("OUTPUTS", outputs)
  data.put("STATES", states)
  data.put("LIST_DOC_ID", data.get("DOCUMENTS").keySet().toArray())
  data.put("MSG_NB_VALID_DOC", "Documents valides : " + new Integer(outputs.size()).toString() + "/" + new Integer(docs.size()).toString())

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - WorkflowToArchiveSimpleView - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - WORKFLOW TO ARCHIVE SIMPLE VIEW INIT - END")