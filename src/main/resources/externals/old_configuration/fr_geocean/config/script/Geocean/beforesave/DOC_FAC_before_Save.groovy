/** Fichier : OnCtrlFiled_before_SaveDoc.groovy
* 	Auteur  : JMU
* 	Date 	: 06/12/12
* 	But     : 1) Parcours tous les champ du document avant sa mise à jour
*			  2) Vérifie l'existence du numéro PS, numéro de marché et numéro de bon de commande au sein de la base PS
			  3) Historise le changement de valeur pour chaque champ modifié
			  4) Met à jour le champ raison sociale en focntion du nouveau numéro PS
			  
			  TODO:  1) meilleure gestion des erreurs
*/
import java.util.List;     
import java.util.Map;
import java.lang.Double;

import org.apache.commons.lang.StringUtils;
 
import org.slf4j.Logger;
import com.digitech.dossier.script.model.IScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;

import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.Utils;

import com.digitech.airs3dossiers.airs.AirsFolder;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.Constants;

import com.digitech.jcorbairs.exception.XmlException; 
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
String nomFichier="DOC_FAC_before_Save.groovy : ";
scriptLogger.debug(nomFichier+" Début");

ScriptResultValueChecker result = new ScriptResultValueChecker();
//Pour afficher les bandeaux d'erreur si besoin
//ScriptResultValueChecker result = output.getValue();

//Le document mis à jour
IDocument airsDocument = (IDocument)airsDocument;

//Parcours de tous les champs du document
scriptLogger.debug(nomFichier+" Parcours de tous les champs du document : Début");
for(String fieldCode : airsDocument.getFieldMap().keySet()) {
	
	String oldValue;
	String newValue;
	String strMessage="";
	
	if( fieldCode.equals("D_MODIF") || fieldCode.equals("D_CREAT") || fieldCode.equals("FACT_RETOUR") || fieldCode.equals("FACT_CDPVALIDE") || ( fieldCode.length()>10 && fieldCode.substring(0,9).equals("FACT_VAL_"))) {
	   continue;
	}
	scriptLogger.debug(nomFichier+" Traitement pour le champ "+fieldCode);
	//Récupération de l'ancienne valeur du champ
	oldValue = getOldValue(airsDocument, fieldCode, "FIELD_TYPE");
	//Récupération de la nouvelle valeur du champ
	newValue = getNewValue(airsDocument, fieldCode, "FIELD_TYPE");
	//On vérifie si la valeur du champ a été modifiée
	
	if( fieldCode.equals("FACT_DATE") || ( fieldCode.length()>9 && fieldCode.substring(0,8).equals("FACT_DT_"))) {
	   if(oldValue!=null && oldValue.length()>10 ) {     oldValue = oldValue.substring(0,10);}
	   if(newValue!=null && newValue.length()>10 ) {     newValue = newValue.substring(0,10);}
  	 scriptLogger.debug(nomFichier+" # Decoupe date  oldValue:"+oldValue+" newValue:"+newValue);
	}
	if( fieldCode.equals("FACT_TTC")) {
  	 scriptLogger.debug(nomFichier+" # Decoupe TTC  AV oldValue:"+oldValue+" newValue:"+newValue);
	   if(oldValue!=null && oldValue.length()>1 ) {     oldValue = Float.parseFloat(oldValue).toString();}
	   if(newValue!=null && newValue.length()>1 ) {     newValue = Float.parseFloat(newValue).toString();}
  	 scriptLogger.debug(nomFichier+" # Decoupe TTC  AP oldValue:"+oldValue+" newValue:"+newValue);
	}
	scriptLogger.debug(nomFichier+" Nouvelle valeur : "+newValue+"    Ancienne valeur : "+oldValue);
	
	if(newValue != null && !oldValue.equalsIgnoreCase(newValue)) {
		scriptLogger.debug(nomFichier+"Le champ "+fieldCode+" a été modifié");
		//Contrôle de cohérence (pour le moment pour FOUR_NUM_PS, FAC_NUM_MARCHE et FAC_NUM_BON_COM)
		strMessage = checkCoherenceForField(airsDocument, userContext, fieldCode, "FIELD_TYPE", oldValue, newValue); 
		if(strMessage.length()<=0) {
		  // ecrire la valeur
		  airsDocument.getField(fieldCode).setValue(newValue);
		
		  // cohérence OK ajouter les log et histo
			//Conversion des id
			if(airsDocument.getField(fieldCode).getReferenceType()!=0) {
				if(newValue.equals("")) {					newValue = "-1";				}
				if(oldValue.equals("")) {					oldValue = "-1";				}
				newValue = ScriptUtils.getListItemValueFromId(Integer.parseInt(newValue), airsDocument.getField(fieldCode).getReferenceType());
				oldValue = ScriptUtils.getListItemValueFromId(Integer.parseInt(oldValue), airsDocument.getField(fieldCode).getReferenceType());
			}
			//Modification de l'historique du document
			scriptLogger.debug(nomFichier+"Contrôle de cohérence OK pour le champ "+fieldCode+". Ajout de l'historique");

			ScriptUtils.addHistoForField(airsDocument, userContext, fieldCode, "FIELD_TYPE", oldValue, newValue);
			result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
			result.setMessageSummary("Mise à jour du document Réussie");
			result.setMessageDetail("La mise à jour du document a été effectuée avec succès");
      //Save the document   KO
      //DocumentUtils.saveDocument(airsDocument);			
			//scriptLogger.debug(nomFichier+"  ### Document Save ###");
		}
		else {
			result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
			result.setMessageSummary("Mise à jour du document échouée");
			result.setMessageDetail("Msg Erreur : "+strMessage);
		}
	}
} 


scriptLogger.debug(nomFichier+" Parcours de tous les champs du document : Fin");
result.setValid(true);   
output.setValue(result);

//Utils.getCustomActionController().getModel().setOutcome(Utils.getNavigationController().gotoViewUnit());
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, airsDocument));
	
scriptLogger.debug(nomFichier+" Fin");






public class CleStrings {
   private String str1;
   private String str2;
   
   public CleStrings(String val1,String val2) {
      this.str1 = val1;
      this.str2 = val2;
   }
   public String getStr1() {       return str1;   }
   public void setStr1(String val) {     this.str1 = val;   }
   public String getStr2() {       return str2;   }
   public void setStr2(String val) {     this.str2 = val;   }
   public String toString() {       return str1+":"+str2;   }
}
	
/**
 * checkCoherenceForField : Contrôle de cohérence pour FOUR_NUM_PS(existence PS+MAJ FOUR_NOM), FAC_NUM_MARCHE(existence PS) et FAC_NUM_BON_COM(existence PS)
 */
private String checkCoherenceForField(IDocument airsDocument, UserCoreContext userContext, String fieldCode, String fieldType, String oldValue, String newValue) {

	String nomFichier="DOC_FAC_before_Save.groovy : ";
	
	String msgReturn=""; //"" = OK
	//cohérence :  

  //***** si code Tiers changer => Vérivier et mise a jours du nom tier ****  KO pour intérogation BASE SqlServeur       
  /*
	if (fieldCode.equals("FOUR_MATRIC") && (newValue.length()>0) )  {
  	
	  CleStrings  NomFour = new CleStrings("","");
	  if((FindNameTiers(airsDocument,userContext,newValue,NomFour))==true) {
      // mettre ajour les champs  nom four
			scriptLogger.debug(nomFichier+" Changement du nom fournisseur en :"+NomFour.getStr1());
			airsDocument.getField("FOUR_NOM").setValue(NomFour.getStr1());
			ScriptUtils.addHistoForField(airsDocument, userContext, "FOUR_NOM", "FIELD_TYPE", "", NomFour.getStr1());
  		return true
    }
    else {
      // erreur code Trier inconnu
			scriptLogger.debug(nomFichier+" Le code Tiers <"+newValue + "> est inconnu du référentiel!");
			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
			airsDocument.getField(fieldCode).setValue(oldValue);
			return false         
    }
  }  
  */

  //**** si code chantier 1 modifié et passé à vide !!! erreur        C
	if ( fieldCode.equals("FACT_CODE_CHANT1")&& (newValue.length()<=0)) {
      // erreur code chantier 1 obligatoire           C
			scriptLogger.debug(nomFichier+" Le code chantier 1  est obligatoire, il ne peut être vide!");
			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
			airsDocument.getField(fieldCode).setValue(oldValue);
			msgReturn = "Le code chantier 1  est obligatoire, il ne doit pas être vide!";
			return msgReturn;  	
	}
  
  //**** si CDP 1 modifié et passé à vide !!! erreur        C
	if ( fieldCode.equals("FACT_USR_CDP1")&& (newValue.length()<=0)) {
      // erreur code chantier 1 obligatoire           C
			scriptLogger.debug(nomFichier+" Le code Chef de projet 1  est obligatoire, il ne peut être vide!");
			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
			airsDocument.getField(fieldCode).setValue(oldValue);
			msgReturn = "Le code Chef de projet 1  est obligatoire, il ne doit pas être vide!";
			return msgReturn;  	
	}
  
  //***** si code chantier sans cdp => mise a jours auto par intéro base ref  ou erreur ****         A
	if ( fieldCode.length()==16 && fieldCode.substring(0,15).equals("FACT_CODE_CHANT")&& (newValue.length()>0)) {

      String strNr=  fieldCode.substring(15,16);
	
	    // recherche de doublon sur les autre NrCHantier
	    int i=0;
	    for(i=1;i<4;i++) {
        if(strNr.equals(i.toString()))
          continue;
  
        String  val2 = getNewValue( airsDocument, "FACT_CODE_CHANT"+i.toString(), "");
        if(newValue.equals(val2)){
          //erreur doublon
          // erreur code chantier inconnu   B
    			scriptLogger.debug(nomFichier+" Le code chantier <"+newValue + "> existe déjà pour cette facture!");
    			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
    			airsDocument.getField(fieldCode).setValue(oldValue);
    			msgReturn = "Doublon Le code chantier <"+newValue + "> existe déjà pour cette facture!";
    			return msgReturn;  	
        }
      }  // end for
	     
	    // recherche si un chef de projet est en place, si non on le positionne par le REF
      String  val = getNewValue( airsDocument, "FACT_USR_CDP"+strNr, "");
      if(val.length()<=0){        
        // cas pas de valeur 
				scriptLogger.debug(nomFichier+" Le champ code chantier 1 est présant alors qu'il n'y a pas de chef de projet!");
				scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
				airsDocument.getField(fieldCode).setValue(oldValue);
  			msgReturn = "Le champ code chantier <"+newValue + "> est présant alors qu'il n'y a pas de chef de projet!";
  			return msgReturn;  
			}

  	  CleStrings  CDx = new CleStrings("","");
      String CDG,CDP;
		  if((FindInfoFromChantier(airsDocument,userContext,newValue,CDx))==false) { 
        // erreur code chantier inconnu   
  			scriptLogger.debug(nomFichier+" Le code chantier <"+newValue + "> est inconnu du référentiel!");
  			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
  			airsDocument.getField(fieldCode).setValue(oldValue);
  			msgReturn = "Le code chantier <"+newValue + "> est inconnu du référentiel!";
  			return msgReturn;  	
      }
			
				// ****** + controle ref ****************
        // cas modification
        /*
    	  CleStrings  CDx = new CleStrings("","");
        String CDG,CDP;
			  if((FindInfoFromChantier(airsDocument,userContext,newValue,CDx))==true) { 
           CDG = CDx.getStr1();
           CDP = CDx.getStr2();
          // mettre ajour les champs
    			scriptLogger.debug(nomFichier+" Changement du CDG"+strNr+" en :"+CDG+" et du CDP1 en :"+CDP);
    			// position d'un CDP
    			if((CDP!=null) && CDP.length()>0 ) {
      			airsDocument.getField("FACT_USR_CDP"+strNr).setValue(CDP);
      			ScriptUtils.addHistoForField(airsDocument, userContext, "FACT_USR_CDP"+strNr, "FIELD_TYPE", "", CDP);
      			scriptLogger.debug(nomFichier+" Ecriture du champ FACT_USR_CDP"+strNr+" avec :"+CDP);
      			// PB de sauvegarde
          }
    			// position d'un CDG
    			if((CDG!=null) && CDG.length()>0 ) {
      			airsDocument.getField("FACT_USR_CTRLGEST"+strNr).setValue(CDG);
      			ScriptUtils.addHistoForField(airsDocument, userContext, "FACT_USR_CTRLGEST"+strNr, "FIELD_TYPE", "", CDG);
      			scriptLogger.debug(nomFichier+" Ecriture du champ FACT_USR_CTRLGEST"+strNr+" avec :"+CDG);
      			// PB de sauvegarde
      		}
      		return msgReturn;
        }
        else {
          // erreur code chantier inconnu   B
    			scriptLogger.debug(nomFichier+" Le code chantier <"+newValue + "> est inconnu du référentiel!");
    			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
    			airsDocument.getField(fieldCode).setValue(oldValue);
    			msgReturn = "Le code chantier <"+newValue + "> est inconnu du référentiel!";
    			return msgReturn;  	
        }
        
				return msgReturn;
      } 
			*/
  }  
  
  

  //******************  si cdp sans code chantier  => erreur  ***************************
//	if (fieldCode.equals("FACT_USR_CDP1") && (newValue.length()>0) ) 
	if ( fieldCode.length()==13 && fieldCode.substring(0,12).equals("FACT_USR_CDP")&& (newValue.length()>0)) {
      String strNr=  fieldCode.substring(12,13);
      String  val = getNewValue( airsDocument, "FACT_CODE_CHANT"+strNr, "");
      if(val.length()<=0){        //erreur
					scriptLogger.debug(nomFichier+" Le champ Chef de projet "+strNr+" est présant alors qu'il n'y a pas de code chantier!");
					scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
					airsDocument.getField(fieldCode).setValue(oldValue);
    			msgReturn = "Le champ Chef de projet "+strNr+" est présant alors qu'il n'y a pas de code chantier!";
    			return msgReturn;  	
      } 
      
      
	    // recherche de doublon sur les autre CDP
	    int i=0;
	    for(i=1;i<4;i++) {
        if(strNr.equals(i.toString()))
          continue;
  
        String  val2 = getNewValue( airsDocument, "FACT_USR_CDP"+i.toString(), "");
        if(newValue.equals(val2)){
          //erreur doublon
          // erreur code chantier inconnu   B
    			scriptLogger.debug(nomFichier+" Le Chef de projet <"+newValue + "> existe déjà pour cette facture!");
    			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
    			airsDocument.getField(fieldCode).setValue(oldValue);
    			msgReturn = "Le Chef de projet <"+newValue + "> existe déjà pour cette facture!";
    			return msgReturn;  	
        }
      }
      
  }  
  
  //***************  si cdg sans code chantier  => erreur  ***************** 
//	if (fieldCode.equals("FACT_USR_CTRLGEST1") && (newValue.length()>0) )  {
	if ( fieldCode.length()==18 && fieldCode.substring(0,17).equals("FACT_USR_CTRLGEST")&& (newValue.length()>0)) {
      String strNr=  fieldCode.substring(17,18);
      
      String  val = getNewValue( airsDocument, "FACT_CODE_CHANT"+strNr, "");
      if(val.length()<=0){        //erreur
					scriptLogger.debug(nomFichier+" Le champ Ctrl Gestion "+strNr+" est présant alors qu'il n'y a pas de code chantier!");
					scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
					airsDocument.getField(fieldCode).setValue(oldValue);
    			msgReturn = "Le champ Ctrl Gestion "+strNr+" est présant alors qu'il n'y a pas de code chantier!";
    			return msgReturn;        
      }

	    // recherche de doublon sur les autre CDG
	    int i=0;
	    for(i=1;i<4;i++) {
        if(strNr.equals(i.toString()))
          continue;
  
        String  val2 = getNewValue( airsDocument, "FACT_USR_CTRLGEST"+i.toString(), "");
        if(newValue.equals(val2)){
          //erreur doublon
          // erreur code chantier inconnu   B
    			scriptLogger.debug(nomFichier+" Le Controleur de gestion <"+newValue + "> existe déjà pour cette facture!");
    			scriptLogger.debug(nomFichier+" On remet l'ancienne valeur : "+oldValue);
    			airsDocument.getField(fieldCode).setValue(oldValue);
    			msgReturn = "Le Controleur de gestion <"+newValue + "> existe déjà pour cette facture!";
    			return msgReturn;  	         
        }
      }
       
  }
	
	return msgReturn;
}
	
/**
* getOldValue : Récupère l'ancienne valeur d'un champ du document
*/
private String getOldValue(IDocument airsDocument, String fieldCode, String fieldType) {
	String nomFichier="DOC_FAC_before_Save.groovy : ";

	com.digitech.jcorbairs.DocumentContent myContents = airsDocument.getAirsDocument().getContents();	

  //Récupération ancienne valeur CODE_CHAMP_FAC_REF
  com.digitech.jcorbairs.Field myFieldFacModified = new com.digitech.jcorbairs.Field(airsDocument.getAirsDocument().getJeton(), fieldCode);
  String oldValue = null;
  try
  {
	  oldValue = myContents.getFieldValue(myFieldFacModified);
  }
  catch(XmlException eXmlExcep)
  {
	  scriptLogger.debug(nomFichier+" XmlException : "+eXmlExcep.getMessage());
	  oldValue = "";
  }

  return oldValue;
}

/**
 * getNewValue : Récupère la nouvelle valeur d'un champ du document
 */
private String getNewValue(IDocument airsDocument, String fieldCode, String fieldType) {
	
	//Récupération nouvelle valeur CODE_CHAMP_FAC_REF
	IField fieldFac_Doc = airsDocument.getField(fieldCode);
	String newValue = getFieldValue( fieldFac_Doc );
	return newValue;

}

private String getFieldValue( IField field ) {
	List<?> values = field.getValues();
	String fieldvalue;
	if(values != null && !values.isEmpty()) {
		fieldvalue =getServerMgr().getFieldValues(values, field.getAirsField());   
		
	}else
	{
		scriptLogger.info("Le champ est nul ou vide");
	}
	
	return fieldvalue;
}

private IServer getServerMgr() {
	return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.impl.AuditMgr getAuditMgr() {
	return (com.digitech.dossier.common.service.impl.AuditMgr) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
}


private IUser getUserMgr() {
	return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }


private boolean FindInfoFromChantier(IDocument airsDocument, UserCoreContext userContext, String Chantier, CleStrings CDx) {
	
  Connection  dbConnection  = null;
	Statement   dbStatement   = null;
	ResultSet   rsSet         = null;
	String POOL_NAME = "GEO_BASE_REF_CHANT";
	String nomFichier="DOC_FAC_before_Save.groovy-REQ_Chant : ";
	
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
  				request="select CDG,CDP from A_REF_CHANTIER WHERE id_chant='"+Chantier+"'";
      }
      else {
  				request="select CDG,CDP from A_REF_CHANTIER WHERE id_chant='"+Chantier+"'";
			}
			scriptLogger.debug(nomFichier+" Requête vers la base PS : "+request);
			
			//Execution de la requête
			rsSet = dbStatement.executeQuery(request);
			boolean valueIsInPS = false;
			if(rsSet != null) {
				//On a trouvé au moins 1 résultat
				rsSet.last();
				if (rsSet.getRow() > 0) {
					scriptLogger.debug(nomFichier+" La requête a renvoyée 1 résultat :"+rsSet.getString(1)+" et "+rsSet.getString(2));
					//CDx.setStr1(rsSet.getString(1));
					//CDx.setStr2(rsSet.getString(2));
	        CDG=rsSet.getString(1);
	        CDP=rsSet.getString(2);					
					// chercher le code correspondant au NOM pour le mettre dans la valeur !!!!!
          List<User> users = new ArrayList<User>();
          List<User> allUsers = getUserMgr().getUsers();
					if(CDG!=null && CDG.length()>0) {
            for (User user : allUsers){
              user.getName()
              if ( user.getName().equals(CDG )){
                 CDx.setStr1( user.getId().toString());
              }
            }
          }   				
					if(CDP!=null && CDP.length()>0) {
            for (User user : allUsers){
              user.getName()
              if ( user.getName().equals(CDP )){
                 CDx.setStr2( user.getId().toString());
              }
            }
          }   				
					scriptLogger.debug(nomFichier+" La requête a renvoyée 1 résultat :"+CDx.getStr1() +" et "+CDx.getStr2());
					ret= true;
				}
				else {
					scriptLogger.debug(nomFichier+" Le chantier = "+Chantier+" n'existe pas au sein de la base Ref!");
					ret= false;
				}
				rsSet.close();
			}
			dbStatement.close();	
      
      connectManager.freeConnection(POOL_NAME, dbConnection);		
		}
		catch(DigiInternalException e) {
			
			scriptLogger.debug(nomFichier+" DigiInternalException : "+e.getMessage());
      return false;				
		}
		catch(SQLException e) {
			
			scriptLogger.debug(nomFichier+" SQLException : "+e.getMessage());
      return false;				
		}
    return ret;				
}

private boolean FindNameTiers(IDocument airsDocument, UserCoreContext userContext, String CodeTier, CleStrings NameTier) {
	
  Connection  dbConnection  = null;
	Statement   dbStatement   = null;
	ResultSet   rsSet         = null;
	String POOL_NAME = "GEO_BASE_REF_TIERS";
	String nomFichier="DOC_FAC_before_Save.groovy-REQ_Tiers : ";
	
	boolean ret= false; 

  
  try {	
			
			scriptLogger.debug(nomFichier+" Recherche info pour le code Tiers: "+CodeTier);
			DBConnectionManager connectManager = DBConnectionManager.getInstance();
			
			//Connexion à la base de données
			if(connectManager.getPool(POOL_NAME) == null)
			{
				connectManager.release();
				connectManager.loadDriver(ScriptUtils.getConstant("TIERS_DRIVER_NAME"));
				connectManager.addPool(POOL_NAME, ScriptUtils.getConstant("TIERS_JDBC_CONNECTION_STRING"), ScriptUtils.getConstant("TIERS_USER"), ScriptUtils.getConstant("TIERS_PWD"), Integer.parseInt(ScriptUtils.getConstant("TIERS_MAX_THREAD")), ScriptUtils.getConstant("TIERS_SEQ"));
			}	

			dbConnection = connectManager.getConnection(POOL_NAME);
    	scriptLogger.debug(nomFichier+" Pool BD OK");
			
			//Vérification de l'existence dans la base PS
			dbStatement = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
	
			String request;
			
			//Requête pour vérifier l'existence au sein de la base PS
 		  if(ScriptUtils.getConstant("SCRIPT_DIGI").equals( "YES")) {
  				request="select NOM from A_REF_TIERS WHERE id_tiers='"+CodeTier+"'";
      }
      else {
  				request="select NOM from A_REF_TIERS WHERE id_tiers='"+CodeTier+"'";
			}
			scriptLogger.debug(nomFichier+" Requête vers la base PS : "+request);
			
			//Execution de la requête
			rsSet = dbStatement.executeQuery(request);
			boolean valueIsInPS = false;
			if(rsSet != null) {
				//On a trouvé au moins 1 résultat
				rsSet.last();
				if (rsSet.getRow() > 0) {
					scriptLogger.debug(nomFichier+" La requête a renvoyée 1 résultat : "+rsSet.getString(1));
					NameTier.setStr1( rsSet.getString(1));
					ret= true;
				}
				else {
					scriptLogger.debug(nomFichier+" Le CodeTier = "+CodeTier+" n'existe pas au sein de la base Ref Tiers!");
					ret= false;
				}
				rsSet.close();
			}
			dbStatement.close();	
      
      connectManager.freeConnection(POOL_NAME, dbConnection);		
		}
		catch(DigiInternalException e) {
			
			scriptLogger.debug(nomFichier+" DigiInternalException : "+e.getMessage());
      return false;				
		}
		catch(SQLException e) {
			
			scriptLogger.debug(nomFichier+" SQLException : "+e.getMessage());
      return false;				
		}
    return ret;				
}


/**
* @return IAuthorityList the Authority List
*/
public IAuthorityList getAuthorityListService() {
 return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
}
