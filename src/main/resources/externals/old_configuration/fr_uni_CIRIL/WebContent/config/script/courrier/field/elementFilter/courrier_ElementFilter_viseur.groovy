/** Fichier : courrier_ElementFilter_viseur.groovy
* 	Auteur  : JMU
* 	Date 	: 12/04/13
* 	But     : Filtre une liste d'utilisateurs en fonction de leur profils
*/
import org.apache.commons.lang.*;

import com.digitech.dossier.common.utils.*;
import com.digitech.jcorbairs.*;

import static CourrierScriptUtils;


/********************* PARAM ********************/
// Logger scriptLogger
// UserContext userContext
// List<User> elements
/************************************************/

String SCRIPT_NAME="courrier_ElementFilter_viseur";

scriptLogger.debug("Script ElementFilter : "+SCRIPT_NAME+" --- Start");

elements.clear();
elements.addAll(CourrierScriptUtils.getUsersFromProfile(CourrierScriptUtils.getConstant("PROFILE_CODE_DOS_VISEUR")));

scriptLogger.debug("Script ElementFilter : "+SCRIPT_NAME+" --- End (returned " + elements.size() + " elements)");