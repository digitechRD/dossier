import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer

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

logger.debug("Script field initialization: N_CHRONO_initValue_Index_CourrierIn.groovy.groovy --- Start");


IField readField = usrContext.getCurrentDocument().getField(CourrierScriptUtils.getConstant("FIELD_CODE_G_SOCIETE"));
  
Integer readFieldValue = (Integer)readField.getValue();

if (readFieldValue != null ) {
    // Set a nex NumChrono
  logger.debug("Script field initialization: N_CHRONO_initValue_Index_CourrierIn.groovy.groovy --- #CLE readFieldValue:" + readFieldValue);
    
  String numChrono = CourrierScriptUtils.generateNumChronoActis(readFieldValue, usrContext, true);
  if (numChrono != null){
  
    logger.debug("Script field initialization: N_CHRONO_initValue_Index_CourrierIn.groovy.groovy --- #CLE Set value:" + numChrono);
  
    FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_N_CHRONO"), numChrono);

//    com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
//    documentMgr.updateDocument(usrContext, theDocument, false);
    Utils.getSearchResultController().replay();
  }
  
}
else
{
  logger.debug("Script field initialization: N_CHRONO_initValue_Index_CourrierIn.groovy.groovy --- #CLE no readFieldValue:" + readFieldValue);
}
    

logger.debug("Script field initialization: N_CHRONO_initValue_Index_CourrierIn.groovy.groovy --- End");
