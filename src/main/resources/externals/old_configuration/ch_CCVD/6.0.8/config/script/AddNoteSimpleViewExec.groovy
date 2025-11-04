import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.ServiceManager

import java.util.*;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.controller.NavigationController;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.IScriptResultValueModel;

import com.digitech.jcorbairs.Note;

import Constants;
import Methods;

/*************************************************************************************************
 *							Ajout d'un commentaire ou description - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'ajout d'un commentaire et/ou d'une description à un ensemble de documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - ADD NOTE SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;
String errorDocuments = null;
List<IDocument> docs = null;
IRight rightMgr = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION AJOUT NOTE : ");

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - AddNoteSimpleViewExec - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - ADD NOTE SIMPLE VIEW EXEC - END");
        return
    }

    rightMgr = (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);

    for(IDocument document : docs)
    {
        try{
            if(!rightMgr.isAuthorizedToEditDocument(userContext, document)){
                scriptLogger.error("[CUSTOM ACTION] - AddNoteSimpleViewExec - DOC n°"+document.getAirsRefId()+" - Droit de modification non autorisé");
                if(errorDocuments == null) errorDocuments = document.getAirsRefId().toString()+" (Droit insuffisant)";
                else errorDocuments += ", "+document.getAirsRefId().toString()+" (Droit insuffisant)";
            }else if(!Constants.UNLOCK_TYPE.equals(document.getLockType())) {
                if(errorDocuments == null) errorDocuments = document.getAirsRefId().toString()+" (Document verrouillé)";
                else errorDocuments += ", "+document.getAirsRefId().toString()+" (Document verrouillé)";
            }else{
                // Dàfinition Commentaire
                if(!data.get("commentaire").equalsIgnoreCase("")){
                    Note myNote = new Note(Constants.AIRS_NOTE_ID);
                    myNote.setText(data.get("commentaire").toString());
                    myNote.setPublic();
                    document.getAirsDocument().getInnerDocument().addNote(myNote);
                }
                // Dàfinition description
                if(Constants.CTY_AFFILIATED_DOCUMENT.equalsIgnoreCase(document.getDomain().getCode())){
                    Methods.defineDocumentIndex(document.getAirsDocument().getInnerDocument(), Constants.FIELD_COMMENT_CODE, data.get("description").toString());
                }
                document.getAirsDocument().getInnerDocument().updateContent();
            }
        }catch(Exception e){
            scriptLogger.error("[CUSTOM ACTION] - AddNoteSimpleViewExec - DOC n°"+document.getAirsRefId()+" - ERREUR : ",e);
            if(errorDocuments == null) errorDocuments = document.getAirsRefId().toString()+" (Erreur de traitement)";
            else errorDocuments += ", "+document.getAirsRefId().toString()+" (Erreur de traitement)";
        }
    }

    Utils.getSearchResultController().replay();
    Utils.getSimpleViewAttachmentController().getModel().refreshDocument();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

    if(errorDocuments == null){
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
        result.setMessageDetail("INFORMATION - Les informations saisies ont été enregistrées avec succès");
    }else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
        result.setMessageDetail("ATTENTION - Les documents suivants n'ont pas été modifiés : "+ errorDocuments +". Veuillez contacter votre administrateur");
    }
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - AddNoteSimpleViewExec - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - ADD NOTE SIMPLE VIEW EXEC - END");

