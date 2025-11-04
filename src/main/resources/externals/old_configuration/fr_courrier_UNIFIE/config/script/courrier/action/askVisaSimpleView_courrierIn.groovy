import java.util.ArrayList;

import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backing.search.SearchResultTableRowModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.MessagesModel;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IField;;
import com.digitech.dossier.common.model.backend.Constants

import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
ScriptResultModel theOutput = output;
boolean documentListIsClear = true;

log.debug("Script triggered on asking for visa : askVisaSimpleView_courrierIn.groovy --- Start");
try
{
	
	Collection<SearchResultTableRowModel> searchResultRows = Utils.getSearchResultTableController().getModel().getSelectedRows();

	int documentsValidated = 0;
	for (SearchResultTableRowModel row : searchResultRows) {
		IDocument document = row.getDocument();
		
		String etatCourrierFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
		String etatVisaFieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");

		Integer etatCourant = (Integer) document.getField(etatCourrierFieldCode).getValue();
		Integer etatVisa = (Integer) document.getField(etatVisaFieldCode).getValue();
		
		// Verification que le document ne possède pas de PJ editable
		documentHasPJEditable = false;
		List<IAttachment> pJList = document.getAttachments(userContext);
		for (IAttachment pj : pJList)
		{
		   if(pj.getType().equals(CourrierScriptUtils.getConstant("ATTACHMENT_TYPE_COURRIER_OUT")))
		   {
			   documentHasPJEditable = true;
		   }
		}
		
		IField signersField = document.getField(CourrierScriptUtils.getConstant("FIELD_CODE_U_VISEURS"));
		
		boolean isValid = Constants.DOC_LOCKED_BYOTHER != document.getLockType();
		isValid = isValid && etatCourant.equals(CourrierScriptUtils.getTermID(document, etatCourrierFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REPONDU")));
		isValid = isValid && etatVisa == null || etatVisa.equals(CourrierScriptUtils.getTermID(document, etatVisaFieldCode, CourrierScriptUtils.getConstant("STATE_CODE_VISA_REFUSE")));
		isValid = isValid && signersField.getValues() != null;
		isValid = isValid && documentHasPJEditable;
		
		if (isValid) {
			CourrierScriptUtils.prepareVisa(document);
			CourrierScriptUtils.markDocumentToNotifyUser(document);
			log.debug("Document [{}] has been mark to notified owner by mail.", document.getAirsRefId());
			documentsValidated++;
			DocumentUtils.saveDocument(document);	
			}
			else
			{
				documentListIsClear = false;
			}
	}

    MessagesModel.getInstance().clearPersistantMessages();
    ScriptResultValueDocumentInitializer scriptResult = new ScriptResultValueDocumentInitializer();
    theOutput.setValue(scriptResult);

    scriptResult.setMessageSummary("Envoi en visa");
	
	if(documentListIsClear)
	{
		scriptResult.setMessageDetail("Les documents ont bien été envoyés en visa");
	}
	else
	{
		scriptResult.setMessageDetail("Les documents éligibles à l'envoi en visa ont bien été envoyés");
	}
    
    scriptResult.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);

	com.digitech.dossier.common.Utils.getSearchResultTableController().refresh();
	
}
catch(Exception e)
{
	log.error(e.getLocalizedMessage());
}

log.debug("Script triggered on asking for visa : askVisaSimpleView_courrierIn.groovy --- End");