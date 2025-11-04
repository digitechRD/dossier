import java.util.*

import javax.faces.component.UIComponent

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.common.model.backing.AbstractFormLocutionModel
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException


import com.digitech.jcorbairs.exception.DocumentException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.common.framework.bdd.DBConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType;

import com.digitech.jcorbairs.Term;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.jcorbairs.User;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.NavigationUtils;

import static ScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IField updatedField le champ de référence pour la dépendance
// IField fieldToUpdate le champ à mettre a jour
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IField theFieldToUpdate = fieldToUpdate;
IField theUpdatedField = updatedField;

logger.debug("Script field dependency: facture_dependency_CDP.groovy --- Start");


String  valueFieldCodeChant;
String  valueFieldCDPOld;
String  valueFieldCDPNew;
String  valueFieldCode;

try {
  // On récupère le code chantier
  valueFieldCodeChant = theUpdatedField.getValue();
  valueFieldCDPOld  = theFieldToUpdate.getValue();
  valueFieldCode  = theFieldToUpdate.getCode();
  
  
   logger.debug("Script field dependency: facture_dependency_CDP.groovy --- valueFieldCode:"+valueFieldCode+"   valueFieldCodeChant:"+valueFieldCodeChant+"   valueFieldCDPOld:"+valueFieldCDPOld);
  
   if(valueFieldCDPOld==null || valueFieldCDPOld.length()<=0 || valueFieldCDPOld.equals("null") ) {
     valueFieldCDPNew = FindInfoCDPFromChantier( valueFieldCodeChant) 
     logger.debug("Script field dependency: facture_dependency_CDP.groovy --- valueFieldCDPNew:"+valueFieldCDPNew);
     
     if(valueFieldCDPNew!=null && valueFieldCDPNew.length()>0 && !valueFieldCDPNew.equals("null") ) {
  
       logger.debug("Script field dependency: facture_dependency_CDP.groovy --- ecriture du champ :"+valueFieldCDPNew);

       theFieldToUpdate.setValue(Integer.valueOf(valueFieldCDPNew));
       ((UIComponent)theFieldToUpdate.getComponent()).getAttributes().put(AbstractFormLocutionModel.KEY_ENABLE_EFFECT, Boolean.TRUE);
     }
     
   }
/*  
  IField CodeChantField = usrContext.getCurrentDocument().getField("FACT_CODE_CHANT1");
  if (CodeChantField != null) {
  
  		valueFieldCodeChant = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_CHRONO"));
  		
        String  val = getNewValue( airsDocument, "FACT_USR_CDP"+strNr, "");
      if(val.length()<=0){        
        // cas pas de valeur 
				scriptLogger.debug(nomFichier+" Le champ code chantier 1 est présant alors qu'il n'y a pas de chef de projet!");
				scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
				airsDocument.getField(fieldCode).setValue(oldValue);
  			msgReturn = "Le champ code chantier <"+newValue + "> est présant alors qu'il n'y a pas de chef de projet!";
  			return msgReturn;  
			}
			
			
			
    Term term = CourrierScriptUtils.getAuthorityListService().getTerm((Integer) courrierTypeField.getValue());
    if (term != null) {
      com.digitech.dossier.common.model.backend.params.CourrierType courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(), term.getCode(), FlowType.IN);
      if (courrierType != null) {
        String rule = courrierType.getDueDateComputingRule();
        Date dueDate = CourrierScriptUtils.computeDueDate(usrContext.getCurrentDocument(),rule);
        theFieldToUpdate.setValue(dueDate);
        ((UIComponent)theFieldToUpdate.getComponent()).getAttributes().put(AbstractFormLocutionModel.KEY_ENABLE_EFFECT, Boolean.TRUE);
      }
    }
    
  }
*/  
} catch (IdentificationException e) {
  logger.error(e.getMessage(),e);
} catch (ServerException e) {
  logger.error(e.getMessage(),e);
}

logger.debug("Script field dependency: facture_dependency_CDP.groovy --- End");




private IUser getUserMgr() {
	return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }




private String FindInfoCDPFromChantier( String Chantier) {
	
  Connection  dbConnection  = null;
	Statement   dbStatement   = null;
	ResultSet   rsSet         = null;
	String POOL_NAME = "GEO_BASE_REF_CHANT";
	String nomFichier="facture_dependency_CDP.groovy : ";
	
	String ResultCode="";
	String ResultNom="";
	
	boolean ret= false; 
	String CDG="";
	String CDP="";
  
  try {	
			
			scriptLogger.debug(nomFichier+" Recherche info pour le code chantier: "+Chantier);
			DBConnectionManager connectManager = DBConnectionManager.getInstance();
			
			//Connexion à la base de données
			if(connectManager.getPool(POOL_NAME) == null)
			{
				connectManager.release();
				connectManager.loadDriver(ScriptUtils.getConstant("ORACLE_AC_DRIVER_NAME"));
				connectManager.addPool(POOL_NAME, ScriptUtils.getConstant("ORACLE_AC_JDBC_CONNECTION_STRING"), ScriptUtils.getConstant("ORACLE_AC_USER"), ScriptUtils.getConstant("ORACLE_AC_PWD"), Integer.parseInt(ScriptUtils.getConstant("ORACLE_AC_MAX_THREAD")), ScriptUtils.getConstant("ORACLE_AC_SEQ"));
			}	

			dbConnection = connectManager.getConnection(POOL_NAME);
    	scriptLogger.debug(nomFichier+" Pool BD OK");
			
			//Vérification de l'existence dans la base PS
			dbStatement = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
	
			String request;
			
			//Requête pour vérifier l'existence au sein de la base PS
 		  if(ScriptUtils.getConstant("SCRIPT_DIGI").equals( "YES")) {
  				request="select CDP from A_REF_CHANTIER WHERE id_chant='"+Chantier+"'";
      }
      else {
  				request="select CDP from A_REF_CHANTIER WHERE id_chant='"+Chantier+"'";
			}
			scriptLogger.debug(nomFichier+" Requête vers la base PS : "+request);
			
			//Execution de la requête
			rsSet = dbStatement.executeQuery(request);
			boolean valueIsInPS = false;
			if(rsSet != null) {
				//On a trouvé au moins 1 résultat
				rsSet.last();
				if (rsSet.getRow() > 0) {
					scriptLogger.debug(nomFichier+" La requete a renvoyee 1 resultat ResultNom:"+rsSet.getString(1));
	        ResultNom=rsSet.getString(1);					
					// chercher le code correspondant au NOM pour le mettre dans la valeur !!!!!
					if(ResultNom!=null && ResultNom.length()>0) {
            List<User> users = new ArrayList<User>();
            List<User> allUsers = getUserMgr().getUsers();
            for (User user : allUsers){
              user.getName()
              if ( user.getName().equals(ResultNom )){
                 ResultCode = user.getId().toString();
                 break;
              }
            }
          }   				
					scriptLogger.debug(nomFichier+" La requete a renvoyée 1 résultat ResultCode:"+ResultCode );
				}
				else {
					scriptLogger.debug(nomFichier+" Le chantier = "+Chantier+" n'existe pas au sein de la base Ref!");
				}
				rsSet.close();
			}
			dbStatement.close();	
      
      connectManager.freeConnection(POOL_NAME, dbConnection);		
		}
		catch(DigiInternalException e) {
			
			scriptLogger.debug(nomFichier+" DigiInternalException : "+e.getMessage());
      return ResultCode;				
		}
		catch(SQLException e) {
			
			scriptLogger.debug(nomFichier+" SQLException : "+e.getMessage());
      return ResultCode;				
		}
    return ResultCode;				
}



