import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.script.utils.ScriptUtilities

ScriptResultValueChecker result = output.getValue()
IAttachment theAttachment = binding.variables.get(ScriptUtilities.AIRS_ATTACHMENT_PARAM_NAME)

result.setValid(true)
result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO)
// You can define a message bundle key to have translations
result.setMessageSummary("Traitement de la pi\u00e8ce jointe '" + theAttachment.getFileName() + "'")

CustomActionController customActionController = Utils.getCustomActionController()
Map<String, Object> data = customActionController.getModel().getModalPanelModel()
if(!data.isEmpty()) {
  result.setMessageDetail("Pi\u00e8ce jointe trait\u00e9e le " + data.get("date") + " par " + data.get("user"))
}