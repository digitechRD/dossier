import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*

import java.text.ParsePosition;
import java.util.Date; 

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.service.IDocument
import com.digitech.courrier.common.model.backend.CourrierConstants
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField          
import com.digitech.dossier.common.model.backend.airs.ITask
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.jcorbairs.Option
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.IAuthorityList
  
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.workflow.model.impl.WFTask   
import com.digitech.dossier.workflow.model.impl.WFTask.WFActor   

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.akazi.flowmind.flowbean.form.ActionFormData;

import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.model.backend.airs.ILocutionModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.model.backend.airs.impl.LocutionModel
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.common.framework.bdd.DBConnectionManager;

import static LaProvenceScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
// com.digitech.dossier.workflow.model.impl.WFTask wkfTask
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;
com.digitech.dossier.workflow.model.impl.WFTask wfTask = wkfTaskModel;
String wkfTaskSortie = wkfTaskOutput;

CONNECT_BDD_AIRS_DOSSIER_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_LOGIN");
CONNECT_BDD_AIRS_DOSSIER_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_MDP");
CONNECT_BDD_AIRS_DOSSIER_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_SERVEUR");

POOL_NAME_DOSSIER=LaProvenceScriptUtils.getConstant("POOL_NAME_DOSSIER");

FAC_ENG_NUM_FIELD_CODE = LaProvenceScriptUtils.getConstant("ENG_NUM_FIELD_CODE");
FAC_ENG_VERROU_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_ENG_VERROU_FIELD_CODE");
FAC_EMETTEUR_FIELD_CODE =LaProvenceScriptUtils.getConstant("FAC_EMETTEUR_FIELD_CODE"); 
FAC_SOC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_SOC_FIELD_CODE");
FAC_DATE_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_FIELD_CODE");  
FAC_DATE_ECH_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_ECH_FIELD_CODE");
FAC_DATE_VALID_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_VALID_FIELD_CODE");
FAC_ETAT_FIELD_CODE =LaProvenceScriptUtils.getConstant("FAC_ETAT_FIELD_CODE");
FAC_MONTANT_HT_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_MONTANT_HT_FIELD_CODE");
FAC_ASSISTANT_CODE = LaProvenceScriptUtils.getConstant("FAC_ASSISTANT_CODE");   
FAC_VALID1_CODE = LaProvenceScriptUtils.getConstant("FAC_VALID1_CODE");
FAC_VALID12_CODE = LaProvenceScriptUtils.getConstant("FAC_VALID12_CODE");
FAC_MAT_FOUR_CODE = LaProvenceScriptUtils.getConstant("FAC_MAT_FOUR_CODE");
ENG_VERROU_CODE = LaProvenceScriptUtils.getConstant("ENG_VERROU_CODE");

FAC_ENG_CT_CODE=LaProvenceScriptUtils.getConstant("FAC_ENG_CT_CODE");

FAC_VERROU_OUI_CODE = LaProvenceScriptUtils.getConstant("FAC_VERROU_OUI_CODE");
 
USR_OPT_MONTANT_MAX = LaProvenceScriptUtils.getConstant("USR_OPT_MONTANT_MAX");


FAC_NOM_TACHE_BLOQUE=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_BLOQUE");
FAC_NOM_TACHE_AVALIDER1=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AVALIDER1");
FAC_NOM_TACHE_AVALIDER2=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AVALIDER2");
FAC_NOM_TACHE_RESTEAPAYER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_RESTEAPAYER");
FAC_NOM_TACHE_ATRAITER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_ATRAITER");
FAC_NOM_TACHE_AENGAGER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AENGAGER");
FAC_NOM_TACHE_AENGAGERPOUSSER=LaProvenceScriptUtils.getConstant("FAC_NOM_TACHE_AENGAGERPOUSSER");
    
FAC_NOM_SORTIE_BAP=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_BAP");
FAC_NOM_SORTIE_BAPPARTIEL=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_BAPPARTIEL");    
FAC_NOM_SORTIE_BLOQUER=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_BLOQUER"); 
FAC_NOM_SORTIE_AVALIDER=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_AVALIDER");    
FAC_NOM_SORTIE_AENGAGER=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_AENGAGER");
FAC_NOM_SORTIE_STANDBY=LaProvenceScriptUtils.getConstant("FAC_NOM_SORTIE_STANDBY");

FAC_PARAMETRE_ACTEUR=LaProvenceScriptUtils.getConstant("FAC_PARAMETRE_ACTEUR");

FAC_ATTRIBUT_ASSISTANT_CODE = LaProvenceScriptUtils.getConstant("FAC_ATTRIBUT_ASSISTANT_CODE");
FAC_ATTRIBUT_VALID1_CODE = LaProvenceScriptUtils.getConstant("FAC_ATTRIBUT_VALID1_CODE");
FAC_ATTRIBUT_VALID12_CODE = LaProvenceScriptUtils.getConstant("FAC_ATTRIBUT_VALID12_CODE");

FAC_ETAT_AVALIDER = LaProvenceScriptUtils.getConstant("FAC_ETAT_AVALIDER");
FAC_ETAT_BAP = LaProvenceScriptUtils.getConstant("FAC_ETAT_BAP");
FAC_ETAT_BAPPARTIEL = LaProvenceScriptUtils.getConstant("FAC_ETAT_BAPPARTIEL");





// define messages
String FACTURE_NON_AUTORISE = "groovy_en_tete_action_non_autorise";   
String FACTURE_COMM_KO = "groovy_error_commentaire_absent";

String FACTURE_EN_TETE_OK = "groovy_en_tete_facture_ok";   
String FACTURE_OK = "groovy_ok_enregistrement";

String FACTURE_EN_TETE_ENG_VER = "groovy_en_tete_facture_eng_ver";
String FACTURE_KO_ENG_VER = "groovy_ko_eng_ver";

String FACTURE_EN_TETE_ENG_INCONNU = "groovy_en_tete_eng_inconnu";
String FACTURE_KO_ENG_INCONNU = "groovy_ko_eng_inconnu";

String FACTURE_EN_TETE_ENG_NON_SAISI = "groovy_en_tete_eng_non_saisi";
String FACTURE_KO_ENG_NON_SAISI = "groovy_ko_eng_non_saisi";


log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Start");
            
ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;
String wfTaskName = null;
                               
log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- NomTache : " + wfTask.getName());   
log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Sortie : " + wkfTaskSortie);


log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Je boucle sur tous les param");
Map<String, Object> listData2 = wfTask.getData();

if ( listData2 != null && listData2.size() > 0)
{
	for (Map.Entry <String, Object> maMap : listData2.entrySet())
	{
		String valeur = maMap.getValue();
		String parametre = maMap.getKey();
		log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptAfter.groovy --- Parametre : "+ parametre + ", valeur : " +valeur);
	}
}

//Positionnement de la date de validation
if  (wfTask != null && 
    (FAC_NOM_TACHE_AVALIDER1.compareTo(wfTask.getName()) == 0 || FAC_NOM_TACHE_AVALIDER2.compareTo(wfTask.getName()) == 0 || FAC_NOM_TACHE_RESTEAPAYER.compareTo(wfTask.getName()) == 0 ) && 
    (FAC_NOM_SORTIE_BAP.compareTo(wkfTaskSortie) == 0 ))
{
    log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On va positionner la date de validation" );
    
	  Locale locale = Locale.getDefault();
	  Date actuelle = new Date();
	  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String dateDuJour = dateFormat.format(actuelle);
    
    FieldUtils.setValue(theDocument, FAC_DATE_VALID_FIELD_CODE, dateDuJour);
	com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
    documentMgr.updateDocument(usrContext, theDocument, false); 
    log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On a positionnÃ© la date de validation" );
}


//cas où on importe la facture avant le bon de commande (devrait pas arriver, mais vérouillons quand mêmeà la fin du traitement par l'assistante)
if  (wfTask != null &&
	FAC_NOM_TACHE_ATRAITER.compareTo(wfTask.getName()) == 0  &&
	(FAC_NOM_SORTIE_STANDBY.compareTo(wkfTaskSortie) == 0  || FAC_NOM_SORTIE_AVALIDER.compareTo(wkfTaskSortie) == 0 ))
{
	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On va verouiller l'engagement, cas où le bon de commande arrive après la facture" );
	String engNumValue = FieldUtils.getValue(theDocument, FAC_ENG_NUM_FIELD_CODE);
	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- NumEng : "+ engNumValue);
	String matFour = FieldUtils.getValue(theDocument, FAC_MAT_FOUR_CODE); 
	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- matFour : "+ matFour);
	if ( StringUtils.isNotBlank(engNumValue) && StringUtils.isNotBlank(matFour))
	{
		ILocutionModel locutionModel = new LocutionModel();
		DocumentUtils.buildLocutionModel(locutionModel, FAC_ENG_NUM_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, engNumValue);
		DocumentUtils.buildLocutionModel(locutionModel, FAC_MAT_FOUR_CODE, Operator.OPERATOR_VALUE_EQUAL, matFour);
		// Compute search
		List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList("D_DOC_ENG"), null);
		if (documentList != null && documentList.size() == 1){
		  for (IDocument docEng : documentList) {
			log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- on boucle sur la liste des engagements");
			// On regarde si l'engagement est vÃ©rouillÃ©
			String verrou = LaProvenceScriptUtils.getTermCode(ENG_VERROU_CODE, docEng.getField(ENG_VERROU_CODE).getValue());
			log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On rÃ©cupÃ¨re l'engagement :  verrou : "+verrou);
			if (verrou == null || "NON".compareTo(verrou) == 0) {
			  // on vÃ©rouille l'engagement
			  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On verouille l'engagement");
			  docEng.getField("ENG_VERROU").setValue(LaProvenceScriptUtils.getTermID(ENG_VERROU_CODE, "OUI"));
			  com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
			  documentMgr.updateDocument(usrContext, docEng, false);
			}
		  }
		}
	}
	
	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- fin de verrou de l'engagement" );
}


//on route correctement l'Ã©tat en fonction du montant max => Valideur 2
if (wfTask != null && 
   (FAC_NOM_TACHE_AVALIDER1.compareTo(wfTask.getName()) == 0 || FAC_NOM_TACHE_BLOQUE.compareTo(wfTask.getName()) == 0 ) &&
   (FAC_NOM_SORTIE_BAP.compareTo(wkfTaskSortie) == 0 || FAC_NOM_SORTIE_BAPPARTIEL.compareTo(wkfTaskSortie) == 0 ))
{                    
	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On va tester si il y a un 2eme valideur niv 1 " );
    
	Integer Valid1 = FieldUtils.getValue(theDocument, FAC_VALID1_CODE);
	Integer Valid12 = FieldUtils.getValue(theDocument, FAC_VALID12_CODE);
	
	Map<String, Object> listData = wfTask.getData();
	boolean bDoubleValid = false;
	
	if ( listData != null && listData.size() > 0)
	{
	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Nb Datas : "+ listData.size() );
	  String sValid1 = listData.get(FAC_ATTRIBUT_VALID1_CODE);
	  String sValid12 = listData.get(FAC_ATTRIBUT_VALID12_CODE);
	  
	  List <WFActor> actors = wfTask.getActors();
	  
	  Integer iSize = actors.size();

	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- iSize : "+ iSize );
	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- le valid 1 est : "+ sValid1 );
	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- le valid 12 est : "+ sValid12 );
	  if ( iSize >= 1 )
	  {
		  String sActor = actors.get(0).getId();
		  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- sActor : "+ sActor );
		  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- et hop, je boucle aussi ");
		  
		  if ( sActor.length() > 5)
		  {
			  sValid1 = sActor.substring(5);
			  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- sIdActor : "+ sValid1 );
		  }
		  for (WFActor acteur : actors)
		  {
			  log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptAfter.groovy --- ID : "+ acteur.getId() + ", label : " +acteur.getLabel());
		  }
	  }
	  else
		  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- problème de récupération des FormData" );
	  
	  
	  for (Map.Entry <String, Object> maMap : listData.entrySet())
	  {
		  String valeur = maMap.getValue();
		  String parametre = maMap.getKey();
		  log.debug("Script triggered onSubmitTaskScriptBefore : onSubmitTaskScriptAfter.groovy --- Parametre : "+ parametre + ", valeur : " +valeur);
	  }
	  
	  if ( sValid1!= null && sValid12 != null && sValid12!="")
	  {
		  if ( sValid1.compareToIgnoreCase(sValid12) != 0)
		  {
		  	bDoubleValid = true;
			log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- OK, il y a double validation" );
		  }
	  }
	}  
	if ( bDoubleValid )
	  {
	    log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Ok, il y a un 2eme valideur niv 1, on change pas l'état " );
	  }
	  else
	  {
	    log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On va router correctement l'Ã©tat AIRS en fonction du montant " );
	    
	  	Integer MontantHT = FieldUtils.getValue(theDocument, FAC_MONTANT_HT_FIELD_CODE);
	  	Integer MontantMax = 0;
	  	Option optMontantMax = usrContext.getOption(USR_OPT_MONTANT_MAX);
	  	
	  	if ( optMontantMax != null )
	  		MontantMax = optMontantMax.getNumericValue();
	  	else
	  		log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Montant HT : l'option est introuvable pour le user ");
	  	
	  	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Montant HT : "+MontantHT);
	  	log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Montant Max : "+MontantMax);
	  	
	  	if ( MontantHT >= MontantMax )
	  	{
	  		FieldUtils.setValue(theDocument, FAC_ETAT_FIELD_CODE, FAC_ETAT_AVALIDER);
	  		com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
	      documentMgr.updateDocument(usrContext, theDocument, false); 
	  		log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Le montant max est dÃ©passÃ©, on envoit au valideur niv2 ");
	  	}
	  	else
	  	{
	  	    if ( FAC_NOM_SORTIE_BAP.compareTo(wkfTaskSortie) == 0 )
	  	    {
	    	    FieldUtils.setValue(theDocument, FAC_ETAT_FIELD_CODE, FAC_ETAT_BAP);
	    		  com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
	          documentMgr.updateDocument(usrContext, theDocument, false); 
	    		  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Le montant max n'est pas dÃ©passÃ©, on met en BAP ");
	        }
	        else
	        {  
	          FieldUtils.setValue(theDocument, FAC_ETAT_FIELD_CODE, FAC_ETAT_BAPPARTIEL);
	    		  com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
	          documentMgr.updateDocument(usrContext, theDocument, false); 
	    		  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Le montant max n'est pas dÃ©passÃ©, on met en BAPPartiel ");
	        }
	     }
	  }
}

//on synchronise l'assistant sÃ©lectionnÃ© dans le WF avec la valeur AIRS de l'assistant
if (wfTask != null && FAC_NOM_TACHE_AENGAGERPOUSSER.compareTo(wfTask.getName()) == 0 && FAC_NOM_SORTIE_AENGAGER.compareTo(wkfTaskSortie) == 0 )
{                    
  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- On va rÃ©cupÃ©rer l'assistant sÃ©lectionnÃ© dans le WF pour le positionner dans la variable AIRS" );
  
  Map<String, Object> listData = wfTask.getData();
    
  if ( listData != null && listData.size() > 0)
  {
	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- Nb Datas : "+ listData.size() );
	  String sActeur = listData.get(FAC_PARAMETRE_ACTEUR);
	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- l'acteur est : "+ sActeur );
	  if ( sActeur != null )
	  {
      boolean valueActeurChanged = true;
		  String sIdActeur = sActeur.substring(5);
		  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- l'id de l'acteur en string est : "+ sIdActeur );
		  if ( sIdActeur != null && sIdActeur.size() > 0 )
		  {
			   Integer nIdActeur = Integer.valueOf(sIdActeur);
         log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- l'id de l'acteur en string est : "+ nIdActeur );
         if ( nIdActeur != null && nIdActeur > 0 )
         {
              Object objActeurValeur = FieldUtils.getValue(theDocument, FAC_ASSISTANT_CODE);
                   
              if( objActeurValeur != null && ( ( Integer)objActeurValeur).equals(nIdActeur))
              {
                   log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- l'id de l'acteur n'a pas changé" );
                   valueActeurChanged = false;
              }
              else
              {
                  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- l'id de l'acteur a changé" );
                  FieldUtils.setValue(theDocument, FAC_ASSISTANT_CODE, nIdActeur);
              }
                   
              // On récupère le niveau de secret correpondant à l'assistant et on laffecte à la facture
              Integer newSecreLevel = getSecretlevelAssistant(nIdActeur, log);
              log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- NOUVEAU SECRET LEVEL : "+ newSecreLevel );
              log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- SECERT LEVEL DOCUMENT AVANT SET : "+ theDocument.getAirsDocument().getSecretLevel());
             
              if (newSecreLevel != 999)
              {
                  theDocument.getAirsDocument().setSecretLevel(newSecreLevel);

                  if( !valueActeurChanged )
                    // pas bo : il faut usiter les methodes & object de la dossier-core, mais bon, parfois, nous n'avons pas le choix ..;
                    theDocument.getAirsDocument().innerDocument.updateContent();
              }
              log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- SECERT LEVEL DOCUMENT APRES SET : "+ theDocument.getAirsDocument().getSecretLevel());
              // On met à jour le document
              com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
              documentMgr.updateDocument(usrContext, theDocument, false);
              log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- SECERT LEVEL DOCUMENT APRES UPDATE : "+ theDocument.getAirsDocument().getSecretLevel());
              log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- OK, c fait" );
          }
		  }
	  }
  }
  else
  {
	  log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- il n'y a pas d'acteur, bizarre" );
  } 
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered onSubmitTaskScriptAfter : onSubmitTaskScriptAfter.groovy --- End");


Integer getSecretlevelAssistant(Integer idAssistant, Logger logger) throws DigiInternalException {
Logger scriptLogger = logger;

Integer secretLevel = 999;

Connection dbConnection = null;
Statement dbStatement = null;
ResultSet rsSet = null;

DBConnectionManager connectManager = DBConnectionManager.getInstance();

// On vérifie que le pool de connexion n'existe pas déja
if(connectManager.getPool(POOL_NAME_DOSSIER) == null) {
  connectManager.release();
  connectManager.loadDriver("com.mysql.jdbc.Driver");
  connectManager.addPool(POOL_NAME_DOSSIER, CONNECT_BDD_AIRS_DOSSIER_SERVEUR, CONNECT_BDD_AIRS_DOSSIER_LOGIN, CONNECT_BDD_AIRS_DOSSIER_MDP, 5, "com.digitech.common.framework.bdd.mysql.SequenceMyImpl");
   scriptLogger.info("GROOVY : adding pool :" + POOL_NAME_DOSSIER);
}

try {
  dbConnection = connectManager.getConnection(POOL_NAME_DOSSIER);
  dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
  String request = "SELECT secret from CORRESP_ENG where assistant='" + idAssistant + "'";
   scriptLogger.debug("Script triggered on onSubmitTaskScriptAfter : Execution de la requete [ " + request + "]");
  rsSet = dbStatement.executeQuery(request);
  if(rsSet != null) {
    while(rsSet.next()) {
      String secretLevelString = rsSet.getString("secret");
       
      if(StringUtils.isNotBlank(secretLevelString)) {
        secretLevel = Integer.valueOf(secretLevelString);
        scriptLogger.debug("Script triggered on onSubmitTaskScriptAfter : Valeurs trouvees [" + secretLevel + "]");
        continue;
      }
    }
    rsSet.close();
  }
  else {
     scriptLogger.debug("Script triggered on onSubmitTaskScriptAfter : Il n'y a pas de correspondance pour l'assistant : " +  idAssistant);
  }
}
catch(DigiInternalException e) {
  scriptLogger.error(e.getMessage());
}
catch(SQLException e) {
  scriptLogger.error(e.getMessage());
}
finally{
  if (dbStatement != null){
    dbStatement.close();
  }
}
scriptLogger.info("GROOVY : freeing pool :" + POOL_NAME_DOSSIER);
connectManager.freeConnection(POOL_NAME_DOSSIER, dbConnection);
 scriptLogger.debug("Script triggered on onSubmitTaskScriptAfter : FIN de Recuperation des champs [secretLevel]");
return secretLevel;
}
