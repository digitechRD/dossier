import com.digitech.dossier.connector.model.IDossierInterface
import com.digitech.dossier.connector.service.ConnectorFactory
import com.digitech.dossier.connector.service.IDossierConnector
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker

/**
 addFileToSafeBox_aftersave.groovy is a sample to show you how to add attachment into your own safebox
 **/

/**
 * input params
 * scriptLogger : logger
 * userContext  : context of the user running this script
 * document     : the current IDocument
 *
 */

/**
 * output params
 *  MessageSummary : summary message who will be displayed
 *  MessageDetail : detail message who will be displayed
 *  MessageSeverity  :severity  message who will be displayed
 */

// define messages linked with messages properties keys
String SUMMARY_MSG_OK = "safebox_add_doc_sum_ok"
String DETAIL_MSG_OK = "safebox_add_doc_detail_ok"
String SUMMARY_MSG_KO = "safebox_add_doc_sum_ko"
String DETAIL_MSG_KO = "safebox_add_doc_detail_ko"

// define interface name & owner id 
String SAFEBOX_INTERFACE_NAME = "INTERFACE_CECURITY"
String SAFEBOX_INTERFACE_TYPE = "COFFREFORT"

Integer interfaceOwnerId = 1

// init the result value
ScriptResultValueChecker scriptResultValue = new ScriptResultValueChecker()

// defined default message
scriptResultValue.setMessageDetail(DETAIL_MSG_OK)
scriptResultValue.setMessageSummary(SUMMARY_MSG_OK)
scriptResultValue.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
try {
  // getting interface defined in AirsAdmin with name SAFEBOX_INTERFACE_NAME & owner Id interfaceOwnerId
  IDossierInterface dossierInterface = ConnectorFactory.getInstance().getInterfaces(SAFEBOX_INTERFACE_TYPE, interfaceOwnerId)
  // from interface, we can now get the dossierConnector
  IDossierConnector dossierConnector = ConnectorFactory.getInstance().getConnector(dossierInterface)
  // we put the first attachment in safeBox
  dossierConnector.put(userContext, document.getAttachments(userContext).get(0))
}
catch(Exception ex) {
  // defined error message
  scriptResultValue.setMessageDetail(DETAIL_MSG_KO)
  scriptResultValue.setMessageSummary(SUMMARY_MSG_KO)
  scriptResultValue.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)

  // log in log file
  scriptLogger.error("Error during inserting document in safeBox", ex)
}

// return result 
output.setValue(scriptResultValue)
