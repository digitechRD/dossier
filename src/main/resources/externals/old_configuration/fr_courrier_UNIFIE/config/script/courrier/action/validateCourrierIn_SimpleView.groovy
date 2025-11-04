import java.util.ArrayList;

import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backing.search.SearchResultTableRowModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.MessagesModel;
import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
ScriptResultModel theOutput = output;

log.debug("Script triggered on validating : validateCourrierIn_SimpleView.groovy --- Start");

Collection<SearchResultTableRowModel> searchResultRows = Utils.getSearchResultTableController().getModel().getSelectedRows();
SearchResultTableRowModel currentRow = Utils.getSearchResultTableController().getModel().getCurrentRow();

int documentsValidated = 0;
boolean changeCurrentRow = (currentRow == null);
for (SearchResultTableRowModel row : searchResultRows) {
    IDocument document = row.getDocument();
    if (document != null) {
        boolean validated = CourrierScriptUtils.validateCourrier(document, usrContext, log);
        if (validated) {
            // Reset Boolean, new mail will be send
            CourrierScriptUtils.markDocumentToNotifyUser(document);
            log.debug("Document [{}] has been mark to notified owner by mail.", document.getAirsRefId());

            if (!changeCurrentRow) {
                changeCurrentRow = row.getDocument().getAirsRefId().equals(currentRow.getDocument().getAirsRefId());
            }

            documentsValidated++;
        }
    }
}

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
Integer documentValidable = customActionModel.getModalPanelModel().get("documentValidable");

if (documentsValidated.intValue() < documentValidable.intValue()) {
    MessagesModel.getInstance().clearPersistantMessages();

    ScriptResultValueDocumentInitializer scriptResult = new ScriptResultValueDocumentInitializer();
    theOutput.setValue(scriptResult);

    Object[] params = new Object[1];
    params[0] = Integer.valueOf(documentValidable.intValue() - documentsValidated.intValue());
    scriptResult.setMessageSummary(BundleUtils.getTranslation("modalPanelValidate_msgDocumentsNonValidatedSummary"));
    scriptResult.setMessageDetail(BundleUtils.getTranslation("modalPanelValidate_msgDocumentsNonValidatedDetail", params));
    scriptResult.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
} else if (changeCurrentRow) {
    Utils.getSearchResultTableController().getModel().setCurrentPage(1);
}
Utils.getSearchResultTableController().refresh();

log.debug("Script triggered on validating : validateCourrierIn_SimpleView.groovy --- End");