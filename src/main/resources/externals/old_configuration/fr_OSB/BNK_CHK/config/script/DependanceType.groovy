import com.digitech.dossier.common.model.backend.UserContext;

import java.util.List;

import com.digitech.jcorbairs.Organization;
import org.apache.commons.beanutils.BeanComparator;
import java.util.Collections;

import java.util.*

import javax.faces.*
import javax.faces.component.*
import javax.faces.component.html.*
import javax.faces.model.*

import org.apache.commons.lang.*

import com.digitech.airs3dossiers.airs.AirsDocument;
import com.digitech.dossier.common.*
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backing.factory.*
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.jcorbairs.*

import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.airs3dossiers.airs.AirsFolder;

CHAMP_A_RECOPIER = "BNK_REM"
CHAMP_ECRASE = "BNK_CHQ"

scriptLogger.debug(">>> DependanceOfinaTypeProduit ");

IField field_CHAMP_A_RECOPIER = fieldMap.get(CHAMP_A_RECOPIER);  
String field_value_CHAMP_A_RECOPIER = getFieldValue( field_CHAMP_A_RECOPIER );
    
IField field_CHAMP_ECRASE = fieldMap.get(CHAMP_ECRASE);
field_CHAMP_ECRASE.setValue( field_value_CHAMP_A_RECOPIER );

scriptLogger.debug("<<< DependanceOfinaTypeProduit ");


String getFieldValue( IField field )
{
	List<?> values = field.getValues();
	String fieldvalue;
	if(values != null && !values.isEmpty()) {
		fieldvalue = getServerMgr().getFieldValues(values, field.getAirsField());   
		scriptLogger.info("Field Value : {} - {}", field.getCode(), fieldvalue);
	}
	else {
		scriptLogger.info("the field value is null or empty");
	}
	
	return fieldvalue;
}

private IUser getUserMgr() {
  return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}

private IServer getServerMgr() {
  return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}
