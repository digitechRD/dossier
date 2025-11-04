import java.util.ArrayList;

import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.script.model.impl.result.AbstractScriptResultValue;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.model.backend.Constants.LockType;

import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;
ScriptResultModel theOutput = output;

log.debug("Script triggered on removing from copy : removeFromCopy_ViewUnitUpdate.groovy --- Start");

ScriptResultValueDocumentInitializer scriptResult = new ScriptResultValueDocumentInitializer();
theOutput.setValue(scriptResult);

CourrierScriptUtils.markAsRead(usrContext, theDocument, theOutput);

if(scriptResult.getMessageSummary()==null){
  scriptResult.setMessageSummary(BundleUtils.getTranslation("msg_info_document_marked_as_read_summary"));
  scriptResult.setMessageDetail(BundleUtils.getTranslation("msg_info_document_marked_as_read_detail"));
  scriptResult.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
}

Utils.getViewUnitController().getModel().clearSecondaryCustomActionPanelGroup();
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.setReRender("actionBar");

theDocument = CourrierScriptUtils.getDocumentMgr().getDocument(usrContext, theDocument.getAirsRefId());
usrContext.setCurrentDocument(theDocument);
CourrierScriptUtils.getDocumentMgr().lockDocument(usrContext, theDocument, LockType.MANUAL);
Utils.getSearchResultController().replay();
log.debug("Script triggered on removing from copy : removeFromCopy_ViewUnitUpdate.groovy --- End");