import java.util.Collections
import java.util.List

import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.AttachmentComparator
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment

import static CourrierScriptUtils

/*************************** PARAMS *****************************
Logger scriptLogger : le logger
UserCoreContext userContext : le user context 
List<IAttachment> attachmentList : la liste des piéces jointes AttachmentDisplaySorting
List<IAttachment> output :  le resultat de sortie
*****************************************************************/
UserCoreContext usrContext = userContext;
Logger log = scriptLogger;
List<IAttachment> theOutput= output;
log.debug("Script document attachment sorting: attachmentDisplaySorting.groovy --- Start");
List<IAttachment> attachmentsList = attachmentList;

Collections.sort(attachmentsList, new AttachmentComparator(CourrierScriptUtils.getConstant("ATTACHMENT_TYPE_COURRIER_IN"), CourrierScriptUtils.getConstant("ATTACHMENT_TYPE_COURRIER_OUT")));

theOutput.addAll(attachmentsList);
log.debug("Script document attachment sorting: attachmentDisplaySorting.groovy --- End");