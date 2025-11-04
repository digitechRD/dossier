import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.resources.BundleUtils
import org.w3c.dom.Document

import javax.faces.model.SelectItem
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat

/*************************************************************************************************
 * 							   Export document pour gravage - INIT
 **************************************************************************************************
 Date : 29.01.2016
 Auteur : MTO

 Description : Permet l'export de document pour gravage sur CD via des filtres
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF FOR ENGRAVING SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/

CustomActionController customActionController = null
Map<String, Object> data = null
List<SelectItem> filter = new ArrayList<SelectItem>()
FileInputStream file = null
String request = null
String login = null
DocumentBuilderFactory builderFactory = null
DocumentBuilder builder = null
Document xmlDocument = null
List<SelectItem> filtersList = null
String langues = null
try {
    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()

    // Chargement du XML Configuration
    file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
    builderFactory = DocumentBuilderFactory.newInstance()
    builder = builderFactory.newDocumentBuilder()
    xmlDocument = builder.parse(file)

    request = userContext.getCurrentSearchModel().getRequest()
    login = userContext.getLoggedUser().getLogin()

}catch(Exception e) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFEngravingSimpleViewInit - ERREUR : ", e)
    return
}

/**
 * TRAITEMENT
 **************************************************************************************************/

try{
    if (request.contains(Constants.FIELD_NSS_CODE+"=\"")) {
        String nss = request.replace(Constants.FIELD_NSS_CODE + "=\"", "")
        int index = nss.indexOf("\"")
        nss = nss.substring(0, index).replace("(", "").replace(")", "")
        String password = Methods.generateRandomPassword()

        data.put("DATA_PASSWORD", password)
        data.put("DATA_PASSWORD_ADMIN", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_BURNS_PASSWORD_ADMINISTRATOR))
        data.put("DATA_PASSWORD_OWNER", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_BURNS_PASSWORD_OWNER))
        //data.put("DATA_FILE", new Scanner(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH)).useDelimiter("\\Z").next());
        data.put("DATA_FILE", new String(Files.readAllBytes(Paths.get(Constants.XML_ACTIONS_CONFIGURATION_PATH))))
        data.put("DATA_NSS", Methods.formatString(nss, Constants.NSS_MASK))
        data.put("DATA_BEGIN_DATE", "01.01.0100")
        if (Constants.DATE_EXPORT_END_DEFAULT != null) {
            data.put("DATA_END_DATE", Constants.DATE_EXPORT_END_DEFAULT)
        } else data.put("DATA_END_DATE", new SimpleDateFormat(Constants.DATE_FORMAT_SWISS).format(new Date()))
        data.put("DATA_BURNS", Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_BURNS_TITLE))
        data.put("DATA_FILTER_FIELD", Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_FIELD))
        String resultatNom = Methods.getNameInWSWebAI(nss)
        data.put("DATA_NAME", (resultatNom.isEmpty()) ? "" : resultatNom)
        data.put("DATA_CREATOR", "")
        data.put("DATA_ENGRAVER_TYPE", "")
        data.put("DATA_PATH_PDF", "")
        data.put("DATA_PATH_JDF", "")
        data.put("DATA_PATH_DAT", "")
        data.put("DATA_IS_FOLDER", "")
        data.put("DATA_FILTER", "")
        data.put("DATA_BURN", "")
        data.put("DATA_UNDER_FILTRES", "")
        data.put("DATA_LANGUE", "")
        data.put("DATA_MODE_EXPORT", "")
        data.put("DATA_EXPORT_PATH", Constants.APPLICATION_EXPORT_FOLDER)
        data.put("DATA_BURN_FOLDER", Constants.APPLICATION_BURN_FOLDER)
        data.put("DATA_BURN_TYPE", Constants.APPLICATION_BURN_TYPE)
        data.put("DATA_BURN_FILE", Constants.FILE_EXPORT_BURN)
        data.put("DATA_NSS_MASK", Constants.NSS_MASK)
        data.put("DATA_BURN_REQUEST_SUMMARY", Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_SUMMARY)
        data.put("DATA_STAKEHOLDERS", Methods.getStakeholdersInWSWebAI(nss))
        data.put("DATA_STAKEHOLDER", "")
        data.put("DATA_OBJECT", "")
        data.put("DATA_SENDER_MAIL", userContext.getUser().getEmail())
        data.put("DATA_SENDER_NAME", userContext.getUser().getFirstName() + " " + userContext.getUser().getName())

        langues = Constants.XML_ACTIONS_EXPORT_DEFAULT_LANGUAGE
        List<SelectItem> result = new ArrayList()
        for (String languageCode : langues.split(";")) {
            String[] language = languageCode.split("-")
            result.add(new SelectItem(language[0], language[1]))
        }
        data.put("DATA_LANGUES", result)


        List<SelectItem> modeExport = new ArrayList()
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_GRAVAGE_NORMAL_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_GRAVAGE_NORMAL_LBELLE)))
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_GRAVAGE_BORDEREAU_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_GRAVAGE_BORDEREAU_LIBELLE)))
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_GRAVAGE_VISUALISATION_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_GRAVAGE_VISUALISATION_LIBELLE)))
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_BORDEREAU_VISUALISATION_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_BORDEREAU_VISUALISATION_LIBELLE)))
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_GRAVAGE_VISUALISATION_BORDEREAU_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_GRAVAGE_VISUALISATION_BORDEREAU_LIBELLE)))
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_EMAIL_GRAVAGE_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_EMAIL_GRAVAGE_LIBELLE)))
        modeExport.add(new SelectItem(Constants.MODE_EXPORT_EMAIL_ID, BundleUtils.getTranslation(Constants.MODE_EXPORT_EMAIL_LIBELLE)))
        data.put("DATA_MODES_EXPORT", modeExport)

        if (Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().size() > 0)
            filtersList = Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_BURNS_FILTRES_TITLE_ALL)
        else filtersList = Methods.getContentsListOfSelectItem(xmlDocument, Constants.XML_ACTIONS_REQUEST_BURNS_FILTRES_TITLE_WITHOUT_SELECTED_DOCUMENTS)
        data.put("DATA_FILTERS", filtersList)
    }else {
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_export_not_search"), false)
        return
    }

    scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF FOR ENGRAVING SIMPLE VIEW INIT - END")
}catch(Exception e) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFEngravingSimpleViewInit - ERREUR : ", e)
    return
}finally{
    if(file != null) {
        try{
            file.close()
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewInit - ATTENTION - Fichier XML de configuration non cloturé : ", e)
        }
    }
}