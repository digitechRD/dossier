import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.document.ViewUnitModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.params.UpdateLinkedDoc;
import com.digitech.dossier.common.model.backend.params.UpdateContentType;

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

logger.debug("Script field initialization: courrierIn_init_readOnly_2.groovy --- Start");

boolean markedAsReadOnly = CourrierScriptUtils.markFieldAsReadOnly(usrContext, logger, theDocument, theOutput);

if(markedAsReadOnly){
  //<column> elements in <inputLinkedDoc> don't have a "field-properties-scriptId" element but must be changed to readOnly. 
  ViewUnitModel viewUnitModel = Utils.getViewUnitController().getModel();
  UpdateContentType updateContentType = viewUnitModel.getUpdateContentType(viewUnitModel.getUpdateOrga(), theDocument);
  for(UpdateLinkedDoc updateLinkedDoc : updateContentType.getUpdateCtyLinkedDoc()) {
    updateLinkedDoc.setReadOnly(true);
  }
  DocumentUtils.unlockDocument(theDocument);
}

logger.debug("Script field initialization: courrierIn_init_readOnly_2.groovy --- End");

