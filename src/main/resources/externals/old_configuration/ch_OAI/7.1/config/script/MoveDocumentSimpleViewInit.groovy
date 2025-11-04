import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/*************************************************************************************************
 * 							Couper / Coller d'un document - INIT
 **************************************************************************************************
 Date : 12.04.2016
 Auteur : JUF

 Description : Permet de couper / coller (Déplacer) un document entre dossier
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - MOVE DOCUMENT SIMPLE VIEW INIT - START")

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
String nss = ""
String ndem = ""
FileInputStream file = null
boolean isDifferentNSS = false
boolean isDifferentNDEM = false

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  data.clear()

  // Chargement du XML Configuration
  file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
  builderFactory = DocumentBuilderFactory.newInstance()
  builder = builderFactory.newDocumentBuilder()
  xmlDocument = builder.parse(file)

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
  if(docs == null || docs.size() == 0) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
    return
  }
  else if(docs.size() == 1) {
    nss = docs.get(0).getField(Constants.FIELD_NSS_CODE).getValue().toString()
    ndem = docs.get(0).getField(Constants.FIELD_DEM_CODE).getValue().toString()
  }
  else {
    for(IDocument doc : docs) {
      if(isDifferentNSS && isDifferentNDEM)
        break
      if(!isDifferentNSS && ("".equals(nss) || nss.equals(doc.getField(Constants.FIELD_NSS_CODE).getValue().toString()))) {
        nss = doc.getField(Constants.FIELD_NSS_CODE).getValue().toString()
      }
      else {
        nss = ""
        isDifferentNSS = true
      }
      if(!isDifferentNDEM && ("".equals(ndem) || ndem.equals(doc.getField(Constants.FIELD_DEM_CODE).getValue().toString()))) {
        ndem = doc.getField(Constants.FIELD_DEM_CODE).getValue().toString()
      }
      else {
        ndem = ""
        isDifferentNDEM = true
      }
    }
  }
  data.put("DATA_NSS", nss)
  data.put("DATA_USE_NDEM", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GLOBAL_USE_NDEM))
  data.put("DATA_DEFAULT_NDEM", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GLOBAL_DEFAULT_NDEM))
  data.put("DATA_NDEM", ndem)
  data.put("DATA_LIST_NDEM", Methods.listToSelectItem(Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_NDEM_LIST)))
  String language = "fr"
  userContext.getUser().getOption("PERSONNAL_SPACE")
  if(option.getXmlValue().contains("value=\"de\"/"))
    language = "de"
  else if(option.getXmlValue().contains("value=\"it\"/"))
    language = "it"
  data.put("DATA_AIRSDOSSIER_URL", Constants.APPLICATION_AIRSDOSSIER_URL + "rest/DocSeries/assure/language/" + language + "/##NSS##")

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
  scriptLogger.error("[CUSTOM ACTION] - MoveDocumentSimpleViewInit - ERREUR : ", e)
  return
} finally {
  if(file != null) {
    try {
      file.close()
    } catch(Exception e) {
      scriptLogger.warn("[CUSTOM ACTION] - MoveDocumentSimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ", e)
    }
  }
}

scriptLogger.debug("[CUSTOM ACTION] - MOVE DOCUMENT SIMPLE VIEW INIT - END")