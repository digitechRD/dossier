import AttachmentGED
import com.digitech.common.lib.model.impl.PageInterval
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel
import com.digitech.dossier.common.model.backing.clipboard.ClipboardAttachmentPages
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.PrimaryDocument
import com.fasterxml.jackson.databind.ObjectMapper
import com.lowagie.text.pdf.PdfReader

import java.util.stream.Collectors

/*************************************************************************************************
 * 							Edition de la pièce jointe du document - EXEC
 **************************************************************************************************
 Date : 21.10.2016
 Auteur : MTO

 Description : Permet d'éditer la pièce jointe du document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EDIT ATTACHMENT DOCUMENT SIMPLE VIEW EXEC - START")

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
    result = output.getValue()
    result.setMessageSummary(BundleUtils.getTranslation("groovy_edit_attachment_document_action"))
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : ", e)
    return
}


/**
 * TRAITEMENT
 **************************************************************************************************/

try {

    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EDIT PJ DOCUMENT SIMPLE VIEW EXEC - END")
        return
    }

    List<Integer> listPagesToKeep = new ArrayList<Integer>()
    String listPagesToDelete = null
    document = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)
    Document doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
    PrimaryDocument primaryDocument = doc.getAllPrimaryDocList().get(0)


    // Suppresion de page
    if ("0".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
        Map<Integer, Boolean> pagesCheckedMap = (Map<Integer, Boolean>) data.get("DATA_PAGES_DELETE_CHECKED")

        // Vérification de la sélection des pages
        if (!pagesCheckedMap.containsValue(true)) {
            scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Aucune page sélectionnée : " + pagesCheckedMap.toString())
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_no_page_selected"))
            return
        } else if (!pagesCheckedMap.containsValue(false)) {
            scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Toutes les pages sont sélectionnées : " + pagesCheckedMap.toString())
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_all_pages_selected"))
            return
        } else {
            for (int i = 1; i <= Integer.parseInt(String.valueOf(data.get("DATA_PAGES_NUMBER"))); i++) {
                if (!pagesCheckedMap.get(i)) listPagesToKeep.add(i)
                else {
                    if (listPagesToDelete == null) listPagesToDelete = String.valueOf(i)
                    else listPagesToDelete += ", " + String.valueOf(i)
                }
            }

            if (listPagesToKeep.size() == 0) {
                scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Toutes les pages sont sélectionnées : " + listPagesToKeep.size() + " page restante")
                result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_all_pages_selected"))
                return
            }

            AttachmentGED attachmentGED = new AttachmentGED("deletePages", document.getAirsRefId(), Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE),
                    Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(doc, Constants.LIST_WK_STATUT_CODE),
                    Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRE_CODE),
                    Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_CODE),
                    Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
                    Methods.getFieldValue(doc, Constants.FIELD_CREATEUR_CODE),
                    listPagesToDelete, "", null, "")
            ObjectMapper mapper = new ObjectMapper()
            String jsonString = mapper.writeValueAsString(attachmentGED)

            try {
                String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Attachment")
                if (outputResFromAPI.contains("200")) {
                    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
                } else {
                    scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
                    if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                    else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                    isInError = true
                }

            } catch (Exception ex) {
                scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
                if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                isInError = true
            }

            if (errorDocuments == null) {
                Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("groovy_edit_attachment_history_delete_page") + " : " + listPagesToDelete)
            }
            Utils.getSimpleViewAttachmentController().nextDocument()
            Utils.getSimpleViewAttachmentController().previousDocument()
            Utils.getAttachmentController().getModel().refreshDocument()
        }


    }
    // Copier
    else if ("1".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {

        IAttachment attachment = document.getAttachments(new UserCoreContext(DossierCoreContext.getAdminJeton())).get(0)
        AttachmentModel attachmentModel = new AttachmentModel()
        attachmentModel.setCurrentAttachment(attachment)
        attachmentModel.setCurrentDocument(document)
        scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Copier du document : " + document.getAirsRefId())
        String nameCopy = String.valueOf(data.get("DATA_PAGES_COPY_NAME")) + " (" + String.valueOf(data.get("DATA_PAGES_COPY_START_PAGE")) + " - " + String.valueOf(data.get("DATA_PAGES_COPY_END_PAGE")) + ")"
        Utils.getClipboardController().getModel().addAttachmentPages(attachmentModel, nameCopy, new PageInterval(Integer.valueOf(String.valueOf(data.get("DATA_PAGES_COPY_START_PAGE"))), Integer.valueOf(String.valueOf(data.get("DATA_PAGES_COPY_END_PAGE")))))
        Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("groovy_edit_attachment_document_copy_page") + " : " + " (" + String.valueOf(data.get("DATA_PAGES_COPY_START_PAGE")) + " - " + String.valueOf(data.get("DATA_PAGES_COPY_END_PAGE")) + ")")
    }
    // Insérer
    else if ("2".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
        try {
            ClipboardAttachmentPages clipboardAttachmentPages = null
            PdfReader pdfReader = null
            scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - NAME : " + data.get("DATA_PAGES_INSERT_NAME"))
            for (int i = 0; i < Utils.getClipboardController().getModel().getClipboardObjectTableModel().getRowCount(); i++) {
                if (String.valueOf(data.get("DATA_PAGES_INSERT_NAME")).equalsIgnoreCase(String.valueOf(Utils.getClipboardController().getModel().getClipboardObject(i).getId()))) {
                    clipboardAttachmentPages = (ClipboardAttachmentPages) Utils.getClipboardController().getModel().getClipboardObject(i)
                }
            }

            if (clipboardAttachmentPages != null) {
                if (!clipboardAttachmentPages.isConsistent()) {
                    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : Consistent " + clipboardAttachmentPages.isConsistent())
                    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
                    return
                }
                IDocument documentClipboard = clipboardAttachmentPages.getDocument()
                IAttachment attachmentToInsert = clipboardAttachmentPages.getAttachment()

                File fileToInsert = Methods.getDocumentMgr().loadDocumentAttachment(new UserCoreContext(DossierCoreContext.getAdminJeton()), documentClipboard, attachmentToInsert, null, null)
                int insertionPage = Integer.parseInt(String.valueOf(data.get("DATA_PAGES_INSERT_START_PAGE")))
                PageInterval pageInterval = clipboardAttachmentPages.getPageInterval()
                int pageDebut = Integer.valueOf(pageInterval.getFirst().intValue())
                int pageFin = Integer.valueOf(pageInterval.getLast().intValue())

                if (fileToInsert != null) {
                    FileInputStream fl = new FileInputStream(fileToInsert)
                    byte[] arr = new byte[(int) fileToInsert.length()]

                    fl.read(arr)
                    fl.close()
                    scriptLogger.debug("->" + arr)

                    AttachmentGED attachmentGED = new AttachmentGED("insertPages", document.getAirsRefId(), Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE),
                            Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(doc, Constants.LIST_WK_STATUT_CODE),
                            Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRE_CODE),
                            Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_CODE),
                            Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
                            Methods.getFieldValue(doc, Constants.FIELD_CREATEUR_CODE),
                            String.valueOf(data.get("DATA_PAGES_INSERT_START_PAGE")), "",
                            arr,
                            pageDebut.toString() + "-" + pageFin.toString())
                    ObjectMapper mapper = new ObjectMapper()
                    String jsonString = mapper.writeValueAsString(attachmentGED)
                    try {
                        String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Attachment")
                        if (outputResFromAPI.contains("200")) {
                            result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
                            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
                        } else {
                            scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
                            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                            isInError = true
                        }

                    } catch (Exception ex) {
                        scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
                        if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                        else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                        isInError = true
                    }

                    Utils.getClipboardController().getModel().removeAllClipboardObjects()

                    Utils.getSimpleViewAttachmentController().nextDocument()
                    Utils.getSimpleViewAttachmentController().previousDocument()
                    Utils.getAttachmentController().getModel().refreshDocument()


                } else {
                    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + fileToInsert)
                    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
                    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
                    return
                }
            } else {
                scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + clipboardAttachmentPages)
                result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_no_clipboard"))
                return
            }
        } catch (Exception e) {
            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
        }
    // Rotation
    }else if("3".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
        Map<Integer, Boolean> pagesCheckedMap = (Map<Integer, Boolean>) data.get("DATA_PAGES_ROTATION_CHECKED")
        List<Integer> listPagesToRotation = new ArrayList<Integer>()

        // Vérification de la sélection des pages
        if (!pagesCheckedMap.containsValue(true)) {
            scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Aucune page sélectionnée")
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_no_page_selected"))
            return
        } else {
            for (int i = 1; i <= Integer.parseInt(String.valueOf(data.get("DATA_PAGES_NUMBER"))); i++) {
                if (pagesCheckedMap.get(i)) listPagesToRotation.add(i)
            }
        }
        AttachmentGED attachmentGED = new AttachmentGED("rotationPages", document.getAirsRefId(), Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE),
                Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(doc, Constants.LIST_WK_STATUT_CODE),
                Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRE_CODE),
                Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_CODE),
                Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
                Methods.getFieldValue(doc, Constants.FIELD_CREATEUR_CODE),
                listPagesToRotation.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",", "", "")),
                data.get("DATA_PAGES_ROTATION_SELECTED").toString(), null, "")
        ObjectMapper mapper = new ObjectMapper()
        String jsonString = mapper.writeValueAsString(attachmentGED)
        scriptLogger.debug(listPagesToRotation.size() + " / " + listPagesToKeep.size() + " / " + data.get("DATA_PAGES_NUMBER") + " / " + data.get("DATA_PAGES_ROTATION_SELECTED"))
        try {
            String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Attachment")
            if (outputResFromAPI.contains("200")) {
                result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
                result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
            } else {
                scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
                if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                isInError = true
            }
        } catch (Exception ex) {
            scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
            isInError = true
        }

        if (errorDocuments == null) {
            Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("jsp_pages_rotation") + " : " + listPagesToRotation.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",", "", "")) + " - " + data.get("DATA_PAGES_ROTATION_SELECTED") + "°")
        }
        Utils.getSimpleViewAttachmentController().nextDocument()
        Utils.getSimpleViewAttachmentController().previousDocument()
        Utils.getAttachmentController().getModel().refreshDocument()
        // Déplacer pages
    }else if("4".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))){
        try{
            if (data.get("DATA_LIST_MOVE_PAGES") != null) {
                List<String> orderOfPages = (List) data.get("DATA_LIST_MOVE_PAGES")


                AttachmentGED attachmentGED = new AttachmentGED("movePages", document.getAirsRefId(), Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE),
                        Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE), Methods.getFieldValue(doc, Constants.LIST_WK_STATUT_CODE),
                        Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRE_CODE),
                        Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_CODE),
                        Methods.getFieldValue(doc, Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE),
                        Methods.getFieldValue(doc, Constants.FIELD_CREATEUR_CODE),
                        String.join(",", orderOfPages),
                        "", null, "")
                ObjectMapper mapper = new ObjectMapper()
                String jsonString = mapper.writeValueAsString(attachmentGED)
                try {
                    String outputResFromAPI = Methods.sendRequestAPI(jsonString, "Attachment")
                    if (outputResFromAPI.contains("200")) {
                        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
                        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
                    } else {
                        scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + outputResFromAPI)
                        if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                        else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                        isInError = true
                    }
                } catch (Exception ex) {
                    scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : - Error 500 from DigitalAPI. JSON sended : " + jsonString)
                    if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                    else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_postman_no_actor") + ")"
                    isInError = true
                }

                if (errorDocuments == null) {
                    Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("jsp_pages_move"))
                }

                Utils.getSimpleViewAttachmentController().nextDocument()
                Utils.getSimpleViewAttachmentController().previousDocument()
                Utils.getAttachmentController().getModel().refreshDocument()
            } else {
                if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
                else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
                scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " - Format de la pièce jointe invalide : " + primaryDocument.getFileName())
            }
        } catch (Exception e) {
            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
        }
    // Restaurer une version
    }else if("5".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))){
        if(data.get("DATA_VERSION_ATTACHMENT") != null){
            int primaryDocumentId
            try {
                PrimaryDocument primaryDocumentRecycled = null
                primaryDocumentId = Integer.parseInt(data.get("DATA_VERSION_ATTACHMENT").toString())
                for (PrimaryDocument p : doc.getPrimaryDocumentVersions(primaryDocument)) {
                    if (primaryDocumentId == p.getId()) {
                        primaryDocumentRecycled = p
                        break
                    }
                }
                if (primaryDocumentRecycled != null) {
                    File folderDest = new File(ExportUtils.getExportPDFDirectory() + new Date().getTime())
                    folderDest.mkdirs()
                    primaryDocumentRecycled.download(folderDest.getAbsolutePath())

                    PrimaryDocument primaryDocumentDest = new PrimaryDocument(primaryDocumentRecycled.getFileName(), primaryDocumentRecycled.getLabel())
                    primaryDocumentDest.setType("ORIGINAL")
                    doc.addPrimaryDocumentVersion(primaryDocument, primaryDocumentDest, folderDest.getAbsolutePath())


                    String numberPage = ""
                    PdfReader pdfReader = null
                    pdfReader = new PdfReader(folderDest.getAbsolutePath() + "/" + primaryDocumentRecycled.getFileName())
                    numberPage = String.valueOf(pdfReader.getNumberOfPages())
                    pdfReader.close()

                    Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, numberPage)
                    doc.updateContent()


                    folderDest.delete()

                    Utils.getSimpleViewAttachmentController().nextDocument()
                    Utils.getSimpleViewAttachmentController().previousDocument()
                    Utils.getAttachmentController().getModel().refreshDocument()
                }else {
                    if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
                    else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
                    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + doc.getId() + " : Version non récupérée")
                }
            }catch(Exception e) {
                if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
                else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
                scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + doc.getId() + " : ", e)
            }

            if (errorDocuments == null) {
                Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("jsp_recycle_version") + " n°" + primaryDocumentId)
            }
        }else {
            if (errorDocuments == null) errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            else errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + doc.getId() + " - Version sélectionnée est null")
        }

    }else {
        scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : Statut --> " + data.get("DATA_STATUS").toString())
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_error_status"))
        return
    }

    if(errorDocuments == null) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
    }else {
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments)
    }

}catch(Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : ", e)
    return
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT ATTACHMENT DOCUMENT SIMPLE VIEW EXEC - END")