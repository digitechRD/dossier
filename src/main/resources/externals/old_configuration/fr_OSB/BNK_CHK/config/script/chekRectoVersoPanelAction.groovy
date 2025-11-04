import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.digitech.common.manager.ServiceManager;
import com.digitech.courrier.common.controller.MultipleVisaController;
import com.digitech.courrier.common.model.MultipleVisaModel;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.ErrorConfigurationController;
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

import static OCSDScriptUtilsRectoVerso

// param
public org.slf4j.Logger log = scriptLogger;
public UserCoreContext usrContext = userContext;


log.debug("Script triggered on checks control Recto Verso : chekControlPanelAction.groovy --- Start");
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

String actionToDo =   (String)customActionModel.getModalPanelModel().get(ConstantsRectoVerso.ACTION_CONTROLLER);

log.debug("checks control Recto Verso : actionToDo => " + actionToDo);

if(actionToDo.equals(ConstantsRectoVerso.ACTION_CANCEL) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - getting next check");
	// On affiche la page des résultats de recherche
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
	log.debug("checks control Recto Verso : set out come to view unit");
}
else if(actionToDo.equals(ConstantsRectoVerso.ACTION_NEXT_CHEK) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - getting next check");
	OCSDScriptUtilsRectoVerso.setNextDocument( usrContext, log);
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - end next check");
}
else if( actionToDo.equals(ConstantsRectoVerso.ACTION_PREVIOUS_CHEK) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - getting previous check");
	OCSDScriptUtilsRectoVerso.getMultipleVisaController().getModel().previousDocument();
	IDocument previousDoc = OCSDScriptUtilsRectoVerso.getMultipleVisaController().getModel().getCurrentDocument();
	OCSDScriptUtilsRectoVerso.setDocument( usrContext, log, previousDoc );
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - end previous check");
}
else if( actionToDo.equals(ConstantsRectoVerso.ACTION_WAIT) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - try to change state of document");
	doActionForDocument( usrContext, log, ConstantsRectoVerso.FIELD_STATE_VALUE_WAIT, ConstantsRectoVerso.ACTION_WAIT);
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - end (mise en attente) try to change state of document");
	
}
else if( actionToDo.equals(ConstantsRectoVerso.ACTION_VALIDATE) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - try to change state of document");
	doActionForDocument( usrContext, log, ConstantsRectoVerso.FIELD_STATE_VALUE_VALIDATE, ConstantsRectoVerso.ACTION_VALIDATE );
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - end (validate) try to change state of document");
}
else if( actionToDo.equals(ConstantsRectoVerso.ACTION_REFUSED) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - try to change state of document");
	doActionForDocument( usrContext, log, ConstantsRectoVerso.FIELD_STATE_VALUE_REFUSED, ConstantsRectoVerso.ACTION_REFUSED );
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - end (refused) try to change state of document");
}

customActionModel.getModalPanelModel().put(ConstantsRectoVerso.ACTION_CONTROLLER, "");

log.debug("Script triggered on checks control Recto Verso : chekControlPanelAction.groovy --- End");


private doActionForDocument( UserCoreContext usrContext, org.slf4j.Logger log, Object fieldValue, String realFieldValue )
{
	log.debug("checks control Recto Verso : changing state of document");
	IDocument currentDocument = OCSDScriptUtils.getMultipleVisaController().getModel().getCurrentDocument();
	updateDocument(usrContext, log,currentDocument,ConstantsRectoVerso.FIELD_STATE_CODE,fieldValue);
	addDocumentEvent(usrContext, log,currentDocument,ConstantsRectoVerso.FIELD_STATE_CODE,realFieldValue);
	addDocumentComment( usrContext, log,currentDocument);
	OCSDScriptUtilsRectoVerso.setNextDocument( usrContext, log);	
}

private updateDocument(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate, String fieldCode, Object fieldValue)
{
	log.debug("checks control Recto Verso : setting field value (" + fieldValue + ") for the field " + documentToUpdate +" of the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;
	IField statutField = documentToUpdate.getField( fieldCode );
	statutField.setValue( fieldValue );
	log.debug("checks control Recto Verso : update document");
	OCSDScriptUtilsRectoVerso.getDocumentMgr().updateDocument(usrContext, documentToUpdate);
	log.debug("checks control Recto Verso : end update document");

}

private addDocumentEvent(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate, String fieldCode, String realFieldValue)
{
	String commentEvent = realFieldValue;
	log.debug("checks control : add event ( "+ commentEvent  +" for the document ( " + documentToUpdate.getAirsRefId() + " ) "  + " event : " + ConstantsRectoVerso.EVENT_CHEK_CTRL + "_" + realFieldValue) ;

	// add the event in AIRS
	OCSDScriptUtilsRectoVerso.getAuditService().addDocumentEvent(usrContext, documentToUpdate, ConstantsRectoVerso.EVENT_CHEK_CTRL + "_" + realFieldValue , commentEvent);
  log.debug("checks control : END add event"); 
}

private addDocumentComment(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate)
{
	MultipleVisaModel multipleVisaModel = OCSDScriptUtilsRectoVerso.getMultipleVisaController().getModel();
	String commentEvent = multipleVisaModel.getComment();
	if( StringUtils.isNotBlank(commentEvent))
	{
		log.debug("checks control : add comment ( "+ commentEvent  +" for the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;
		IComment backendComment = new Comment();
		backendComment.setComment(commentEvent);
		documentToUpdate.getComments().add(backendComment);
		OCSDScriptUtilsRectoVerso.getDocumentMgr().updateDocumentComments(usrContext, documentToUpdate);
		log.debug("checks control : END add comment");
	}

}
