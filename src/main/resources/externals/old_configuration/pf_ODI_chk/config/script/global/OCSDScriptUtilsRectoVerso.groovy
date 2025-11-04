import com.digitech.dossier.common.model.backend.UserCoreContext;

import java.util.ArrayList
import java.util.List

import javax.el.ValueExpression
import javax.faces.context.FacesContext
import javax.faces.model.SelectItem

import org.apache.commons.lang.StringUtils

import com.akazi.flowmind.webapp.admind.Constants
import com.digitech.courrier.common.controller.MultipleVisaController
import com.digitech.courrier.common.model.MultipleVisaModel
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel
import com.digitech.dossier.common.service.IDocuments
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ExpressionUtils
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Field
import com.digitech.jcorbairs.SortCriterion
import com.digitech.jcorbairs.Sorting
import com.digitech.jcorbairs.utils.Direction


// local constants to be used in the groovy script
class Constants
{
	// event
	public static final EVENT_CHEK_CTRL="event_chek_control_recto_verso";

	//actions
	public static final ACTION_CONTROLLER  = "ctrlCheckAction";
	public static final CONTROLLER_MUlTIPLE_VISA  = "MultipleVisaController";
	public static final ACTION_PREVIOUS_CHEK  = "PREVIOUS_CHEK";
	public static final ACTION_NEXT_CHEK  = "NEXT_CHEK";
	public static final ACTION_VALIDATE  = "VALIDATE";
	public static final ACTION_REFUSED  = "REFUSED";
	public static final ACTION_WAIT  = "WAIT";

	//field code & value to set
	public static final FIELD_STATE_CODE = "CHQ_ETAT_SIGN";
	public static final Integer FIELD_STATE_VALUE_VALIDATE = 1711 ;
	public static final Integer FIELD_STATE_VALUE_REFUSED = 1712 ;
	public static final Integer FIELD_STATE_VALUE_WAIT = 1713 ;

	// audit manager informations
	public static final AUDITMGR_AUDIT_CODE =  "CHQ_ETAT_SIGN";
	public static final AUDITMGR_AUDIT_VALUE_VALIDATE =  "Validé";
	public static final AUDITMGR_AUDIT_VALUE_REFUSED =  "Refusé";
	public static final AUDITMGR_AUDIT_VALUE_WAIT =  "mis en attente";

	// for initialization
	static final CONTROLLER_MUlTIPLE_VISA  = "MultipleVisaController";	
}

/**
 * Common method for OSCD chek control management
 */
class OCSDScriptUtilsRectoVerso {

	// set next chek to control
	public static void setNextDocument( UserCoreContext usrContext, org.slf4j.Logger log) {
		MultipleVisaModel multipleVisaModel =getMultipleVisaController().getModel();
		if( multipleVisaModel.isNextDocumentExisting() )
		{
			log.debug("checks control Recto Verso : set next document");
			IDocument nextDoc = null;

			while (multipleVisaModel.isNextDocumentExisting() )
			{
				multipleVisaModel.nextDocument();
				nextDoc = getMultipleVisaController().getModel().getCurrentDocument();
				if( ( ( nextDoc.isLocked() && ( nextDoc.getLockType() != Document.LOCKEDBYOTHER ) )
				|| !nextDoc.isLocked() ) )
					break;
			}



			if( nextDoc != null )
				setDocument( usrContext, log, nextDoc );
		}
	}
	//set check to control
	public static void setDocument(UserCoreContext usrContext, org.slf4j.Logger log, IDocument doumentToSet ) {
		MultipleVisaModel multipleVisaModel =getMultipleVisaController().getModel();
		log.debug("checks control Recto Verso : setting  document ( " + doumentToSet.getAirsRefId() + " ) " ) ;
		multipleVisaModel.setCurrentDocument( doumentToSet ) ;

		// init the attachment
		List<IAttachment>  attachmentsInList = doumentToSet.getAttachments(usrContext);
		log.debug("checks control Recto Verso : " + attachmentsInList.size() + " attachments find" ) ;
		if( attachmentsInList.size() > 0 )
		{
			log.debug("checks control Recto Verso : setting first attachment for document ( " + doumentToSet.getAirsRefId() + " ) " ) ;
			AttachmentModel attachmentModel = new AttachmentModel();
			attachmentModel.setCurrentDocument(doumentToSet);
			attachmentModel.setCurrentAttachment(attachmentsInList.get(0));
			multipleVisaModel.setAttachmentInModel(attachmentModel);
		}	
	}

	public static MultipleVisaController getMultipleVisaController() {
		FacesContext context = Utils.getFacesContext();
		ValueExpression ve = ExpressionUtils.getValueExpression(Constants.CONTROLLER_MUlTIPLE_VISA);
		MultipleVisaController controller = (MultipleVisaController) ve.getValue(context.getELContext());
		if(controller == null) {
			controller = new MultipleVisaController();
			Utils.store(Constants.CONTROLLER_MUlTIPLE_VISA, controller);
		}
		return controller
	}

	private static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
		return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
	}

	private static IDocuments getDocumentsMgr() {
		return (IDocuments) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENTS_MGR);
	}
	private static IServer getServerMgr() {
		return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
	}
	private static com.digitech.dossier.common.service.IAuditService getAuditService() {
		return (com.digitech.dossier.common.service.IAuditService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
	}

}