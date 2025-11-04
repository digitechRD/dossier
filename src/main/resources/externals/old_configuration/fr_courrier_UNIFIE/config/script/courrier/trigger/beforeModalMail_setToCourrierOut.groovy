import java.util.*;

import org.apache.commons.lang.*;
import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// Map<String, IField> fieldsToSet les champs qui doivent prendre une valeur
/************************************************/

UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;

log.debug("Script mail action: sendMail_addLinkedDoc.groovy --- Start");

CourrierScriptUtils.setInputToMailModal("C_DESTINATAIRE", log);

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script mail action: sendMail_addLinkedDoc.groovy --- End");