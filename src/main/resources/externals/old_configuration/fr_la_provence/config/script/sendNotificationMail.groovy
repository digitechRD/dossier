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
import com.digitech.dossier.common.model.backend.airs.IDelegate  
import com.digitech.dossier.common.model.backend.airs.ILocutionModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.ITask
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.Constants
import com.digitech.dossier.common.service.IUser
import com.digitech.jcorbairs.User
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
import com.digitech.dossier.workflow.model.impl.WFTask.WFActor   


/**
* Author : JMU
* Date : 22/06/12
* Description : Ce script permet d'envoyer des mails de notification aux assistants,
*               valideursNiv1 ou valideursNiv2. Lancé par le taskManager.
* IN : String strEtatFac : état des factures à rechercher via AIRS
*
*/


//Constantes
String FAC_ETAT_FIELD_CODE = "FAC_ETAT";
String SOCIETE_FIELD_CODE = "SOC";
String NUM_FAC_FIELD_CODE = "FAC_NUM";
String FAC_VALID1_FIELD_CODE = "FAC_VALID1";
String FAC_VALID2_FIELD_CODE = "FAC_VALID2";
String FAC_ASSIST_FIELD_CODE = "FAC_TRAIT1";
String CONTENT_TYPE = "D_DOC_FAC";
String MAIL_TEMPLATE_VALID = "notificationVALID.htm";
String MAIL_TEMPLATE_BLOQ = "notificationBLOQ.htm";
String MAIL_TEMPLATE_ENG = "notificationENG.htm";
     
String ETAT_AVALID1 = "A_VALID1";   
String ETAT_AVALID2 = "A_VALID2"; 
String ETAT_BLOQ1 = "BLOQ1";
String ETAT_BLOQ2 = "BLOQ2";  
    
String ETAT_AVALID = "A_VALID";  
String ETAT_BLOQ = "BLOQ";

String ETAPE_AVALIDER1 = "AValider1";
String ETAPE_AVALIDER2 = "AValider2";    
String ETAPE_BLOQUER1 = "Bloquee1";
String ETAPE_BLOQUER2 = "Bloquee2";
    
String FAC_ATTRIBUT_VALID1_CODE="UtilValid1";                                

String FAC_ETAT_PARAM="fac_etat";

//Récupération du Logger
Logger log = LoggerFactory.getLogger(this.getClass());
log.info("sendNotificationMail : IN");
//Structure contenant les id des users associés aux factures dans l'état recherché
Map<Integer,Map<Integer,List<IDocument>>> UserMailList;
//Récupération du paramètre d'entrée
String strEtatFac = (String)parameterMap.get(FAC_ETAT_PARAM).getValue();
String strEtatFacForAIRSRequest = strEtatFac;

if(strEtatFac.equals(ETAT_AVALID1) || strEtatFac.equals(ETAT_AVALID2)) {
	strEtatFacForAIRSRequest = ETAT_AVALID;
}
else if(strEtatFac.equals(ETAT_BLOQ1) || strEtatFac.equals(ETAT_BLOQ2)) {
	strEtatFacForAIRSRequest = ETAT_BLOQ;
}

log.info("Lecture du parametre d'entree fac_etat="+strEtatFac);



String MAIL_TEMPLATE_FOLDER = "config/templates/mail_template";
log.info("MAIL_TEMPLATE_FOLDER : "+MAIL_TEMPLATE_FOLDER);

//Initialisations
UserMailList = new Hashtable();
log.info("check 1");
ILocutionModel locutionModel = new LocutionModel();
log.info("check 2");

//Construction de la requête AIRS
Integer nTermIDForEtat = getTermID(FAC_ETAT_FIELD_CODE, strEtatFacForAIRSRequest);    
log.info("Valeur de l'etat : "+strEtatFacForAIRSRequest);
log.info("Id de l'etat recherché : "+nTermIDForEtat);
log.info("Id de l'etat recherché : "+nTermIDForEtat);

DocumentUtils.buildLocutionModel(locutionModel, FAC_ETAT_FIELD_CODE, Operator.OPERATOR_VALUE_EQUAL, String.valueOf(nTermIDForEtat));
log.info("check 3");

//Lancement de la requête
log.info("on effectue la recherche AIRS");
List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList(CONTENT_TYPE), null);


if(documentList!=null) {
	log.info(documentList.size()+" facture(s) sont dans l'etat : "+strEtatFac);
	//Type de personne à qui le mail est destiné
	String userField;
	//Fichier de template pour le formatage du mail
	String mailTemplateFile;
		
  //On parcourt toutes les factures
  
  for(IDocument facture : documentList) {
    try {

		//Récupération du Doc_Id
		Integer docId =  facture.getAirsRefId();
		log.info("DocID="+String.valueOf(docId));
		

		
		//Ici pas besoin d'accéder au workflow
		if(strEtatFac.equals("A_ENG")) {
			userField = FAC_ASSIST_FIELD_CODE;
			mailTemplateFile = MAIL_TEMPLATE_ENG;
		}
		//Ici accès au worflow nécessaire pour distinguer Valideurs 1 et 2
		else {
				//Récupération de l'état de la tâche workflow
				UserCoreContext userContext = UserUtils.getAdminUserContext();
				List<IWFTaskModel> taskList = getWFSearchMgr().getTasksFromAirsIds(userContext, Arrays.asList(docId), false, false);

			if(taskList!=null){
				//Choix du type de destinataire et du template mail en fonction de l'état de la tâche workflow
				if(taskList.size()>0){
					WFTask task = taskList.get(0);
					log.info("Valeur workflow : "+task.getName());
						
					if(task.getName().equals(ETAPE_AVALIDER1)){
							userField = FAC_VALID1_FIELD_CODE;
							log.info("Type de destinataire : "+FAC_VALID1_FIELD_CODE);
							mailTemplateFile = MAIL_TEMPLATE_VALID;
					}
					else if(task.getName().equals(ETAPE_AVALIDER2)) {
							userField = FAC_VALID2_FIELD_CODE;
							log.info("Type de destinataire : "+FAC_VALID2_FIELD_CODE);
							mailTemplateFile = MAIL_TEMPLATE_VALID;
					}
					else if(task.getName().equals(ETAPE_BLOQUER1)) {
							userField = FAC_VALID1_FIELD_CODE;
							log.info("Type de destinataire : "+FAC_VALID1_FIELD_CODE);
							mailTemplateFile = MAIL_TEMPLATE_BLOQ;
					}
					else if(task.getName().equals(ETAPE_BLOQUER2)) {
							userField = FAC_VALID2_FIELD_CODE;
							log.info("Type de destinataire : "+FAC_VALID2_FIELD_CODE);
							mailTemplateFile = MAIL_TEMPLATE_BLOQ;
					}
				}
				else {
				log.error("Impossible de récupérer la tâche workflow pour le DOC_ID "+docId);
				continue;
				}
			}
			else {
				log.error("Impossible de récupérer la tâche workflow pour le DOC_ID "+docId);
				continue;
			}
		}
		
		//Récupération du UserID (en fonction du paramètre d'entrée)
		//Integer idUser = (Integer) facture.getField(userField).getValue();
		
		Integer idUser = (Integer) facture.getField(userField).getValue();
		
		UserCoreContext userContext = UserUtils.getAdminUserContext();
		List<IWFTaskModel> taskList = getWFSearchMgr().getTasksFromAirsIds(userContext, Arrays.asList(docId), false, false);
		if(taskList!=null)
		{
			if(taskList.size()>0)
			{
				WFTask task = taskList.get(0);
				Map<String, Object> listData = task.getData();
				if ( listData != null && listData.size() > 0)
				{
					log.info("Nb Datas : "+ listData.size() );
					
					log.info("DEBUT LISTE");
					for (Map.Entry <String, Object> maMap : listData.entrySet())
					{
						String valeur = maMap.getValue();
						String parametre = maMap.getKey();
						log.info(" Parametre : "+ parametre + ", valeur : " +valeur);
					}
					log.info("FIN LISTE");

					String sValid1 = listData.get(FAC_ATTRIBUT_VALID1_CODE);   
					log.info("sValid1 from FAC_ATTRIBUT_VALID1_CODE : "+ sValid1 );

					List <WFActor> actors = task.getActors();
					Integer iSize = actors.size();
					log.info("iSize : "+ iSize );
					if ( iSize >= 1 )
	  				{					
						log.info("iSize >=1 ");
		  				String sActor = actors.get(0).getId();
						log.info("sActor : "+ sActor );
		  				if ( sActor.length() > 5)
		  				{
			  				sValid1 = sActor.substring(5);
							log.info("sValid1 from actorsWF : "+ sValid1 );
			 		  	}
	  				}

					if ( sValid1!= null && sValid1.length() >0 )
					{
						idUser = Integer.parseInt(sValid1);
						log.info("idUser="+idUser);
					}
				}
			}
		}
				  
		log.info("idUser="+String.valueOf(idUser));
	   
		//Récupération du SOCID
		Integer idSoc = (Integer) facture.getField(SOCIETE_FIELD_CODE).getValue();
		log.info("idSoc="+idSoc);
		 
		 //Si un des champs est null on effectue un saut de boucle
		 if(idUser == null) {
		  log.error(userField+" est nul pour le DOC_ID : "+docId);
		  continue;
		}
		if(idSoc == null) {
		  log.error(SOCIETE_FIELD_CODE+" est nul pour le DOC_ID : "+docId);
		  idSoc = -1;
		  //continue;
		} 
		
		//On fait un saut de boucle pour ne pas gérer les valideurs niv 1 et 2 dans le même script
		if((strEtatFac.equals(ETAT_AVALID1) || strEtatFac.equals(ETAT_BLOQ1) ) && !userField.equals(FAC_VALID1_FIELD_CODE)) {
				continue;
		}
		else if((strEtatFac.equals(ETAT_AVALID2) || strEtatFac.equals(ETAT_BLOQ2) ) && !userField.equals(FAC_VALID2_FIELD_CODE)) {
			continue;
		}
		
		//Besoin d'ajouter un nouvel utilisateur dans la liste
		log.info("Ajout d'un nouvel utilisateur dans la mailing list");
		if(UserMailList.get(idUser) == null) {
		  
			UserMailList.put(idUser, new Hashtable<Integer,List<IDocument>>());
			log.info("Utilisateur "+idUser+" ajoute");
		}
		
		//Besoin d'ajouter une nouvelle société dans la liste
		log.info("Ajout d'une nouvelle société dans la mailing list");
		if(UserMailList.get(idUser).get(idSoc) == null) {
		  
			UserMailList.get(idUser).put(idSoc, new ArrayList<IDocument>());
			log.info("Société "+idSoc+" ajoutee");
		}
		
		//Ajout d'une nouvelle facture
		log.info("Ajout d'une nouvelle facture dans la mailing list");
		UserMailList.get(idUser).get(idSoc).add(facture);
    }
    catch(Exception e) {
      log.error("exception : "+e.printStackTrace()+e.getMessage());
	}
  }//end for
  
  //Envoi du mail
	try {
		//Récupération du template mail
		log.info("Récupération du template mail");
		File theMailTemplate = new File(getDossierTemplateFolderPath()+File.separator+mailTemplateFile);
	  
		if(theMailTemplate == null){
			log.error("impossible d'ouvrir le template mail "+getDossierTemplateFolderPath()+File.separator+mailTemplateFile);
		}
	  
		if (!UserMailList.isEmpty()) {
		  
			for (Iterator<Integer> id = UserMailList.keySet().iterator() ; id.hasNext() ;){
				Integer key = id.next();
				//Envoi du mail à l'utilisateur
				log.info("Envoi d'un mail à : "+key);
				sendMailToUser(key, UserMailList.get(key), theMailTemplate);
				List<Integer> listDeleg = getDelegTemporaire(key);
			  for(Integer idDeleg : listDeleg) { 
			    log.info("J'envoit aussi un mail à : "+idDeleg); 
				  sendMailToUser(idDeleg, UserMailList.get(key), theMailTemplate);
			  }
			}
		}
	} catch(Exception e) {
      log.error("exception : "+e.getMessage()+"\n"+e.getStackTrace());
	}
}
else {
  log.info("Aucune facture n'est dans l'état "+strEtatFac);
}

log.info("sendNotificationMail : OUT");


List<Integer> getDelegTemporaire(Integer idUser)
{
  List<Integer> delegationIds = new ArrayList<Integer>();
  Logger log = LoggerFactory.getLogger(this.getClass());
  log.info("Récupération des id des délégués de : "+idUser);
  try {
     for(IDelegate delegate : getUserMgr().getDelegates(UserUtils.getAdminUserContext().getJeton(), idUser)) {
      User user = delegate.getUser();
      Date startDate = delegate.getStartDate();
      Date endDate = delegate.getEndDate();
	  log.info("Date début délégation : "+startDate);
	  log.info("Date fin délégation : "+endDate);
      boolean bDelegationActif = false;
      if ( startDate != null && endDate != null )
      {       
         Date dateDuJour=new Date(); 
		 log.info("Date du jour : "+dateDuJour);
         if ( dateDuJour.before(endDate) && dateDuJour.after(startDate)) 
		 {
            bDelegationActif = true;
			log.info("OK, c'est un délégué temporaire : "+user.getId());
		 }
      }
      
      if(user != null && bDelegationActif) {
			    log.info("J'ajoute aussi : "+user.getId()); 
         delegationIds.add(user.getId());
      }
    }
  }
  catch(Exception e) {
     throw new RuntimeException(e);
  } 
  return delegationIds;
}    
      


private void sendMailToUser(Integer idUser, Map<Integer,List<IDocument>> listeFacture, File theMailTemplate) throws InvalidParameterException, ServerException, IdentificationException, IOException, TemplateException,
JAXBException, SAXException, EmailException, InvalidConfigurationException, MessagingException {
//Get the logger
Logger log = LoggerFactory.getLogger(this.getClass());
  Integer userId = idUser;
  List<IDocument> theDocumentUserList = new ArrayList();
  IUser userService = (IUser) ServiceManager.getInstance().getService(Constants.SERVICE_AIRS_USER_MGR);
log.info("userId in sendMailToUser="+userId);
  UserCoreContext adminContext = UserUtils.getAdminUserContext();
  
  //On trie les factures dans une liste de IDocuments
  for (Iterator<List> liste = listeFacture.values().iterator() ; liste.hasNext() ;){

   for(IDocument facture : liste.next()) {
      //Récupération du Doc_Id
		  Integer docId =  facture.getAirsRefId();
		  log.info("DocIDInMail="+String.valueOf(docId));
      theDocumentUserList.add(facture);
    }
  }
  log.info("ApplicationUtils.sendMail : BEFORE");
  ApplicationUtils.sendMail(
    DossierCoreContext.getParamsInfos().getWebAppURL(),
    adminContext,
    theDocumentUserList,
    theMailTemplate,
    new ReportPerson(adminContext, userService.getUser(userId)),
    //BundleUtils.getTranslation("AIRS DOSSIER notification facture"),
    "AIRS DOSSIER notification facture",
    null);    
  log.info("ApplicationUtils.sendMail : AFTER");
}


/**
* Gets a term ID.
* @param fieldCode the field code
* @param termCode the term code
* @return the term ID
* @throws IdentificationException
* @throws ServerException
*/
public Integer getTermID(String fieldCode, String termCode)
throws IdentificationException, ServerException {
 List<Term> termList = getAuthorityListService().getTerms(UserUtils.getAdminUserContext().getJeton(), fieldCode);
 for(Term term : termList) {
   if(term.getCode().equals(termCode)) {
     return term.getId();
   }
 }
 return -1;
}

/**
* Gets the path to the Courrier Template folder.
*
* @return the path to the Courrier Template folder
*/
public String getDossierTemplateFolderPath() {
 StringBuffer sb = new StringBuffer();
 sb.append(DossierCoreContext.getApplicationPath());
 sb.append(File.separator);
 sb.append("config/templates/mail_template");
 return sb.toString();
}

private IUser getUserMgr() {
  return (com.digitech.dossier.common.service.IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }
  
    /**
   * @return IAuthorityList the Authority List
   */
  public static IAuthorityList getAuthorityListService() {
    return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
  }
  
  private static IWFSearchService getWFSearchMgr() {
    return (IWFSearchService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_WORKFLOW_SEARCH_MGR);
  }