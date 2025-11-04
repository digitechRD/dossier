import java.util.*;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import Methods;
import Constants;

/*************************************************************************************************
 *							Ajout d'un commentaire ou description - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'ajout d'un commentaire et/ou d'une description à un ensemble de documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - ADD NOTE SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
IDocument document = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - AddNoteSimpleViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
    if(docs.size() == 0){
        Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - Aucun document sélectionné", false);
        return;
    }else document = docs.get(0);

    // Récupération de la description
    if(Constants.CTY_AFFILIATED_DOCUMENT.equalsIgnoreCase(document.getDomain().getCode())) {
        if (document.getField(Constants.FIELD_COMMENT_CODE) != null && document.getField(Constants.FIELD_COMMENT_CODE).getValues() != null){
            data.put("description", document.getField("COM").getValues().get(0).toString());
        }else data.put("description", "");
    }
    data.put("commentaire","");

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - AddNoteSimpleViewInit - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - ADD NOTE SIMPLE VIEW INIT - END");