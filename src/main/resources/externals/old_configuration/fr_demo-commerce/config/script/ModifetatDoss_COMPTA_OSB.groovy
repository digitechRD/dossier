import java.util.List;
import java.util.Map;
import java.lang.Double;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.Utils;

ETAT_DOSS_FIELD_CODE = "ETA_DOS"
ETAT_DOSS_INITIAL_VALUE = "605";
ETAT_DOSS_FINAL_VALUE = "607";

if(airsDocument != null) 
{
	IField fieldEtat_Doss = airsDocument.getField(ETAT_DOSS_FIELD_CODE);  
	String fieldStatutvalue = getFieldValue( fieldEtat_Doss );
	
  if( fieldStatutvalue.compareToIgnoreCase(ETAT_DOSS_INITIAL_VALUE) == 0 )
	{
		fieldEtat_Doss.setValue( ETAT_DOSS_FINAL_VALUE );
		airsDocument. getFieldMap().put(ETAT_DOSS_FIELD_CODE, fieldEtat_Doss );
		scriptLogger.info("Setting field value : " + fieldEtat_Doss.getCode() + " - " + ETAT_DOSS_FINAL_VALUE );	
	}
	else
	{
		scriptLogger.info("The field value : " + fieldEtat_Doss.getCode() + " - " + fieldStatutvalue + " couldt not be set. Waiting for " + ETAT_DOSS_INITIAL_VALUE + " recieve " + fieldStatutvalue );  
	}
}

String getFieldValue( IField field )
{
	List<?> values = field.getValues();
	String fieldvalue;
	if(values != null && !values.isEmpty()) {
		fieldvalue =getServerMgr().getFieldValues(values, field.getAirsField());   
		scriptLogger.warn("Field Value : " + field.getCode() + " - " + fieldvalue );
	}else
	{
		scriptLogger.warn("the field value is null or empty");
	}
	
	return fieldvalue;
}

private IServer getServerMgr() {
	return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}
