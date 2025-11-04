import java.util.List;

import org.apache.commons.io.FilenameUtils

import com.digitech.courrier.common.controller.ResponseController
import com.digitech.courrier.common.model.ResponseModel
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.attachment.SelectionAttachmentModel
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.NavigationUtils;

import static CourrierScriptUtils

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on restore mail : restoreMail_courrierIn.groovy --- Start");

// Mise à jour de l'état du document
String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
// tester if a D_REPONSE have been set
if (haveResponseDate(theDocument)){
	FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU") ));
} else {
	FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE") ));
}
CourrierScriptUtils.saveDocument(usrContext, theDocument);

// On raffiche la page courante et on rafraichit la page des résultats de recherche
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, theDocument));

log.debug("Script triggered on response mail : restoreMail_courrierIn.groovy --- End");

public boolean haveResponseDate(IDocument doc){
	Object fieldValue = null ;
	try {
		fieldValue = FieldUtils.getValue(doc, CourrierScriptUtils.getConstant("FIELD_CODE_D_REPONSE"));
	} catch (Exception e){
		return false;
	}
	if (fieldValue != null)
		return true;
	return false;
}
