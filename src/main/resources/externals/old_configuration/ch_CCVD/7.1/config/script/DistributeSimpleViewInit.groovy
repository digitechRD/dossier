import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument

import javax.faces.model.SelectItem
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

/*************************************************************************************************
 *   					    Distribution des documents - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet aux responsables des organisations de distribuer les documents aux utilisateurs désirés
 et en spécifiant le statut du document
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTE SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
Connection connection = null
PreparedStatement preparedStatement = null
ResultSet resultSet = null
List<SelectItem> items = new ArrayList<SelectItem>()
List<SelectItem> states = new ArrayList<SelectItem>()
List<IDocument> docs = null
IDocument doc = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  if(docs != null && docs.size() > 0) {
    doc = docs.get(0)
  }
  else {
    Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION : Veuillez sélectionner un document à distribuer", false)
    return
  }

  // Combobox States
  states.add(new SelectItem(0, "Choisir un statut"))
  states.add(new SelectItem(Constants.LIST_STATUS_ITEM_TO_TREAT_ID, "A traiter"))
  if(userContext.getCurrentOrgId() != Constants.ORGANIZATION_FINANCE_ID) states.add(new SelectItem(Constants.LIST_STATUS_ITEM_URGENT_ID, "Urgent"))
  else states.add(new SelectItem(Constants.LIST_STATUS_ITEM_TO_VALID_ID, "A valider"))

  _scriptLogger.debug("[CUSTOM ACTION] - DistributeSimpleViewInit : Organisation Courante : " + userContext.getCurrentOrgId())

  if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_PCI_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_AFFILIATION_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_1_ID
      || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_2_ID) {
    // Combobox States
    if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_1_ID || userContext.getCurrentOrgId() == Constants.ORGANIZATION_PSA_2_ID)
      states.add(new SelectItem(Constants.LIST_STATUS_ITEM_TO_CONTROL_ID, "A controler"))
    states.add(new SelectItem(Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID, "En attente de reponse"))
    Calendar calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, Constants.CCVD_DAY_NUMBER_DATE_DUE_1)
    data.put("echeance", calendar.getTime())
  }
  else {
    if(userContext.getCurrentOrgId() == Constants.ORGANIZATION_ALLOCATIONS_MATERNITES_FEDERALES_ID) states.add(new SelectItem(Constants.LIST_STATUS_ITEM_TO_MOOR_ID, "A amarrer"))
    data.put("echeance", null)
  }

  // Combobox Users
  Class.forName(Constants.DB_AIRS_DRIVER)
  connection = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, Constants.DB_AIRS_PASSWORD)

  if(userContext.getCurrentOrgId() != Constants.ORGANIZATION_AFFILIATION_ID || Constants.CTY_AVIS_MUTATION_DOCUMENT.equalsIgnoreCase(doc.getDomain().getCode())) {
    preparedStatement = connection.prepareStatement(Constants.DB_AIRS_REQUEST_GET_USERS_BY_ORGANIZATION)
    preparedStatement.setInt(1, userContext.getCurrentOrgId())
  }
  else {
    preparedStatement = connection.prepareStatement(Constants.DB_AIRS_REQUEST_GET_USERS_BY_SOME_ORGANIZATIONS)
    preparedStatement.setInt(1, userContext.getCurrentOrgId())
    preparedStatement.setInt(2, Constants.ORGANIZATION_EMPLOYEURS_ID)
    preparedStatement.setInt(3, Constants.ORGANIZATION_PCI_ID)
    preparedStatement.setInt(4, Constants.ORGANIZATION_PSA_1_ID)
    preparedStatement.setInt(4, Constants.ORGANIZATION_PSA_2_ID)
  }

  resultSet = preparedStatement.executeQuery()

  items.add(new SelectItem(0, "Choisir un utilisateur"))
  while(resultSet.next()) {
    items.add(new SelectItem(resultSet.getInt(1), resultSet.getString(2)))
  }

  data.put("users", items)
  if(Constants.CTY_AFFILIATED_DOCUMENT.equalsIgnoreCase(doc.getDomain().getCode())) {
    data.put("etats", states)
  }
  data.put("acontroler", false)

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement", false)
  _scriptLogger.error("[CUSTOM ACTION] - DistributeSimpleViewInit - ERREUR : ", e)
  return
} finally {
  if(resultSet != null) resultSet.close()
  if(preparedStatement != null) preparedStatement.close()
  if(connection != null) connection.close()
}

_scriptLogger.debug("[CUSTOM ACTION] - DISTRIBUTE SIMPLE VIEW INIT - END")