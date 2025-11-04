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

CHAMP_A_RECOPIER = "OFINA_CLT_TYPE_ACQUISITION"
CHAMP_ECRASE = "OFINA_CLT_TYPE_ACQUISITION_2"

AMEX_code = "1675"
AMEX = "AMEX"

CUP_code = "1676"
CUP = "CUP"

PJ_code = "1677"
PJ = "Pieces jointes"

Adef_code = "1678"
Adef = "A definir"

scriptLogger.debug(">>> DependanceOfinaTypeAcquisition ");

IField field_CHAMP_A_RECOPIER = fieldMap.get(CHAMP_A_RECOPIER);  
String field_value_code_CHAMP_A_RECOPIER = getFieldValue( field_CHAMP_A_RECOPIER );
String field_value_CHAMP_A_RECOPIER="";

switch (field_value_code_CHAMP_A_RECOPIER) 
  {
    case AMEX_code :
    	field_value_CHAMP_A_RECOPIER = AMEX;
    	break;

    case CUP_code :
    	field_value_CHAMP_A_RECOPIER = CUP;
    	break;
            
    case PJ_code :
    	field_value_CHAMP_A_RECOPIER = PJ;
    	break;
      
    case Adef_code :
    	field_value_CHAMP_A_RECOPIER = Adef;
    	break;                 
                 
    default: 
    	break;
    }
    
IField field_CHAMP_ECRASE = fieldMap.get(CHAMP_ECRASE);
field_CHAMP_ECRASE.setValue( field_value_CHAMP_A_RECOPIER );

scriptLogger.debug("<<< DependandeOfinaTypeAcquisition ");


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
