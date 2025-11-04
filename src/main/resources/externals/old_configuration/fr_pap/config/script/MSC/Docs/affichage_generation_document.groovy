import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule
/************************************************************************************************************************************
 * Auteur 	  	: 
 * Date 		: 
 * Description  : 
 *------------------------------------------------------------------------------------------------------------------------------------
 *  Paramètres d'entrée :
 * 	- scriptLogger
 * 	- document
 * 	- userContext
 * 	- output
 ************************************************************************************************************************************/
final String SCRIPT_NAME="Docs_generation_document_displayRule"
//final String PROFILE_CODE="ACTES_ENVOI_PJ"



_scriptLogger.debug("Script groovy de type display rule : "+SCRIPT_NAME+" --- Start")
try
{
    boolean visible = true
    //ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule()

    // Vérification que l'utilisateur connecté possède le bon profil...
    //visible = UserUtils.hasProfile(userContext, PROFILE_CODE)

    // ... Si oui, on affiche le bouton action
    _result.setValid(visible)
    //_output.setValue(result)
}
catch(Exception e)
{
    _scriptLogger.error(e.getLocalizedMessage(), e)
}

_scriptLogger.debug("Script groovy de type display rule : "+SCRIPT_NAME+" --- End")