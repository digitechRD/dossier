import Constants
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.w3c.dom.Document

import javax.faces.model.SelectItem
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
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
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFForEngravingSimpleViewExec - ERREUR : ", e)
    return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT FOR ENGRAVING SIMPLE VIEW EXEC - END")
        return
    }


    data.put("DATA_IS_FOLDER", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_IS_FOLDER.replace("##ID##", String.valueOf(data.get("DATA_FILTER")))))
    data.put("DATA_ENGRAVER_TYPE", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_ENGRAVER_TYPE.replace("##ID##", String.valueOf(data.get("DATA_BURN")))))
    data.put("DATA_PATH_PDF", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GET_PATH_PDF.replace("##ID##", String.valueOf(data.get("DATA_BURN")))))
    data.put("DATA_PATH_JDF", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GET_PATH_JDF.replace("##ID##", String.valueOf(data.get("DATA_BURN")))))
    data.put("DATA_PATH_DAT", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_GET_PATH_DAT.replace("##ID##", String.valueOf(data.get("DATA_BURN")))))

    boolean isExportFolder = ("1".equals(data.get("DATA_IS_FOLDER").toString()))
    boolean isAdmin = (data.get("DATA_PASSWORD_ADMIN").equals(data.get("DATA_PASSWORD_ADMIN_INPUT")))
    Date beginDate = (Date) data.get("DATA_BEGIN_DATE")
    Date endDate = (Date) data.get("DATA_END_DATE")

    // Récupération de libellé du filtre
    Integer idFilter = 0
    idFilter = Integer.parseInt(String.valueOf(data.get("DATA_FILTER")))
    String labelFilter = ""
    for (SelectItem si : (List<SelectItem>) data.get("DATA_FILTERS")) {
        if (si.getValue().toString().equalsIgnoreCase(idFilter.toString())) {
            labelFilter = si.getLabel()
            break
        }
    }
    filterTitle = labelFilter
    data.put("FILTER_TITLE", filterTitle)
    scriptLogger.debug("IsExport" + isExportFolder)
    Map<String, List<String>> filters = Methods.getContentsMapWithFormat(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_FILTERS_MAP.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER"))), "%05d")


    /************************* Chargement fichier traduction ***********************************/
    //scriptLogger.debug(Constants.APPLICATION_TRADUCTION_FILES+data.get("DATA_LANGUE").toString()+".properties");
    if (!new File(Constants.APPLICATION_TRADUCTION_FILES + data.get("DATA_LANGUE").toString() + ".properties").exists()) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_export_tribunaux_erreur_properties"))
        return
    }

    conf.load(new InputStreamReader(new FileInputStream(Constants.APPLICATION_TRADUCTION_FILES + data.get("DATA_LANGUE").toString() + ".properties"), "UTF8"))
    /************************* Fin chargement fichier traduction ***********************************/
    if (isExportFolder) {
        if (!filters.isEmpty()) {
            scriptLogger.debug("	--> Dossier avec Filtre Filtre")
            //listDocumentsExport = Methods.getDocumentsListIdByNSS(Constants.CTY_DOCUMENT_ASSURE, String.valueOf(data.get("DATA_NSS")), String.valueOf(data.get("DATA_FILTER_FIELD")), filters, simpleDateFormat.format(data.get("DATA_BEGIN_DATE")), simpleDateFormat.format(data.get("DATA_END_DATE")), isAdmin);
            listDocumentsExport = Methods.getDocumentsListIdByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), filters, (Date) data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false, xmlDocument, String.valueOf(data.get("DATA_FILTER")), Constants.XML_ACTIONS_REQUEST_EXPORT_INCLUDE_FILTERS_MAP)
        }else {
            scriptLogger.debug("	--> Dossier sans Filtre Filtre")
            //listDocumentsExport = Methods.getDocumentsListIdByNSS(Constants.CTY_DOCUMENT_ASSURE, String.valueOf(data.get("DATA_NSS")), String.valueOf(data.get("DATA_FILTER_FIELD")), null, simpleDateFormat.format(data.get("DATA_BEGIN_DATE")), simpleDateFormat.format(data.get("DATA_END_DATE")), isAdmin);
            listDocumentsExport = Methods.getDocumentsListIdByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), null, (Date) data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false, xmlDocument, String.valueOf(data.get("DATA_FILTER")), Constants.XML_ACTIONS_REQUEST_EXPORT_INCLUDE_FILTERS_MAP)
        }
    } else {
        scriptLogger.debug("	--> Sélection sans Filtre Filtre")
        docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
        for (IDocument document : docs) {
            boolean toAdd = true
            Date dateDocument = (Date) document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue()
            if (filters.isEmpty()) {
                if ((dateDocument.after(beginDate) && dateDocument.before(endDate)) || (dateDocument.compareTo(beginDate) == 0) || (dateDocument.compareTo(endDate) == 0)) {
                    listDocumentsExport.add(document.getAirsRefId())
                }
            } else {
                Set cles = filters.keySet()
                Iterator it = cles.iterator()
                while (it.hasNext()) {
                    String field = (String) it.next()
                    boolean hasExcluded = ("0".equals(Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_INCLUDE_FILTERS_MAP.replace("##REPLACE_VALUE##", field).replace("##ID##", String.valueOf(data.get("DATA_FILTER"))).toString())) ? true : false)
                    String values = (List<String>) filters.get(field)
                    if (toAdd && ((!hasExcluded && !values.contains(String.format("%05d", Integer.valueOf(String.valueOf(document.getField(field).getValue()))))) || (hasExcluded && values.contains(String.format("%05d", Integer.valueOf(String.valueOf(document.getField(field).getValue())))))) && !isAdmin && (((dateDocument.after(beginDate) && dateDocument.before(endDate)) || (dateDocument.compareTo(beginDate) == 0) || (dateDocument.compareTo(endDate) == 0)))) {
                        toAdd = false
                    }

                }
                if (toAdd) {
                    listDocumentsExport.add(document.getAirsRefId())
                }
            }
        }
    }

    // Création du PDF
    if (listDocumentsExport.isEmpty()) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_list_file"))
        return
    } else {

        data.put("DATA_CREATOR", userContext.getUser().getLogin())

        /******** Si export bordereau -> Check que tous les indexes soient présents *****/
        if (!"1".equals(data.get("DATA_MODE_EXPORT").toString())) {
            if ("7".equals(data.get("DATA_MODE_EXPORT").toString()) || "5".equals(data.get("DATA_MODE_EXPORT").toString())) {
                if (!data.get("DATA_CONFIRMATION_SEND")) {
                    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_confirmatin_send_notchecked"))
                    return
                }
                if (data.get("DATA_STAKEHOLDER") == null) {
                    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_stackholder_emtpy"))
                    return
                }
                String intervenantTmp = data.get("DATA_STAKEHOLDER").toString().replace("KO-", "").replace("OK-", "")
                data.remove("DATA_STAKEHOLDER")
                data.put("DATA_STAKEHOLDER", intervenantTmp)
                //Contrôle de conformité
                if (!Methods.isValidEmailAddress(data.get("DATA_STAKEHOLDER").toString())) {
                    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_stackholder_email_notvalid"))
                    return
                }
                if (!Constants.USE_EMAIL_GENERIC && !Methods.isValidEmailAddress(data.get("DATA_SENDER_MAIL").toString())) {
                    result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_sender_email_notvalid"))
                    return
                }
                data.put("DATA_OBJECT", BundleUtils.getTranslation("groovy_sender_email_for").replace("##NSS##", data.get("DATA_NSS").toString()) + " " + data.get("DATA_SENDER_NAME").toString() + " (" + data.get("DATA_SENDER_MAIL").toString() + ") - " + data.get("DATA_OBJECT").toString())


            }

            indexDateEmissions = Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_DEFAULT_DATE)
            indexComplete = true

            docIncomplete = conf.getProperty("groovy_export_tribunaux_indexes_incomplets")
            for (Integer idDoc : listDocumentsExport) {
                IDocument docTmp = Methods.getDocumentMgr().getDocument(UserContext.getInstance().getJeton(), idDoc)
                if (docTmp.getField(Constants.FIELD_EMETTEUR_CODE).getValue() == null) {

                    if (indexComplete) {
                        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
                        indexComplete = false
                    }
                    docIncomplete = docIncomplete + "\n" + idDoc
                }else {
                    if(indexDateEmissions!="") {
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
        while (it.hasNext()) {
            Object cle = it.next()
            Object valeur = data.get(cle)
            data_tmp.put(cle, valeur)
        }

        URL url
        HttpURLConnection conn = null
        try {
            url = new URL(Constants.HTTP_GRAVAGE_EXECTUEBURNING_STANDARD)
            conn = (HttpURLConnection) url.openConnection()
            conn.setDoOutput(true)
            conn.setRequestMethod("POST")
            conn.setRequestProperty("Accept", "application/json")
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss")
            GravageOAI gravageOAI = new GravageOAI(listDocumentsExport, String.valueOf(data.get("DATA_NSS")) + "_" + userContext.getUser().getLogin() + "_" + df.format(new Date().getTime()) + Constants.APPLICATION_PDF_EXTENSION, xmlDocument, indexDateEmissions, data_tmp, conf)

            ObjectMapper mapper = new ObjectMapper()
            String jsonString = mapper.writeValueAsString(gravageOAI)
            OutputStream os = conn.getOutputStream()
            os.write(jsonString.getBytes())
            os.flush()

            BufferedReader bra = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())))

            String output
            while ((output = bra.readLine()) != null) {
                if (!output.contains("OK")) {
                    throw new Exception(output)
                }
            }
        } catch (Exception exc) {
            throw new Exception(exc)
        } finally {
            conn.disconnect()
        }

    }

    Methods.logActionUser(Constants.ACTION_GRAVAGE + " : " + BundleUtils.getTranslation(Constants.MAP_ACTION_GRAVAGE.get(data.get("DATA_MODE_EXPORT").toString())), labelFilter, Integer.toString(listDocumentsExport.size()), userContext.getUser().getLogin(), data.get("DATA_NSS").toString())
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_export_tribunaux_inprocess"))

    Utils.getSearchResultTableController().refreshAndKeepFilter()


} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFForEngravingSimpleViewExec - ERREUR : ", e)
    return
} finally {
    if (fileInputStream != null) {
        try {
            fileInputStream.close()
        } catch (Exception e) {
            scriptLogger.warn("[CUSTOM ACTION] - ExportPDFForEngravingSimpleViewExec - ATTENTION - Fichier XML de configuration non cloturé : ", e)
        }
    }
}

scriptLogger.debug("[CUSTOM ACTION] - EXPORT FOR ENGRAVING SIMPLE VIEW EXEC - END")

class GravageOAI {

    GravageOAI(@JsonProperty("ids") List<Integer> ids, @JsonProperty("nomFichier") String nomFichier, @JsonProperty("xmlDocument") org.w3c.dom.Document xmlDocument, @JsonProperty("indexDateEmissions") String indexDateEmissions, @JsonProperty("data") Map<Object, Object> data, @JsonProperty("traduction") Properties traduction) {
        this.ids = ids
        this.nomFichier = nomFichier
        this.indexDateEmissions = indexDateEmissions
        this.xmlDocument = xmlDocument
        this.data = data
        this.traduction = traduction

    }
    @JsonProperty("ids")
    private List<Integer> ids
    @JsonProperty("nomFichier")
    private String nomFichier
    @JsonProperty("xmlDocument")
    private org.w3c.dom.Document xmlDocument
    @JsonProperty("indexDateEmissions")
    private String indexDateEmissions
    @JsonProperty("data")
    private Map<Object, Object> data
    @JsonProperty("traduction")
    private Properties traduction

    List<Integer> getIds() {
        return ids
    }

    void setIds(List<Integer> ids) {
        this.ids = ids
    }

    String getNomFichier() {
        return nomFichier
    }

    void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier
    }

    Document getXmlDocument() {
        return xmlDocument
    }

    void setXmlDocument(org.w3c.dom.Document xmlDocument) {
        this.xmlDocument = xmlDocument
    }

    String getIndexDateEmissions() {
        return indexDateEmissions
    }

    void setIndexDateEmissions(String indexDateEmissions) {
        this.indexDateEmissions = indexDateEmissions
    }

    Map<Object, Object> getData() {
        return data
    }

    void setData(Map<Object, Object> data) {
        this.data = data
    }

    Properties getTraduction() {
        return traduction
    }

    void setTraduction(Properties traduction) {
        this.traduction = traduction
    }

}