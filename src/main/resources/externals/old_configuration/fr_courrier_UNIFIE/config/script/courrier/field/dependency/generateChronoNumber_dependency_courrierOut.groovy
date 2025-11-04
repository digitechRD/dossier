import java.util.*

import javax.faces.component.UIComponent

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backing.AbstractFormLocutionModel
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.jcorbairs.Organization;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IField updatedField le champ de référence pour la dépendance
// IField fieldToUpdate le champ à mettre a jour
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IField theFieldToUpdate = fieldToUpdate;
IField updatedField = updatedField;

logger.debug("Script field dependency: generateChronoNumber_dependency_courrierOut.groovy --- Start");

IDocument document = usrContext.getCurrentDocument();
Integer serviceId = null;
if (document != null){
  
  String currentPropId = document.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue();
  IUser userMgr = (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  
  if(currentPropId != null && !currentPropId.equals(usrContext.getUserId().toString())) {
    Organization orga = userMgr.getUser(Integer.parseInt(currentPropId)).getOrganizations().get(0);
    serviceId = orga.getProperties().getId();
  }
  else{
    serviceId = usrContext.getCurrentOrgId();
  }
}
serviceId == null ? usrContext.getCurrentOrgId() : serviceId; 

String numChrono = CourrierScriptUtils.generateNumChrono(serviceId, usrContext, true);
if (numChrono != null){
  theFieldToUpdate.setValue(numChrono);
  logger.debug("generate_TEMP_NumChrono=[" + numChrono + "]");
  ((UIComponent)theFieldToUpdate.getComponent()).getAttributes().put(AbstractFormLocutionModel.KEY_ENABLE_EFFECT, Boolean.TRUE);
}


logger.debug("Script field dependency: generateChronoNumber_dependency_courrierOut.groovy --- End");