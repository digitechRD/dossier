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

log.debug("Script triggered on document_action_visible_gv : document_action_visible_gv.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

result.setValid(UserUtils.hasProfile(usrContext, "PF_ADMINISTRATION") || UserUtils.hasProfile(usrContext, "PF_MODIFICATION"));
 
log.debug("Script triggered on document_action_visible_gv : valideur : " + UserUtils.hasProfile(usrContext, "PF_ADMINISTRATION"));
log.debug("Script triggered on document_action_visible_gv : gestionnaire : " + UserUtils.hasProfile(usrContext, "PF_MODIFICATION"));

outputParam.setValue(result);

log.debug("Script triggered on document_action_visible_gv : document_action_visible_gv.groovy --- End");
