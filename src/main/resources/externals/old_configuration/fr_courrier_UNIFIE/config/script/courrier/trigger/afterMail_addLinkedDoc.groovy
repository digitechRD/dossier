import java.util.*

import javax.faces.component.UIComponent

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import javax.faces.model.SelectItem;
import com.digitech.dossier.common.model.backend.params.abstracts.AbstractField;
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// Map<String, IField> fieldsToSet les champs qui doivent prendre une valeur
/************************************************/

UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;
SelectItemFactory selectItemFactory = new SelectItemFactory();

log.debug("Script mail action: sendMail_addLinkedDoc.groovy --- Start");

Map<String, IField> fieldsMap = theDocument.getFieldMap();
fieldsMap.get("U_REPONSE").setValue(usrContext.getUserId());
fieldsMap.get("D_REPONSE").setValue(new Date());
IField type = fieldsMap.get("T_REPONSE");
List<SelectItem> items = selectItemFactory.getAuthorities(type.getConfigField(), usrContext.getInstance().getLocale());
for(SelectItem item : items){
  if(item.getLabel().equals("Courriel")){
    type.setValue(item.getValue());
    break;
  }
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);

log.debug("Script mail action: sendMail_addLinkedDoc.groovy --- End");