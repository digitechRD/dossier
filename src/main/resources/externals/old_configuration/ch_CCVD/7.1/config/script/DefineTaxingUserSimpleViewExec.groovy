import Constants
import Methods
import com.akazi.flowmind.api.*
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer

/*************************************************************************************************
 * 								Définition du taxateur - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Définit ou redéfinit l’utilisateur étant le taxateur du document.
 Et met à jour l'acteur dans l'instance workflow
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE TAXING USER SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
String lockedDocuments = null
CustomActionController customActionController = null
Map<String, Object> data = new HashMap<String, Object>()

try {
  result = output.getValue()
  result.setMessageSummary("ACTION DEFINITION TAXATEUR : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DefineTaxingUserSimpleViewExec - ERREUR : ", e.localizedMessage)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - DEFINE TAXING USER SIMPLE VIEW EXEC - END")
    return
  }
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

  for(IDocument doc : docs) {
    if(Constants.UNLOCK_TYPE.equals(doc.getLockType())) {
      // On change ensuite l'acteur de la tâche courante du document par le nouveau taxateur selectionné
      boolean isDone = false
      String historic = ""
      if(!Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(doc.getDomain().getCode())) {
        isDone = modifyTaskActorForTaxingUser((List<String>) Constants.AKAZI_TASKS_TO_TREAT, doc, data.get("user").toString(), userContext)
        historic = "Nouveau taxateur défini : " + Methods.getUserMgr().getUser(Integer.valueOf(data.get("user").toString())).getName()
      }
      else {
        result.setMessageSummary("ACTION DEFINITION GESTIONNAIRE : ")
        historic = "Nouveau gestionnaire défini : " + Methods.getUserMgr().getUser(Integer.valueOf(data.get("user").toString())).getName()
        isDone = true
      }

      if(isDone) {
        Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_TAXING_USER_CODE, data.get("user").toString())
        doc.getAirsDocument().updateContents()
        Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_FIELDCHANGE, historic)
        //doc.getAirsDocument().updateContents();
      }
      else {
        if(lockedDocuments == null) lockedDocuments = doc.getAirsRefId().toString()
        else lockedDocuments += ", " + doc.getAirsRefId().toString()
      }

    }
    else {
      if(lockedDocuments == null) lockedDocuments = doc.getAirsRefId().toString()
      else lockedDocuments += ", " + doc.getAirsRefId().toString()
    }
  }

  Utils.getSearchResultController().replay()
  Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

  if(lockedDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    if(!Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(docs.get(0).getDomain().getCode())) {
      result.setMessageDetail("INFORMATION - Affectation du nouveau taxateur effectuée sur tous les documents avec succès.")
    }
    else {
      result.setMessageDetail("INFORMATION - Affectation du nouveau gestionnaire effectuée sur tous les documents avec succès.")
    }
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Les documents suivants n'ont pas été mis à jour car ils sont bloqués ou l'attribution n'a pu être effectuée :" + lockedDocuments + ".")
  }
}
catch(Exception e) {
  _scriptLogger.error("[CUSTOM ACTION] - DefineTaxingUserSimpleViewExec - ERREUR : ", e)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur")
}

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE TAXING USER SIMPLE VIEW EXEC - END")

private boolean modifyTaskActorForTaxingUser(List<String> tasksList, IDocument doc, String usrId, UserContext userContext) throws Exception {
  BpmSession akaziSession = userContext.getWkUser().getAkaziSession()
  Filter processFilter = null
  BpmCollection processInstanceCollection = null
  List<String> processInstanceList = new ArrayList()
  processFilter = new Filter(ProcessInstance.class)

  processFilter.add(new CustomAttribute("AIRSID"), FilterOperator.EQ, doc.getAirsRefId().toString())
  processInstanceCollection = akaziSession.selectProcessInstances(processFilter, SortCriteria.RANDOM)

  Iterator it

  if(processInstanceCollection != null) {
    for(it = processInstanceCollection.iterator(); it.hasNext();) {
      ProcessInstance processI = (ProcessInstance) it.next()
      Id processID = processI.getProcess().getId()
      processInstanceList.add(processID.toString())
    }
  }
  else {
    _scriptLogger.error("[CUSTOM ACTION] - DefineTaxingUserSimpleViewExec - ERREUR processInstanceCollection")
    return false
  }

  if(processInstanceList.size() == 0) {
    _scriptLogger.error("[CUSTOM ACTION] - DefineTaxingUserSimpleViewExec - ERREUR processInstanceCollection : " + processInstanceList.size())
    return false
  }

  //Récupération des tâches
  Filter filterTask = new Filter(Task.class)
  filterTask.add(Task.FilterOn.PROCESS, FilterOperator.EQ, processInstanceList.get(0))
  filterTask.add(Task.FilterOn.STATE, FilterOperator.EQ, Task.States.ACTIVE)
  filterTask.add(Task.FilterOn.ACTORS, FilterOperator.CONTAINS, "airs\$" + doc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString())


  BpmCollection tasks = akaziSession.selectTasks(filterTask, SortCriteria.RANDOM)
  if(tasks.isEmpty()) {
    _scriptLogger.error("[CUSTOM ACTION] - DefineTaxingUserSimpleViewExec - ERREUR tasksCollection : " + tasks.size())
    return false
  }

  //Reassignation de la tâche
  Iterator<Task> taskIte = tasks.iterator()
  while(taskIte.hasNext()) {
    Task task = taskIte.next()
    //Dans le cas d'une tache attribuee au taxateur uniquement on va juste definir le taxateur comme acteur
    if(tasksList.contains(task.getLabel()) && task.getLabel().equals("MutationCTE") && task.getProcessInstance().getCustomAttributeValue("AIRSID").equals(doc.getAirsRefId().toString())) {
      Actor actor = (Actor) task.getActors().get(1)
      String[] userId = new String[1]
      userId[0] = "AIRS\$" + usrId
      task.reassign(actor.getId().toString(), userId)
      task.refresh()
      actor = (Actor) task.getActors().get(1)

      task.getProcessInstance().suspend()
      DataSet ds = task.getProcessInstance().getRootStep().getDataSet()
      ds.set("taxateur", usrId)
      task.getProcessInstance().getRootStep().save(ds)
      task.getProcessInstance().resume()
      return true
    }
    else if(tasksList.contains(task.getLabel()) && task.getProcessInstance().getCustomAttributeValue("AIRSID").equals(doc.getAirsRefId().toString())) {
      Actor actor = (Actor) task.getActors().get(0)
      String[] userId = new String[1]
      userId[0] = "AIRS\$" + usrId
      task.reassign(actor.getId().toString(), userId)
      task.refresh()
      actor = (Actor) task.getActors().get(0)

      task.getProcessInstance().suspend()
      DataSet ds = task.getProcessInstance().getRootStep().getDataSet()
      ds.set("taxateur", usrId)
      task.getProcessInstance().getRootStep().save(ds)
      task.getProcessInstance().resume()
      return true
    }
    else {
      _scriptLogger.warn("[CUSTOM ACTION] - DefineTaxingUserSimpleViewExec - ERREUR processInstanceCollection : Task non trouvé : " + tasksList.toString() + " - " + task.getLabel() + " - " + task.getProcessInstance().getCustomAttributeValue("AIRSID") + " - " + tasks.size())
      //return false;
    }
  }
  return false
}
