import com.aspose.slides.Presentation
import com.aspose.words.License
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Domain
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

import com.digitech.report.service.impl.ooo.DocumentConvertionService
import com.lowagie.text.pdf.RandomAccessFileOrArray
import com.lowagie.text.pdf.codec.TiffImage
import org.apache.commons.io.FileUtils
import org.apache.pdfbox.pdmodel.PDDocument

import Constants
import Methods

/*************************************************************************************************
 *   					    			SetInformationsForNewDocumentTask - EXEC
 **************************************************************************************************
 Date : 12.04.2016
 Auteur : MTO

 Description : Permet de mettre à jour le groupe du document et le nom de l'assuré
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET INFORMATIONS FOR NEW DOCUMENT TASK - START");

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null;

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    License wordsLicense = new License();
    wordsLicense.setLicense(ApplicationUtils.getXMLConfigurationFolderPath()+"Aspose.Words.lic");
    listDocumentToTreat = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_CREATE_DOCUMENTS);
    scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Nombre de documents à traiter : "+listDocumentToTreat.size());
    if(!listDocumentToTreat.isEmpty()) {
        for(Integer id : listDocumentToTreat){
            try {
                Document doc = new Document(DossierCoreContext.getAdminJeton(), id);
                String typeId = null;
                String name = null;
                String groupId = null;
                String numberPage = null;

                try{
                    typeId = Methods.getFieldValue(doc, Constants.LIST_TYPES_DOCUMENT_CODE);
                    if(Constants.USE_GROUP_LIST) groupId = Methods.getFieldValue(doc, Constants.LIST_GROUPES_DOCUMENT_CODE);
                    name = Methods.getFieldValue(doc, Constants.FIELD_NAME_CODE);
                    numberPage = Methods.getFieldValue(doc, Constants.FIELD_NUMBER_PAGES_CODE);
                }catch(Exception e){
                    Methods.defineDocumentIndex(doc, Constants.LIST_TYPES_DOCUMENT_CODE, null);
                    Methods.defineDocumentIndex(doc, Constants.FIELD_NAME_CODE, null);
                    doc.updateContent();
                }

                if(typeId != null || name == null || name.isEmpty() || numberPage != null) {
                    if (typeId != null && groupId == null && Constants.USE_GROUP_LIST) {
                        AuthorityListTermAdmin altm = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Integer.parseInt(typeId));
                        String groupCode = altm.getValue5().replaceAll(";", "");
                        List<AuthorityListTermAdmin> listGroup = AuthorityListsManager.loadTerms(DossierCoreContext.getAdminJeton(), Constants.LIST_GROUPES_DOCUMENT_ID);
                        for (AuthorityListTermAdmin alta : listGroup) {
                            if (groupCode.equals(alta.getCode())) {
                                groupId = String.valueOf(alta.getId());
                                break;
                            }
                        }
                        scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Groupe - Document traité n° "+id+" / "+groupId+" / "+typeId);
                        Methods.defineDocumentIndex(doc, Constants.LIST_GROUPES_DOCUMENT_CODE, groupId);
                    }

                    if (name == null || name.isEmpty()) {
                        String nss = Methods.getFieldValue(doc, Constants.FIELD_NSS_CODE);
                        List<String> names = Methods.getRequestInWebAI(nss, "name");
                        name = (names.isEmpty())?"":names.get(0);
                        if(name.length()>25) name = name.substring(0,25);
                        Methods.defineDocumentIndex(doc, Constants.FIELD_NAME_CODE, name);
                        scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Name - Document traité n° "+id+" / "+name+" / "+nss);
                    }else scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Name :  "+name+" / Document n° "+id);

                    if(numberPage == null){
                        File folder = new File(ApplicationUtils.getUserDownloadFolderPath(String.valueOf(DossierCoreContext.getAdminJeton().getTokenValue())));
                        if (!folder.exists()) folder.mkdir();
                            PDDocument pddocument = null;
                            if(!doc.getPrimaryDocList().isEmpty()){
                                try {
                                    File file = doc.getPrimaryDocument(doc.getPrimaryDocList().get(0), folder.getAbsolutePath());
                                    if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_PDF_EXTENSION)) {
                                        pddocument = PDDocument.load(file);
                                        numberPage = String.valueOf(pddocument.getNumberOfPages());
                                    } else if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_WORD_EXTENSION)) {
                                        File tmp = new File(Long.toString(new Date().getTime())+Constants.APPLICATION_PDF_EXTENSION.toLowerCase());
                                        new DocumentConvertionService().convert(file,tmp);
                                        pddocument = PDDocument.load(tmp);
                                        numberPage = String.valueOf(pddocument.getNumberOfPages());
                                        Methods.deleteFile(tmp);
                                    } else if (StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_OFFICE_POWERPOINT_EXTENSION)) {
                                        Presentation presentation = new Presentation(new FileInputStream(file));
                                        numberPage = String.valueOf(presentation.getSlides().size());
                                    } else if(StringUtils.isExtensionIgnoreCase(file.getName(), Constants.APPLICATION_TIF_EXTENSION)){
                                        numberPage = String.valueOf(TiffImage.getNumberOfPages(new RandomAccessFileOrArray(FileUtils.readFileToByteArray(file))));
                                    } /*else {
                                        numberPage = "0";
                                    }*/
                                    Methods.defineDocumentIndex(doc, Constants.FIELD_NUMBER_PAGES_CODE, numberPage);
                                    if(file != null) file.delete();
                                    scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Page - Document traité n°"+id+" / "+numberPage);
                                }catch(Exception ex){
                                    scriptLogger.error("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - ERROR :  Traitement du document impossible ",ex);
                                }finally{
                                    if(pddocument != null) pddocument.close();
                                }
                            }else scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Document sans pièce jointe : " +id);
                    }

                    doc.updateContent();
                }

            }catch(Exception ex){
                scriptLogger.warn("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - ERROR Document n° :  "+id, ex);
            }
        }
    }

    // Dévérouillage des ocuments vérouiller depuis plus de 20 minutes
    scriptLogger.debug("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - Nombre de documents deverouillé : "+Methods.executeRequest(Constants.DB_AIRS_REQUEST_UNLOCK_DOCUMENTS));
    

}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - SetInformationsForNewDocumentTask - ERROR :  ",e);
}

scriptLogger.debug("[CUSTOM ACTION] - SET INFORMATIONS FOR NEW DOCUMENT TASK - END");