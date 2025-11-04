import java.util.*;

import org.apache.commons.lang.*;
import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.dossier.common.model.backing.document.ViewUnitLinkedDocTableRowModel;
import com.digitech.dossier.common.model.backing.document.ViewUnitLinkedDocTableRowModel.Mode;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backend.params.UpdateField;
import com.digitech.dossier.common.model.backend.params.abstracts.AbstractField;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// Map<String, IField> fieldsToSet les champs qui doivent prendre une valeur
/************************************************/

UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;
ViewUnitLinkedDocTableRowModel theRowModel = rowModel;

log.debug("Script mail action: sendMail_addLinkedDoc.groovy --- Start");

boolean mutable= false;

IField field = theDocument.getField("U_REPONSE");
if(field!=null){
  if(usrContext.getUserId().equals(field.getValue())) {
    mutable = true;
  }
}

if(Mode.EDIT.equals(theRowModel.getMode()) || Mode.READ.equals(theRowModel.getMode())){
  IField[] fields = new IField[2];
  fields[0] = theDocument.getField("D_REPONSE");
  fields[1] = theDocument.getField("U_REPONSE");
  
  for(IField field_ : fields) {
    AbstractField paramField = field_.getConfigField();
    if(paramField == null) {
      continue;
    }
    if(!(paramField instanceof UpdateField)) {
      continue;
    }
    UpdateField updateField = (UpdateField) paramField;
    updateField.setReadOnly(true);
  }
}

theRowModel.setMutableGroovy(mutable);

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script mail action: sendMail_addLinkedDoc.groovy --- End");