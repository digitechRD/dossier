import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.dossier.workflow.model.IWFTaskModel
import com.digitech.dossier.workflow.service.IWFProcessService
import com.digitech.dossier.workflow.service.IWFSearchService
import com.digitech.jcorbairs.admin.AuthorityListsManager
import com.digitech.jcorbairs.admin.OrganizationsManager

/*************************************************************************************************
 *   					Réaffectation du document à un autre service - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Définit ou redéfinit l’utilisateur étant le taxateur du document.
 Définition des listes de services et des organisations workflow en fonction de l'organisation courante
 Lorsque la redirection se fait depuis une organisation PC, Rentes, ou Affiliation/Cotisation celle-ci se fait dans le service alors que le scan peut renvoyer à tous les services
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION SERVICE SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
CustomActionController customActionController = null
Map<String, Object> data = new HashMap<String, Object>()
IWFProcessService wfProcMgr = null
IWFSearchService wfSearchMgr = null
String errorDocuments = null
IRight rightMgr = null


try {
  result = output.getValue()
  result.setMessageSummary("ACTION REDIRECTION SERVICE : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - ERREUR : ", e.localizedMessage)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION SERVICE SIMPLE VIEW EXEC - END")
    return
  }

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

  wfProcMgr = Methods.getWkfMgr()
  wfSearchMgr = Methods.getWFSearchService()

  Integer serviceId = null
  String group = null
  String orgaWkf = data.get("orgaWkf")

  if(orgaWkf.equals("0")) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Veuillez choisir une organisation")
    return
  }

  _scriptLogger.debug("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DEBUG - ORGANISATION WORKFLOW : " + orgaWkf)
  rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR)

  switch( orgaWkf ) {
    case String.valueOf(Constants.ORGANIZATION_AFFILIATION_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AF_ID
      break
    case String.valueOf(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AF_PSA_ID
      break
    case String.valueOf(Constants.ORGANIZATION_EMPLOYEURS_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_PSA_1_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_PSA_2_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_PCI_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_CI_CA_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_IM_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_RECOUVREMENT_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_RECOUVREMENT_ID
      break
    case String.valueOf(Constants.ORGANIZATION_RENTES_2_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_RENTES_AI_ID
      break
    case String.valueOf(Constants.ORGANIZATION_IJAI_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_IJAI_ID
      break
    case String.valueOf(Constants.ORGANIZATION_ESTIMATIONS_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_RENTES_ID
      break
    case String.valueOf(Constants.ORGANIZATION_RENTES_4_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_API_AI_ID
      break
    case String.valueOf(Constants.ORGANIZATION_RENTES_3_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_RENTES_AVS_ID
      break
    case String.valueOf(Constants.ORGANIZATION_RENTES_1_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_RENTES_ID
      break
    case String.valueOf(Constants.ORGANIZATION_ACCORDS_BI_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_RENTES_ID
      break
    case String.valueOf(Constants.ORGANIZATION_APG_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_APG_ID
      break
    case String.valueOf(Constants.ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_AMF_ID
      break
    case String.valueOf(Constants.ORGANIZATION_PC_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_PC_ID
      break
    case String.valueOf(Constants.ORGANIZATION_FINANCE_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_FIN_ID
      break
    case String.valueOf(Constants.ORGANIZATION_JURIDIQUE_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_JU_ID
      break
    case String.valueOf(Constants.ORGANIZATION_REVISION_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_REV_ID
      break
    case String.valueOf(Constants.ORGANIZATION_PCF_ID):
      serviceId = Constants.LIST_SERVICE_ITEM_PCF_ID
      break
    default:
      break
  }

  String iSecretLevel = Constants.MAP_SERVICE_SECRET_LEVEL.get(serviceId)

  for(IDocument doc : docs) {
    try {
      //_scriptLogger.debug("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DEBUG - Taille de la map : "+Constants.MAP_SERVICE_SECRET_LEVEL.size()+ " - Test : "+Constants.MAP_SERVICE_SECRET_LEVEL.get(30));

      if(iSecretLevel == null) {
        _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DOC n°" + doc.getAirsRefId() + " - Erreur lors de la récupération du niveau de secret : Service : " + serviceId.toString() + ", Niveau de secret : " + iSecretLevel)
        if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString() + " (Niveau de secret inconnu)"
        else errorDocuments += ", " + doc.getAirsRefId().toString() + " (Niveau de secret inconnu)"
      }
      else if(!rightMgr.isAuthorizedToEditDocument(userContext, doc)) {
        _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DOC n°" + document.getAirsRefId() + " - Droit de modification non autorisé")
        if(errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (Droit insuffisant)"
        else errorDocuments += ", " + document.getAirsRefId().toString() + " (Droit insuffisant)"
      }
      else {
        // On ne met pas à jour le service pour un transfert inter-Rentes ou pour un transfert vers le service juridique
        if(!orgaWkf.equalsIgnoreCase(String.valueOf(Constants.ORGANIZATION_RENTES_1_ID)) && !orgaWkf.equalsIgnoreCase(String.valueOf(Constants.ORGANIZATION_RENTES_2_ID))
            && !orgaWkf.equalsIgnoreCase(String.valueOf(Constants.ORGANIZATION_RENTES_3_ID)) && !orgaWkf.equalsIgnoreCase(String.valueOf(Constants.ORGANIZATION_RENTES_4_ID))
            && !orgaWkf.equalsIgnoreCase(String.valueOf(Constants.ORGANIZATION_JURIDIQUE_ID))) {
          Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.LIST_SERVICE_CODE, String.valueOf(serviceId))
        }
        Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID.toString())
        Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, orgaWkf)

        if((serviceId == Constants.LIST_SERVICE_ITEM_RENTES_AI_ID || serviceId == Constants.LIST_SERVICE_ITEM_RENTES_ID || serviceId == Constants.LIST_SERVICE_ITEM_RENTES_AVS_ID || serviceId == Constants.LIST_SERVICE_ITEM_API_AI_ID) && userContext.getCurrentOrgId() != Constants.ORGANIZATION_RENTES_1_ID && userContext.getCurrentOrgId() != Constants.ORGANIZATION_RENTES_2_ID && userContext.getCurrentOrgId() != Constants.ORGANIZATION_RENTES_3_ID && userContext.getCurrentOrgId() != Constants.ORGANIZATION_RENTES_4_ID) {
          String orga = Methods.getGroupDistributionForRentes(String.valueOf(doc.getField(Constants.FIELD_LASTNAME_AFF_CODE).getValue()))
          _scriptLogger.debug("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DOC n°" + doc.getAirsRefId() + " / Oraganisation : " + orga + " / NOM : " + doc.getField(Constants.FIELD_LASTNAME_AFF_CODE).getValue())
          Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, Methods.getGroupDistributionForRentes(String.valueOf(doc.getField(Constants.FIELD_LASTNAME_AFF_CODE).getValue())))
        }//else if(serviceId == Constants.LIST_SERVICE_ITEM_AC_ID && (userContext.getCurrentOrgId() != Constants.ORGANIZATION_PSA_1_ID && userContext.getCurrentOrgId() != Constants.ORGANIZATION_PSA_2_ID)){
        else if((String.valueOf(Constants.ORGANIZATION_PSA_1_ID).equalsIgnoreCase(orgaWkf) || String.valueOf(Constants.ORGANIZATION_PSA_2_ID).equalsIgnoreCase(orgaWkf)) && (userContext.getCurrentOrgId() != Constants.ORGANIZATION_PSA_1_ID && userContext.getCurrentOrgId() != Constants.ORGANIZATION_PSA_2_ID)) {
          String orga = Methods.getGroupDistributionForPSA(String.valueOf(doc.getField(Constants.FIELD_LASTNAME_AFF_CODE).getValue()))
          _scriptLogger.debug("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DOC n°" + doc.getAirsRefId() + " / Oraganisation : " + orga + " / NOM : " + doc.getField(Constants.FIELD_LASTNAME_AFF_CODE).getValue())
          Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, Methods.getGroupDistributionForPSA(String.valueOf(doc.getField(Constants.FIELD_LASTNAME_AFF_CODE).getValue())))
        }

        String toDistributeLabel = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID).getValue1()
        Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_STATUS_WORKFLOW_CODE, toDistributeLabel)
        Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_TAXING_USER_CODE, null)
        doc.getAirsDocument().setSecretLevel(Integer.parseInt(iSecretLevel))
        doc.getAirsDocument().getInnerDocument().updateContent()

        // Check des instances de workflow existantes sur le document qu'on supprime toutes
        List<IWFTaskModel> wfTasks = wfSearchMgr.getTasksFromAirsId(UserContext.getInstance(), doc.getAirsRefId(), DossierCoreContext.getParamsInfos().isWfActorFilter().booleanValue())
        for(IWFTaskModel taskmodel : wfTasks) {
          wfProcMgr.deleteProcess(UserContext.getInstance(), taskmodel)
        }

        // Définition de l'historique
        //String historic = "Document envoyé par le service " + AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), userContext.getCurrentOrgId()).getValue();
        String historic = "Document envoyé par le service " + OrganizationsManager.load(DossierCoreContext.getAdminJeton(), userContext.getCurrentOrgId()).getDescription()
        Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_FIELDCHANGE, historic)

        //doc.getAirsDocument().updateContents();
      }
    }
    catch(Exception e) {
      _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DOC n°" + doc.getAirsRefId() + " - ERREUR : ", e)
      if(errorDocuments == null) errorDocuments = doc.getAirsRefId().toString()
      else errorDocuments += ", " + doc.getAirsRefId().toString()
    }
  }

  Utils.getSearchResultController().replay()
  Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

  if(errorDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail("INFORMATION - Document(s) redirigé(s) avec succès.")
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail("ATTENTION - Les documents suivants n'ont pas été redirigés : " + errorDocuments + ". Veuillez contacter votre administrateur")
  }
} catch(Exception e) {
  _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - ERREUR : ", e)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur")
}

_scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION SERVICE SIMPLE VIEW EXEC - END")