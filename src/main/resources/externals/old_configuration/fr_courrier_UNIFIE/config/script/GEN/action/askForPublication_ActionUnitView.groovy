import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script lançant la demande de publication d'un document  
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="askForPublication_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [demande de publication du document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doAskForPublication(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Le demande de publication a été effectuée.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Une erreur s'est produite lors de la demande de publication.");
}

scriptLogger.debug("Script groovy de type workflow [demande de publication du document] : "+SCRIPT_NAME+" --- End");