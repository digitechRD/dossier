import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script faisant passer le document dans l'état périmé 
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="outOfDate_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [Peremption du document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doOutOfDate(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Le document est désormais considéré comme périmé.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Une erreur s'est produite au cours de la demande de péremption du document.");
}

scriptLogger.debug("Script groovy de type workflow [Peremption du document] : "+SCRIPT_NAME+" --- End");