import java.util.List;
import java.util.Map;
import java.lang.Double;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.Utils;

AGENT_TRAIT_FIELD_CODE = "TRAIT_AGENT";
ETAT_DOC_FIELD_CODE = "ETAT_TRAIT"
EN_INSTANCE_FIELD_CODE = "INS_EN_INSTANCE";
BESOIN_RAPPEL_DOC_FIELD_CODE = "INS_BESOIN_RAPPEL";
DATE_RAPPEL_DOC_FIELD_CODE = "INS_DATE_RAPPEL";
ETAT_DOC_INITIAL_VALUE = "203";
ETAT_DOC_FINAL_VALUE = "204";

ETAT_INSTANCE_DOC_NON = "303";
BESOIN_RAPPEL_DOC_NON = "308";
DATE_RAPPEL_DOC_VIDE = "";

scriptLogger.warn("Change_Etat_Affecte_Trait:Gestion de l'état du traitement : debut");  
if(airsDocument != null) 
{
	IField fieldEtat_Doc = airsDocument.getField(ETAT_DOC_FIELD_CODE);  
	String fieldStatutvalue = getFieldValue( fieldEtat_Doc );
  scriptLogger.warn("Change_Etat_Affecte_Trait:Gestion de l'état du traitement : apres recherche"); 
	
  if( fieldStatutvalue.compareToIgnoreCase(ETAT_DOC_INITIAL_VALUE) == 0 )
	{
    scriptLogger.warn("Change_Etat_Affecte_Trait:Gestion de l'état du traitement : Le document était bien dans l'état AFFECTE");  
		fieldEtat_Doc.setValue( ETAT_DOC_FINAL_VALUE );
		airsDocument.getFieldMap().put(ETAT_DOC_FIELD_CODE, fieldEtat_Doc );
		scriptLogger.warn("Setting field value : " + fieldEtat_Doc.getCode() + " - " + ETAT_DOC_FINAL_VALUE );	
		
		IField fieldEtatInstance_Doc = airsDocument.getField(EN_INSTANCE_FIELD_CODE);
  	fieldEtatInstance_Doc.setValue( ETAT_INSTANCE_DOC_NON );
  	airsDocument.getFieldMap().put(EN_INSTANCE_FIELD_CODE, fieldEtatInstance_Doc );
  	
  	IField fieldBesoinRappel_Doc = airsDocument.getField(BESOIN_RAPPEL_DOC_FIELD_CODE);
  	fieldBesoinRappel_Doc.setValue( BESOIN_RAPPEL_DOC_NON );
  	airsDocument.getFieldMap().put(BESOIN_RAPPEL_DOC_FIELD_CODE, fieldBesoinRappel_Doc );
  	
  	IField fieldDateRappel_Doc = airsDocument.getField(DATE_RAPPEL_DOC_FIELD_CODE);
  	fieldDateRappel_Doc.setValue( DATE_RAPPEL_DOC_VIDE );
  	airsDocument.getFieldMap().put(DATE_RAPPEL_DOC_FIELD_CODE, fieldDateRappel_Doc );  	
  	  
    scriptLogger.warn("Change_Etat_Affecte_Trait:Gestion de l'état du traitement : On a mis le document dans l'état traité");  
	}
	else
	{
		scriptLogger.warn("Change_Etat_Affecte_Trait:Gestion de l'état du traitement : Le document n'est pas dans l'état initial AFFECTE");  
	}
}
else
{
		scriptLogger.warn("DOCUMENT NULLLLLLLLLLL");
}
scriptLogger.warn("Change_Etat_Affecte_Trait:Gestion de l'état du traitement : fin");  

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
