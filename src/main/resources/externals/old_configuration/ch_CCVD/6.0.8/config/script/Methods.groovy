/**
 * Created by Mathieu Toubache on 04.11.2014.
 * Regroupe l'ensenble des méthodes utiles dans différents groovy
 */


import com.akazi.flowmind.api.Actor
import com.akazi.flowmind.api.BpmCollection
import com.akazi.flowmind.api.BpmSession
import com.akazi.flowmind.api.CustomAttribute
import com.akazi.flowmind.api.DataSet
import com.akazi.flowmind.api.Filter
import com.akazi.flowmind.api.FilterOperator
import com.akazi.flowmind.api.Id
import com.akazi.flowmind.api.ProcessInstance
import com.akazi.flowmind.api.SortCriteria
import com.akazi.flowmind.api.Task
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IAuditService;
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.workflow.model.IWFTaskModel;
import com.digitech.dossier.workflow.service.IWFProcessService;
import com.digitech.dossier.workflow.service.IWFSearchService
import com.digitech.dossier.workflow.service.IWFUpdateService;

import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.Note;
import com.digitech.jcorbairs.Request;
import com.digitech.jcorbairs.Search;
import com.digitech.jcorbairs.Token
import com.digitech.jcorbairs.admin.OrganizationAdmin
import com.digitech.jcorbairs.admin.OrganizationUserAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager;
import com.digitech.jcorbairs.admin.ProfilsManager;
import com.digitech.jcorbairs.admin.ProfilAdmin;
import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.admin.AuthorityListsManager;
import com.digitech.jcorbairs.exception.XmlException;
import com.digitech.jcorbairs.PrimaryDocument;
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.report.service.IDocumentConvertionService;
import com.digitech.report.service.impl.ooo.DocumentConvertionService

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.DateFormat
import java.text.Normalizer
import java.text.SimpleDateFormat

public class Methods {

    /***
     * Définit une valeur à un index d'un document
     *
     * @param doc
     * @param index
     * @param value
     * @throws Exception
     */
    public static void defineDocumentIndex(Document doc, String index, String value) throws Exception {
        try {
            doc.getContent().modifyFieldValue(index, value);
        } catch (Exception e) {
            try {
                doc.getContent().addFieldValue(index, value);
            } catch (Exception ex) {
                throw new Exception("Exception de la definition du champ : "+ index +" avec la valeur" + value +" : ",ex);
            }
        }
    }

    public static com.digitech.dossier.common.service.IDocument getDocumentMgr()
    {
        return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
    }

    public static IAuditService getAuditMgr()
    {
        return (IAuditService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
    }

    public static IUser getUserMgr()
    {
        return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
    }

    public static IWFProcessService getWkfMgr()
    {
        return (IWFProcessService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_WORKFLOW_PROCESS_MGR);
    }

    public static IWFSearchService getWFSearchService()
    {
        return (IWFSearchService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_WORKFLOW_SEARCH_MGR);
    }

    public static IWFUpdateService getWFUpdateService()
    {
        return (IWFUpdateService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_WORKFLOW_UPDATE_MGR);
    }

    /***
     * Ajout un message dans l'objet data en fonction de son type
     * Le Mesaage peut être ajouté ou écrasé
     *
     * @param data
     * @param key
     * @param message
     * @param overwrite
     */
    public static void addStateMessage(Map<String, Object> data, String key, String message, Boolean overwrite) {
        String previousMsg = data.get(key);
        if(overwrite || previousMsg == null){
            data.put(key, message);
        }else{
            data.put(key, previousMsg + "<br/>" + message);
        }
    }

    /***
     *
     * @param tasksList
     * @param doc
     * @param usrId
     * @param userContext
     * @throws Exception
     */
    public static boolean modifyTaskActorForTaxingUser(List<String> tasksList, IDocument doc, String usrId, UserContext userContext) throws Exception
    {
        BpmSession akaziSession = userContext.getWkUser().getAkaziSession();
        Filter processFilter = null;
        BpmCollection processInstanceCollection = null;
        List<String> processInstanceList = new ArrayList();
        processFilter = new Filter(ProcessInstance.class);

        processFilter.add(new CustomAttribute("AIRSID"), FilterOperator.EQ, doc.getAirsRefId().toString());
        processInstanceCollection = akaziSession.selectProcessInstances(processFilter, SortCriteria.RANDOM);

        Iterator it;

        if (processInstanceCollection != null){
            for (it = processInstanceCollection.iterator(); it.hasNext();){
                ProcessInstance processI = (ProcessInstance)it.next();
                Id processID = processI.getProcess().getId();
                processInstanceList.add(processID.toString());
            }
        }
        else return false;

        if(processInstanceList.size() == 0) return false;

        //Récupération des tâches
        Filter filterTask = new Filter(Task.class);
        filterTask.add(Task.FilterOn.PROCESS, FilterOperator.EQ, processInstanceList.get(0));
        filterTask.add(Task.FilterOn.STATE, FilterOperator.EQ, Task.States.ACTIVE);
		filterTask.add(Task.FilterOn.ACTORS, FilterOperator.CONTAINS, "airs\$"+doc.getField(Constants.FIELD_TAXING_USER_CODE).getValue().toString());


        BpmCollection tasks = akaziSession.selectTasks(filterTask, SortCriteria.RANDOM);
		if(tasks.isEmpty()) return false;

        //Reassignation de la tâche
        Iterator<Task> taskIte = tasks.iterator();
        while( taskIte.hasNext() )
        {
            Task task = taskIte.next();
            //Dans le cas d'une tache attribuee au taxateur uniquement on va juste definir le taxateur comme acteur
			if(tasksList.contains(task.getLabel()) && task.getLabel().equals("MutationCTE") && task.getProcessInstance().getCustomAttributeValue("AIRSID").equals(doc.getAirsRefId().toString()))
            {
                Actor actor = (Actor) task.getActors().get(1);
                String[] userId = new String[1];
                userId[0] = "AIRS\$" + usrId;
                task.reassign(actor.getId().toString(), userId);
                task.refresh();
                actor =  (Actor) task.getActors().get(1);

				task.getProcessInstance().suspend();
            	DataSet ds = task.getProcessInstance().getRootStep().getDataSet();
                ds.set("taxateur", usrId);
                task.getProcessInstance().getRootStep().save(ds);
                task.getProcessInstance().resume();
				return true;
			}else if(tasksList.contains(task.getLabel()) && task.getProcessInstance().getCustomAttributeValue("AIRSID").equals(doc.getAirsRefId().toString())){
                Actor actor = (Actor) task.getActors().get(0);
                String[] userId = new String[1];
                userId[0] = "AIRS\$" + usrId;
                task.reassign(actor.getId().toString(), userId);
                task.refresh();
                actor =  (Actor) task.getActors().get(0);

                task.getProcessInstance().suspend();
            	DataSet ds = task.getProcessInstance().getRootStep().getDataSet();
                ds.set("taxateur", usrId);
                task.getProcessInstance().getRootStep().save(ds);
                task.getProcessInstance().resume();
				return true;
            }
        }
		return false;
    }

    /***
     *
     * @param dataSet
     * @param initialData
     * @throws Exception
     */
    public static void updateDataSet(DataSet dataSet, Map<String, java.lang.Object> initialData) throws Exception {
        if (dataSet == null || initialData == null) {
            return;
        }

        for (Iterator<Map.Entry<String, Object>> iterator = initialData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();

            if (dataSet.contains(key) && value != null)
            {
                if (value instanceof String)
                {
                    dataSet.set(key, (String) value);
                } else {
                    dataSet.setObject(key, value);
                }

            }
        }
    }

    /***
     *
     * @param id
     * @param map
     * @param jeton
     * @return
     */
    public static String getProfilLoad(Integer id, Map<Integer,String> map, Token jeton) {
        String s = map.get(id);
        if (s == null)
        {			
			List<UserAdmin> users = ProfilsManager.load(jeton,id).getUsers();
            for(int i = 0; i < users.size(); ++i)
            {
                if(i == 0)
                {	s="" + users.get(0).getId(); }
                else
                {
                    s += " AIRS\$"  + users.get(i).getId();
                    map.put(id,s);
                }
            }
        }
        return s;
    }

    /***
     *
     * @param document
     * @param contentType
     * @param field
     * @param value
     * @param SecretLevel
     * @return
     */
    public static Document getDossier(IDocument document, String contentType, String field, String value, Integer SecretLevel) throws Exception {
        Document dossier = null;
        ArrayList<Domain> listDomain = new ArrayList<Domain>();
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType));

        Request req = new Request();
        req.addLocution(field, Request.Operator.OPERATOR_EQUAL, value);

        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain);
        int count = search.getNbResults();
        if (count >= 1){
            for(int i = 0 ; i < count ; i++){
                if(search.getDocumentByIndex(i).getSecretLevel() == SecretLevel) dossier = search.getDocumentByIndex(i);
                else {
                    //document.deleteDocument(DossierCoreContext., search.getDocumentByIndex(i));
                    //getDocumentMgr().deleteDocument(new UserCoreContext(DossierCoreContext.getAdminJeton()), search.getDocumentByIndex(i).getId());
                    return getDossier(document, contentType, field, value, SecretLevel);
                }
            }
        } else {
            dossier = new Document(DossierCoreContext.getAdminJeton(), listDomain.get(0), SecretLevel);
            defineDocumentIndex(dossier, field, value);
            dossier.updateContent();
        }
        return dossier;
    }

    public static Document getDossier(Document document, String contentType, String field, String value, Integer SecretLevel) throws Exception {
        Document dossier = null;
        ArrayList<Domain> listDomain = new ArrayList<Domain>();
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType));

        Request req = new Request();
        req.addLocution(field, Request.Operator.OPERATOR_EQUAL, value);

        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain);
        int count = search.getNbResults();
        if (count >= 1){
            for(int i = 0 ; i < count ; i++){
                if(search.getDocumentByIndex(i).getSecretLevel() == SecretLevel) dossier = search.getDocumentByIndex(i);
                else {
                    //document.deleteDocument(DossierCoreContext., search.getDocumentByIndex(i));
                    //getDocumentMgr().deleteDocument(new UserCoreContext(DossierCoreContext.getAdminJeton()), search.getDocumentByIndex(i).getId());
                    return getDossier(document, contentType, field, value, SecretLevel);
                }
            }
        } else {
            dossier = new Document(DossierCoreContext.getAdminJeton(), listDomain.get(0), SecretLevel);
            defineDocumentIndex(dossier, field, value);
            dossier.updateContent();
        }
        return dossier;
    }

    /***
     *
     * @param document
     * @param contentType
     * @param field
     * @param value
     * @param SecretLevel
     * @return
     */
    /*public static Document getDossier(String contentType, String field, String value, Integer SecretLevel) throws Exception {
        Document dossier = null;
        ArrayList<Domain> listDomain = new ArrayList<Domain>();
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType));

        Request req = new Request();
        req.addLocution(field, Request.Operator.OPERATOR_EQUAL, value);

        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain);
        int count = search.getNbResults();
        if (count >= 1){
            for(int i = 0 ; i < count ; i++){
                if(search.getDocumentByIndex(i).getSecretLevel() == SecretLevel) dossier = search.getDocumentByIndex(i);
                else {
                    return getDossier(document, contentType, field, value, SecretLevel);
                }
            }
        } else {
            dossier = new Document(DossierCoreContext.getAdminJeton(), listDomain.get(0), SecretLevel);
            defineDocumentIndex(dossier, field, value);
            dossier.updateContent();
        }
        return dossier;
    }*/

    /***
     *
     * @param folder
     */
    public static void deleteFile(File folder){
        if(folder.isDirectory()){
            for(File f:folder.listFiles()){
                deleteFile(f);
            }
        }else{
            folder.delete();
        }
    }

    /***
     *
     * @param task
     * @param output
     * @param document
     */
    public static void submitTask(IWFTaskModel task, String output, IDocument document, String groovyName) throws Exception{

		Map<String, String> airsValueMap = null;
		if(task != null){
			/*if(Constants.AKAZI_OUTPUT_REQUEST_MOORED.equalsIgnoreCase(output)) getWFUpdateService().submitTask(UserContext.getInstance(), task, Constants.AKAZI_OUTPUT_ARCHIVE, document);
			else*/ getWFUpdateService().submitTask(UserContext.getInstance(), task, output, document);
		}

        // Update current document with the airsValueMap ( from preference.xml )
        if(airsValueMap != null){
            for(Map.Entry<String, String> fieldEntry : airsValueMap.entrySet()){
                String airsValueFieldCode = fieldEntry.getKey();
                IField field = document.getField(airsValueFieldCode);
                if(field != null) field.setValue(fieldEntry.getValue());
            }
        }

        // LOCK
        // getDocumentMgr().lockDocument(UserContext.getInstance(), document, com.digitech.dossier.common.model.backend.Constants.LockType.MANUAL);
        // UPDATE
        // getDocumentMgr().updateDocument(UserContext.getInstance(), document);
        // UNLOCK
        // getDocumentMgr().unlockDocument(UserContext.getInstance(), document);

        // Cas pour WorkflowValidatedSimpleViewInit et WorkflowInTreatmentSimpleViewInit
        if(Constants.AKAZI_OUTPUT_VALID.equalsIgnoreCase(output) || Constants.AKAZI_OUTPUT_IN_TREATMENT.equalsIgnoreCase(output)) {
            if (Constants.AKAZI_TASK_ARCHIVE_CTE.equalsIgnoreCase(task.getName()) || Constants.AKAZI_TASK_MUTATION_CTE.equalsIgnoreCase(task.getName())) {
                try {
                    document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());
                } catch (Exception e) {
                    document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());
                }
            } else {
                try {
                    document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_IN_TREATMENT_ID.toString());
                } catch (Exception e) {
                    document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_IN_TREATMENT_ID.toString());
                }
            }
        }
        // Cas pour WorkflowUrgentSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_URGENT.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_URGENT_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_URGENT_ID.toString());
            }
        }
        // Cas pour WorkflowResponseObtainedSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_CENTRAL_RESPONSE_OK.equalsIgnoreCase(output) && "WorkflowResponseObtainedSimpleViewInit".equalsIgnoreCase(groovyName)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_RESPONSE_OBTAINED_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_RESPONSE_OBTAINED_ID.toString());
            }
        }
        // Cas pour WorkflowCreateNSSCICASimpleViewInit
        else if(Constants.AKAZI_OUTPUT_CENTRAL_RESPONSE_OK.equalsIgnoreCase(output) && "WorkflowCreateNSSCICASimpleViewInit".equalsIgnoreCase(groovyName)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_CREATE_NIP_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_CREATE_NIP_ID.toString());
            }

            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_SERVICE_CODE, Constants.LIST_SERVICE_ITEM_SCAN_ID.toString());
            } catch (XmlException e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_SERVICE_CODE, Constants.LIST_SERVICE_ITEM_SCAN_ID.toString());
            }
        }
        // Cas pour WorkflowRejectedSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_REJECTED.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_TREAT_ID.toString());
            }
        }
        // Cas pour WorkflowRefusedUrgentSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_REFUSED_URGENT.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_URGENT_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_URGENT_ID.toString());
            }
        }
        // Cas pour WorkflowRefusalAwaitingResponseSimpleViewInit et WorkflowAwaitingResponseSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_REFUSAL_AWAITING_RESPONSE.equalsIgnoreCase(output) || Constants.AKAZI_OUTPUT_AWAITING_RESPONSE.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_WAITING_FOR_REPLY_ID.toString());
            }
        }
        // Cas pour WorkflowToCreateNSSSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_TO_CREATE_NSS.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_CREATE_NSS_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_CREATE_NSS_ID.toString());
            }

            document.getAirsDocument().getInnerDocument().setSecretLevel(Constants.SECRET_LEVEL_AC);
        }
        // Cas pour WorkflowMutationInProgressSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_MUTATION_IN_PROGRESS.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_MUTATION_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_MUTATION_ID.toString());
            }
        }
        // Cas pour WorkflowEmailIndexeSimpleViewInit, WorkflowNIPCreatedSimpleView et WorkflowCorrectedIndexSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_EMAIL_INDEXE.equalsIgnoreCase(output) ||Constants.AKAZI_OUTPUT_CORRECTED_INDEX.equalsIgnoreCase(output)
                || Constants.AKAZI_OUTPUT_NIP_CREATED.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID.toString());
            }
            if(Constants.AKAZI_OUTPUT_EMAIL_INDEXE.equalsIgnoreCase(output)){
                try {
                    document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_SERVICE_CODE, Constants.LIST_SERVICE_ITEM_SCAN_ID.toString());
                } catch (XmlException e) {
                    document.getAirsDocument().getContents().addFieldValue(Constants.LIST_SERVICE_CODE, Constants.LIST_SERVICE_ITEM_SCAN_ID.toString());
                }
            }
        }
        // Cas pour WorkflowAwaitingAffiliatePSASimpleViewInit
        else if(Constants.AKAZI_OUTPUT_AFFILIATE_PSA.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_AFFILIATE_PSA_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_AFFILIATE_PSA_ID.toString());
            }
        }
        // Cas pour WorkflowAwaitingAffiliatePCISimpleViewInit
        else if(Constants.AKAZI_OUTPUT_AFFILIATE_PCI.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_AFFILIATE_PCI_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_AFFILIATE_PCI_ID.toString());
            }
        }
        // Cas pour WorkflowAwaitingAffiliateEMPSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_AFFILIATE_EMP.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_AFFILIATE_EMP_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_AFFILIATE_EMP_ID.toString());
            }
        }
        // Cas pour WorkflowRequestMooredSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_REQUEST_MOORED.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_REQUEST_MOORED_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_REQUEST_MOORED_ID.toString());
            }
        }
        // Cas pour WorkflowToValidSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_TO_VALID.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_VALID_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_VALID_ID.toString());
            }
        }
        // Cas pour WorkflowToArchiveSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_ARCHIVE.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());
            }
        }
        // Cas pour WorkflowAdressCreatedSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_ADRESS_CREATED.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ADRESS_CREATED_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ADRESS_CREATED_ID.toString());
            }
        }
		// Cas pour WorkflowToControlSimpleViewInit
        else if(Constants.AKAZI_OUTPUT_TO_CONTROL.equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_TO_CONTROL_ID.toString());
            }
        }
		// Cas pour WorkflowToControlSimpleViewInit
        else if(Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString().equalsIgnoreCase(output)){
            try{
                document.getAirsDocument().getContents().modifyFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());
            } catch (Exception e){
                document.getAirsDocument().getContents().addFieldValue(Constants.LIST_STATUS_CODE, Constants.LIST_STATUS_ITEM_ARCHIVE_ID.toString());
            }
        }
        // Si non reconnu
        else throw new Exception("Methods.submitTask() : Output non défini : " + output);

        // Mise é jour des modifications
        document.getAirsDocument().updateContents();
    }

    public static boolean isValidActor(int userId, int organizationId) throws Exception{
        boolean result = false;
        OrganizationAdmin organization = OrganizationsManager.load(DossierCoreContext.getAdminJeton(), organizationId);
        List<OrganizationUserAdmin> organizationListUser = organization.getUsers();
        for(OrganizationUserAdmin organizationUser : organizationListUser){
            if(organizationUser.getUser().getId() == userId){
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean isResponsableActor(int userId) throws Exception{
        boolean result = false;
        List<ProfilAdmin> profilList = ProfilsManager.loadAll(DossierCoreContext.getAdminJeton());
        for(ProfilAdmin profil : profilList){
            if(profil.getCode().startsWith("RESPONSABLE_")){
                if(profil.getUserIds().contains(userId)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

	public static boolean isActorInProfil(int userId, int profilId) throws Exception{
        ProfilAdmin profil = ProfilsManager.load(DossierCoreContext.getAdminJeton(), profilId);
		return profil.getUserIds().contains(userId);
    }

    public static void convertAttachment(IDocument document, File fileOffice) throws Exception{
		String filePDFName = getPDFFileName(fileOffice.getName());
		File filePDF = new File(fileOffice.getParent()+"/"+filePDFName);
		getDocumentConversionService().convert(fileOffice, filePDF);
		PrimaryDocument primaryDoc = new PrimaryDocument(filePDF.getName(), "00-"+filePDF.getName());
		document.getAirsDocument().getInnerDocument().addOrUpdatePrimaryDocument(primaryDoc, filePDF.getParent());
		document.getAirsDocument().getInnerDocument().updateContent();
    }
	
	private static String getPDFFileName(String fileName){
		String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		fileName = fileName.replaceAll("\\(","_").replaceAll("\\)","_").replaceAll(" ","_");
		return fileName.replaceAll(extension, Constants.APPLICATION_PDF_EXTENSION.toLowerCase());
	}
	
	private static IDocumentConvertionService getDocumentConversionService() {
		IDocumentConvertionService docConversionService = new DocumentConvertionService();
		return docConversionService;
	}

	public static String getAlTermValue(Integer id, Integer alId) throws IdentificationException, ServerException {
        String result = null;
        List<AuthorityListTermAdmin> listValues = AuthorityListsManager.loadTermRoots(DossierCoreContext.getAdminJeton(), alId);
        for (AuthorityListTermAdmin a : listValues) {
            if (id == a.getId()) {
                result = a.getValue1();
                break;
            }
        }
        return result;
    }

    public static String getAlTermValue(String sortKey, Integer alId) throws IdentificationException, ServerException {
        String result = null;
        List<AuthorityListTermAdmin> listValues = AuthorityListsManager.loadTerms(DossierCoreContext.getAdminJeton(), alId);
        for (AuthorityListTermAdmin a : listValues) {
            if (sortKey.equalsIgnoreCase(a.getSortKey())) {
                result = String.valueOf(a.getId());
                break;
            }
        }
        return result;
    }

    public static String getGroupDistributionForRentes(String name) {
        String result = "";
        try {
            if(name == null || "".equalsIgnoreCase(name) || name.isEmpty()){
                result = Constants.MAP_RENTES_DISTRIBUTION.get(Constants.MAP_RENTES_DISTRIBUTION.values().toArray()[0].toString());
            }
            else{
                name = stripAccents(name);
                for (Map.Entry<String, String> entry : Constants.MAP_RENTES_DISTRIBUTION.entrySet()) {
                    String[] tab = entry.getValue().trim().split("-");
                    if(startsBetween(name, tab[0].toUpperCase().charAt(0), tab[1].toUpperCase().charAt(0))){
                        return entry.getKey();
                    }
                }
            }
        } catch (Exception e) {
            result = Constants.MAP_RENTES_DISTRIBUTION.get(Constants.MAP_RENTES_DISTRIBUTION.values().toArray()[0].toString());
        }
        return result;
    }

    public static String getGroupDistributionForPSA(String name) {
        String result = "";
        try {
            if(name == null || "".equalsIgnoreCase(name) || name.isEmpty()){
                result = Constants.MAP_PSA_DISTRIBUTION.get(Constants.MAP_PSA_DISTRIBUTION.values().toArray()[0].toString());
            }
            else{
                name = stripAccents(name);
                for (Map.Entry<String, String> entry : Constants.MAP_PSA_DISTRIBUTION.entrySet()) {
                    String[] tab = entry.getValue().trim().split("-");
                    if(startsBetween(name, tab[0].toUpperCase().charAt(0), tab[1].toUpperCase().charAt(0))){
                        return entry.getKey();
                    }
                }
            }
        } catch (Exception e) {
            result = Constants.MAP_PSA_DISTRIBUTION.get(Constants.MAP_PSA_DISTRIBUTION.values().toArray()[0].toString());
        }
        return result;
    }

    public static boolean startsBetween(String s, char lowest, char highest) {
        char c = s.charAt(0);
        c = Character.toUpperCase(c);
        return c >= lowest && c <= highest;
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static String synchronizeAvisMutation(IDocument document) throws Exception{
        String result = null;
        String numAvisMutation = document.getField(Constants.FIELD_NAVISMUTATION_CODE).getValue().toString();
        Class.forName(Constants.DB_GLOBAZ_DRIVER);
        Connection connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_INFORMATIONS_BY_NAVISMUTATION);
        preparedStatement.setString(1, numAvisMutation);
        ResultSet resultSet = preparedStatement.executeQuery();
        Document doc = getDocumentMgr().getDocument(DossierCoreContext.getAdminJeton(), document.getAirsRefId()).getAirsDocument().getInnerDocument();

        int countRow = 0;
        while (resultSet.next()) {
            ++countRow;
            defineDocumentIndex(doc, Constants.FIELD_LASTNAME_AFF_CODE, resultSet.getString(2));
            defineDocumentIndex(doc, Constants.FIELD_FIRSTNAME_AFF_CODE, resultSet.getString(3));
            defineDocumentIndex(doc, Constants.FIELD_REF_CAISSE_AVS_CODE, resultSet.getString(4));
            defineDocumentIndex(doc, Constants.FIELD_NIDE_CODE, resultSet.getString(5));
            defineDocumentIndex(doc, Constants.LIST_FORME_JURIDIQUE_CODE, getAlTermValue(resultSet.getString(6), Constants.LIST_FORME_JURIDIQUE_ID));
            defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, resultSet.getString(7).trim().replaceAll("\\.", ""));
            defineDocumentIndex(doc, Constants.LIST_FORME_JURIDIQUE_CODE, getAlTermValue(resultSet.getString(8), Constants.LIST_CODE_MOTIF_OFAS_ID));
        }

        if (countRow == 0){
            result = "ERREUR - Le numero d'avis de mutation suivant n'est pas valide : " + numAvisMutation;
        } else if (countRow > 1){
            result = "ATTENTION - Le numero d'avis de mutation : "+numAvisMutation+" retourne plusieurs résultats ne peut donc pas etre synchronise le document.";
        } else {
            doc.updateContent();

            // Recherche du dossier parent
            Document dossier = getDossier(document, Constants.CTY_AFFILIATED_FOLDER, Constants.FIELD_AFF_CODE, document.getField(Constants.FIELD_AFF_CODE).getValue().toString(), Constants.SECRET_LEVEL_DEFAULT);
            // Rattachement au dossier père
            if (dossier != null){
                doc.addParent(dossier);
                doc.updateContent();
            }
        }

        return result;
    }

    public static void createAffiliateDocument(IDocument documentAvisMutation, Integer organizationId, String nip, boolean addComment, String comment) throws Exception{
        Document document = null;

        try {
            Domain domain = new Domain(DossierCoreContext.getAdminJeton(), Constants.CTY_AFFILIATED_DOCUMENT);
            document = new Document(DossierCoreContext.getAdminJeton(), domain, Integer.parseInt(Constants.MAP_SERVICE_SECRET_LEVEL.get(Constants.MAP_ORGANIZATION_SERVICE.get(organizationId))));
            document.updateContent();

            if (nip != null && !nip.isEmpty()) {
                defineDocumentIndex(document, Constants.FIELD_AFF_CODE, nip);
                Document dossier = getDossier(document, Constants.CTY_AFFILIATED_FOLDER, Constants.FIELD_AFF_CODE, nip, Constants.SECRET_LEVEL_DEFAULT);
                if (dossier != null) {
                    document.addParent(dossier);
                    document.updateContent();
                }

                Map<String, String> mapInformations = getInformationsWebAI(nip);
                defineDocumentIndex(document, Constants.FIELD_NSS_CODE, mapInformations.get("nss"));
                defineDocumentIndex(document, Constants.LIST_TITLE_CODE, mapInformations.get("title"));
                defineDocumentIndex(document, Constants.FIELD_LASTNAME_AFF_CODE, mapInformations.get("lastName"));
                defineDocumentIndex(document, Constants.FIELD_FIRSTNAME_AFF_CODE, mapInformations.get("firstName"));
                defineDocumentIndex(document, Constants.FIELD_ADRESS_AFF_CODE, mapInformations.get("adress"));

                defineDocumentIndex(document, Constants.LIST_STATUS_CODE, String.valueOf(Constants.LIST_STATUS_ITEM_TO_DISTRIBUTE_ID));
                defineDocumentIndex(document, Constants.LIST_SERVICE_CODE, String.valueOf(Constants.MAP_ORGANIZATION_SERVICE.get(organizationId)));

                if(organizationId != Constants.ORGANIZATION_PSA_1_ID && organizationId != Constants.ORGANIZATION_PSA_2_ID)
                    defineDocumentIndex(document, Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, String.valueOf(organizationId));
                else defineDocumentIndex(document, Constants.FIELD_ORGANIZATION_WORKFLOW_CODE, String.valueOf(Methods.getGroupDistributionForPSA(mapInformations.get("lastName"))));
                defineDocumentIndex(document, Constants.FIELD_DATE_CODE, convertDateAirsToAirs(String.valueOf(documentAvisMutation.getField(Constants.FIELD_DATE_CODE).getValue())));
                defineDocumentIndex(document, Constants.LIST_TYPE_CODE, String.valueOf(Constants.LIST_TYPE_ITEM_AVMUT_ID));
                defineDocumentIndex(document, Constants.FIELD_DESCRIPTION_CODE, "Avis de mutation");

            } else {
                defineDocumentIndex(document, Constants.LIST_STATUS_CODE, String.valueOf(Constants.LIST_STATUS_ITEM_TO_CREATE_NIP_ID));
                defineDocumentIndex(document, Constants.FIELD_DATE_CODE, convertDateAirsToAirs(String.valueOf(documentAvisMutation.getField(Constants.FIELD_DATE_CODE).getValue())));
                defineDocumentIndex(document, Constants.FIELD_SCANNER_USER_CODE, String.valueOf(Constants.USER_SCAN_ID));
                defineDocumentIndex(document, Constants.LIST_TYPE_CODE, String.valueOf(Constants.LIST_TYPE_ITEM_AVMUT_ID));
                defineDocumentIndex(document, Constants.FIELD_DESCRIPTION_CODE, "Avis de mutation");
            }

            if (addComment) {
                Note myNote = new Note(Constants.AIRS_NOTE_ID);
                String service = "";
                if(Constants.ORGANIZATION_PSA_1_ID == organizationId || Constants.ORGANIZATION_PSA_2_ID == organizationId) service = "PSA";
                else if(Constants.ORGANIZATION_PCI_ID == organizationId) service = "PCI";
                else if(Constants.ORGANIZATION_EMPLOYEURS_ID  == organizationId) service = "Employeurs";
                myNote.setText("A destination du service " + service);
                myNote.setPublic();
                document.addNote(myNote);
            }

            if(comment != null){
                Note myNote = new Note(Constants.AIRS_NOTE_ID);
                myNote.setText(comment);
                myNote.setPublic();
                document.addNote(myNote);
            }

            File exportPath = new File(ExportUtils.getExportPDFDirectory());
            if (!exportPath.exists()) exportPath.mkdirs();
            for (IAttachment attachment : documentAvisMutation.getAttachments(new UserCoreContext(DossierCoreContext.getAdminJeton()))) {
                documentAvisMutation.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), ExportUtils.getExportPDFDirectory());
                PrimaryDocument primaryDoc = new PrimaryDocument(attachment.getFileName(), attachment.getFileName());
                document.addOrUpdatePrimaryDocument(primaryDoc, exportPath.getAbsolutePath());
            }

            document.updateContent();
        }catch(Exception e){
            if(document != null) document.destroy();
            throw e;
        }

    }

    private static String convertDateAirsToAirs(String date){
        DateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat sdfOutput = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date dateInput = sdfInput.parse(date);
        return sdfOutput.format(dateInput);
    }

    public static Map<String, String> getInformationsWebAI(String nip) throws Exception{
        Map<String, String> result = new HashMap();
        Class.forName(Constants.DB_GLOBAZ_DRIVER);
        Connection connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD);

        PreparedStatement preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_NSS_BY_NAFF);
        preparedStatement.setString(1, nip);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            result.put("nss", resultSet.getString(1).trim().replaceAll("\\.", ""));
        }else{
            throw new Exception("ERREUR - Le NIP suivant n'est pas valide : "+nip);
        }

        preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_IDENTITY_BY_NAFF);
        preparedStatement.setString(1, nip);
        resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            // Définition du titre
            if(resultSet.getInt(1) == Integer.parseInt(Constants.DB_GLOBAZ_CODE_TITLE_MR))
                result.put("title", String.valueOf(Constants.LIST_TITLE_ITEM_MR_ID));
            else if(resultSet.getInt(1) == Integer.parseInt(Constants.DB_GLOBAZ_CODE_TITLE_MME))
                result.put("title", String.valueOf(Constants.LIST_TITLE_ITEM_MME_ID));

            // Définition du nom et prénom
            result.put("lastName", resultSet.getString(2));
            result.put("firstName", resultSet.getString(3));
        }

        // Définition des adresses
        preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF);
        preparedStatement.setString(1, nip);
        resultSet = preparedStatement.executeQuery();
        String adress = "";
        if(resultSet.next()){
            if("".equals(adress)) adress = resultSet.getString(1);
            else adress += " ; "+resultSet.getString(1);
        }
        result.put("adress", adress);

        return result;
    }
}