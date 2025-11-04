import com.digitech.dossier.common.model.backend.airs.IAttachment

// PARAMS
// scriptLogger : log for script
// userContext  : the userContext

List<IAttachment> attachments = userContext.getCurrentDocument().getAttachments()
if(attachments != null && !attachments.isEmpty()) {
  output = attachments.get(0)
}








