package workflow

import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils

import javax.faces.model.SelectItem

/*************************************************************************************************
 *   					    			PEC - INIT
 **************************************************************************************************
 Date : 23.02.2016
 Auteur : MTO

 Description : Permet la prise en compte ou la distribution des documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - PEC SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<SelectItem> items = new ArrayList<SelectItem>()

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {

  if(Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() == 0) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
    return
  }


  IDocument document = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)
  if(!String.valueOf(Constants.LIST_WK_STATUT_TRANSFERT_PEC).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) &&
      !(String.valueOf(Constants.LIST_WK_STATUT_SEDEX).equalsIgnoreCase(String.valueOf(document.getField(Constants.LIST_WK_STATUT_CODE).getValue())) &&
          Constants.WORKFLOW_SEDEX_TO_ARCHIVE_AUTHORIZED)) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_error_status"), false)
    return

  }


  data.put("DATA_STATUS", Constants.LIST_WK_STATUT_ARCHIVE)
  data.put("DATA_COMMENT", "")

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - PECSimpleViewInit - ERREUR : ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - PEC SIMPLE VIEW INIT - END")