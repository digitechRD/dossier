import com.digitech.dossier.script.model.impl.result.ScriptResultValueConnectorRule
import fr.digitech.connector.impl.signatureBook.type.EnumDepth

/**
 manageParamsForSafeBox.groovy is a sample to show you how to manage custom properties according to your own safebox
 **/

/**
 * input params
 * scriptLogger : logger
 * userContext  : context of the user running this script
 * document     : the current IDocument
 * attachment   : the attachment you select to put on SafeBox
 *
 */

/**
 * output params
 *  annexeDocument : list of IAttachment, representing annexes linked to the prime document
 *  bManageXmlFile : do you want to put the airs xml file in the safe box ?
 *  propertiesMap  : Map of String, String - contains properties linked to your safebox type
 *  metadatasMap   : Map of String, String - contains meta datas linked to your safebox type
 *  directory      : the directory into you want to  put you document & annexes
 */


String MODEL_NAME = "circuit_1"
String NATURE_NAME = "dossierTests"
// init the result value
ScriptResultValueConnectorRule resultValue = new ScriptResultValueConnectorRule()



resultValue.getMetadatasMap().put(EnumDepth.DEPTH_0.name(), MODEL_NAME) // here set the model
resultValue.getMetadatasMap().put(EnumDepth.DEPTH_1.name(), NATURE_NAME) // here set the nature

// put the result in the output model
output.setValue(resultValue)





