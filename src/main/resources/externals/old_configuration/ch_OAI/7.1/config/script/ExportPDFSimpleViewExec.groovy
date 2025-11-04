import Constants
import Methods
import com.aspose.pdf.facades.IPdfFileEditor
import com.aspose.pdf.facades.PdfFileEditor
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.export.impl.OdtGenerator
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.script.model.impl.result.ScriptResultValueExportInitializer
import com.digitech.jcorbairs.Field
import com.digitech.jcorbairs.SortCriterion
import com.digitech.jcorbairs.Sorting
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager
import com.digitech.jcorbairs.utils.Direction
import com.digitech.toolbox.document.service.impl.tiff.TIFFOperationService
import com.lowagie.text.PageSize
import com.lowagie.text.pdf.*
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.text.SimpleDateFormat
import java.util.Map.Entry

/*************************************************************************************************
 * 							Export et listing des documents
 **************************************************************************************************
 Date : 04.04.2016
 Auteur : JFE

 Description : Permet l'exportation des documents
 **************************************************************************************************/


scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF VIEW EXEC - START")

/******************** Décalration + Initialisation ********************************/
ScriptResultValueExportInitializer result = new ScriptResultValueExportInitializer()
FileInputStream fileInputStream = null
PdfReader reader = null
PdfCopy copy = null
try {
  String exportDestFileName = null
  CustomActionController customActionController = Utils.getCustomActionController()
  Map<String, Object> data = customActionController.getModel().getModalPanelModel()
  Map<String, String> folderNameMap = new HashMap<String, String>()
  Map<Date, List> treeMap = new TreeMap<Date, List>()
  //Récupération de tous les dossiers à exlcure
  List<String> foldersToExclude = null
  boolean hasExcluded = false
  String fieldFilter = null
  //loginUser_CurrentDate.pdf
  exportDestFileName = ExportUtils.getPdfFileName()
  String exportPath = ExportUtils.getExportPDFDirectory()
  String exportDestPath = exportPath + exportDestFileName
  String exportEntete = exportPath + "entete" + Constants.APPLICATION_PDF_EXTENSION.toLowerCase()
  String exportFilesConcat = exportPath + "filesConcat" + Constants.APPLICATION_PDF_EXTENSION.toLowerCase()
  //Création du dossier sur le système de fichier
  new File(exportPath).mkdirs()
  List<Integer> docListId = null
  SimpleDateFormat simpleDateFormat = null
  String converteddownPathCurrentAttachment = null
  String date = null
  String nss = null
  String typeDocument = null
  String ndem = null
  String nssTitle = null
  String ndemTitle = null
  String typeDocTitle = null
  String dateTitle = null
  int cptPage
  int cptNombreDocuments
  int nombrePageDocument
  File enteteTemplate = null
  File enteteResult = null
  com.lowagie.text.Document documentEntete = null
  BaseFont bf = null
  PdfWriter writer = null
  PdfContentByte cb = null

  PdfImportedPage page = null
  float y = 550f
  float scaleY = -15f
  ArrayList<HashMap<String, Object>> outlines = new ArrayList<HashMap<String, Object>>()
  List<Integer> numeroPage = null

  Document xmlDocument = null
  DocumentBuilderFactory builderFactory = null
  DocumentBuilder builder = null
  List<String> sommaireConfig = null
  Integer nbDocumentExporte = 0
  ScriptResultValueChecker resultMsg = new ScriptResultValueChecker()
  resultMsg.setMessageSummary(BundleUtils.getTranslation("groovy_save_document_action"))
  /** ****************************************************************/

  /******************** Traitements ********************************/
  fileInputStream = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH))
  builderFactory = DocumentBuilderFactory.newInstance()
  builder = builderFactory.newDocumentBuilder()
  xmlDocument = builder.parse(fileInputStream)
  //scriptLogger.debug("NOMBRE ELEMENT : "+documents.size());
  List<String> profilAuthorized = Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_PROFILS)
  if(profilAuthorized.size() == 0 || Methods.isActorInProfil(UserContext.getInstance().getUserId(), profilAuthorized)) {

    foldersToExclude = Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_FILTERS)
    hasExcluded = ("0".equals(Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_FILTERS_INCLUDED).toString()) ? true : false)
    sommaireConfig = Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_SUMMARY)
    fieldFilter = Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_FILTER_FIELD)

    //Récupération des documents sélectionné


    docListId = new ArrayList<Integer>()


    for(IDocument doc : documents) {
      String sousDossier = doc.getField(fieldFilter).getValue()
      boolean shouldAdd = true

      Date d = null
      simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_INPUT, Locale.ENGLISH)

      try {
        d = simpleDateFormat.parse(doc.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue().toString())
      }
      catch(Exception e) {
        d = simpleDateFormat.parse(doc.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue().toString())
      }


      if(foldersToExclude.contains(sousDossier) && hasExcluded) {
        shouldAdd = false
        //scriptLogger.debug("Dans la liste et a exclude");
      }
      else if(!foldersToExclude.contains(sousDossier) && !hasExcluded) {
        shouldAdd = false
        //scriptLogger.debug("Pas dans la liste et a inclure");
      }


      if(shouldAdd) {

        StringBuilder tmpKeyTreeMap = new StringBuilder()
        tmpKeyTreeMap.append(sousDossier)
        tmpKeyTreeMap.append("_")
        tmpKeyTreeMap.append(simpleDateFormat.format(d))
        // Tri par date
        if(treeMap.containsKey(tmpKeyTreeMap.toString())) {
          treeMap.get(tmpKeyTreeMap.toString()).add(doc.getAirsRefId())
        }
        else {
          List<Integer> docIdList = new ArrayList<Integer>()
          docIdList.add(doc.getAirsRefId())
          treeMap.put(tmpKeyTreeMap.toString(), docIdList)
        }
      }
    }

    for(Entry a : treeMap.entrySet()) {
      List<Integer> subDocIdList = treeMap.get(a.getKey())
      for(Integer docId : subDocIdList) {

        docListId.add(docId)
      }
    }

    if(docListId.size() > 0) {
      HashMap<String, Object> titrePrincipal = null
      String downPathCurrentAttachment = null
      numeroPage = new ArrayList()
      com.lowagie.text.Document documentConcat = new com.lowagie.text.Document()
      copy = new PdfCopy(documentConcat, new FileOutputStream(exportFilesConcat))
      documentConcat.open()
      cptPage = 1
      cptNombreDocuments = 1
      //Ecriture dans l'entete => Titre + libellé colonne
      enteteTemplate = new File(Constants.APPLICATION_EXPORT_PDF_HEADER)
      enteteResult = new File(exportEntete)
      documentEntete = new com.lowagie.text.Document(PageSize.A4)
      bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
      writer = PdfWriter.getInstance(documentEntete, new FileOutputStream(enteteResult))
      documentEntete.open()
      cb = writer.getDirectContent()
      reader = new PdfReader(new FileInputStream(enteteTemplate))
      page = writer.getImportedPage(reader, 1)
      documentEntete.newPage()

      //Titre du document de l'entete
      cb.addTemplate(page, 0, 0)
      cb.beginText()
      cb.setFontAndSize(bf, 21)
      cb.showTextAligned(PdfContentByte.ALIGN_CENTER, Methods.formatString(String.valueOf(
          Methods.getDocumentMgr().getDocument(UserContext.getInstance().getJeton(), docListId.get(0)).getField(Constants.FIELD_NSS_CODE).getValue()),
                                                                           Constants.NSS_MASK), 300, 600, 0)
      cb.endText()
      if(sommaireConfig.contains(Constants.FIELD_NSS_CODE)) {
        nssTitle = BundleUtils.getTranslation("xml_configuration_label_nss")
      }
      if(sommaireConfig.contains(Constants.FIELD_DEM_CODE)) {
        ndemTitle = BundleUtils.getTranslation("xml_configuration_label_request_number")
      }
      if(sommaireConfig.contains(Constants.FIELD_DATE_DOCUMENT_CODE)) {
        dateTitle = BundleUtils.getTranslation("xml_configuration_label_date")
      }
      if(sommaireConfig.contains(Constants.LIST_TYPES_DOCUMENT_CODE)) {
        typeDocTitle = BundleUtils.getTranslation("xml_configuration_label_type")
      }
      Methods.writeHeader(cb, dateTitle, nssTitle, ndemTitle, typeDocTitle, BundleUtils.getTranslation("xml_configuration_label_count_pages"),
                          BundleUtils.getTranslation("xml_configuration_label_number_document"), y)

      List<SortCriterion> ls = new ArrayList()
      for(int nbCrit = 0; nbCrit < Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_COLUMN_SORTED).size(); nbCrit++) {
        ls.add(new SortCriterion(new Field(DossierCoreContext.getAdminJeton(), Methods.getContentsList(xmlDocument,
                                                                                                       Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_COLUMN_SORTED).
            get(nbCrit)), ("DESC".equals(Methods.getContentsList(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_FILE_OPTION_SORTED).get(nbCrit))) ?
                                     Direction.DESCENDANT : Direction.ASCENDANT))
      }
      List<Integer> docListIdSort = com.digitech.jcorbairs.Document.sort(DossierCoreContext.getAdminJeton(), docListId, new Sorting(ls))
      nbDocumentExporte = docListIdSort.size()
      for(Integer idDocument : docListIdSort) {
        IDocument currentDoc = Methods.getDocumentMgr().getDocument(UserContext.getInstance().getJeton(), idDocument)
        List<IAttachment> attatchmentList = currentDoc.getAttachments(UserContext.getInstance())
        if(attatchmentList != null && attatchmentList.size() > 0) {
          for(IAttachment currentAttachement : attatchmentList) {
            File pdfFile = null
            File currentFile = null
            try {
              downPathCurrentAttachment = Methods.downloadAttachment(exportPath, currentDoc, currentAttachement)
              //Conversion des documents
              if(StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), Constants.APPLICATION_PDF_EXTENSION.toLowerCase())) {
                converteddownPathCurrentAttachment = downPathCurrentAttachment
              }
              else if(StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), Constants.APPLICATION_TIF_EXTENSION)) {
                currentFile = new File(downPathCurrentAttachment)
                pdfFile = new File(downPathCurrentAttachment + Constants.APPLICATION_PDF_EXTENSION.toLowerCase())
                new TIFFOperationService().convert(currentFile, pdfFile, Collections.EMPTY_MAP)
                converteddownPathCurrentAttachment = downPathCurrentAttachment + Constants.APPLICATION_PDF_EXTENSION.toLowerCase()
              }
              else if(StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), OdtGenerator.EXTENSIONS_AUTO_CONVERTION_TO_ODT)) {
                currentFile = new File(downPathCurrentAttachment)
                pdfFile = new File(downPathCurrentAttachment + Constants.APPLICATION_PDF_EXTENSION.toLowerCase())
                Methods.getDocumentConversionService().convert(currentFile, pdfFile)
                converteddownPathCurrentAttachment = downPathCurrentAttachment + Constants.APPLICATION_PDF_EXTENSION.toLowerCase()
              }
            } catch(Exception ex) {
              if(!StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), Constants.APPLICATION_TIF_EXTENSION)) {
                throw new Exception("Erreur à la conversion du document n°" + idDocument + " / " + pdfFile.getAbsolutePath(), ex)
              }
              else {
                try {
                  Methods.convertTiffToPDF(currentFile, pdfFile)
                  converteddownPathCurrentAttachment = downPathCurrentAttachment + Constants.APPLICATION_PDF_EXTENSION.toLowerCase()
                } catch(Exception ex2) {
                  throw new Exception("Erreur à la conversion du document n°" + idDocument + " / " + pdfFile.getAbsolutePath(), ex2)
                }
              }
            }
          }

          /********************Récupération des informations pour l'entête***********************/
          scriptLogger.debug(downPathCurrentAttachment)

          if(new File(converteddownPathCurrentAttachment).exists()) {
            com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(converteddownPathCurrentAttachment)
            com.aspose.pdf.License license = new com.aspose.pdf.License()
            license.setLicense(Constants.APPLICATION_LICENCE_ASPOSE_PDF)
            // Resize contents of resultant PDF
            int[] page_cnt1 = new int[pdfDocument.getPages().size()]
            for(int i = 0; i < pdfDocument.getPages().size(); i++) {
              page_cnt1[i] = i + 1
            }
            PdfFileEditor pfe = new PdfFileEditor()
            pfe.resizeContents(pdfDocument, page_cnt1, IPdfFileEditor.ContentsResizeParameters.pageResize(com.aspose.pdf.PageSize.getA4().getWidth(),
                                                                                                          com.aspose.pdf.PageSize.getA4().getHeight()))
            // Save output as PDF format
            pdfDocument.save(converteddownPathCurrentAttachment)
            //Nombre page du document
            PdfReader readerDocument = new PdfReader(converteddownPathCurrentAttachment)
            nombrePageDocument = readerDocument.getNumberOfPages()
            if(sommaireConfig.contains(Constants.FIELD_DATE_DOCUMENT_CODE)) {
              if(currentDoc.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue() != null) {
                Date dTmp = simpleDateFormat.parse(currentDoc.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue().toString())
                SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS, Locale.ENGLISH)

                date = formatter.format(dTmp)
              }
              else {
                dates = ""
              }
            }
            if(sommaireConfig.contains(Constants.LIST_TYPES_DOCUMENT_CODE)) {
              //currentDoc.getField > peut retourner null; si c'est le cas erreur pour loadTerm
              if(currentDoc.getField(Constants.LIST_TYPES_DOCUMENT_CODE).getValue() != null) {
                AuthorityListTermAdmin alTermAdmin = AuthorityListsManager.loadTerm(UserContext.getInstance().getJeton(),
                                                                                    currentDoc.getField(Constants.LIST_TYPES_DOCUMENT_CODE).getValue())
                if(alTermAdmin.getValue1().startsWith("al_"))
                  typeDocument = BundleUtils.getTranslation(alTermAdmin.getValue1())
                else
                  typeDocument = alTermAdmin.getValue1()
              }
              else {
                typeDocument = ""
              }
            }
            if(sommaireConfig.contains(Constants.FIELD_NSS_CODE)) {
              nss = currentDoc.getField(Constants.FIELD_NSS_CODE).getValue().toString()
            }
            if(sommaireConfig.contains(Constants.FIELD_DEM_CODE)) {
              if(currentDoc.getField(Constants.FIELD_DEM_CODE).getValue() != null) {
                ndem = currentDoc.getField(Constants.FIELD_DEM_CODE).getValue().toString()
              }
              else {
                ndem = ""
              }

            }

            //BOOKMARKS - DEBUT
            titrePrincipal = new HashMap<String, Object>()
            titrePrincipal.put("Title", date + " - " + typeDocument)
            titrePrincipal.put("Action", "GoTo")
            titrePrincipal.put("Page", String.format("%d Fit", cptPage))
            ArrayList<HashMap<String, Object>> kids = new ArrayList<HashMap<String, Object>>()

            int cpteurTmp = cptPage
            for(int i = 1; i <= readerDocument.getNumberOfPages(); i++) {
              HashMap<String, Object> pagesBM = new HashMap<String, Object>()
              pagesBM.put("Title", "Page " + i)
              pagesBM.put("Action", "GoTo")
              pagesBM.put("Page", Integer.toString(cpteurTmp))
              cpteurTmp = cpteurTmp + 1
              kids.add(pagesBM)
            }

            titrePrincipal.put("Kids", kids)
            outlines.add(titrePrincipal)
            //BOOKMARKS - FIN
            /********************Fin  récupération des informations pour l'entête***********************/
            Methods.concatPdf(copy, converteddownPathCurrentAttachment)

            //Position "curseur" ecriture entete
            y = y + scaleY
            if(y < 40f) {
              documentEntete.newPage()
              cb.addTemplate(page, 0, 0)
              y = 600f
              Methods.writeHeader(cb, dateTitle, nssTitle, ndemTitle, typeDocTitle, BundleUtils.getTranslation("xml_configuration_label_page"),
                                  BundleUtils.getTranslation("xml_configuration_label_number_document"), y)
              y = y + scaleY
            }
            numeroPage.add(cptPage)
            Methods.writeHeader(cb, date, nss, ndem, typeDocument, Integer.toString(cptPage), Integer.toString(cptNombreDocuments), y)
            cptPage = cptPage + nombrePageDocument
            cptNombreDocuments = cptNombreDocuments + 1
            try {
              //System.gc();
              File fileSource = new File(downPathCurrentAttachment)
              if(fileSource.exists()) {
                Methods.deleteFile(fileSource)
              }
              File fileTmp = new File(converteddownPathCurrentAttachment)

              if(fileTmp.exists()) {

                Methods.deleteFile(fileTmp)
              }
            } catch(Exception e) {
              scriptLogger.warn("Suppresion impossible", e)
            }
          }
        }
      }
      documentEntete.close()
      documentConcat.close()
      String resultPath = exportFilesConcat
      //addBookmark(resultPath,numeroPage);
      Methods.setPagingPage(resultPath, exportPath, numeroPage)
      //Fusion de l'entete et des documents fusionnés
      com.lowagie.text.Document documentFinal = new com.lowagie.text.Document()
      PdfCopy mergerFinal = new PdfCopy(documentFinal, new FileOutputStream(exportDestPath))
      documentFinal.open()
      Methods.concatPdf(mergerFinal, exportFilesConcat, outlines)
      Methods.concatPdf(mergerFinal, exportEntete)
      documentFinal.close()
      mergerFinal.close()
      //  Methods.deleteFile(new File(exportFilesConcat));
      // Methods.deleteFile(new File(exportEntete));

      if(new File(exportDestPath).exists()) {
        result.setFileResultName(exportDestFileName)
        result.setFileResultPath(exportDestPath)
        output.setValue(result)
      }
      else {
        result.setFileResultPath(Constants.APPLICATION_EXPORT_PDF_ERROR_PATH + "" + Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_SUBFOLDER)
        result.setFileResultName(Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_SUBFOLDER)
        output.setValue(result)
      }

    }
    else {
      result.setFileResultPath(Constants.APPLICATION_EXPORT_PDF_ERROR_PATH + "" + Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_SUBFOLDER)
      result.setFileResultName(Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_SUBFOLDER)
      output.setValue(result)
    }

  }
  else {
    result.setFileResultPath(Constants.APPLICATION_EXPORT_PDF_ERROR_PATH + "" + Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_RIGHTS)
    result.setFileResultName(Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_RIGHTS)
    output.setValue(result)
  }
  Methods.logActionUser(Constants.ACTION_EXPORT, userContext.getUser().getLogin(),
                        BundleUtils.getTranslation("groovy_nombre_document") + " : " + nbDocumentExporte.toString())

} catch(Exception e) {
  scriptLogger.error("", e)
  result.setFileResultPath(Constants.APPLICATION_EXPORT_PDF_ERROR_PATH + "" + Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_SUBFOLDER)
  result.setFileResultName(Constants.APPLICATION_EXPORT_PDF_ERROR_FILE_RIGHTS)
  output.setValue(result)
} finally {
  try {
    fileInputStream?.close()
  }
  catch(IOException ignored) {
  }
  try {
    copy?.close()
  }
  catch(Exception ignored) {
  }
  try {
    reader?.close()
  }
  catch(Exception ignored) {
  }
  scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF VIEW EXEC - END")
}