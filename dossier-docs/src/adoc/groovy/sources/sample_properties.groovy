import com.digitech.dossier.common.utils.AppConfigurationUtilsCore
import com.digitech.dossier.script.utils.ScriptUtilities

def propFile = Paths.get(AppConfigurationUtilsCore.getXMLConfigurationFolderPath() + "ws.properties")

/**
 * Le 2ème paramètre ("wsProps") permet de mettre en cache le résultat.
 * Celui-ci sera recalculé seulement si le fichier est modifié (chargement à chaud)
 */
Properties conf = ScriptUtilities.getOrLoadProperties(propFile, "wsProps"/** <cacheKey>*/)

String driver = conf.getProperty("airs.db.class")