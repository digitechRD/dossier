import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;

import com.digitech.dossier.common.utils.UserUtils
import com.digitech.jcorbairs.Term
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.service.ServiceConstants
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger

import java.text.ParsePosition;
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.model.backend.airs.ILocutionModel

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.digitech.dossier.common.model.backend.airs.impl.LocutionModel

import com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.common.framework.bdd.DBConnectionManager;

import java.lang.Math;

import com.digitech.dossier.common.model.backend.Constants.LockType;

import static LaProvenceScriptUtils;


/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;



// define messages
String FACTURE_NON_AUTORISE = "groovy_en_tete_modification_non_autorise";
String FACTURE_MONTANT_KO = "groovy_error_montant_ttc";
String ENG_NUM_INVALID = "groovy_en_tete_eng_non_saisi" ;
String ENG_NUM_KO = "groovy_modification_engnum_ko";


String TYPE_ENG_INVALID  = "groovy_modification_typenum_invalid";
String TYPE_ENG_KO = "groovy_modification_typenum_ko";

String ENG_NUM_LOCKED = "groovy_modification_engnum_locked" ;
String ENG_NUM_LOCKED_DETAIL = "groovy_modification_engnum_locked_detail";
String ENG_NUM_PB_ENR ="groovy_modification_engnum_pbenr";
String ENG_NUM_PB_ENR_DETAIL = "groovy_modification_engnum_pbenr_detail";
String ENG_NUM_INEXISTANT = "groovy_modification_engnum_inexistant";


CONNECT_BDD_REF_ENG_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_REF_ENG_LOGIN");
CONNECT_BDD_REF_ENG_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_REF_ENG_MDP");
CONNECT_BDD_REF_ENG_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_REF_ENG_SERVEUR");

CONNECT_BDD_AIRS_DOSSIER_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_LOGIN");
CONNECT_BDD_AIRS_DOSSIER_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_MDP");
CONNECT_BDD_AIRS_DOSSIER_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_SERVEUR");

POOL_NAME = LaProvenceScriptUtils.getConstant("POOL_NAME");
POOL_NAME_CAPTURE = LaProvenceScriptUtils.getConstant("POOL_NAME_CAPTURE");
POOL_NAME_DOSSIER=LaProvenceScriptUtils.getConstant("POOL_NAME_DOSSIER");
POOL_NAME_ENGAG=LaProvenceScriptUtils.getConstant("POOL_NAME_ENGAG");

FAC_TTC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_TTC_FIELD_CODE");
FAC_HT_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_HT_FIELD_CODE");
FAC_TVA_TOTAL_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_TVA_TOTAL_FIELD_CODE");
ENG_NUM_FIELD_CODE = LaProvenceScriptUtils.getConstant("ENG_NUM_FIELD_CODE");
FAC_MAT_FOUR_CODE =LaProvenceScriptUtils.getConstant("FAC_MAT_FOUR_CODE");
ENG_VERROU_CODE = LaProvenceScriptUtils.getConstant("ENG_VERROU_CODE");
FAC_EMETTEUR_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_EMETTEUR_FIELD_CODE");
FAC_SOC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_SOC_FIELD_CODE");
FAC_DATE_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_FIELD_CODE");
FAC_DATE_ECH_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_DATE_ECH_FIELD_CODE");

log.debug("Script triggered on before save : beforeSave_verifyTTC.groovy --- Start");

FAC_TYPE_DOC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_TYPE_DOC_FIELD_CODE");
AL_FAC_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_FIELD_CODE");
AL_FAC_DUP_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_DUP_FIELD_CODE");
AL_AVOIR_FIELD_CODE  = LaProvenceScriptUtils.getConstant("AL_AVOIR_FIELD_CODE");

// On vérifie qu'on n'est pas dans le cas d'un contrat avant d'éxécuter le script
String facTypeDoc = document.getField(FAC_TYPE_DOC_FIELD_CODE).getValue();
if (String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_FIELD_CODE)).compareTo(facTypeDoc) == 0 ||
  String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_DUP_FIELD_CODE)).compareTo(facTypeDoc) == 0||
  String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_AVOIR_FIELD_CODE)).compareTo(facTypeDoc) == 0){
  
  // Generates the final chrono number
  boolean isTvaValid = false;
  boolean isValidEngNum = false;
  Double HT = FieldUtils.getValue(document, FAC_HT_FIELD_CODE);
  Double TVA = FieldUtils.getValue(document, FAC_TVA_TOTAL_FIELD_CODE);
  Double TTC = FieldUtils.getValue(document, FAC_TTC_FIELD_CODE);
  
  Double addss = HT+TVA;
  log.debug("Script triggered on beforeSave_verify : HT + TVA : "+ addss);
  log.debug("Script triggered on beforeSave_verify : TTC : "+ TTC);
  log.debug("Script triggered on beforeSave_verify : TVA inValid : "+Math.abs(TTC - (HT + TVA)));
  
  //if (TTC == (HT + TVA)){
  //if ((Math.abs(TTC - (HT + TVA)) == 0)){
  if (Math.abs(TTC - (HT + TVA)) < 0.009) {
    isTvaValid = true;
    log.debug("Script triggered on beforeSave_verify : TVA Valid");
  }
  
  if (!isTvaValid){
    log.debug("Script triggered on beforeSave_verify : TVA inValid : "+Math.abs(TTC - (HT + TVA)));
    ScriptResultValueChecker result = new ScriptResultValueChecker();
    result.setValid(false);
    result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
    result.setMessageSummary(FACTURE_NON_AUTORISE);
    result.setMessageDetail(FACTURE_MONTANT_KO);
    output.setValue(result);
  }
  else {
    boolean bOK = true;
    String engNumValue = FieldUtils.getValue(document, ENG_NUM_FIELD_CODE);
    if ( engNumValue != null && engNumValue.size() > 0)
    {
      log.debug("Script triggered on beforeSave_verify : Le nouveau NumÃ©ro d'engagement est : "+engNumValue);
      //Pattern p = Pattern.compile("[a-zA-Z]{2}[0-9]{6}");
      //Matcher m = p.matcher(engNumValue);
      //isValidEngNum = m.find();
      isValidEngNum = true;
    }
    else
    {
      isValidEngNum = true;
    }
    boolean isValidAA = false;
    Integer secretLevel = -1;
    if (isValidEngNum){
      log.debug("Script triggered on beforeSave_verify : numeroEngagement valid");
      secretLevel = getSecretLevel(engNumValue, document);
      if (secretLevel > -1)
      {
        isValidAA = true;
        //document.getAirsDocument().setSecretLevel(secretLevel);
      }
      else 
      {
        log.debug("Script triggered on beforeSave_verify : Type engagement non présent dans le référentiel");
        ScriptResultValueChecker result = new ScriptResultValueChecker();
        result.setValid(false);
        result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
        result.setMessageSummary(TYPE_ENG_INVALID);
        result.setMessageDetail(TYPE_ENG_KO);
        output.setValue(result);
      }
    } 
    else 
    {
      log.debug("Script triggered on beforeSave_verify : numeroEngagement non - valide");
      ScriptResultValueChecker result = new ScriptResultValueChecker();
      result.setValid(false);
      result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
      result.setMessageSummary(ENG_NUM_INVALID);
      result.setMessageDetail(ENG_NUM_KO);
      output.setValue(result);
    }
    
    log.debug("Script triggered on beforeSave_verify : isValidEngNum : "+isValidEngNum);
    log.debug("Script triggered on beforeSave_verify : isValidAA : "+isValidAA);
    if (isValidEngNum && isValidAA)
    {
      log.debug("Script triggered on before save : numeroEngagement valide et present dans la table CORRESP_ENG");
      //on vÃ©rifie que le numÃ©ro d'engagement a changÃ©
      com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
      IDocument oldDocument = documentMgr.getDocument(UserUtils.getAdminUserContext(), document.getAirsRefId());
  
      String engNumValueOld = FieldUtils.getValue(oldDocument, ENG_NUM_FIELD_CODE);
      String matFour = FieldUtils.getValue(document, FAC_MAT_FOUR_CODE);
  
      log.debug("Script triggered on beforeSave_verify : le numÃ©ro d'engagement. Old : "+engNumValueOld+" New : "+engNumValue);
  
  
      //si le nouveau numéro d'engagement n'est pas vide et qu'il a changé
      if ( StringUtils.isNotBlank(engNumValue) && (engNumValueOld == null || engNumValue.compareToIgnoreCase(engNumValueOld) != 0) )
      {
        ILocutionModel locutionModel = new LocutionModel();
        DocumentUtils.buildLocutionModel(locutionModel, ENG_NUM_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, engNumValue);
        log.debug("Script triggered on beforeSave_verify : le matFour : "+matFour);
        DocumentUtils.buildLocutionModel(locutionModel, FAC_MAT_FOUR_CODE, Operator.OPERATOR_VALUE_EQUAL, matFour);
        // Compute search
        //List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList("D_DOC_ENG"), null);
        List<IDocument> documentList = DocumentUtils.search(userContext, locutionModel, DocumentUtils.getSearchContentTypeList("D_DOC_ENG"), null);
        if (documentList != null && documentList.size() == 1){
          for (IDocument docEng : documentList) {
            // On regarde si l'engagement est vÃ©rouillÃ©
            String verrou = LaProvenceScriptUtils.getTermCode(ENG_VERROU_CODE, docEng.getField(ENG_VERROU_CODE).getValue());
            log.debug("Script triggered on beforeSave_verify : On rÃ©cupÃ¨re l'engagement :  verrou : "+verrou);
            if (verrou == null || "NON".compareTo(verrou) == 0) {
              // on vÃ©rouille l'engagement
              log.debug("Script triggered on beforeSave_verify : On verouille l'engagement");
              docEng.getField("ENG_VERROU").setValue(LaProvenceScriptUtils.getTermID(ENG_VERROU_CODE, "OUI"));
              documentMgr.updateDocument(usrContext, docEng, false);
  
              // on met à jour les données de la facture
              String emetteur = (String) docEng.getField(FAC_EMETTEUR_FIELD_CODE).getValue();
              log.debug("Script triggered on beforeSave_verify : emetteur : "+emetteur);
              document.getField(FAC_EMETTEUR_FIELD_CODE).setValue(emetteur);
  
              String society = (String) docEng.getField(FAC_SOC_FIELD_CODE).getValue();
              log.debug("Script triggered on beforeSave_verify : society : "+society);
              document.getField(FAC_SOC_FIELD_CODE).setValue(society);
  
              java.util.Date dateFacDate = FieldUtils.getValue(document, FAC_DATE_FIELD_CODE);
              DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
              String facDate = dateFormat.format(dateFacDate);
              log.debug("Script triggered on beforeSave_verify : facDate : "+facDate);
  
              // On rÃ©cupÃ¨re la date d'Ã©chÃ©ance
              String dueDat = treatmentDueDate(engNumValue, facDate);
		          //  String dueDat = null;

              if (dueDat != null){              
		            log.debug("Script triggered on beforeSave_verify : Avant MAJ DueDate");
                document.getField(FAC_DATE_ECH_FIELD_CODE).setValue(LaProvenceScriptUtils.stringToDate(dueDat));
                log.debug("Script triggered on beforeSave_verify : Après MAJ DueDate");
              }
		else
		{
			dueDat = treatmentDueDate(engNumValue, facDate);
			 if (dueDat != null){              
		            log.debug("Script triggered on beforeSave_verify : Avant MAJ DueDate");
                	     document.getField(FAC_DATE_ECH_FIELD_CODE).setValue(LaProvenceScriptUtils.stringToDate(dueDat));
                          log.debug("Script triggered on beforeSave_verify : Après MAJ DueDate");
              	}
			else
			{
				// Il y a eu un soucis lors de la récupération de la date d'echeance
              		log.debug("Script triggered on beforeSave_verify : Problème de recuperation de la date d'echeance");
              		ScriptResultValueChecker result = new ScriptResultValueChecker();
            	  		result.setValid(false);
              		result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
              		result.setMessageSummary(ENG_NUM_PB_ENR);
              		result.setMessageDetail(ENG_NUM_PB_ENR_DETAIL);
              		output.setValue(result);
              		bOK = false;
			}
		}
              // On rÃ©cupÃ¨re les champs assistant,valid1,valid2,secretLevel
              treatmentAVVSL(engNumValue,document);
              // On met Ã  jour le document
  
                log.debug("Script triggered on beforeSave_verify : Avant update doc");
                documentMgr.updateDocument(usrContext, document, false);
                log.debug("Script triggered on beforeSave_verify : Après update doc");
                documentMgr.lockDocument(usrContext, document, LockType.AUTO);
                log.debug("Script triggered on beforeSave_verify : Après lock doc");
            }
            else
            {
              // L'engagement est vÃ©rouillÃ©
              log.debug("Script triggered on beforeSave_verify : Engagement vÃ©rouillÃ©");
              ScriptResultValueChecker result = new ScriptResultValueChecker();
              result.setValid(false);
              result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
              result.setMessageSummary(FACTURE_NON_AUTORISE);
              result.setMessageDetail(ENG_NUM_LOCKED_DETAIL);
              output.setValue(result);
              bOK = false;
            }
          }
        }
        else
        {
          // L'engagement n'existe pas ERROR
          log.debug("Script triggered on beforeSave_verify : l engagement n existe pas");
          ScriptResultValueChecker result = new ScriptResultValueChecker();
          result.setValid(false);
          result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
          result.setMessageSummary(FACTURE_NON_AUTORISE);
          result.setMessageDetail(ENG_NUM_INEXISTANT);
          output.setValue(result);
          bOK = false;
        }
      }
  
      //si le nouveau numéro d'engagement est vide, on vide les champs
      if ( !StringUtils.isNotBlank(engNumValue) && StringUtils.isNotBlank(engNumValueOld)  )
      {
        // on met à jour les données de la facture
        log.debug("Script triggered on beforeSave_verify : on vide l'emetteur");
        document.getField(FAC_EMETTEUR_FIELD_CODE).setValue("");
  
        log.debug("Script triggered on beforeSave_verify : on vide la society");
      //  document.getField(FAC_SOC_FIELD_CODE).setAirsValue(null);
    //  document.getField(FAC_SOC_FIELD_CODE) = null;
  
        log.debug("Script triggered on beforeSave_verify : on vide la facDate");
        document.getField(FAC_DATE_ECH_FIELD_CODE).setValue("");
  
        log.debug("Script triggered on beforeSave_verify : on vide le trait1");
        document.getField("FAC_TRAIT1").setValue(null);
  
        log.debug("Script triggered on beforeSave_verify : on vide le valid1");
        document.getField("FAC_VALID1").setValue(null);
  
        log.debug("Script triggered on beforeSave_verify : on vide le valid12");
        document.getField("FAC_VALID12").setValue(null);
  
        log.debug("Script triggered on beforeSave_verify : on vide le valid2");
        document.getField("FAC_VALID2").setValue(null);
  
        log.debug("Script triggered on beforeSave_verify : on vide le secret level");
        //recuperation du niveau de secret
        Integer nIdAssistant = usrContext.getUser().getId();
        Integer newSecreLevel = getSecretlevelAssistant(nIdAssistant, log);
        document.getAirsDocument().setSecretLevel(newSecreLevel);
  
        // On met Ã  jour le document      
        log.debug("Script triggered on beforeSave_verify : on update");
        documentMgr.updateDocument(usrContext, document, false);
        log.debug("Script triggered on beforeSave_verify : on lock");
        documentMgr.lockDocument(usrContext, document, LockType.AUTO);
      }
  
      //si l'ancien numéro d'engagement n'est pas null et qu'il a changé
      if (StringUtils.isNotBlank(engNumValueOld) && (engNumValue == null || engNumValueOld.compareToIgnoreCase(engNumValue) != 0) )
      {
        log.debug("Script triggered on beforeSave_verify : on déverouille l'ancien engagement ");
  
        //on vÃ©rifie qu'il existe un engagement avec l'ancient numÃ©ro numÃ©ro
        ILocutionModel locutionModelOld = new LocutionModel();
        DocumentUtils.buildLocutionModel(locutionModelOld, ENG_NUM_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, engNumValueOld);
        // Compute search for old engagement
        List<IDocument> documentListOld = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModelOld, DocumentUtils.getSearchContentTypeList("D_DOC_ENG"), null);
        if (documentListOld != null && documentListOld.size() == 1){
          for (IDocument docEngOld : documentListOld) {
            // On regarde si l'engagement est vÃ©rouillÃ©
            String verrouOld = LaProvenceScriptUtils.getTermCode(ENG_VERROU_CODE, docEngOld.getField(ENG_VERROU_CODE).getValue());
            log.debug("Script triggered on beforeSave_verify : verrou de l'ancien engagement est : "+verrouOld);
            if (verrouOld == null || "OUI".compareTo(verrouOld) == 0) {
              // on dÃ©vÃ©rouille le document
              docEngOld.getField(ENG_VERROU_CODE).setValue(LaProvenceScriptUtils.getTermID(ENG_VERROU_CODE, "NON"));
              log.debug("Script triggered on beforeSave_verify : l'ancien engagement etait vÃ©rouille, on le deverouille");
              // On sauvegarde l'ancien engageÃ¹ment
              documentMgr.updateDocument(usrContext, docEngOld, false);
            }
          }
        }
      }
      else
      {
        log.debug("Script triggered on beforeSave_verify : l'ancien numÃ©ro d'engagement est vide ou le numero d'engagement n'a pas changé ");
      }
      if ( bOK )
      {
        ScriptResultValueChecker result = new ScriptResultValueChecker();
        result.setValid(true);
        output.setValue(result)
      }
    }
  }
  
} else {
  ScriptResultValueChecker result = new ScriptResultValueChecker();
  result.setValid(true);
  output.setValue(result);
}
log.debug("Script triggered on beforeSave_verify : beforeSave_verifyTTC.groovy --- End");




public String treatmentDueDate(String numEng,String dateRef){
  scriptLogger.info("Script triggered on beforeSave_verify : DEBUT Recuperation dueDate");
  String dueDateComputed = null;
  if (StringUtils.isNotBlank(numEng) && StringUtils.isNotBlank(dateRef)){

    Connection  dbConnection       = null;
    Statement   dbStatement        = null;
    ResultSet   rsSet              = null;

    DBConnectionManager connectManager = DBConnectionManager.getInstance();

    // On vérifie que le pool dde connexion n'existe pas déja
    if( connectManager.getPool(POOL_NAME_ENGAG) == null ){
       connectManager.release();
       connectManager.loadDriver("oracle.jdbc.driver.OracleDriver");
       connectManager.addPool(POOL_NAME_ENGAG, CONNECT_BDD_REF_ENG_SERVEUR, CONNECT_BDD_REF_ENG_LOGIN, CONNECT_BDD_REF_ENG_MDP, 5, "com.digitech.common.framework.bdd.oracle.SequenceOraImpl");
       scriptLogger.info("GROOVY : adding pool :"+ POOL_NAME_ENGAG);
    }

    try {
      dbConnection = connectManager.getConnection(POOL_NAME_ENGAG);
      if ( dbConnection != null)
	{
      		dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      		String request = "SELECT TO_CHAR(ADD_MONTHS(TO_DATE('"+ dateRef + "', 'YYYYMMDD'), DECODE(NBMOICR, NULL, 0, NBMOICR))+DECODE(NBJOUCR, NULL, 0, NBJOUCR), 'DD/MM/YYYY') AS ECH_DATE FROM CIPPRV.ECH ECHEAN WHERE ECHEAN.ECH IN (SELECT CDECHEA FROM CIPPRV.TIEFOU TIEF, PROENGBCOM WHERE IDBONCOM = '"+ numEng +"' AND PROENGBCOM.CDTIERS = TIEF.CDTIERS)";
      		scriptLogger.info("Script triggered on beforeSave_verify : Execution de la requete [ " + request + "]");
      		rsSet = dbStatement.executeQuery(request);
    		if(rsSet != null) {
        		boolean passedIn = false;
       	 	while(rsSet.next()) {
        	  		passedIn = true;
        	  		dueDateComputed = rsSet.getString("ECH_DATE");
        	  		scriptLogger.info("Script triggered on beforeSave_verify : dueDate trouvee [" + dueDateComputed + "]");
       	 	}
        		rsSet.close();
      		}
	}
      dbStatement.close();
    }
    catch(DigiInternalException e) {
      // TODO Auto-generated catch block
      scriptLogger.error(e.getMessage());
	//e.printStackTrace();

    }
    catch(SQLException e) {
      // TODO Auto-generated catch block
      scriptLogger.error(e.getMessage());
	//e.printStackTrace();

    }
    connectManager.freeConnection(POOL_NAME_ENGAG, dbConnection);
  }
  scriptLogger.info("Script triggered on beforeSave_verify : FIN de Recuperation dueDate");
  return dueDateComputed;
}


// RÃ©cupÃ©ration de l'assistant, du valid1, valid2 et du secret level
public void treatmentAVVSL(String numEng,IDocument theDoc){
  scriptLogger.info("Script triggered on beforeSave_verify : DEBUT Recuperation des champs [assistant,valid1, valid2, secretLevel]");
  String assistant = null;
  String valid1 = null;
  String valid12 = null;
  String valid2 = null;
  String secretLevel = null;
  if (StringUtils.isNotBlank(numEng)){

  Connection  dbConnection       = null;
  Statement   dbStatement        = null;
  ResultSet   rsSet              = null;

  DBConnectionManager connectManager = DBConnectionManager.getInstance();

  // On vérifie que le pool dde connexion n'existe pas déja
  if( connectManager.getPool(POOL_NAME_DOSSIER) == null ){
    connectManager.release();
    connectManager.loadDriver("com.mysql.jdbc.Driver");
    connectManager.addPool(POOL_NAME_DOSSIER, CONNECT_BDD_AIRS_DOSSIER_SERVEUR, CONNECT_BDD_AIRS_DOSSIER_LOGIN, CONNECT_BDD_AIRS_DOSSIER_MDP, 5, "com.digitech.common.framework.bdd.mysql.SequenceMyImpl");
    scriptLogger.info("GROOVY : adding pool :"+ POOL_NAME_DOSSIER);
  }
  try {
    dbConnection = connectManager.getConnection(POOL_NAME_DOSSIER);
    dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    String request = "SELECT assistant,valid1,valid12,valid2,secret from CORRESP_ENG where eng_num='" + numEng.substring(0,2) + "'";
    scriptLogger.info("Script triggered on beforeSave_verify : Execution de la requete [ " + request + "]");
    rsSet = dbStatement.executeQuery(request);
    if(rsSet != null) {
    while(rsSet.next()) {
      assistant = rsSet.getString("assistant");
      valid1 = rsSet.getString("valid1");
      valid12 = rsSet.getString("valid12");
      valid2 = rsSet.getString("valid2");
      secretLevel = rsSet.getString("secret");
      scriptLogger.info("Script triggered on beforeSave_verify : Valeurs trouvees [" + assistant + ", " + valid1 + ", " + valid2 + ", " + secretLevel + "]");
      if (StringUtils.isNotBlank(secretLevel)){
      	theDoc.getAirsDocument().setSecretLevel(Integer.valueOf(secretLevel));     
	} 
      theDoc.getField("FAC_TRAIT1").setValue(Integer.parseInt(assistant));
      theDoc.getField("FAC_VALID1").setValue(Integer.parseInt(valid1));
      theDoc.getField("FAC_VALID2").setValue(Integer.parseInt(valid2));
      
      if (StringUtils.isNotBlank(valid12)){
        theDoc.getField("FAC_VALID12").setValue(Integer.parseInt(valid12));
      }
      else{
        theDoc.getField("FAC_VALID12").setValue(null);
	  // theDoc.getField("FAC_VALID12").resetValue();
      }
    }
    rsSet.close();
    }
    else
    {
    scriptLogger.info("Script triggered on beforeSave_verify : erreur de recuperation des champs");
    }
    dbStatement.close();
  }
  catch(DigiInternalException e) {
    // TODO Auto-generated catch block
    scriptLogger.error(e.getMessage());
  }
  catch(SQLException e) {
    // TODO Auto-generated catch block
    scriptLogger.error(e.getMessage());
  }
  connectManager.freeConnection(POOL_NAME_DOSSIER, dbConnection);
  }
  scriptLogger.info("Script triggered on beforeSave_verify : FIN de Recuperation des champs [assistant,valid1, valid2, secretLevel]");
  return;
}


// Recuperation du secret level
public Integer getSecretLevel(String numEng,IDocument theDoc){
  scriptLogger.debug("Script triggered on beforeSave_verify : DEBUT Recuperation du secretLevel");
  Integer secretLevel = 600;
  if (StringUtils.isNotBlank(numEng)){

    Connection  dbConnection       = null;
    Statement   dbStatement        = null;
    ResultSet   rsSet              = null;

    DBConnectionManager connectManager = DBConnectionManager.getInstance();

    // On vérifie que le pool dde connexion n'existe pas déja
    if( connectManager.getPool(POOL_NAME_DOSSIER) == null ){
       connectManager.release();
       connectManager.loadDriver("com.mysql.jdbc.Driver");
       connectManager.addPool(POOL_NAME_DOSSIER, CONNECT_BDD_AIRS_DOSSIER_SERVEUR, CONNECT_BDD_AIRS_DOSSIER_LOGIN, CONNECT_BDD_AIRS_DOSSIER_MDP, 5, "com.digitech.common.framework.bdd.mysql.SequenceMyImpl");
       scriptLogger.info("GROOVY : adding pool :"+ POOL_NAME_DOSSIER);
    }

    try {
      dbConnection = connectManager.getConnection(POOL_NAME_DOSSIER);
      dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      String request = "SELECT secret from CORRESP_ENG where eng_num='" + (numEng.substring(0,2)).toUpperCase() + "'";
      scriptLogger.debug("Script triggered on beforeSave_verify : Execution de la requete [ " + request + "]");
      rsSet = dbStatement.executeQuery(request);
      if(rsSet != null) 
      {
        while(rsSet.next()) {
          String secretLevelString = rsSet.getString("secret");
          scriptLogger.debug("Script triggered on beforeSave_verify : Valeurs trouvees [" + secretLevelString + "]");
          if (StringUtils.isNotBlank(secretLevelString)){
            secretLevel = Integer.valueOf(secretLevelString);
          }
        }
        rsSet.close();
      }
      else
      {
        scriptLogger.debug("Script triggered on beforeSave_verify : Il n'y a pas de correspondance pour : "+ numEng.substring(0,2));
      }
      dbStatement.close();
    }
    catch(DigiInternalException e) {
      // TODO Auto-generated catch block
      scriptLogger.error(e.getMessage());
    }
    catch(SQLException e) {
      // TODO Auto-generated catch block
      scriptLogger.error(e.getMessage());
    }
    scriptLogger.info("GROOVY : freeing pool :"+POOL_NAME_DOSSIER);
    connectManager.freeConnection(POOL_NAME_DOSSIER, dbConnection);
  }
  else {
    secretLevel = 600;
  }
  scriptLogger.debug("Script triggered on beforeSave_verify : FIN de Recuperation des champs [secretLevel]");
  return secretLevel;
}


Integer getSecretlevelAssistant(Integer idAssistant, Logger logger) throws DigiInternalException {
  Logger scriptLogger = logger;
  
  Integer secretLevel = 600;
  
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

