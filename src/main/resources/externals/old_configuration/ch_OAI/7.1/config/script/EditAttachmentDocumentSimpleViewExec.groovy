import com.digitech.common.lib.model.impl.PageInterval
import com.digitech.common.lib.utils.FileUtils
import com.digitech.common.lib.utils.StringUtils
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
import com.digitech.toolbox.document.model.IEnumOperationParameter
import com.digitech.toolbox.document.model.impl.EnumPDFOperationParameter
import com.digitech.toolbox.document.service.impl.OperationService
import com.lowagie.text.pdf.*

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
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : ", e)
  return
}


/**
 * TRAITEMENT
 **************************************************************************************************/

try {

  if(data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
    scriptLogger.debug("[CUSTOM ACTION] - EDIT PJ DOCUMENT SIMPLE VIEW EXEC - END")
    return
  }

  List<Integer> listPagesToKeep = new ArrayList<Integer>()
  String listPagesToDelete = null
  document = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments().get(0)
  Document doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())
  PrimaryDocument primaryDocument = doc.getAllPrimaryDocList().get(0)

  // Suppresion de page
  if("0".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
    Map<Integer, Boolean> pagesCheckedMap = (Map<Integer, Boolean>) data.get("DATA_PAGES_DELETE_CHECKED")

    // Vérification de la sélection des pages
    if(!pagesCheckedMap.containsValue(true)) {
      scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Aucune page sélectionnée : " + pagesCheckedMap.toString())
      result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
      result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_no_page_selected"))
      return
    }
    else if(!pagesCheckedMap.containsValue(false)) {
      scriptLogger.
          warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Toutes les pages sont sélectionnées : " + pagesCheckedMap.toString())
      result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
      result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_all_pages_selected"))
      return
    }
    else {
      for(int i = 1; i <= Integer.parseInt(String.valueOf(data.get("DATA_PAGES_NUMBER"))); i++) {
        if(!pagesCheckedMap.get(i))
          listPagesToKeep.add(i)
        else {
          if(listPagesToDelete == null)
            listPagesToDelete = String.valueOf(i)
          else
            listPagesToDelete += ", " + String.valueOf(i)
        }
      }

      if(listPagesToKeep.size() == 0) {
        scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Toutes les pages sont sélectionnées : " +
                              listPagesToKeep.size() + " page restante")
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_all_pages_selected"))
        return
      }
    }

    try {
      PdfReader pdfReader = null
      if(primaryDocument != null) {
        if(StringUtils.isExtensionIgnoreCase(primaryDocument.getFileName(), Constants.APPLICATION_PDF_EXTENSION)) {
          String numberPage = null
          File folder = new File(ExportUtils.getExportPDFDirectory() + new Date().getTime())
          if(!folder.exists()) {
            folder.mkdirs()
          }
          File file = doc.getLinkedPrimaryDocuments(primaryDocument, "ORIGINAL").get(0).download(folder.getAbsolutePath())
          File folderDest = new File(file.getParentFile().getAbsolutePath() + File.separator + "work")
          folderDest.mkdirs()

          //String filename ="_"+new SimpleDateFormat("yyyyMMddhhmmsss").format(new Date())+Constants.APPLICATION_PDF_EXTENSION.toLowerCase();
          //String dest2 = folderDest.getAbsolutePath() + File.separator + file.getName();
          String dest1 = folderDest.getAbsolutePath() + File.separator + file.getName()
          pdfReader = new PdfReader(file.getAbsolutePath())
          pdfReader.selectPages(listPagesToKeep)
          numberPage = String.valueOf(pdfReader.getNumberOfPages())
          PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(dest1))
          stamper.close()
          pdfReader.close()

          try {
            //Methods.copyFile(new File(dest2), new File(dest1));
            //doc.addOrUpdatePrimaryDocument(primaryDocument, folderDest.getAbsolutePath());
            PrimaryDocument primaryDocumentDest = new PrimaryDocument(file.getName(), file.getName())
            primaryDocumentDest.setType("ORIGINAL")
            scriptLogger.debug(doc.getPrimaryDocumentVersions(primaryDocument).size() + " / " +
                                   primaryDocument.getFileName() + " / " + primaryDocumentDest.getFileName())
            doc.addPrimaryDocumentVersion(primaryDocument, primaryDocumentDest, folderDest.getAbsolutePath())
          } catch(Exception e) {
            /*scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Suppression page - Methode n°1 n'a pas fonctionnée : ",e.toString());
            PrimaryDocument primaryDocumentDest = new PrimaryDocument(filename, filename);
            doc.addPrimaryDocument(primaryDocumentDest, folderDest.getAbsolutePath());
            scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Suppression page - Nombre de primary document après insertion : " + doc.getAllPrimaryDocList().size());
            if(doc.getAllPrimaryDocList().size() == 2) {
                try {
                    doc.deletePrimaryDocument(primaryDocument);
                } catch (Exception ex) {*/
            scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Suppression page - Suppression de l'ancienne pièce jointe : ", e)
            if(errorDocuments == null)
              errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            else
              errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            /*}
        }*/
          }

          if(errorDocuments == null) {
            Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                                   BundleUtils.getTranslation("groovy_edit_attachment_history_delete_page") + " : " + listPagesToDelete)
            Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, numberPage)
            doc.updateContent()
          }

        }
        else {
          if(errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
          else
            errorDocuments += ", " +
                document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
          scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" +
                                 document.getAirsRefId() + " - Format de la pièce jointe invalide : " + primaryDocument.getFileName())
        }

        Utils.getSimpleViewAttachmentController().nextDocument()
        Utils.getSimpleViewAttachmentController().previousDocument()
        Utils.getAttachmentController().getModel().refreshDocument()

      }
      else {
        if(errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
        scriptLogger.
            error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " ne possède pas de pièce jointe")
      }

    } catch(Exception e) {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
    }
  }
  // Copier
  else if("1".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {

    IAttachment attachment = document.getAttachments(new UserCoreContext(DossierCoreContext.getAdminJeton())).get(0)
    AttachmentModel attachmentModel = new AttachmentModel()
    attachmentModel.setCurrentAttachment(attachment)
    attachmentModel.setCurrentDocument(document)
    scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Copier du document : " + document.getAirsRefId())
    String nameCopy = String.valueOf(data.get("DATA_PAGES_COPY_NAME")) + " (" + String.valueOf(data.get("DATA_PAGES_COPY_START_PAGE")) + " - " +
        String.valueOf(data.get("DATA_PAGES_COPY_END_PAGE")) + ")"
    Utils.getClipboardController().getModel().addAttachmentPages(attachmentModel, nameCopy, new PageInterval(Integer.valueOf(
        String.valueOf(data.get("DATA_PAGES_COPY_START_PAGE"))), Integer.valueOf(String.valueOf(data.get("DATA_PAGES_COPY_END_PAGE")))))
    Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation(
        "groovy_edit_attachment_document_copy_page") + " : " + " (" + String.valueOf(data.get("DATA_PAGES_COPY_START_PAGE")) + " - " + String.valueOf(
        data.get("DATA_PAGES_COPY_END_PAGE")) + ")")
  }
  // Insérer
  else if("2".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
    try {
      ClipboardAttachmentPages clipboardAttachmentPages = null
      PdfReader pdfReader = null
      scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - NAME : " + data.get("DATA_PAGES_INSERT_NAME"))
      for(int i = 0; i < Utils.getClipboardController().getModel().getClipboardObjectTableModel().getRowCount(); i++) {
        if(String.valueOf(data.get("DATA_PAGES_INSERT_NAME")).equalsIgnoreCase(String.valueOf(
            Utils.getClipboardController().getModel().getClipboardObject(i).getId()))) {
          clipboardAttachmentPages = (ClipboardAttachmentPages) Utils.getClipboardController().getModel().getClipboardObject(i)
        }
      }

      if(clipboardAttachmentPages != null) {
        if(!clipboardAttachmentPages.isConsistent()) {
          scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : Consistent " + clipboardAttachmentPages.isConsistent())
          result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
          result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
          return
        }
        IDocument documentClipboard = clipboardAttachmentPages.getDocument()
        IAttachment attachmentToInsert = clipboardAttachmentPages.getAttachment()

        File fileToInsert = Methods.getDocumentMgr().loadDocumentAttachment(new UserCoreContext(DossierCoreContext.getAdminJeton()), documentClipboard,
                                                                            attachmentToInsert, null, null)
        if(fileToInsert != null) {
          IAttachment attachment = document.getAttachments(new UserCoreContext(DossierCoreContext.getAdminJeton())).get(0)
          AttachmentModel attachmentModel = new AttachmentModel()
          attachmentModel.setCurrentAttachment(attachment)
          attachmentModel.setCurrentDocument(document)

          String attachmentFilePath = attachmentModel.getAttachmentFilePath()
          //File attachmentFile = new File(new UserCoreContext(DossierCoreContext.getAdminJeton()).getUserUploadPath() + File.separator + FilenameUtils.getName(attachmentFilePath));
          File attachmentFile = new File(
              new UserCoreContext(DossierCoreContext.getAdminJeton()).getUserUploadPath() + File.separator + doc.getPrimaryDocList().get(0).getFileName())
          FileUtils.copyFile(new File(attachmentFilePath), attachmentFile)

          Map<IEnumOperationParameter, Object> parameters = new HashMap()
          parameters.put(EnumPDFOperationParameter.CLEAN_OUTLINE, Boolean.TRUE)

          int insertionPage = Integer.parseInt(String.valueOf(data.get("DATA_PAGES_INSERT_START_PAGE")))
          PageInterval pageInterval = clipboardAttachmentPages.getPageInterval()
          PageInterval finalPageInterval = new PageInterval(Integer.valueOf(pageInterval.getFirst().intValue() - 1),
                                                            Integer.valueOf(pageInterval.getLast().intValue() - 1))
          new OperationService().insertPages(attachmentFile, insertionPage, fileToInsert, finalPageInterval, parameters)

          scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Insertion : " + String.valueOf(data.get("DATA_PAGES_INSERT_START_PAGE")))
          Integer nombrePage = Integer.valueOf(pageInterval.getLast().intValue() - 1) - Integer.valueOf(pageInterval.getFirst().intValue() - 1) + 1

          String numberPage = null
          pdfReader = new PdfReader(attachmentFile.getAbsolutePath())
          numberPage = String.valueOf(pdfReader.getNumberOfPages())
          pdfReader.close()

          PrimaryDocument primaryDocumentDest = new PrimaryDocument(attachmentFile.getName(), attachmentFile.getName())
          primaryDocumentDest.setType("ORIGINAL")
          doc.addPrimaryDocumentVersion(attachment.getAirsAttachment(), primaryDocumentDest, attachmentFile.getParentFile().getAbsolutePath())
          Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                                 BundleUtils.getTranslation("groovy_edit_attachment_history_insert_page") + " : " +
                                                     Integer.toString(nombrePage) + " - " +
                                                     BundleUtils.getTranslation("jsp_page_start") + " : " +
                                                     String.valueOf(data.get("DATA_PAGES_INSERT_START_PAGE")))
          Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, numberPage)
          doc.updateContent()

          Utils.getClipboardController().getModel().removeAllClipboardObjects()

          Utils.getSimpleViewAttachmentController().nextDocument()
          Utils.getSimpleViewAttachmentController().previousDocument()
          Utils.getAttachmentController().getModel().refreshDocument()

          attachmentFile.delete()
        }
        else {
          scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + fileToInsert)
          result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
          result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
          return
        }
      }
      else {
        scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : " + clipboardAttachmentPages)
        result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_no_clipboard"))
        return
      }
    } catch(Exception e) {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
    }
    // Rotation
  }
  else if("3".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
    Map<Integer, Boolean> pagesCheckedMap = (Map<Integer, Boolean>) data.get("DATA_PAGES_ROTATION_CHECKED")
    List<Integer> listPagesToRotation = new ArrayList<Integer>()

    // Vérification de la sélection des pages
    if(!pagesCheckedMap.containsValue(true)) {
      scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ATTENTION : Aucune page sélectionnée")
      result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
      result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_attachment_document_no_page_selected"))
      return
    }
    else {
      for(int i = 1; i <= Integer.parseInt(String.valueOf(data.get("DATA_PAGES_NUMBER"))); i++) {
        if(pagesCheckedMap.get(i))
          listPagesToRotation.add(i)
      }
    }

    scriptLogger.debug(
        listPagesToRotation.size() + " / " + listPagesToKeep.size() + " / " + data.get("DATA_PAGES_NUMBER") + " / " + data.get("DATA_PAGES_ROTATION_SELECTED"))

    try {
      PdfReader pdfReader = null
      if(primaryDocument != null) {
        if(StringUtils.isExtensionIgnoreCase(primaryDocument.getFileName(), Constants.APPLICATION_PDF_EXTENSION)) {
          String numberPage = null
          File folder = new File(ExportUtils.getExportPDFDirectory() + new Date().getTime())
          if(!folder.exists()) {
            folder.mkdirs()
          }
          File file = doc.getLinkedPrimaryDocuments(primaryDocument, "ORIGINAL").get(0).download(folder.getAbsolutePath())
          File folderDest = new File(file.getParentFile().getAbsolutePath() + File.separator + "work")
          folderDest.mkdirs()

          //String filename ="_"+new SimpleDateFormat("yyyyMMddhhmmsss").format(new Date())+Constants.APPLICATION_PDF_EXTENSION.toLowerCase();
          //String dest2 = folderDest.getAbsolutePath() + File.separator + file.getName();
          String dest1 = folderDest.getAbsolutePath() + File.separator + file.getName()
          pdfReader = new PdfReader(file.getAbsolutePath())
          String pageToTreat = ""

          for(Integer i : listPagesToRotation) {
            pageToTreat += Integer.toString(i) + ","

            scriptLogger.debug(i + " / " + data.get("DATA_PAGES_ROTATION_SELECTED"))

            PdfDictionary page = pdfReader.getPageN(i)
            PdfNumber rotate = page.getAsNumber(PdfName.ROTATE)
            if(rotate == null) {
              page.put(PdfName.ROTATE, new PdfNumber(Integer.parseInt(String.valueOf(data.get("DATA_PAGES_ROTATION_SELECTED")))))
            }
            else {
              page.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + Integer.parseInt(String.valueOf(data.get("DATA_PAGES_ROTATION_SELECTED")))) % 360))
            }
          }

          PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(dest1))
          stamper.close()
          pdfReader.close()

          try {
            //Methods.copyFile(new File(dest2), new File(dest1));
            //doc.addOrUpdatePrimaryDocument(primaryDocument, folderDest.getAbsolutePath());
            PrimaryDocument primaryDocumentDest = new PrimaryDocument(file.getName(), file.getName())
            primaryDocumentDest.setType("ORIGINAL")
            doc.addPrimaryDocumentVersion(primaryDocument, primaryDocumentDest, folderDest.getAbsolutePath())
          } catch(Exception e) {
            /*scriptLogger.warn("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Suppression page - Methode n°1 n'a pas fonctionnée : ",e.toString());
            PrimaryDocument primaryDocumentDest = new PrimaryDocument(filename, filename);
            doc.addPrimaryDocument(primaryDocumentDest, folderDest.getAbsolutePath());
            scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Suppression page - Nombre de primary document après insertion : " + doc.getAllPrimaryDocList().size());
            if(doc.getAllPrimaryDocList().size() == 2) {
                try {
                    doc.deletePrimaryDocument(primaryDocument);
                } catch (Exception ex) {*/
            scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Suppression page - Suppression de l'ancienne pièce jointe : ", e)
            if(errorDocuments == null)
              errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            else
              errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            /*}
        }*/
          }

          if(errorDocuments == null) {
            Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                                   BundleUtils.getTranslation("jsp_pages_rotation") + " : " +
                                                       pageToTreat.substring(0, pageToTreat.lastIndexOf(",")) + " - " +
                                                       data.get("DATA_PAGES_ROTATION_SELECTED") + "°")
          }

        }
        else {
          if(errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
          else
            errorDocuments += ", " +
                document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
          scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" +
                                 document.getAirsRefId() + " - Format de la pièce jointe invalide : " + primaryDocument.getFileName())
        }

        Utils.getSimpleViewAttachmentController().nextDocument()
        Utils.getSimpleViewAttachmentController().previousDocument()
        Utils.getAttachmentController().getModel().refreshDocument()

      }
      else {
        if(errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
        scriptLogger.
            error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " ne possède pas de pièce jointe")
      }

    } catch(Exception e) {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
    }
    // Déplacer pages
  }
  else if("4".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
    try {
      if(data.get("DATA_LIST_MOVE_PAGES") != null) {
        List<String> orderOfPages = (List) data.get("DATA_LIST_MOVE_PAGES")
        if(StringUtils.isExtensionIgnoreCase(primaryDocument.getFileName(), Constants.APPLICATION_PDF_EXTENSION)) {
          List<Integer> pagesNewOrder = new ArrayList()
          for(String order : orderOfPages) {
            pagesNewOrder.add(Integer.valueOf(order.replace(BundleUtils.getTranslation("xml_configuration_label_page") + " ", "")))
          }
          scriptLogger.debug("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - INFO - Nouvel ordre des pages : " + pagesNewOrder)
          File folder = new File(ExportUtils.getExportPDFDirectory() + new Date().getTime())
          if(!folder.exists()) {
            folder.mkdirs()
          }
          File file = doc.getLinkedPrimaryDocuments(primaryDocument, "ORIGINAL").get(0).download(folder.getAbsolutePath())
          File folderDest = new File(file.getParentFile().getAbsolutePath() + File.separator + "work")
          folderDest.mkdirs()
          String dest1 = folderDest.getAbsolutePath() + File.separator + file.getName()
          pdfReader = new PdfReader(file.getAbsolutePath())
          pdfReader.selectPages(pagesNewOrder)
          PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(dest1))
          stamper.close()
          pdfReader.close()

          try {
            PrimaryDocument primaryDocumentDest = new PrimaryDocument(file.getName(), file.getName())
            primaryDocumentDest.setType("ORIGINAL")
            doc.addPrimaryDocumentVersion(primaryDocument, primaryDocumentDest, folderDest.getAbsolutePath())
          } catch(Exception e) {
            scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - Déplacer page - Déplacement des pages : ", e)
            if(errorDocuments == null)
              errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
            else
              errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
          }

          if(errorDocuments == null) {
            Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT, BundleUtils.getTranslation("jsp_pages_move"))
          }
        }
        else {
          if(errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
          else
            errorDocuments += ", " +
                document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
          scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" +
                                 document.getAirsRefId() + " - Format de la pièce jointe invalide : " + primaryDocument.getFileName())
        }
        Utils.getSimpleViewAttachmentController().nextDocument()
        Utils.getSimpleViewAttachmentController().previousDocument()
        Utils.getAttachmentController().getModel().refreshDocument()
      }
      else {
        if(errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_edit_attachment_document_no_attachment") + ")"
        scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" +
                               document.getAirsRefId() + " - Format de la pièce jointe invalide : " + primaryDocument.getFileName())
      }
    } catch(Exception e) {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + document.getAirsRefId() + " : ", e)
    }
    // Restaurer une version
  }
  else if("5".equalsIgnoreCase(String.valueOf(data.get("DATA_STATUS")))) {
    if(data.get("DATA_VERSION_ATTACHMENT") != null) {
      int primaryDocumentId
      try {
        PrimaryDocument primaryDocumentRecycled = null
        primaryDocumentId = Integer.parseInt(data.get("DATA_VERSION_ATTACHMENT").toString())
        for(PrimaryDocument p : doc.getPrimaryDocumentVersions(primaryDocument)) {
          if(primaryDocumentId == p.getId()) {
            primaryDocumentRecycled = p
            break
          }
        }
        if(primaryDocumentRecycled != null) {
          File folderDest = new File(ExportUtils.getExportPDFDirectory() + new Date().getTime())
          folderDest.mkdirs()
          primaryDocumentRecycled.download(folderDest.getAbsolutePath())

          PrimaryDocument primaryDocumentDest = new PrimaryDocument(primaryDocumentRecycled.getFileName(), primaryDocumentRecycled.getLabel())
          primaryDocumentDest.setType("ORIGINAL")
          doc.addPrimaryDocumentVersion(primaryDocument, primaryDocumentDest, folderDest.getAbsolutePath())
          folderDest.delete()

          Utils.getSimpleViewAttachmentController().nextDocument()
          Utils.getSimpleViewAttachmentController().previousDocument()
          Utils.getAttachmentController().getModel().refreshDocument()
        }
        else {
          if(errorDocuments == null)
            errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
          else
            errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
          scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + doc.getId() + " : Version non récupérée")
        }
      } catch(Exception e) {
        if(errorDocuments == null)
          errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
        else
          errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
        scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + doc.getId() + " : ", e)
      }

      if(errorDocuments == null) {
        Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_WF_TASK_SUBMIT,
                                               BundleUtils.getTranslation("jsp_recycle_version") + " n°" + primaryDocumentId)
      }
    }
    else {
      if(errorDocuments == null)
        errorDocuments = document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      else
        errorDocuments += ", " + document.getAirsRefId().toString() + " (" + BundleUtils.getTranslation("groovy_traitment_exec_error") + ")"
      scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR - Document n" + doc.getId() + " - Version sélectionnée est null")
    }

  }
  else {
    scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : Statut --> " + data.get("DATA_STATUS").toString())
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_error_status"))
    return
  }

  if(errorDocuments == null) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success"))
  }
  else {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments)
  }

} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - EditAttachmentDocumentSimpleViewExec - ERREUR : ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT ATTACHMENT DOCUMENT SIMPLE VIEW EXEC - END")