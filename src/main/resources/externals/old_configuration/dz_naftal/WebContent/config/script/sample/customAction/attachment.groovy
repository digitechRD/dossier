import java.util.Map;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;

ScriptResultValueChecker result = output.getValue();

result.setValid( true );
result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
// You can define a message bundle key to have translations
result.setMessageSummary("Traitement de la pièce jointe '" + attachment.getFileName() + "'");

CustomActionController customActionController = Utils.getCustomActionController();
Map<String, Object> data = customActionController.getModel().getModalPanelModel();
if (! data.isEmpty()) {
  result.setMessageDetail("Pièce jointe traitée le " + data.get("date") + " par " + data.get("user"));
}