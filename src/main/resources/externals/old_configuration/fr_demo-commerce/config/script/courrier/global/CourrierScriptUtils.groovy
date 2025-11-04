import groovy.util.ScriptException

import java.io.File
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*

import org.apache.commons.lang.StringUtils

import com.digitech.airs3dossiers.constantes.DAirsDossierStringConstants
import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson;
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backing.DefaultSharingModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.service.Constants
import com.digitech.dossier.common.service.IAuditService
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.service.ICounter
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.dossier.common.utils.DateUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.service.impl.ScriptMgr
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.admin.OrganizationAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException

/**
 * Utility methods for courrier light
 */
class CourrierScriptUtils {
  private static File constantsFile = null;
  private static Properties properties = new Properties();
  static {
    constantsFile = new File(DossierCoreContext.getApplicationPath() + File.separator + ScriptMgr.SCRIPT_RELATIVE_PATH + File.separator + "courrier" + File.separator + "global"  +File.separator + "constants.properties");
    constantsFile.withInputStream {  stream ->
      properties.load(stream)
    }
  }

  public static String getConstant(String code) {
    if (!properties.containsKey(code)) {
      throw new ScriptException("Key '" + code + "' not found in file " + constantsFile);
    }
    return properties.getProperty(code);
  }


  /**
   * @return IAuthorityList the Authority List
   */
  public static IAuthorityList getAuthorityListService() {
    return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
  }
  
  /**
  * Checks if the courrier field "T_ETAT_COURRIER" value is one of possible states.
  * @param theDocument the document
  * @param states the possible states
  * @return true if the courrier field "T_ETAT_COURRIER" value is one of possible states
  */
 public static boolean hasState(IDocument theDocument, List<String> states){
   return hasState(null, theDocument, states);
 }

  /**
   * Checks if the courrier field "T_ETAT_COURRIER" value is one of possible states.
   * @param userContext the user context
   * @param theDocument the document
   * @param states the possible states
   * @return true if the courrier field "T_ETAT_COURRIER" value is one of possible states
   */
  public static boolean hasState(UserContext userContext, IDocument theDocument, List<String> states){
    String fieldCodeCourrierState = getConstant("FIELD_CODE_T_ETAT_COURRIER");
    Integer stateId = FieldUtils.getValue(theDocument, fieldCodeCourrierState);
    if (stateId != null){
      Term term = ((IAuthorityList)getAuthorityListService()).getTerm(stateId);
      if (term == null) {
        throw new IllegalStateException("No term with ID '" + stateId + "' found for authority list " + fieldCodeCourrierState);
      }
      return states.contains(term.getCode());
    }
    return false;
  }
  
  /**
   * Defines if the document has an accepted visa.
   * @param userContext the user context
   * @param theDocument the document
   * @return
   */
  public static boolean hasVisaAccepted(UserContext userContext, IDocument theDocument) {
    String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
    Integer visaState = FieldUtils.getValue(theDocument, etatVisaFieldCode);
    return CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_ACCEPTE")).equals(visaState);
  }

  /**
   * Checks if the user context is the owner of the document (base on field U_PROPRIETAIRE).
   * @param userContext the user context
   * @param theDocument the document
   * @return true if the user context is the owner of the document
   */
  public static boolean isOwnerUser(UserContext userContext, IDocument theDocument) {
    Integer docUserOwner = null;
    Integer docOrgOwner = null
    
    String fieldCodeCourrierOwnerUser = getConstant("FIELD_CODE_U_PROPRIETAIRE");
    docUserOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerUser);
    if (docUserOwner == null) {
      String fieldCodeCourrierOwnerGroup = getConstant("FIELD_CODE_O_PROPRIETAIRE");
      try {
        docOrgOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerGroup);
      }
      catch(IllegalStateException ise) {
      }
    }
    return (docUserOwner == null && docOrgOwner != null &&  UserUtils.isInOrganization(userContext, docOrgOwner, true)) || (docUserOwner != null && docUserOwner.equals(userContext.getUser().getId()));
  }

  /**
   * Checks if the user context is the validator of the document (base on field U_VALIDEUR).
   * @param userContext the user context
   * @param theDocument the document
   * @return true if the user context is the validator of the document
   */
  public static boolean isValidatorUser(UserContext userContext, IDocument theDocument) {
    String fieldCodeCourrierValidatorUser = getConstant("FIELD_CODE_U_VALIDEUR");
    Integer docValidator = null;
    try {
      docValidator = FieldUtils.getValue(theDocument, fieldCodeCourrierValidatorUser);
    }
    catch(IllegalStateException ise) {
    }
    return docValidator != null && docValidator.equals(userContext.getUser().getId());
  }

  /**
   * Checks if the user context is the viewer of the document (base on field U_VISEUR).
   * @param userContext the user context
   * @param theDocument the document
   * @return true if the user context is the viewer of the document
   */
  public static boolean isSignerUser(UserContext userContext, IDocument theDocument) {
    String fieldCodeCourrierViewerUser = getConstant("FIELD_CODE_U_VISEUR");
    Integer docViewer = FieldUtils.getValue(theDocument, fieldCodeCourrierViewerUser);
    return docViewer != null && docViewer.equals(userContext.getUser().getId());
  }

  /**
   * Checks if the user context can act on the document (base on field U_PROPRIETAIRE, U_VALIDEUR and U_VISEUR).
   * @param userContext the user context
   * @param theDocument the document
   * @return true if the user context is the owner of the document
   */
  public static boolean canActOn(UserContext userContext, IDocument theDocument) {
    return isOwnerUser(userContext, theDocument) || isValidatorUser(userContext, theDocument) || isSignerUser(userContext, theDocument);
  }

  /**
   * Generate the sequence of number Chrono
   * @param nextVal integer the number of sequence
   * @param sequenceCompleteSize the size of the sequence completed
   * @param provisory defines if the numero chrono is provisory or not
   * @return sequenceComplete the sequence completed with some "0"
   */
  public static String generateNumChrono(Integer service, UserContext userContext, boolean provisory) throws IdentificationException, ServerException {
    if (service == null) {
      return null;
    }
    
    ICounter counterService = ((ICounter) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_COUNTER_MGR));
    String counterCode = getServiceCode(service, userContext);
    if (Boolean.valueOf(getConstant("NUM_CHRONO_UNIQUE_BY_YEAR")).booleanValue()) {
      counterCode = counterCode  + "_" + Calendar.getInstance().get(Calendar.YEAR);
    }
    Integer nextValue = null;
    if (provisory) {
      nextValue = Integer.valueOf(counterService.getCurrentValue(counterCode).intValue());
    }
    else {
      nextValue = counterService.getNextValue(counterCode);
    }
    String counter = generateSequenceComplete(nextValue, Integer.parseInt(getConstant("NUM_CHRONO_COUNTER_SIZE")));
    String serviceLabel = getServiceLabelFromId(service, userContext);
    String numChrono = generateNumChrono(serviceLabel, counter, userContext);
    return numChrono;
  }

  /**
   * Generate the due date
   * @param document the current Document
   * @param rule the wished to compute due date
   * @return dueDate the due date
   */
  public static Date computeDueDate(IDocument document, String rule) {
    Date dueDate = null;
    if(rule != null && rule.length() >= 3) {
      String[] tabRules = getTabRules(rule);
      if(tabRules != null) {
        Date dateRef = getDateRef(document, tabRules[0]);
        if(dateRef != null) {
          dueDate = DateUtils.computeDate(dateRef, tabRules[1], Integer.parseInt(tabRules[2]));
        }
      }
    }
    return dueDate;
  }

  /**
   * Saves a document.
   * @param userContext the user context
   * @param document the document to save
   */
  public static void saveDocument(UserContext userContext, IDocument document) {
    saveDocument(userContext, document, null);
  }

  /**
   * Saves a document and adds a specific event if enabled.
   * @param userContext the user context
   * @param document the document to save
   * @param eventType the event type to audit
   * @param eventComment the event comment
   */
  public static void saveDocument(UserContext userContext, IDocument document, CourrierAdvancedAuditType eventType) {
    saveDocument(userContext, document, eventType, false);
  }
  
  /**
  * Saves a document and adds a specific event if enabled.
  * @param userContext the user context
  * @param document the document to save
  * @param eventType the event type to audit
  * @param eventComment the event comment
  * @param unlockDocument if true, the document is unlocked after saving it
  */
 public static void saveDocument(UserContext userContext, IDocument document, CourrierAdvancedAuditType eventType, boolean unlockDocument) {
   boolean eventEnabled = false;
   if (eventType != null) {
     CourrierOrga courrierOrga = DossierCoreContext.getCourrierInfos().getOrganizationOrDefault(userContext.getCurrentOrgId());
     if (courrierOrga != null) {
       eventEnabled = courrierOrga.getAdvancedAudits().contains(eventType);
     }
   }

   if (eventEnabled) {
     DocumentUtils.saveDocument(document, eventType.name(), null);
   }
   else {
     DocumentUtils.saveDocument(document);
   }
   
   if (unlockDocument) {
     DocumentUtils.unlockDocument(document);
   }
 }

  /**
   * Saves a document specific event if enabled.
   * @param userContext the user context
   * @param document the document
   * @param eventType the event type to audit
   * @param eventComment the event comment
   */
  public static void saveEvent(UserContext userContext, IDocument document, CourrierAdvancedAuditType eventType, String eventComment) {
    boolean eventEnabled = false;
    if (eventType != null) {
      CourrierOrga courrierOrga = DossierCoreContext.getCourrierInfos().getOrganizationOrDefault(userContext.getCurrentOrgId());
      if (courrierOrga != null) {
        eventEnabled = courrierOrga.getAdvancedAudits().contains(eventType);
      }
    }

    if (eventEnabled) {
      IAuditService auditService = (IAuditService) ServiceManager.getInstance().getService(
          Constants.SERVICE_AUDIT_DOC_MGR);
      auditService.addDocumentEvent(userContext, document, eventType, eventComment);
    }
  }
  
  /**
  * Gets a term ID.
  * @param fieldCode the field code
  * @param termCode the term code
  * @return the term ID
  * @throws IdentificationException
  * @throws ServerException
  */
 public static Integer getTermID(String fieldCode, String termCode)
 throws IdentificationException, ServerException {
   return getTermID(null, fieldCode, termCode);
 }

  /**
   * Gets a term ID.
   * @param theDocument the document
   * @param fieldCode the field code
   * @param termCode the term code
   * @return the term ID
   * @throws IdentificationException
   * @throws ServerException
   */
  public static Integer getTermID(IDocument theDocument, String fieldCode, String termCode)
  throws IdentificationException, ServerException {
    List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(UserUtils.getAdminUserContext().getJeton(), fieldCode);
    for(Term term : termList) {
      if(term.getCode().equals(termCode)) {
        return term.getId();
      }
    }
    return -1;
  }

  /**
   * Defines if a mail template exists for the document.
   * @param usrContext the user context
   * @param theDocument the document
   * @return true if a mail template exists for the document
   */
  public static boolean isMailTemplateDefined(UserContext usrContext, IDocument theDocument) {
    IField courrierTypeField = theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_T_TYPE"));
    if (courrierTypeField != null) {
      Term term = getAuthorityListService().getTerm((Integer) courrierTypeField.getValue());
      if (term != null) {
        com.digitech.dossier.common.model.backend.params.CourrierType courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(),term.getCode(), FlowType.IN);
        if (courrierType != null) {
          return !courrierType.getCourrierTemplates().isEmpty();
        }
      }
    }
    return false;
  }
  
  /**
   * Gets the sharing model to apply.
   * @param publicc is the element public ?
   * @param theDocument the document
   * @return the sharing model
   */
  public static AbstractSharingModel getSharingModel(Boolean publicc, IDocument theDocument) {
    DefaultSharingModel sharingModel = null;
    if (publicc != null) {
      sharingModel = new DefaultSharingModel(publicc.booleanValue());
      
      if (!publicc.booleanValue()) {
        List<String> states = new ArrayList<String>()
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_REJECT"));
        if (hasState(null, theDocument, states)) {
          // Courrier rejected during validation phase
          Integer docUserCreator = FieldUtils.getValue(theDocument, getConstant("FIELD_CODE_U_CREAT"));
          if (docUserCreator != null) {
            sharingModel.setUsers(Arrays.asList(getUserMgr().getUser(docUserCreator)));
          }
        }
        else {
          String fieldCodeCourrierOwnerUser = getConstant("FIELD_CODE_U_PROPRIETAIRE");
          Integer docUserOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerUser);
          if (docUserOwner != null) {
            sharingModel.setUsers(Arrays.asList(getUserMgr().getUser(docUserOwner)));
          }
          else {
            String fieldCodeCourrierOwnerGroup = getConstant("FIELD_CODE_O_PROPRIETAIRE");
            Integer docOrgOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerGroup);
            sharingModel.setOrgs(Arrays.asList(getServerMgr().getOrganization(DossierCoreContext.getAdminJeton(), docOrgOwner)));
          }
        }
      }
    }
    return sharingModel;
  }
  
 /**
  * Adds a response.
  * @param userContext the user context
  * @param theDocument the parent document
  * @param sendNotification if true, the mail notification is send
  */
  public static void addResponse(UserContext usrContext, IDocument theDocument, boolean sendNotification) {    
    // Mise à jour de l'état du document
    String fieldCode = getConstant("FIELD_CODE_T_ETAT_COURRIER");

    FieldUtils.setValue(theDocument, fieldCode, getTermID(theDocument, fieldCode, getConstant("STATE_CODE_REPONDU") ));
    saveDocument(usrContext, theDocument);
    
    // Adds the response type
    addResponseType(usrContext, theDocument);
    
    if (sendNotification && Boolean.TRUE.equals(Boolean.valueOf(getConstant("MAIL_NOTIFICATION_ENABLED")))) {
      IUser userMgr = (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
      File templateFile = new File(CourrierUtils.getCourrierTemplateFolderPath(), "changementEtat.htm");
    
      // Notifier le viseur
      Map<String, Object> customPropertyMap = new HashMap<String, Object>();
      customPropertyMap.put("state", CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_SIGN);
      User user = userMgr.getUser((Integer)theDocument.getField(getConstant("FIELD_CODE_U_VISEUR")).getValue());
      ApplicationUtils.sendMail(usrContext, Arrays.asList(theDocument), templateFile, new ReportPerson(usrContext, user), BundleUtils.getTranslation("mail_subject_courrier_state_to_sign"), customPropertyMap);
    }
  }
  
  public static boolean isAttachmentOutExisting(UserContext usrContext, IDocument theDocument) {
    for (IAttachment attachment : theDocument.getAttachments(usrContext)) {
      if (getConstant("ATTACHMENT_TYPE_COURRIER_OUT").equals(attachment.getType())) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Adds a response type.
   * @param userContext the user context
   * @param theDocument the parent document
   */
  private static void addResponseType(UserContext userContext, IDocument theDocument) {
    if (getConstant("CONTENT_TYPE_COU_COURRIER_OUT").equals(theDocument.getDomain().getCode())) {
      return;
    }
    Map<String, String> mapCodeFieldValues = new HashMap<String, String>();
    mapCodeFieldValues.put(getConstant("FIELD_CODE_T_REPONSE"), getTermID(null, getConstant("FIELD_CODE_T_REPONSE"), getConstant("TYPE_RESPONSE_CODE_COURRIER")).toString());
    
    DateFormat dateFormat = new SimpleDateFormat(com.digitech.dossier.common.model.backend.Constants.FORMAT_DATE_TIME);
    mapCodeFieldValues.put(getConstant("FIELD_CODE_D_REPONSE"), dateFormat.format(new Date()));
    
    mapCodeFieldValues.put(getConstant("FIELD_CODE_U_REPONSE"), userContext.getUserId().toString());
    mapCodeFieldValues.put(getConstant("FIELD_CODE_C_COMPLEMENT"), BundleUtils.getTranslation("title_responseGenerated"));
    
    IDocument responseDocument = getDocumentMgr().createDocument(userContext, getConstant("CONTENT_TYPE_COU_REPONSE"), 1, mapCodeFieldValues);
    theDocument.getAirsDocument().getInnerDocument().addDocumentLink(responseDocument.getAirsDocument().getInnerDocument(), getConstant("LINK_COURRIER_REPONSE"));
  }

  /**
   * Generate the number chrono
   * @param service the service code
   * @param counter the counter code
   * @param userContext the user context
   * @return numChrono the number chrono
   */
  private static String generateNumChrono(String  service, String counter, UserContext userContext) throws IdentificationException, ServerException {
    String numChrono = null;
    if(service != null && counter != null) {
      String numChronoPattern = getConstant("NUM_CHRONO_PATTERN");
      String numChronoDateFormat = getConstant("NUM_CHRONO_DATE_FORMAT");

      String date = getFormattedDate(numChronoDateFormat);
      if (numChronoPattern != null){
        numChrono = numChronoPattern.replace("<DATE>", date);
        numChrono = numChrono.replace("<SERVICE>", service);
        numChrono = numChrono.replace("<COUNTER>", counter);
      }else {
        String message = "NumeroChrono generation impossible due to misconfiguration of NUM_CHRONO_PATTERN property";
        throw new ScriptException(message);
      }
    } else {
      String message = "NumeroChrono generation impossible due to misconfiguration of NumeroChrono properties";
      throw new ScriptException(message);
    }
    return numChrono;
  }

  /**
   * Generate the sequence of number Chrono
   * @param nextVal integer the number of sequence
   * @param sequenceCompleteSize the size of the sequence completed
   * @return sequenceComplete the sequence completed with some "0"
   */
  private static String generateSequenceComplete(Integer nextVal, int sequenceCompleteSize) {
    String sequenceComplete = null;
    String nextValue = String.valueOf(nextVal);
    sequenceComplete = StringUtils.leftPad(nextValue, sequenceCompleteSize, "0");
    return sequenceComplete;
  }

  /**
   * Generate the service label
   * @param orgId the organization id
   * @param userContext the user context
   * @return service the service code or service id label
   */
  private static String getServiceLabelFromId(Integer orgId, UserContext userContext) throws IdentificationException, ServerException {
    String service = null;
    String serviceFormat = getConstant("NUM_CHRONO_SERVICE_FORMAT");
    if("CODE".compareTo(serviceFormat) == 0) {
      service = getServiceCode(orgId, userContext);
    }
    else if("ID".compareTo(serviceFormat) == 0) {
      service = String.valueOf(orgId);
    }
    if (service == null) {
      String message = "NumeroChrono generation impossible due to misconfiguration of NUM_CHRONO_SERVICE_FORMAT property";
      throw new ScriptException(message);
    }
    return service;
  }

  /**
   * @param orgId the organization id
   * @param userContext the user context
   * @return service the service code
   */
  private static String getServiceCode(Integer orgId, UserContext userContext) throws IdentificationException, ServerException {
    String service = null;
    OrganizationAdmin orgAdm = OrganizationsManager.load(userContext.getJeton(), orgId);
    if (orgAdm != null){
      service = orgAdm.getCode();
    }
    return service;
  }

  /**
   * Generate the service label
   * @param numChronoDateFormat the wished pattern of the date
   * @return dateFormated the formated date
   */
  private static String getFormattedDate(String numChronoDateFormat) {
    String dateFormated = null;
    if (numChronoDateFormat != null){
      dateFormated = new SimpleDateFormat(numChronoDateFormat).format(new Date());
    } else {
      String message = "NumeroChrono generation impossible due to misconfiguration of NUM_CHRONO_DATE_FORMAT property";
      throw new ScriptException(message);
    }
    return dateFormated;
  }

  /**
   * Generate the table completed with the rule parsing
   * @param rule the rule to parse
   * @return tabRule the table rule
   */
  private static String[] getTabRules(String rule) {
    String operator = "+";
    String[] tabRules = null;
    String[] tabSplit = StringUtils.split(rule, '+');
    if(tabSplit.length != 2) {
      tabSplit = StringUtils.split(rule, '-');
      operator = "-";
    }
    if(tabSplit != null && tabSplit.length == 2) {
      tabRules = new String[3];
      if(tabSplit[0].length() >= 1 && tabSplit[1].length() >= 1) {
        tabRules[0] = tabSplit[0];
        tabRules[1] = operator;
        tabRules[2] = tabSplit[1];
      }
    }
    return tabRules;
  }

  /**
   * Generate the due date
   * @param document the current Document
   * @param code the code of the wished field (type Date)
   * @return dateRef the reference date
   */
  private static Date getDateRef(IDocument document, String code) {
    Date dateRef = null;
    if(DAirsDossierStringConstants.SEARCH_VAR_TODAY.compareTo(code) == 0) {
      dateRef = new Date();
    }
    else if(document != null) {
      IField fieldDateRef = document.getField(code);
      if(fieldDateRef != null) {
        int typeField = fieldDateRef.getType();
        if(typeField != IField.TYPE_DATE) throw new java.lang.IllegalStateException("Invalid type for field " + code + " (type Date expected)");
        dateRef = (Date) fieldDateRef.getValue();
      }
      else
        throw new java.lang.IllegalStateException("Field " + code + " not found");
    }
    return dateRef;
  }
  
  private static IUser getUserMgr() {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }

  private static IServer getServerMgr() {
    return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
  }
  
  private static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
    return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
  }
}