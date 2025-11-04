import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.impl.Document.Comment;
import com.digitech.dossier.common.utils.MessageUtils;

//Java imports
import java.util.Map;
import java.security.InvalidParameterException;
import javax.mail.MessagingException
import javax.xml.bind.JAXBException
import java.util.ArrayList
import java.util.Hashtable
import java.util.Arrays


//Other imports
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.commons.mail.EmailException
import org.xml.sax.SAXException

//Digitech imports
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.ILocutionModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.ITask
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.Constants
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.IUser
import freemarker.template.TemplateException
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.Term
import com.digitech.dossier.common.exception.InvalidConfigurationException
import com.digitech.dossier.common.model.backend.airs.impl.LocutionModel
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.dossier.workflow.service.IWFSearchService
import com.digitech.dossier.workflow.model.IWFTaskModel
import com.digitech.dossier.workflow.model.impl.WFTask

import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.common.framework.bdd.DBConnectionManager;

import static LaProvenceScriptUtils;

/**
 * Author : JMU
 * Date : 22/06/12
 * Description : Ce script permet de mettre à jour l'environnement CIPRES pour les factures à payer. Lancé par le taskManager.
 * IN : scriptLogger/
 *
 */


//Constantes
String FAC_ETAT_FIELD_CODE = "FAC_ETAT";
String SOCIETE_FIELD_CODE = "SOC";
String NUM_FAC_FIELD_CODE = "FAC_NUM";  
String MATRICULE_FIELD_CODE = "FOUR_MAT";
String DATE_FACTURE_FIELD_CODE = "FAC_DATE";
String ETAT_BONAPAYER = "BON_A_PAY";
Integer ETAT_BONAPAYER_ID = 115;
Integer ETAT_REGLEE_ID = 120;
String CONTENT_TYPE = "D_DOC_FAC";

//MODIFIER L'ID
Integer ETAT_ATTENTE_REGLEMENT_ID=606;


POOL_NAME_CIPRES=LaProvenceScriptUtils.getConstant("POOL_NAME_CIPRES");
CONNECT_BDD_CIPRES_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_CIPRES_SERVEUR");
CONNECT_BDD_CIPRES_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_CIPRES_LOGIN");
CONNECT_BDD_CIPRES_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_CIPRES_MDP");

Logger log = scriptLogger;
log.info("MAJCIPRES : IN");

//récupération de toutes les factures dans l'état Bon A payer
ILocutionModel locutionModel = new LocutionModel();
DocumentUtils.buildLocutionModel(locutionModel, FAC_ETAT_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, String.valueOf(ETAT_BONAPAYER_ID));
log.info("Lancement de la requete de recherche des facture bon a payer");
List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList(CONTENT_TYPE), null);

if(documentList!=null) {
  log.info(documentList.size()+" facture(s) sont dans l'etat : "+ETAT_BONAPAYER);
  com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
  UserCoreContext userContext = UserUtils.getAdminUserContext();
  
  //On parcourt toutes les factures
  for(IDocument facture : documentList) {
    //Récupération du Doc_Id
    Integer docId =  facture.getAirsRefId();
    log.info("DocID="+String.valueOf(docId));

    //récupération du code_societe et num_fac
    
    String sCodeSoc = null;
    String sIdSoc = facture.getField(SOCIETE_FIELD_CODE).getValue();
    
    if ( sIdSoc != null )
    {
      sCodeSoc = LaProvenceScriptUtils.getTermCode(SOCIETE_FIELD_CODE, Integer.valueOf( sIdSoc ));
      log.info("sCodeSoc="+sCodeSoc);
    }

    String sNumFac = facture.getField(NUM_FAC_FIELD_CODE).getValue();
    log.info("sNumFac="+sNumFac);

    String sMatricule = facture.getField(MATRICULE_FIELD_CODE).getValue();
    log.info("sMatricule="+sMatricule);
    
    Object maDate = facture.getField(DATE_FACTURE_FIELD_CODE).getValue();
    
    String sDateFacture = null;
    
    if (maDate != null)
    {
      sDateFacture = getDateAsString(maDate);
    }
    log.info("sDateFacture="+sDateFacture);
    
    if(  sCodeSoc == null || sNumFac == null || sMatricule == null || sDateFacture == null)
    {
      //erreur, les informations de la facture sont erronnées
      log.error("Les données de la facture sont erronées. Code Société : " + sCodeSoc + " Num Facture : " + sNumFac + " Matricule : "+sMatricule + "sDateFacture :"+sDateFacture);
      addcomment(facture, "Verification CIPRES en ereur : Numero de facture ou Code societe ou Matricule ou Date Facture non correct", userContext, documentMgr, log );
    }
    else
    {
       //mise à jour de CIPRES
        boolean bUpdateCIPRES = MAJCIPRES(sCodeSoc,sNumFac, sMatricule, sDateFacture, log);
        if ( bUpdateCIPRES == true )
        {
          //on passe la facture à l'état Réglée dans AIRS DOSSIER
          log.info("MAJCIPRES : la mise à jour CIPRES s'est correctement effectuée, la facture passe dans l'état Attente Reglement dans AIRS DOSSIER");
          facture.getField(FAC_ETAT_FIELD_CODE).setValue(ETAT_ATTENTE_REGLEMENT_ID);
          documentMgr.updateDocument(userContext, facture, false);
        }
        else
        {
          //problème lors de la mise à jour CIPRES
          log.info("MAJCIPRES : Problème lors de la mise à jour de CIPRES : le système n'est pas cohérent. La facture reste dans l'état BonAPayer dans AIRS DOSSIER");
          addcomment(facture, "Verification CIPRES en ereur : le systeme CIPRES n'est pas coherent (nombre d'elements incorrect)", userContext, documentMgr, log );
        }
     }
   }
}
 
 
//récupération de toutes les factures dans l'état Attente Regl
ILocutionModel locutionModel2 = new LocutionModel();
DocumentUtils.buildLocutionModel(locutionModel2, FAC_ETAT_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, String.valueOf(ETAT_ATTENTE_REGLEMENT_ID));
log.info("Lancement de la requete de recherche des facture Attente Regl");
List<IDocument> documentList2 = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel2, DocumentUtils.getSearchContentTypeList(CONTENT_TYPE), null);

if(documentList2!=null) {
  log.info(documentList2.size()+" facture(s) sont dans l'etat : "+ETAT_ATTENTE_REGLEMENT_ID);
  com.digitech.dossier.common.service.IDocument documentMgr2 = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
  UserCoreContext userContext = UserUtils.getAdminUserContext();
  
  //On parcourt toutes les factures
  for(IDocument facture : documentList2) {
    //Récupération du Doc_Id
    Integer docId =  facture.getAirsRefId();
    log.info("DocID="+String.valueOf(docId));

    //récupération du code_societe et num_fac
    String sCodeSoc = null;
    String sIdSoc = facture.getField(SOCIETE_FIELD_CODE).getValue();
    
    if ( sIdSoc != null )
    {
      sCodeSoc = LaProvenceScriptUtils.getTermCode(SOCIETE_FIELD_CODE, Integer.valueOf( sIdSoc ));
      log.info("sCodeSoc="+sCodeSoc);
    }

    String sNumFac = facture.getField(NUM_FAC_FIELD_CODE).getValue();
    log.info("sNumFac="+sNumFac);

    String sMatricule = facture.getField(MATRICULE_FIELD_CODE).getValue();
    log.info("sMatricule="+sMatricule);
    
    Object maDate = facture.getField(DATE_FACTURE_FIELD_CODE).getValue();
    
    String sDateFacture = null;
    
    if (maDate != null)
    {
      sDateFacture = getDateAsString(maDate);
    }
    log.info("sDateFacture="+sDateFacture);


    if(  sCodeSoc == null || sNumFac == null || sMatricule == null || sDateFacture == null)
    {
      //erreur, les informations de la facture sont erronnées
      log.error("Les données de la facture sont erronées. Code Société : " + sCodeSoc + " Num Facture : " + sNumFac + " sMatricule : "+sMatricule + "sDateFacture :"+sDateFacture);
      addcomment(facture, "Vérification facture lettree : Numéro de facture ou Code societe ou Matricule ou Date Facture non correct", userContext, documentMgr2, log );
    }
    else
    {
      boolean bLettree = estLettree(sCodeSoc, sNumFac, sMatricule, sDateFacture, log);
      if ( bLettree == true )
      {
          log.info("MAJCIPRES : La facture a été payée, on la passe dans l'état réglée");
          facture.getField(FAC_ETAT_FIELD_CODE).setValue(ETAT_REGLEE_ID);
          documentMgr2.updateDocument(userContext, facture, false);
      }
    }
   }
} 
 

public boolean estLettree(String sCodeSoc,String sNumFact, String sMatricule, String sDateFacture, Logger log){
  log.info("Script MAJCIPRES : DEBUT estLettree");

  boolean bFind = false;
  if (StringUtils.isNotBlank(sCodeSoc) && StringUtils.isNotBlank(sNumFact) && StringUtils.isNotBlank(sMatricule)&& StringUtils.isNotBlank(sDateFacture) ){

    Connection  dbConnection       = null;
    Statement   dbStatement        = null;
    ResultSet   rsSet              = null;

    DBConnectionManager connectManager = DBConnectionManager.getInstance();

    // On vérifie que le pool dde connexion n'existe pas déja
    if( connectManager.getPool(POOL_NAME_CIPRES) == null ){
      connectManager.release();
      connectManager.loadDriver("oracle.jdbc.driver.OracleDriver");
      connectManager.addPool(POOL_NAME_CIPRES, CONNECT_BDD_CIPRES_SERVEUR, CONNECT_BDD_CIPRES_LOGIN, CONNECT_BDD_CIPRES_MDP, 5, "com.digitech.common.framework.bdd.oracle.SequenceOraImpl");
      log.info("Script MAJCIPRES : adding pool :"+ POOL_NAME_CIPRES);
    }
    String request =null;
    try {
      request = "SELECT distinct mov_invoice FROM mov WHERE cdnatpi='FACT' and etlettr='L' and cdlgsch='FOUTTC' and noseque_pior=(SELECT noseque FROM PIECES WHERE CDJOURN NOT IN ('INJOD', 'CGEXA') AND CDSOCIE='"+sCodeSoc+"' and cdtiers_pr = '"+sMatricule+"' and LBRFPIE='"+sNumFact+"' and to_char(dtpiece,'yyyymmdd')='"+sDateFacture+"')";
      dbConnection = connectManager.getConnection(POOL_NAME_CIPRES);
      dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
     

      log.info("Script MAJCIPRES : Execution de la requete [ " + request + "]");
      rsSet = dbStatement.executeQuery(request);
      if(rsSet != null) {
        while(rsSet.next()) {
          String sMovInvoice = rsSet.getString("mov_invoice");
          bFind = true;
          log.info("Script MAJCIPRES : facture trouvée, lettrée! : "+ sMovInvoice);
       }
       rsSet.close();
     }
     dbStatement.close();
    }
    catch(DigiInternalException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }
    catch(SQLException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }
    connectManager.freeConnection(POOL_NAME_CIPRES, dbConnection);
  }
  log.info("Script MAJCIPRES : FIN estLettree");
  return bFind;
}



public boolean MAJCIPRES(String sCodeSoc,String sNumFact, String sMatricule, String sDateFacture, Logger log){
  log.info("Script MAJCIPRES : DEBUT MAJCIPRES");

  boolean bUpdateOK = false;
  if (StringUtils.isNotBlank(sCodeSoc) && StringUtils.isNotBlank(sNumFact) && StringUtils.isNotBlank(sMatricule) && StringUtils.isNotBlank(sDateFacture)){

    Connection  dbConnection       = null;
    Statement   dbStatement        = null;

    DBConnectionManager connectManager = DBConnectionManager.getInstance();

    // On vérifie que le pool dde connexion n'existe pas déja
    if( connectManager.getPool(POOL_NAME_CIPRES) == null ){
      connectManager.release();
      connectManager.loadDriver("oracle.jdbc.driver.OracleDriver");
      log.info("Script MAJCIPRES : CONNECT_BDD_CIPRES_SERVEUR :"+ CONNECT_BDD_CIPRES_SERVEUR);
      log.info("Script MAJCIPRES : CONNECT_BDD_CIPRES_LOGIN :"+ CONNECT_BDD_CIPRES_LOGIN);
      log.info("Script MAJCIPRES : CONNECT_BDD_CIPRES_MDP :"+ CONNECT_BDD_CIPRES_MDP);
      connectManager.addPool(POOL_NAME_CIPRES, CONNECT_BDD_CIPRES_SERVEUR, CONNECT_BDD_CIPRES_LOGIN, CONNECT_BDD_CIPRES_MDP, 5, "com.digitech.common.framework.bdd.oracle.SequenceOraImpl");
      log.info("Script MAJCIPRES : adding pool :"+ POOL_NAME_CIPRES);
    }

    try {
      dbConnection = connectManager.getConnection(POOL_NAME_CIPRES);
      dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
  
      String requestSelect1 = "SELECT COUNT(*) AS NB FROM PIECES WHERE CDJOURN NOT IN ('INJOD', 'CGEXA') AND CDSOCIE='"+sCodeSoc+"' and LBRFPIE='"+sNumFact+"' and cdtiers_pr='"+sMatricule+"' and to_char(dtpiece,'yyyymmdd')='"+sDateFacture+"'";
      
      String requestSelect2 = "SELECT COUNT(*) AS NB FROM MOV WHERE company='"+sCodeSoc+"' and noseque_pior=(SELECT noseque FROM PIECES WHERE CDJOURN NOT IN ('INJOD', 'CGEXA') AND CDSOCIE='"+sCodeSoc+"' and LBRFPIE='"+sNumFact+"' and cdtiers_pr = '"+sMatricule+"' and to_char(dtpiece,'yyyymmdd')='"+sDateFacture+"')";
      
      log.info("Script MAJCIPRES : Execution de la requete de verif 1 [ " + requestSelect1 + "]");
      log.info("Script MAJCIPRES : Execution de la requete de verif 2 [ " + requestSelect2 + "]");

      Integer nNb = 0;
      rsSet = dbStatement.executeQuery(requestSelect1);
      if(rsSet != null) {
        while(rsSet.next()) {
          nNb = rsSet.getInt("NB");
          log.info("Script MAJCIPRES : Nombre de facture impactées select 1 dans PIECES : "+ nNb);
        }
        rsSet.close();
       }

       if (nNb == 1)
       {
         rsSet = dbStatement.executeQuery(requestSelect2);
         if(rsSet != null) {
            while(rsSet.next()) {
              nNb = rsSet.getInt("NB");
              log.info("Script MAJCIPRES : Nombre de facture impactées select 2 dans MOV : "+ nNb);
            }
            rsSet.close();
          }

          if ( nNb >= 1 )
          {     
              String request1 = "UPDATE PIECES SET cdbloca = null WHERE CDJOURN NOT IN ('INJOD', 'CGEXA') AND CDSOCIE='"+sCodeSoc+"' and cdtiers_pr='"+sMatricule+"' and LBRFPIE='"+sNumFact+"' and to_char(dtpiece,'yyyymmdd')='"+sDateFacture+"'";
              String request2 = "UPDATE MOV SET mov_blocage = null, mov_flag_a = '0' WHERE company='"+sCodeSoc+"' and noseque_pior=(SELECT noseque FROM PIECES WHERE CDJOURN NOT IN ('INJOD', 'CGEXA') AND CDSOCIE='"+sCodeSoc+"' and LBRFPIE='"+sNumFact+"' and cdtiers_pr = '"+sMatricule+"' and to_char(dtpiece,'yyyymmdd')='"+sDateFacture+"')";
          
		log.info("Script MAJCIPRES : Execution de la requete 1 [ " + request1 + "]");
              log.info("Script MAJCIPRES : Execution de la requete 2 [ " + request2 + "]");

              //execution de la premiere requete
              //MODIFICATION POUR L'ENVIRONNEMENT DE RECETTE
              //Integer nNbUpdate = dbStatement.executeUpdate(request1);
              Integer nNbUpdate = 1;
              log.info("Script MAJCIPRES : Nombre de Update request1 (recette toujours à 1): "+ nNbUpdate);
              if ( nNbUpdate == 1 )
              {                                                  
                  //MODIFICATION POUR L'ENVIRONNEMENT DE RECETTE
                  //nNbUpdate = dbStatement.executeUpdate(request2);
                  nNbUpdate = 1;   
                  log.info("Script MAJCIPRES : Nombre de Update request2 (recette toujours à 1) : "+ nNbUpdate);
                  if ( nNbUpdate >= 1 )
                  {
                      log.info("Script MAJCIPRES : TOUT EST OK, la mise à jour CIPRES est effective");
                      bUpdateOK = true;
                      //dbConnection.commit();
                  }
              }
          }
         else
         {
             log.info("Script MAJCIPRES : Nombre de facture impactées select 2 incorrect : "+ nNb);
         }
      }
      else
      {
          log.info("Script MAJCIPRES : Nombre de facture impactées select 1 incorrect : "+ nNb);
      }
    }
    catch(DigiInternalException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }
    catch(SQLException e) {
      // TODO Auto-generated catch block
      log.error(e.getMessage());
    }
    finally{
         log.info("Script MAJCIPRES : close statement before");
         dbStatement.close();
         log.info("Script MAJCIPRES : close statement after");
      }
      log.info("Script MAJCIPRES : free connection before");
      connectManager.freeConnection(POOL_NAME_CIPRES, dbConnection);
      log.info("Script MAJCIPRES : free connection after");
    }
  
    log.info("Script MAJCIPRES : FIN MAJCIPRES");
    return bUpdateOK;
}

String getDateAsString( Date date )
  {
      java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
      return dateFormat.format(date);
  }

void addcomment(com.digitech.dossier.common.model.backend.airs.impl.Document document, String commentToAdd, UserCoreContext userContext, com.digitech.dossier.common.service.impl.DocumentMgr documentMgr, org.slf4j.impl.Log4jLoggerAdapter log ){
  /*IComment commentModel = new Comment();
  commentModel.setComment(commentToAdd);
  try {
	//on supprime les commentaires du même type si il y en a déjà
	List<IComment> listComment = document.getComments();
	for(IComment monComm : listComment) {
		String sComm = monComm.getComment();
		if ( commentToAdd.compareToIgnoreCase(sComm) == 0)
		{
			monComm.setMode(com.digitech.dossier.common.model.backend.airs.IDocument.MODE_DELETE);	
		}
	}

    document.getComments().add(commentModel);
    documentMgr.updateDocumentComments(userContext, document);
  }
  catch(Exception e) {
    document.getComments().remove(commentModel);
    log.error(e.getLocalizedMessage(), e);
  }*/
  
}