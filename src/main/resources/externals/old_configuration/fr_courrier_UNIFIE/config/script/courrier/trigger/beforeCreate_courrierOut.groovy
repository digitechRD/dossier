import org.slf4j.Logger

import com.digitech.common.lib.utils.StringUtils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.jcorbairs.User;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/** **********************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.info("Script triggered on before save: beforeCreate_courrierOut.groovy --- Start");

// manage O_PROPRIETAIRE field
String UPROPCurrentValue = (String) theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_PROPRIETAIRE")).getValue();
if (StringUtils.isNotBlank(UPROPCurrentValue)) {
    String OPROPCurrentValue = (String) theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
    //define an orga if orga is null or user isn't in orga
    if (StringUtils.isBlank(OPROPCurrentValue) || !UserUtils.isInOrganization(Integer.parseInt(UPROPCurrentValue), Integer.parseInt(OPROPCurrentValue))) {
        // keep current orga if U_PROPRIETAIRE is current user
        if (UPROPCurrentValue.equals(usrContext.getUserId().toString())) {
            FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"), usrContext.getInstance().getCurrentOrgId());
            log.debug(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE") + " \tuserId : " + usrContext.getUserId() + " \tU_PROP_Id : " + UPROPCurrentValue + " \tO_PROP_Id : " + usrContext.getInstance().getCurrentOrgId());
        } else {
            // set the first orga for another U_PROPRIETAIRE
            User user = CourrierScriptUtils.getUserMgr().getUser(Integer.parseInt(UPROPCurrentValue));
            if (!user.getOrganizations() == null && !user.getOrganizations().isEmpty()) {
                FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE"), user.getOrganizations().get(0));
                log.debug(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE") + " \tuserId : " + usrContext.getUserId() + " \tU_PROP_Id : " + UPROPCurrentValue + " \tO_PROP_Id : " + user.getOrganizations().get(0));
            }
        }
    }
}

// Generates the final chrono number
Integer orgaId = (Integer) theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();
orgaId = (orgaId != null ? orgaId : usrContext.getCurrentOrgId());
String numChrono = CourrierScriptUtils.generateNumChrono(orgaId, usrContext, false);
if (numChrono != null) {
    CourrierScriptUtils.alertUserIfNumChronoChanged(numChrono, theDocument);
    log.debug("docId=[" + theDocument.getAirsRefId() + "] generate_FINAL_NumChrono=[" + numChrono + "]");
    FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"), numChrono);
    // Reset Boolean, new mail will be send
    CourrierScriptUtils.markDocumentToNotifyUser(theDocument);
    log.debug("Document [{}] has been mark to notified owner by mail.", theDocument.getAirsRefId());
} else {
    log.error("docId=[" + theDocument.getAirsRefId() + "] chrono generation failed !");
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.info("Script triggered on before save: beforeCreate_courrierOut.groovy --- End");

private static IUser getUserMgr() {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
}

