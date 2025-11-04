import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.jcorbairs.Option
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/*************************************************************************************************
 * 							Copier / Coller d'un document - INIT
 **************************************************************************************************
 Date : 04.03.2016
 Auteur : MTO

 Description : Permet de copier / coller un document entre dossier
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
List<IDocument> docs = null
IDocument document = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
Document xmlDocument = null
FileInputStream file = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  data.clear()

  // Chargement du XML Configuration
  file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
  builderFactory = DocumentBuilderFactory.newInstance()
  builder = builderFactory.newDocumentBuilder()
  xmlDocument = builder.parse(file)

} catch (Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  if (docs == null || docs.size() == 0) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
    return
  } else if (docs.size() > 1) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_one_document_only"), false)
    return
  }

  document = docs.get(0)
  data.put("DATA_NSS", document.getField(Constants.FIELD_NSS_CODE).getValue().toString())
  data.put("DATA_USE_NDEM", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GLOBAL_USE_NDEM))
  data.put("DATA_NDEM", (document.getField(Constants.FIELD_DEM_CODE).getValue() == null) ? "" : document.getField(Constants.FIELD_DEM_CODE).getValue().toString())
  data.put("DATA_DATE", (document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue() == null) ? "" : document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue().toString())
  data.put("DATA_DOCUMENT", Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0))
  data.put("DATA_COUNT_PAGES", document.getField(Constants.FIELD_NUMBER_PAGES_CODE).getValue().toString())
  data.put("DATA_LIST_DOCUMENT_TYPE", document.getField(Constants.LIST_TYPES_DOCUMENT_CODE).getValue().toString())
  data.put("DATA_LIST_DOCUMENT_TYPES", Methods.getAuthorityListOfSelectItem(Constants.LIST_TYPES_DOCUMENT_ID))
  data.put("DATA_LIST_NDEM", Methods.listToSelectItem(Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_NDEM_LIST)))
  data.put("DATA_PRIORITE", (document.getField(Constants.FIELD_PRIORITE_CODE).getValue() == null) ? "" : document.getField(Constants.FIELD_PRIORITE_CODE).getValue().toString())
  data.put("DATA_NAME", Methods.getFieldValue(document.getAirsDocument().getInnerDocument(), Constants.FIELD_NAME_CODE))
  String language = "fr"
  Option option = userContext.getUser().getOption("PERSONNAL_SPACE")
  if (option.getXmlValue().contains("value=\"de\"/")) language = "de"
  else if (option.getXmlValue().contains("value=\"it\"/")) language = "it"
  data.put("DATA_AIRSDOSSIER_URL", Constants.APPLICATION_AIRSDOSSIER_URL + "rest/DocSeries/assure/language/" + language + "/##NSS##")
} catch (Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewInit - ERREUR : ", e)
  return
} finally {
  if (file != null) {
    try {
      file.close()
    } catch (Exception e) {
      scriptLogger.warn("[CUSTOM ACTION] - CopyPasteDocumentSimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ", e)
    }
  }
}

scriptLogger.debug("[CUSTOM ACTION] - COPY PASTE DOCUMENT SIMPLE VIEW INIT - END")