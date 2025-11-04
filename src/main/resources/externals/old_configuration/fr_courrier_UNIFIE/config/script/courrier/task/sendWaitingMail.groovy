import com.digitech.common.lib.utils.StringUtils
import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.exception.InvalidConfigurationException
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.airs.ISearchContentTypeModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.impl.Search
import com.digitech.dossier.common.model.backend.airs.impl.SearchContentTypeModel
import com.digitech.dossier.common.model.backend.report.value.IReportComplexValue
import com.digitech.dossier.common.model.backend.report.value.airs.ReportOrganization
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.ISearch
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.jcorbairs.Domain
import com.digitech.jcorbairs.Organization
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User
import com.digitech.jcorbairs.exception.ServerException
import org.slf4j.Logger

import java.util.Map.Entry

getLog().info("[GROOVY] - SEND WAITING MAIL [TASK]\tSTART");
try {

    if (!Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_BATCH_ENABLED")))) {
        getLog().info("Parameter MAIL_NOTIFICATION_BATCH_ENABLED is on FALSE");
        getLog().info("[GROOVY] - SEND WAITING MAIL [TASK]\tCLOSE");
        return;
    }

    getLog().debug("Load Admin User Context");
    UserCoreContext usrContext = UserUtils.getAdminUserContext();
    getLog().debug("[OK]");

    String FIELD_MAIL_SEND = "FIELD_CODE_E_SEND_MAIL";
    String recipientFieldCode = null;
    Map<String, List<Integer>> waitingMails = new HashMap<String, List<Integer>>();
    Map<Integer, CourrierAdvancedAuditType> courrierAuditTypes = new HashMap<Integer, CourrierAdvancedAuditType>();

    // 1st step - récupérer la liste des mails en attente.
    List<Integer> mailingDocumentIds = getWaitingMailDocIds(usrContext);

    if (mailingDocumentIds == null || mailingDocumentIds.size() == 0) {
        getLog().info("waiting mail list is empty");
        return;
    }
    getLog().debug("Number of mail to send : {}", mailingDocumentIds.size());

// 2nd step - créer liste des destinataires et affecter les docId.
    for (Integer documentId : mailingDocumentIds) {
        Boolean documentAdd = false;

        IDocument document = getDocumentMgr().getDocument(usrContext.getJeton(), documentId);

        String chronoNumber = document.getField(CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO")).getValue().toString();
        getLog().debug("current document : {}", document.getAirsRefId());

        recipientFieldCode = getRecipientFieldCode(document);
        if (StringUtils.isEmpty(recipientFieldCode)) {
            getLog().debug("recipient field code is empty.");
            continue;
        }

        getLog().debug("recipient field code [{}]", recipientFieldCode);

        IField recipientField = document.getField(recipientFieldCode);
        if (recipientField.isMultiValued()) {

            getLog().debug("[{}] is multiValues field", recipientFieldCode);

            // if multivalued, field containt a list of UserId
            String valueSeparator = "";
            valueSeparator = recipientField.getAirsField().getValueSeperator();

            if (StringUtils.isEmpty(valueSeparator)) {
                throw new InvalidConfigurationException("The Field '" + recipientField.getCode() + "' is multivalued but no value separator is defined in AIRS");
            }
            List<String> valuesStr = new ArrayList<String>();

            for (Object value : recipientField.getValues()) {

                if (value != null) {

                    getLog().debug("check for user : {}", value);

                    //add document to each user
                    documentAdd = addDocToRecipient(usrContext, waitingMails, value, documentId);
                }
            }
        } else {
            if (recipientField.getValue() != null) {

                getLog().debug("check for user : {}", recipientField.getValue());
                documentAdd = addDocToRecipient(usrContext, waitingMails, recipientField.getValue(), documentId);
            }
        }

        if (!documentAdd) {
            documentAdd = addDocToRecipient(usrContext, waitingMails, new Integer(-1), documentId);
        }
        // if document add we add also type of Audit.
        // this will use on document update.
        if (documentAdd) {
            CourrierAdvancedAuditType auditType = getCourrierAuditType(recipientFieldCode);
            if (auditType != null) {
                courrierAuditTypes.put(documentId, auditType);
            }
        }
    }
    getLog().debug("Number of recipients : {}", waitingMails.size());

    // 3rd step - send one mail for each user .
    for (Entry<String, List<Integer>> entry : waitingMails.entrySet()) {
        IReportComplexValue correspondant = findCorrespondant(usrContext, entry.getKey());
        sendMail(usrContext, correspondant, entry.getValue());
    }

    // Last Step - Mark each document from list a send
    markAsSend(usrContext, mailingDocumentIds);

}
catch (Exception e) {
    getLog().error(e.getLocalizedMessage(), e);
}
finally {
    getLog().info("[GROOVY] - SEND WAITING MAIL [TASK]\tCLOSE");
}

/**
 * get list of documentId where mail dont send
 *
 * @param usrContext the userContext
 *
 * @return list of documentId where mail dont send
 */
private List<Integer> getWaitingMailDocIds(UserCoreContext usrContext) {
    ISearchModel searchModel = new Search();

    // Selection des documents de COU_COURRIER_IN et COU_COURRIER_OUT.
    List<Domain> contentTypes = new ArrayList<Domain>();
    try {
        contentTypes.add(getServerMgr().getDomain(usrContext.getJeton(), CourrierScriptUtils.getConstant("CONTENT_TYPE_COU_COURRIER_IN")));
        contentTypes.add(getServerMgr().getDomain(usrContext.getJeton(), CourrierScriptUtils.getConstant("CONTENT_TYPE_COU_COURRIER_OUT")));
    }
    catch (Exception e) {
        getLog().error(e.getLocalizedMessage(), e);
    }

    List<ISearchContentTypeModel> searchContentTypeModels = new ArrayList<ISearchContentTypeModel>();
    for (Domain domain : contentTypes) {
        searchContentTypeModels.add(new SearchContentTypeModel(domain));
        getLog().debug("add Content Type {} : [OK] ", domain.getCode());
    }

    // Création de la requete AIRS
    String airsRequest = "(" + CourrierScriptUtils.getConstant("FIELD_CODE_E_SEND_MAIL") + " = \"0\")";
    getLog().debug("AIRS_REQUEST : " + airsRequest);

    searchModel.setAirsRequest(airsRequest);
    searchModel.setContentTypes(searchContentTypeModels);
    searchModel.setUsedOnlySelectedContentTypeCode(false);

    try {
        // Execution de la requete retour de la liste des documentIds
        return getSearchService().getSearch(usrContext, searchModel, false);
    }
    catch (Exception e) {
        getLog().error(e.getLocalizedMessage(), e);
        return null;
    }
}

/**
 *
 * @param waitingMailList
 * @param userId
 * @param documentId
 */
private Boolean addDocToRecipient(UserCoreContext usrContext, Map<String, List<Integer>> waitingMailList, Integer userId, Integer documentId) {
    // check entry variables
    if (userId == null || documentId == null) {
        String messErr = "";
        messErr += userId == null ? "UserId is null" : "";
        messErr += documentId == null ? "documentId is null" : "";
        getLog().error(messErr);
        return false;
    }

    //chek null
    if (waitingMailList == null) {
        waitingMailList = new HashMap<String, List<Integer>>();
    }

    List<Integer> existingDocumentIds = new ArrayList<Integer>();

    IReportComplexValue correspondant = getMailCorrespondant(usrContext, documentId, userId);

    if (correspondant == null) {
        return false;
    }

    String key = buildCorrespondantKey(correspondant);
    //check for userId
    if (waitingMailList.containsKey(key)) {
        existingDocumentIds = waitingMailList.get(key);
        for (Integer currentDocId : existingDocumentIds) {
            if (currentDocId == documentId) {
                //documentId is already on the list ... we quit
                return false;
            }
        }
    }

    // if userId isn't in the list
    // or if "for" loops don't find documentId we add documentId to the List add put an entry in Map.
    existingDocumentIds.add(documentId);
    getLog().debug("add document : {} to key : {} in mailing list", documentId, key);
    waitingMailList.put(key, existingDocumentIds);
    return true;
}

/**
 * get the field name to select the recipient.
 * depend on document's state.
 *
 * @param IDocument
 * @return field name
 */
private String getRecipientFieldCode(IDocument document) {
    String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
    Integer etatCourrant = (Integer) document.getField(etatCourrierFieldCode).getValue();

    Term etatTerm = etatCourrant != null && etatCourrant > 0 ? CourrierScriptUtils.getAuthorityListService().getTerm(etatCourrant) : null;
    getLog().debug("docId=[" + document.getAirsRefId() + "] T_ETAT_COURRIER=[" + (etatTerm == null ? "" : etatTerm.getPreferedValue()) + "]");

    if (CourrierScriptUtils.getTermID(document, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_VALIDER")).equals(etatCourrant)) {
        // Renvoyer le code du champ validateur
        return CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR");
    } else if (CourrierScriptUtils.getTermID(document, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")).equals(etatCourrant)) {
        // Renvoyer le code du champ propriétaire
        return CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE");
    } else if (CourrierScriptUtils.getTermID(document, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")).equals(etatCourrant)) {
        // Renvoyer le code du champ viseur
        return CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR");
    }
    //default return
    return null;
}

/**
 * get audit Type in fonction of the field
 * @param fieldCode
 * @return
 */
private CourrierAdvancedAuditType getCourrierAuditType(String fieldCode) {
    if (CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR").equals(fieldCode)) {
        return CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_VALIDATE;
    } else if (CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE").equals(fieldCode)) {
        return CourrierAdvancedAuditType.ADV_EVENT_COURRIER_DIFFUSED;
    } else if (CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR").equals(fieldCode)) {
        return CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_SIGN;
    }
    return null;
}

/**
 * send a mail with description of each document
 *
 * @param userId
 * @param documentId
 */
private void sendMail(UserCoreContext usrContext, IReportComplexValue correspondant, List<Integer> documentIds) {
    String mailFieldSubject = null;
    File templateFile = new File(CourrierUtils.getCourrierTemplateFolderPath(), "changementEtatMulti.htm");

    List<IDocument> documents = new ArrayList<IDocument>();
    com.digitech.dossier.common.service.IDocument docMgr = getDocumentMgr();

    try {

        //check existing mail
        boolean checkSendMail = false;

        // send mail
        StringBuffer logMsg = new StringBuffer();
        if (correspondant instanceof ReportPerson) {
            logMsg.append("send mail to userId : ");
            logMsg.append(((ReportPerson) correspondant).getId());
        } else if (correspondant instanceof ReportOrganization) {
            logMsg.append("send mail to orgalizationId : ");
            logMsg.append(((ReportOrganization) correspondant).getId());
        }
        logMsg.append(" for documents : ");

        // get mail subject
        mailFieldSubject = BundleUtils.getTranslation("mail_subject_courrier_new");

        // Get all documents
        for (Integer documentId : documentIds) {
            IDocument currentDocument = docMgr.getDocument(usrContext.getJeton(), documentId);
            if (currentDocument != null) {
                documents.add(currentDocument);
                logMsg.append(documentId + " | ");
            }
        }


        getLog().debug(logMsg.toString());
        // send mail
        String webAppUrl = DossierCoreContext.getParamsInfos().getWebAppURL()
        ApplicationUtils.sendMail(webAppUrl, usrContext, documents, templateFile, correspondant, mailFieldSubject, null);
    }
    catch (Exception e) {
        getLog().error(e.getLocalizedMessage(), e);
        return;
    }
    getLog().debug("[OK]");
}

private void markAsSend(UserCoreContext usrContext, List<Integer> documentIds) {
    for (Integer documentId : documentIds) {
        markAsSend(usrContext, documentId);
    }
}

private void markAsSend(UserCoreContext usrContext, Integer documentId) {
    com.digitech.dossier.common.service.IDocument docMgr = getDocumentMgr();

    IDocument document = docMgr.getDocument(usrContext.getJeton(), documentId);

    FieldUtils.setValue(document, CourrierScriptUtils.getConstant("FIELD_CODE_E_SEND_MAIL"), 1);

    try {
        docMgr.updateDocument(usrContext, document, true);
        getLog().debug("document {} mark as send.", documentId);
    }
    catch (ServerException e) {
        getLog().error(e.getLocalizedMessage());
    }

}

private IReportComplexValue getMailCorrespondant(UserCoreContext usrContext, Integer docId, Integer userId) {

    if (userId != -1) {
        User user = getUserMgr().getUser(userId);
        if (!StringUtils.isEmpty(user.getEmail())) {
            return new ReportPerson(usrContext, user);
        }
    }

    IDocument currentDocument = getDocumentMgr().getDocument(usrContext.getJeton(), docId);
    String orgaId = currentDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
    if (!StringUtils.isEmpty(orgaId)) {
        Organization orga = getServerMgr().getOrganization(usrContext.getJeton(), Integer.parseInt(orgaId));
        String orgaMail = orga.getProperties().getEmail();
        if (orga != null && !StringUtils.isEmpty(orgaMail)) {
            return new ReportOrganization(orga);
        }
    }
    return null;
}

private String buildCorrespondantKey(IReportComplexValue correspondant) {
    if (correspondant instanceof ReportPerson) {
        return CourrierScriptUtils.getConstant("PREFIX_PERSON") + ((ReportPerson) correspondant).getId();
    }
    if (correspondant instanceof ReportOrganization) {
        return CourrierScriptUtils.getConstant("PREFIX_ORGANIZATION") + ((ReportOrganization) correspondant).getId();
    }
    return null;
}

private IReportComplexValue findCorrespondant(UserCoreContext usrContext, String key) {
    if (key.startsWith(CourrierScriptUtils.getConstant("PREFIX_PERSON"))) {
        String correspondantId = key.replace(CourrierScriptUtils.getConstant("PREFIX_PERSON"), "");
        User user = getUserMgr().getUser(Integer.parseInt(correspondantId))
        return new ReportPerson(usrContext, user);
    } else if (key.startsWith(CourrierScriptUtils.getConstant("PREFIX_ORGANIZATION"))) {
        String correspondantId = key.replace(CourrierScriptUtils.getConstant("PREFIX_ORGANIZATION"), "");
        Organization orga = getServerMgr().getOrganization(usrContext.getJeton(), Integer.parseInt(correspondantId));
        return new ReportOrganization(orga);
    }
    return null;
}

private IServer getServerMgr() {
    return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
    return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

private IUser getUserMgr() {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}

private ISearch getSearchService() {
    return (ISearch) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SEARCH_MGR);
}

private Logger getLog() {
    return (Logger) scriptLogger;
}
