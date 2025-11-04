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
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Field
import com.digitech.jcorbairs.SortCriterion
import com.digitech.jcorbairs.Sorting
import com.digitech.jcorbairs.utils.Direction


// local constants to be used in the groovy script
class Constants
{
	// event
	public static final EVENT_CHEK_CTRL="event_chek_control";

	//actions
	public static final ACTION_CONTROLLER  = "ctrlCheckAction";
	public static final CONTROLLER_MUlTIPLE_VISA  = "MultipleVisaController";
	public static final ACTION_PREVIOUS_CHEK  = "PREVIOUS_CHEK";
	public static final ACTION_NEXT_CHEK  = "NEXT_CHEK";
	public static final ACTION_PREVIOUS_SIGN  = "PREVIOUS_SIGN";
	public static final ACTION_NEXT_SIGN  = "NEXT_SIGN";
	public static final ACTION_VALIDATE  = "VALIDATE";
	public static final ACTION_REFUSED  = "REFUSED";
	public static final ACTION_WAIT  = "WAIT";
	public static final ACTION_CANCEL  = "CANCEL";

	//field code & value to set
	public static final FIELD_STATE_CODE = "CHQ_ETAT_SIGN";
	public static final Integer FIELD_STATE_VALUE_VALIDATE = 1809 ;
	public static final Integer FIELD_STATE_VALUE_REFUSED = 1810 ;
	public static final Integer FIELD_STATE_VALUE_WAIT = 1811 ;

	// audit manager informations
	public static final AUDITMGR_AUDIT_CODE =  "CHQ_ETAT_SIGN";
	public static final AUDITMGR_AUDIT_VALUE_VALIDATE =  "Validé";
	public static final AUDITMGR_AUDIT_VALUE_REFUSED =  "Refusé";
	public static final AUDITMGR_AUDIT_VALUE_WAIT =  "mis en attente";

	// for initialization
	static final CONTROLLER_MUlTIPLE_VISA  = "MultipleVisaController";
	static final LINKEDCONTENTYPECODE		 = "D_DOC_OVC";
	static final CODELINK					 = "CHQ_CART";
	static final LINKDIRECTION			 = "child";

	// for sign navigation
	public static final SIGN_NAV_PREVIOUS  = "isSignPreviousExisting";
	public static final SIGN_NAV_NEXT  = "isSignNextExisting";
	public static final SIGN_NAV_CURRENT_IDX  = "currentSignIdx";

	//for sort
	public static final LINKED_DOC_FIELD_TO_SORT  = "D_CREAT";
}

/**
 * Common method for OSCD chek control management
 */
class OCSDScriptUtils {

	// set next chek to control
	public static void setNextDocument( UserCoreContext usrContext, org.slf4j.Logger log) {
		MultipleVisaModel multipleVisaModel =getMultipleVisaController().getModel();
		if( multipleVisaModel.isNextDocumentExisting() )
		{
			log.debug("checks control : set next document");
			IDocument nextDoc = null;

			while (multipleVisaModel.isNextDocumentExisting() )
			{
				multipleVisaModel.nextDocument();
				nextDoc = getMultipleVisaController().getModel().getCurrentDocument();
					log.debug("checks control : Next document : " + nextDoc.getAirsRefId());
				if( ( ( nextDoc.isLocked() && ( nextDoc.getLockType() != Document.LOCKEDBYOTHER ) )	|| !nextDoc.isLocked() ) )
				{
					log.debug("checks control : Next document : " + nextDoc.getAirsRefId() + "is not locked." );
					break;					
				}
				else
				{
				log.debug("checks control : Next document : " + nextDoc.getAirsRefId() + "is locked." );
        }
			}



			if( nextDoc != null )
				setDocument( usrContext, log, nextDoc );
//			else
//			{
//				// On affiche la page des résultats de recherche
//				Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
//			}
		}
//		else
//		{
//			// On affiche la page des résultats de recherche
//			Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
//		}
	}
	//set check to control
	public static void setDocument(UserCoreContext usrContext, org.slf4j.Logger log, IDocument doumentToSet ) {
		MultipleVisaModel multipleVisaModel =getMultipleVisaController().getModel();
		log.debug("checks control : setting  document ( " + doumentToSet.getAirsRefId() + " ) " ) ;
		multipleVisaModel.setCurrentDocument( doumentToSet ) ;

		// init the attachment
		List<IAttachment>  attachmentsInList = doumentToSet.getAttachments(usrContext);
		log.debug("checks control : " + attachmentsInList.size() + " attachments find" ) ;
		if( attachmentsInList.size() > 0 )
		{
			log.debug("checks control : setting first attachment for document ( " + doumentToSet.getAirsRefId() + " ) " ) ;
			AttachmentModel attachmentModel = new AttachmentModel();
			attachmentModel.setCurrentDocument(doumentToSet);
			attachmentModel.setCurrentAttachment(attachmentsInList.get(0));
			multipleVisaModel.setAttachmentInModel(attachmentModel);
		}

		log.debug("checkControl : trying to find linked document " ) ;

		// init the first linked document
		List<Integer> linkedDocumentsList = getDocumentsMgr().getLinkedDocuments(usrContext,doumentToSet.getAirsRefId(), getServerMgr().getDomain(usrContext.getJeton(),
				Constants.LINKEDCONTENTYPECODE), Constants.CODELINK, Constants.LINKDIRECTION);

		log.debug("checkControl : " +  linkedDocumentsList.size() + " linked docs find " ) ;
		if( linkedDocumentsList.size() > 0 )
		{

			if( linkedDocumentsList.size() > 1 )
			{
				log.debug("checkControl : linked doc - sorting needed " ) ;
				List<SortCriterion> sortCritery = new ArrayList<SortCriterion>();
				sortCritery.add(new SortCriterion(new Field(usrContext.getJeton(),Constants.LINKED_DOC_FIELD_TO_SORT ), Direction.DESCENDANT));
				Sorting resultSorting = new Sorting(sortCritery);

				com.digitech.jcorbairs.Document.sort(usrContext.getJeton(), linkedDocumentsList, resultSorting)

			}

			List<SelectItem> availableAttachmentsOut = new ArrayList<SelectItem>();

			for( Integer linkedDocID : linkedDocumentsList)
			{
				IDocument linekdDoc = getDocumentMgr().getDocument(usrContext, linkedDocID );
				log.debug("checkControl : set linked document ( " + linekdDoc.getAirsRefId() + " ) "  ) ;
				List<IAttachment>  attachmentsSignList = linekdDoc.getAttachments(usrContext);
				log.debug("checks control : " + attachmentsSignList.size() + " attachments find" ) ;
				for(IAttachment attachment : attachmentsSignList ) {
					String title = StringUtils.isBlank(attachment.getLabel()) ? attachment.getFileName() : attachment.getLabel();
					log.debug("checks control : setting " +  title ) ;
					availableAttachmentsOut.add(new SelectItem(attachment, title));
				}
			}

			log.debug("checkControl : add the " + availableAttachmentsOut.size() + " sign available"  ) ;
			multipleVisaModel.setAvailableAttachmentsOut( availableAttachmentsOut );
			setDocumentLinked(usrContext, log, (IAttachment) availableAttachmentsOut.get(0).getValue() );		
			majCustomModelForSignNavigation(log, 0 );
		}
		else
		{
		  log.debug("checkControl : finalyse initialization for next attachment" ) ;
			CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
			customActionModel.getModalPanelModel().put(Constants.SIGN_NAV_CURRENT_IDX, 0  );
			//previous existing ?
			customActionModel.getModalPanelModel().put(Constants.SIGN_NAV_PREVIOUS,false );
	
			// next existing ??
			customActionModel.getModalPanelModel().put(Constants.SIGN_NAV_NEXT, false ); ;
		}
		
	
	}

	public static majCustomModelForSignNavigation(org.slf4j.Logger log, Integer currentSignIdx)
	{
		log.debug("checkControl : Maj customActionModle for sign navigation");
		CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
		MultipleVisaModel multipleVisaModel =getMultipleVisaController().getModel();
		Integer signListSign = multipleVisaModel.getAvailableAttachmentsOut().size();
		//set current index
		customActionModel.getModalPanelModel().put(Constants.SIGN_NAV_CURRENT_IDX, currentSignIdx  );

		//previous existing ?
		customActionModel.getModalPanelModel().put(Constants.SIGN_NAV_PREVIOUS, currentSignIdx > 0 ? true : false );

		// next existing ??
		customActionModel.getModalPanelModel().put(Constants.SIGN_NAV_NEXT, currentSignIdx < ( signListSign - 1 ) ? true : false ); ;

	}

	public static setDocumentLinked(UserCoreContext usrContext, org.slf4j.Logger log, IAttachment signAttachment )
	{
		MultipleVisaModel multipleVisaModel =getMultipleVisaController().getModel()
		log.debug("checkControl : setting the sign attachment ( document : " + signAttachment.getDocument().getAirsRefId() + " - attachment : " + signAttachment.getId() + " )" ) ;
		AttachmentModel attachmentSignModel = new AttachmentModel();
		attachmentSignModel.setCurrentDocument(signAttachment.getDocument());
		attachmentSignModel.setCurrentAttachment(signAttachment);
		multipleVisaModel.setAttachmentOutModel( attachmentSignModel);
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
