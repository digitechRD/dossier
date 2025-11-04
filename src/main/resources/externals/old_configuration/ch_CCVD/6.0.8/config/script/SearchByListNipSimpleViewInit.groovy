import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;

import java.util.*;

import Methods;

/*************************************************************************************************
 *								Recherche par NIP - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet une recherche en saisissant une liste de NIP
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SEARCH BY LIST NIP SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - SearchByListNipSimpleViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    data.put("listnip", "");
}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false);
    scriptLogger.error("[CUSTOM ACTION] - SearchByListNipSimpleViewInit - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - SEARCH BY LIST NIP SIMPLE VIEW INIT - END");

 