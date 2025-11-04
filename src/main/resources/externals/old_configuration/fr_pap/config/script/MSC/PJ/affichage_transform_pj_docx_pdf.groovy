import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule

/************************************************************************************************************************************
 * Auteur 	  	: LBE
 * Date 		  	: 28/08/2025
 * Description   : Script gerant l'affichage du bouton de transformation des pj docx en pdf
 *------------------------------------------------------------------------------------------------------------------------------------
 * Paramètres d'entree :
 *   - scriptLogger
 *   - document
 *   - userContext
 * 	- output
 ************************************************************************************************************************************/

// Constantes
final String SCRIPT_NAME = "affichage_transform_pj_docx_pdf";

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