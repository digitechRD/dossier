import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

import javax.faces.model.SelectItem

/*************************************************************************************************
 *   					Réaffectation du document à un autre service - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Définit ou redéfinit l’utilisateur étant le taxateur du document.
 Définition des listes de services et des organisations workflow en fonction de l'organisation courante
 Lorsque la redirection se fait depuis une organisation PC, Rentes, ou Affiliation/Cotisation celle-ci se fait dans le service alors que le scan peut renvoyer à tous les services
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION SERVICE SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<SelectItem> items = new ArrayList<SelectItem>()
List<SelectItem> itemsOrganizationWkf = new ArrayList<SelectItem>()

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  items.add(new SelectItem(0, "Choisir un service"))
  itemsOrganizationWkf.add(new SelectItem(0, "Choisir une organisation workflow"))
  int organizationId = userContext.getCurrentOrgId()

  _scriptLogger.debug("[CUSTOM ACTION] - ReallocationServiceSimpleViewInit - DEBUG - ORGANISATION COURANTE : " + organizationId.toString())

  if(organizationId == Constants.ORGANIZATION_PC_ID || organizationId == Constants.ORGANIZATION_PCF_ID || organizationId == Constants.ORGANIZATION_RFM_ID) {
    AuthorityListTermAdmin altaPC = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Constants.LIST_SERVICE_ITEM_PCC_ID)
    //String code = altaPC.getCode();
    //items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_PCC_ID, code));

    for(AuthorityListTermAdmin altaFils : altaPC.loadChildren()) {
      String codeFils = altaFils.getCode()
      int idFils = altaFils.getId()
      items.add(new SelectItem(idFils, codeFils))
    }

    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFILIATION_ID, "AC"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_CI_CA_ID, "-- CI-CA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_EMPLOYEURS_ID, "-- CTE"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IM_ID, "-- IM"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PCI_ID, "-- PCI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_1_ID, "-- PSA"))
    //itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_2_ID, "-- PSA 2"));
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_ID, "AF"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID, "AF PSA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID, "AMF"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RECOUVREMENT_ID, "REC"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_1_ID, "RENTES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ACCORDS_BI_ID, "-- ACCORDS BILATERAUX"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_APG_ID, "-- APG MILITAIRES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IJAI_ID, "-- IJAI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_2_ID, "RENTES AI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_4_ID, "RENTES API"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_3_ID, "RENTES AVS"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ESTIMATIONS_ID, "-- ESTIMATIONS"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_REVISION_ID, "REV"))

    // On definit par defaut les organisations non visibles
    data.put("orgaVisible", true)

  }
  else if(organizationId == Constants.ORGANIZATION_RENTES_1_ID || organizationId == Constants.ORGANIZATION_RENTES_2_ID || organizationId == Constants.ORGANIZATION_RENTES_3_ID || organizationId == Constants.ORGANIZATION_RENTES_4_ID) {
    // Pour ce service la il faudra egalement definir l'organisation workflow de distribution
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ACCORDS_BI_ID, "ACCORDS BILATERAUX"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_APG_ID, "APG MILITAIRES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IJAI_ID, "IJAI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ESTIMATIONS_ID, "ESTIMATIONS"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_1_ID, "RENTES 1"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_2_ID, "RENTES 2"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_3_ID, "RENTES 3"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_4_ID, "RENTES 4"))

    // On definit par defaut les organisations visibles
    data.put("orgaVisible", true)
  }
  else if(
  organizationId == Constants.ORGANIZATION_ESTIMATIONS_ID || organizationId == Constants.ORGANIZATION_ACCORDS_BI_ID || organizationId == Constants.ORGANIZATION_IJAI_ID ||
      organizationId == Constants.ORGANIZATION_APG_ID) {

    AuthorityListTermAdmin altaREN = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Constants.LIST_SERVICE_ITEM_RENTES_ID)
    String code = altaREN.getCode()
    items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_RENTES_ID, code))

    for(AuthorityListTermAdmin altaFils : altaREN.loadChildren()) {
      String codeFils = altaFils.getCode()
      int idFils = altaFils.getId()
      items.add(new SelectItem(idFils, "-- " + codeFils))
    }

    // Pour ce service la il faudra egalement definir l'organisation workflow de distribution
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_1_ID, "RENTES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ACCORDS_BI_ID, "-- ACCORDS BILATERAUX"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_APG_ID, "-- APG MILITAIRES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IJAI_ID, "-- IJAI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_2_ID, "RENTES AI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_4_ID, "RENTES API"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_3_ID, "RENTES AVS"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ESTIMATIONS_ID, "-- ESTIMATIONS"))

    // On definit par defaut les organisations visibles
    data.put("orgaVisible", true)

  }
  else if(organizationId == Constants.ORGANIZATION_AFFILIATION_ID || organizationId == Constants.ORGANIZATION_CI_CA_ID || organizationId == Constants.ORGANIZATION_PCI_ID ||
      organizationId == Constants.ORGANIZATION_EMPLOYEURS_ID ||
      organizationId == Constants.ORGANIZATION_REVISION_ID || organizationId == Constants.ORGANIZATION_IM_ID) {
    AuthorityListTermAdmin altaAC = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Constants.LIST_SERVICE_ITEM_AC_ID)
    String code = altaAC.getCode()
    items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_AC_ID, code))

    //pour ce service la il faudra egalement definir l'organisation workflow de distribution
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFILIATION_ID, "AC"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_CI_CA_ID, "-- CI-CA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PCI_ID, "-- PCI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_EMPLOYEURS_ID, "-- CTE"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_1_ID, "-- PSA"))
    //itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_2_ID, "-- PSA 2"));
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IM_ID, "-- IM"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_JURIDIQUE_ID, "JU"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_REVISION_ID, "REV"))

    // On definit par defaut les organisations visibles
    data.put("orgaVisible", true)

  }
  else if(organizationId == Constants.ORGANIZATION_AFFAIRES_FAMILIALES_ID || organizationId == Constants.ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID) {
    items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_AF_ID, "AF"))
    items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_AF_PSA_ID, "AF PSA"))

    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_ID, "AF"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID, "AF PSA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_JURIDIQUE_ID, "JU"))

    // On definit par defaut les organisations visibles
    data.put("orgaVisible", true)

  }
  else if(organizationId == Constants.ORGANIZATION_SCAN_ID || organizationId == Constants.ORGANIZATION_PSA_1_ID || organizationId == Constants.ORGANIZATION_PSA_2_ID ||
      organizationId == Constants.ORGANIZATION_JURIDIQUE_ID || organizationId == Constants.ORGANIZATION_RECOUVREMENT_ID) {
    List<AuthorityListTermAdmin> listValues = AuthorityListsManager.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_SERVICE_ID)
    for(AuthorityListTermAdmin alta : listValues) {
      String code = alta.getValue1()
      int id = alta.getId()
      if(id != Constants.LIST_SERVICE_ITEM_PCC_ID) items.add(new SelectItem(id, code))
      else {
        items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_PC_ID, "PC"))
        items.add(new SelectItem(Constants.LIST_SERVICE_ITEM_PCF_ID, "PCF"))
      }

      /*for (AuthorityListTermAdmin altaFils : alta.loadChildren())
      {
          String codeFils = altaFils.getCode();
          int idFils = altaFils.getId();
  if(idFils != Constants.LIST_SERVICE_ITEM_AF_ID && idFils == Constants.LIST_SERVICE_ITEM_AF_PSA_ID)
    items.add(new SelectItem(idFils, "-- " + codeFils));
      }*/
    }


    // Pour ce service la il faudra egalement definir l'organisation workflow de distribution
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFILIATION_ID, "AC"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_CI_CA_ID, "-- CI-CA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_EMPLOYEURS_ID, "-- CTE"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IM_ID, "-- IM"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PCI_ID, "-- PCI"))
    if(organizationId == Constants.ORGANIZATION_PSA_2_ID) itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_1_ID, "-- PSA 1"))
    else if(organizationId == Constants.ORGANIZATION_PSA_1_ID) itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_2_ID, "-- PSA 2"))
    else itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PSA_1_ID, "-- PSA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_ID, "AF"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_AFFAIRES_FAMILIALES_PSA_ID, "AF PSA"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID, "AMF"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RECOUVREMENT_ID, "REC"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_JURIDIQUE_ID, "JU"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_PC_ID, "PC"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_1_ID, "RENTES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ACCORDS_BI_ID, "-- ACCORDS BILATERAUX"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_APG_ID, "-- APG MILITAIRES"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_IJAI_ID, "-- IJAI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_2_ID, "RENTES AI"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_4_ID, "RENTES API"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RENTES_3_ID, "RENTES AVS"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_REVISION_ID, "REV"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_RFM_ID, "RFM"))
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_ESTIMATIONS_ID, "-- ESTIMATIONS"))


    // On definit par defaut les organisations non visibles
    data.put("orgaVisible", true)
  }
  else if(organizationId == Constants.ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID) {
    itemsOrganizationWkf.add(new SelectItem(Constants.ORGANIZATION_JURIDIQUE_ID, "JU"))
    // On definit par defaut les organisations non visibles
    data.put("orgaVisible", true)
  }
  else Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - La redirection n'est pas possible depuis cette organisation", false)

  data.put("services", items)
  data.put("organisations", itemsOrganizationWkf)

  // On definit les valeurs par defaut sur lesquelles la jsp va s'initialiser
  data.put("service", 0)
  data.put("orgaWkf", 0)

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - REALLOCATION SERVICE SIMPLE VIEW INIT - END")
