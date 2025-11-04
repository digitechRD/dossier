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
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel;
import com.digitech.dossier.common.utils.ExpressionUtils;

import static OCSDScriptUtilsRectoVerso

// param
public org.slf4j.Logger log = scriptLogger;
public UserCoreContext usrContext = userContext;


log.debug("Script triggered on checks control Recto Verso : chekControlPanelAction.groovy --- Start");
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

String actionToDo =   (String)customActionModel.getModalPanelModel().get(Constants.ACTION_CONTROLLER);

log.debug("checks control Recto Verso : actionToDo => " + actionToDo);

if(actionToDo.equals(Constants.ACTION_NEXT_CHEK) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - getting next check");
	OCSDScriptUtilsRectoVerso.setNextDocument( usrContext, log);
}
else if( actionToDo.equals(Constants.ACTION_PREVIOUS_CHEK) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - getting previous check");
	OCSDScriptUtilsRectoVerso.getMultipleVisaController().getModel().previousDocument();
	IDocument previousDoc = OCSDScriptUtilsRectoVerso.getMultipleVisaController().getModel().getCurrentDocument();
	OCSDScriptUtilsRectoVerso.setDocument( usrContext, log, previousDoc );
}
else if( actionToDo.equals(Constants.ACTION_WAIT) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - try to change state of document");
	doActionForDocument( usrContext, log, Constants.FIELD_STATE_VALUE_WAIT);
	
}
else if( actionToDo.equals(Constants.ACTION_VALIDATE) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - try to change state of document");
	doActionForDocument( usrContext, log, Constants.FIELD_STATE_VALUE_VALIDATE );
}
else if( actionToDo.equals(Constants.ACTION_REFUSED) )
{
	log.debug("checks control Recto Verso : actionToDo => " + actionToDo + " - try to change state of document");
	doActionForDocument( usrContext, log, Constants.FIELD_STATE_VALUE_REFUSED );
}

customActionModel.getModalPanelModel().put(Constants.ACTION_CONTROLLER, "");

log.debug("Script triggered on checks control Recto Verso : chekControlPanelAction.groovy --- End");


private doActionForDocument( UserCoreContext usrContext, org.slf4j.Logger log, Object fieldValue )
{
	log.debug("checks control Recto Verso : changing state of document");
	IDocument currentDocument = OCSDScriptUtils.getMultipleVisaController().getModel().getCurrentDocument();
	updateDocument(usrContext, log,currentDocument,Constants.FIELD_STATE_CODE,fieldValue);
	addDocumentEvent(usrContext, log,currentDocument,Constants.FIELD_STATE_CODE,fieldValue.toString());
	OCSDScriptUtilsRectoVerso.setNextDocument( usrContext, log);	
}

private updateDocument(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate, String fieldCode, Object fieldValue)
{
	log.debug("checks control Recto Verso : setting field value (" + fieldValue + ") for the field " + documentToUpdate +" of the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;
	IField statutField = documentToUpdate.getField( fieldCode );
	statutField.setValue( fieldValue );
	log.debug("checks control Recto Verso : update document");
	OCSDScriptUtilsRectoVerso.getDocumentMgr().updateDocument(usrContext, documentToUpdate);

}

private addDocumentEvent(UserCoreContext usrContext, org.slf4j.Logger log, IDocument documentToUpdate, String fieldCode, String fieldValue)
{
	String commentEvent = "";
	log.debug("checks control Recto Verso : add event ( "+ commentEvent  +" for the document ( " + documentToUpdate.getAirsRefId() + " ) " ) ;

	// add the event in AIRS
	OCSDScriptUtilsRectoVerso.getAuditService().addDocumentEvent(usrContext, documentToUpdate, Constants.EVENT_CHEK_CTRL, commentEvent);

}