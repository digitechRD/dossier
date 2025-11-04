/*******************************************************************
 * Before create/update via rest API trigger for MONTEE_SUR_CALE
 *******************************************************************/
// Java imports
import org.apache.commons.lang.StringUtils
import java.text.DateFormat
import java.text.SimpleDateFormat

// Digitech imports
import com.digitech.common.script.model.EnumScriptStatus
import com.digitech.dossier.common.service.Utils
import com.digitech.dossier.script.model.impl.result.ScriptModeRun
import com.digitech.dossier.webservices.IFieldType
import com.digitech.dossier.webservices.WSUtils
import com.digitech.dossier.admin.Utils
import com.digitech.dossier.common.model.backend.Constants.LockType
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.document.ViewUnitModel
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.common.service.ICounter;


/********************* PARAM ********************/
// _scriptLoggerger script_scriptLoggerger 			 : le _scriptLoggerger
// UserContext userContext 		 : l'...userContext
// IDocument document 			 : le document courant (pas disponible en création via API
// IWSDocumentType _docType      : les éléments transmis par le WS
// ScriptModeRun _mode           : ScriptModeRun.CREATE ou ScriptModeRun.UPDATE
/** **********************************************/

// Input parameters
//UserContext usrContext = userContext
//IDocument theDocument = document


// ********* DEBUT DU SCRIPT *********
_scriptLogger.debug("Script WS_create_update.groovy --- Start")

//ScriptResultValueChecker result = new ScriptResultValueChecker()
_result.status = EnumScriptStatus.OK
if(_mode == ScriptModeRun.CREATE) {
	createDocument()
}
else if(_mode == ScriptModeRun.UPDATE) {
	_result.fillKOResult("WS_create_update.groovy : Mode '$_mode' non géré !")
}
else {
	_result.fillKOResult("WS_create_update.groovy : Mode '$_mode' non géré !")
}

// ********* FIN DU SCRIPT *********

// ********* Fonctions appelées selon le cas à traiter Create / Update : postAction
private void createDocument() {
	_scriptLogger.debug("In WS_create_update.groovy-createDocument")
	
	try {
		
		String sContentType = _docType.getContentType();
		_scriptLogger.debug("In WS_create_update.groovy-createDocument - content type : "+sContentType)
		if (sContentType == "MONTEE_SUR_CALE"){
			
			_scriptLogger.debug("Script WS_create_update.groovy --- calcul du num chrono")

			// calculer le numéro chrono
			String sNumChrono = "xx";
			
			ICounter counterService = ServiceUtils.getCounterService();
			// le CODE du compteur est l'année en cours
			String dateFormated = new SimpleDateFormat("yyyy").format(new Date());
			String counterCode = "MSC_NUM_MONTEE_"+dateFormated;	

			_scriptLogger.debug("Script WS_create_update.groovy --- counterCode:"+counterCode)
			
			Integer nextValue = null;
			nextValue = counterService.getNextValue(counterCode);
			
			if (nextValue == null) {
			}
			else
				sNumChrono = dateFormated + "-" + nextValue.toString();
			_scriptLogger.debug("Script WS_create_update.groovy --- sNumChrono:"+sNumChrono)
			
			// ajout du numéro de montée sur cale MSC_NUM_MONTEE dans le dataSet avec le numéro chrono calculé
			if(WSUtils.addField(_docType, "MSC_NUM_MONTEE",sNumChrono)) {
				_scriptLogger.debug("WS_create_update.groovy-createDocument : Champ MSC_NUM_MONTEE ajouté")
			}
			else {
				_scriptLogger.warn("WS_create_update.groovy-createDocument : Unable to add field 'MSC_NUM_MONTEE' to field list")
			}
		}
	}
	catch(Exception ex) {
		_scriptLogger.error("WS_create_update.groovy-createDocument : Error while creating document: '{}'", ex.getLocalizedMessage(), ex)
		_result.fillKOResult("Creation failed !", ex)
	}
	finally {
	}
	_scriptLogger.debug("Out WS_create_update.groovy-createDocument")
}// createDocument


private void updateDocument() {
	_scriptLogger.debug("In TEST_WSDocumentInitializer.groovy-updateDocument")

	try {

	}
	catch(Exception ex) {
		_scriptLogger.error("TEST_WSDocumentInitializer.groovy-updateDocument : Error while updating document: '{}'", ex.getLocalizedMessage(), ex)
		_result.fillKOResult("Update failed !", ex)
	}
	finally {
	}

	_scriptLogger.debug("Out TEST_WSDocumentInitializer.groovy-updateDocument")
} // updateDocument

/**
 * (optional) method called once main task (creation) is done*/
@SuppressWarnings('unused')
private void postWSAction() {
  _scriptLogger.info(">>> WS_create_update#postWSAction($_mode)")
 
  _result.status = EnumScriptStatus.OK
  if (_mode == ScriptModeRun.CREATE || _mode == ScriptModeRun.UPDATE) {
    // nada
    _result.fillOKResult("Mode '$_mode' géré en POST action, mais aucun traitement !")
  } else {
    _result.fillKOResult("Mode '$_mode' non géré !")
  }
 
  _scriptLogger.info("<<< WS_create_update#postWSAction($_mode)")
}
_scriptLogger.debug("Script WS_create_update.groovy ---  End")