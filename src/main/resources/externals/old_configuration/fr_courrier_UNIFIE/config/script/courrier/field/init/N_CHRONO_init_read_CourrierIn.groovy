import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import org.apache.commons.lang.*;
import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.Constants;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;

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

logger.debug("Script field initialization: N_CHRONO_init_read_CourrierIn.groovy.groovy --- Start");

boolean isOwnerUser = CourrierScriptUtils.isOwnerUser(usrContext, theDocument);
if (isOwnerUser) {
  IField readField = usrContext.getCurrentDocument().getField(CourrierScriptUtils.getConstant("FIELD_CODE_E_LU"));
  Integer readFieldValue = (Integer)readField.getValue();
  if (readFieldValue == null || readFieldValue.equals(Integer.valueOf(0))) {
    // Flags the document as read
    FieldUtils.setValue(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_E_LU"), 1);
    com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
    Integer lockType = theDocument.getLockType();
    if (lockType == Constants.DOC_LOCKED_BYOWNER || lockType == Constants.DOC_NOTLOCKED) {
      documentMgr.updateDocument(usrContext, theDocument, false);
      if (lockType == Constants.DOC_LOCKED_BYOWNER) {
        // Do not use the Dossier lock (no management of lock type at this level)
        theDocument.getAirsDocument().lock();
      }
    }

    Utils.getSearchResultController().replay();
  }
}

logger.debug("Script field initialization: N_CHRONO_init_read_CourrierIn.groovy.groovy --- End");