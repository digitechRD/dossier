import java.util.Map;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.Utils;
import org.slf4j.Logger;
import com.digitech.dossier.common.controller.CustomActionController;

import com.digitech.dossier.common.service.ServiceManager;

import com.digitech.jcorbairs.exception.DocumentException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.Constants;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils

import static CourrierScriptUtils

ScriptResultValueDocumentInitializer result = output.getValue();
String USR_FIELD_CODE = "FACT_ACTEUR";
String STATUT_FIELD_CODE = "FACT_ETAT";
Integer STATUT_FIELD_VALUE = 1609;

String MSG_SUCCESS_SUM = "msg_success_dmdvalid_sumary";
String MSG_SUCCESS_DETAIL = "msg_success_dmdvalid_detail";

String MSG_INFO_NO_USERREF_SUM = "msg_no_usr_dmdvalid_sumary";
String MSG_NO_USERREF_DETAIL = "msg_no_usr_dmdvalid_detail";

String MSG_ERROR_SERVER_SUM = "msg_error_serv_dmdvalid_sumary";
String MSG_ERROR_SERVER_DETAIL = "msg_error_serv_dmdvalid_detail";

String MSG_ERROR_DOC_SUM = "msg_error_doc_dmdvalid_sumary";
String MSG_ERROR_DOC_DETAIL = "msg_error_doc_dmdvalid_detail";

String MSG_ERROR_IDENT_SUM = "msg_error_ident_dmdvalid_sumary";
String MSG_ERROR_IDENT_DETAIL = "msg_error_ident_dmdvalid_detail";

result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
// You can define a message bundle key to have translations
result.setMessageSummary(MSG_SUCCESS_SUM);
result.setMessageDetail(MSG_SUCCESS_DETAIL);

if( document.getField( USR_FIELD_CODE ).getValue() != null  )
{

  try {
    // setting the right value to the STATUT field
    IField statutField = document.getField( STATUT_FIELD_CODE );
    statutField.setValue( STATUT_FIELD_VALUE );

    // add the event in AIRS
    //String user = BundleUtils.getTitle(getUserMgr().getUser(document.getField( USR_FIELD_CODE ).getValue()));
    Integer userListId = FieldUtils.getValue(document, USR_FIELD_CODE);
      String user = ""; 
      if ( userListId > 0 )
        user = BundleUtils.getTitle(getUserMgr().getUser(userListId));
    String commentEvent = "demande de validation envoy\u00e9e \u00e0 " +  user;
    getAuditMgr().addDocumentEvent(userContext, document, Constants.EVENT_WORKFLOW, commentEvent);
  }
  catch(DocumentException e) {
    result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR );
    // You can define a message bundle key to have translations
    result.setMessageSummary(MSG_ERROR_DOC_SUM);
    result.setMessageDetail(MSG_ERROR_DOC_DETAIL);
    scriptLogger.error(e.getLocalizedMessage(), e);
  }
  catch(ServerException e) {
    result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR );
    // You can define a message bundle key to have translations
    result.setMessageSummary(MSG_ERROR_SERVER_SUM);
    result.setMessageDetail(MSG_ERROR_SERVER_DETAIL );
    scriptLogger.error(e.getLocalizedMessage(), e);
  }
  catch(IdentificationException e) {
    result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR );
    // You can define a message bundle key to have translations
    result.setMessageSummary(MSG_ERROR_IDENT_SUM);
    result.setMessageDetail(MSG_ERROR_IDENT_DETAIL);
    scriptLogger.error(e.getLocalizedMessage(), e);
  }
}
else
{
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
  // You can define a message bundle key to have translations
  result.setMessageSummary(MSG_INFO_NO_USERREF_SUM);
  result.setMessageDetail(MSG_NO_USERREF_DETAIL);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(
  com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

private com.digitech.dossier.common.service.IAuditService getAuditMgr() {
  return (com.digitech.dossier.common.service.IAuditService) ServiceManager.getInstance().getService(
  com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
}

private IUser getUserMgr() {
  return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}

