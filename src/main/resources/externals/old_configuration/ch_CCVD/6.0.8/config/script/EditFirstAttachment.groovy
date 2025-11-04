import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.attachment.EditAttachmentModel
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer

/**************************************************************************************************
 *				                Edition de la première pièce jointe
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'édition de la 1ère pièce jointe du 1er document sélectionné
 **************************************************************************************************/
scriptLogger.debug("[CUSTOM ACTION] - EDIT FIRST ATTACHMENT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
List<IDocument> docs = null;
try {
    result = output.getValue();
    result.setMessageSummary("EDITION PIECE JOINTE : ");

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - EditFirstAttachment - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    EditAttachmentModel editAttachmentModel = Utils.getEditAttachmentController().getModel();
    editAttachmentModel.clear();

    if (docs == null || docs.size()==0) docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments();

    IDocument document = docs.get(0);
    List<IAttachment> attachments = document.getAttachments(userContext);

    editAttachmentModel.setCurrentDocument(document);
    if (!attachments.isEmpty()) {
        editAttachmentModel.setCurrentAttachment(attachments.getAt(0));
        editAttachmentModel.getAbsoluteAttachmentFilePath();
    }
    editAttachmentModel.setUploadUserPath(UserContext.getInstance().getUserUploadPath());

    UserContext.getInstance().setCurrentDocument(document);

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'ouverture de la pièce jointe est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - EditFirstAttachment - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT FIRST ATTACHMENT - END");
