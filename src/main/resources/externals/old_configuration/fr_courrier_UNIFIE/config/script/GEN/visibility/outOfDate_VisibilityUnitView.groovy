/**
* Auteur : JMU
* Date : 15/07/14
* Flux : Générique
* Description : Script gérant l'affichage du bouton de péremption.
* Version : 1.0
* Paramêtres d'entrée :
* 	- scriptLogger
* 	- document
* 	- userContext
* 	- output
**/

import GenScriptUtils;

String SCRIPT_NAME="outOfDate_VisibilityUnitView";

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Péremption] : "+SCRIPT_NAME+" --- Start");
 
output.setValue(GenScriptUtils.isOutOfDateButtonVisible(userContext, document));

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Péremption] : "+SCRIPT_NAME+" --- End");