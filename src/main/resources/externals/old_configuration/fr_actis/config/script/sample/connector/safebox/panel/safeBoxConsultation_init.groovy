import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.connector.model.IDossierInterface
import com.digitech.dossier.connector.service.ConnectorFactory
import com.digitech.dossier.connector.service.ConnectorUtils
import com.digitech.dossier.connector.service.IDossierConnector
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger

// define interface name & owner id
String SAFEBOX_INTERFACE_NAME = "INTERFACE_CECURITY"
String SAFEBOX_INTERFACE_TYPE = "COFFREFORT"

String XML_TAG_CFEC = "archive:cfec"
String XML_TAG_DATETIME = "archive:datetime"
String XML_TAG_SNUMBER = "archive:serialNumber"
String XML_TAG_RECEIPT_NAME = "receipt:name"
String XML_TAG_RECEIPT_METHOD = "ds:DigestMethod"
String XML_TAG_RECEIPT_DIGESTVALUE = "ds:DigestValue"
String PATH_TO_XML = "xmlPath"
String PATH_TO_ZIP = "xmlZip"


Integer interfaceOwnerId = 1

Logger log = scriptLogger

log.debug("Script triggered on init custom Modal Panel: safeBoxConsultation_init.groovy --- Start")

CustomActionModel customActionModel = Utils.getCustomActionController().getModel()

// Setting panel size 

customActionModel.setModalPanelWidth(540)
customActionModel.setModalPanelHeight(216)
//customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelResponse_title"));
customActionModel.setModalPanelTitle("Informations")

// retrieving information from CFEC ( possible from AIRS to, using CFEC type file ... )
try {

  // getting interface defined in AirsAdmin with name SAFEBOX_INTERFACE_NAME & owner Id interfaceOwnerId
  IDossierInterface dossierInterface = ConnectorFactory.getInstance().getInterfaces(SAFEBOX_INTERFACE_TYPE, interfaceOwnerId)

  // from interface, we can now get the dossierConnector
  IDossierConnector dossierConnector = ConnectorFactory.getInstance().getConnector(dossierInterface)

  // we get the first attachment in safeBox
  File archive = dossierConnector.get(userContext, document.getAttachments(userContext).get(0), "7403", "cr")

  // we get the zip to 
  File archiveZip = dossierConnector.get(userContext, document.getAttachments(userContext).get(0), "7403", "zip")

  // read file into String
  String fileContents = FileUtils.readFileToString(archive)

  // get xml tag key from the String
  List<String> xmlKeyValueList = ConnectorUtils.getTagKeyFromXml(fileContents)

  // first wirting file, case we maybe want to donwload it !
  String pathToArchive = userContext.getUserDownloadPath() + File.separator + archive.getName()
  File finalArchive = new File(pathToArchive)
  String pathToArchiveZip = userContext.getUserDownloadPath() + File.separator + archiveZip.getName()
  File fianalArchiveZip = new File(pathToArchiveZip)
  log.debug("4: safeBoxConsultation_init.groovy --- End")
  // copy file to the user donwnload directory
  FileUtils.copyFile(archive, finalArchive)
  FileUtils.copyFile(archiveZip, fianalArchiveZip)

  // put it in the map
  customActionModel.getModalPanelModel().put(PATH_TO_XML, pathToArchive)
  customActionModel.getModalPanelModel().put(PATH_TO_ZIP, pathToArchiveZip)
  log.debug("6: safeBoxConsultation_init.groovy --- End")

  // get the tag value
  List<String> xmlValueList = ConnectorUtils.getDataFromXml(fileContents, XML_TAG_CFEC)
  customActionModel.getModalPanelModel().put(StringUtils.remove(XML_TAG_CFEC, ":"), xmlValueList.toString())
  log.debug(XML_TAG_CFEC + " : " + xmlValueList.toString())
  xmlValueList = ConnectorUtils.getDataFromXml(fileContents, XML_TAG_DATETIME)
  customActionModel.getModalPanelModel().put(StringUtils.remove(XML_TAG_DATETIME, ":"), xmlValueList.toString())
  log.debug(XML_TAG_DATETIME + " : " + xmlValueList.toString())
  xmlValueList = ConnectorUtils.getDataFromXml(fileContents, XML_TAG_SNUMBER)
  customActionModel.getModalPanelModel().put(StringUtils.remove(XML_TAG_SNUMBER, ":"), xmlValueList.toString())
  log.debug(XML_TAG_SNUMBER + " : " + xmlValueList.toString())
  xmlValueList = ConnectorUtils.getDataFromXml(fileContents, XML_TAG_RECEIPT_NAME)
  customActionModel.getModalPanelModel().put(StringUtils.remove(XML_TAG_RECEIPT_NAME, ":"), xmlValueList.toString())
  log.debug(XML_TAG_RECEIPT_NAME + " : " + xmlValueList.toString())
  xmlValueList = ConnectorUtils.getDataFromXml(fileContents, XML_TAG_RECEIPT_METHOD)
  customActionModel.getModalPanelModel().put(StringUtils.remove(XML_TAG_RECEIPT_METHOD, ":"), xmlValueList.toString())
  log.debug(XML_TAG_RECEIPT_METHOD + " : " + xmlValueList.toString())
  xmlValueList = ConnectorUtils.getDataFromXml(fileContents, XML_TAG_RECEIPT_DIGESTVALUE)
  customActionModel.getModalPanelModel().put(StringUtils.remove(XML_TAG_RECEIPT_DIGESTVALUE, ":"), xmlValueList.toString())
  log.debug(XML_TAG_RECEIPT_DIGESTVALUE + " : " + xmlValueList.toString())

  log.debug("7: safeBoxConsultation_init.groovy --- End")

  // store it if needed !
  Utils.getGenericDownloader()


}
catch(Exception ex) {
  log.error("error", ex)
}


log.debug("Script triggered on init custom Modal Panel: safeBoxConsultation_init.groovy --- End")