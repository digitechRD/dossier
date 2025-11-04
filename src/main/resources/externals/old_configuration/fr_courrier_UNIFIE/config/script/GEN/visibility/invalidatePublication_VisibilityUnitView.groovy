/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script gérant l'affichage du bouton de refus de validation.
* Version : 1.0
* Paramêtres d'entrée :
* 	- scriptLogger
* 	- document
* 	- userContext
* 	- output
**/

import GenScriptUtils;

String SCRIPT_NAME="invalidatePublication_VisibilityUnitView";

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Refus de publication] : "+SCRIPT_NAME+" --- Start");
 
output.setValue(GenScriptUtils.isInvalidatePublicationButtonVisible(userContext, document));

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Refus de publication] : "+SCRIPT_NAME+" --- End");