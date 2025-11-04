import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument

/*************************************************************************************************
 * 				Synchronisation des informations WEB@AVS avec les documents sélectionnés
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet de récupérer des informations provenant de WEB@AVS lors de la création d’un document affilié depuis la GED.
 Toutes les informations sont retrouvés grâce au numéro affilié.
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - SynchronizationWebAVSSimpleViewInit - ERREUR : ", e)
  return
}


/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(docs.size() == 0) {
    Methods.addStateMessage(data, "DATA_WARN_MSG", "ATTENTION - Aucun document sélectionné", false)
    return
  }

  data.put("NIP", (docs.get(0).getField(Constants.FIELD_AFF_CODE).getValue() == null) ? "" : docs.get(0).getField(Constants.FIELD_AFF_CODE).getValue())
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - SynchronizationWebAVSSimpleViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW INIT - END")


