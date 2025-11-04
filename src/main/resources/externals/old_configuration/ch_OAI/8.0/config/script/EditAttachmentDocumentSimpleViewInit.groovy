import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.PrimaryDocument
import org.apache.pdfbox.pdmodel.PDDocument

import javax.faces.model.SelectItem

/*************************************************************************************************
 *							Edition de la pièce jointe du document - INIT
 **************************************************************************************************
 Date : 21.10.2016
 Auteur : MTO

 Description : Permet d'éditer la pièce jointe du document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EDIT ATTACHMENT DOCUMENT SIMPLE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
IDocument document = null
Map<String, Object> data = null
CustomActionController customActionController = null
String errorDocuments = null

try {
    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_init_error"))
    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewInit - ERREUR : ", e)
    return
}


/**
 * TRAITEMENT
 **************************************************************************************************/

try {
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
    Integer numberPages = 0

    if (docs.size() == 0) {
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false)
        return
    } else if (docs.size() > 1) {
        Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_one_document_only"), false)
        return
    }

    document = docs.get(0)
    PrimaryDocument primaryDocument = document.getAirsDocument().getInnerDocument().getPrimaryDocList().get(0)
    HashMap<Integer, String> pagesMap = new HashMap<Integer, String>()
    HashMap<Integer, Boolean> pagesCheckedMapForDeleted = new HashMap<Integer, Boolean>()
    HashMap<Integer, Boolean> pagesCheckedMapForRotation = new HashMap<Integer, Boolean>()
    List<Integer> pageId = new ArrayList<Integer>()
    List<SelectItem> pagesListSelectItemForCopy = new ArrayList<SelectItem>()
    List<SelectItem> pagesListSelectForInsert = new ArrayList<SelectItem>()
    List<SelectItem> pagesNameSelectItem = new ArrayList<SelectItem>()
    List<SelectItem> versionsListSelectItem = new ArrayList<SelectItem>()
    String pagesMapString = ""
    if (document.getAirsDocument().getInnerDocument().getPrimaryDocList().isEmpty()) Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_edit_attachment_empty"), false)
    else if (!StringUtils.isExtensionIgnoreCase(primaryDocument.getFileName(), Constants.APPLICATION_PDF_EXTENSION)) Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_edit_attachment_no_pdf"), false)
    else {
        Document doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
        PDDocument pddocument = null
        if (Methods.getFieldValue(doc, Constants.FIELD_NUMBER_PAGES_CODE) == null) {
            if (!new File(ExportUtils.getExportPDFDirectory()).exists()) new File(ExportUtils.getExportPDFDirectory()).mkdirs()
            File file = doc.getPrimaryDocument(primaryDocument, ExportUtils.getExportPDFDirectory())

            if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_PDF_EXTENSION)) {
                pddocument = PDDocument.load(file)
                numberPages = pddocument.getNumberOfPages()
            }
        } else numberPages = Integer.parseInt(Methods.getFieldValue(doc, Constants.FIELD_NUMBER_PAGES_CODE))

        pagesListSelectForInsert.add(new SelectItem(0, BundleUtils.getTranslation("groovy_pages") + "0"))
        for(int i=1; i<=numberPages; i++) {
            pageId.add(i)
            pagesCheckedMapForDeleted.put(i, false)
            pagesCheckedMapForRotation.put(i, false)
            pagesMap.put(i, BundleUtils.getTranslation("groovy_pages") + String.valueOf(i))
            pagesListSelectItemForCopy.add(new SelectItem(i, BundleUtils.getTranslation("groovy_pages") + String.valueOf(i)))
            pagesListSelectForInsert.add(new SelectItem(i, BundleUtils.getTranslation("groovy_pages") + String.valueOf(i)))
            pagesMapString += i + "::" + BundleUtils.getTranslation("groovy_pages") + String.valueOf(i) + ";"
        }


        // Récupération des éléments copiés
        if (Utils.getClipboardController().getModel().getClipboardObjectTableModel().getRowCount() > 0) {
            pagesNameSelectItem.add(new SelectItem("", ""))
            for (int i = 0; i < Utils.getClipboardController().getModel().getClipboardObjectTableModel().getRowCount(); i++) {
                scriptLogger.debug("ID : " + Utils.getClipboardController().getModel().getClipboardObject(i).getId() + " - " + Utils.getClipboardController().getModel().getClipboardObject(i).getIndex())
                pagesNameSelectItem.add(new SelectItem(Utils.getClipboardController().getModel().getClipboardObject(i).getId(), Utils.getClipboardController().getModel().getClipboardObject(i).getTitle()))
            }
        }

        // Création des la liste des actions
        List<SelectItem> statusList = new ArrayList<SelectItem>()
        if (numberPages > 1 && Methods.getRightMgr().hasAirsRight(userContext, Constants.RIGHT_DELETE_PAGE)) {
            data.put("DATA_STATUS", "0")
            statusList.add(new SelectItem("0", BundleUtils.getTranslation("groovy_edit_attachment_document_delete_page")))
        }

        if (Methods.getRightMgr().hasAirsRight(userContext, Constants.RIGHT_COPY_PAGE)) {
            if (statusList.isEmpty()) data.put("DATA_STATUS", "1")
            statusList.add(new SelectItem("1", BundleUtils.getTranslation("groovy_edit_attachment_document_copy_page")))
        }

        if (!pagesNameSelectItem.isEmpty() && Methods.getRightMgr().hasAirsRight(userContext, Constants.RIGHT_ADD_PAGE)) {
            if (statusList.isEmpty()) data.put("DATA_STATUS", "2")
            statusList.add(new SelectItem("2", BundleUtils.getTranslation("groovy_edit_attachment_document_insert_page")))
        }

        statusList.add(new SelectItem("3", BundleUtils.getTranslation("groovy_edit_attachment_document_rotation_page")))
        List<SelectItem> rotationsList = new ArrayList()
        rotationsList.add(new SelectItem(90, BundleUtils.getTranslation("groovy_edit_attachment_rotation_90")))
        rotationsList.add(new SelectItem(180, BundleUtils.getTranslation("groovy_edit_attachment_rotation_180")))
        rotationsList.add(new SelectItem(270, BundleUtils.getTranslation("groovy_edit_attachment_rotation_270")))

        List<String> pagesListMenu = new ArrayList()
        if (numberPages > 1) {
            statusList.add(new SelectItem("4", BundleUtils.getTranslation("groovy_edit_attachment_document_order_page")))
            for (int i = 1; i <= numberPages; i++) {
                pagesListMenu.add(BundleUtils.getTranslation("xml_configuration_label_page") + " " + i)
            }
        }
        if (statusList.isEmpty()) {
            Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_edit_attachment_no_right"), false)
            return
        }

        List<PrimaryDocument> primaryDocumentList = doc.getPrimaryDocumentVersions(primaryDocument)
        if (primaryDocumentList.size() > 0) {
            statusList.add(new SelectItem("5", BundleUtils.getTranslation("groovy_edit_attachment_recycle_version")))
            int numberOfVersion = 0
            for (PrimaryDocument p : primaryDocumentList) {
                numberOfVersion++
                versionsListSelectItem.add(new SelectItem(p.getId(), numberOfVersion + " - " + p.getDate()))
            }
        }

        // Liste des variables
        data.put("DATA_PAGES_DELETE_ID", pageId)
        data.put("DATA_PAGES_DELETE_NUMBER_LIST", pagesMap)
        data.put("DATA_PAGES_DELETE_CHECKED", pagesCheckedMapForDeleted)

        data.put("DATA_PAGES_COPY_NAME", document.getAirsDocument().getInnerDocument().getPrimaryDocList().get(0).getFileName())
        data.put("DATA_PAGES_COPY_START_PAGE", "1")
        data.put("DATA_PAGES_COPY_START_LIST", pagesListSelectItemForCopy)
        data.put("DATA_PAGES_COPY_END_PAGE", "1")
        data.put("DATA_PAGES_COPY_END_LIST", pagesListSelectItemForCopy)
        data.put("DATA_PAGES_COPY_LIST", pagesMapString)

        data.put("DATA_PAGES_INSERT_START_PAGE", "0")
        data.put("DATA_PAGES_INSERT_START_LIST", pagesListSelectForInsert)
        data.put("DATA_PAGES_INSERT_NAME_LIST", pagesNameSelectItem)
        data.put("DATA_PAGES_INSERT_NAME", "")

        data.put("DATA_PAGES_ROTATION_ID", pageId)
        data.put("DATA_PAGES_ROTATION_LIST", rotationsList)
        data.put("DATA_PAGES_ROTATION_SELECTED", 90)
        data.put("DATA_PAGES_ROTATION_NUMBER_LIST", pagesMap)
        data.put("DATA_PAGES_ROTATION_CHECKED", pagesCheckedMapForRotation)

        data.put("DATA_PAGES_NUMBER", numberPages)
        data.put("DATA_LIST_STATUS", statusList)

        data.put("DATA_LIST_MOVE_PAGES", pagesListMenu)

        data.put("DATA_VERSIONS_ATTACHMENT_LIST", versionsListSelectItem)
        if (!versionsListSelectItem.isEmpty()) data.put("DATA_VERSION_ATTACHMENT", versionsListSelectItem.get(0).getValue())

    }

}catch(Exception e) {
    Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false)
    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewInit - ERREUR : ", e)
    return
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT ATTACHMENT DOCUMENT SIMPLE VIEW INIT - END")