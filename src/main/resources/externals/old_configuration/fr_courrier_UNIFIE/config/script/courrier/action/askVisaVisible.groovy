import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.Constants
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule
import org.slf4j.Logger
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IField;;

import static CourrierScriptUtils;

UserContext usrContext = userContext;
IDocument theDocument = document;
final String METHOD = "askVisaVisible";

getLog().debug("Script triggered on asking visa visibility : " + METHOD + " --- Start");




ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
outputParam.setValue(result);

// Get all infos we need
String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
Integer etatCourant = (Integer) theDocument.getField(etatCourrierFieldCode).getValue();
Integer etatVisa = (Integer) theDocument.getField(etatVisaFieldCode).getValue();

// To check if the document has an attachment (type courrier_out)
documentHasPJEditable = false;

List<IAttachment> pJList = document.getAttachments(userContext);
for (IAttachment pj : pJList)
{
   if(pj.getType().equals(CourrierScriptUtils.getConstant("ATTACHMENT_TYPE_COURRIER_OUT")))
   {
	   documentHasPJEditable = true;
   }
}

IField signersField = theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEURS"));

boolean isValid = Constants.DOC_LOCKED_BYOTHER != theDocument.getLockType();
getLog().debug("DOC_LOCKED_BYOTHER = "+isValid);
isValid = isValid && etatCourant.equals(CourrierScriptUtils.getTermID(theDocument, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")));
getLog().debug("STATE_CODE_REPONDU = "+isValid);
isValid = isValid && etatVisa == null || etatVisa.equals(CourrierScriptUtils.getTermID(theDocument, etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_REFUSE")));
getLog().debug("etatVisa == null ou STATE_CODE_VISA_REFUSE "+isValid);
isValid = isValid && signersField.getValues() != null;
getLog().debug("signersField.getValues() != null = "+isValid);
isValid = isValid && documentHasPJEditable;
getLog().debug("documentHasPJEditable = "+isValid);

result.setValid(isValid);

getLog().debug("isValid final = "+isValid);

getLog().debug("Script triggered on asking visa visibility : " + METHOD + " --- Stop");


private Logger getLog(){
    return (Logger) scriptLogger;
}
