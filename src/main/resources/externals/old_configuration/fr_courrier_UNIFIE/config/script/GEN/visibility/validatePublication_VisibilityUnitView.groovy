/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script gérant l'affichage du bouton de publication.
* Version : 1.0
* Paramêtres d'entrée :
* 	- scriptLogger
* 	- document
* 	- userContext
* 	- output
**/

import GenScriptUtils;

String SCRIPT_NAME="validatePublication_VisibilityUnitView";

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Publication] : "+SCRIPT_NAME+" --- Start");

output.setValue(GenScriptUtils.isValidatePublicationButtonVisible(userContext, document));

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Publication] : "+SCRIPT_NAME+" --- End");