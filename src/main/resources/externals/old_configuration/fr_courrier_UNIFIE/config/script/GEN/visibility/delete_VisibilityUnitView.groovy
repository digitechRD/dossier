/**
* Auteur : JMU
* Date : 24/07/14
* Flux : Générique
* Description : Script gérant l'affichage du bouton d'effacement.
* Version : 1.0
* Paramêtres d'entrée :
* 	- scriptLogger
* 	- document
* 	- userContext
* 	- output
**/

import GenScriptUtils;

String SCRIPT_NAME="delete_VisibilityUnitView";

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Effacement] : "+SCRIPT_NAME+" --- Start");
 
output.setValue(GenScriptUtils.isDeleteButtonVisible(userContext, document));

scriptLogger.debug("Script groovy de type visibilité pour le bouton [Effacement] : "+SCRIPT_NAME+" --- End");