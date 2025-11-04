import java.util.List;
import java.util.Map;
import java.lang.Double;

import com.digitech.jcorbairs.exception.XmlException;

import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.script.model.IScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;

import com.digitech.airs3dossiers.airs.AirsFolder;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.dossier.common.model.backend.airs.IField;
import org.apache.commons.lang.StringUtils;
import com.digitech.dossier.common.Utils;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;


AGENT_TRAIT_FIELD_CODE = "TRAIT_AGENT";
EN_INSTANCE_FIELD_CODE = "INS_EN_INSTANCE";
ETAT_DOC_FIELD_CODE = "ETAT_TRAIT";
BESOIN_RAPPEL_DOC_FIELD_CODE = "INS_BESOIN_RAPPEL";
DATE_RAPPEL_DOC_FIELD_CODE = "INS_DATE_RAPPEL";

ETAT_DOC_ATTENTE = "202";
ETAT_DOC_AFFECTE = "203";
ETAT_DOC_TRAITE = "204";

ETAT_INSTANCE_DOC_NON = "303";
ETAT_INSTANCE_DOC_OUI = "302";
BESOIN_RAPPEL_DOC_NON = "308";
BESOIN_RAPPEL_DOC_NON_15 = "307";
BESOIN_RAPPEL_DOC_NON_30 = "402";
BESOIN_RAPPEL_DOC_NON_45 = "403";
DATE_RAPPEL_DOC_VIDE = "null";


//messages renvoyés
TEST_KO_SUM 		= "infos_test_script_Onsave_summary_KO";
TEST_KO_DETAIL 	= "warn_script_OnSave_detail";

TEST_OK_SUM 		= "infos_test_script_Onsave_summary_OK";
TEST_OK_DETAIL 	= "infos_script_OnSave_detail_OK";


ScriptResultValueChecker result = new ScriptResultValueChecker();


com.digitech.jcorbairs.DocumentContent myContents = airsDocument.getAirsDocument().getContents();

//Gestion de l'affectation des agents de traitement
if(airsDocument != null)
{
   scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : debut");
   IField fieldEtat_Doc = airsDocument.getField(ETAT_DOC_FIELD_CODE);
   IField fieldAgent_Doc = airsDocument.getField(AGENT_TRAIT_FIELD_CODE);

	String fieldStatutvalue = getFieldValue( fieldEtat_Doc );
	String fieldAgentvalue = getFieldValue( fieldAgent_Doc );

    scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : au milieu");
    com.digitech.jcorbairs.Field myFieldModified = new com.digitech.jcorbairs.Field(airsDocument.getAirsDocument().getJeton(), AGENT_TRAIT_FIELD_CODE);
    String strOldFieldValue = null;
    try
    {
        strOldFieldValue = myContents.getFieldValue(myFieldModified);
    }
    catch(XmlException eXmlExcep)
    {
        scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : ERREUR, LE CHAMP N'EXISTE PAS !!!!!");
        strOldFieldValue = "";
    }

	//si on a changé le nom de l'agent affecté
	//if (fieldStatutvalue.compareToIgnoreCase(ETAT_DOC_TRAITE) != 0 )
	//if (fieldAgent_Doc.isEdited())
	if (fieldAgentvalue != null && strOldFieldValue.compareToIgnoreCase(fieldAgentvalue) != 0)
	{
      	if( fieldAgentvalue.length() == 0 )
      	{
             scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : Aucun agent affecté, on met le doc en ATTENTE");
      	     fieldEtat_Doc.setValue( ETAT_DOC_ATTENTE );
      		 airsDocument.getFieldMap().put(ETAT_DOC_FIELD_CODE, fieldEtat_Doc );
      		 scriptLogger.warn("Setting field value : " + fieldEtat_Doc.getCode() + " - " + ETAT_DOC_ATTENTE );
        }
      	else
      	{
             scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : Un agent est affecté, on met le doc en AFFECTE");
      	     fieldEtat_Doc.setValue( ETAT_DOC_AFFECTE );
      		 airsDocument.getFieldMap().put(ETAT_DOC_FIELD_CODE, fieldEtat_Doc );
      		 scriptLogger.warn("Setting field value : " + fieldEtat_Doc.getCode() + " - " + ETAT_DOC_AFFECTE );
      	}
	    result.setValid( true );
        scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : fin");
    }
    else
    {
        scriptLogger.warn("OnSaveDoc:Gestion de l'affectation des agents : le nom de l'agent n'a pas été changé");
	    result.setValid( true );
    }
}

//Gestion de l'instance
if(airsDocument != null)
{
    scriptLogger.warn("OnSaveDoc:Gestion de l'instance : début");

    IField fieldEtatInstance_Doc = airsDocument.getField(EN_INSTANCE_FIELD_CODE);
	String fieldStatutvalue = getFieldValue( fieldEtatInstance_Doc );

	if ( fieldStatutvalue!= null )
	{
    	if( fieldStatutvalue.length() == 0 || fieldStatutvalue.compareToIgnoreCase(ETAT_INSTANCE_DOC_NON) == 0 )
    	{
            scriptLogger.warn("OnSaveDoc:Gestion de l'instance : ON MET PAS EN INSTANCE");
            IField fieldBesoinRappel_Doc = airsDocument.getField(BESOIN_RAPPEL_DOC_FIELD_CODE);
            IField fieldDateRappel_Doc = airsDocument.getField(DATE_RAPPEL_DOC_FIELD_CODE);

    	    fieldBesoinRappel_Doc.setValue( BESOIN_RAPPEL_DOC_NON );
    	    fieldDateRappel_Doc.setValue( DATE_RAPPEL_DOC_VIDE );

    		airsDocument.getFieldMap().put(BESOIN_RAPPEL_DOC_FIELD_CODE, fieldBesoinRappel_Doc );
    		airsDocument.getFieldMap().put(DATE_RAPPEL_DOC_FIELD_CODE, fieldDateRappel_Doc );

    		scriptLogger.info("Setting field value : " + fieldBesoinRappel_Doc.getCode() + " - (" + BESOIN_RAPPEL_DOC_NON + ") and "+ fieldDateRappel_Doc.getCode() + " - " + DATE_RAPPEL_DOC_VIDE);
       	    result.setValid( true );
        }
        else
        {
            scriptLogger.warn("OnSaveDoc:Gestion de l'instance : ON MET EN INSTANCE");
            result.setValid( true );
        }
    }
    scriptLogger.warn("OnSaveDoc:Gestion de l'instance : fin");
}


//Gestion du rappel
if(airsDocument != null)
{
    scriptLogger.warn("OnSaveDoc:Gestion du rappel : début");
    IField fieldBesoinRappel_Doc = airsDocument.getField(BESOIN_RAPPEL_DOC_FIELD_CODE);
	String fieldStatutvalue = getFieldValue( fieldBesoinRappel_Doc );

	if ( fieldStatutvalue!= null )
	{
    	if( fieldStatutvalue.length() == 0 || fieldStatutvalue.compareToIgnoreCase(BESOIN_RAPPEL_DOC_NON) == 0 )
    	{
            scriptLogger.warn("OnSaveDoc:Gestion du rappel : IL N'Y A PAS DE RAPPEL : on vide la date de rappel");
    	    IField fieldDateRappel_Doc = airsDocument.getField(DATE_RAPPEL_DOC_FIELD_CODE);
    	    fieldDateRappel_Doc.setValue( DATE_RAPPEL_DOC_VIDE );
    		airsDocument.getFieldMap().put(DATE_RAPPEL_DOC_FIELD_CODE, fieldDateRappel_Doc );
    		scriptLogger.warn("Setting field value : " + fieldDateRappel_Doc.getCode() + " - (" + DATE_RAPPEL_DOC_VIDE + ")");
        }
    	else
    	{
            scriptLogger.warn("OnSaveDoc:Gestion du rappel : IL Y A UN RAPPEL ");
    	    //ici, le rappel est à OUI (délai indifférent). Il faut voir si la valeur de ce champ a été modifié ou non. Si oui, on change la date de rappel, si non, on fait rien.
    	    //récupération de l'ancienne valeur du champ
            com.digitech.jcorbairs.Field myFieldModified = new com.digitech.jcorbairs.Field(airsDocument.getAirsDocument().getJeton(), BESOIN_RAPPEL_DOC_FIELD_CODE);
            String strOldFieldBesoinRappelValue = null;
            try
            {
                strOldFieldBesoinRappelValue = myContents.getFieldValue(myFieldModified);
            }
            catch(XmlException eXmlExcep)
            {
                scriptLogger.warn("OnSaveDoc:Gestion du rappel : ERREUR, LE CHAMP N'EXISTE PAS !!!!!");
                strOldFieldBesoinRappelValue = "";
            }

            scriptLogger.warn("OnSaveDoc:Gestion du rappel : Ancienne valeur " + strOldFieldBesoinRappelValue + " Nouvelle valeur : " + fieldStatutvalue);
            if (strOldFieldBesoinRappelValue.compareToIgnoreCase(fieldStatutvalue) != 0)
    	    {
    	       //La date du rappel a été changée
    	       //on récupère la date du jour
      		   Calendar myToday = GregorianCalendar.getInstance();

      		   //selon le cas, on ajoute soit 15J, 30J ou 45 J
      		   if ( fieldStatutvalue.compareToIgnoreCase(BESOIN_RAPPEL_DOC_NON_15) == 0)
               {
                    scriptLogger.warn("OnSaveDoc:Gestion du rappel : On ajoute 15 J ");
                    myToday.add(myToday.DATE, 15);
               }
               else
               {
                    if ( fieldStatutvalue.compareToIgnoreCase(BESOIN_RAPPEL_DOC_NON_30) == 0 )
                    {
                    scriptLogger.warn("OnSaveDoc:Gestion du rappel : On ajoute 30 J ");
                        myToday.add(myToday.MONTH, 1);
                    }
                    else
                    {
                    scriptLogger.warn("OnSaveDoc:Gestion du rappel : On ajoute 45 J ");
                        myToday.add(myToday.DATE, 15);
                        myToday.add(myToday.MONTH, 1);
                    }
               }
    	       IField fieldDateRappel_Doc = airsDocument.getField(DATE_RAPPEL_DOC_FIELD_CODE);
    	       String strdate = null;

               SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
               strdate = sdf.format(myToday.getTime());
    	       fieldDateRappel_Doc.setValue( strdate );
    		   airsDocument.getFieldMap().put(DATE_RAPPEL_DOC_FIELD_CODE, fieldDateRappel_Doc );
    		   scriptLogger.warn("Setting field value : " + fieldDateRappel_Doc.getCode() + " - (" + strdate + ")");

               //dans le doute, on met le champ (en instance) à Oui
               IField fieldEtatInstance_Doc = airsDocument.getField(EN_INSTANCE_FIELD_CODE);
    	       fieldEtatInstance_Doc.setValue( ETAT_INSTANCE_DOC_OUI );
    		   airsDocument.getFieldMap().put(EN_INSTANCE_FIELD_CODE, fieldEtatInstance_Doc );
    		   scriptLogger.warn("Setting field value : " + fieldEtatInstance_Doc.getCode() + " - (" + ETAT_INSTANCE_DOC_OUI + ")");
    	    }
        }
        result.setValid( true );
    }
}

output.setValue( result );



ScriptResultValueBefore traitementOK( ScriptResultValueBefore result )
{
	scriptLogger.warn("Groovy : OK");

	result.setValid( false ); // on ne sauve pas
	result.setMessageSummary(TEST_KO_SUM);
	result.setMessageDetail( TEST_KO_DETAIL );
	return result;
}

ScriptResultValueBefore traitementKO( ScriptResultValueBefore result )
{
	scriptLogger.warn("Groovy : KO");

	result.setValid( true ); // on sauve
	result.setMessageSummary(TEST_OK_SUM);
	result.setMessageDetail(TEST_OK_DETAIL );
	return result;
}


String getFieldValue( IField field )
{
	List<?> values = field.getValues();
	String fieldvalue;
	if(values != null && !values.isEmpty()) {
		fieldvalue =getServerMgr().getFieldValues(values, field.getAirsField());
		//scriptLogger.warn("ggggrrrrField Value : " + field.getCode() + " - " + fieldvalue );
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
