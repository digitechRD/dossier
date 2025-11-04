import com.digitech.dossier.script.model.impl.result.ScriptResultValueAttachmentToDisplay;
import com.digitech.dossier.common.service.IRight;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.service.ServiceManager;
import java.util.List;
import com.digitech.dossier.common.model.backend.airs.IDocument;
// PARAMS
// scriptLogger : log for script
// userContext  : the userContext
IDocument currentDoc = userContext.getCurrentDocument();
ScriptResultValueAttachmentToDisplay  attachmentResultModel = new ScriptResultValueAttachmentToDisplay();
List<IAttachment> attachments = currentDoc.getAttachments();

if(attachments != null && !attachments.isEmpty())
{
	attachmentResultModel.setAttachment( getAttachment( attachments, currentDoc ) );
  attachmentResultModel.setDocument( currentDoc );
}
else if( currentDoc.isFolder() ) {
	List<Integer> childIds = getDocumentsMgr().getChildDocuments(userContext, currentDoc.getAirsRefId(), null);
	// for the first child, we need the firts usable Document according to Naftal Rules
	if( childIds.size() > 0 )
	{
		IDocument doc = getDocumentMgr().getDocument(userContext.getJeton(), childIds.get(0));
		scriptLogger.error("getting Document : " + childIds.get(0) );
		List<IAttachment> attachmentsListChild = doc.getAttachments();
		scriptLogger.error("AttachmentList size : " + attachmentsListChild.size() );

    attachmentResultModel.setAttachment( getAttachment( attachmentsListChild, doc ) );
    attachmentResultModel.setDocument( doc );

	}
}
output.setValue(attachmentResultModel);
return output;

IAttachment getAttachment(  List<IAttachment> attachmentsList,  currentDoc ) {
	for( IAttachment attached: attachmentsList ) {
		scriptLogger.debug("current attachment  : "  + attached.getId() );
		try {
			if(!getRightMgr().isAuthorizedToEditDocument(UserContext.getInstance(), currentDoc )) {
				if(IAttachment.ATTACHMENT_TYPE_DEGRADE.equals(attached.getType())) {
					continue;
				}
			}
			else
			{
				scriptLogger.debug("returning attachment : " + attached.getId() );
				 return attached;
			}
		}
		catch(Exception e) {
			scriptLogger.error(e.getLocalizedMessage(), e);
			continue;
		}
	}
}

private com.digitech.dossier.common.service.IDocuments getDocumentsMgr() {
	return (com.digitech.dossier.common.service.IDocuments) ServiceManager.getInstance().getService(
	com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENTS_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(
	com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

private IRight getRightMgr() {
	return (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);
}