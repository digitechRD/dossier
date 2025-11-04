import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script validant la publication d'un document  
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="validatePublication_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [publication du document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doValidatePublication(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("La demande de publication du document a été validée.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Une erreur s'est produite au cours de la validation du document.");
}

scriptLogger.debug("Script groovy de type workflow [publication du document] : "+SCRIPT_NAME+" --- End");