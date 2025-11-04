import java.util.List;

import javax.faces.model.SelectItem

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitech.common.lib.utils.StringUtils;
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.airs.IProfile
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment
import com.digitech.dossier.common.model.backend.airs.IDocument.IEvent;
import com.digitech.dossier.common.model.backend.airs.impl.Document
import com.digitech.dossier.common.model.backend.airs.impl.Document.Event
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule
import com.digitech.dossier.script.service.impl.ScriptMgr
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User
import com.digitech.jcorbairs.admin.OrganizationAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager
import com.digitech.jcorbairs.admin.ProfilAdmin
import com.digitech.jcorbairs.admin.UserAdmin
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException

/**
 * Auteur : JMU
 * Date : 31/03/14
 * Description : Classe statique utilitaire pour le flux générique.
 * Version : 1.0
 **/
class GenScriptUtils
{
  private final static Logger log = LoggerFactory.getLogger("com.digitech.dossier.script.GenScriptUtils");
  private static File constantsFile = null;
  private static Properties properties = new Properties();
  static
  {
    constantsFile = new File(DossierCoreContext.getApplicationPath() + File.separator + ScriptMgr.SCRIPT_RELATIVE_PATH + File.separator + "GEN" + File.separator + "global"  +File.separator + "constants.properties");
    constantsFile.withInputStream
    {
      stream->properties.load(stream)
    }
  }

  /**
   * Permet de récupérer la valeur d'une constante à partir d'un code.
   *
   * @param code : Le code de la constante à récupérer.
   * @return La valeur de la constante.
   */
  public static String getConstant(String code)
  {
    if (!properties.containsKey(code))
    {
      throw new ScriptException("Key '" + code + "' not found in file " + constantsFile);
    }

    return properties.getProperty(code);
  }

  /**
   * Permet de récupérer la valeur d'une constante à partir d'un code.
   *
   * @param code : Le code de la constante à récupérer.
   * @param defaultValue : La valeur par défaut retournée si le code n'existe pas.
   * @return La valeur de la constante.
   */
  public static String getConstant(String code, String defaultValue)
  {
    if (!properties.containsKey(code))
    {
      return defaultValue;
    }
    return properties.getProperty(code);
  }

  /**
   * Permet de récupérer le service des listes d'autorité.
   *
   * @return IAuthorityList : Le service des listes d'autorité.
   */
  public static IAuthorityList getAuthorityListService()
  {
    return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
  }

  /**
   * Permet de récupérer le service des audits.
   *
   * @return AuditMgr : Le manager des audits.
   */
  private static com.digitech.dossier.common.service.impl.AuditMgr getAuditMgr()
  {
    return (com.digitech.dossier.common.service.impl.AuditMgr) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
  }

  /**
   * Permet de récupérer le User Manager.
   *
   * @return IUser : Le User Manager.
   */
  private static IUser getUserMgr()
  {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }

  /**
   * Permet de récupérer le Server Manager.
   *
   * @return IServer : Le Server Manager.
   */
  private static IServer getServerMgr()
  {
    return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
  }

  /**
   * Permet de récupérer le Right Manager.
   *
   * @return IRight : Le Right Manager.
   */
  private static IRight getRightMgr()
  {
    return (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);
  }

  /**
   * Permet de récupérer le Document Manager.
   *
   * @return IDocument : Le Document Manager.
   */
  private static com.digitech.dossier.common.service.IDocument getDocumentMgr()
  {
    return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
  }

  /**
   * Récupère le code d'un service à partir de son ID.
   * @param orgId : L'ID du service.
   * @param userContext : Le contexte de l'utilsiateur courant.
   * @return Le code du service.
   */
  private static String getServiceCode(Integer orgId, UserContext userContext) throws IdentificationException, ServerException
  {
    String service = null;
    OrganizationAdmin orgAdm = OrganizationsManager.load(userContext.getJeton(), orgId);
    if (orgAdm != null)
    {
      service = orgAdm.getCode();
    }
    return service;
  }
  /**
   * Récupére la liste des utilisateurs possédant un profil spécifique.
   * @param profileCode : Le profil spécifique.
   * @return La liste des utilisateur possédant le profil spécifique.
   */
  public static List<User> getUsersFromProfile(String profileCode)
  {
  String methodName = "getUsersFromProfile";
    List<User> elements = new ArrayList<User>();

    // Chargement du profil
    IProfile profile = getRightMgr().getProfile(profileCode);
    if( profile != null )
    {
      ProfilAdmin currentProfil = com.digitech.jcorbairs.admin.ProfilsManager.load(DossierCoreContext.getAdminJeton(), profile.getId().intValue());
      if( currentProfil != null )
      {
        // Get all users who have the profile
        List<UserAdmin> userAdmList = currentProfil.getUsers();
        if( userAdmList != null && userAdmList.size() > 0 )
        {
          User user;
          for(UserAdmin usrAdm : userAdmList)
          {
            if( (user = getUserMgr().getUser(usrAdm.getId())) != null )
            {
        log.debug(methodName+" :  Ajout de l'utilisateur "+user.getLogin());
              elements.add(user);
            }
          }
        }
      }
    }
    return elements;
  }

  
    /**
   * Récupère la liste des item dépendant du code de l'item père
   * @param rankingCode : Le code du classeur.
   * @return La liste du plan de classement qui découle de ce classeur.
   */
  public static List<SelectItem> getSelectItemsFromParent(String rankingCode, IField field)
  {
    String methodName;
    List<SelectItem> selectItems = new SelectItemFactory().getAuthorities(field.getConfigField());
	List<SelectItem> filteredSelectItems = new ArrayList<SelectItem>();
    List<Term> termList = new ArrayList<Term>();
    Iterator<SelectItem> iter = selectItems.iterator();
    while (iter.hasNext())
    {
      SelectItem ItemElement = iter.next();
      log.debug(methodName + " : " + ItemElement);
      if( ItemElement != null && !StringUtils.isBlank((String)ItemElement.getValue()) )
      {
        log.debug(methodName + " : ItemElement.getValue()) : " + ItemElement.getValue() );
        log.debug(methodName + " : parse id : "  + ItemElement.getValue() );
        Integer nIdItem = Integer.parseInt((String)ItemElement.getValue());
        Term monTerme = CourrierUtils.getAuthorityListMgr().getTerm(nIdItem);
        String codeTerm = monTerme.getCode();
        log.debug(methodName + " : code : "  + codeTerm);
        
        if (!StringUtils.isBlank(codeTerm) && codeTerm.startsWith(rankingCode))
        {
          log.debug(methodName +" : ajout de l'item : "  + codeTerm);
          filteredSelectItems.add(ItemElement);
        }
      }
    }
    return filteredSelectItems;
  }
  
  

  /**
   * Vérifie si l'état du document fais partie de la liste d'états (states).
   *
   * @param document : Le document.
   * @param states : Liste des états possibles.
   * @return true si le document est dans l'un des états listés dans la liste des états possibles.
   */
  public static boolean hasState(IDocument document, List<String> states)
  {
    return hasState(null, document, states);
  }

  /**
   * Vérifie si l'état du document fais partie de la liste d'états (states).
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @param states : Liste des états possibles.
   * @return true si le document est dans l'un des états listés dans la liste des états possibles.
   */
  public static boolean hasState(UserContext userContext, IDocument document, List<String> states)
  {
    String fieldCodeDocumentState = getConstant("FIELD_CODE_DOC_STATUS");
    Integer stateId = FieldUtils.getValue(document, fieldCodeDocumentState);
    if (stateId != null)
    {
      Term term = ((IAuthorityList)getAuthorityListService()).getTerm(stateId);
      if(term == null)
      {
        throw new IllegalStateException("No term with ID '" + stateId + "' found for authority list " + fieldCodeDocumentState);
      }
      return states.contains(term.getCode());
    }
    return false;
  }

  /**
   * Vérifie si l'utilisateur courant est le valideur du document. Basé sur le champ valideur.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true si l'utilisateur est le valideur du document.
   */
  public static boolean isValidatorUser(UserContext userContext, IDocument document)
  {
    String fieldCodeDocumentValidatorUser = getConstant("FIELD_CODE_DOC_VALIDATOR");
    Integer docValidator = null;
    try
    {
      docValidator = FieldUtils.getValue(document, fieldCodeDocumentValidatorUser);
    }
    catch(IllegalStateException ise)
    {
      return false;
    }
    return docValidator != null && docValidator.equals(userContext.getUser().getId());
  }

  /**
   * Vérifie si l'utilisateur courant est le valideur du document. Basé sur le champ valideur.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true si l'utilisateur est le valideur du document.
   */
  public static boolean isCollaboratorUser(UserContext userContext, IDocument document)
  {
    String fieldCodeDocumentCollaboratorUser = getConstant("FIELD_CODE_DOC_COLLABORATOR");
    List<Integer> docCollaborators = null;
    try
    {
      docCollaborators = FieldUtils.getValues(document, fieldCodeDocumentCollaboratorUser);
    }
    catch(IllegalStateException ise)
    {
      log.error("isCollaboratorUser : "+ise.getMessage());
      return false;
    }
    return docCollaborators != null && docCollaborators.contains(userContext.getUser().getId());
  }

  /**
   * Retire un utilisateur de la liste des collaborateurs.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   */
  public static void removeUserFromCollaborators(UserContext userContext, IDocument document)
  {
  String methodName = "removeUserFromCollaborators";
    String fieldCodeDocumentCollaboratorUser = getConstant("FIELD_CODE_DOC_COLLABORATOR");
    List<Integer> docCollaborators = null;
    try
    {
      docCollaborators = FieldUtils.getValues(document, fieldCodeDocumentCollaboratorUser);
    }
    catch(IllegalStateException ise)
    {
      log.error(methodName+" : "+ise.getMessage());
    }
  
  Integer userId = userContext.getUser().getId();
    if(docCollaborators != null && docCollaborators.contains(userId))
    {
    // Obligation de parcourir la liste à cause de la syntaxe groovy...
    for(int i=0; i < docCollaborators.size(); i++)
    {
      if(docCollaborators.get(i) == userId)
      {
        docCollaborators.remove(i);
        break;
      }
    }
      
      FieldUtils.setValues(document, getConstant("FIELD_CODE_DOC_COLLABORATOR"), docCollaborators);
    }
  else
  {
    log.error(methodName+" : L'utilisateur ne fait pas parti de la liste des collaborateurs.");
  }
  }

  /**
   * Retire un utilisateur de la liste des collaborateurs.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   */
  public static void removeAllUsersFromCollaborators(UserContext userContext, IDocument document)
  {
  String methodName = "removeAllUsersFromCollaborators";
    String fieldCodeDocumentCollaboratorUser = getConstant("FIELD_CODE_DOC_COLLABORATOR");
    List<Integer> docCollaborators = null;
    try
    {
      docCollaborators = FieldUtils.getValues(document, fieldCodeDocumentCollaboratorUser);
    }
    catch(IllegalStateException ise)
    {
      log.error(methodName+" : "+ise.getMessage());
    }
  
  Integer userId = userContext.getUser().getId();
    if(docCollaborators != null)
    {
    docCollaborators.removeAll();
      FieldUtils.setValues(document, getConstant("FIELD_CODE_DOC_COLLABORATOR"), docCollaborators);
    }
  }
  
  /**
   * Vérifie si l'utilisateur courant est le valideur du document. Basé sur le champ valideur.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true si l'utilisateur est le valideur du document.
   */
  public static boolean isCreatorUser(UserContext userContext, IDocument document)
  {
    boolean isCreator = false;
  boolean isCreatorByEvent = false;
  boolean isCreatorByField = false;
    List<IEvent> eventsList;

    // Récupération des events pour le document.
    eventsList = document.getEvents();

    // Récupération de l'event de création.
    for(Event event : eventsList)
    {
      if(event.getType() == com.digitech.jcorbairs.DocumentAction.getDocumentActionTypeId(userContext.getJeton(), AdvancedAuditType.ADV_EVENT_DOC_CREATE.name()))
      {
        // Comparaison entre l'id de l'utilisateur courant et de celui ayant crée le document.
        if(event.getUserId().equals(userContext.getUser().getId()))
        {
          isCreatorByEvent = true;
        }
        break;
      }
    }
  
  String fieldCodeDocumentCreatorUser = getConstant("FIELD_CODE_DOC_CREAT");
    Integer docCreator = null;
    try
    {
      docCreator = FieldUtils.getValue(document, fieldCodeDocumentCreatorUser);
    if(userContext.getUser().getId() == docCreator)
    {
    isCreatorByField = true;
    }
    }
    catch(IllegalStateException ise)
    {
      return false;
    }

  isCreator = isCreatorByEvent || isCreatorByField;
    return isCreator;
  }

  /**
   * Récupère l'ID d'un terme (item de liste d'autorité).
   * @param fieldCode : Le code du champ.
   * @param termCode : Le code de l'item de liste d'autorité.
   * @return L'ID du terme.
   * @throws IdentificationException
   * @throws ServerException
   */
  public static Integer getTermID(String fieldCode, String termCode)
  throws IdentificationException, ServerException
  {
    return getTermID(null, fieldCode, termCode);
  }

  /**
   * Récupère l'ID d'un terme (item de liste d'autorité).
   * @param document : Le document.
   * @param fieldCode : Le code du champ.
   * @param termCode : Le code de l'item de liste d'autorité.
   * @return L'ID du terme.
   * @throws IdentificationException
   * @throws ServerException
   */
  public static Integer getTermID(IDocument document, String fieldCode, String termCode)
  throws IdentificationException, ServerException
  {
    List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(fieldCode);
    for(Term term : termList)
    {
      if(term.getCode().equals(termCode))
      {
        return term.getId();
      }
    }
    return -1;
  }

  /**
   *  Ajoute un événement d'historique de type changement de valeur pour un document.
   *
   * @param document : Le document.
   * @param userContext : Le contexte utilisateur.
   * @param fieldCode : Le code du champ AIRS.
   * @param oldValue : L'ancienne valeur du champ.
   * @param newValue : La nouvelle valeur du champ.
   *
   */
  private static void addHistoForField(IDocument document, UserCoreContext userContext, String fieldCode, String oldValue, String newValue)
  {
    // Si le document n'est pas verrouillé
    if(document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER)
    {
      if(newValue != null && oldValue.compareToIgnoreCase(newValue) != 0 && !fieldCode.equals("D_MODIF"))
      {
        // Ajout dans l'historique
        String commentEvent = fieldCode+" : "+oldValue+" :"+newValue;
        getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_FIELDCHANGE.name(), commentEvent);
      }
    }
  }

  /**
   * Ajoute une entrée de type workflow dans l'historique d'un document
   * @param document : Le document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param newWorkflowValue : L'entrée workflow à ajouter.
   */
  private static void addHistoForWorkflow(IDocument document, UserCoreContext userContext,  String newWorkflowValue)
  {
    // Si le document n'est pas verrouillé
    if(document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER)
    {
      if(newWorkflowValue != null )
      {
        // Ajout dans l'historique
        String commentEvent = "Action workflow : "+newWorkflowValue;
        getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_FIELDCHANGE.name(), commentEvent);
      }
    }
  }

  /**
   *
   * @param document
   * @param userContext
   * @param newWorkflowValue
   */
  private static void addComment(IDocument document, UserCoreContext userContext, String comment)
  {
    // Si le document n'est pas verrouillé
    if(document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER)
    {
      //Ajout du commentaire si la chaine n'est pas vide
      if(!comment.equals(""))
      {
        IComment commentObj = new Document.Comment();
        commentObj.setComment(comment);
        document.getComments().add(commentObj);

        log.debug("addComment : Enregistrement du commentaire : "+comment);
        GenScriptUtils.getDocumentMgr().updateDocumentComments(userContext, document);
      }
      else
      {
        log.debug("addComment : Le commentaire est vide.");
      }
    }
    else
    {
      log.debug("addComment : Le document est verrouillé.");
    }
  }

  /**
   * Indique si le bouton de publication est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isValidatePublicationButtonVisible(UserContext userContext, IDocument document)
  {
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
    String methodName = "isValidatePublicationButtonVisible";
    boolean documentHasState;
    boolean userHasProfile;
    boolean userIsValidator;
    boolean visible;
    boolean documentLockedByOther;

    List<String> states = new ArrayList<String>();
    states.add(getConstant("ITEM_CODE_DOC_STATUS_WAIT_PUB"));

    documentHasState = hasState(userContext, document, states);
    userHasProfile = UserUtils.hasProfile(userContext, getConstant("PROFILE_CODE_VALIDATOR"));
    userIsValidator = isValidatorUser(userContext, document);

    // Le bouton est visible si le document est dans l'état "en attente de publication", que l'utilisateur possède le profil valideur et qu'il soit le valideur du document.
    visible =  documentHasState && userHasProfile && userIsValidator;

    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

    log.debug(methodName+" [documentHasState] = "+documentHasState);
    log.debug(methodName+" [userHasProfile] = "+userHasProfile);
    log.debug(methodName+" [userIsValidator] = "+userIsValidator);
    log.debug(methodName+" [visible] = "+visible);
    log.debug(methodName+" [documentLockedByOther] = "+documentLockedByOther);

    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }

  /**
   * Indique si le bouton de refus de publication est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isInvalidatePublicationButtonVisible(UserContext userContext, IDocument document)
  {
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
    String methodName = "isInvalidatePublicationButtonVisible";
    boolean documentHasState;
    boolean userHasProfile;
    boolean userIsValidator;
    boolean visible;
    boolean documentLockedByOther;

    List<String> states = new ArrayList<String>();
    states.add(getConstant("ITEM_CODE_DOC_STATUS_WAIT_PUB"));

    documentHasState = hasState(userContext, document, states);
    userHasProfile = UserUtils.hasProfile(userContext, getConstant("PROFILE_CODE_VALIDATOR"));
    userIsValidator = isValidatorUser(userContext, document);

    // Le bouton est visible si le document est dans l'état "en attente de publication", que l'utilisateur possède le profil valideur et qu'il soit le valideur du document.
    visible =  documentHasState && userHasProfile && userIsValidator;

    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

  log.debug(methodName+" [documentHasState] = "+documentHasState);
    log.debug(methodName+" [userHasProfile] = "+userHasProfile);
    log.debug(methodName+" [userIsValidator] = "+userIsValidator);
    log.debug(methodName+" [visible] = "+visible);
    log.debug(methodName+" [documentLockedByOther] = "+documentLockedByOther);

    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }

  /**
   * Indique si le bouton de demande de publication est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isAskForPublicationButtonVisible(UserContext userContext, IDocument document)
  {
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
    String methodName = "isAskForPublicationButtonVisible";
    boolean visible;
    boolean documentLockedByOther;
    boolean documentHasState;
    boolean userIsCreator;

   List<String> states = new ArrayList<String>();
  
    states.add(getConstant("ITEM_CODE_DOC_STATUS_CREATED"));
    states.add(getConstant("ITEM_CODE_DOC_STATUS_REF_PUB"));
  documentHasState = hasState(userContext, document, states);
    userIsCreator = isCreatorUser(userContext, document);

    // Le bouton est visible si le document est dans l'état "crée" et que l'utilisateur courant est le créateur.
    visible = documentHasState && userIsCreator;
    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

  log.debug(methodName+" [documentHasState] = "+documentHasState);
    log.debug(methodName+" [isCreatorUser] = "+userIsCreator);
  log.debug(methodName+" [visible] = "+visible);
    log.debug(methodName+" [documentLockedByOther] = "+documentLockedByOther);
  
    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }

  /**
   * Indique si le bouton de péremption est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isOutOfDateButtonVisible(UserContext userContext, IDocument document)
  {
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
  String methodName = "isOutOfDateButtonVisible";
    boolean visible;
    boolean documentLockedByOther;
    boolean documentHasState;
  boolean userHasProfile;
  boolean userIsValidatorOrCreator;
    List<String> states = new ArrayList<String>();
    states.add(getConstant("ITEM_CODE_DOC_STATUS_PUB"));
  
  documentHasState = hasState(userContext, document, states);
  userHasProfile = UserUtils.hasProfile(userContext, getConstant("PROFILE_CODE_VALIDATOR"));
  userIsValidatorOrCreator = isValidatorUser(userContext, document) || isCreatorUser(userContext, document);

    // Le bouton est visible si le document est dans l'état "publié", que l'utilisateur possède le profil valideur et est le valideur du document ou le créateur.
    visible = documentHasState && userHasProfile && userIsValidatorOrCreator;
    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

  log.debug(methodName+" [documentHasState] = "+documentHasState);
    log.debug(methodName+" [userHasProfile] = "+userHasProfile);
  log.debug(methodName+" [userIsValidatorOrCreator] = "+userIsValidatorOrCreator);
  log.debug(methodName+" [visible] = "+visible);
  log.debug(methodName+" [Retour] = "+documentLockedByOther);
  
    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }


  /**
   * Indique si le bouton de fin de collaboration est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isCollabEndButtonVisible(UserContext userContext, IDocument document)
  {
  String methodName = "isCollabEndButtonVisible";
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
  boolean visible;
  boolean documentLockedByOther;
  boolean documentHasState;
  boolean userIsCollaborator;

    List<String> states = new ArrayList<String>();
    states.add(getConstant("ITEM_CODE_DOC_STATUS_PUB"));

  documentHasState = hasState(userContext, document, states);
  userIsCollaborator = isCollaboratorUser(userContext, document);
    // Le bouton est visible si le document est dans l'état "en attente de publication" et que l'utilisateur possède le profil valideur.
    visible =  documentHasState && userIsCollaborator;
    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;
  
  log.debug(methodName+" [documentHasState] = "+documentHasState);
    log.debug(methodName+" [userIsCollaborator] = "+userIsCollaborator);
  log.debug(methodName+" [visible] = "+visible);
  log.debug(methodName+" [Retour] = "+documentLockedByOther);

    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }
  
    /**
   * Indique si le bouton d'effacement d'un document est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isDeleteButtonVisible(UserContext userContext, IDocument document)
  {
  String methodName = "isDeleteButtonVisible";
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
  boolean visible;
  boolean documentLockedByOther;
  boolean documentHasState;
  boolean userIsValidatorOrCreator;

    List<String> states = new ArrayList<String>();
  states.add(getConstant("ITEM_CODE_DOC_STATUS_PUB"));
  states.add(getConstant("ITEM_CODE_DOC_STATUS_OUT_OF_DATE"));

  documentHasState = hasState(userContext, document, states);
  userIsValidatorOrCreator = isValidatorUser(userContext, document) || isCreatorUser(userContext, document);
  
    // Le bouton est visible si le document est dans l'état "en attente de publication" et que l'utilisateur possède le profil valideur.
    visible =  documentHasState && userIsValidatorOrCreator;
    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;
  
  log.debug(methodName+" [documentHasState] = "+documentHasState);
    log.debug(methodName+" [userIsValidatorOrCreator] = "+userIsValidatorOrCreator);
  log.debug(methodName+" [visible] = "+visible);
  log.debug(methodName+" [Retour] = "+documentLockedByOther);

    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }

    /**
   * Indique si le bouton de restauration d'un document est visible.
   * @param userContext : Le contexte utilisateur.
   * @param document : Le document.
   * @return true si le bouton est visible.
   */
  public static ScriptResultValueDisplayRule isRestoreButtonVisible(UserContext userContext, IDocument document)
  {
  String methodName = "isRestoreButtonVisible";
    ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
  boolean visible;
  boolean documentLockedByOther;
  boolean documentHasState;

    List<String> states = new ArrayList<String>();

  states.add(getConstant("ITEM_CODE_DOC_STATUS_DELETED"));

  documentHasState = hasState(userContext, document, states);
  
    // Le bouton est visible si le document est dans l'état "en attente de publication" et que l'utilisateur possède le profil valideur.
    visible =  documentHasState;
    // Si le document est verrouillé par un autre utilisateur, le bouton ne s'affiche pas.
    documentLockedByOther = document.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;
  
  log.debug(methodName+" [documentHasState] = "+documentHasState);
  log.debug(methodName+" [visible] = "+visible);
  log.debug(methodName+" [Retour] = "+documentLockedByOther);

    result.setValid(visible && !documentLockedByOther);

    log.debug(methodName+" [Retour] = "+result.isValid());

    return result;
  }
  
  /**
   * Action de publication d'un document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doValidatePublication(UserContext userContext, IDocument document)
  {
  boolean ret = true;
  String methodName =  "doPublication";
  
  try
  {
    // Mise à jour du document avec le statut "refus de publication"
    String fieldCode = getConstant("FIELD_CODE_DOC_STATUS");
  log.debug(methodName+"  :  Passage du document dans l'état [Publié].");
    FieldUtils.setValue(document, fieldCode,getTermID(document, fieldCode, getConstant("ITEM_CODE_DOC_STATUS_PUB")));

    // Ajout d'un historique.
  log.debug(methodName+"  :  Ajout de l'historique.");
    addHistoForWorkflow(document, userContext, "Publication du document.");

  log.debug(methodName+"  :  Sauvegarde du document.");
    DocumentUtils.saveDocument(document);

    // Rafraichissement de la vue unitaire
  log.debug(methodName+"  :  Rafraichissement de la vue unitaire.");
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
  }
  catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }

  /**
   * Action de refus de publication d'un document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doInvalidatePublication(UserContext userContext, IDocument document)
  {
  boolean ret = true;
  String methodName =  "doInvalidatePublication";
  
  try
  {
    CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
  
  log.debug(methodName+"  :  Récupération du commentaire entré par l'utilisateur.");
    // Récupération du commentaire entré par l'utilisateur.
    String comment = customActionModel.getModalPanelModel().get("comment");
  
  log.debug(methodName+"  :  [Comment] = "+comment);

  log.debug(methodName+"  :  Ajout du commentaire au document");
    // Ajout du commentaire.
    addComment(document, userContext, comment);

  log.debug(methodName+"  :  Passage du document dans l'état [Publication refusée].");
    // Mise à jour du document avec le statut "publication refusée"
    String fieldCode = getConstant("FIELD_CODE_DOC_STATUS");
    FieldUtils.setValue(document, fieldCode,getTermID(document, fieldCode, getConstant("ITEM_CODE_DOC_STATUS_REF_PUB") ));

  log.debug(methodName+"  :  Ajout de l'historique.");
    // Ajout d'un historique
    addHistoForWorkflow(document, userContext, "Refus de Publication.");

  log.debug(methodName+"  :  Sauvegarde du document.");
    DocumentUtils.saveDocument(document);

    // Rafraichissement de la vue unitaire.
  log.debug(methodName+"  :  Rafraichissement de la vue unitaire.");
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
  }
  catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }

  /**
   * Action de demande de publication d'un document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doAskForPublication(UserContext userContext, IDocument document)
  {
  boolean ret = true;
  String methodName =  "doAskForPublication";
  
  try
  {
    CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

  log.debug(methodName+"  :  Passage du document dans l'état [Publication en attente].");
    // Mise à jour du document avec le statut "en attente de publication".
    String fieldCode = getConstant("FIELD_CODE_DOC_STATUS");
    FieldUtils.setValue(document, fieldCode,getTermID(document, fieldCode, getConstant("ITEM_CODE_DOC_STATUS_WAIT_PUB") ));

  log.debug(methodName+"  :  Ajout de l\'historique.");
    // Ajout d'un historique.
    addHistoForWorkflow(document, userContext, "Demande de publication.");

  log.debug(methodName+"  :  Sauvegarde du document.");
    DocumentUtils.saveDocument(document);

  log.debug(methodName+"  :  Rafraichissement de la vue unitaire.");
    // Rafraichissement de la vue unitaire.
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
  }
  catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }

  /**
   * Action de péremption d'un document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doOutOfDate(UserContext userContext, IDocument document)
  {
  boolean ret = true;
  String methodName =  "doOutOfDate";
  
  try
  {
    CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
  log.debug(methodName+" :  Passage du document dans l\'état [Périmé].");
    // Mise à jour du document avec le statut "périmé".
    String fieldCode = getConstant("FIELD_CODE_DOC_STATUS");
    FieldUtils.setValue(document, fieldCode, getTermID(document, fieldCode, getConstant("ITEM_CODE_DOC_STATUS_OUT_OF_DATE") ));

  log.debug(methodName+" :  Ajout de l'historique.");
    // Ajout d'un historique.
    addHistoForWorkflow(document, userContext, "Péremption du document.");

  log.debug(methodName+" :  Sauvegarde du document.");
    DocumentUtils.saveDocument(document);

  log.debug(methodName+" :  Rafraichissement de la vue unitaire.");
    // Rafraichissement de la vue unitaire.
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
  }
  catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }

  /**
   * Action de fin de collaboration d'un document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doCollabEnd(UserContext userContext, IDocument document)
  {
  boolean ret = true;
  String methodName =  "doCollabEnd";
  try
  {
    if(isCollaboratorUser(userContext, document))
    {
  
      CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
    
    log.debug(methodName+" :  Récupération du commentaire entré par l'utilisateur");
      // Récupération du commentaire entré par l'utilisateur.
      String comment = customActionModel.getModalPanelModel().get("comment");
    log.debug(methodName+" :  [Comment] = "+comment);
    
    log.debug(methodName+" :  Ajout du commentaire au document");
      // Ajout du commentaire.
      addComment(document, userContext, comment);

    removeUserFromCollaborators(userContext, document);
    
    log.debug(methodName+" :  Ajout de l'historique.");
      // Ajout d'un historique.
      addHistoForWorkflow(document, userContext, "Fin de collaboration.");

    log.debug(methodName+" :  Sauvegarde du document.");
      DocumentUtils.saveDocument(document);

    log.debug(methodName+" :  Rafraichissement de la vue unitaire.");
      // Rafraichissement de la vue unitaire.
      Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
    }
    else
    {
      log.debug(methodName+" : L'utilisateur connecté ne fait pas parti de la liste des collaborateurs.");
    }
  }
  catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }
   /**
   * Action d'effacement du document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doDelete(UserContext userContext, IDocument document)
  {
    boolean ret = true;
    String methodName =  "doDelete";
      try{
    removeAllUsersFromCollaborators(userContext, document);
    
    
     // Mise à jour du document avec le statut "effacé".
    String fieldCode = getConstant("FIELD_CODE_DOC_STATUS");
    FieldUtils.setValue(document, fieldCode, getTermID(document, fieldCode, getConstant("ITEM_CODE_DOC_STATUS_DELETED") ));
    
    log.debug(methodName+" :  Ajout de l'historique.");
      // Ajout d'un historique.
      addHistoForWorkflow(document, userContext, "Effacement du document.");

    log.debug(methodName+" :  Sauvegarde du document.");
      DocumentUtils.saveDocument(document);

    log.debug(methodName+" :  Rafraichissement de la vue unitaire.");
      // Rafraichissement de la vue unitaire.
      Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
    }
    catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }

   /**
   * Action de restauration du document.
   * @param userContext : Le contexte de l'utilisateur courant.
   * @param document : Le document.
   * @return true auncune exception n'est générée.
   */
  public static boolean doRestore(UserContext userContext, IDocument document)
  {
    boolean ret = true;
    String methodName =  "doRestore";
    
    try{
     // Mise à jour du document avec le statut "publié".
    String fieldCode = getConstant("FIELD_CODE_DOC_STATUS");
    FieldUtils.setValue(document, fieldCode, getTermID(document, fieldCode, getConstant("ITEM_CODE_DOC_STATUS_PUB") ));
    
    log.debug(methodName+" :  Ajout de l'historique.");
      // Ajout d'un historique.
      addHistoForWorkflow(document, userContext, "Restauration du document.");

    log.debug(methodName+" :  Sauvegarde du document.");
      DocumentUtils.saveDocument(document);

    log.debug(methodName+" :  Rafraichissement de la vue unitaire.");
      // Rafraichissement de la vue unitaire.
      Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
    }
    catch(Exception e)
    {
    ret = false;
    }
    finally
    {
    return ret;
    }
  }
}