import java.util.ArrayList
import java.util.List

import com.digitech.dossier.common.model.backend.Constants;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule

import static CourrierScriptUtils

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on response visibility : writeCourrierVisible_courrierOut.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

Date dateEnvoi = (Date)theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_D_ENVOI")).getValue();

// Check visa state
String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
Integer visaState = theDocument.getField(etatVisaFieldCode).getValue();

// Visible if logged user is courrier owner or has direction profile
boolean visibleByDirection = UserUtils.hasProfile(usrContext, "DOS_DIRECTION") && UserUtils.isInSameOrganization(usrContext, FieldUtils.getValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")), true);
boolean visible = CourrierScriptUtils.isOwnerUser(usrContext, theDocument) || visibleByDirection;
boolean responseTemplateExisting = CourrierScriptUtils.isMailTemplateDefined(usrContext, theDocument);
boolean documentLockedByOther = theDocument.getLockType() == Constants.DOC_LOCKED_BYOTHER;
 
boolean visaProcessOn = CourrierScriptUtils.getTermID(etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_INDEFINI")).equals(visaState);

// Check the visa state
if (CourrierScriptUtils.hasVisaAccepted(usrContext, theDocument)) {
  visible = UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN"));
}

log.debug("visaProcessOn = "+visaProcessOn);
log.debug("visible = "+visible);
log.debug("responseTemplateExisting = "+responseTemplateExisting);
log.debug("documentLockedByOther = "+documentLockedByOther);

result.setValid(dateEnvoi == null && visible && responseTemplateExisting && !documentLockedByOther && !visaProcessOn);

outputParam.setValue(result);

log.debug("Script triggered on init custom Modal Panel: writeCourrierVisible_courrierOut.groovy --- End");
