import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.comparator.SelectItemComparator
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

import javax.faces.model.SelectItem

/*************************************************************************************************
 * 								Duplication de document - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la duplication des documents sélectionnés
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
IDocument document = null
List<SelectItem> typeItems = new ArrayList<SelectItem>()
List<SelectItem> serviceItems = new ArrayList<SelectItem>()

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(docs.size() == 0) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Aucun document n'est sélectionné", false)
    return
  }
  else document = docs.get(0)

  // Initialisation des types de document
  List<AuthorityListTermAdmin> lstalterms = AuthorityListsManager.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_TYPE_ID)
  for(AuthorityListTermAdmin alterm : lstalterms) {
    typeItems.add(new SelectItem(alterm.getId().toString(), alterm.getValue1()))
  }
  Collections.sort(typeItems, new SelectItemComparator())
  data.put("TYPE_LIST", typeItems)
  data.put("TYPE_LIST_VALUE", (document.getField(Constants.LIST_TYPE_CODE).getValue() != null) ? document.getField(Constants.LIST_TYPE_CODE).getValue() : "0")

  // Initialisation des types de document
  List<AuthorityListTermAdmin> lstserviceitems = AuthorityListsManager.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_SERVICE_ID)
  for(AuthorityListTermAdmin alterm : lstserviceitems) {
    serviceItems.add(new SelectItem(alterm.getId().toString(), alterm.getValue1()))
    for(AuthorityListTermAdmin alfilsterm : alterm.loadChildren()) {
      serviceItems.add(new SelectItem(alfilsterm.getId().toString(), "-- " + alfilsterm.getValue1()))
    }
  }
  data.put("SERVICE_LIST", serviceItems)
  data.put("SERVICE_LIST_VALUE", (document.getField(Constants.LIST_SERVICE_CODE).getValue() != null) ? document.getField(Constants.LIST_SERVICE_CODE).getValue() : "0")

  data.put("DESC_FIELD", "")
  data.put("NAFF_FIELD", (document.getField(Constants.FIELD_AFF_CODE).getValue() != null) ? document.getField(Constants.FIELD_AFF_CODE).getValue() : "")
  data.put("COMMENT", "")

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - DuplicationDocumentSimpleViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - DUPLICATION DOCUMENT SIMPLE VIEW INIT - EMD")

