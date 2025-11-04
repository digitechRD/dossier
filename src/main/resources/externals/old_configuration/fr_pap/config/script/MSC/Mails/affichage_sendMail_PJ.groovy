import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;
import com.digitech.dossier.common.utils.UserUtils;
import MSC_Utils;
import com.digitech.dossier.common.model.backend.airs.IAttachment;

/************************************************************************************************************************************
* Auteur 	  	: PRO+JMU
* Date 		  	: 13/11/2018
* Description   : Script gerant l'affichage du bouton d'envoi E-mail avec lien sur le document
*------------------------------------------------------------------------------------------------------------------------------------  
* Paramètres d'entree :
*   - scriptLogger
*   - document
*   - userContext
* 	- output
************************************************************************************************************************************/

// Constantes
final String SCRIPT_NAME = "affichage_sendMail_PJ";

scriptLogger.debug("Script groovy de type affichage de bouton : "+SCRIPT_NAME+" --- Start");

boolean resultOK = true;

try {
	ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

	scriptLogger.debug(SCRIPT_NAME+" : visible="+resultOK);
	result.setValid(resultOK);
	output.setValue(result);
}
catch(Exception e) {
    scriptLogger.error(SCRIPT_NAME+" : ERREUR : "+e.getLocalizedMessage());
}

scriptLogger.debug("Script groovy de type affichage de bouton : "+SCRIPT_NAME+" --- End");