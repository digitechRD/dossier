import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 24/07/14
* Flux : Générique
* Description : Script lançant l'effacement d'un document (changement d'état workflow)
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="delete_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [effacement du document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doDelete(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Le document a été effacé.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Une erreur s'est produite lors de l'effacement du document.");
}

scriptLogger.debug("Script groovy de type workflow [effacement du document] : "+SCRIPT_NAME+" --- End");