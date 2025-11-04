import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;

import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;

import static CourrierScriptUtils;

/*************************** PARAMS *****************************
Logger scriptLogger : le logger
UserCoreContext userContext : le user context
List<IAttachment> attachmentList : la liste des pi�ces jointes AttachmentDisplaySorting
List<IAttachment> output :  le resultat de sortie
*****************************************************************/
UserCoreContext usrContext = userContext;
Logger log = scriptLogger;
List<IAttachment> theOutput= output;
log.debug("Script document attachment sorting: AttachmentTableSorting.groovy --- Start");

List<IAttachment> attachmentsList = attachmentList;

if( attachmentsList != null && attachmentsList.size() > 0 ) {
  Collections.sort(attachmentsList, new BeanComparator("id"));
}
theOutput.addAll(attachmentsList);

log.debug("Script document attachment sorting: AttachmentTableSorting.groovy --- End");