import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import com.digitech.jcorbairs.admin.AuthorityListTermAdmin;
import com.digitech.jcorbairs.admin.AuthorityListsManager;

import com.digitech.jcorbairs.Note;
import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.Document;
import com.digitech.jcorbairs.PrimaryDocument

import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import Constants;
import Methods;

/*************************************************************************************************
 *								Duplication de document - INIT
 **************************************************************************************************
 Date : 16.11.2018
 Auteur : MTO

 Description : Permet la duplication le document Avis de mutation sélecionné
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT AVIS MUTATION SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = new HashMap<String, Object>();
List<IDocument> docs = null;
String errorDocuments = null;
IDocument document = null;
Connection connection = null;
PreparedStatement preparedStatement = null;
ResultSet resultSet = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION DE DUPLICATION : ");

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

}catch(Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - ERREUR : ", e.localizedMessage);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try
{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT SIMPLE VIEW EXEC - END");
        return
    }

    document = docs.get(0);

    Domain domain = new Domain(DossierCoreContext.getAdminJeton(), Constants.CTY_AVIS_MUTATION_DOCUMENT);

    Document newdoc = new Document(DossierCoreContext.getAdminJeton(), domain, 500);

    //recuperation de la liste de tous les index du document
    Iterator ite = document.getFields().iterator();

    while(ite.hasNext())
    {
        IField fld = (IField)ite.next();
        if(fld != null && fld.getValues() != null && !fld.getCode().equalsIgnoreCase(Constants.LIST_STATUS_CODE)){
            String value = null;
            if(fld.getValue() instanceof String) value = fld.getValue();
            if(fld.getValue() instanceof Integer) value = fld.getValue().toString();
            if(fld.getValue() instanceof Date){
                SimpleDateFormat fout = new SimpleDateFormat(Constants.DATE_FORMAT);
                value = fout.format(fld.getValue());
            }
            Methods.defineDocumentIndex(newdoc, fld.getCode(), value);
        }
    }

    Methods.defineDocumentIndex(newdoc, Constants.LIST_STATUS_CODE, String.valueOf(Constants.LIST_STATUS_ITEM_ARCHIVE_ID));

    newdoc.updateContent();
    scriptLogger.debug("[CUSTOM ACTION] - DuplicationDocumentAvisMutationSimpleViewExec - DEBUG - Nouveau document ID n°"+newdoc.getId().toString());

    //telechargement de la pièce jointe du document courant pour la définir au nouveau document
    List<IAttachment> listAttach = document.getAttachments(userContext);
    if(!listAttach.isEmpty()) {
        IAttachment attach = listAttach.get(0);
        String primarydocLabel = attach.getAirsAttachment().getLabel();
        document.getAirsDocument().getInnerDocument().getPrimaryDocument(attach.getAirsAttachment(), userContext.getUserDownloadPath());
        File fattach = new File(userContext.getUserDownloadPath() + File.separator + attach.getAirsAttachment().getFileName());
        if (!fattach.exists()) {
            Methods.getDocumentMgr().deleteDocument(userContext, newdoc.getId());
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
            result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
            scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentAvisMutationSimpleViewExec - ERREUR : Téléchargement de la pièce jointe impossible");
            return;
        }
        String strTmp = fattach.getName();
        PrimaryDocument primaryDoc = new PrimaryDocument(strTmp, primarydocLabel);
        if (primaryDoc == null) {
            Methods.getDocumentMgr().deleteDocument(userContext, newdoc.getId());
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
            result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
            scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentAvisMutationSimpleViewExec - ERREUR : Création de la nouvelle pièce jointe impossible");
            return;
        }
        String pathDoc = fattach.getParent();
        pathDoc = pathDoc.replace("\\", "\\\\");
        newdoc.addOrUpdatePrimaryDocument(primaryDoc, pathDoc);

        fattach.delete();
    }

    //ajout historique sur document original
    Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_FIELDCHANGE, "Document dupliqué");
    //ajout historique sur duplicata
    Methods.getAuditMgr().addDocumentEvent(userContext, Methods.getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), newdoc.getId()),  Constants.ADV_EVENT_FIELDCHANGE, "Duplicata du document " + document.getAirsRefId());


    if(docs.size() > 1) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN );
        result.setMessageDetail("ATTENTION - Plusieurs documents ont été sélectionnés mais seul le premier a été traité : Doc n° "+document.getAirsRefId().toString());
    }
    else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO );
        result.setMessageDetail("INFORMATION - Document dupliqué avec succés");
    }

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR );
    result.setMessageDetail("ERREUR - La duplication est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewExec - ERREUR : ",e);
    return;
}finally{
    if(resultSet != null) resultSet.close();
    if(preparedStatement != null) preparedStatement.close();
    if(connection != null) connection.close();
}

scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT AVIS MUTATION SIMPLE VIEW EXEC - END");
