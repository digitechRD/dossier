import Constants
import Methods
import com.aspose.slides.Presentation
import com.digitech.common.image.IImage
import com.digitech.common.image.ImageFactory
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.jcorbairs.Document
import com.digitech.report.service.impl.ooo.DocumentConvertionService
import org.apache.pdfbox.pdmodel.PDDocument

/*************************************************************************************************
 *   					    			SetCountPagesModifiedDocumentTask - EXEC
 **************************************************************************************************
 Date : 15.03.2016
 Auteur : MTO

 Description : Permet de mettre à jour le nombre de pages des documents modifiés via la visionneuse Digitech (suppression de page)
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT TASK - START")

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null
Integer countDocumentTreating = 0

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  //License wordsLicense = new License()
  //wordsLicense.setLicense(ApplicationUtils.getXMLConfigurationFolderPath() + "Aspose.Words.lic")
  listDocumentToTreat = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_MODIFIED_DOCUMENTS)
  scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT TASK - Nombre de documents à traiter : " + String.valueOf(listDocumentToTreat.size()))
  if(!listDocumentToTreat.isEmpty()) {
    File folder = new File(ApplicationUtils.getUserDownloadFolderPath(String.valueOf(DossierCoreContext.getAdminJeton().getTokenValue())))
    if(!folder.exists())
      folder.mkdir()
    for(Integer id : listDocumentToTreat) {
      Document document = new Document(DossierCoreContext.getAdminJeton(), id)
      PDDocument pddocument = null
      if(!document.getPrimaryDocList().isEmpty()) {
        try {
          File file = document.getPrimaryDocument(document.getPrimaryDocList().get(0), folder.getAbsolutePath())
          if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_PDF_EXTENSION)) {
            pddocument = PDDocument.load(file)
            Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(pddocument.getNumberOfPages()))
            document.updateContent()
          }
          else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_WORD_EXTENSION)) {
            File tmp = new File(Long.toString(new Date().getTime()) + Constants.APPLICATION_PDF_EXTENSION.toLowerCase())
            new DocumentConvertionService().convert(file, tmp)
            pddocument = PDDocument.load(tmp)
            Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(pddocument.getNumberOfPages()))
            Methods.deleteFile(tmp)
            document.updateContent()
          }
          else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_POWERPOINT_EXTENSION)) {
            FileInputStream fis = null
            try {
              fis = new FileInputStream(file)
              Presentation presentation = new Presentation(fisS)
              Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(presentation.getSlides().size()))
              document.updateContent()
            }
            finally {
              try {
                fis?.close()
              }
              catch(IOException ignored) {
              }
            }
          }
          else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_TIF_EXTENSION)) {
            IImage tiffImage = null
            try {
              tiffImage = ImageFactory.buildImageFromFileName(file.getAbsolutePath())
              Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, String.valueOf(tiffImage.getImageCount()))
              document.updateContent()
            }
            finally {
              if(tiffImage != null) {
                tiffImage.close()
              }
            }
          }
          else {
            Methods.defineDocumentIndex(document, Constants.FIELD_NUMBER_PAGES_CODE, null)
            document.updateContent()
          }
          countDocumentTreating++
          file.delete()
        } catch(Exception ex) {
          scriptLogger.error("[CUSTOM ACTION] - SetCountPagesModifiedDocumentsTask - ERROR :  Traitement du document impossible ", ex)
        } finally {
          try {
            pddocument.close()
          }
          catch(IOException ioe) {
            scriptLogger.error("Error while closing pdf file '{}'", file, ioe)
          }
        }
      }
      else
        scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT TASK - Document sans pièce jointe : " + id)
    }
    scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT TASK - Nombre de documents traités : " + countDocumentTreating)
  }
} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - SetCountPagesModifiedDocumentsTask - ERROR :  ", e)
}

scriptLogger.debug("[CUSTOM ACTION] - SET COUNT PAGES MODIFIED DOCUMENT TASK - END")