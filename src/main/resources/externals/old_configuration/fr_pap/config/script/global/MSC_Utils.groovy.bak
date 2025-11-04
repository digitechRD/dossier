/************************************************************************************************************************************
 * Auteur 	  	: JMU
 * Date 		  	: 24/03/16
 * Description   : Classe utilitaire pour le flux documentaire
 ************************************************************************************************************************************/


import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment
import com.digitech.dossier.common.model.backend.airs.IProfile
import com.digitech.dossier.common.model.backend.airs.impl.Document
import com.digitech.dossier.common.service.*
import com.digitech.dossier.common.service.export.IOdtGenerator
import com.digitech.dossier.common.service.export.impl.OdtGenerator
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User
import com.digitech.jcorbairs.admin.OrganizationAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager
import com.digitech.jcorbairs.admin.ProfilAdmin
import com.digitech.jcorbairs.admin.UserAdmin
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.report.service.IDocumentConvertionService
import com.digitech.report.service.IDocumentGenerationService
import com.digitech.report.service.IDocumentInspectorService
import com.digitech.report.service.impl.ooo.DocumentConvertionService
import com.digitech.report.service.impl.ooo.DocumentGenerationService
import com.digitech.report.service.impl.ooo.DocumentInspectorService
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MSC_Utils {
  public interface GroovyCommand {
    public void run();
  }

  private final static Logger log = LoggerFactory.getLogger("com.digitech.dossier.script.MSC_Utils");


  /**
   * Renvoie l'id d'un item de liste d'autorite en fonction du code.
   * @param fieldCode le code du champ (liste autorite)
   * @param termCode le code de l'item de liste d'autorite
   * @return l'id de l'item
   * @throws IdentificationException* @throws ServerException
   */
  public static Integer getTermID(String fieldCode, String termCode) throws IdentificationException, ServerException {
    List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(fieldCode);
    for (Term term : termList) {
      if (term.getCode().equals(termCode)) {
        return term.getId();
      }
    }
    return -1;
  }

  /**
   * Gets a term Code.
   * @param fieldCode the field code
   * @param termId the term Id
   * @return the term Code
   * @throws IdentificationException* @throws ServerException
   */
  public static String getTermCode(String fieldCode, Integer termId) throws IdentificationException, ServerException {
    List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(fieldCode);
    for (Term term : termList) {
      if (term.getId().equals(termId)) {
        return term.getCode();
      }
    }
    return null;
  }

  /**
   * Retourne le code du service en fonction d'un id
   * @param orgId l'id du service
   * @param userContext le contexte de l'utilisateur connecte
   * @return service le code du service
   */
  private static String getServiceCode(Integer orgId, UserContext userContext) throws IdentificationException, ServerException {
    String serviceCode = null;
    OrganizationAdmin orgAdm = OrganizationsManager.load(userContext.getJeton(), orgId);
    if (orgAdm != null) {
      serviceCode = orgAdm.getCode();
    }
    return serviceCode;
  }

  /**
   * Retourne la liste des utilisateurs possedant le profil profileCode
   * @param profileCode le code du profil concerne
   * @return elements la liste des utilisateurs possedant le profil
   */
  public static List<User> getUsersFromProfile(String profileCode) {
    String methodName = "getUsersFromProfile";
    List<User> elements = new ArrayList<User>();

    //Chargement du profil
    IProfile profile = getRightMgr().getProfile(profileCode);
    if (profile != null) {
      ProfilAdmin currentProfil = com.digitech.jcorbairs.admin.ProfilsManager.load(DossierCoreContext.getAdminJeton(), profile.getId().intValue());
      if (currentProfil != null) {
        // Recuperation de tous les utilisateurs qui ont le profil
        List<UserAdmin> userAdmList = currentProfil.getUsers();
        if (userAdmList != null && userAdmList.size() > 0) {
          User user;
          for (UserAdmin usrAdm : userAdmList) {
            if ((user = getUserMgr().getUser(usrAdm.getId())) != null) {
              log.debug(methodName + " :  Ajout de l'utilisateur " + user.getLogin());
              elements.add(user);
            }
          }
        }
      }
    }
    return elements;
  }


  /**
   *  Ajoute un evenement d'historique de type changement de valeur pour un document.
   * @param document : Le document.
   * @param userContext : Le contexte utilisateur.
   * @param fieldCode : Le code du champ AIRS.
   * @param oldValue : L'ancienne valeur du champ.
   * @param newValue : La nouvelle valeur du champ.
   *
   */
  private static void addHistoForField(IDocument document, UserCoreContext userContext, String fieldCode, String oldValue, String newValue) {
    // Si le document n'est pas verrouille
    if (document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER) {
      if (newValue != null && oldValue.compareToIgnoreCase(newValue) != 0 && !fieldCode.equals("D_MODIF")) {
        // Ajout dans l'historique
        String commentEvent = "Changement de valeur " + fieldCode + " : " + oldValue + " :" + newValue;
        getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_FIELDCHANGE.name(), commentEvent);
      }
    }
  }

  /**
   * Ajoute une entree de type workflow dans l'historique d'un document
   * @param document : Le document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param newWorkflowValue : L'entree workflow à ajouter.
   */
  private static void addHistoForWorkflow(IDocument document, UserCoreContext userContext, String newWorkflowValue) {
    // Si le document n'est pas verrouille
    if (document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER) {
      if (newWorkflowValue != null) {
        // Ajout dans l'historique
        String commentEvent = "Action workflow : " + newWorkflowValue;
        getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_FIELDCHANGE.name(), commentEvent);
      }
    }
  }

  /**
   * Ajoute un commentaire au document
   * @param document
   * @param userContext
   * @param newWorkflowValue
   */
  private static void addComment(IDocument document, UserCoreContext userContext, String comment) {
    String methodName = "addComment";
    // Si le document n'est pas verrouille
    if (document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER) {
      // Ajout du commentaire si la chaine n'est pas vide
      if (comment != null && !comment.equals("")) {
        IComment commentObj = new Document.Comment();
        commentObj.setComment(comment);
        document.getComments().add(commentObj);

        log.debug(methodName + " : Enregistrement du commentaire : " + comment);
        getDocumentMgr().updateDocumentComments(userContext, document);
      } else {
        log.debug(methodName + " : Le commentaire est vide");
      }
    } else {
      log.debug(methodName + " : Le document est verrouille");
    }
  }


  /**
   * @return IAuthorityList : Retourne le AuthorityListMgr
   */
  public static IAuthorityList getAuthorityListService() {
    return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
  }

  /**
   * @return IUser : Retourne le UserMgr
   */
  public static IUser getUserMgr() {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }

  /**
   * @return IServer : Retourne le ServerMgr
   */
  public static IServer getServerMgr() {
    return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
  }

  /**
   * @return IRight : Retourne le RightMgr
   */
  public static IRight getRightMgr() {
    return (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);
  }

  /**
   * @return IDocument : Retourne le DocumentMgr
   */
  public static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
    return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
  }

  /**
   * @return IAuditService : Retourne le AuditMgr
   */
  public static com.digitech.dossier.common.service.IAuditService getAuditMgr() {
    return (com.digitech.dossier.common.service.IAuditService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
  }

  private static IDocumentConvertionService getDocumentConversionService() {
    IDocumentConvertionService docConversionService = new DocumentConvertionService();
    return docConversionService;
  }

  private static IOdtGenerator getOdtGeneration() {
    IOdtGenerator OdtGeneratorService = new OdtGenerator();
    return OdtGeneratorService;
  }

  private static IDocumentInspectorService getDocumentInspectorService() {
    IDocumentInspectorService docInspetorService = new DocumentInspectorService();
    return docInspetorService;
  }

  private static IDocumentGenerationService getDocumentGenerationService() {
    //A verifier
    IDocumentGenerationService docGenerationService = new DocumentGenerationService();
    return docGenerationService;
  }


  /**
   * Ajoute une entree dans l'historique d'un document
   * @param document : Le document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param typeEvent : type de l'évenement AdvancedAuditType.
   * @param libelle : Libellé à ajouter.
   */
  private static void addHisto(IDocument document, UserCoreContext userContext, String typeEvent, String libelle) {
    // Si le document n'est pas verrouille
    if (document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER) {
      if (libelle != null) {
        getAuditMgr().addDocumentEvent(userContext, document, typeEvent, libelle);
      }
    }
  }

  /**
   * Envoi de mail
   * @param document : Le document
   * @param userContext : Le contexte de l'utilisateur courant
   * @user : l'utilisateur connecté
   * @emailTo : la valeur De
   * @emailFrom : la valeur Pour
   * @emailCc : la valeur Copie à
   * @emailBcc : la valeur Copie cachée à
   * @emailSubject : la valeur du sujet
   * @emailContent : la valeur du contenu
   * @emailSendLinkToDocument : true si on veut ajouter un lien sur le document dans le contenu du mail
   * @emailAttachments : liste des pièces jointes
   */
  public static boolean sendMail_PJ(IDocument document, UserContext userContext,
                                    String emailTo, String emailFrom, String emailCc, String emailBcc, String emailSubject,
                                    String emailContent, String emailSendLinkToDocument, List<IAttachment> emailAttachments) {

    String methodName = "sendMailAvecLien";

    log.debug(methodName + " : envoi mail a " + emailTo);

    Dictionary<String, Object> fields = new Hashtable<>();
    if (StringUtils.isNotBlank(emailFrom)) {
      fields.put(IMail.FROM, emailFrom);
//      fields.put(IMail.REPLYTO, emailFrom);
    }
    if (StringUtils.isNotBlank(emailTo)) {
      fields.put(IMail.TO, emailTo);
    }
    if (StringUtils.isNotBlank(emailCc)) {
      fields.put(IMail.CC, emailCc);
    }
    if (StringUtils.isNotBlank(emailBcc)) {
      fields.put(IMail.BCC, emailBcc);
    }
    if (StringUtils.isNotBlank(emailSubject)) {
      fields.put(IMail.SUBJECT, emailSubject);
    }

    // Ajout du lien cliquable si besoin au sein du contenu du mail
    if (emailSendLinkToDocument == "true") {
      String url = DossierCoreContext.getParamsInfos().getWebAppURL() + "/faces/redirect.jsp?outcome=gotoDocumentUnitaire&docId=" + document.getAirsRefId();
      String docLink = "Pour accéder directement au document en GED : " + url;

      if (StringUtils.isNotBlank(emailContent))
        emailContent = emailContent + "\n\n";

      emailContent = emailContent + docLink;
    }
    if (StringUtils.isNotBlank(emailContent))
      fields.put(IMail.CONTENT, emailContent);

    IMail emailService = (IMail) ServiceManager.getInstance().getService(Constants.SERVICE_MAIL_MGR);

    try {
      emailService.sendMail(userContext, document, emailAttachments, fields, null, null);
      return true;
    }
    catch (Exception e) {
      log.error(methodName + " : " + e.getMessage());
    }

    return false;
  }
  
    /**
   * Envoi de mail sans PJ
   * @param document : Le document
   * @param userContext : Le contexte de l'utilisateur courant
   * @user : l'utilisateur connecté
   * @emailTo : la valeur De
   * @emailFrom : la valeur Pour
   * @emailCc : la valeur Copie à
   * @emailBcc : la valeur Copie cachée à
   * @emailSubject : la valeur du sujet
   * @emailContent : la valeur du contenu
   * @emailSendLinkToDocument : true si on veut ajouter un lien sur le document dans le contenu du mail
   */
  public static boolean sendMail(IDocument document, UserContext userContext,
                                    String emailTo, String emailFrom, String emailCc, String emailBcc, String emailSubject,
                                    String emailContent, String emailSendLinkToDocument) {

    String methodName = "sendMailSansLien";

    log.debug(methodName + " : envoi mail a " + emailTo);

    Dictionary<String, Object> fields = new Hashtable<>();
    if (StringUtils.isNotBlank(emailFrom)) {
      fields.put(IMail.FROM, emailFrom);
//      fields.put(IMail.REPLYTO, emailFrom);
    }
    if (StringUtils.isNotBlank(emailTo)) {
      fields.put(IMail.TO, emailTo);
    }
    if (StringUtils.isNotBlank(emailCc)) {
      fields.put(IMail.CC, emailCc);
    }
    if (StringUtils.isNotBlank(emailBcc)) {
      fields.put(IMail.BCC, emailBcc);
    }
    if (StringUtils.isNotBlank(emailSubject)) {
      fields.put(IMail.SUBJECT, emailSubject);
    }

    // Ajout du lien cliquable si besoin au sein du contenu du mail
    if (emailSendLinkToDocument == "true") {
      String url = DossierCoreContext.getParamsInfos().getWebAppURL() + "/faces/redirect.jsp?outcome=gotoDocumentUnitaire&docId=" + document.getAirsRefId();
      String docLink = "Pour accéder directement au document en GED : " + url;

      if (StringUtils.isNotBlank(emailContent))
        emailContent = emailContent + "\n\n";

      emailContent = emailContent + docLink;
    }
    if (StringUtils.isNotBlank(emailContent))
      fields.put(IMail.CONTENT, emailContent);

    IMail emailService = (IMail) ServiceManager.getInstance().getService(Constants.SERVICE_MAIL_MGR);

    try {
      emailService.sendMail(userContext, document, fields, null, null);
      return true;
    }
    catch (Exception e) {
      log.error(methodName + " : " + e.getMessage());
    }

    return false;
  }


} // Class MSC_Utils