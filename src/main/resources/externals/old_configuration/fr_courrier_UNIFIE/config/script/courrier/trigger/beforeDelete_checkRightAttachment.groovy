import java.sql.ResultSet;
import java.util.*;

import org.apache.commons.lang.*;
import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.IScriptResultValueModel.Severity;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.dossier.common.model.backing.attachment.AttachmentTableRowModel;
import com.digitech.dossier.common.model.backend.airs.IAttachment;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// Map<String, IField> fieldsToSet les champs qui doivent prendre une valeur
/************************************************/

UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;
AttachmentTableRowModel rowModel = rowModel;

log.debug("Script mail action: beforeDelete_checkRightAttachment.groovy --- Start");
log.debug("current rowModel : [" + rowModel.getRowKey() + "]\t" + rowModel.getLabel());
boolean canDelete = true;

Boolean isFirstRow;
List<AttachmentTableRowModel> rows = Utils.getAttachmentTableController().getModel().getWrappedData();

if(rows != null && !rows.isEmpty()){
  String type = rowModel.getCurrentAttachmentType();
  if("COU_RECU".equals(type) && rows.get(0).equals(rowModel)){
    log.debug("the first row can't be deleted");
    canDelete = false;
  }
}

rowModel.setCanModifyGroovy(canDelete);

ScriptResultValueChecker result = new ScriptResultValueChecker();

result.setValid(canDelete);
output.setValue(result);

log.debug("Script mail action: beforeDelete_checkRightAttachment.groovy --- End");