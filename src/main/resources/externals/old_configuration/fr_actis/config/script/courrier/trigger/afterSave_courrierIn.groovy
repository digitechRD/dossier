import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.jcorbairs.User

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.debug("Script triggered on after save: afterSave_courrierIn.groovy --- Start");

if (Boolean.TRUE.equals(Boolean.valueOf(CourrierScriptUtils.getConstant("MAIL_NOTIFICATION_ENABLED")))) {
  IUser userMgr = (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  File templateFile = new File(CourrierUtils.getCourrierTemplateFolderPath(), "changementEtat.htm");
  
  String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
  Integer etatCourrant = (Integer)theDocument.getField(etatCourrierFieldCode).getValue();
  if (CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_A_VALIDER")).equals(etatCourrant)) {
    // Notifier le valideur
    Map<String, Object> customPropertyMap = new HashMap<String, Object>();
    customPropertyMap.put("state", CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_VALIDATE);
    User user = userMgr.getUser((Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR")).getValue());
    ApplicationUtils.sendMail(usrContext, Arrays.asList(theDocument), templateFile, new ReportPerson(usrContext, user), BundleUtils.getTranslation("mail_subject_courrier_state_to_validate"), customPropertyMap);
  }
  else if (CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE")).equals(etatCourrant)) {
    // Notifier le propriétaire
    Map<String, Object> customPropertyMap = new HashMap<String, Object>();
    customPropertyMap.put("state", CourrierAdvancedAuditType.ADV_EVENT_COURRIER_DIFFUSED);
    User user = userMgr.getUser((Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue());
    ApplicationUtils.sendMail(usrContext, Arrays.asList(theDocument), templateFile, new ReportPerson(usrContext, user), BundleUtils.getTranslation("mail_subject_courrier_state_diffused"), customPropertyMap);
  }
  else if (CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")).equals(etatCourrant)) { 
    // Notifier le viseur
    Map<String, Object> customPropertyMap = new HashMap<String, Object>();
    customPropertyMap.put("state", CourrierAdvancedAuditType.ADV_EVENT_COURRIER_TO_SIGN);
    User user = userMgr.getUser((Integer)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEUR")).getValue());
    ApplicationUtils.sendMail(usrContext, Arrays.asList(theDocument), templateFile, new ReportPerson(usrContext, user), BundleUtils.getTranslation("mail_subject_courrier_state_to_sign"), customPropertyMap);
  }
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script triggered on after save: afterSave_courrierIn.groovy --- End");






