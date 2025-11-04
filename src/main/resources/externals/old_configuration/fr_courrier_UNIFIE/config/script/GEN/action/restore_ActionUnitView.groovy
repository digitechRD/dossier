import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 28/07/14
* Flux : Générique
* Description : Script lançant la restauration d'un document effacé (changement d'état workflow)
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="restore_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [restauration du document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doRestore(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Le document a été restauré.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Changement d'état du document.");
	result.setMessageDetail("Une erreur s'est produite lors de la restauration du document.");
}

scriptLogger.debug("Script groovy de type workflow [restauration du document] : "+SCRIPT_NAME+" --- End");