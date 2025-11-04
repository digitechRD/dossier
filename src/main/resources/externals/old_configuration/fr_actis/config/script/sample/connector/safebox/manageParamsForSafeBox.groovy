import com.digitech.dossier.script.model.impl.result.ScriptResultValueConnectorRule
import fr.digitech.connector.safebox.cecurity.constant.CecuritySafeBoxConstant

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

// init the result value
ScriptResultValueConnectorRule resultValue = new ScriptResultValueConnectorRule()

// setting the directory
//resultValue.setDirectory("/" + document.getAirsRefId() + "/"); // this is the default value

// activate the xml file from Airs Server Management
//resultValue.setbManageXmlFile( true );

// adding new Annexe from the current Document
//resultValue.getAnnexeDocument().add (document.getAttachment(userContext, "test.txt") );

// adding new properties
//resultValue.getPropertiesMap().putAt("CFEC_ID",  document.getAirsRefId());

// adding new meta data
// resultValue.getMetadatasMap().putAt("CFEC_ID_MD",  document.getAirsRefId());

// compute meta datas
for(int i = 1; i < 8; i++) {
  resultValue.getMetadatasMap().put(CecuritySafeBoxConstant.PARAM_DOC_UPLDXML_META_X + i, CecuritySafeBoxConstant.PARAM_DOC_UPLDXML_META_X + i)
}

// compute properties

resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_TYPE,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_TYPE)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_CONTRIBUTOR,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_CONTRIBUTOR)
// properties.put( CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_CREATED ,
// CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_CREATED);
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_CREATOR,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_CREATOR)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_DESC,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_DESC)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_EMPREINTE,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_EMPREINTE)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_FORMAT,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_FORMAT)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_ID_CONTENEUR,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_ID_CONTENEUR)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_LANGUAGE,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_LANGUAGE)
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_SOURCE,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_SOURCE)
// properties.put( CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_TEMPORAL ,
// "01/01/2100");
resultValue.getPropertiesMap().put(CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_TITLE,
    CecuritySafeBoxConstant.PROPS_DOC_UPLDXML_TITLE)

// put the result in the output model
output.setValue(resultValue)





