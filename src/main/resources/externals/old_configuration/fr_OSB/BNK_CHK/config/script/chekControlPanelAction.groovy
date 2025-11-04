import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.digitech.common.manager.ServiceManager;
import com.digitech.courrier.common.controller.MultipleVisaController;
import com.digitech.courrier.common.model.MultipleVisaModel;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.ErrorConfigurationController;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.airs.impl.Document.Comment;
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel;
import com.digitech.dossier.common.utils.ExpressionUtils;
import com.digitech.dossier.common.utils.NavigationUtils;

import static OCSDScriptUtils

// param
public org.slf4j.Logger log = scriptLogger;
public UserCoreContext usrContext = userContext;


log.debug("Script triggered on checks control : chekControlPanelAction.groovy --- Start");
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

String actionToDo =   (String)customActionModel.getModalPanelModel().get(Constants.ACTION_CONTROLLER);

log.debug("checks control : actionToDo => " + actionToDo);
if( StringUtils.isNotEmpty(actionToDo) )
{


	if(actionToDo.equals(ConstantsRectoVerso.ACTION_CANCEL) )
	{
		log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - getting next check");
		// On affiche la page des résultats de recherche
		Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
		log.debug("checks control Recto Verso : set out come to view unit");
	}
	else if(actionToDo.equals(Constants.ACTION_NEXT_CHEK) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - getting next check");
		OCSDScriptUtils.setNextDocument( usrContext, log);
		log.debug("checks control : actionToDo => " + actionToDo + " - end next check");
	}
	else if( actionToDo.equals(Constants.ACTION_PREVIOUS_CHEK) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - getting previous check");
		OCSDScriptUtils.getMultipleVisaController().getModel().previousDocument();
		IDocument previousDoc = OCSDScriptUtils.getMultipleVisaController().getModel().getCurrentDocument();
		OCSDScriptUtils.setDocument( usrContext, log, previousDoc );
		log.debug("checks control : actionToDo => " + actionToDo + " - end previous check");
	}
	else if(actionToDo.equals( Constants.ACTION_NEXT_SIGN ) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - getting next sign");
		Integer currentIdx = (Integer) customActionModel.getModalPanelModel().get(Constants.SIGN_NAV_CURRENT_IDX);
		MultipleVisaModel multipleVisaModel = OCSDScriptUtils.getMultipleVisaController().getModel();
		OCSDScriptUtils.setDocumentLinked(usrContext, log, (IAttachment) multipleVisaModel.getAvailableAttachmentsOut().get(currentIdx + 1).getValue() );
		OCSDScriptUtils.majCustomModelForSignNavigation( log, currentIdx + 1 );
		log.debug("checks control : actionToDo => " + actionToDo + " - end next sign");
	}
	else if( actionToDo.equals(Constants.ACTION_PREVIOUS_SIGN) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - getting previous sign");
		Integer currentIdx = (Integer) customActionModel.getModalPanelModel().get(Constants.SIGN_NAV_CURRENT_IDX);
		MultipleVisaModel multipleVisaModel = OCSDScriptUtils.getMultipleVisaController().getModel();
		OCSDScriptUtils.setDocumentLinked(usrContext, log, (IAttachment) multipleVisaModel.getAvailableAttachmentsOut().get(currentIdx - 1).getValue() );
		OCSDScriptUtils.majCustomModelForSignNavigation( log, currentIdx - 1 );
		log.debug("checks control : actionToDo => " + actionToDo + " - end previous sign");
	}

	else if( actionToDo.equals(Constants.ACTION_WAIT) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - try to change state of document");
		doActionForDocument( usrContext, log, Constants.FIELD_STATE_VALUE_WAIT, Constants.ACTION_WAIT);
		log.debug("checks control : actionToDo => " + actionToDo + " - end(mise en attente) try to change state of document");

	}
	else if( actionToDo.equals(Constants.ACTION_VALIDATE) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - try to change state of document");
		doActionForDocument( usrContext, log, Constants.FIELD_STATE_VALUE_VALIDATE , Constants.ACTION_VALIDATE);
		log.debug("checks control : actionToDo => " + actionToDo + " - end (validate) try to change state of document");
	}
	else if( actionToDo.equals(Constants.ACTION_REFUSED) )
	{
		log.debug("checks control : actionToDo => " + actionToDo + " - try to change state of document");
		doActionForDocument( usrContext, log, Constants.FIELD_STATE_VALUE_REFUSED, Constants.ACTION_REFUSED );
		log.debug("checks control : actionToDo => " + actionToDo + " - end (refused) try to change state of document");
	}

}
customActionModel.getModalPanelModel().put(Constants.ACTION_CONTROLLER, "");


log.debug("Script triggered on checks control : chekControlPanelAction.groovy --- End");


private doActionForDocument( UserCoreContext usrContext, org.slf4j.Logger log, Object fieldValue, String realFieldValue )
{
	log.debug("checks control : changing state of document");
	IDocument currentDocument = OCSDScriptUtils.getMultipleVisaController().getModel().getCurrentDocument();
	updateDocument(usrContext, log,currentDocument,Constants.FIELD_STATE_CODE,fieldValue);
	addDocumentEvent(usrContext, log,currentDocument,Constants.FIELD_STATE_CODE, realFieldValue);
	addDocumentComment( usrContext, log,currentDocument);
	OCSDScriptUtils.setNextDocument( usrContext, log);
}

private updateDocument(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate, String fieldCode, Object fieldValue)
{
	log.debug("checks control : setting field value (" + fieldValue + ") for the field " + documentToUpdate +" of the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;
	IField statutField = documentToUpdate.getField( fieldCode );
	statutField.setValue( fieldValue );
	log.debug("checks control : update document");
	OCSDScriptUtils.getDocumentMgr().updateDocument(usrContext, documentToUpdate);
	log.debug("checks control : end update document");

}

private addDocumentComment(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate)
{
	MultipleVisaModel multipleVisaModel = OCSDScriptUtils.getMultipleVisaController().getModel();
	String commentEvent = multipleVisaModel.getComment();
	if( StringUtils.isNotBlank(commentEvent))
	{
		log.debug("checks control : add comment ( "+ commentEvent  +" for the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;
		IComment backendComment = new Comment();
		backendComment.setComment(commentEvent);
		documentToUpdate.getComments().add(backendComment);
		OCSDScriptUtils.getDocumentMgr().updateDocumentComments(UserContext.getInstance(), documentToUpdate);	
		log.debug("checks control : END add comment ( "+ commentEvent  +" for the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;
	}

}

private addDocumentEvent(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate, String fieldCode, String realFieldValue)
{
	String commentEvent = realFieldValue;
	log.debug("checks control : add event ( "+ commentEvent  +" for the document ( " + documentToUpdate.getAirsRefId() + " ) "  + " event : " + Constants.EVENT_CHEK_CTRL + "_" + realFieldValue) ;

	// add the event in AIRS
	OCSDScriptUtils.getAuditService().addDocumentEvent(usrContext, documentToUpdate, Constants.EVENT_CHEK_CTRL + "_" + realFieldValue , commentEvent);
	log.debug("checks control : END add event");

}