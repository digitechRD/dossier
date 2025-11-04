import com.digitech.dossier.common.controller.NavigationController

import java.util.Map.Entry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.digitech.airs3dossiers.airs.DocumentFactory;
import com.digitech.airs3dossiers.constantes.DAirsDossierStringConstants;
import com.digitech.courrier.common.controller.VisaController;
import com.digitech.courrier.common.model.VisaModel;
import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType;
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType;
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.application.NavigationHandlerImpl;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.airs.IFieldValue;
import com.digitech.dossier.common.model.backend.airs.IProfile;
import com.digitech.dossier.common.model.backend.params.CourrierOrga;
import com.digitech.dossier.common.model.backend.params.UpdateField;
import com.digitech.dossier.common.model.backend.MessagesModel;
import com.digitech.dossier.common.model.backend.params.CourrierType;
import com.digitech.dossier.common.model.backend.report.value.airs.ReportOrganization;
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType;
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel;
import com.digitech.dossier.common.model.backing.DefaultSharingModel;
import com.digitech.dossier.common.model.backing.search.SearchResultTableRowModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.service.Constants;
import com.digitech.dossier.common.service.IAuditService;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.service.ICounter;
import com.digitech.dossier.common.service.IRight;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.model.backend.pop.IPerson;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.dossier.common.utils.DateUtils;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.script.service.impl.ScriptMgr;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentActions.Action;
import com.digitech.dossier.script.model.impl.result.AbstractScriptResultValue;
import com.digitech.jcorbairs.Term;
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.Organization;
import com.digitech.jcorbairs.admin.OrganizationAdmin;
import com.digitech.jcorbairs.admin.OrganizationsManager;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.admin.ProfilAdmin;

/**
 * Utility methods for courrier light
 */
class CourrierScriptUtils {

    private static final String FIELD_NUMERO_CHRONO = "N_CHRONO";
	private final static Logger log = LoggerFactory.getLogger("com.digitech.dossier.script.CourrierScriptUtils");

    private static File constantsFile = null;
    private static Properties properties = new Properties();
    static {
        constantsFile = new File(DossierCoreContext.getApplicationPath() + File.separator + ScriptMgr.SCRIPT_RELATIVE_PATH + File.separator + "courrier" + File.separator + "global" + File.separator + "constants.properties");
        constantsFile.withInputStream { stream ->
            properties.load(stream)
        }
    }

    public static String getConstant(String code) {
        if (!properties.containsKey(code)) {
            throw new ScriptException("Key '" + code + "' not found in file " + constantsFile);
        }
        return properties.getProperty(code);
    }

    public static String getConstant(String code, String defaultValue) {
        if (!properties.containsKey(code)) {
            return defaultValue;
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
    public static boolean hasState(IDocument theDocument, List<String> states) {
        return hasState(null, theDocument, states);
    }

    /**
     * Checks if the courrier field "T_ETAT_COURRIER" value is one of possible states.
     * @param userContext the user context
     * @param theDocument the document
     * @param states the possible states
     * @return true if the courrier field "T_ETAT_COURRIER" value is one of possible states
     */
    public static boolean hasState(UserContext userContext, IDocument theDocument, List<String> states) {
        String fieldCodeCourrierState = getConstant("FIELD_CODE_T_ETAT_COURRIER");
        Integer stateId = (Integer) FieldUtils.getValue(theDocument, fieldCodeCourrierState);
        if (stateId != null) {
            Term term = ((IAuthorityList) getAuthorityListService()).getTerm(stateId);
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
        String etatVisaFieldCode = getConstant("FIELD_CODE_T_ETAT_VISA");
        Integer visaState = (Integer) FieldUtils.getValue(theDocument, etatVisaFieldCode);
        return getTermID(etatVisaFieldCode, getConstant("STATE_CODE_VISA_ACCEPTE")).equals(visaState);
    }

    /**
     * Checks if the user context is the owner of the document (base on field U_PROPRIETAIRE).
     * @param userContext the user context
     * @param theDocument the document
     * @return true if the user context is the owner of the document
     */
    public static boolean isOwnerUser(UserContext userContext, IDocument theDocument) {
        Integer docOrgOwner = null

        String fieldCodeCourrierOwnerUser = getConstant("FIELD_CODE_U_PROPRIETAIRE");
        Integer docUserOwner = (Integer) FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerUser);
        if (docUserOwner == null) {
            String fieldCodeCourrierOwnerGroup = getConstant("FIELD_CODE_O_PROPRIETAIRE");
            try {
                docOrgOwner = (Integer) FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerGroup);
            }
            catch (IllegalStateException ise) {

            }
        }
        return (docUserOwner == null && docOrgOwner != null && UserUtils.isInOrganization(userContext, docOrgOwner, true)) || (docUserOwner != null && docUserOwner.equals(userContext.getUser().getId()));
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
        catch (IllegalStateException ise) {
        }
        return docValidator != null && docValidator.equals(userContext.getUser().getId());
    }

    /**
     * Checks if the current validator is the last one (base on both fields U_VALIDEUR & U_VALIDEURS).
     * @param theDocument the document
     * @return true if the current is the last
     */
    public static boolean isLastValidator(IDocument theDocument, String currentFieldCode, String listFieldCode) {
        Integer docValidator = null;
        List<Integer> docValidators = null;
        try {
            docValidator = FieldUtils.getValue(theDocument, currentFieldCode);
            docValidators = FieldUtils.getValues(theDocument, listFieldCode);
        }
        catch (IllegalStateException ise) {
        }

        return docValidators == null || docValidators.size() == 0 || docValidators.get(docValidators.size() - 1).equals(docValidator);
    }

    /**
     * Checks if the current validator is the last one (base on both fields U_VALIDEUR & U_VALIDEURS).
     * @param theDocument the document
     * @return true if the current is the last
     */
    public static Integer getNextValidator(IDocument theDocument, String currentFieldCode, String listFieldCode) {
        try {
            Integer currentValidator = FieldUtils.getValue(theDocument, currentFieldCode);
            List<Integer> docValidators = FieldUtils.getValues(theDocument, listFieldCode);
            if (docValidators != null) {
                int i = 0;
                for (Integer valid : docValidators) {
                    if (valid.equals(currentValidator)) {
                        if (docValidators.size() > (i + 1)) {
                            return docValidators.get(i + 1);
                        }
                    }

                    i++;
                }
            }
        }
        catch (IllegalStateException ise) {
        }

        return null;
    }

    /**
     * Fix #13171
     * Return the inital validator if a validator refuse the validation
     * @param theDocument the document
     * @return
     */
    public static Integer getInitValidator(IDocument theDocument, String listFieldCode) {
        try {
            List<Integer> docValidators = FieldUtils.getValues(theDocument, listFieldCode);
            if ((docValidators != null) && (!docValidators.isEmpty())) {
                return docValidators.get(0);
            }
        }
        catch (IllegalStateException ise) {
        }
        return null;
    }

    /**
     * Checks if the user context is the viewer of the document (base on field U_VISEUR).
     * @param userContext the user context
     * @param theDocument the document
     * @return true if the user context is the viewer of the document
     */
    public static boolean isSignerUser(UserContext userContext, IDocument theDocument) {
        Integer docViewer = FieldUtils.getValue(theDocument, getConstant("FIELD_CODE_U_VISEUR"));
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
            counterCode = counterCode + "_" + Calendar.getInstance().get(Calendar.YEAR);
        }
        Integer nextValue = null;
        if (provisory) {
            nextValue = counterService.getCurrentValue(counterCode);
        } else {
            nextValue = counterService.getNextValue(counterCode);
        }

        if (nextValue == null) {
            return null;
        }

        String counter = generateSequenceComplete(nextValue, Integer.parseInt(getConstant("NUM_CHRONO_COUNTER_SIZE")));
        String serviceLabel = getServiceLabelFromId(service, userContext);

        User user = userContext.getUser();
        return _generateNumChrono(serviceLabel, counter, getTrigramme(user), userContext);
    }

    /**
     * Generate the number chrono
     * @param service the service code
     * @param counter the counter code
     * @param userContext the user context
     * @return numChrono the number chrono
     */
    private
    static String _generateNumChrono(String service, String counter, String idUser, UserContext userContext) throws IdentificationException, ServerException {
        String numChrono = null;
        if (service != null && counter != null) {
            String numChronoPattern = getConstant("NUM_CHRONO_PATTERN");
            String numChronoDateFormat = getConstant("NUM_CHRONO_DATE_FORMAT");

            String date = getFormattedDate(numChronoDateFormat);
            if (numChronoPattern != null) {
                numChrono = numChronoPattern.replace("<DATE>", date);
                numChrono = numChrono.replace("<SERVICE>", service);
                numChrono = numChrono.replace("<COUNTER>", counter);
                numChrono = numChrono.replace("<IDTRIG>", idUser);
            } else {
                String message = "NumeroChrono generation impossible due to misconfiguration of NUM_CHRONO_PATTERN property";
                throw new ScriptException(message);
            }
        } else {
            String message = "NumeroChrono generation impossible due to misconfiguration of NumeroChrono properties";
            throw new ScriptException(message);
        }
        return numChrono;
    }

    private static String getTrigramme(User user) {
        String trigramme = user.getTrigram();
        if (trigramme == null || trigramme.isEmpty()) {
            int length = 3;
            String login = user.getLogin();
            login = login.replaceAll("[^a-zA-Z0-9]", "");
            if (login.length() < length) {
                length = login.length();
            }
            trigramme = login.substring(0, length);
        }
        trigramme = trigramme.toUpperCase();
        return trigramme;
    }

    public static void alertUserIfNumChronoChanged(String numChrono, IDocument document) {
        if (!numChrono.equals(document.getField(FIELD_NUMERO_CHRONO).getValue())) {
            Utils.getDocumentCreationController().getModel().setOldChronoNumber(document.getField(FIELD_NUMERO_CHRONO).getValue());
            Utils.getDocumentCreationController().getModel().setNewChronoNumber(numChrono);
            Utils.getDocumentCreationController().getModel().setNumChronoChanged(true);
        }
    }

    /**
     * Compare current (form) and AIRS (saved) values of a multivalued field
     * @param fieldToCheck field to check
     * @return true si field value(s) changed
     */
    public static boolean isMultivaluedFieldChanged(IField fieldToCheck) {
        List<Integer> values = fieldToCheck.getValues();
        StringBuffer newValues = new StringBuffer();
        if (values != null && values.size() > 0) {
            String sep = fieldToCheck.getAirsField().getValueSeperator();

            Iterator<Integer> it = values.iterator();
            while (it.hasNext()) {
                newValues.append(it.next());
                if (it.hasNext()) {
                    newValues.append(sep);
                }
            }
        }

        //log.debug("field=[" + fieldToCheck.getCode() + "] newValues=[" + newValues.toString() + "] oldValues=[" + fieldToCheck.getAirsValue() + "]");
        return !newValues.toString().equals(fieldToCheck.getAirsValue());
    }

    /**
     * Generate the due date
     * @param document the current Document
     * @param rule the wished to compute due date
     * @return dueDate the due date
     */
    public static Date computeDueDate(IDocument document, String rule) {
        Date dueDate = null;
        if (rule != null && rule.length() >= 3) {
            String[] tabRules = getTabRules(rule);
            if (tabRules != null) {
                Date dateRef = getDateRef(document, tabRules[0]);
                if (dateRef != null) {
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
        } else {
           // DocumentUtils.saveDocument(document);
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
        List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(fieldCode);
        for (Term term : termList) {
            if (term.getCode().equals(termCode)) {
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
        IField courrierTypeField = theDocument.getField(getConstant("FIELD_CODE_T_TYPE"));
        if (courrierTypeField != null) {
            Term term = getAuthorityListService().getTerm((Integer) courrierTypeField.getValue());
            if (term != null) {
                com.digitech.dossier.common.model.backend.params.CourrierType courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(), term.getCode(), getFlowType(theDocument));
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
                states.add(getConstant("STATE_CODE_REJECT"));
                if (hasState(null, theDocument, states)) {
                    // Courrier rejected during validation phase
                    Integer docUserCreator = FieldUtils.getValue(theDocument, getConstant("FIELD_CODE_U_CREAT"));
                    if (docUserCreator != null) {
                        sharingModel.setUsers(Arrays.asList(getUserMgr().getUser(docUserCreator)));
                    }
                } else {
                    String fieldCodeCourrierOwnerUser = getConstant("FIELD_CODE_U_PROPRIETAIRE");
                    Integer docUserOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerUser);
                    if (docUserOwner != null) {
                        sharingModel.setUsers(Arrays.asList(getUserMgr().getUser(docUserOwner)));
                    } else {
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
     * @param mailNotification if true, the mail notification is send
     */
    public static void addResponse(UserContext usrContext, IDocument theDocument, boolean mailNotification) {
        // Update document state
        String fieldCode = getConstant("FIELD_CODE_T_ETAT_COURRIER");

        FieldUtils.setValue(theDocument, fieldCode, getTermID(theDocument, fieldCode, getConstant("STATE_CODE_REPONDU")));

        //JMU désactivation du visa auto
		//prepareVisa(theDocument);

        CourrierScriptUtils.markDocumentToNotifyUser(theDocument);

        saveDocument(usrContext, theDocument);

        // Adds the response type
        addResponseType(usrContext, theDocument);

        if (mailNotification && Boolean.TRUE.equals(Boolean.valueOf(getConstant("MAIL_NOTIFICATION_ENABLED")))) {
            sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_SIGN, "FIELD_CODE_U_VISEUR", "mail_subject_courrier_state_to_sign", "changementEtat.htm", true);
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
     * Prepare document for visa when response is done
     * @param theDocument document
     */
    public static void prepareVisa(IDocument theDocument) {
        String etatVisaFieldCode = getConstant("FIELD_CODE_T_ETAT_VISA");
        Integer visaState = (Integer) theDocument.getField(etatVisaFieldCode).getValue();
        Term etatVisaTerm = visaState != null && visaState > 0 ? getAuthorityListService().getTerm(visaState) : null;
        //log.debug("docId=[" + theDocument.getAirsRefId() + "] T_ETAT_COURRIER=[STATE_CODE_REPONDU] T_ETAT_VISA=[" + (etatVisaTerm == null ? "" : etatVisaTerm.getPreferedValue()) + "]");

        // Build the current signers string to compare it with the previous and determine if it has just been modified
        Integer currentSigner = null;
        IField signersField = theDocument.getField(getConstant("FIELD_CODE_U_VISEURS"));

        boolean signersFieldChanged = false;
        List<Integer> signers = signersField.getValues();
        if (signers != null && signers.size() > 0) {
            currentSigner = signers.get(0);
            signersFieldChanged = isMultivaluedFieldChanged(signersField);
        }

        boolean visaInit = false;
        if (signers != null && signers.size() > 0
                // First visa or the signers list has changed
                && (visaState == null || signersFieldChanged || (getConstant("STATE_CODE_VISA_REFUSE").compareTo(etatVisaTerm.getCode()) == 0))) {
            // Visa process init, set the first signer
            FieldUtils.setValue(theDocument, getConstant("FIELD_CODE_U_VISEUR"), currentSigner);
            visaInit = true;
        } else if (signersFieldChanged && (signers == null || signers.size() == 0)) {
            // Signers list have been set to empty
            FieldUtils.setValue(theDocument, getConstant("FIELD_CODE_U_VISEUR"), "");
        }

        if (visaInit
                // Someone refused and there is a new signers list filled
                || (currentSigner != null && getTermID(theDocument, etatVisaFieldCode, getConstant("STATE_CODE_VISA_REFUSE")).equals(visaState))) {
            FieldUtils.setValue(theDocument, etatVisaFieldCode, getTermID(theDocument, etatVisaFieldCode, getConstant("STATE_CODE_VISA_INDEFINI")));
            //log.debug("visaInit=[true] T_ETAT_VISA set to [VISA_INDEFINI]");
        }
    }

    /**
     * Signing action (ok/ko)
     * @param usrContext user context
     * @param theDocument document
     */
    public static void doVisa(UserContext usrContext, IDocument theDocument) {
        VisaController visaController = CourrierUtils.getVisaController();
        VisaModel visaModel = visaController.getModel();
        String comment = visaModel.getComment();
        String visaResponse = visaModel.getSelectedVisaType();

        // modified attachment add
        if (visaModel.isModified()) {
            AttachmentModel selectionAttachmentModel = visaModel.getAttachmentOutModel();
            IAttachment attachment = (IAttachment) selectionAttachmentModel.getCurrentAttachment(false);
            DocumentUtils.editAttachment(theDocument, attachment);
        }

        boolean visaOk = false;
        String fieldCode = getConstant("FIELD_CODE_T_ETAT_VISA");
        CourrierAdvancedAuditType evtType = CourrierAdvancedAuditType.ADV_EVENT_COURRIER_REFUSED;
        Integer visaStateId = getTermID(theDocument, fieldCode, getConstant("STATE_CODE_VISA_REFUSE"));
        if (Boolean.TRUE.equals(Boolean.valueOf(visaResponse))) {
            visaStateId = getTermID(theDocument, fieldCode, getConstant("STATE_CODE_VISA_ACCEPTE"));
            evtType = CourrierAdvancedAuditType.ADV_EVENT_COURRIER_ACCEPTED;
            visaOk = true;
        }

        String currentFieldCode = getConstant("FIELD_CODE_U_VISEUR"), listFieldCode = getConstant("FIELD_CODE_U_VISEURS");
        boolean isLastValidator = isLastValidator(theDocument, currentFieldCode, listFieldCode);
        boolean validationProcessComplete = !visaOk || isLastValidator;

        if (!validationProcessComplete) {
            //logger.debug("docId [" + theDocument.getAirsRefId()  + "] jump to next signer.");

            FieldUtils.setValue(theDocument, currentFieldCode, getNextValidator(theDocument, currentFieldCode, listFieldCode));
        } else {
            // Document state update
            FieldUtils.setValue(theDocument, fieldCode, visaStateId);
            if (visaOk) {
                // ... et de la date d'acceptation
                FieldUtils.setValue(theDocument, getConstant("FIELD_CODE_D_ACCEPTATION"), new Date());
            }
        }

        // Reset Boolean, new mail will be send
        if (!isLastValidator) {
            CourrierScriptUtils.markDocumentToNotifyUser(theDocument);
        }

        saveDocument(usrContext, theDocument, evtType);

        // Ajout du commentaire
        AbstractSharingModel sharingModel = getSharingModel(Boolean.valueOf(visaModel.isPublicDisplay()), theDocument);
        DocumentUtils.addComment(theDocument, visaModel.getComment(), sharingModel);

        // Mail notification
        if (Boolean.TRUE.equals(Boolean.valueOf(getConstant("MAIL_NOTIFICATION_ENABLED")))) {
            String templateFileName = "changementEtat.htm";
            if (validationProcessComplete) {
                // Owner notification
                sendNotification(usrContext, theDocument, visaOk ? CourrierAdvancedAuditType.ADV_EVENT_COURRIER_ACCEPTED : CourrierAdvancedAuditType.ADV_EVENT_COURRIER_REJECTED,
                        "FIELD_CODE_U_PROPRIETAIRE", visaOk ? "mail_subject_courrier_state_accepted" : "mail_subject_courrier_state_rejected", templateFileName, true);
            } else {
                // Notifier le prochain viseur
                sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_SIGN, "FIELD_CODE_U_VISEUR",
                        "mail_subject_courrier_state_to_validate", templateFileName, true);
            }
        }

        // Display search results
        Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));
    }

    /**
     * Tell if visa action is visible
     * @param usrContext user context
     * @param theDocument document
     * @param visibleByDirection clause for direction visibility (depends on the mail flow)
     * @return true si field is visible
     */
    public static ScriptResultValueDisplayRule isVisaVisible(UserContext usrContext, IDocument theDocument, boolean visibleByDirection) {
        ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
        List<String> states = new ArrayList<String>()
        states.add(getConstant("STATE_CODE_REPONDU"));

        // Visible if the document is in state "REPONDU" and logged user is courrier signer or has direction profile
        visibleByDirection = visibleByDirection && UserUtils.hasProfile(usrContext, "DOS_DIRECTION");

        boolean visible = false;
        if (hasState(usrContext, theDocument, states)) {
            if (isSignerUser(usrContext, theDocument)) {
                String etatVisaFieldCode = getConstant("FIELD_CODE_T_ETAT_VISA");
                if (getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_INDEFINI")).equals(theDocument.getField(etatVisaFieldCode).getValue())) {
                    visible = true;
                }
            } else {
                visible = visibleByDirection;
            }
        }

        boolean documentLockedByOther = theDocument.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

        result.setValid(visible && !documentLockedByOther);

        return result;
    }

    public static void setVisaReadOnlyInputState(IDocument theDocument, ScriptResultModel<ScriptResultValueFieldInitializer> theOutput) {
        String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
        Integer visaState = theDocument.getField(etatVisaFieldCode).getValue();
        if (CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_INDEFINI")).equals(visaState)) {
            //logger.debug("T_ETAT_VISA=[STATE_CODE_VISA_INDEFINI] > the signers list is set to read only.");
            theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
        }
    }

    public static void closingSimpleView(UserContext userContext, ScriptResultModel theOutput, boolean checkOrgaEntity) {
        Collection<SearchResultTableRowModel> searchResultRows = Utils.getSearchResultTableController().getModel().getSelectedRows();
        int numberDocumentNonValid = 0;
        int numberDocumentIndexing = 0;
        int numberDocumentAlreadyClosed = 0;
        for (SearchResultTableRowModel row : searchResultRows) {
            IDocument document = row.getDocument();
            if (document != null) {
                ScriptResultValueDisplayRule result;
                try {
                    result = CourrierScriptUtils.isClosingVisible(userContext, document, checkOrgaEntity, true);
                    if (result.isValid()) {
                        CourrierScriptUtils.doClosing(UserContext.getInstance(), document, false);
                    } else {
                        numberDocumentNonValid++;
                    }
                } catch (ClosingException closingException) {
                    result = closingException.result;
                    if (closingException.alreadyClosed) {
                        numberDocumentAlreadyClosed++;
                    }
                    if (closingException.stateIndexing) {
                        numberDocumentIndexing++;
                    }
                }
            }
        }

        ScriptResultValueDocumentInitializer scriptResult = new ScriptResultValueDocumentInitializer();
        theOutput.setValue(scriptResult);

        Object[] params;

        MessagesModel.getInstance().clearPersistantMessages();

        if (numberDocumentNonValid == 0 && numberDocumentIndexing == 0 && numberDocumentAlreadyClosed == 0) {
            params = new Object[1];
            params[0] = Integer.valueOf(searchResultRows.size());
            scriptResult.setMessageSummary(BundleUtils.getTranslation("msg_document_closed_simple_view_summary"));
            scriptResult.setMessageDetail(BundleUtils.getTranslation("msg_document_closed_simple_view_detail", params));
        } else {
            scriptResult.setMessageSummary(BundleUtils.getTranslation("msg_document_closed_simple_view_summary_error"));
            params = new Object[4];
            params[0] = Integer.valueOf(numberDocumentNonValid + numberDocumentIndexing + numberDocumentAlreadyClosed);
            params[1] = Integer.valueOf(numberDocumentIndexing);
            params[2] = Integer.valueOf(numberDocumentAlreadyClosed);
            params[3] = Integer.valueOf(numberDocumentNonValid);
            scriptResult.setMessageDetail(BundleUtils.getTranslation("msg_document_closed_simple_view_detail_error", params));
        }
        scriptResult.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
        Utils.getSearchResultTableController().refreshAndKeepFilter();
    }

    /**
     * Tell if closing action is visible
     * @param usrContext user context
     * @param theDocument document
     * @param visibleByDirection clause for direction visibility (depends on the mail flow)
     * @param checkOrgaEntity check O_PROPRIETAIRE field
     * @return true si field is visible
     */
    public static ScriptResultValueDisplayRule isClosingVisible(UserContext usrContext, IDocument theDocument, boolean checkOrgaEntity, boolean throwException)
            throws ClosingException {
        ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
        result.setValid(false);
        boolean callIsClosingVisiblePrivate = true;

        List<String> states = new ArrayList<String>();
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER"));
        if (CourrierScriptUtils.hasState(usrContext, theDocument, states)) {
            if (throwException) {
                throw new ClosingException(result, false, true);
            }
            callIsClosingVisiblePrivate = false;
        }

        states.clear();
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE"));
        if (CourrierScriptUtils.hasState(usrContext, theDocument, states)) {
            if (throwException) {
                throw new ClosingException(result, true, false);
            }
            callIsClosingVisiblePrivate = false;
        }

        if (callIsClosingVisiblePrivate) {
            result.setValid(isClosingVisiblePrivate(usrContext, theDocument, checkOrgaEntity));
        }

        return result;
    }

    private static class ClosingException extends Exception {
        private final ScriptResultValueDisplayRule result;
        private final boolean alreadyClosed;
        private final boolean stateIndexing;

        private ClosingException(ScriptResultValueDisplayRule result, boolean alreadyClosed, boolean stateIndexing) {
            super();
            this.result = result;
            this.alreadyClosed = alreadyClosed;
            this.stateIndexing = stateIndexing;
        }
    }

    private static boolean isClosingVisiblePrivate(UserContext usrContext, IDocument theDocument, boolean checkOrgaEntity) {
        // Visible if the logged user is courrier owner or has direction profile
        boolean visibleByDirection = UserUtils.hasProfile(usrContext, "DOS_DIRECTION") && (!checkOrgaEntity || UserUtils.isInOrganization(usrContext, FieldUtils.getValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")), true));
        boolean visible = (CourrierScriptUtils.isOwnerUser(usrContext, theDocument) || visibleByDirection);
        boolean documentLockedByOther = theDocument.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

        return (visible && !documentLockedByOther);
    }

    public static void doClosing(UserContext usrContext, IDocument theDocument) {
        doClosing(usrContext, theDocument, true);
    }

    /**
     * Closing action
     * @param usrContext user context
     * @param theDocument document
     */
    public static void doClosing(UserContext usrContext, IDocument theDocument, boolean redirectionHomePage) {
        CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

        // Document state update
        String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
        FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE")));
        // ... and the closing date
        FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_CLOTURE"), new Date());

        Date sendDate = FieldUtils.getValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_ENVOI"));
        if (sendDate == null) {
            FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_ENVOI"), new Date());
        }

        CourrierScriptUtils.saveDocument(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_CLOSED);

        // Comment add
        AbstractSharingModel sharingModel = CourrierScriptUtils.getSharingModel((Boolean) customActionModel.getModalPanelModel().get("publicShare"), theDocument);
        DocumentUtils.addComment(theDocument, (String) customActionModel.getModalPanelModel().get("comment"), sharingModel);

        // Mail notification
        if (Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")))) {
            // Owwner notification
            CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_CLOSED, "FIELD_CODE_U_PROPRIETAIRE",
                    "mail_subject_courrier_state_closed", "changementEtat.htm", true);
        }

        // display home page
       // if (redirectionHomePage) {
         //   Utils.getCustomActionController().getModel().setOutcome(NavigationHandlerImpl.OUTCOME_HOME);
        //}
		
		Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));

        // if user had SIGNATORY_ONLY profile ... he must stay on search result table.
        if (usrContext.isSignataireOnly()) {
            Utils.getCustomActionController().getModel().setOutcome(NavigationHandlerImpl.OUTCOME_SEARCH_RESULT_SIMPLE);
        }

    }

    /**
     * Tell if closing action is visible
     * @param usrContext user context
     * @param theDocument document
     * @param visibleByDirection clause for direction visibility (depends on the mail flow)
     * @return true si field is visible
     */
    public static ScriptResultValueDisplayRule isRestoreVisible(UserContext usrContext, IDocument theDocument) {
        ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

        List<String> states = new ArrayList<String>()
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE"));

        // Visible if the document is in state "CLOTURE" and logged user is admin
        boolean visible = CourrierScriptUtils.hasState(usrContext, theDocument, states) && (UserUtils.hasProfile(usrContext, "DOS_ADMIN") || UserUtils.hasProfile(usrContext, "DOS_DIRECTION") || UserUtils.hasProfile(usrContext, "DOS_DECLOTURE") || UserUtils.hasProfile(usrContext, "DOS_ADMIN_CLIENT"));
        boolean documentLockedByOther = theDocument.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

        result.setValid(visible && !documentLockedByOther);

        return result;
    }

    public static boolean haveResponseDate(IDocument doc) {
        Object fieldValue = null;
        try {
            fieldValue = FieldUtils.getValue(doc, CourrierScriptUtils.getConstant("FIELD_CODE_D_REPONSE"));
        } catch (Exception e) {
            return false;
        }
        if (fieldValue != null)
            return true;
        return false;
    }

    /**
     * Restore action
     * @param usrContext user context
     * @param theDocument document
     */
    public static void doRestore(UserContext usrContext, IDocument theDocument) {
        // Document state update
        String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
        // Test if a D_REPONSE has been set
        if (haveResponseDate(theDocument)) {
            FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")));
        } else {
            FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")));
        }
		
		// Add an histo line for restore
		CourrierScriptUtils.addHistoForWorkflow(theDocument, usrContext, "Restauration du courrier");
        CourrierScriptUtils.saveDocument(usrContext, theDocument);

        // Display current page and refresh search results
        Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, theDocument));
    }

    /**
     * Generate the sequence of number Chrono
     * @param nextVal integer the number of sequence
     * @param sequenceCompleteSize the size of the sequence completed
     * @return sequenceComplete the sequence completed with some "0"
     */
    private static String generateSequenceComplete(Integer nextVal, int sequenceCompleteSize) {
        return StringUtils.leftPad(String.valueOf(nextVal), sequenceCompleteSize, "0");
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
        if ("CODE".compareTo(serviceFormat) == 0) {
            service = getServiceCode(orgId, userContext);
        } else if ("ID".compareTo(serviceFormat) == 0) {
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
        if (orgAdm != null) {
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
        if (numChronoDateFormat != null) {
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
        if (tabSplit.length != 2) {
            tabSplit = StringUtils.split(rule, '-');
            operator = "-";
        }
        if (tabSplit != null && tabSplit.length == 2) {
            tabRules = new String[3];
            if (tabSplit[0].length() >= 1 && tabSplit[1].length() >= 1) {
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
        if (DAirsDossierStringConstants.SEARCH_VAR_TODAY.compareTo(code) == 0) {
            dateRef = new Date();
        } else if (document != null) {
            IField fieldDateRef = document.getField(code);
            if (fieldDateRef != null) {
                int typeField = fieldDateRef.getType();
                if (typeField != IField.TYPE_DATE) throw new java.lang.IllegalStateException("Invalid type for field " + code + " (type Date expected)");
                dateRef = (Date) fieldDateRef.getValue();
            } else
                throw new java.lang.IllegalStateException("Field " + code + " not found");
        }
        return dateRef;
    }

    /**
     * Send a notification mail
     * @param userContext user context
     * @param theDocument document
     * @param type audit type
     * @param userFieldCode to user field
     * @param mailSubjectKey message key for object
     * @param templateFileName template file name
     * @param checkOrgaProprietaire try to send mail to the owner orga if user mail not filled
     */
    public
    static void sendNotification(UserContext userContext, IDocument theDocument, CourrierAdvancedAuditType type, String userFieldCode, String mailSubjectKey, String templateFileName,
                                 boolean checkOrgaProprietaire) {
        File templateFile = new File(CourrierUtils.getCourrierTemplateFolderPath(), templateFileName);
        Map<String, Object> customPropertyMap = new HashMap<String, Object>();
        customPropertyMap.put("state", type);
        User user = getUserMgr().getUser((Integer) theDocument.getField(getConstant(userFieldCode)).getValue());
        if (user != null) {
            String authLogin = getConstant("MAIL_NOTIFICATION_AUTH_DEFAULT_LOGIN", null);
            String authPassword = getConstant("MAIL_NOTIFICATION_AUTH_DEFAULT_PASSWORD", null);

            if (user.getEmail() == null || user.getEmail().length() <= 5) {
                if (checkOrgaProprietaire) {
                    // Get the orga if user does not have email
                    Organization orga = null;
                    if (user.getId().equals(userContext.getUserId())) {
                        // Connected user = notified user (we take current connected orga)
                        orga = getServerMgr().getOrganization(userContext.getJeton(), userContext.getCurrentOrgId());
                    } else if (user.getOrganizations() != null && user.getOrganizations().size() > 0) {
                        // We take the first orga of the user in the list
                        orga = user.getOrganizations().get(0);
                    }

                    if (orga != null) {
                        ApplicationUtils.sendMail(ApplicationUtils.getServerUrl(), userContext, Arrays.asList(theDocument), templateFile, new ReportOrganization(orga),
                                BundleUtils.getTranslation(mailSubjectKey), customPropertyMap, Boolean.TRUE, authLogin, authPassword);
                    }
                }
            } else if (!user.getId().equals(userContext.getUserId())) {
                // Send the mail to the user if he is not also the sender
                ApplicationUtils.sendMail(userContext, Arrays.asList(theDocument), templateFile, new ReportPerson(userContext, user), BundleUtils.getTranslation(mailSubjectKey),
                        customPropertyMap, Boolean.TRUE, authLogin, authPassword);
            }
        }
    }

    public static List<User> getUsersFromProfile(String profileCode) {
        List<User> elements = new ArrayList<User>();

        //Chargement du profil
        IProfile profile = getRightMgr().getProfile(profileCode);
        if (profile != null) {
            ProfilAdmin currentProfil = com.digitech.jcorbairs.admin.ProfilsManager.load(DossierCoreContext.getAdminJeton(), profile.getId().intValue());
            if (currentProfil != null) {
                // Get all users who have the profile
                List<UserAdmin> userAdmList = currentProfil.getUsers();
                if (userAdmList != null && userAdmList.size() > 0) {
                    User user;
                    for (UserAdmin usrAdm : userAdmList) {
                        if ((user = getUserMgr().getUser(usrAdm.getId())) != null) {
                            elements.add(user);
                        }
                    }
                }
            }
        }

        return elements;
    }

    public static void setInputToMailModal(String fieldCorrespondent, Logger log) {
        IField fieldFrom = Utils.getViewUnitController().getModel().getDocument().getField(fieldCorrespondent);
        boolean clear = true;
        boolean error = false;
        if (fieldFrom != null) {
            Object valueObject = fieldFrom.getValue();
            if (valueObject != null) {
                Object correspondantObject = null;
                try {
                    correspondantObject = getCorrespondentMgr().getCorrespondent(UserContext.getInstance(), valueObject.toString());
                }
                catch (Exception e) {
                    log.error(e.getLocalizedMessage(), e);
                    error = true;
                }
                if (!error && correspondantObject != null) {
                    if (correspondantObject instanceof com.digitech.jcorbairs.User || correspondantObject instanceof IPerson) {
                        Utils.getMailController().getModel().setCorrespondent(correspondantObject);
                        clear = false;
                    }
                }
            }
        }
        if (clear) {
            Utils.getMailController().getModel().setCorrespondent(null);
            log.error("Field \"From\" not setted");
        }
    }

    public static boolean canMarkAsRead(UserContext usrContext, IDocument theDocument) {
        List<Integer> copyUsers = FieldUtils.getValues(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES"));
        List<Integer> copyOrg = FieldUtils.getValues(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_O_COPIES"));

        Integer userId = usrContext.getUserId();
        Integer orgId = usrContext.getCurrentOrgId();

        // On vérifie que l'utilisateur est bien en copie du courrier
        return (copyUsers != null && copyUsers.contains(userId)) || (copyOrg != null && copyOrg.contains(orgId));
    }

    public static boolean alreadyMarkAsRead(UserContext usrContext, IDocument theDocument) {
        return getReadCopyUsers(usrContext, theDocument, false) == null;
    }

    private static List<Integer> getReadCopyUsers(UserContext usrContext, IDocument theDocument, boolean addCurrentUser) {
        // On récupère la liste des utilisateurs qui ont lu le courrier
        List<Integer> readCopyUsers = FieldUtils.getValues(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES_LUES"));
        if (readCopyUsers == null) {
            readCopyUsers = new ArrayList<Integer>();
        }
        Integer userId = usrContext.getUserId();

        if (readCopyUsers.contains(userId)) {
            return null;
        }
        if (addCurrentUser) {
            // on ajoute l'utilisateur s'il n'a pas déjà lu le courrier
            readCopyUsers.add(userId);
        }
        return readCopyUsers;
    }

    public static void markAsRead(UserContext usrContext, IDocument theDocument, ScriptResultModel<ScriptResultValueFieldInitializer> theOutput) {
        markAsRead(usrContext, theDocument, theOutput, null);
    }

    public
    static void markAsRead(UserContext usrContext, IDocument theDocument, ScriptResultModel<ScriptResultValueFieldInitializer> theOutput, Boolean markAsRead) {
        if (markAsRead == null) {
            markAsRead = canMarkAsRead(usrContext, theDocument);
        }

        Integer userId = usrContext.getUserId();

        if (markAsRead) {
            if (isDocumentAvailableForUser(usrContext, theDocument)) {
                List<Integer> readCopyUsers = getReadCopyUsers(usrContext, theDocument, true);
                if (readCopyUsers == null) {
                    return;
                }
                // On sauvegarde le champ
                com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
                IDocument theNewDoc = new com.digitech.dossier.common.model.backend.airs.impl.Document(DocumentFactory.getInstance().getDocument(UserUtils.getAdminUserContext().getJeton(), theDocument.getAirsRefId()))
                FieldUtils.setValues(theNewDoc, CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES_LUES"), readCopyUsers);
                documentMgr.unlockDocument(usrContext, theDocument);
                documentMgr.updateDocument(UserUtils.getAdminUserContext(), theNewDoc, java.lang.Boolean.TRUE);
                if (theOutput != null) {
                    theOutput.getValue().setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
                }
            } else {
                if (theOutput != null) {
                    // On affiche un message d'information indiquant que le document ne peut pas être marqué comme lu
                    AbstractScriptResultValue result = theOutput.getValue();
                    result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
                    result.setMessageSummary(BundleUtils.getTranslation("msg_info_document_lockbyuser_summary"));
                    result.setMessageDetail(BundleUtils.getTranslation("msg_info_document_not_read_summary"));
                    theOutput.setValue(result);
                }
            }
        }
    }

    private static boolean isDocumentAvailableForUser(UserContext usrContext, IDocument theDocument) {
        return !theDocument.isLocked() || CourrierScriptUtils.getDocumentMgr().isDocumentLockedByUser(usrContext, theDocument);
    }

    /**
     * Can the document be validated?
     * @param theDocument the document to test
     * @param usrContext the user
     * @return
     */
    public static boolean canValidate(IDocument theDocument, UserContext usrContext) {
        List<String> states = new ArrayList<String>()
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_A_VALIDER"));

        // Visible if the document is in state "A_VALIDER" and logged user is courrier validator
        boolean visible = CourrierScriptUtils.hasState(usrContext, theDocument, states) && CourrierScriptUtils.isValidatorUser(usrContext, theDocument);
        boolean documentLockedByOther = theDocument.getLockType() == com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER;

        return visible && !documentLockedByOther;
    }

    /**
     * Change document status to validate
     * @param theDocument the document to modify
     * @param usrContext the user
     * @param log
     * @return was the validation possible?
     */
    public static boolean validateCourrier(IDocument theDocument, UserContext usrContext, Logger log) {
        if (!canValidate(theDocument, usrContext)) {
            return false;
        }
        CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

        String currentFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR");
        String listFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEURS");

        boolean validationProcessComplete = CourrierScriptUtils.isLastValidator(theDocument, currentFieldCode, listFieldCode);

        if (!validationProcessComplete) {
            Integer nextValidator = CourrierScriptUtils.getNextValidator(theDocument, currentFieldCode, listFieldCode);
            log.debug("docId [" + theDocument.getAirsRefId() + "] jump to next validator [" + nextValidator + "]");

            FieldUtils.setValue(theDocument, currentFieldCode, nextValidator);
        } else {
            log.debug("Validation docId [" + theDocument.getAirsRefId() + "] complete.");

            // Mise à jour de l'état du document...
            String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
            FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")));
            // ... et de la date de validation
            FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_D_VALIDATION"), new Date());
        }

        // Reset Boolean, new mail will be send
        CourrierScriptUtils.markDocumentToNotifyUser(theDocument);

        CourrierScriptUtils.saveDocument(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_VALIDATED, true);

        // Ajout du commentaire
        AbstractSharingModel sharingModel = CourrierScriptUtils.getSharingModel((Boolean) customActionModel.getModalPanelModel().get("public"), theDocument);
        DocumentUtils.addComment(theDocument, (String) customActionModel.getModalPanelModel().get("comment"), sharingModel);

        // Mail notification
        //if( Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED"))) ) {
        /* Désactivation des mails suite à l'ajout des notification par Lots. */
        if (Boolean.TRUE.equals(Boolean.FALSE)) {
            String templateFileName = "changementEtat.htm";
            if (validationProcessComplete) {
                // Notifier le propriétaire
                CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_VALIDATED, "FIELD_CODE_U_PROPRIETAIRE",
                        "mail_subject_courrier_state_validated", templateFileName, true);
            } else {
                // Notifier le prochain valideur
                CourrierScriptUtils.sendNotification(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_VALIDATE, "FIELD_CODE_U_VALIDEUR",
                        "mail_subject_courrier_state_to_validate", templateFileName, true);
            }
        }
        return true;
    }

    /**
     * Mark the field as READ_ONLY if the user got access to ViewUnit in type Update only because he is referenced as an user in copy.
     *
     * @param usrContext
     * @param logger
     * @param theDocument
     * @param theOutput
     * @return Was the field marked as read
     */
    public static boolean markFieldAsReadOnly(UserContext usrContext, Logger logger, IDocument theDocument,
                                              ScriptResultModel<ScriptResultValueFieldInitializer> theOutput) {
        List<Action> unavailableActions = CourrierScriptUtils.unavailableActions_courrierIn_fromDocument(usrContext, logger, theDocument);
        boolean editUnavailable = unavailableActions.contains(Action.EDIT);
        if (editUnavailable) {
            if (theOutput != null) {
                theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
            }
            return true;
        }
        return false;
    }

    /**
     * Set the unavailable actions in the theOutput ScriptResultModel. Save and Edit actions are removed from unavailable acions (i.e. they are authorised) if the user is in the field "U_COPIES"
     *
     * @param usrContext
     * @param logger
     * @param theOutput
     * @param theFieldValueMap
     */
    public static void unavailableActions_courrierIn(UserContext usrContext, Logger logger,
                                                     ScriptResultModel<ScriptResultValueDocumentActions> theOutput, Map<String, IFieldValue> theFieldValueMap) {
        Map<String, Object> values = new HashMap<String, Object>();

        Set<Entry<String, IFieldValue>> entrySet = theFieldValueMap.entrySet();
        for (Entry<String, IFieldValue> entry : entrySet) {
            values.put(entry.getKey(), entry.getValue().getValue());
        }

        unavailableActions(usrContext, logger, theOutput, values, true);
    }

    private static List<Action> unavailableActions_courrierIn_fromDocument(UserContext usrContext, Logger logger,
                                                                           IDocument theDocument) {
        Map<String, Object> values = new HashMap<String, Object>();

        Set<Entry<String, IField>> entrySet = theDocument.getFieldMap().entrySet();
        for (Entry<String, IField> entry : entrySet) {
            values.put(entry.getKey(), entry.getValue().getValue());
        }

        return unavailableActions(usrContext, logger, null, values, false);
    }

    private static List<Action> unavailableActions(UserContext usrContext, Logger logger, ScriptResultModel<ScriptResultValueDocumentActions> theOutput,
                                                   Map<String, Object> theFieldValueMap, boolean withUserCopy) {
        Boolean haveNoAction = true;
        com.digitech.jcorbairs.User usr = usrContext.getUser();
        Integer userId = usr.getId();

        Object orgaProprietaire = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"));
        Object userProprietaire = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE"));

        String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
        Object etatCourrierField = theFieldValueMap.get(etatCourrierFieldCode);
        Integer courrierState = etatCourrierField == null ? null : etatCourrierField;
        Term etatTerm = CourrierScriptUtils.getAuthorityListService().getTerm(courrierState);

        logger.debug("userId=[" + userId + "] courrierState=[" + (etatTerm == null ? "" : etatTerm.getPreferedValue()) + "] orgaProprietaire=[" + (orgaProprietaire == null ? "" : orgaProprietaire)
                + "] userProprietaire=[" + (userProprietaire == null ? "" : userProprietaire) + "]");

        if (UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_DIR"))) {
            haveNoAction = orgaProprietaire == null ? Boolean.TRUE : !UserUtils.isInOrganization(usrContext, orgaProprietaire, true);
        } else {
            boolean owner, validator, signer = false;

            Integer docUserOwner = userProprietaire;
            if (docUserOwner != null) {
                owner = docUserOwner.equals(userId);
            } else {
                if (orgaProprietaire != null) {
                    owner = orgaProprietaire == null ? Boolean.FALSE : UserUtils.isInOrganization(usrContext, orgaProprietaire, true);
                }
            }

            Object valideur = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR"));
            Integer docUserValidator = valideur == null ? null : valideur;
            if (docUserValidator != null) {
                validator = docUserValidator.equals(userId);
            }

            Object viseur = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR"));
            Integer docUserSigner = viseur == null ? null : viseur;
            if (docUserSigner != null) {
                signer = docUserSigner.equals(userId);
            }

            logger.debug("docUserValidator=[" + docUserValidator + "] docUserSigner=[" + docUserSigner + "]")

            haveNoAction = !owner && !validator && !signer;
        }

        List<Action> unavailableActions = new ArrayList<Action>();
        unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
        // Every body can add a comment
        unavailableActions.remove(ScriptResultValueDocumentActions.Action.ADD_COMMENT);
        if (!haveNoAction) {
            unavailableActions.clear();
        }

        // Is the mail in indexation?
        if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_INDEXER")).equals(courrierState)) {
            unavailableActions.clear();
        }

        // Is the mail refused?
        if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REJECT")).equals(courrierState)) {
            Integer docUserCreator = theFieldValueMap.get(CourrierScriptUtils.getConstant("FIELD_CODE_U_CREAT"));
            if (docUserCreator != null && docUserCreator.equals(userId)) {
                unavailableActions.clear();
            }
        }

        // Check the visa state
        String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
        Integer visaState = theFieldValueMap.get(etatVisaFieldCode);
        if (CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_ACCEPTE")).equals(visaState)) {
            unavailableActions.clear();
            unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
            if (!UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
                unavailableActions.remove(Action.EDIT);
                unavailableActions.remove(Action.SAVE);
            } else {
                unavailableActions.clear();
            }
        }
        // Si l'utilsiateur est un administrateur, toutes les actions sont permises tant que le visa n'est pas validé
        else if (UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
            unavailableActions.clear();
        }

        // Is the mail closed?
        if (CourrierScriptUtils.getTermID(etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE")).equals(courrierState)) {
            unavailableActions.clear();
            unavailableActions.addAll(ScriptResultValueDocumentActions.getAllActions());
            if (UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN")) || UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN_CLIENT"))) {
                unavailableActions.clear();
            }
        }

        //If the current user is in the U_COPIES field, he is authorised to edit the document
        boolean canEdit_U_COPIES = false;
        String U_COPIES_FIELD = CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES");
        Object values = theFieldValueMap.get(U_COPIES_FIELD);
        if (values != null && withUserCopy) {
            if (values instanceof List) {
                String idUserString = usrContext.getUserId().toString();
                List<Object> valuesList = (List) values;
                for (Object value : valuesList) {
                    if (value.toString().equals(idUserString)) {
                        canEdit_U_COPIES = true;
                        break;
                    }
                }
            } else {
                if (values.toString().equals(usrContext.getUserId().toString())) {
                    canEdit_U_COPIES = true;
                }
            }
        }
        if (canEdit_U_COPIES) {
            unavailableActions.remove(Action.EDIT);
            unavailableActions.remove(Action.SAVE);
        }

        if (theOutput != null) {
            theOutput.getValue().getUnavailableActions().addAll(unavailableActions);
        }
        return unavailableActions;
    }

    public static boolean changeRequired_U_VALIDEURS(UserContext usrContext, Logger logger, IField theUpdatedField, IField theFieldToUpdate) {
        return changeRequired_U_VALIDEURS_private(usrContext, logger, theUpdatedField, theFieldToUpdate, null);
    }

    public
    static boolean changeRequired_U_VALIDEURS(UserContext usrContext, Logger logger, IField theUpdatedField, ScriptResultModel<ScriptResultValueFieldInitializer> theOutput) {
        return changeRequired_U_VALIDEURS_private(usrContext, logger, theUpdatedField, null, theOutput);
    }

    private static boolean changeRequired_U_VALIDEURS_private(UserContext usrContext, Logger logger, IField theUpdatedField, IField theFieldToUpdate,
                                                              ScriptResultModel<ScriptResultValueFieldInitializer> theOutput) {
        Boolean required = false;

        CourrierType courrierType;
        try {
            // On récupère le type du courrier
            Term term = CourrierScriptUtils.getAuthorityListService().getTerm((Integer) theUpdatedField.getValue());
            if (term != null) {
                courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(), term.getCode(), FlowType.IN);
                if (courrierType != null) {
                    // On regarde si un validator est requis
                    required = courrierType.isValidatorRequired();
                }
            }
        } catch (IdentificationException e) {
            logger.error(e.getMessage(), e);
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
        }

        if (theFieldToUpdate != null) {
            FieldUtils.setRequired(theFieldToUpdate, required, true);
        } else if (theOutput != null) {
            theOutput.getValue().getProperties().put(FieldProperty.REQUIRED, required.toString());
        }
        return required;
    }

    public static void changeReadOnly_U_VALIDEURS(UserContext usrContext, IDocument theDocument, ScriptResultModel<ScriptResultValueFieldInitializer>
            theOutput) {
        List states = new ArrayList();
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE"));
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_REPONDU"));
        states.add(CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE"));

        if (CourrierScriptUtils.hasState(usrContext, theDocument, states) && !UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
            theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
        }
    }

    public static void conditionneInitialValue_U_VALIDEURS(UpdateField theUpdatedField, IDocument theDocument) {
        List<String> initialValues = theUpdatedField.getInitialValues();
        theUpdatedField.setInitialValues(new ArrayList<String>());
        List<Integer> valuesIntFromInitialValues = new ArrayList<Integer>();
        for (String initialValue : initialValues) {
            valuesIntFromInitialValues.add(Integer.valueOf(initialValue));
        }
        theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEURS")).getAttributes().put("initialValues", valuesIntFromInitialValues);
    }

    /**
     * mark the document as "notification send".
     *
     * @param theDocument document to flag
     */
    public static void markDocumentToNotifyUser(IDocument theDocument) {
        // Reset Boolean, new mail will be send
        FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_E_SEND_MAIL"), "0");
    }

    private static FlowType getFlowType(IDocument document) {
        String domainCode = document.getDomain().getCode();
        if (domainCode.equals(CourrierScriptUtils.getConstant("CONTENT_TYPE_COU_COURRIER_IN"))) {
            return FlowType.IN;
        }
        if (domainCode.equals(CourrierScriptUtils.getConstant("CONTENT_TYPE_COU_COURRIER_OUT"))) {
            return FlowType.OUT;
        }
        return null;
    }
	
	/**
	* Ajoute une entrée de type workflow dans l'historique d'un document
	* @param document : Le document.
	* @param userContext : Le contexte de l'utilisateur courant.
	* @param newWorkflowValue : L'entrée workflow à ajouter.
	*/
	private static void addHistoForWorkflow(IDocument document, UserCoreContext userContext,  String newWorkflowValue){
		// if the document is not locked
		if(document.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER){
			log.debug("Le document n'est pas verrouillé");
		
			if(newWorkflowValue != null ){
				// add a line in histo table
				String commentEvent = newWorkflowValue;
				log.debug("Ajout de l'historique => "+commentEvent);
				getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_FIELDCHANGE.name(), commentEvent);
			}
		}
	}

    private static IUser getUserMgr() {
        return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
    }

    private static IServer getServerMgr() {
        return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
    }

    private static IRight getRightMgr() {
        return (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);
    }

    private static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
        return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
    }

    private static com.digitech.dossier.common.service.ICorrespondent getCorrespondentMgr() {
        return (com.digitech.dossier.common.service.ICorrespondent) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_CORRESPONDENT);
    }
	
	private static com.digitech.dossier.common.service.impl.AuditMgr getAuditMgr()
	{
		return (com.digitech.dossier.common.service.impl.AuditMgr) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
	}
}