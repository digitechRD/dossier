import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.HashMap
import java.util.List
import java.util.Map

import net.sf.jooreports.templates.DocumentTemplateException

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration

import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.export.PdfRenumberFontModel
import com.digitech.dossier.common.model.backend.report.ReportAttachment
import com.digitech.dossier.common.model.backend.report.ReportDocument
import com.digitech.dossier.common.model.backend.report.ReportModel
import com.digitech.dossier.common.service.export.IOdtGenerator
import com.digitech.dossier.common.service.export.impl.OdtGenerator
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueExportInitializer
import com.digitech.jcorbairs.exception.DocumentException
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.report.model.MergingModel.Type
import com.digitech.report.service.IDocumentConvertionService
import com.digitech.report.service.IDocumentGenerationService
import com.digitech.report.service.IDocumentInspectorService
import com.digitech.report.service.IDocumentMergingService
import com.digitech.report.service.impl.ooo.DocumentConvertionService
import com.digitech.report.service.impl.ooo.DocumentGenerationService
import com.digitech.report.service.impl.ooo.DocumentInspectorService
import com.digitech.report.service.impl.ooo.DocumentMergingService
import com.digitech.report.service.impl.ooo.OfficeManager
import com.digitech.report.service.impl.ooo.SectionModel
import com.digitech.toolbox.document.exception.DocumentOperationException
import com.digitech.toolbox.document.service.IOperationService

/******* DEFINE *****/

String MODEL_ODT_NAME = "listing_Attachment.odt";

/*******************/

String exportDestFileName = null;
try {
  exportDestFileName = ExportUtils.getPdfFileName();
}
catch(Exception e) {
  getLog().error(e.getLocalizedMessage(), e);
  currentExportModel.setError(true);
  throw new RuntimeException(e);
}

String ExportPath = ExportUtils.getExportPDFDirectory() + File.separator;
String exportDestPath = ExportPath + exportDestFileName;
String exportDestPathODT = ExportPath + exportDestFileName + ".odt";

new File(ExportUtils.getExportPDFDirectory()).mkdirs();
new File(ExportPath).mkdirs();

String exportODTFilePath = ExportUtils.getODTDirectory() + File.separator + MODEL_ODT_NAME;

ScriptResultValueExportInitializer result = new ScriptResultValueExportInitializer(); 

try {

 generateListing(UserContext.getInstance(), exportODTFilePath, exportDestPathODT, exportDestPath, ExportPath, documents,  DossierCoreContext.getParamsInfos().getOfficeConfiguration());
 result.setFileResultName(exportDestFileName);
 result.setFileResultPath(exportDestPath);
}
catch(IOException e) {
  getLog().error(e.getLocalizedMessage(), e);
  currentExportModel.setError(true);
  throw new RuntimeException(e);
}
catch(DocumentTemplateException e) {
  getLog().error(e.getLocalizedMessage(), e);
  currentExportModel.setError(true);
  throw new RuntimeException(e);
}

output.setValue( result );

/******************************* listing generation  ******************************/
public void generateListing(UserCoreContext usrContext, String odt_templateFilePath, String odt_resultFilePath, String resultFilePath, String tempFilePath,
List<IDocument> docList, DefaultOfficeManagerConfiguration configuration)
throws FileNotFoundException, ServerException, IdentificationException, DocumentException, IOException, DocumentTemplateException,
com.lowagie.text.DocumentException, DocumentOperationException {

  // model to set the position of the stamper
  PdfRenumberFontModel pdfFont = new PdfRenumberFontModel();
 
  OfficeManager.getInstance().startOffice(configuration);

  try {
    File odtModelFile = new File(odt_templateFilePath);
    File odtResultFile = new File(odt_resultFilePath);
    File resultFile = new File(resultFilePath); 

   
    // defining object
    //Map<String, ReportAttachment> sectionAttachmentMap = new HashMap<String, ReportAttachment>();
    ReportModel reportDataModel = getOdtGeneration().getReportModel(docList, sectionAttachmentMap);
    Map<String, ReportModel> data = new HashMap<String, ReportModel>();
    Map<String, ReportAttachment> mapReportAttachement = new HashMap<String, ReportAttachment>();
    data.put( OdtGenerator.REPORT_MODEL_NAME, reportDataModel);
   

    // first step
    // fusion template with variable
    getDocumentGenerationService().generate(odtModelFile, odtResultFile, data);
    
    // refresh the section 
    Map<String, SectionModel> fileSections = getDocumentInspectorService().getSectionModels(odtResultFile,  OdtGenerator.NAME_FILE_PREFIX);
   
   
    // for the document list, attachment traitment
    for(ReportDocument reportDoc : reportDataModel.getReportDocumentList()) {
      mapReportAttachement.putAll(reportDoc.getSectionAttachmentMap());
      // second step
      // merge file which can merge with ODT
      fileSections = getOdtGeneration().mergeFilesBeforeConversion(odt_templateFilePath, tempFilePath, odtResultFile, Type.SECTION, mapReportAttachement, fileSections);

      // update the section
      fileSections = getDocumentInspectorService().getSectionModels(odtResultFile, OdtGenerator.NAME_FILE_PREFIX);

      // third step
      // adding blank page for the pdf or file to be converted in pdf format. convert the unconverted file too
      getOdtGeneration().addBlankPage(odtResultFile, tempFilePath, mapReportAttachement, fileSections);

      // update the section
      fileSections = getDocumentInspectorService().getSectionModels(odtResultFile,  OdtGenerator.NAME_FILE_PREFIX);

      // renumber pdf file
     
      getOdtGeneration().renumberPdfFile( mapReportAttachement, fileSections);

    }

    // third step
    // generate output file
    getDocumentConversionService().convert(odtResultFile, resultFile);
    if(resultFile.exists() && resultFile.canWrite()) {
      // fourth step
      // concant the final result file with the others
      Map<String, SectionModel> fileSectionsPDF = getDocumentInspectorService().getSectionModels(odtResultFile, OdtGenerator.NAME_FILE_PREFIX);
      getOdtGeneration().concatWithPdfOutput(resultFile, tempFilePath, mapReportAttachement, fileSectionsPDF, configuration);
    }
  }
  finally {
    OfficeManager.getInstance().stopOffice();
  }
}

private IDocumentConvertionService getDocumentConversionService() {
  IDocumentConvertionService docConversionService = new DocumentConvertionService();
  return docConversionService;
}

private IDocumentGenerationService getDocumentGenerationService() {
  IDocumentGenerationService docGenerationService = new DocumentGenerationService();
  return docGenerationService;
}

private IDocumentInspectorService getDocumentInspectorService() {
  IDocumentInspectorService docInspetorService = new DocumentInspectorService();
  return docInspetorService;
}

private IDocumentMergingService getDocumentMergeService() {
  IDocumentMergingService odtMergingService = new DocumentMergingService();
  return odtMergingService;
}

private IDocumentMergingService getDocumentPdfMergeService() {

  IDocumentMergingService odtMergingService = new com.digitech.report.service.impl.pdf.DocumentMergingService();
  return odtMergingService;
}

private IDocumentInspectorService getDocumentPdfInspectorService() {
  IDocumentInspectorService odtMergingService = new com.digitech.report.service.impl.pdf.DocumentInspectorService();
  return odtMergingService;
}

private IOperationService getDocumentTiffConvectorService() {
  IOperationService tifOperationService = new com.digitech.toolbox.document.service.impl.tiff.TIFFOperationService();
  return tifOperationService;
}

private  IOdtGenerator getOdtGeneration()
{
  IOdtGenerator OdtGeneratorService = new OdtGenerator();
  return OdtGeneratorService;
}