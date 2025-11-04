import java.util.*

import javax.faces.component.UIComponent

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.search.SearchResultTableRowModel;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.model.backend.MessagesModel;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
/************************************************/

UserContext userContext = userContext
Logger log = scriptLogger;
ScriptResultModel theOutput = output;

log.debug("Script remove from copy action: removeFromCopy_SimpleView.groovy --- Start");

Collection<SearchResultTableRowModel> searchResultRows = Utils.getSearchResultTableController().getModel().getSelectedRows();
int numberDocumentsChanged = 0;
ScriptResultValueDocumentInitializer scriptResult = new ScriptResultValueDocumentInitializer();
theOutput.setValue(scriptResult);
for(SearchResultTableRowModel row : searchResultRows){
  IDocument document = row.getDocument();
  if(document!=null){
    scriptResult.setMessageSummary(null);
    scriptResult.setMessageSeverity(null);
    CourrierScriptUtils.markAsRead(userContext, document, theOutput);
    if(scriptResult.getMessageSeverity()!=null && scriptResult.getMessageSummary()==null){
      numberDocumentsChanged++;
    }
  }
}

scriptResult = new ScriptResultValueDocumentInitializer();

MessagesModel.getInstance().clearPersistantMessages();

Object[] params = new Object[1];
params[0] = Integer.valueOf(numberDocumentsChanged);
scriptResult.setMessageSummary(BundleUtils.getTranslation("msg_info_document_marked_as_read_simple_view_summary"));
scriptResult.setMessageDetail(BundleUtils.getTranslation("msg_info_document_marked_as_read_simple_view_detail", params));
scriptResult.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);

theOutput.setValue(scriptResult);

com.digitech.dossier.common.Utils.getSearchResultTableController().refresh();

log.debug("Script remove from copy action: removeFromCopy_SimpleView.groovy --- End");