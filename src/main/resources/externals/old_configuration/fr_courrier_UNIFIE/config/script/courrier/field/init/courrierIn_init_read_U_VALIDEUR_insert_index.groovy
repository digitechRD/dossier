import java.util.*;

import org.apache.commons.lang.*;
import org.slf4j.Logger;

import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.common.model.backend.params.UpdateField;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
// UpdateField theUpdatedField le param field du champ
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;
UpdateField theUpdatedField = updateField;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: courrierIn_init_read_U_VALIDEUR_insert_index.groovy --- Start");

CourrierScriptUtils.changeRequired_U_VALIDEURS(usrContext, logger, theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_T_TYPE")), theOutput);

CourrierScriptUtils.conditionneInitialValue_U_VALIDEURS(theUpdatedField, theDocument);

CourrierScriptUtils.changeReadOnly_U_VALIDEURS(usrContext, theDocument, theOutput);

logger.debug("Script field initialization: courrierIn_init_read_U_VALIDEUR_insert_index.groovy --- End");