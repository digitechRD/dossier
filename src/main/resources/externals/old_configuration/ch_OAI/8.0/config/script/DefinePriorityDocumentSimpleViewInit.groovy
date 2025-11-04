import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import org.w3c.dom.Document

import javax.faces.model.SelectItem
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/*************************************************************************************************
 * 							    DefinePriorityDocumentSimpleViewInit - INIT
 **************************************************************************************************
 Date : 11.08.2016
 Auteur : JFE

 Description : Définir la priorité d'un document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DEFINE PRIORITY DOCUMENT SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
IDocument document = null
String request = null
String login = null
List<SelectItem> filter = new ArrayList<SelectItem>()
FileInputStream file = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
Document xmlDocument = null
String fieldsList = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  request = userContext.getCurrentSearchModel().getRequest()
  login = userContext.getLoggedUser().getLogin()

  // Chargement du XML Configuration
  file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
  builderFactory = DocumentBuilderFactory.newInstance()
  builder = builderFactory.newDocumentBuilder()
  xmlDocument = builder.parse(file)

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - DefinePriorityDocumentSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  if(docs.size() == 0) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
    return
  }
  else if(docs.size() > 1) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_one_document_only"), false)
    return
  }
  
  document = docs.get(0)
  data.put("DATA_LIST_PRIORITY", Methods.getAuthorityListOfSelectItemWithEmptyItem(Constants.LIST_PRIORITE_ID))
  data.put("DATA_PRIORITY", document.getField(Constants.FIELD_PRIORITE_CODE).getValue().toString())

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - DefinePriorityDocumentSimpleViewInit - ERREUR : ", e)
  return
} finally {
  if(file != null) {
    try {
      file.close()
    } catch(Exception e) {
      scriptLogger.warn("[CUSTOM ACTION] - DefinePriorityDocumentSimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ", e)
    }
  }
}

scriptLogger.debug("[CUSTOM ACTION] - DEFINE PRIORITY DOCUMENT SIMPLE VIEW INIT - END")

