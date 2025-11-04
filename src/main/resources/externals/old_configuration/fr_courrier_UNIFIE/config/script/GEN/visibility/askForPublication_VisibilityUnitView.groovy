/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script gérant l'affichage du bouton de demande de publication.
* Version : 1.0
* Paramêtres d'entrée :
* 	- scriptLogger
* 	- document
* 	- userContext
* 	- output
**/

import GenScriptUtils;

String SCRIPT_NAME="isAskForPublicationButtonVisible";

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Demande de publication] : "+SCRIPT_NAME+" --- Start");
 
output.setValue(GenScriptUtils.isAskForPublicationButtonVisible(userContext, document));

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Demande de publication] : "+SCRIPT_NAME+" --- End");