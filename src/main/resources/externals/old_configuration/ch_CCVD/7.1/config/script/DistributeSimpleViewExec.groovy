import Constants
import Methods
import com.akazi.flowmind.api.*
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.dossier.workflow.ConstantsWF
import com.digitech.dossier.workflow.model.IWFProcessModel
import com.digitech.dossier.workflow.model.IWFTaskModel
import com.digitech.dossier.workflow.service.IWFProcessService
import com.digitech.dossier.workflow.service.IWFSearchService
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Token

import java.text.SimpleDateFormat

/**************************************************************************************************
 *   					    Distribution des documents - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet aux responsables des organisations de distribuer les documents aux utilisateurs désirés
 et en spécifiant le statut du document
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTE SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
CustomActionController customActionController = null
Map<String, Object> data = new HashMap<String, Object>()
String errorDocuments = null
IWFProcessModel ProcModelGood = null
IWFProcessService wfProcMgr = null
IWFSearchService wfSearchMgr = null
List<IWFProcessModel> listProcModel = null
String user = null
String state = null
Date dueDate = null
Token jeton = null

try {
  result = output.getValue()
  result.setMessageSummary("ACTION DISTRIBUTION : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  jeton = UserContext.getInstance().getJeton()

  user = data.get("user")
  state = data.get("etat")
  dueDate = (Date) data.get("echeance")

  wfProcMgr = Methods.getWkfMgr()
  wfSearchMgr = Methods.getWFSearchService()
  listProcModel = wfProcMgr.getProcesses(UserContext.getInstance())

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTE SIMPLE VIEW EXEC - END")
    return
  }

  String sDueDate = null
  HashMap<Integer, String> profileLoaded = new HashMap<Integer, String>()
  Map<String, java.lang.Object> infosForWF = new HashMap<String, java.lang.Object>()
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  if(dueDate != null && data.get("acontroler") == "true") {
    SimpleDateFormat fout = new SimpleDateFormat("dd/MM/yyyy")
    sDueDate = fout.format(dueDate)
  }

  for(IWFProcessModel ProcModel : listProcModel) {
    if(Constants.AKAZI_NAME_PROCESS.equals(ProcModel.getName())) {
      ProcModelGood = ProcModel
      break
    }
  }

  String serviceValue = ""
  String responsableValue = ""

  int currentOrgId = UserContext.getInstance().getCurrentOrgId()
  switch( currentOrgId ) {
    case 1: //AC
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(10, profileLoaded, jeton)
      break

    case 8: //CI-CA
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(16, profileLoaded, jeton)
      break

    case 19: //PCI
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(47, profileLoaded, jeton)
      break

    case 20: //PSA 1
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(25, profileLoaded, jeton)
      break

    case 10: //EMP
      serviceValue = "CTE"
      responsableValue = Methods.getProfilLoad(18, profileLoaded, jeton)
      break

    case 14: //IM
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(20, profileLoaded, jeton)
      break

    case 3: //AF
      serviceValue = "AF"
      break

    case 4: //AF_PSA
      serviceValue = "AF PSA"
      break

    case 12: //FM
      serviceValue = "AF"
      break

    case 6: //AMF
      serviceValue = "AMF"
      break

    case 16: //PC
      serviceValue = "PC"
      responsableValue = Methods.getProfilLoad(22, profileLoaded, jeton)
      break

    case 18: //PCG
      serviceValue = "PC"
      responsableValue = Methods.getProfilLoad(24, profileLoaded, jeton)
      break

    case 17: //PCF
      serviceValue = "PC"
      responsableValue = Methods.getProfilLoad(23, profileLoaded, jeton)
      break

    case 22: //RENTES
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(15, profileLoaded, jeton)
      break

    case 23: //AI
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(15, profileLoaded, jeton)
      break

    case 24: //AVS
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(15, profileLoaded, jeton)
      break

    case 11: //ESTIMATIONS
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(15, profileLoaded, jeton)
      break

    case 28: //API
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(15, profileLoaded, jeton)
      break


    case 2: //ACC. BIL.
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(15, profileLoaded, jeton)
      break

    case 13: //IJAI
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(19, profileLoaded, jeton)
      break

    case 7: //APG
      serviceValue = "REN"
      responsableValue = Methods.getProfilLoad(13, profileLoaded, jeton)
      break

    case 21://REC
      serviceValue = "REC"
      responsableValue = Methods.getProfilLoad(26, profileLoaded, jeton)
      break

    case 15: //JU
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(21, profileLoaded, jeton)
      break

    case 25: //REV
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(27, profileLoaded, jeton)
      break

    case 31: //PSA 2
      serviceValue = "AC"
      responsableValue = Methods.getProfilLoad(25, profileLoaded, jeton)
      break

    default:
      break
  }


  for(IDocument doc : docs) {
    try {
      int docId = doc.getAirsRefId()
      Document innerDocument = doc.getAirsDocument().getInnerDocument()

      if(Constants.UNLOCK_TYPE.equals(doc.getLockType())) {

        if(Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(innerDocument.getDomain().getCode())) {
          Methods.defineDocumentIndex(innerDocument, Constants.FIELD_TAXING_USER_CODE, user)
          Methods.defineDocumentIndex(innerDocument, Constants.LIST_STATUS_CODE, String.valueOf(Constants.LIST_STATUS_ITEM_TO_TREAT_ID))
          doc.getAirsDocument().updateContents()

          //definition de l'historique
          String historic = "Document distribue à " + Methods.getUserMgr().getUser(Integer.parseInt(user)).getLogin()
          Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_WF_TASK_SUBMIT, historic)
        }
        else {
          // Si Workflow Finance ou état demande amarrée
          if(Constants.FLAG_WORKFLOW_FINANCE.toString().equalsIgnoreCase(doc.getField(Constants.FIELD_FILENAME).getValue().toString()) || Constants.LIST_STATUS_ITEM_REQUEST_MOORED_ID.toString().equalsIgnoreCase(doc.getField(Constants.LIST_STATUS_CODE).getValue().toString())) {
            // Definition du taxateur selectionne avec un id
            Methods.defineDocumentIndex(innerDocument, Constants.FIELD_TAXING_USER_CODE, user)
            // Définition du statut workflow
            Methods.defineDocumentIndex(innerDocument, Constants.LIST_STATUS_CODE, state)
            // Définition de l'echeance s'il y en a une
            if(sDueDate != null && !sDueDate.equalsIgnoreCase("")) {
              Methods.defineDocumentIndex(innerDocument, Constants.FIELD_DATE_DUE_CODE, sDueDate)
            }
            doc.getAirsDocument().updateContents()

          }
          else {
            //check des instances de workflow existantes sur le document
            List<IWFTaskModel> wfTasks = wfSearchMgr.getTasksFromAirsId(UserContext.getInstance(), docId, DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue())
            //si un workflow existe deja sur le document on passe au suivant et on le pr?cisera avec un message et on ne distribuera pas le document
            if(wfTasks.size() >= 1) {
              /*_scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewExec - DOC n°" + doc.getAirsRefId() + " - Un Workflow existe déjà.");
              */ try {
                BpmSession akaziSession = userContext.getWkUser().getAkaziSession()
                Filter filter = new Filter(ProcessInstance.class)
                // Cloture de l'instance si le document veut être redistribué
                filter.add(new CustomAttribute("AIRSID"), FilterOperator.EQ, docId.toString())
                akaziSession.stopProcessInstances(filter)
                _scriptLogger.info("[CUSTOM ACTION] - DistributeSimpleViewExec - Suprresssion instance workflow : DOC n°" + doc.getAirsRefId())
              } catch(Exception e) {
                if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString() + " (Workflow déjà existant)"
                else errorDocuments += ", " + doc.getAirsRefId().toString() + " (Workflow déjà existant)"
                _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewExec - Suprresssion instance workflow : DOC n°" + doc.getAirsRefId() + " : ", e)
                continue
              }
            }

            //definition du taxateur selectionne avec un id
            Methods.defineDocumentIndex(innerDocument, Constants.FIELD_TAXING_USER_CODE, user)

            if(state.equalsIgnoreCase(Constants.LIST_STATUS_ITEM_URGENT_ID.toString()) || state.equalsIgnoreCase(Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID.toString()) || state.equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString()) || state.equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_MOOR_ID.toString())) {
              Methods.defineDocumentIndex(innerDocument, Constants.LIST_STATUS_CODE, state)
            }
            else if(state.equalsIgnoreCase(Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString())) {
              Methods.defineDocumentIndex(innerDocument, Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString())
            }
            //on definit l'echeance s'il y en a une
            if(sDueDate != null && !sDueDate.equalsIgnoreCase("")) {
              Methods.defineDocumentIndex(innerDocument, Constants.FIELD_DATE_DUE_CODE, sDueDate)
            }

            //definition de organisation workflow
            Methods.defineDocumentIndex(innerDocument, Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, String.valueOf(currentOrgId))

            if(currentOrgId == 19 || currentOrgId == 20 || currentOrgId == 31) {
              if(responsableValue.equalsIgnoreCase(user)) {
                //si le taxateur est le responsable alors on d?finit l'etat "a valider" et non "a traiter"
                Methods.defineDocumentIndex(innerDocument, Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_VALID_ID.toString())
              }
            }

            doc.getAirsDocument().updateContents()

            //lancement d'un workflow
            String sdocID = "" + docId
            String sctyID = String.valueOf(doc.getDomain().getId())
            infosForWF.put("taxateur", "" + user)
            infosForWF.put("etat", "A traiter")
            //définition du service par rapport à l'organisation courante
            infosForWF.put("service", serviceValue)
            infosForWF.put("responsable", responsableValue)

            java.lang.Object source = ProcModelGood.getSource()
            com.akazi.flowmind.api.Process fmProcess = (com.akazi.flowmind.api.Process) source
            DataSet initialDataSet = fmProcess.getInitialDataSet()
            Methods.updateDataSet(initialDataSet, infosForWF)
            Map<String, String> customAttribute = new HashMap<String, String>()
            customAttribute.put(ConstantsWF.FM_CATR_AIRSID, sdocID)
            customAttribute.put(ConstantsWF.FM_CATR_CTYID, sctyID)
            fmProcess.start(initialDataSet, customAttribute)
            infosForWF.clear()

            //definition de l'historique
            String historic = "Document distribue à " + Methods.getUserMgr().getUser(Integer.parseInt(user)).getLogin()
            Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_FIELDCHANGE, historic)
          }
        }
      }
      else {
        _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewExec - DOC n°" + doc.getAirsRefId() + " - Document verrouillé.")
        if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString() + " (Document verrouillé)"
        else errorDocuments += ", " + doc.getAirsRefId().toString() + " (Document verrouillé)"
      }
    } catch(Exception e) {
      if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString() + " (Droits insuffisants)"
      else errorDocuments += ", " + doc.getAirsRefId().toString() + " (Droits insuffisants)"
      _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewExec - DOC n°" + doc.getAirsRefId() + " : ", e)
    }
  }

  Utils.getSearchResultController().getModel().replay()
  Utils.getAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

  if(errorDocuments != null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Les documents suivants :" + errorDocuments + " n'ont pu être distribués. Veuillez contacter votre administrateur.")
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail("INFORMATION - Les documents ont été distribués avec succès")
  }
} catch(Exception e) {
  _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewExec - ERREUR : ", e)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur")
}

_scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTE SIMPLE VIEW EXEC - END")