import java.util.Map;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;

ScriptResultValueDocumentInitializer result = output.getValue();

result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
// You can define a message bundle key to have translations
if (binding.variables.containsKey("document")) {
  result.setMessageSummary("Traitement du document '" + document.getAirsRefId() + "'");
}

CustomActionController customActionController = Utils.getCustomActionController();
Map<String, Object> data = customActionController.getModel().getModalPanelModel();
if (! data.isEmpty()) {
  result.setMessageDetail("Le " + data.get("date") + " par " + data.get("user"));
}