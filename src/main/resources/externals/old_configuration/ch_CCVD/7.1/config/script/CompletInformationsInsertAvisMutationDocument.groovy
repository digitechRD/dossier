import Methods
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/*************************************************************************************************
 * 				Completement d'information lors de la création d'un document avis de mutation
 **************************************************************************************************
 Date : 14.06.2018
 Auteur : MTO

 Description : Permet de récupérer des informations provenant de la central lors de la création d’un document avis de mutation depuis la GED.
 Toutes les informations sont retrouvés grâce au numéro d'avis de mutation.
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - COMPLET INFORMATIONS INSERT AVIS MUTATION DOCUMENT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
Connection connection = null
PreparedStatement preparedStatement = null
ResultSet resultSet = null
Document doc = null

try {
  result = output.getValue()
  result.setMessageSummary("CREATION DOCUMENT AVIS DE MUTATION : ")

  doc = Methods.getDocumentMgr().getDocument(userContext.getJeton(), document.getAirsRefId()).getAirsDocument().getInnerDocument()

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'initialisation du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - CompletInformationsInsertAffiliedDocument - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  /*Class.forName(Constants.DB_GLOBAZ_DRIVER);
  connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD);

  preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_NSS_BY_NAFF);
  preparedStatement.setString(1, document.getField(Constants.FIELD_AFF_CODE).getValue());
  resultSet = preparedStatement.executeQuery();
  if(resultSet.next()){
      if(resultSet.getString(1) != null)
          Methods.defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, resultSet.getString(1).trim().replaceAll("\\.", ""));
      else {
          Methods.getDocumentMgr().deleteDocument(DossierCoreContext.getAdminJeton(), document.getAirsRefId());
          result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
          result.setMessageDetail("ERREUR - Le N°AFF n'existe pas. Le document a été supprimé. Veuillez contacter votre administrateur");
          _scriptLogger.error("[CUSTOM ACTION] - CompletInformationsInsertAffiliedDocument - ERREUR : Le N°AFF n'existe pas. Le document a été supprimé");
          return;
      }
  }

  preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_IDENTITY_BY_NAFF);
  preparedStatement.setString(1, document.getField(Constants.FIELD_AFF_CODE).getValue());
  resultSet = preparedStatement.executeQuery();
  if(resultSet.next()){
      // Définition du titre
      if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MR) Methods.defineDocumentIndex(doc, Constants.LIST_TITLE_CODE, Constants.LIST_TITLE_ITEM_MR_ID.toString());
      else if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MME) Methods.defineDocumentIndex(doc, Constants.LIST_TITLE_CODE, Constants.LIST_TITLE_ITEM_MME_ID.toString());

      // Définition du nom et prénom
      Methods.defineDocumentIndex(doc, Constants.FIELD_LASTNAME_AFF_CODE, resultSet.getString(2));
      Methods.defineDocumentIndex(doc, Constants.FIELD_FIRSTNAME_AFF_CODE, resultSet.getString(3));
  }

  // Définition des adresses
  preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF);
  preparedStatement.setString(1, document.getField(Constants.FIELD_AFF_CODE).getValue());
  resultSet = preparedStatement.executeQuery();
  String adress = "";
  if(resultSet.next()){
      if("".equals(adress)) adress = resultSet.getString(1);
  else adress += " ; "+resultSet.getString(1);
  }
  Methods.defineDocumentIndex(doc, Constants.FIELD_ADRESS_AFF_CODE, adress);

  // Définition de l'utilisateur courant comme taxateur
  Methods.defineDocumentIndex(doc, Constants.FIELD_TAXING_USER_CODE, userContext.getUserId().toString());

  // Définition du statut par archivé
  Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());

  // Définition de la catégorie
  AuthorityListTermAdmin autorityListTermAdminType = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), document.getField(Constants.LIST_TYPE_CODE).getValue());
  List<AuthorityListTermAdmin> autorityListTermAdminCategory = AuthorityListsManager.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_CATEGORY_ID);
  for(AuthorityListTermAdmin alta : autorityListTermAdminCategory) {
      if(alta.getCode().equalsIgnoreCase(autorityListTermAdminType.getSortKey())) {
          Methods.defineDocumentIndex(doc, Constants.LIST_CATEGORY_CODE, alta.getId().toString());
          break;
      }
  }

  doc.updateContent();

  // Rattachement au dossier père
  Document dossier = Methods.getDossier(document, Constants.CTY_AFFILIATED_FOLDER, Constants.FIELD_AFF_CODE, document.getField(Constants.FIELD_AFF_CODE).getValue().toString(), Constants.SECRET_LEVEL_DEFAULT);
  if(dossier != null){
  doc.addParent(dossier);
  result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
      result.setMessageDetail("INFORMATION - Le document a été créé avec succés");

  } else {
      result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
      result.setMessageDetail("ATTENTION - Le document a été synchronisé avec la base des tiers sans être lié au dossier");
  }

  userContext.getCurrentDocument().reload();*/

} catch(Exception e) {
  Methods.getDocumentMgr().deleteDocument(userContext, document.getAirsRefId())
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - La récupération des informations depuis la centrale est impossible. Le document a été supprimé. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - CreateReminderViewExec - ERREUR : ", e)
  return
} finally {
  if(resultSet != null) resultSet.close()
  if(preparedStatement != null) preparedStatement.close()
  if(connection != null) connection.close()
}

_scriptLogger.debug("[CUSTOM ACTION] - COMPLET INFORMATIONS INSERT AVIS MUTATION DOCUMENT - END")
