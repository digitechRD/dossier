
import java.io.*

//Other imports
import org.slf4j.Logger
import org.slf4j.LoggerFactory



/**
* Author : JMU
* Date : 12/07/12
* Description : Ce script permet d'executer un fichier bat
* IN : String path : chemin vers le fichier bat
*
*/

//Récupération du Logger
Logger log = LoggerFactory.getLogger(this.getClass());
log.error("launchBatDataloader : IN");

//Récupération du paramètre d'entrée
String path = (String)parameterMap.get("path").getValue();

Runtime runtime = Runtime.getRuntime();

//String command[] = { "cmd", "/C", "Start", path };
runtime.exec("cmd /C Start "+path);


log.error("launchBatDataloader : OUT");