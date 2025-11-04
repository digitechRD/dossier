// Script groovy permetant d'exporter au format pdf les pièces jointes (en PDF ou TIF) d'un dossier ou d'un document.
// L'export se présente sous forme de listing : 1ère pièce jointe, 2ème, etc dans un même fichier pdf.

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sf.jooreports.templates.DocumentTemplateException;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;

import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.export.PdfRenumberFontModel;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.export.impl.ExportUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueExportInitializer;
import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.exception.DocumentException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.report.model.MergingModel;
import com.digitech.report.model.MergingModel.Type;
import com.digitech.report.service.IDocumentConvertionService;
import com.digitech.report.service.IDocumentMergingService;
import com.digitech.report.service.impl.ooo.DocumentConvertionService;
import com.digitech.report.service.impl.ooo.OfficeManager;
import com.digitech.report.service.impl.pdf.DocumentMergingService.PDFFileMergingModel;
import com.digitech.toolbox.document.exception.DocumentOperationException;
import com.digitech.toolbox.document.service.IOperationService;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.DossierCoreContext;

String exportDestFileName = null;
try {
  exportDestFileName = com.digitech.dossier.common.utils.ExportUtils.getPdfFileName();
}
catch(Exception e) {
  scriptLogger.error(e.getLocalizedMessage(), e);
  //currentExportModel.setError(true);
  throw new RuntimeException(e);
}

String ExportPath = com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory() + File.separator;
String exportDestPath = ExportPath + exportDestFileName;


new File(com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory()).mkdirs();
new File(ExportPath).mkdirs();

scriptLogger.debug("Attachment Listing : Export - Starting traitement ");

ScriptResultValueExportInitializer result = new ScriptResultValueExportInitializer();

try {


  scriptLogger.debug("Attachment Listing : Export - adding child id if needed");
  List<IDocument> docListToTrait = new  ArrayList<IDocument>();
  for(IDocument currentDoc : documents) {
    docListToTrait.push(currentDoc);
    if( currentDoc.isFolder() ) {
      scriptLogger.debug("Attachment Listing : Export - This is a folder.So adding child");
      Collection<Domain> childContentTypes = DossierCoreContext.getContentTypeInfos().getChildContentTypes(currentDoc.getDomain().getCode());
      for(Domain childContentType : childContentTypes) {
        List<Integer> childIds = getDocumentsMgr().getChildDocuments(userContext, currentDoc.getAirsRefId(), childContentType);
        for (Integer childDocId :childIds  ) {
          docListToTrait.push( getDocumentMgr().getDocument(userContext, childDocId )) ;
        }
      }
    }
  }

  generateListing( UserContext.getInstance(), exportDestPath, ExportPath, docListToTrait,  DossierCoreContext.getParamsInfos().getOfficeConfiguration(), result);
  result.setFileResultName(exportDestFileName);
  result.setFileResultPath(exportDestPath);

  scriptLogger.debug("Attachment Listing : Export - End of traitment ( file generated : " + exportDestPath + exportDestFileName);
}
catch(IOException e) {
  scriptLogger.error(e.getLocalizedMessage(), e);

  throw new RuntimeException(e);
}
catch(DocumentTemplateException e) {
  scriptLogger.error(e.getLocalizedMessage(), e);

  throw new RuntimeException(e);
}

output.setValue( result );

/******************************* listing generation ******************************/
public  void generateListing(UserCoreContext usrContext, String resultFilePath, String tempFilePath, List<IDocument> docList,
DefaultOfficeManagerConfiguration configuration, ScriptResultValueExportInitializer result)
throws FileNotFoundException, ServerException, IdentificationException, DocumentException, IOException, DocumentTemplateException,
com.lowagie.text.DocumentException, DocumentOperationException {

  String   FILE_EXTENSION_PDF                = ".pdf";


  /** Defines extensions which can be converted to ODT */
  String[]  EXTENSIONS_AUTO_CONVERTION_TO_ODT =  com.digitech.dossier.common.service.export.impl.OdtGenerator.EXTENSIONS_AUTO_CONVERTION_TO_ODT;
  /** Defines extensions which can be converted to PDF */
  String[]  EXTENSIONS_AUTO_CONVERTION_TO_PDF = com.digitech.dossier.common.service.export.impl.OdtGenerator.EXTENSIONS_AUTO_CONVERTION_TO_PDF;

  // model to set the position of the stamper
  PdfRenumberFontModel pdfFont = new PdfRenumberFontModel();
  try {

    File resultFile = new File(resultFilePath);

    // defining object

    List<MergingModel> mergingModels = new ArrayList<MergingModel>();

    boolean firtsAttachment = true;
    File masterFile = null;

    for(IDocument currentDoc : docList) {
      List<IAttachment> attatchmentList = currentDoc.getAttachments(usrContext);

      for(IAttachment currentAttachement : attatchmentList) {
        String downPathCurrentAttachment = downloadAttachment(tempFilePath, currentDoc, currentAttachement);

        scriptLogger.debug("Attachment Listing : Export - Traitment of file : " + downPathCurrentAttachment );

        String converteddownPathCurrentAttachment = null;
        File pdfFile = null;
        // need a conversion ??
        if(com.digitech.common.lib.utils.StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), FILE_EXTENSION_PDF)) {
          // noconversionneeded
          converteddownPathCurrentAttachment = downPathCurrentAttachment;
          pdfFile = new File(downPathCurrentAttachment);
        }
        else if(com.digitech.common.lib.utils.StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), FILE_EXTENSIONS_TOF)) { // tiff
          File currentFile = new File(downPathCurrentAttachment);
          pdfFile = new File(downPathCurrentAttachment + FILE_EXTENSION_PDF);
          // conversion
          getDocumentTiffConvectorService().convert(currentFile, pdfFile, Collections.EMPTY_MAP);
          converteddownPathCurrentAttachment = downPathCurrentAttachment + FILE_EXTENSION_PDF;
        }
        else if(com.digitech.common.lib.utils.StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), EXTENSIONS_AUTO_CONVERTION_TO_PDF)) { // odt
          File currentFile = new File(downPathCurrentAttachment);
          pdfFile = new File(downPathCurrentAttachment + FILE_EXTENSION_PDF);
          // conversion
          getDocumentConversionService().convert(currentFile, pdfFile);
          converteddownPathCurrentAttachment = downPathCurrentAttachment + FILE_EXTENSION_PDF; // conversion
        }

        if(pdfFile != null && !firtsAttachment) {
          mergingModels.add(new MergingModel(Type.END, new PDFFileMergingModel(pdfFile, true, true)));

        }
        else if(firtsAttachment) {
          masterFile = new File(converteddownPathCurrentAttachment);
          firtsAttachment = false;
        }
      }
    }
     scriptLogger.debug("Attachment Listing : Export - Tryin to merged files");
    if( masterFile != null && mergingModels != null && mergingModels.size() > 0 )
    {
      getDocumentPdfMergeService().merge(masterFile, mergingModels);
      scriptLogger.debug("Attachment Listing : Export - Merged files");
    }

    if(  masterFile != null )
    {
      masterFile.renameTo(resultFile);
      ExportUtils.PdfRenumberWithStamp(resultFile, 1, pdfFont);
      scriptLogger.debug("Attachment Listing : Export - File renamed");
    }

  }
  catch(Exception e) {
    scriptLogger.error(e.getLocalizedMessage(), e);
    result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
    result.setMessageSummary("Erreur lors de  la fusion de document");
    result.setMessageDetail(e.getLocalizedMessage());
    //currentExportModel.setError(true);
  }
  finally {
    OfficeManager.getInstance().stopOffice();
  }
}

private  String downloadAttachment(String tempFilePath, IDocument document, IAttachment attachment)
throws ServerException, IdentificationException, DocumentException, IOException {

  String strPathClient = tempFilePath;
  document.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), strPathClient);
  return strPathClient + File.separator + attachment.getFileName();

}

private  IDocumentConvertionService getDocumentConversionService() {
  IDocumentConvertionService docConversionService = new DocumentConvertionService();
  return docConversionService;
}

private  IDocumentMergingService getDocumentPdfMergeService() {

  IDocumentMergingService odtMergingService = new com.digitech.report.service.impl.pdf.DocumentMergingService();
  return odtMergingService;
}

private  IOperationService getDocumentTiffConvectorService() {
  IOperationService tifOperationService = new com.digitech.toolbox.document.service.impl.tiff.TIFFOperationService();
  return tifOperationService;
}
private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(
  com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

private com.digitech.dossier.common.service.IDocuments getDocumentsMgr() {
  return (com.digitech.dossier.common.service.IDocuments) ServiceManager.getInstance().getService(
  com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENTS_MGR);
}
