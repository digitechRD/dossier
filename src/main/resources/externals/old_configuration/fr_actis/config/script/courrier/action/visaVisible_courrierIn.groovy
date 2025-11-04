import com.digitech.dossier.common.utils.UserUtils;

import com.digitech.dossier.common.utils.FieldUtils;

import com.digitech.dossier.common.utils.UserUtils;

import com.digitech.dossier.common.utils.FieldUtils;

import java.util.ArrayList
import java.util.List

import com.digitech.dossier.common.model.backend.Constants;
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule

import static CourrierScriptUtils

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on visa Visibility : visaVisibility_courrierIn.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
List<String> states = new ArrayList<String>()
states.add(CourrierScriptUtils.getConstant("STATE_CODE_REPONDU"));

// Visible if the document is in state "REPONDU" and logged user is courrier signer or has direction profile
boolean visibleByDirection = UserUtils.hasProfile(usrContext, "DOS_DIRECTION") && UserUtils.isInOrganization(usrContext, FieldUtils.getValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")), true);
boolean visible = CourrierScriptUtils.hasState(usrContext, theDocument, states) &&  (CourrierScriptUtils.isSignerUser(usrContext, theDocument) || visibleByDirection);
boolean documentLockedByOther = theDocument.getLockType() == Constants.DOC_LOCKED_BYOTHER;

// Check the visa state
if (CourrierScriptUtils.hasVisaAccepted(usrContext, theDocument)) {
  visible = UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"));
}

result.setValid(visible && !documentLockedByOther);
 
outputParam.setValue(result);

log.debug("Script triggered on visa Visibility : visaVisibility_courrierIn.groovy --- End");