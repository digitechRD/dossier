import Constants
import ch.digitech.oai.interfaceRMI.ServiceOAI
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import org.w3c.dom.Document

import javax.faces.model.SelectItem
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.text.SimpleDateFormat

/*************************************************************************************************
 * 							ExportDocumentFroEngravingAISimpleView - EXEC
 **************************************************************************************************
 Date : 22.03.2016
 Auteur : MTO

 Description : Permet l'export de document pour gravage sur CD via des filtres
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT FOR ENGRAVING SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
CustomActionController customActionController = null
Map<String, Object> data = null
String errorDocuments = null
List<IDocument> docs = null
IRight rightMgr = null
List<Integer> listDocumentsExport = new ArrayList()
String exportDestFileName = ExportUtils.getPdfFileName()
String exportDestPath = null
SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS)
FileInputStream fileInputStream = null
Document xmlDocument = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
Properties conf = new Properties()
boolean indexComplete = true
String docIncomplete = null
String indexDateEmissions = null
String msg = "OK"

try {
  result = output.getValue()
  result.setMessageSummary(BundleUtils.getTranslation("groovy_export_engraving_action"))

  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  fileInputStream = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
  builderFactory = DocumentBuilderFactory.newInstance()
  builder = builderFactory.newDocumentBuilder()
  xmlDocument = builder.parse(fileInputStream)

  docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - ExportPDFForEngravingSimpleViewExec - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT FOR ENGRAVING SIMPLE VIEW EXEC - END")
    return
  }

  List<String> filters = new ArrayList()
  for(String s : data.get("DATA_UNDER_FILTER").toString().split("::")) {
    if(s != null && !s.isEmpty())
      filters.add(String.format("%05d", Integer.valueOf(s)))
  }


  boolean isExportFolder = ("1".equals(data.get("DATA_IS_FOLDER").toString()))
  boolean isAdmin = (data.get("DATA_PASSWORD_ADMIN").equals(data.get("DATA_PASSWORD_ADMIN_INPUT")))
  Date beginDate = (Date) data.get("DATA_BEGIN_DATE")
  Date endDate = (Date) data.get("DATA_END_DATE")

  // Récupération de libellé du filtre
  Integer idFilter = 0
  if(isExportFolder)
    idFilter = Integer.parseInt(String.valueOf(data.get("DATA_FILTER"))) - 2
  else
    idFilter = Integer.parseInt(String.valueOf(data.get("DATA_FILTER"))) - 1
  List<SelectItem> listSI = (List<SelectItem>) data.get("DATA_FILTERS")
  String labelFilter = String.valueOf(listSI.get(idFilter).getLabel())
  data.put("FILTER_TITLE", labelFilter)

  /************************* Chargement fichier traduction ***********************************/
  //scriptLogger.debug(Constants.APPLICATION_TRADUCTION_FILES+data.get("DATA_LANGUE").toString()+".properties");
  if(!new File(Constants.APPLICATION_TRADUCTION_FILES + data.get("DATA_LANGUE").toString() + ".properties").exists()) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_export_tribunaux_erreur_properties"))
    return
  }

  InputStreamReader reader = null
  try {
    reader = new InputStreamReader(new FileInputStream(Constants.APPLICATION_TRADUCTION_FILES + data.get("DATA_LANGUE").toString() + "" +
                                                           ".properties"), "UTF8")
    conf.load(reader)
  }
  finally {
    try {
      reader?.close()
    }
    catch(IOException ignored) {
    }
  }
  /************************* Fin chargement fichier traduction ***********************************/
  if(isExportFolder) {
    if(!data.get("DATA_UNDER_FILTER").toString().isEmpty()) {
      //listDocumentsExport = Methods.getDocumentsListIdByNSS(Constants.CTY_DOCUMENT_ASSURE, String.valueOf(data.get("DATA_NSS")), String.valueOf(data.get("DATA_FILTER_FIELD")), filters, simpleDateFormat.format(data.get("DATA_BEGIN_DATE")), simpleDateFormat.format(data.get("DATA_END_DATE")), isAdmin);
      listDocumentsExport =
          Methods.getDocumentsListIdByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), String.valueOf(
              data.get("DATA_FILTER_FIELD")), filters, (Date) data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false)
    }
    else {
      //listDocumentsExport = Methods.getDocumentsListIdByNSS(Constants.CTY_DOCUMENT_ASSURE, String.valueOf(data.get("DATA_NSS")), String.valueOf(data.get("DATA_FILTER_FIELD")), null, simpleDateFormat.format(data.get("DATA_BEGIN_DATE")), simpleDateFormat.format(data.get("DATA_END_DATE")), isAdmin);
      listDocumentsExport =
          Methods.getDocumentsListIdByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), String.valueOf(
              data.get("DATA_FILTER_FIELD")), null, (Date) data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false)
    }
  }
  else {
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
    for(IDocument document : docs) {
      Date dateDocument = (Date) document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue()
      if(data.get("DATA_UNDER_FILTER").toString().isEmpty() && (((dateDocument.after(beginDate) && dateDocument.before(endDate)) || (
          dateDocument.compareTo(beginDate) == 0) || (dateDocument.compareTo(endDate) == 0)))) {
        listDocumentsExport.add(document.getAirsRefId())
      }
      else {
        for(String filter : filters) {
          if(filter.equals(String.format("%05d", Integer.valueOf(String.valueOf(document.getField(String.valueOf(data.get("DATA_FILTER_FIELD"))).
              getValue())))) && !isAdmin && (((dateDocument.after(beginDate) && dateDocument.before(endDate)) || (dateDocument.compareTo(beginDate) == 0) || (
              dateDocument.compareTo(endDate) == 0))))
            listDocumentsExport.add(document.getAirsRefId())
        }
      }
    }
  }

  // Création du PDF
  if(listDocumentsExport.isEmpty()) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_list_file"))
    return
  }
  else {


    /******** Si export bordereau -> Check que tous les indexes soient présents *****/
    if(!"1".equals(data.get("DATA_MODE_EXPORT").toString())) {
      if("7".equals(data.get("DATA_MODE_EXPORT").toString()) || "5".equals(data.get("DATA_MODE_EXPORT").toString())) {
        if(!data.get("DATA_CONFIRMATION_SEND")) {
          result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
          result.setMessageDetail(BundleUtils.getTranslation("groovy_confirmatin_send_notchecked"))
          return
        }
        if(data.get("DATA_STAKEHOLDER") == null) {
          result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
          result.setMessageDetail(BundleUtils.getTranslation("groovy_stackholder_emtpy"))
          return
        }

        //Contrôle de conformité
        if(!Methods.isValidEmailAddress(data.get("DATA_STAKEHOLDER").toString())) {
          result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
          result.setMessageDetail(BundleUtils.getTranslation("groovy_stackholder_email_notvalid"))
          return
        }
        if(!Methods.isValidEmailAddress(data.get("DATA_SENDER_MAIL").toString())) {
          result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
          result.setMessageDetail(BundleUtils.getTranslation("groovy_sender_email_notvalid"))
          return
        }
      }

      indexDateEmissions = Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_DEFAULT_DATE)
      indexComplete = true

      docIncomplete = conf.getProperty("groovy_export_tribunaux_indexes_incomplets")
      for(Integer idDoc : listDocumentsExport) {
        IDocument docTmp = Methods.getDocumentMgr().getDocument(UserContext.getInstance().getJeton(), idDoc)
        if(docTmp.getField(Constants.FIELD_EMETTEUR_CODE).getValue() == null) {

          if(indexComplete) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
            indexComplete = false
          }
          docIncomplete = docIncomplete + "\n" + idDoc
        }
        else {
          if(indexDateEmissions != "") {
            if(docTmp.getField(indexDateEmissions).getValue() == null) {
              result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
              indexComplete = false
              docIncomplete = docIncomplete + "\n" + idDoc
            }
          }

        }
      }
    }
    /*************************************************************************/

    Set cles = data.keySet()
    Iterator it = cles.iterator()
    Map<Object, Object> data_tmp = new HashMap()
    while(it.hasNext()) {
      Object cle = it.next()
      Object valeur = data.get(cle)
      data_tmp.put(cle, valeur)
    }
    Registry annuaire = LocateRegistry.getRegistry(Constants.APPLICATION_AIRSSERVEUR_HOST)
    ServiceOAI serviceDistant = (ServiceOAI) annuaire.lookup("servicesOAI")

    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss")

    serviceDistant.executeBurning(listDocumentsExport, data_tmp, xmlDocument, conf, indexDateEmissions, String.valueOf(data.get("DATA_NSS")) + "_" +
        userContext.getUser().getLogin() + "_" + df.format(new Date().getTime()) + Constants.APPLICATION_PDF_EXTENSION)
  }

  Methods.logActionUser(Constants.ACTION_GRAVAGE, labelFilter, Integer.toString(listDocumentsExport.size()), userContext.getUser().getLogin(),
                        data.get("DATA_NSS").toString())
  result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_export_tribunaux_inprocess"))

  Utils.getSearchResultTableController().refreshAndKeepFilter()


} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - ExportPDFForEngravingSimpleViewExec - ERREUR : ", e)
  return
} finally {
  if(fileInputStream != null) {
    try {
      fileInputStream.close()
    } catch(Exception e) {
      scriptLogger.warn("[CUSTOM ACTION] - ExportPDFForEngravingSimpleViewExec - ATTENTION - Fichier XML de configuration non cloturé : ", e)
    }
  }
}

scriptLogger.debug("[CUSTOM ACTION] - EXPORT FOR ENGRAVING SIMPLE VIEW EXEC - END")