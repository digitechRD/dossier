/** Fichier : collaborator_elementFilter.groovy
* 	Auteur  : JMU
* 	Date 	: 29/07/14
* 	Description     : Filtre une liste d'utilisateurs en fonction de leur profils(PF_GEN_MODIF)
*   Paramêtres d'entrée : 
* 	- scriptLogger
*	- List<User> elements
*	- userContext
*/
import org.apache.commons.lang.*;
import com.digitech.dossier.common.utils.*;
import com.digitech.jcorbairs.*;
import GenScriptUtils;

String SCRIPT_NAME="collaborator_elementFilter";

scriptLogger.debug("Script groovy de type filtre : "+SCRIPT_NAME+" --- Start");

elements.clear();
elements.addAll(GenScriptUtils.getUsersFromProfile(GenScriptUtils.getConstant("PROFILE_CODE_COLLABORATOR")));

scriptLogger.debug("Script groovy de type filtre : "+SCRIPT_NAME+" --- End (returned " + elements.size() + " elements)");