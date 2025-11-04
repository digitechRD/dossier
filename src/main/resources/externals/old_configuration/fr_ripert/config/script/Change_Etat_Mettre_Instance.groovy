import java.util.List;
import java.util.Map;
import java.lang.Double;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.Utils;

EN_INSTANCE_DOC_FIELD_CODE = "INS_EN_INSTANCE"
DATE_RAPPEL_DOC_FIELD_CODE = "INS_DATE_RAPPEL";
BESOIN_RAPPEL_DOC_FIELD_CODE = "INS_BESOIN_RAPPEL";
EN_INSTANCE_DOC_INITIAL_VALUE = "303";
EN_INSTANCE_DOC_FINAL_VALUE = "302";

BESOIN_RAPPEL_DOC_OUI_15 = "307";

ETAT_INSTANCE_DOC_OUI = "302";

if(airsDocument != null)
{
	IField fieldEtatInstance_Doc = airsDocument.getField(EN_INSTANCE_DOC_FIELD_CODE);  
	String fieldEtatInstancevalue = getFieldValue( fieldEtatInstance_Doc );
	
  if( fieldEtatInstancevalue == null || fieldEtatInstancevalue.compareToIgnoreCase(EN_INSTANCE_DOC_INITIAL_VALUE) == 0 )
	{
		fieldEtatInstance_Doc.setValue( EN_INSTANCE_DOC_FINAL_VALUE );
		airsDocument.getFieldMap().put(EN_INSTANCE_DOC_FIELD_CODE, fieldEtatInstance_Doc );
		scriptLogger.warn("Setting field value : " + fieldEtatInstance_Doc.getCode() + " - " + EN_INSTANCE_DOC_FINAL_VALUE );	
		
		IField fieldBesoinRappel_Doc = airsDocument.getField(BESOIN_RAPPEL_DOC_FIELD_CODE);
      	fieldBesoinRappel_Doc.setValue( BESOIN_RAPPEL_DOC_OUI_15 );
      	airsDocument.getFieldMap().put(BESOIN_RAPPEL_DOC_FIELD_CODE, fieldBesoinRappel_Doc );
      	
      	//Calendar myToday = GregorianCalendar.getInstance();
      //	myToday.add(myToday.DATE, 15);
      //	String strdate = null;
       // SimpleDateFormat FULL_DAY_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
       // strdate = FULL_DAY_FORMATTER.format(myToday.getTime());
        
      //	IField fieldDateRappel_Doc = airsDocument.getField(DATE_RAPPEL_DOC_FIELD_CODE);
      //	fieldDateRappel_Doc.setValue( strdate );
      //	airsDocument.getFieldMap().put(DATE_RAPPEL_DOC_FIELD_CODE, fieldDateRappel_Doc );  
  	
	}
}
else
{
		scriptLogger.warn("DOCUMENT NULLLLLLLLLLL");
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
