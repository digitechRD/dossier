import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.controller.NavigationController;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import java.sql.Connection;
import java.sql.DriverManager
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.digitech.dossier.common.model.backend.DossierCoreContext;

import com.digitech.jcorbairs.admin.AuthorityListTermAdmin;
import com.digitech.jcorbairs.admin.AuthorityListsManager;
import com.digitech.jcorbairs.Document;

import Constants;
import Methods;

/*************************************************************************************************
 *				Completement d'information lors de la création d'un document affilié
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet de récupérer des informations provenant de Web@AI lors de la création d’un document affilié depuis la GED.
 Toutes les informations sont retrouvés grâce au numéro affilié.
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
Connection connection = null;
PreparedStatement preparedStatement = null;
ResultSet resultSet = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
String errorDocuments = null;
String title = null;
String firstName = null;
String lastName = null;
String adress = null;
String categoryId = null;
String nss = null;
String nip = null;

try {
    result = output.getValue();
    result.setMessageSummary("SYNCHONISATION WEB@AVS DOCUMENT AFFILIE : ");

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
    nip = data.get("NIP").toString();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - SynchronizationWebAVSSimpleViewExec - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AI SIMPLE VIEW EXEC - END");
        return
    }else if("--".equalsIgnoreCase(nip) || "".equalsIgnoreCase(nip)){
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Veuillez saisir un numéro de dossier");
        scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW EXEC - END");
        return;
    }

    Class.forName(Constants.DB_GLOBAZ_DRIVER);
    connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD);

    preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_NSS_BY_NAFF);
    preparedStatement.setString(1, nip);
    resultSet = preparedStatement.executeQuery();
    if(resultSet.next()){
        nss = resultSet.getString(1).trim().replaceAll("\\.", "");
    }else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
        result.setMessageDetail("ERREUR - Le NIP suivant n'est pas valide : "+nip);
        scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW EXEC - ERROR : Le NIP suivant n'est pas valide : "+nip);
        return;	
    }


    preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_IDENTITY_BY_NAFF);
    preparedStatement.setString(1, nip);
    resultSet = preparedStatement.executeQuery();
    if(resultSet.next()){
        // Définition du titre
        if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MR) title = Constants.LIST_TITLE_ITEM_MR_ID;
        else if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MME) title =  Constants.LIST_TITLE_ITEM_MME_ID;

        // Définition du nom et prénom
        lastName = resultSet.getString(2);
        firstName = resultSet.getString(3);
    }

    // Définition des adresses
    preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF);
    preparedStatement.setString(1, nip);
    resultSet = preparedStatement.executeQuery();
    adress = "";
    if(resultSet.next()){
        if("".equals(adress)) adress = resultSet.getString(1);
		else adress += " ; "+resultSet.getString(1);
    }

	if(nss == null || lastName == null || firstName == null || adress == null){
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Les informations n'ont pas été retrouvées dans la base tiers pour le NIP : "+nip+". Veuillez contacter votre administrateur.");
		return;
	}
	
    for(IDocument document : docs) {
        Document doc = Methods.getDocumentMgr().getDocument(userContext.getJeton(), document.getAirsRefId()).getAirsDocument().getInnerDocument();
        Methods.defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, nss);
		Methods.defineDocumentIndex(doc, Constants.FIELD_AFF_CODE, nip);
        Methods.defineDocumentIndex(doc, Constants.LIST_TITLE_CODE, title);
        Methods.defineDocumentIndex(doc, Constants.FIELD_LASTNAME_AFF_CODE, lastName);
        Methods.defineDocumentIndex(doc, Constants.FIELD_FIRSTNAME_AFF_CODE, firstName);
        Methods.defineDocumentIndex(doc, Constants.FIELD_ADRESS_AFF_CODE, adress);
        // Définition de l'utilisateur courant comme taxateur
        //Methods.defineDocumentIndex(doc, Constants.FIELD_TAXING_USER_CODE, userContext.getUserId().toString());
        if (document.getField(Constants.LIST_STATUS_CODE) != null && Constants.LIST_STATUS_ITEM_TO_ARCHIVED_ID.toString().equals(document.getField(Constants.LIST_STATUS_CODE).getValue()))
            Methods.defineDocumentIndex(doc, Constants.LIST_STATUS_CODE.toString(), Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());

        doc.updateContent();

        // Recherche du dossier parent
        Document dossier = Methods.getDossier(document, Constants.CTY_AFFILIATED_FOLDER, Constants.FIELD_AFF_CODE, document.getField(Constants.FIELD_AFF_CODE).getValue().toString(), Constants.SECRET_LEVEL_DEFAULT);
        // Rattachement au dossier père
        if (dossier != null){
			doc.addParent(dossier);
			doc.updateContent();
		} else {
            if(errorDocuments == null) errorDocuments = doc.getId().toString();
            else errorDocuments += ", "+doc.getId().toString();
        }
    }

    if(errorDocuments != null) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Les documents n° :" + errorDocuments +" ont été synchronisé avec la base des tiers sans être liés au dossier");
    }else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
        result.setMessageDetail("INFORMATION - L'ensemble des documents a été synchonisé avec succès");
    }

    Utils.getSearchResultController().getModel().replay();
    Utils.getAttachmentController().getModel().refreshDocument();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - SynchronizationWebAVSSimpleViewExec - ERREUR : ",e);
    return;
}finally{
    if(resultSet != null) resultSet.close();
    if(preparedStatement != null) preparedStatement.close();
    if(connection != null) connection.close();
}

scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW EXEC - END");