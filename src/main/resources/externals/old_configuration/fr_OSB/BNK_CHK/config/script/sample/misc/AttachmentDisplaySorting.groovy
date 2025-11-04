import java.util.List;

import com.digitech.dossier.common.model.backend.airs.IAttachment;

import java.util.List;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;

// PARAMS
// scriptLogger : log for script
// userContext  : the userContext

 List<IAttachment> attachments = userContext.getCurrentDocument().getAttachments()
if(attachments != null && !attachments.isEmpty()) {
  output = attachments.get(0);
}








