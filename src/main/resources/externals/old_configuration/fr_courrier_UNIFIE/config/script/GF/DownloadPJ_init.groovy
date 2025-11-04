import org.slf4j.Logger;

import com.digitech.courrier.common.model.ResponseModel;
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel;
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField

import com.digitech.dossier.common.service.IDocuments;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.report.service.IDocumentConvertionService;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.ExportUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.MessageUtils;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.common.utils.UserUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

//Input parameters
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;


log.debug("Script triggered on init custom Modal Panel: DownloadPJ_init.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.clear();

// recupérer la PJ
//On récupère la liste des pièces jointes du document
  ArrayList<Integer> tmpDocIdList = new ArrayList<Integer>();
  tmpDocIdList.add(theDocument.getAirsRefId());
  Map<Integer, List<IAttachment>> attachmentsList = getDocumentsService().getAttachments(UserUtils.getAdminUserContext(), tmpDocIdList); 
  List<IAttachment> attachmentList = attachmentsList.get(theDocument.getAirsRefId());
  
//  Integer   NbIn=0;
  String strFilePjPath, strFileName;
  File attachedFile=null;

  //On vérifie que le document a bien une pièce jointe
  if(attachmentList!=null && attachmentList.size()>0) {
    for(IAttachment att : attachmentList){
      log.debug("Script DownloadPJ_init.groovy --- Pj type :"+att.getType());    
      log.debug("Script DownloadPJ_init.groovy --- nom de la piece jointe : "+att.getFileName());
      
      // rechercher la dernière piece jointe du document      #########################
    
      //On tÃ©lÃ©charge le fichier de l'attachment
      String attachedFilePath = null;
      attachedFilePath=ExportUtils.getExportPDFDirectory(); 
      new File(attachedFilePath).mkdirs();		
      attachedFile = getDocumentMgr().loadDocumentAttachment(UserUtils.getAdminUserContext(),  theDocument,  att,attachedFilePath);
                 
      break; // en premndre que le 1er
    }
  }


// positionner le fichier pour le download
  if(attachedFile!=null) {
  
    log.debug("Script DownloadPJ_init.groovy --- attachedFile!=null : "+attachedFile.getName());
  
    // lire le champ   FIN_FAC_FILENAME
    String strFileNameDest = FieldUtils.getValue(theDocument, "GF_PJ_NOM")+".pdf";
    log.debug("Script DownloadPJ_init.groovy --- strFileNameDest : "+strFileNameDest);
  
  
    // ADD THE document TO THE DOWNLOADER
    // Construct the path
//    String pathToMain = usrContext.getUserDownloadPath() + File.separator + attachedFile.getName();
    String pathToMain = usrContext.getUserDownloadPath() + File.separator + strFileNameDest;        // nouveau nom de fichier pour download
    File finalMain = new File( pathToMain );
    // copy file to the user donwnload directory
    FileUtils.copyFile(attachedFile, finalMain);
    customActionModel.getModalPanelModel().put( "FileNameToDownload", strFileNameDest );            // nouveau nom de fichier pour affichage
    // put it in Model to be donwloaded
    customActionModel.getModalPanelModel().put( "docsigned", pathToMain );
    // Store it if needed !
    Utils.getGenericDownloader();

  }


  log.debug("Script DownloadPJ_init.groovy --- OK");


customActionModel.setModalPanelWidth(150);
customActionModel.setModalPanelHeight(150);
customActionModel.setModalPanelTitle("Téléchargement de la PJ");
customActionModel.getModalPanelModel().put("DownloadPJ", true);

//customActionModel.setCancelReRender(AttachmentModel.FORM_ID + ":" + CustomActionController.ID_ATTACHMENT_LAYOUT_UNIT_CONTENT);

log.debug("Script triggered on init custom Modal Panel: DownloadPJ_init.groovy --- End");



  private static IDocuments getDocumentsService() 
  {
     return (IDocuments) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENTS_MGR);
  }
  
  private static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
    return (com.digitech.dossier.common.service.impl.DocumentMgr) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
  }
  
  private static IUser getUserMgr() 
  {
  	return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }
  
  
 
