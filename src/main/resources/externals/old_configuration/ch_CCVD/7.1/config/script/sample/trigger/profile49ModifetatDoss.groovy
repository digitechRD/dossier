import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.ServiceManager

ETAT_DOSS_FIELD_CODE = "ETA_DOS"
ETAT_DOSS_INITIAL_VALUE = ""
ETAT_DOSS_FINAL_VALUE = ""

if(airsDocument != null) {
  IField fieldEtat_Doss = airsDocument.getField(ETAT_DOSS_FIELD_CODE)
  String fieldStatutvalue = getFieldValue(fieldEtat_Doss)

  //if( fieldStatutvalue.compareToIgnoreCase(ETAT_DOSS_INITIAL_VALUE) == 0 )
  //{
  fieldEtat_Doss.setValue(ETAT_DOSS_FINAL_VALUE)
  //}
}

String getFieldValue(IField field) {
  List<?> values = field.getValues()
  String fieldvalue
  if(values != null && !values.isEmpty()) {
    fieldvalue = getServerMgr().getFieldValues(values, field.getAirsField())
    _scriptLogger.warn("Field Value : " + field.getCode() + " - " + fieldvalue)
  }
  else {
    _scriptLogger.warn("the field value is null or empty")
  }

  return fieldvalue
}

private IServer getServerMgr() {
  return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR)
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
}
