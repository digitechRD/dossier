import java.util.UUID;

import org.ajax4jsf.component.html.HtmlAjaxCommandLink;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger

import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.html.HtmlGraphicImage;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.utils.ExpressionUtils;
import com.digitech.dossier.connector.model.IDossierInterface;
import com.digitech.dossier.connector.service.ConnectorFactory;
import com.digitech.dossier.connector.service.ConnectorUtils;
import com.digitech.dossier.connector.service.IDossierConnector;
import com.digitech.dossier.connector.service.ISBDossierConnetor;
import com.digitech.dossier.listener.DossierFileDownloader;
import com.digitech.dossier.script.exception.ScriptException;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.jcorbairs.exception.DocumentException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.dossier.common.Constants;
import com.digitech.dossier.common.Utils;
import com.sun.webui.html.HTMLAttributes;

import fr.digitech.connector.exception.ConnectorException;
import fr.digitech.connector.impl.signatureBook.constant.SignatureBookInternalConstant;
import fr.digitech.connector.impl.signatureBook.type.CommentRecord;
import fr.digitech.connector.impl.signatureBook.type.HistoryRecord;


// define interface name & owner id
String SBSRCI_INTERFACE_NAME = "INTERFACE_SB_SRCI";
String SBSRCI_INTERFACE_TYPE = "PARAPHEUR";


// param name 
String COMMENT_LIST = "commentRecord";
String HISTO_LIST = "histoRecord";
String DOC_SIGNED = "docsigned";
String STATE = "state";

Integer interfaceOwnerId = 1;

Logger log = scriptLogger;

log.debug("Script triggered on init custom Modal Panel: SBConsultation_init.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

// Setting panel size

customActionModel.setModalPanelWidth(540);
customActionModel.setModalPanelHeight(216);
//customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelResponse_title"));
customActionModel.setModalPanelTitle("Informations");

// retrieving information from SB SRCI
try
{

  // getting interface defined in AirsAdmin with name SB_INTERFACE_TYPE & owner Id interfaceOwnerId
  IDossierInterface dossierInterface = ConnectorFactory.getInstance().getInterfaces(SBSRCI_INTERFACE_TYPE, interfaceOwnerId);

  // from interface, we can now get the dossierConnector
  IDossierConnector dossierConnector = ConnectorFactory.getInstance().getConnector(dossierInterface);

  //many case here ..
  // cast dossierConnector into ISBDossierConnector
  ISBDossierConnetor sbConnector =  ((ISBDossierConnetor) dossierConnector);

  // get doucment status
  Integer state = sbConnector.getState(userContext, document.getAttachments(userContext).get(0) );
  customActionModel.getModalPanelModel().put( STATE , state );
  
  // we have to get history    
  List<HistoryRecord> sbHistoRecord = sbConnector.getHisto(userContext, document.getAttachments(userContext).get(0) );
  // put it in Model to be displayed
  customActionModel.getModalPanelModel().put( HISTO_LIST, sbHistoRecord );
  
  // we have to get comment 
  List<CommentRecord> sbCommentRecord = sbConnector.getComments(userContext, document.getAttachments(userContext).get(0) );
  // put it in Model to be displayed
  customActionModel.getModalPanelModel().put( COMMENT_LIST, sbCommentRecord );
  if(state.equals(SignatureBookInternalConstant.PARAPHEUR_ETAT_TRAITE )) 
  {
    // first, most simple , you just know the attachment  
    File mainFile_1 = sbConnector.get(userContext, document.getAttachments(userContext).get(0) );
    
    String pathToMain = userContext.getUserDownloadPath() + File.separator + mainFile_1.getName();
    File finalMain = new File( pathToMain ); 
    
    // copy file to the user donwnload directory
    FileUtils.copyFile(mainFile_1, finalMain);
    
    // put it in Model to be donwloaded
    customActionModel.getModalPanelModel().put( DOC_SIGNED, pathToMain );
  
      // store it if needed !
    Utils.getGenericDownloader();
  }  
}
catch(Exception ex)
{
  log.error("error", ex);
}


log.debug("Script triggered on init custom Modal Panel: SBConsultation_init.groovy --- End");