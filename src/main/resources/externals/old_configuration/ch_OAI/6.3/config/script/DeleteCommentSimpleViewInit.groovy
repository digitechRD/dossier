import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Note

import javax.faces.model.SelectItem

/*************************************************************************************************
 *							Suppression des commentaires - INIT
 **************************************************************************************************
 Date : 06.07.2016
 Auteur : MTO

 Description : Permet de supprimer tous les commentaires d'un document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DELETE COMMENT SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
List<IDocument> docs = null;
IDocument document = null;
Map<String, Object> data = null;
CustomActionController customActionController = null;
String errorDocuments = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_init_error"));
    scriptLogger.error("[CUSTOM ACTION] - DeleteCommentSimpleViewINIT - ERREUR : ",e);
    return;
}


/**
 * TRAITEMENT
 **************************************************************************************************/

try{
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
    if(docs.size() == 0){
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false);
        return;
    }else if(docs.size() > 1){
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_one_document_only"), false);
        return;
    }

    document = docs.get(0);
    HashMap<Integer, String> noteMap = new HashMap<Integer, String>();
    HashMap<Integer, Boolean> noteCheckedMap = new HashMap<Integer, Boolean>();
    if(document.getAirsDocument().getInnerDocument().getNotes().isEmpty()) Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_comment_empty"), false);
    else{
        for(Note note : document.getAirsDocument().getInnerDocument().getNotes()){
            noteCheckedMap.put(note.getId(), false);
            noteMap.put(note.getId(), note.getText());
        }
    }
    List<SelectItem> statusList = new ArrayList<SelectItem>();
    statusList.add(new SelectItem("1", BundleUtils.getTranslation("groovy_comment_selected_all")));
    statusList.add(new SelectItem("0", BundleUtils.getTranslation("groovy_comment_selected_unitary")));

    data.put("DATA_NOTES_ID", noteMap.keySet().toArray());
    data.put("DATA_NOTES_COMMENT", noteMap);
    data.put("DATA_NOTES_CHECKED", noteCheckedMap);

    data.put("DATA_LIST_STATUS", statusList);
    data.put("DATA_STATUS", "0");

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_init_error"));
    scriptLogger.error("[CUSTOM ACTION] - DeleteCommentSimpleViewInit - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - DELETE COMMENT SIMPLE VIEW INIT - END");