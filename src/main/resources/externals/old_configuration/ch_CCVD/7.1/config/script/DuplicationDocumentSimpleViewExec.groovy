import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Domain
import com.digitech.jcorbairs.Note
import com.digitech.jcorbairs.PrimaryDocument
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

/*************************************************************************************************
 * 								Duplication de document - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la duplication des documents sélectionnés
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = new HashMap<String, Object>()
List<IDocument> docs = null
String errorDocuments = null
IDocument document = null
Connection connection = null
PreparedStatement preparedStatement = null
ResultSet resultSet = null

try {
  result = output.getValue()
  result.setMessageSummary("ACTION DE DUPLICATION : ")

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - ERREUR : ", e.localizedMessage)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    _scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT SIMPLE VIEW EXEC - END")
    return
  }

  document = docs.get(0)

  if(data.get("NAFF_FIELD").toString().length() != Constants.CCVD_NIP_MIN_SIZE) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail("ERREUR - Le numéro affilié n'est pas correct")
    return
  }

  // Récupération du nouveau service
  Domain domain = new Domain(DossierCoreContext.getAdminJeton(), Constants.CTY_AFFILIATED_DOCUMENT)
  // Définition selon cette valeur de service la valeur du niveau de secret du nouveau document
  Integer serviceId = Integer.parseInt(data.get("SERVICE_LIST_VALUE").toString())

  if(serviceId.equals("0")) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Veuillez choisir un service")
    return
  }

  String iSecretLevel = Constants.MAP_SERVICE_SECRET_LEVEL.get(serviceId)
  if(iSecretLevel == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail("ERREUR - La définition du niveau de secret du document est impossible. Veuillez contacter votre administrateur")
    _scriptLogger.error("[CUSTOM ACTION] - ReallocationServiceSimpleViewExec - DOC n°" + document.getAirsRefId() + " - Erreur lors de la récupération du niveau de secret : Service : " + data.get("SERVICE_LIST_VALUE").toString() + ", Niveau de secret : " + iSecretLevel)
    return
  }

  Document newdoc = new Document(DossierCoreContext.getAdminJeton(), domain, Integer.parseInt(iSecretLevel))

  //recuperation de la liste de tous les index du document
  Iterator ite = document.getFields().iterator()
  String valDossier = ""
  while(ite.hasNext()) {
    IField fld = (IField) ite.next()
    if(fld != null && fld.getValues() != null) {
      // Pour le type on définit celui recupere depuis le gui
      if(fld.getCode().equalsIgnoreCase(Constants.LIST_TYPE_CODE)) {
        Methods.defineDocumentIndex(newdoc, Constants.LIST_TYPE_CODE, data.get("TYPE_LIST_VALUE").toString())
        //recuperation de la categorie dependant du type
        String idCategory = null
        AuthorityListTermAdmin termtype = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Integer.parseInt(data.get("TYPE_LIST_VALUE").toString()))
        List<AuthorityListTermAdmin> lstalterms = AuthorityListsManager.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_CATEGORY_ID)
        for(AuthorityListTermAdmin termadm : lstalterms) {
          if(termtype.getSortKey().equalsIgnoreCase(termadm.getCode().toString())) {
            idCategory = termadm.getId().toString()
            break
          }
        }
        if(idCategory != null) Methods.defineDocumentIndex(newdoc, Constants.LIST_CATEGORY_CODE, idCategory)
      }
      else if(fld.getCode().equalsIgnoreCase(Constants.LIST_SERVICE_CODE)) {
        Methods.defineDocumentIndex(newdoc, Constants.LIST_SERVICE_CODE, data.get("SERVICE_LIST_VALUE").toString())
      }
      else if(fld.getCode().equalsIgnoreCase(Constants.LIST_STATUS_CODE)) {
        Methods.defineDocumentIndex(newdoc, Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString())

      }
      else if(fld.getCode().equalsIgnoreCase(Constants.FIELD_COMMENT_CODE)) {
        Methods.defineDocumentIndex(newdoc, Constants.FIELD_COMMENT_CODE, data.get("DESC_FIELD").toString())
      }
      /* A supprimer (Pour tests seulement)
      else if(fld.getCode().equalsIgnoreCase(Constants.FIELD_ORGANIZATION_WORKFLOW_CODE))
      {
          Methods.defineDocumentIndex(newdoc, Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, Constants.ORGANIZATION_RENTES_2_ID.toString());
      }*/
      else if(fld.getCode().equalsIgnoreCase(Constants.FIELD_AFF_CODE)) //on stocke la valeur de dossier pour rechercher le dossier par la suite et le lier au document
      {
        //si le nip est diff�rent de celui du document original on red�finira les valeur d'adresse, non, prenom qu'on r�cup�erera depuis la base GLOBAZ
        valDossier = data.get("NAFF_FIELD")
        Methods.defineDocumentIndex(newdoc, Constants.FIELD_AFF_CODE, valDossier)
      }
      else if(fld.getCode().equalsIgnoreCase(Constants.FIELD_CREATE_DATE_CODE)) {
        Date d = new Date()
        SimpleDateFormat fout = new SimpleDateFormat(Constants.DATE_FORMAT)
        Methods.defineDocumentIndex(newdoc, Constants.FIELD_CREATE_DATE_CODE, fout.format(fld.getValue()))
      }
      else if(fld.getCode().equalsIgnoreCase(Constants.FIELD_TAXING_USER_CODE)) {
        Methods.defineDocumentIndex(newdoc, Constants.FIELD_TAXING_USER_CODE, userContext.getUserId().toString())
      }
      else {
        String value = null
        if(fld.getValue() instanceof String) value = fld.getValue()
        if(fld.getValue() instanceof Integer) value = fld.getValue().toString()
        if(fld.getValue() instanceof Date) {
          SimpleDateFormat fout = new SimpleDateFormat(Constants.DATE_FORMAT)
          value = fout.format(fld.getValue())
        }
        Methods.defineDocumentIndex(newdoc, fld.getCode(), value)
      }
    }
  }

  newdoc.updateContent()
  _scriptLogger.debug("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - DEBUG - Nouveau document ID n°" + newdoc.getId().toString())


  // Si le NIP précisé dans le formulaire est le même que celui du document initial on ne synchronisera pas le document avec la base WEBAVS
  if(!document.getField(Constants.FIELD_AFF_CODE).getValue().toString().equalsIgnoreCase(data.get("NAFF_FIELD").toString())) {
    String title = null
    String firstName = null
    String lastName = null
    String adress = null
    String nss = null

    Class.forName(Constants.DB_GLOBAZ_DRIVER)
    connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD)

    preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_NSS_BY_NAFF)
    preparedStatement.setString(1, valDossier)
    resultSet = preparedStatement.executeQuery()
    if(resultSet.next()) {
      nss = resultSet.getString(1).trim().replaceAll("\\.", "")
    }

    preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_IDENTITY_BY_NAFF)
    preparedStatement.setString(1, valDossier)
    resultSet = preparedStatement.executeQuery()
    if(resultSet.next()) {
      // Définition du titre
      if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MR) title = Constants.LIST_TITLE_ITEM_MR_ID
      else if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MME) title = Constants.LIST_TITLE_ITEM_MME_ID

      // Définition du nom et prénom
      lastName = resultSet.getString(2)
      firstName = resultSet.getString(3)
    }

    // Définition des adresses
    preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF)
    preparedStatement.setString(1, valDossier)
    resultSet = preparedStatement.executeQuery()
    adress = ""
    if(resultSet.next()) {
      adress += resultSet.getString(1) + ";"
    }

    Methods.defineDocumentIndex(newdoc, Constants.FIELD_NSS_CODE, nss)
    Methods.defineDocumentIndex(newdoc, Constants.LIST_TITLE_CODE, title)
    Methods.defineDocumentIndex(newdoc, Constants.FIELD_LASTNAME_AFF_CODE, lastName)
    Methods.defineDocumentIndex(newdoc, Constants.FIELD_FIRSTNAME_AFF_CODE, firstName)
    Methods.defineDocumentIndex(newdoc, Constants.FIELD_ADRESS_AFF_CODE, adress)
    newdoc.updateContent()
  }

  //telechargement de la pièce jointe du document courant pour la définir au nouveau document
  List<IAttachment> listAttach = document.getAttachments(userContext)
  if(!listAttach.isEmpty()) {
    IAttachment attach = listAttach.get(0)
    String primarydocLabel = attach.getAirsAttachment().getLabel()
    document.getAirsDocument().getInnerDocument().getPrimaryDocument(attach.getAirsAttachment(), userContext.getUserDownloadPath())
    File fattach = new File(userContext.getUserDownloadPath() + File.separator + attach.getAirsAttachment().getFileName())
    if(!fattach.exists()) {
      Methods.getDocumentMgr().deleteDocument(userContext, newdoc.getId())
      result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
      result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
      _scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - ERREUR : Téléchargement de la pièce jointe impossible")
      return
    }
    String strTmp = fattach.getName()
    PrimaryDocument primaryDoc = new PrimaryDocument(strTmp, primarydocLabel)
    if(primaryDoc == null) {
      Methods.getDocumentMgr().deleteDocument(userContext, newdoc.getId())
      result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
      result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur")
      _scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - ERREUR : Création de la nouvelle pièce jointe impossible")
      return
    }
    String pathDoc = fattach.getParent()
    pathDoc = pathDoc.replace("\\", "\\\\")
    newdoc.addOrUpdatePrimaryDocument(primaryDoc, pathDoc)

    fattach.delete()
  }

  // Lien avec le dossier parent
  if(!valDossier.equalsIgnoreCase("")) {
    Document dossier = Methods.getDossier(document, Constants.CTY_AFFILIATED_FOLDER, Constants.FIELD_AFF_CODE, valDossier, Constants.SECRET_LEVEL_DEFAULT)
    newdoc.addParent(dossier)
  }

  //creation du lien de copie entre le document original et le duplicata
  newdoc.addDocumentLink(document.getAirsDocument().getInnerDocument(), "copie")
  document.getAirsDocument().getInnerDocument().addDocumentLink(newdoc, "copie")


  //ajout du commentaire
  String comment = data.get("COMMENT")
  if(!comment.equalsIgnoreCase("")) {
    Note newNote = new Note(Constants.AIRS_NOTE_ID)
    newNote.setText(comment)
    newNote.setPublic()
    newdoc.addNote(newNote)
  }

  //ajout historique sur document original
  Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_FIELDCHANGE, "Document dupliqué")
  //ajout historique sur duplicata
  Methods.getAuditMgr().addDocumentEvent(userContext, Methods.getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), newdoc.getId()), Constants.ADV_EVENT_FIELDCHANGE, "Duplicata du document " + document.getAirsRefId())


  if(docs.size() > 1) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail("ATTENTION - Plusieurs documents ont été sélectionnés mais seul le premier a été traité : Doc n° " + document.getAirsRefId().toString())
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail("INFORMATION - Document dupliqué avec succés")
  }

  //Utils.getSearchResultController().replay();
  //Utils.getSimpleViewAttachmentController().getModel().refreshDocument();
  //Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail("ERREUR - La duplication est impossible. Veuillez contacter votre administrateur")
  _scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - ERREUR : ", e)
  return
} finally {
  if(resultSet != null) resultSet.close()
  if(preparedStatement != null) preparedStatement.close()
  if(connection != null) connection.close()
}

_scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT SIMPLE VIEW EXEC - END")