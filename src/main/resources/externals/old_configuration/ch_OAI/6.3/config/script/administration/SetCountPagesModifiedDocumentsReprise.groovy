import com.aspose.slides.Presentation
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.jcorbairs.Document
import com.lowagie.text.pdf.RandomAccessFileOrArray
import com.lowagie.text.pdf.codec.TiffImage
import org.apache.commons.io.FileUtils
import org.apache.pdfbox.pdmodel.PDDocument
import Constants
import Methods

/*************************************************************************************************
 *   					    			SetCountPagesModifiedDocumentReprise - EXEC
 **************************************************************************************************
 Date : 15.03.2016
 Auteur : MTO

 Description : Permet de mettre à jour le nombre de pages des documents modifiés via la visionneuse Digitech (suppression de page)
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT REPRISE - START");

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null;

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    listDocumentToTreat = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_ALL_DOCUMENTS);
    if(!listDocumentToTreat.isEmpty()) {
        File folder = new File(ApplicationUtils.getUserDownloadFolderPath(String.valueOf(DossierCoreContext.getAdminJeton().getTokenValue())));
        if (!folder.exists()) folder.mkdir();
        for (Integer id : listDocumentToTreat) {
            try {
                Document document = new Document(DossierCoreContext.getAdminJeton(), id);
                File file = document.getPrimaryDocument(document.getPrimaryDocList().get(0), folder.getAbsolutePath());
                if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_PDF_EXTENSION)) {
                    PDDocument pddocument = PDDocument.load(file);
                    Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(pddocument.getNumberOfPages()));
                    document.updateContent();
                } else if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_WORD_EXTENSION)) {
                    com.aspose.words.Document doc = new com.aspose.words.Document(new FileInputStream(file));
                    Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(doc.getPageCount()));
                    document.updateContent();
                } else if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_POWERPOINT_EXTENSION)) {
                    Presentation presentation = new Presentation(new FileInputStream(file));
                    Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(presentation.getSlides().size()));
                    document.updateContent();
                } else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_TIF_EXTENSION)){
                    Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(TiffImage.getNumberOfPages(new RandomAccessFileOrArray(FileUtils.readFileToByteArray(file)))));
                    document.updateContent();
                }
                System.gc();
                file.delete();
            }catch(Exception ex){
                scriptLogger.error("[CUSTOM ACTION] - SetCountPagesModifiedDocumentReprise - ERROR :  Traitement du document impossible ",ex);
            }

        }
    }
}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - SetCountPagesModifiedDocumentReprise - ERROR :  ",e);
}

scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT REPRISE - END");