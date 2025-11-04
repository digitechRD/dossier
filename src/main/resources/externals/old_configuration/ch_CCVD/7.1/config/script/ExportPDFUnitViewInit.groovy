import Constants
import Methods
import com.digitech.airs3dossiers.airs.AirsDocument
import com.digitech.airs3dossiers.airs.AirsFolder
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.DWServiceConstants
import com.digitech.dossier.common.service.IDocToPDFConverterService
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.service.export.impl.PdfExport
import org.apache.commons.io.FilenameUtils

import java.nio.file.Path
import java.text.DateFormat
import java.text.SimpleDateFormat

/**************************************************************************************************
 * 							          Export fichier au format PDF - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'export en téléchargement d'un fichier au format PDF
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null
Map<String, Object> data = null
File genFolder = null
DateFormat formatter = null
List<String> pdfs = new ArrayList<String>()
List<String> filteredType = new ArrayList<String>()
String outFolder = null
String relativeOutFolder = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()

  formatter = new SimpleDateFormat("yyyyMMddhhmmssS")

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - ExportPDFUnitViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  String tempFolderId = formatter.format(new Date())
  genFolder = new File(userContext.getInstance().getUserDownloadPath() + File.separator + tempFolderId + File.separator)
  genFolder.mkdir()
  outFolder = userContext.getInstance().getUserDownloadPath() + File.separator + tempFolderId + File.separator
  relativeOutFolder = userContext.getInstance().getUserDownloadRelativePath() + File.separator + tempFolderId + File.separator
  filteredType.add("AMFL1W1EXP")

  getAttachments(document, outFolder, files, filteredType)

  String outText = ""
  for(File file : files) {
    if(isPDF(file.getName())) {
      outText += "<br>" + file.getName() + " inclus dans le pdf </br>"
      pdfs.add(file.getAbsolutePath())
    }
    else if(isKnown(file.getName())) {
      outText += "<br>" + file.getName() + " inclus dans le pdf </br>"
      pdfs.add(convertToPdf(file, new File(outFolder)).getAbsolutePath())
    }
    else {
      outText += "<br>" + file.getName() + " non inclus dans le PDF - format non pris en compte </br>"
    }

    PdfExport exporter = new PdfExport()
    OutputStream fo = null
    try {
      fo = new FileOutputStream(outFolder + File.separator + "ExportedFolder.pdf")
    }
    catch(FileNotFoundException e) {
      throw new RuntimeException(e)
    }
    exporter.concatPDFs(pdfs, fo, false)

    data.put("file", relativeOutFolder + "ExportedFolder.pdf")
    data.put("text", outText)
    data.put("folder", genFolder)
  }

} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - ExportPDFUnitViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW INIT - END")


/**
 * METHODES
 **************************************************************************************************/

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
}

private boolean isPDF(String name) {
  String ext = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase()
  return ext.equals("pdf")
}

private boolean isWriter(String name) {
  String ext = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase()
  Set<String> map = new HashSet<String>()
  map.add("odt")
  map.add("ott")
  map.add("sxw")
  map.add("stw")
  map.add("doc")
  map.add("dot")
  map.add("rtf")
  map.add("txt")
  map.add("docx")
  map.add("docm")
  map.add("dotx")
  map.add("dotm")
  map.add("602")
  map.add("wpd")
  map.add("hwp")
  return map.contains(ext)
}

private boolean isWebWriter(String name) {

  String ext = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase()
  Set<String> map = new HashSet<String>()
  map.add("html")
  map.add("htm")
  map.add("oth")
  return map.contains(ext)
}

private boolean isCalc(String name) {
  String ext = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase()
  Set<String> map = new HashSet<String>()
  map.add("ods")
  map.add("ots")
  map.add("sxc")
  map.add("dif")
  map.add("dbf")
  map.add("xls")
  map.add("xlc")
  map.add("xlm")
  map.add("xlt")
  map.add("slk")
  map.add("csv")
  map.add("xlsb")
  map.add("xlsm")
  map.add("xlsx")
  map.add("wk1")
  map.add("wks")
  map.add("123")
  map.add("wb2")
  return map.contains(ext)

}

private boolean isDraw(String name) {
  String ext = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase()
  Set<String> map = new HashSet<String>()
  map.add("odg")
  map.add("otg")
  map.add("sxd")
  map.add("std")
  map.add("dxf")
  map.add("emf")
  map.add("eps")
  map.add("met")
  map.add("pct")
  map.add("pict")
  map.add("sgf")
  map.add("sgv")
  map.add("wmf")
  map.add("bmp")
  map.add("gif")
  map.add("jpg")
  map.add("jpeg")
  map.add("pbm")
  map.add("pcx")
  map.add("png")
  map.add("pgm")
  map.add("ppm")
  map.add("ras")
  map.add("psd")
  map.add("tga")
  map.add("tif")
  map.add("tiff")
  map.add("xbm")
  map.add("xpm")
  map.add("pcd")
  return map.contains(ext)
}

private boolean isImpress(String name) {
  String ext = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase()
  Set<String> map = new HashSet<String>()
  map.add("otp")
  map.add("odp")
  map.add("sxi")
  map.add("sti")
  map.add("ppt")
  map.add("pps")
  map.add("cgm")
  map.add("pptm")
  map.add("pptx")
  map.add("potm")
  return map.contains(ext)
}

private boolean isKnown(String name) {
  return isWriter(name) || isWebWriter(name) || isCalc(name) || isDraw(name) || isImpress(name)
}

private IDocToPDFConverterService getConversionService() {
  return ServiceManager.getInstance().getService(DWServiceConstants.DOC_TO_PDF_CONVERTER_SERVICE, IDocToPDFConverterService.class);
}

private File convertToPdf(File input, File output) {

  File outputFile = new File(output, FilenameUtils.getBaseName(input.getAbsolutePath()) + ".pdf")

  Path pdf = getConversionService().convert2Pdf(input.toPath(), outputFile.toPath())
  return pdf?.toFile()
}

void getAttachments(IDocument doc, String outFolder, List<File> files, List<String> filteredType) {
  if(doc.isFolder()) {
    AirsFolder airsFolder = (AirsFolder) doc.getAirsDocument()
    for(AirsDocument docChild : airsFolder.getChildList()) {
      getAttachments(Methods.getDocumentMgr().getDocument(userContext.getJeton(), docChild.getId()), outFolder, files, filteredType)
    }
  }
  else if(!filteredType.contains(doc.getFieldMap().get(Constants.LIST_TYPE_CODE))) {
    for(IAttachment attachment : doc.getAttachment(userContext)) {
      files.add(Methods.getDocumentMgr().loadDocumentAttachment(userContext, doc, attachment, outFolder))
    }
  }
}
