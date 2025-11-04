import com.digitech.dossier.common.utils.FieldUtils;

import com.digitech.dossier.common.utils.UserUtils;

import com.digitech.dossier.common.utils.FieldUtils;

import com.digitech.dossier.common.utils.FieldUtils;

import com.digitech.dossier.common.utils.UserUtils;

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


// Input parameters
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on response visibility : DownloadPJVisible.groovy --- Start");

ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();

result.setValid(true);

outputParam.setValue(result);

log.debug("Script triggered on init custom Modal Panel: DownloadPJVisible.groovy --- End");
