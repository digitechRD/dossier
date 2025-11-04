import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import java.util.*;

import Methods;
import Constants;

/*************************************************************************************************
 *				Synchronisation des informations WEB@AVS avec les documents sélectionnés
 **************************************************************************************************
 Date : 24.09.2018
 Auteur : MTO

 Description : Permet de récupérer des informations provenant de l'application métier lors de la création d’un document avis mutation depuis la GED.
 Toutes les informations sont retrouvés grâce au numéro d'avis de mutation.
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - SynchronizationAvisMutationSimpleViewInit - ERREUR : ",e);
    return;
}


/**
 * TRAITEMENT
 **************************************************************************************************/
try
{
    if(docs.size() == 0){
        Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - Aucun document sélectionné", false);
        return;
    }

    data.put("AVIS_MUTATION", (docs.get(0).getField(Constants.FIELD_NAVISMUTATION_CODE).getValue() == null)?"":docs.get(0).getField(Constants.FIELD_NAVISMUTATION_CODE).getValue());
}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - SynchronizationAvisMutationSimpleViewInit - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION AVIS MUTATION SIMPLE VIEW INIT - END");



