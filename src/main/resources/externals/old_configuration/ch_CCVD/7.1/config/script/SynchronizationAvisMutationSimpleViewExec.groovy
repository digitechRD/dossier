import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

/*************************************************************************************************
 * 				Completement d'information lors de la création d'un document affilié
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet de récupérer des informations provenant de Web@AI lors de la création d’un document affilié depuis la GED.
 Toutes les informations sont retrouvés grâce au numéro affilié.
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
Connection connection = null
PreparedStatement preparedStatement = null
ResultSet resultSet = null
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
String errorDocuments = null
String nom1 = null
String nom2 = null
String refCaisseAVS = null
String categoryId = null
String numIde = null
String nss = null
String codeFormeJuridique = null
String numAvisMutation = null
String codeMotifOFAS = null

try {
  result = output.getValue()
  result.setMessageSummary("SYNCHONISATION DOCUMENT AVIS MUTATION : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  numAvisMutation = data.get("AVIS_MUTATION").toString()
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - SynchronizationAvisMutationSimpleViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW EXEC - END")
    return
  }
  else if("--".equalsIgnoreCase(numAvisMutation) || "".equalsIgnoreCase(numAvisMutation)) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Veuillez saisir un numéro d'avis de mutation")
    _scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW EXEC - END")
    return
  }

  Class.forName(Constants.DB_GLOBAZ_DRIVER)
  connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD)

  preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_INFORMATIONS_BY_NAVISMUTATION)
  preparedStatement.setString(1, numAvisMutation)
  resultSet = preparedStatement.executeQuery()
  int countRow = 0
  while(resultSet.next()) {
    ++countRow
    nom1 = resultSet.getString(2)
    nom2 = resultSet.getString(3)
    refCaisseAVS = resultSet.getString(4)
    numIde = resultSet.getString(5)
    codeFormeJuridique = Methods.getAlTermValue(resultSet.getString(6).trim(), Constants.LIST_FORME_JURIDIQUE_ID)
    nss = resultSet.getString(7).trim().replaceAll("\\.", "")
    //codeMotifOFAS = Methods.getAlTermValue(resultSet.getString(8).trim(), Constants.LIST_CODE_MOTIF_OFAS_ID);
  }

  if(countRow == 0) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail("ERREUR - Le numéro d'avis de mutation suivant n'est pas valide : " + numAvisMutation)
    _scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW EXEC - ERROR : Le numéro d'avis de mutation suivant n'est pas valide : " + numAvisMutation)
    return
  }
  else if(countRow > 1) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Le numéro d'avis de mutation : " + numAvisMutation + " retourne plusieurs résultats et ne peut donc pas être synchronisé le document.")
    return
  }

  if(nss == null || refCaisseAVS == null || nss == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Toutes les informations n'ont pas été retrouvées dans la base WebAVS pour le numéro d'avis de mutation : " + numAvisMutation + ". Veuillez contacter votre administrateur.")
    return
  }

  for(IDocument document : docs) {
    Document doc = Methods.getDocumentMgr().getDocument(userContext.getJeton(), document.getAirsRefId()).getAirsDocument().getInnerDocument()
    Methods.defineDocumentIndex(doc, Constants.FIELD_NAVISMUTATION_CODE, numAvisMutation)
    Methods.defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, nss)
    Methods.defineDocumentIndex(doc, Constants.FIELD_LASTNAME_AFF_CODE, nom1)
    Methods.defineDocumentIndex(doc, Constants.FIELD_FIRSTNAME_AFF_CODE, nom2)
    Methods.defineDocumentIndex(doc, Constants.FIELD_REF_CAISSE_AVS_CODE, refCaisseAVS)
    Methods.defineDocumentIndex(doc, Constants.FIELD_NIDE_CODE, numIde)
    //Methods.defineDocumentIndex(doc, Constants.LIST_CODE_MOTIF_OFAS_CODE, codeMotifOFAS);
    Methods.defineDocumentIndex(doc, Constants.LIST_FORME_JURIDIQUE_CODE, codeFormeJuridique)

    doc.updateContent()

    // Recherche du dossier parent
    Document dossier = Methods.getDossier(document, Constants.CTY_AFFILIATED_FOLDER, Constants.FIELD_AFF_CODE, document.getField(Constants.FIELD_AFF_CODE).getValue().toString(), Constants.SECRET_LEVEL_DEFAULT)
    // Rattachement au dossier père
    if(dossier != null) {
      doc.addParent(dossier)
      doc.updateContent()
    }
    else {
      if(errorDocuments == null) errorDocuments = doc.getId().toString()
      else errorDocuments += ", " + doc.getId().toString()
    }
  }

  if(errorDocuments != null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Les documents n° :" + errorDocuments + " ont été synchronisé avec la base des tiers sans être liés au dossier")
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail("INFORMATION - L'ensemble des documents a été synchonisé avec succès")
  }

  Utils.getSearchResultController().getModel().replay()
  Utils.getAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - SynchronizationAvisMutationSimpleViewExec - ERREUR : ", e)
  return
} finally {
  if(resultSet != null) resultSet.close()
  if(preparedStatement != null) preparedStatement.close()
  if(connection != null) connection.close()
}

_scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW EXEC - END")