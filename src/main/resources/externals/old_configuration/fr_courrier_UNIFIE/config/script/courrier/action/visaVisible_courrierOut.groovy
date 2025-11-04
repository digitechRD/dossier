import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule;

import static CourrierScriptUtils;

// Input parameters
org.slf4j.Logger log = scriptLogger;
UserContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on visa Visibility : visaVisible_courrierOut.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;

outputParam.setValue(CourrierScriptUtils.isVisaVisible(usrContext, theDocument, false));

log.debug("Script triggered on visa Visibility : visaVisible_courrierOut.groovy --- End");