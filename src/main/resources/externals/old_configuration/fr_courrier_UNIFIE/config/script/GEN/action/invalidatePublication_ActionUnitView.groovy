import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script lançant le refus de publication d'un document  
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="invalidatePublication_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [refus de publication du document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doInvalidatePublication(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("La publication du document a été refusée.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Une erreur s'est produite lors du refus de publication du document.");
}

scriptLogger.debug("Script groovy de type workflow [refus de publication du document] : "+SCRIPT_NAME+" --- End");


