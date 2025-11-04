import java.util.List;
import java.util.Map;
import java.lang.Double;

// import standart pour un script de type AIRSFLOW
import com.digitech.dossier.script.model.IScriptResultValueModel;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.script.model.IScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import com.digitech.airs3dossiers.airs.AirsFolder;
import com.digitech.dossier.common.model.backend.airs.IField;
import org.apache.commons.lang.StringUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;



TEST_OK_SUM                 = "script_isPossibleSuppresion_summary_OK";
TEST_KO_SUM                 = "script_isPossibleSuppresion_summary_KO";

TEST_OK_DETAIL              = "script_isPossibleSuppresion_detail_OK";
TEST_KO_DETAIL              = "script_isPossibleSuppresion_detail_KO";


//champs AIRS concern�s
DATE_TO_TCHECK          = "D_CREAT";

//IScriptResultModel<> ouput = new ScriptResultModel<List<IDocument>>()
ScriptResultValueChecker result = new ScriptResultValueChecker();


if(airsDocument != null)
{
    // testing document date value
    IField fieldDate = airsDocument.getField(DATE_TO_TCHECK);
    String fieldDatevalue = getFieldValue( fieldDate );

    if( StringUtils.isNotBlank( fieldDatevalue ) && StringUtils.isNotBlank( fieldDatevalue ) )
    {
      scriptLogger.warn("[isPossiblleSuppression] : date : " + fieldDatevalue);

    /* format de la date : 09/04/2010 10:58:36 */
    Date dateToTcheck =  new SimpleDateFormat("MM/dd/yy HH:ss:SSS" ).parse( fieldDatevalue) ;

  	Calendar myCalendarToTcheck = GregorianCalendar.getInstance();
		myCalendarToTcheck.setTime(dateToTcheck);
		myCalendarToTcheck.add( Calendar.YEAR, 2 );

		Calendar myToday = GregorianCalendar.getInstance();

		if( myToday.after( myCalendarToTcheck ) )
		{
			result.setValid( true );
			result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
			result.setMessageSummary(TEST_OK_SUM);
			result.setMessageDetail(TEST_OK_DETAIL);
			scriptLogger.warn("[isPossiblleSuppression] : suppression accept�e ");
		}
		else
		{
			result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
			result.setValid( false );
			result.setMessageSummary(TEST_KO_SUM);
			result.setMessageDetail(TEST_KO_DETAIL);
			scriptLogger.warn("[isPossiblleSuppression] : suppression non accept�e ");
		}

    }
    else
    {
      result.setValid( false );
      result.setMessageSummary(TEST_KO_SUM);
      result.setMessageDetail(TEST_KO_CAS_1_DETAIL);
      scriptLogger.warn("fields values are null");
    }


  }

else
{

  result.setValid( false );
  result.setMessageSummary(TEST_KO_SUM);
  result.setMessageDetail(TEST_KO_CAS_1_DETAIL);
  scriptLogger.warn("airsDocument is null");
}

output.setValue( result );

//**************************************************************************************************************************************************************//

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

int countLinkedDoc(AirsFolder airsFolder)
{ // compte les documents fils li� au formulaire
  List<Integer> childIds;
  int nLinkedDOc = 0;
  try {
    childIds = airsFolder.getChildListId();
    boolean docAllReadyLinked = false;
    boolean mustBeSave = true;
    if(childIds != null) {

      for(Integer childId : childIds) {

        IDocument docToTrait = getDocumentMgr().getDocument(userContext.getJeton(), childId);

        IField field = docToTrait.getField(C_ETAT_DOC );


        String fieldvalue = getFieldValue( field );

        if(fieldvalue != null && fieldvalue.equalsIgnoreCase( DOC_VALUE_LINKED ) )
        {// doc linked
          nLinkedDOc++;
        }
      }
    }
    else
      scriptLogger.warn("childIds is null");
  }
  catch(Exception e) {
    scriptLogger.error(e.getLocalizedMessage(), e);
  }

  scriptLogger.warn( nLinkedDOc + " child linked");
  return nLinkedDOc;

}

private IServer getServerMgr() {
  return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
    return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
  }

