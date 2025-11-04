import java.io.IOException;
import java.util.Map;

import javax.faces.application.FacesMessage.Severity;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.connector.model.IDossierInterface;
import com.digitech.dossier.connector.service.ConnectorFactory;
import com.digitech.dossier.connector.service.IDossierConnector;
import com.digitech.dossier.script.exception.ScriptException;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.jcorbairs.exception.DocumentException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;

import fr.digitech.connector.exception.ConnectorException;

/**
 addFileToSB_doc.groovy is a sample to show you how to add attachment into your own signature book
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
String SUMMARY_MSG_OK = "sb_srci_add_doc_sum_ok";
String DETAIL_MSG_OK = "sb_srci_add_doc_detail_ok";
String SUMMARY_MSG_KO = "sb_srci_add_doc_sum_ko";
String DETAIL_MSG_KO = "sb_srci_add_doc_detail_ko";

// define interface name & owner id 
String SBSRCI_INTERFACE_NAME = "INTERFACE_SB_SRCI"; 
String SBSRCI_INTERFACE_TYPE = "PARAPHEUR";

Integer interfaceOwnerId = 1;

// init the result value
ScriptResultValueDocumentInitializer scriptResultValue = new ScriptResultValueDocumentInitializer();
 
// defined default message
scriptResultValue.setMessageDetail(DETAIL_MSG_OK);
scriptResultValue.setMessageSummary(SUMMARY_MSG_OK);
scriptResultValue.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO)
try
{
  // getting interface defined in AirsAdmin with type SBSRCI_INTERFACE_TYPE & owner Id interfaceOwnerId
 IDossierInterface dossierInterface = ConnectorFactory.getInstance().getInterfaces(SBSRCI_INTERFACE_TYPE, interfaceOwnerId);
  // from interface, we can now get the dossierConnector
  IDossierConnector dossierConnector = ConnectorFactory.getInstance().getConnector(dossierInterface);
  // we put the first attachment in SB
  dossierConnector.put(userContext, document.getAttachments(userContext).get(0));
}
catch(Exception ex)
{
  // defined error message
  scriptResultValue.setMessageDetail(DETAIL_MSG_KO);
  scriptResultValue.setMessageSummary(SUMMARY_MSG_KO);
  scriptResultValue.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR)
  
  // log in log file
  scriptLogger.error("Error during inserting document in Signature book", ex );
}

// return result 
output.setValue( scriptResultValue );
