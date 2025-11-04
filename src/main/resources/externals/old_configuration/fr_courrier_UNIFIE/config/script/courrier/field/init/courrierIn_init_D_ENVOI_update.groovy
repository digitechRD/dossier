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
import com.digitech.dossier.common.model.backing.validator.TwoDateValidator;
import com.digitech.dossier.common.Constants;
import com.digitech.dossier.common.Utils;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: courrierIn_init_D_ENVOI_update.groovy --- Start");

if (CourrierScriptUtils.hasVisaAccepted(usrContext, theDocument)) {
  if(!UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"))) {
    theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
  }
}
//Si l'utilisateur est un agent les champs doivent �tre gris�s
if(UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_AGT")))
{
	 theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
}

Map<String, String> paramsValidator = new HashMap<String, String>();
paramsValidator.put(TwoDateValidator.OTHER_COMPONENT_NAME, CourrierScriptUtils.getConstant("FIELD_CODE_D_RECEPTION"));

theOutput.getValue().setValidatorAttributs(paramsValidator);

CourrierScriptUtils.markFieldAsReadOnly(usrContext, logger, theDocument, theOutput);

boolean rendered = Utils.getViewUnitLinkedDocTableController().getModel().isRendered();
if(!rendered){
  theOutput.getValue().getProperties().put(FieldProperty.CALENDAR_DIRECTION, "auto");
}

logger.debug("Script field initialization: courrierIn_init_D_ENVOI_update.groovy --- End");

