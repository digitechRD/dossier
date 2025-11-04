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
import org.slf4j.Logger

import com.digitech.dossier.common.service.ICounter;
import java.text.DateFormat
import java.text.SimpleDateFormat



/*******************************************************************
 * Before save trigger for MONTEE_SUR_CALE
 *******************************************************************/
/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/** **********************************************/

// Input parameters
UserContext usrContext = userContext
IDocument theDocument = document
Logger log = scriptLogger

log.debug("Script MSC_beforeSave.groovy --- Start")

ScriptResultValueChecker result = new ScriptResultValueChecker()


String sMSC_NUM_MONTEE = theDocument.getField("MSC_NUM_MONTEE").getValue()


if((sMSC_NUM_MONTEE == null)||(sMSC_NUM_MONTEE.length()==0)){

	log.debug("Script MSC_beforeSave.groovy --- sMSC_NUM_MONTEE vide ou null à positionné")

	// calculer le numéro chrono
	String sNumChrono = "xx";
	
    ICounter counterService = ServiceUtils.getCounterService();
	// le CODE du compteur est l'année en cours
	String dateFormated = new SimpleDateFormat("yyyy").format(new Date());
    String counterCode = "MSC_NUM_MONTEE_"+dateFormated;	

	log.debug("Script MSC_beforeSave.groovy --- counterCode:"+counterCode)
	
	Integer nextValue = null;
	nextValue = counterService.getNextValue(counterCode);
	
    if (nextValue == null) {
    }
	else
		sNumChrono = dateFormated + "-" + nextValue.toString();
	log.debug("Script MSC_beforeSave.groovy --- sNumChrono:"+sNumChrono)
	
    FieldUtils.setValue(theDocument, "MSC_NUM_MONTEE", sNumChrono)
	
}
else {
	log.debug("Script MSC_beforeSave.groovy --- Num CHRONO déjà en place  sMSC_NUM_MONTEE"+sMSC_NUM_MONTEE)
	
}

result.setValid(true)
output.setValue(result)

log.debug("Script MSC_beforeSave.groovy ---  End")