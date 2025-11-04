import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script lançant la fin de la collaboration de l'utilisateur courant sur le document
* Version : 1.0
* Paramètres d'entrée : 
* 	- scriptLogger
*	- document
*	- userContext
**/

String SCRIPT_NAME="collabEnd_ActionUnitView.groovy";

scriptLogger.debug("Script groovy de type workflow [fin de collaboration sur le document] : "+SCRIPT_NAME+" --- Start");

ScriptResultValueDocumentInitializer result = output.getValue();

if(GenScriptUtils.doCollabEnd(userContext, document))
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Fin de collaboration");
	result.setMessageDetail("La fin de votre collaboration sur le document a bien été prise en compte.");
}
else
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Fin de collaboration");
	result.setMessageDetail("Une erreur s'est produite au cours de votre demande de fin de collaboration sur le document.");
}

scriptLogger.debug("Script groovy de type workflow [fin de collaboration sur le document] : "+SCRIPT_NAME+" --- End");