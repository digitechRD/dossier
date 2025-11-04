import java.util.List

import org.slf4j.Logger

import com.digitech.courrier.common.controller.MultipleVisaController
import com.digitech.courrier.common.model.MultipleVisaModel
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel
import com.digitech.dossier.common.service.IDocument
import com.digitech.dossier.common.service.IDocuments
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.ServiceManager

import static OCSDScriptUtilsRectoVerso

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;

log.debug("Script triggered on checks control : initChekRectoVersoControlPanelAction.groovy --- Start");
// desactive standart button
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.setModalPanelButtonsRendered(false);
customActionModel.setModalPanelHeight( 725 );
customActionModel.setModalPanelWidth( 650 );

//start controller
MultipleVisaController multipleVisaController = OCSDScriptUtilsRectoVerso.getMultipleVisaController();
multipleVisaController.start();
multipleVisaController.getModel().clear();

List<IDocument> selectedDocumentsList =  Utils.getSearchResultTableController().getModel().getAllDocuments();
log.debug("check Control RectoVerso : " + selectedDocumentsList.size()  + " selected documents " ) ;

if( selectedDocumentsList.size() > 0 )
{	
	// init the documents list
	MultipleVisaModel multipleVisaModel = multipleVisaController.getModel();
	multipleVisaModel.setSelectedDocuments( selectedDocumentsList );

	// init the first document
	com.digitech.dossier.common.model.backend.airs.IDocument firstDocument = selectedDocumentsList.get(0);
	OCSDScriptUtilsRectoVerso.setDocument( usrContext, log, firstDocument );
	Utils.getCustomActionController().getModel().setOutcome(null);
}

log.debug("Script triggered on checks cosntrol : initChekRectoVersoControlPanelAction.groovy --- End");

