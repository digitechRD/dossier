import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;

import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.jcorbairs.Term;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.ServiceConstants;

import org.apache.commons.lang.StringUtils;

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

FAC_TYPE_DOC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_TYPE_DOC_FIELD_CODE");
ENG_NUM_FIELD_CODE = LaProvenceScriptUtils.getConstant("ENG_NUM_FIELD_CODE");
AL_FAC_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_FIELD_CODE");
AL_FAC_DUP_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_DUP_FIELD_CODE");
AL_FAC_CONTRAT_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_CONTRAT_FIELD_CODE");

CONNECT_BDD_AIRS_DOSSIER_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_LOGIN");
CONNECT_BDD_AIRS_DOSSIER_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_MDP");
CONNECT_BDD_AIRS_DOSSIER_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_SERVEUR");

POOL_NAME_DOSSIER=LaProvenceScriptUtils.getConstant("POOL_NAME_DOSSIER");
 
// define messages
String FACTURE_CREATION_NON_AUTORISE = "groovy_en_tete_action_non_autorise";
String FACTURE_CREATION_KO = "groovy_error_creation_interdite";

String FACTURE_CREATION_NUM_ENG_KO = "groovy_modification_engnum_ko";

log.debug("Script triggered on before save : beforeCreate_verifyTypeDoc.groovy --- Start");

// Generates the final chrono number
boolean isFac = false;
String facTypeDoc = theDocument.getField(FAC_TYPE_DOC_FIELD_CODE).getValue();
if (String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_FIELD_CODE)).compareTo(facTypeDoc) == 0 ||
String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_DUP_FIELD_CODE)).compareTo(facTypeDoc) == 0){
  isFac = true;
}
if (isFac){
  ScriptResultValueChecker result = new ScriptResultValueChecker();
  result.setValid(false);
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
  result.setMessageSummary(FACTURE_CREATION_NON_AUTORISE);
  result.setMessageDetail(FACTURE_CREATION_KO);
  output.setValue(result);
} else {
  // ajouter la vérification du numéro engagement
  if (String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_CONTRAT_FIELD_CODE)).compareTo(facTypeDoc) == 0){
    // On récupère le ENG_NUM
    String engNum = theDocument.getField(ENG_NUM_FIELD_CODE).getValue();
    // On vérifie que le numéro d'engagement est au bon format pour les factures de type CONTRAT : XXCONTRAT
    Integer secretLvl = verifyEngNum(engNum);
    if (secretLvl == 999){
      ScriptResultValueChecker result = new ScriptResultValueChecker();
      result.setValid(false);
      result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN );
      result.setMessageSummary(FACTURE_CREATION_NUM_ENG_KO);
      result.setMessageDetail(FACTURE_CREATION_NON_AUTORISE);
      output.setValue(result);
    } else {
      theDocument.getAirsDocument().setSecretLevel(secretLvl);
      // On met à jour le document
      com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
      documentMgr.updateDocument(usrContext, theDocument, false);
      ScriptResultValueChecker result = new ScriptResultValueChecker();
      result.setValid(true);
      output.setValue(result)
    }
  } else {
    ScriptResultValueChecker result = new ScriptResultValueChecker();
    result.setValid(true);
    output.setValue(result)
  }
}

log.debug("Script triggered on before save: beforeCreate_verifyTypeDoc.groovy --- End");

Integer verifyEngNum(String engNum){
  if (engNum != null && engNum.size() == 9){
    if (engNum.endsWith("CONTRAT")){
      Integer secretLevel = getSecretLevel(engNum);
      if (secretLevel != 999) return secretLevel;
    }
  }
  return 999;
}

// Recuperation du secret level
public Integer getSecretLevel(String numEng){
  scriptLogger.debug("Script triggered on beforeSave_verify : DEBUT Recuperation du secretLevel");
  Integer secretLevel = 999;
  if (StringUtils.isNotBlank(numEng)){

    Connection  dbConnection       = null;
    Statement   dbStatement        = null;
    ResultSet   rsSet              = null;
    
    DBConnectionManager connectManager = DBConnectionManager.getInstance();

    // On vérifie que le pool de connexion n'existe pas déja
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
          scriptLogger.debug("Script triggered on beforeSave_verify : Valeurs trouvees [" + secretLevel + "]");
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
    secretLevel = 999;
    
  }
  scriptLogger.debug("Script triggered on beforeSave_verify : FIN de Recuperation des champs [secretLevel]");
  return secretLevel;
}

