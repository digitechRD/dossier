import com.digitech.dossier.common.model.backend.report.value.airs.ReportOrganization;

import java.io.File
import java.io.IOException
import java.security.InvalidParameterException
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.Map.Entry

import javax.mail.MessagingException
import javax.xml.bind.JAXBException

import org.apache.commons.collections.map.HashedMap
import org.apache.commons.mail.EmailException
import org.xml.sax.SAXException

import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.exception.InvalidConfigurationException
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.ILocutionModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.ITask
import com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.model.backend.airs.impl.LocutionModel
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.Constants
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.dossier.common.utils.DateUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException

import freemarker.template.TemplateException

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// Map<String, IParameter> parameterMap map des paramètres
/************************************************/

org.slf4j.Logger logger = scriptLogger;
Map<String, ITask.IParameter> theParameterMap = parameterMap;


logger.debug("Script task: checker_dueDate.groovy --- Start");

ITask.IParameter intervalParam = theParameterMap.get("interval");
int interval = intervalParam.getValue();
logger.debug("Interval parameter value is " + interval);

// Construct locution for the COURRIER_IN
ILocutionModel locutionModel = new LocutionModel();
DocumentUtils.buildLocutionModelWithDate(locutionModel, "D_ECHEANCE", Operator.OPERATOR_VALUE_EQUAL, DateUtils.computeDate(new Date(), "-", Integer.valueOf(interval)));

ILocutionModel subLocutionModel = new LocutionModel();
locutionModel.addSubLocution(subLocutionModel);
subLocutionModel.setOperator(Operator.OPERATOR_BOOLEAN_OR);
DocumentUtils.buildLocutionModel(subLocutionModel, "T_ETAT_COURRIER", Operator.OPERATOR_VALUE_EQUAL, String.valueOf(CourrierScriptUtils.getTermID(null,"T_ETAT_COURRIER","REPONDU")));
DocumentUtils.buildLocutionModel(subLocutionModel, "T_ETAT_COURRIER", Operator.OPERATOR_VALUE_EQUAL, String.valueOf(CourrierScriptUtils.getTermID(null,"T_ETAT_COURRIER","DIFFUSE")));

// Compute search for the COURRIER_IN
List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList("COU_COURRIER_IN"), null);

// Construct locution for the COURRIER_OUT
locutionModel = new LocutionModel();
DocumentUtils.buildLocutionModelWithDate(locutionModel, "D_ECHEANCE", Operator.OPERATOR_VALUE_EQUAL, DateUtils.computeDate(new Date(), "-", Integer.valueOf(interval)));

// Compute search for the COURRIER_OUT
documentList.addAll(DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList("COU_COURRIER_OUT"), null));

// Get Documents
Map<Integer, List<IDocument>> userIDocumentListMap = new HashedMap();
Map<Integer, List<IDocument>> serviceIDocumentListMap = new HashedMap();

if (documentList != null) {
  for (IDocument doc : documentList) {
    // Construct the map for users
    Integer idUserProp = (Integer) doc.getField("U_PROPRIETAIRE").getValue();
    if (idUserProp != null) {
      fillMap(userIDocumentListMap, idUserProp, doc);
    } else {
      // Construct the map for services
      Integer serviceUserProp = (Integer) doc.getField("O_PROPRIETAIRE").getValue();
      if (serviceUserProp != null) {
        fillMap(serviceIDocumentListMap, serviceUserProp, doc);
      }
    }
  }
}

File theMailTemplate = new File(CourrierUtils.getCourrierTemplateFolderPath(), "relance.htm");
if (!userIDocumentListMap.isEmpty()) {
  // Send mail for each users
  for (Entry<Integer, List<IDocument>> entry : userIDocumentListMap.entrySet()) {
    sendMailToUser(entry, theMailTemplate, interval);
  }
}
if (!serviceIDocumentListMap.isEmpty()) {
  // Send mail for each service
  for (Entry<Integer, List<IDocument>> entry : serviceIDocumentListMap.entrySet()) {
    sendMailToOrg(entry, theMailTemplate, interval);
  }
}

private void fillMap(Map<Integer, List<IDocument>> propIDocumentListMap, Integer idProp, IDocument doc) {
  List<IDocument> documentUserList = null;
  if (propIDocumentListMap.containsKey(idProp)) {
    documentUserList = propIDocumentListMap.get(idProp);
  } else {
    documentUserList = new ArrayList<IDocument>();
  }
  documentUserList.add(doc);
  propIDocumentListMap.put(idProp, documentUserList);
}

private void sendMailToUser(Entry<Integer, List<IDocument>> entry, File theMailTemplate, int interval) throws InvalidParameterException, ServerException, IdentificationException, IOException, TemplateException,
JAXBException, SAXException, EmailException, InvalidConfigurationException, MessagingException {
  Integer userId = entry.getKey();
  List<IDocument> theDocumentUserList = entry.getValue();
  IUser userService = (IUser) ServiceManager.getInstance().getService(Constants.SERVICE_AIRS_USER_MGR);
  UserCoreContext adminContext = UserUtils.getAdminUserContext();
  ApplicationUtils.sendMail(
    DossierCoreContext.getParamsInfos().getWebAppURL(), 
    adminContext, 
    theDocumentUserList, 
    theMailTemplate, 
    new ReportPerson(adminContext, userService.getUser(userId)),
    BundleUtils.getTranslation("mail_subject_courrier_revival"), 
    getMapPropertiesFilled(interval));
}

private void sendMailToOrg(Entry<Integer, List<IDocument>> entry, File theMailTemplate, int interval) throws InvalidParameterException, ServerException, IdentificationException, IOException, TemplateException,
JAXBException, SAXException, EmailException, InvalidConfigurationException, MessagingException {
  Integer orgId = entry.getKey();
  List<IDocument> theDocumentOrgList = entry.getValue();
  IServer serverService = (IServer) ServiceManager.getInstance().getService(Constants.SERVICE_AIRS_SERVER_MGR);
  UserCoreContext adminContext = UserUtils.getAdminUserContext();
  ApplicationUtils.sendMail(
    DossierCoreContext.getParamsInfos().getWebAppURL(),
    adminContext,
    theDocumentOrgList,
    theMailTemplate,
    new ReportOrganization(serverService.getOrganization(adminContext.getJeton(), orgId)),
    BundleUtils.getTranslation("mail_subject_courrier_revival"),
    getMapPropertiesFilled(interval));
}

private Map<String, Object> getMapPropertiesFilled(int interval) {
  Map<String, Object> customPropertyMap = new HashMap<String, Object>();
  customPropertyMap.put("interval", interval);
  return customPropertyMap;
}

logger.debug("Script task: checker_dueDate.groovy --- End");