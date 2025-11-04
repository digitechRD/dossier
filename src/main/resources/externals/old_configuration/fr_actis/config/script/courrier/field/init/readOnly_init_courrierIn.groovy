import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.CourrierConstants
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.dossier.common.utils.UserUtils

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: readOnly_init_courrierIn.groovy --- Start");

//if (!UserUtils.hasProfile(usrContext, "DOS_DIRECTION")){
//	List<String> states = new ArrayList<String>()
//  states.add(CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE"));
//  
//	Object value = theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VALIDEUR")).getValue();
//  if (value != null && CourrierScriptUtils.hasState(usrContext, theDocument, states)){
//    theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
//  }
//}

if (CourrierScriptUtils.hasVisaAccepted(usrContext, theDocument)) {
  if(!UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
    theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
  }
}

logger.debug("Script field initialization: readOnly_init_courrierIn.groovy --- End");

