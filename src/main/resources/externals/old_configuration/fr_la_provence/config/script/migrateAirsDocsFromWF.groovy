import com.digitech.dossier.workflow.model.impl.WFTask.WFActor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;

import com.akazi.flowmind.api.BpmCollection;
import com.akazi.flowmind.api.BpmSession;
import com.akazi.flowmind.api.Filter;
import com.akazi.flowmind.api.FilterOperator;
import com.akazi.flowmind.api.ProcessInstance;
import com.akazi.flowmind.api.SortCriteria;
import com.akazi.flowmind.api.Task;
import com.akazi.flowmind.api.User;
import com.digitech.ged.common.dal.model.IDocument;
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.service.IDocument;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.workflow.ConstantsWF;
import com.digitech.dossier.workflow.model.IWFTaskModel;
import com.digitech.dossier.workflow.model.IWFUser;
import com.digitech.dossier.workflow.model.impl.WFTask;
import com.digitech.dossier.workflow.service.IWFTaskService;
import com.digitech.dossier.workflow.service.impl.WFSearchMgr;
import com.digitech.dossier.workflow.service.impl.WFTaskMgr;
import com.digitech.jcorbairs.AdminSQLResponse;
import com.digitech.jcorbairs.SQLQuery;
import com.digitech.jcorbairs.Term;

Logger log = scriptLogger;

log.debug("IN");

int count = 0, countError = 0;
IWFTaskService wfTaskService = new WFTaskMgr();
IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
IAuthorityList authListMgr = (IAuthorityList) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_AUTHORITYLIST_MGR);

UserCoreContext userContext = UserUtils.getAdminUserContext();

Map<String, Object> attrs = new HashMap<String, Object>();
attrs.put(ConstantsWF.FM_ATR_TASK_STATE, ConstantsWF.FM_ATR_TASK_STATE_ACTIVE);

List<Term> termList = authListMgr.getTerms("FAC_ETAT");

List<IWFTaskModel> tasks = wfTaskService.getTasks(UserUtils.getAdminUserContext(), attrs, null, false);

/*
System.out.println("SYNCHRONISATION WF/AIRS step I");

if( tasks != null && tasks.size() > 0 ) {
  log.debug(tasks.size() + " taches a traiter...");

  String docId;
  WFTask taskW4;
  com.digitech.dossier.common.model.backend.airs.IDocument doc;

  Map<String,String> mapping = new HashMap<String, String>();
  mapping.put("AValider1", "A valider");
  mapping.put("AValider2", "A valider");
  mapping.put("StandByTrait", "Stand-by traitement");
  mapping.put("Bloquee1", "Bloquée");
  mapping.put("AEngagerPousser", "A Engager-Pousser");
  mapping.put("AEngager", "A engager");
  mapping.put("ATraiter", "A traiter");
  mapping.put("StandByValid", "Stand-by validation");
  
  for(Map.Entry<String, String> entry : mapping.entrySet()) {
    log.debug(entry.getKey() + ":" + entry.getValue());
  }

  for(IWFTaskModel task : tasks) {
    taskW4 = (WFTask)task;

    docId = task.geAttributeValue(ConstantsWF.FM_CATR_AIRSID);

    doc = documentMgr.getDocument(userContext, Integer.valueOf(docId));

    String etatDoc = "";
    Object valChamp = doc.getField("FAC_ETAT").getValue();
    if( valChamp != null ) {
      for(Term term : termList) {
        if(term.getId().equals(Integer.valueOf(valChamp))) {
          etatDoc = term.getPreferedValue();
          break;
        }
      }
    }

    if( mapping.containsKey(taskW4.getName()) && mapping.get(taskW4.getName()).equals(etatDoc) ) {
      //log.debug("Etat OK : docId=[" + docId + "] etatTache=[" + taskW4.getName() + "] etatDoc=[" + etatDoc + "] numFac=[" + doc.getField("FAC_NUM").getValue() + "]");
    }
    else {
      log.warn("Etat KO : docId=[" + docId + "] etatTache=[" + taskW4.getName() + "] etatDoc=[" + etatDoc + "] numFac=[" + doc.getField("FAC_NUM").getValue() + "]");

      String nouvelEtatDoc = mapping.get(taskW4.getName());
      Integer nouvelEtatId = null;

      for(Term term : termList) {
        if(term.getPreferedValue().equals(nouvelEtatDoc)) {
          nouvelEtatId = term.getId();
          break;
        }
      }

      if( nouvelEtatId != null ) {
        log.debug("Mise a jour document nouvelEtatDoc=[" + nouvelEtatDoc + "] nouvelEtatId=[" + nouvelEtatId + "]");
        
        FieldUtils.setValue(doc, "FAC_ETAT", nouvelEtatId);
        documentMgr.updateDocument(userContext, doc);

        count++;
      }
      else log.error("Impossible de trouver l'id pour l'etat [" + nouvelEtatDoc + "]");
    }
  }
}
else
  log.debug("Aucune tache a traiter.");

log.debug("OUT : " + count + " taches traitees.");*/


System.out.println("SYNCHRONISATION WF/AIRS step II");
 
if( tasks != null && tasks.size() > 0 ) {
  log.debug(tasks.size() + " taches a traiter...");
 
  String docId;
  WFTask taskW4;
  com.digitech.dossier.common.model.backend.airs.IDocument doc;
 
  for(IWFTaskModel task : tasks) {
    taskW4 = (WFTask)task;
     
    if( taskW4.getName().equals("AEngager") ) {
      docId = task.geAttributeValue(ConstantsWF.FM_CATR_AIRSID);
 
      doc = documentMgr.getDocument(userContext, Integer.valueOf(docId));

      if( doc.getSecretLevel() == 600 ) {
        List<WFActor> actors = taskW4.getActors();
        
        if( actors == null || actors.size() != 1 || actors.get(0).isGroup() ) {
          log.warn("Liste d'acteur non prise en charge size=[" + (actors == null ? 0 : actors.size()) + "] ou acteur de type groupe.");
        }
        else {
          String usrId;
          int pos;
          if( (pos=actors.get(0).getId().indexOf('$')) != -1 )
            usrId = actors.get(0).getId().substring(pos+1);
          else
            usrId = actors.get(0).getId();
          
          try {
            Integer.valueOf(usrId);
          }
          catch(NumberFormatException ex) {
            log.error(ex.toString(), ex);
            usrId = null;
          }  
          
          if( usrId != null ) {
            log.debug("Acteur [" + actors.get(0).getLabel() + "] usrId=[" + usrId + "]");
            
            SQLQuery query = new SQLQuery(UserUtils.getAdminUserContext().getJeton(), "SELECT SECRET FROM CORRESP_ENG WHERE ASSISTANT=" + usrId);
            AdminSQLResponse admResponse = new AdminSQLResponse(query.getFullResult());
            List<String> secrets = admResponse.getAllValues("SECRET");
            if( secrets != null && secrets.size() > 0 ) {
              Integer newSecret = null;
              try {
                newSecret = Integer.valueOf(secrets.get(0).toString());
              }
              catch(NumberFormatException ex) {
                log.error(ex.toString(), ex);
                newSecret = null;
              }
    
              if( newSecret != null ) {
                log.debug("Update docId=[" + docId + "] facNum=[" + doc.getField("FAC_NUM").getValue() + "] >> FAC_TRAIT1=[" + usrId + "] secretLevel=[" + newSecret + "]...");
                
                FieldUtils.setValue(doc, "FAC_TRAIT1", usrId);
                documentMgr.updateDocument(userContext, doc, true);

                doc.getAirsDocument().setSecretLevel(newSecret.intValue());
                doc.getAirsDocument().updateContents();
                
                count++;
              }
            }
            else
              log.warn("La requête sur la table CORRESP_ENG n'a pas renvoyé d'enregistrement !");
          }
        }
      }
    }
  }
}
else
  log.debug("Aucune tache a traiter.");
 
log.debug("OUT : " + count + " taches traitees.");

///////////////////////////////////////////////////////////////

/*System.out.println("RECUPERATION WF HS");

count = 0;

attrs.put(ConstantsWF.FM_ATR_TASK_STATE, ConstantsWF.FM_ATR_TASK_STATE_DONE);
attrs.put(ConstantsWF.FM_ATR_TASK_NAME, "AValider1");

IWFUser wfUser = UserUtils.getAdminUserContext().getWkUser();
BpmSession session = wfUser.getAkaziSession();
Filter processFilter = new Filter(ProcessInstance.class);
processFilter.add(ProcessInstance.FilterOn.STATE, FilterOperator.EQ, ProcessInstance.States.FINISHED);

BpmCollection pis = session.selectProcessInstances(processFilter, SortCriteria.RANDOM);
if( pis != null && pis.size() > 0 ) {
  log.debug(pis.size() + " instance terminees");

  SortCriteria sc = new SortCriteria(Task.class);
  sc.add(Task.SortBy.COMPLETION_DATE, false);

  String label, docId;
  Task currentTask;
  Object valChamp;
  BpmCollection piTasks;
  
  for(ProcessInstance pi : pis) {
    Filter taskFilter = new Filter(Task.class);
    taskFilter.add(Task.FilterOn.PROCESS_INSTANCE, FilterOperator.EQ, pi);
  
    piTasks = session.selectTasks(taskFilter, sc);
    if( piTasks != null ) {
      currentTask = (Task)piTasks.get(0);
      label = currentTask.getLabel();
      
      if( label != null && label.equals("AValider1") ) {
        docId = pi.getCustomAttributeValue(ConstantsWF.FM_CATR_AIRSID);
        
        doc = documentMgr.getDocument(userContext, Integer.valueOf(docId));
    
        valChamp = doc.getField("FAC_ETAT").getValue();
        
        if( valChamp != null && valChamp.equals(Integer.valueOf(113)) ) {
            
          log.debug("Update docId=[" + docId + "] facNum=[" + doc.getField("FAC_NUM").getValue() + "] >> LabelTache=[" + label + "] - EtatFac=[A valider]...");
          
          try {
            FieldUtils.setValue(doc, "FAC_ETAT", Integer.valueOf(115));
            documentMgr.updateDocument(userContext, doc);
            count++;
          }
          catch(Exception ex) {
            log.error(ex.toString(), ex);
          }

          countError++;
        }
      }
    }
    else log.error("Pas de tache pour l'instance [" + pi.getId().toString() + "] ?");
  }
}

log.debug("OUT : " + count + " documents traites et " + countError + " erreurs.");*/