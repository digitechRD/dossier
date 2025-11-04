import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// --entete--
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
// --/entete--

import com.digitech.dossier.common.model.backend.export.PdfRenumberFontModel;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;

import com.digitech.dossier.common.service.export.impl.*;
import com.digitech.dossier.common.model.backend.report.*;
import com.digitech.dossier.common.model.backend.*;
import com.digitech.dossier.common.model.backend.airs.*;
import com.digitech.dossier.common.Utils;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueExportInitializer;

import net.sf.jooreports.templates.*;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;


import com.digitech.jcorbairs.exception.*;

import com.digitech.report.model.*;
import com.digitech.report.service.IDocumentConvertionService;
import com.digitech.report.service.impl.ooo.DocumentConvertionService;

import com.digitech.toolbox.document.exception.*;

import com.digitech.toolbox.document.service.IOperationService;
import com.digitech.report.service.IDocumentMergingService;
import com.digitech.report.service.impl.pdf.DocumentMergingService.PDFFileMergingModel;
import com.digitech.report.service.impl.ooo.OfficeManager;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin;
import com.digitech.jcorbairs.admin.AuthorityListsManager;

/******* VARIABLES A MODIFIER*****/

String MODEL_ODT_NAME = "listing_Attachment.odt"; // le modele OOo a partir duquel le listing sera genere

String DATE_FIELD_CODE = "D_CREAT"; // le champ AIRS de date
String FOLDER_FIELD_CODE = "N_AVIS_MUTATION"; // le champ AIRS de dossier
String TYPE_FIELD_CODE = "AL_CODE_MOTIF_OFAS"; // le champ AIRS de type de document

String AL_TYPE_ID = "12"; //ID de la liste d'authorit� AIRS des types de documents

String DATE_TITLE_LABEL = "Date"; // le label pour le titre de la colonne date du sommaire 
String FOLDER_TITLE_LABEL = "No Avis Mutation"; // le label pour le titre de la colonne dossier du sommaire
String TYPE_TITLE_LABEL = "Code modif OFAS"; // le label pour le titre de la colonne type de document du sommaire
String PAGE_TITLE_LABEL = "Page"; // le label pour le titre de la colonne page du sommaire

String DATE_FORMAT = "dd/MM/yyyy"; // format de date pour sommaire document

String HEADER_FILE_ABSOLUTE_PATH = "/opt/digitech/apache-tomcat-webapps/AirsDossier/custom/ui/img/Entete.pdf"; // le fichier PDF contenant l'entete en PDF
String FOLDER_HEADER_FILE_ABSOLUTE_PATH = "/opt/digitech/apache-tomcat-webapps/AirsDossier/custom/ui/img/EnteteDossier.pdf"; // le fichier PDF contenant l'entete de dossier en PDF

/*******************/

//scriptLogger.debug("Starting exportListing_Documents.groovy...");
String exportDestFileName = null;

try 
{
	exportDestFileName = com.digitech.dossier.common.utils.ExportUtils.getPdfFileName();
}
catch(Exception e) 
{
	scriptLogger.error(e.getLocalizedMessage(), e);
	throw new RuntimeException(e);
}

String ExportPath = com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory();
String exportDestPath = ExportPath + exportDestFileName;
String exportDestPathODT = ExportPath + exportDestFileName + ".odt";

//scriptLogger.debug("Output file is " + exportDestPath);

new File(com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory()).mkdirs();
new File(ExportPath).mkdirs();

String exportODTFilePath = com.digitech.dossier.common.utils.ExportUtils.getODTDirectory() + File.separator + MODEL_ODT_NAME;

ScriptResultValueExportInitializer result = new ScriptResultValueExportInitializer(); 

try 
{	
	generateListing( UserContext.getInstance(), 
	exportDestPath, 
	ExportPath, 
	documents,  
	DossierCoreContext.getParamsInfos().getOfficeConfiguration(), 
	result,
	DATE_FIELD_CODE,
	FOLDER_FIELD_CODE,
	TYPE_FIELD_CODE,
	DATE_TITLE_LABEL, 
	FOLDER_TITLE_LABEL,
	TYPE_TITLE_LABEL,
	PAGE_TITLE_LABEL,
	DATE_FORMAT,
	HEADER_FILE_ABSOLUTE_PATH,
	FOLDER_HEADER_FILE_ABSOLUTE_PATH,
	AL_TYPE_ID
	);
	result.setFileResultName(exportDestFileName);
	result.setFileResultPath(exportDestPath);
}
catch(IOException e) 
{
	scriptLogger.error(e.getLocalizedMessage(), e);
	throw new RuntimeException(e);
}
catch(DocumentTemplateException e) 
{
	scriptLogger.error(e.getLocalizedMessage(), e);
	throw new RuntimeException(e);
}

//download(exportDestPath);
output.setValue(result);

/******************************* listing generation ******************************/
public  void generateListing(UserCoreContext usrContext, 
	String resultFilePath, 
	String tempFilePath, 
	List<IDocument> docList,
    DefaultOfficeManagerConfiguration configuration, 
	ScriptResultValueExportInitializer result,
	String DATE_FIELD_CODE,
	String FOLDER_FIELD_CODE,
	String TYPE_FIELD_CODE,
	String DATE_TITLE_LABEL, 
	String FOLDER_TITLE_LABEL,
	String TYPE_TITLE_LABEL,
	String PAGE_TITLE_LABEL,
	String DATE_FORMAT,
	String HEADER_FILE_ABSOLUTE_PATH,
	String FOLDER_HEADER_FILE_ABSOLUTE_PATH,
	String AL_TYPE_ID
	)
    throws FileNotFoundException, ServerException, IdentificationException, DocumentException, IOException, DocumentTemplateException,
    com.lowagie.text.DocumentException, DocumentOperationException {

	String   FILE_EXTENSION_PDF                = ".pdf";
	String[] FILE_EXTENSIONS_TIF               =  [".tif", ".tiff"];

	/** Defines extensions which can be converted to ODT */
	String[]  EXTENSIONS_AUTO_CONVERTION_TO_ODT =  com.digitech.dossier.common.service.export.impl.OdtGenerator.EXTENSIONS_AUTO_CONVERTION_TO_ODT;
	/** Defines extensions which can be converted to PDF */
	String[]  EXTENSIONS_AUTO_CONVERTION_TO_PDF = com.digitech.dossier.common.service.export.impl.OdtGenerator.EXTENSIONS_AUTO_CONVERTION_TO_PDF;
 
	// model to set the position of the stamper
	PdfRenumberFontModel pdfFont = new PdfRenumberFontModel();
	pdfFont.setRectangleYPosition("10");
	List<String> streamOfPDFFiles=new ArrayList<String>();
	try 
	{
		if(docList.size()==1 && docList.get(0).getAttachments(usrContext).size()==1)
		{
		}
		else
		{
			streamOfPDFFiles.add(new File(FOLDER_HEADER_FILE_ABSOLUTE_PATH));
		}
		File resultFile = new File(resultFilePath);
		
		// -- entete -- //
		ArrayList<String> docDate=new ArrayList<String>();
		ArrayList<String> docFolder=new ArrayList<String>();
		ArrayList<String> docType=new ArrayList<String>();
		ArrayList<String> docPageNumber=new ArrayList<String>();
		String pageNumber = 2;
		// -- /entete -- //
				
		for(IDocument currentDoc : docList) 
		{			
			List<IAttachment> attatchmentList = currentDoc.getAttachments(usrContext);
			
			for(IAttachment currentAttachement : attatchmentList) 
			{		  
				String downPathCurrentAttachment = downloadAttachment(tempFilePath, currentDoc, currentAttachement);
				//scriptLogger.debug("Current file is " + downPathCurrentAttachment);
				String converteddownPathCurrentAttachment = null;
				File pdfFile = null;
				// need a conversion ??
				// -- entete -- //
				
				// -- /entete -- //
				if(com.digitech.common.lib.utils.StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), FILE_EXTENSION_PDF)) 
				{ // no conversion needed 
			
					converteddownPathCurrentAttachment = downPathCurrentAttachment;
					pdfFile = new File(downPathCurrentAttachment);
				}
				else if(com.digitech.common.lib.utils.StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), FILE_EXTENSIONS_TIF)) 
				{ 	// tiff
					File currentFile = new File(downPathCurrentAttachment);
					pdfFile = new File(downPathCurrentAttachment + FILE_EXTENSION_PDF);
					// conversion
					//scriptLogger.debug("File need TIF convertion");
					getDocumentTiffConvectorService().convert(currentFile, pdfFile, Collections.EMPTY_MAP);
					converteddownPathCurrentAttachment = downPathCurrentAttachment + FILE_EXTENSION_PDF;
					// scriptLogger.debug("File converted to "+converteddownPathCurrentAttachment);
				}
				else if(com.digitech.common.lib.utils.StringUtils.isExtensionIgnoreCase(currentAttachement.getFileName(), EXTENSIONS_AUTO_CONVERTION_TO_PDF)) 
				{ // odt
					File currentFile = new File(downPathCurrentAttachment);
					pdfFile = new File(downPathCurrentAttachment + FILE_EXTENSION_PDF);
					// conversion
					// scriptLogger.debug("File need OOo convertion");
					getDocumentConversionService().convert(currentFile, pdfFile);
					converteddownPathCurrentAttachment = downPathCurrentAttachment + FILE_EXTENSION_PDF; // conversion
					// scriptLogger.debug("File converted to "+converteddownPathCurrentAttachment);
				}
				scriptLogger.debug("Adding "+converteddownPathCurrentAttachment +" to merge list or master file?");
			
				// -- entete -- //
			
				//scriptLogger.debug("entete1");
				DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
				
				if(currentDoc.getField(DATE_FIELD_CODE).getValue() != null)
				{
					String dateDoc = dateFormat.format(currentDoc.getField(DATE_FIELD_CODE).getValue());
					docDate.add(dateDoc);
				}
				else 
				{
					docDate.add("");
				}
				
				//scriptLogger.debug("entete1.1");
				
				if(currentDoc.getField(FOLDER_FIELD_CODE).getValue() != null)
				{
					String folder = currentDoc.getField(FOLDER_FIELD_CODE).getValue();					
					docFolder.add(folder);
				}
				else 
				{
					docFolder.add("");
				}
				
				//scriptLogger.debug("entete1.2");
				
				if (currentDoc.getField(TYPE_FIELD_CODE).getValue() != null)
				{
					List<AuthorityListTermAdmin> listValues = AuthorityListsManager.loadTermRoots(userContext.getJeton(), Integer.parseInt(AL_TYPE_ID));
				
					for (AuthorityListTermAdmin tal : listValues) 
					{
						if (tal.getId() == currentDoc.getField(TYPE_FIELD_CODE).getValue())
						{
							docType.add(tal.getValue().toString());
						}
						else
						{
							for (AuthorityListTermAdmin talChild : tal.loadChildren())
							{
								if (talChild.getId() == currentDoc.getField(TYPE_FIELD_CODE).getValue())
								{
									docType.add(talChild.getValue().toString());
								}
							}
						}
					}
				}			
				else
				{
					docType.add("");
				}
					
				//scriptLogger.debug("entete1.3");				
				docPageNumber.add(pageNumber);
				if(converteddownPathCurrentAttachment != null && (new File(converteddownPathCurrentAttachment) != null || new File(converteddownPathCurrentAttachment).exists())){
					try{
						int tmp = new PdfReader(converteddownPathCurrentAttachment).getNumberOfPages();
						tmp += Integer.parseInt(pageNumber);
						pageNumber = "" + tmp;
						//scriptLogger.debug("entete2");
						
						//scriptLogger.debug(converteddownPathCurrentAttachment+" added");
						streamOfPDFFiles.add(converteddownPathCurrentAttachment);
					}catch(Exception e){
						scriptLogger.error("Erreur : ",e);
					}
				}
			}			
		}
		//scriptLogger.debug("Merging " + streamOfPDFFiles.size() + " files")
   
		// --START ENTETE-- //
		//scriptLogger.debug("6");
		String enteteFile = HEADER_FILE_ABSOLUTE_PATH;		
		File enteteFolderFile = new File(FOLDER_HEADER_FILE_ABSOLUTE_PATH);
		File entete = new File(enteteFile);
		PDPage enteteTemplate = PDDocument.load(entete).getDocumentCatalog().getAllPages().get(0);
		PDDocument enteteDoc = new PDDocument();
		enteteDoc.importPage(enteteTemplate);
		int pageNb=0;
		
		//generating header page
		PDPage entetePage=(PDPage)enteteDoc.getDocumentCatalog().getAllPages().get(pageNb);
		//scriptLogger.debug("7");
		//vars
		PDFont font = PDType1Font.HELVETICA_BOLD;
		float fontSize=10;
		float y=565f;
		float decalageY=-15f;
		float dateX=50;
		float folderX=120;
		float typeX=215;
		float pageNumX=500;
		String dateTitle = DATE_TITLE_LABEL;
		String folderTitle = FOLDER_TITLE_LABEL;
		String docTypeTitle = TYPE_TITLE_LABEL;
		String pageNumTitle = PAGE_TITLE_LABEL;
		
		//setting titles
		PDPageContentStream contentStream = new PDPageContentStream(
				enteteDoc, entetePage, true, true);
		//scriptLogger.debug("8");
		contentStream.beginText();
		// set the font and size
		contentStream.setFont(font, fontSize);
		// set the text position x,y
		contentStream.moveTextPositionByAmount(dateX, y);
		contentStream.drawString(dateTitle); // draw it
		contentStream.endText();
		//scriptLogger.debug("9");
		contentStream.beginText();
		// set the font and size
		contentStream.setFont(font, fontSize);
		// set the text position x,y
		contentStream.moveTextPositionByAmount(folderX, y);
		contentStream.drawString(folderTitle); // draw it
		contentStream.endText();
		
		contentStream.beginText();
		// set the font and size
		contentStream.setFont(font, fontSize);
		// set the text position x,y
		contentStream.moveTextPositionByAmount(typeX, y);
		contentStream.drawString(docTypeTitle); // draw it
		contentStream.endText();
		
		contentStream.beginText();
		// set the font and size
		contentStream.setFont(font, fontSize);
		// set the text position x,y
		contentStream.moveTextPositionByAmount(pageNumX, y);
		contentStream.drawString(pageNumTitle); // draw it
		contentStream.endText();		
		
		//scriptLogger.debug("10");
		//adding informations		
		
		for(int i=0 ; i < docDate.size() ; i++)
		{
			//scriptLogger.debug("------------------------------------------------------for1");
			//scaling Y
			y=y+decalageY;
			//scriptLogger.debug("for2");
			if(y < 40f)
			{
				//scriptLogger.debug("y<40f : y="+y);
				y = 565f;
				contentStream.close();
				contentStream=null;
				enteteTemplate = PDDocument.load(new File(enteteFile)).getDocumentCatalog().getAllPages().get(0);				
				enteteDoc.importPage(enteteTemplate);				
				pageNb++;
				
				//generating header page
				PDPage entetePageNew=(PDPage)enteteDoc.getDocumentCatalog().getAllPages().get(pageNb);
				//scriptLogger.debug("new page added");
				contentStream = new PDPageContentStream(
						enteteDoc, entetePageNew, true, true);
						
				//new titles
				contentStream.beginText();
				// set the font and size
				contentStream.setFont(font, fontSize);
				// set the text position x,y
				contentStream.moveTextPositionByAmount(dateX, y);
				contentStream.drawString(dateTitle); // draw it
				contentStream.endText();
				//scriptLogger.debug("9");
				contentStream.beginText();
				// set the font and size
				contentStream.setFont(font, fontSize);
				// set the text position x,y
				contentStream.moveTextPositionByAmount(folderX, y);
				contentStream.drawString(folderTitle); // draw it
				contentStream.endText();
				
				contentStream.beginText();
				// set the font and size
				contentStream.setFont(font, fontSize);
				// set the text position x,y
				contentStream.moveTextPositionByAmount(typeX, y);
				contentStream.drawString(docTypeTitle); // draw it
				contentStream.endText();
				
				contentStream.beginText();
				// set the font and size
				contentStream.setFont(font, fontSize);
				// set the text position x,y
				contentStream.moveTextPositionByAmount(pageNumX, y);
				contentStream.drawString(pageNumTitle); // draw it
				contentStream.endText();
				
				y=y+decalageY;
		
			}
			
			//scriptLogger.debug("for3");
			contentStream.beginText();
			// set the font and size
			contentStream.setFont(font, fontSize);
			// set the text position x,y
			contentStream.moveTextPositionByAmount(dateX, y);
			contentStream.drawString(docDate.get(i)); // draw it
			contentStream.endText();
			//scriptLogger.debug("for4");
			contentStream.beginText();
			// set the font and size
			contentStream.setFont(font, fontSize);
			// set the text position x,y
			contentStream.moveTextPositionByAmount(folderX, y);
			contentStream.drawString(docFolder.get(i)); // draw it
			contentStream.endText();
			//scriptLogger.debug("for5");
			contentStream.beginText();
			//scriptLogger.debug("for5.1");
			// set the font and size
			contentStream.setFont(font, fontSize);
			//scriptLogger.debug("for5.2");
			// set the text position x,y
			contentStream.moveTextPositionByAmount(typeX, y);
			//scriptLogger.debug("for5.3");
			//scriptLogger.debug("type a definir : " + docType.get(i));
			contentStream.drawString(docType.get(i)); // draw it
			//scriptLogger.debug("for5.4");
			contentStream.endText();
			//scriptLogger.debug("for6");
			contentStream.beginText();
			// set the font and size
			contentStream.setFont(font, fontSize);
			// set the text position x,y
			contentStream.moveTextPositionByAmount(pageNumX, y);
			contentStream.drawString(docPageNumber.get(i)); // draw it
			contentStream.endText();
		}
		//scriptLogger.debug("11");
    
		// close the stream for that page
		contentStream.close();        
		enteteDoc.save(new FileOutputStream(enteteFolderFile));
			
		// --END ENTETE-- //
		
		//scriptLogger.debug("12");
		concatPDFs(streamOfPDFFiles, docDate, docType, new FileOutputStream(resultFile), false);		
		//scriptLogger.debug("13");
	}
	catch(Exception e) 
	{
		/*println("exception : ");
		println(e);
		println(e.getCause());*/
		  
		scriptLogger.error(e.getLocalizedMessage(), e);
		result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
		result.setMessageSummary("ACTION EXPORT PDF :");
		result.setMessageDetail("ERROR - Fusion des fichiers PDF impossible : "+e.getLocalizedMessage());
		// currentExportModel.setError(true);
	}	
}

private void concatPDFs(List<String> streamOfPDFFiles, List<String> dates, List<String> types, OutputStream outputStream, boolean paginate) throws Exception {
	
    com.lowagie.text.Document document = new com.lowagie.text.Document();
    try 
	{
		List<String> pdfs = streamOfPDFFiles;
		//scriptLogger.debug("Taille liste PDF = " + pdfs.size());
		List<PdfReader> readers = new ArrayList<PdfReader>();
		int totalPages = 0;
		Iterator<String> iteratorPDFs = pdfs.iterator();
	  
		int loop = -1;

		// Create Readers for the pdfs.
		while(iteratorPDFs.hasNext()) 
		{
			String pdf = iteratorPDFs.next();
			//scriptLogger.debug("-------------------------------------Fichier a ajouter = " + pdf);
			PdfReader pdfReader = new PdfReader(pdf);
			readers.add(pdfReader);
			//scriptLogger.debug("Reader ajoute");
			totalPages += pdfReader.getNumberOfPages();
		}
		//println("Nombre total de pages = " + totalPages);
		iteratorPDFs = pdfs.iterator();

		// Create a writer for the outputstream
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		//scriptLogger.debug("Writer cree");		
		writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);

		document.open();		
		//println("Fichier final ouvert");
		
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		PdfContentByte cb = writer.getDirectContent();
		PdfImportedPage page;
		int currentPageNumber = 0;
		int pageOfCurrentReaderPDF = 0;
		Iterator<PdfReader> iteratorPDFReader = readers.iterator();
		PdfOutline root = writer.getRootOutline();
		PdfOutline bookmark;
		PdfOutline bookmark2;
		Boolean toBookmarked=true;
		Boolean isHeader=true;
		// Loop through the PDF files and add to the output.
		while(iteratorPDFReader.hasNext()) 
		{
			//println("Loop " + loop);
			PdfReader pdfReader = iteratorPDFReader.next();
			//println("Reader ouvert");
			String currentFile = iteratorPDFs.next();
			//scriptLogger.debug("Fichier courant : " + currentFile);
			toBookmarked=!isHeader;

			int p = 1;
			// Create a new page in the target for each source page.
			while(pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) 
			{
				scriptLogger.debug("Num�ro document : "+p);
				
				pageOfCurrentReaderPDF++;
				currentPageNumber++;
				boolean isRotation = false;
				/*Modification Orientation*/
				if(pdfReader.getPageSize(pageOfCurrentReaderPDF).getWidth() > pdfReader.getPageSize(pageOfCurrentReaderPDF).getHeight()){
					document.setPageSize(new Rectangle(pdfReader.getPageSize(pageOfCurrentReaderPDF).getWidth(), pdfReader.getPageSize(pageOfCurrentReaderPDF).getHeight()));
					isRotation = true;
					document.setMargins(0, 0, 0, 0);
				} 
                else{					
					document.setPageSize(PageSize.A4);
					document.setMargins(0, 0, 0, 0);
				}
				/*Fin Modification Orientation*/
				
				document.newPage();				
				
				page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
				/*Modification Orientation*/
				Image image = Image.getInstance(page); 
				image.setDpi(72, 72);
				if(isRotation){
					float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / image.getWidth()) * 100;
					image.scalePercent(scaler);
				}
				
				if (pdfReader.getPageRotation(pageOfCurrentReaderPDF) == 90){
					image.setRotationDegrees(180); 
				}
                //document.add(image);
				cb.addTemplate(page, 0, 0);
				/*Fin Modification Orientation*/
				if(toBookmarked)
				{					
					String bookmarkName = new String();
					if (loop == -1)
					{
						bookmarkName = "Sommaire";
					}
					else
					{
						bookmarkName = dates.get(loop) + " - " + types.get(loop);
					}
					bookmark = new PdfOutline(root, new PdfDestination(PdfDestination.FITH, writer.getVerticalPosition(true)), bookmarkName, true);
					toBookmarked = false;
				}
				//println("Bookmark defini");
				if(bookmark != null)
					bookmark2 = new PdfOutline(bookmark, new PdfDestination(PdfDestination.FITH, writer.getVerticalPosition(true)), "Page " + pageOfCurrentReaderPDF, true);
				//println("Bookmark cree");
				// Code for pagination.
				if(paginate) 
				{
					cb.beginText();
					cb.setFontAndSize(bf, 7);
					cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Page " + currentPageNumber + " / " + totalPages, 275, 5, 0);
					cb.endText();
				}
				isHeader=false;
				isRotation=false;
			}
			pageOfCurrentReaderPDF = 0;
			loop += 1;
			p++;
		}
		try 
		{
			writer.flush();
			//writer.close();
		}
		catch(Exception e) 
		{
			 scriptLogger.error(e.getLocalizedMessage(), e);
		}
    }
    catch(Exception e) 
	{
		scriptLogger.error(e.getLocalizedMessage(), e);
    }
    finally 
	{
		if(document.isOpen()) 
		{
			document.close();
		}
		try 
		{
			outputStream.flush();

			if(outputStream != null) 
			{
				outputStream.close();
			}
		}
		catch(IOException ioe) 
		{
			scriptLogger.error(e.getLocalizedMessage(), e);
		}
    }
  }
  
  
    public static void PdfRenumberWithStamp(File pdfInputFile, Integer startPage, PdfRenumberFontModel renumberModel)
    throws IOException, DocumentException {

		PdfReader reader = new PdfReader(new FileInputStream(pdfInputFile));
		int n = reader.getNumberOfPages();

		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdfInputFile));
 
		try 
		{
			int i = 0;
		  
			PdfContentByte over;

			BaseFont bf = BaseFont.createFont(renumberModel.getFont(), BaseFont.WINANSI, BaseFont.EMBEDDED);
			while(i < n) 
			{
				i++;
				Rectangle page = reader.getPageSize(i);
				over = stamp.getOverContent(i);
				over.beginText();
				over.setFontAndSize(bf, renumberModel.getFontSize());
				float x_position = 0;
				float y_position = 0;
				  
				if( renumberModel.getRectangleXPosition().compareToIgnoreCase(PdfRenumberFontModel.POS_H_CENTER)==0)
				{
					x_position = page.getWidth()/2;
				}
				else
				{
					x_position = Float.parseFloat(renumberModel.getRectangleXPosition());
				}
				  
				if( renumberModel.getRectangleYPosition().compareToIgnoreCase(PdfRenumberFontModel.POS_V_CENTER)==0)
				{
					y_position = page.getHeight()/2;
				}
				else
				{
					y_position = Float.parseFloat(renumberModel.getRectangleYPosition());
				}
					
				over.showTextAligned(Element.ALIGN_CENTER, "page " + (startPage + i - 1) +" / "+n, x_position  , y_position, 0);
				over.setTextMatrix(30, 30);
				over.setColorFill(Color.GREEN);
				over.endText();
			}
		}
		finally 
		{
			stamp.close();
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
public String getMime(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        String ctype = "application/force-download";

        if (ext.equalsIgnoreCase("pdf"))
            ctype = "application/pdf";
        if (ext.equalsIgnoreCase("class"))
            ctype = "application/java";
        if (ext.equalsIgnoreCase("jar"))
            ctype = "application/java-archive";
        if (ext.equalsIgnoreCase("exe"))
            ctype = "application/octet-stream";
        if (ext.equalsIgnoreCase("zip"))
            ctype = "application/zip";
        if (ext.equalsIgnoreCase("doc"))
            ctype = "application/msword";
        if (ext.equalsIgnoreCase("xls"))
            ctype = "application/vnd.ms-excel";
        if (ext.equalsIgnoreCase("ppt"))
            ctype = "application/vnd.ms-powerpoint";
        if (ext.equalsIgnoreCase("gif"))
            ctype = "image/gif";
        if (ext.equalsIgnoreCase("png"))
            ctype = "image/png";
        if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg"))
            ctype = "image/jpg";
        if (ext.equalsIgnoreCase("mp3"))
            ctype = "audio/mpeg";
        if (ext.equalsIgnoreCase("wav"))
            ctype = "audio/x-wav";
        if (ext.equalsIgnoreCase("ogg"))
            ctype = "application/ogg";
        if (ext.equalsIgnoreCase("mpeg") || ext.equalsIgnoreCase("mpg")
                || ext.equalsIgnoreCase("mpe"))
            ctype = "video/mpeg";
        if (ext.equalsIgnoreCase("wmv"))
            ctype = "video/x-ms-wmv";
        if (ext.equalsIgnoreCase("mov"))
            ctype = "video/quicktime";
        if (ext.equalsIgnoreCase("avi"))
            ctype = "video/x-msvideo";
        if (ext.equalsIgnoreCase("php") || ext.equalsIgnoreCase("htm")
                || ext.equalsIgnoreCase("html"))
            ctype = "text/html";
        if (ext.equalsIgnoreCase("c") || ext.equalsIgnoreCase("java")
                || ext.equalsIgnoreCase("mf") || ext.equalsIgnoreCase("ads")
                || ext.equalsIgnoreCase("adb") || ext

                .equalsIgnoreCase("txt"))
            ctype = "text/plain";

        return ctype;

    }

    public void download(String filename) {

        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse res = (HttpServletResponse) context
                .getExternalContext().getResponse();

        res.setContentType(getMime(filename));

        // filename=getPath()+filename;
        // System.out.scriptLogger.debug("filename=" + filename);
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try 
		{

            fis = new FileInputStream(filename);
            bis = new BufferedInputStream(fis);
        } catch (FileNotFoundException ex) 
		{
            Logger.getLogger(FileDownloadController.class.getName()).log(Level.SEVERE, null, ex);
        }
        File f = null;
        try 
		{
            f = new File(filename);
            res.reset();
            res.resetBuffer();
            res.setContentLength((int) (f.length()));
            res.setHeader("Content-Type", "application/force-download; name=\""
                    + f.getName() + "\"");

            res.setHeader("Content-Disposition", "attachment; filename=\""
                    + f.getName() + "\"");
            res.setHeader("Expires", "0");
            res.setHeader("Cache-Control", "no-cache, must-revalidate");
            res.setHeader("Pragma", "no-cache");

        }

        catch (Exception e) 
		{
            scriptLogger.error("error setting html header : " + e.getMessage());
        }

        int read = 0;
        if (bis != null)
            try 
			{
                ServletOutputStream out = res.getOutputStream();
                byte[] buffer = new byte[1000];
                while ((read = fis.read(buffer)) != -1) 
				{
                    out.write(buffer, 0, read);
                }

                res.flushBuffer();
            } catch (IOException ex1) 
			{
                scriptLogger.error(e.getLocalizedMessage(), e);
            }

        context.renderResponse();
        context.responseComplete();

    }
        public String getPath() {

        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        String webRoot = request.getRealPath("/");
        return webRoot;
    }