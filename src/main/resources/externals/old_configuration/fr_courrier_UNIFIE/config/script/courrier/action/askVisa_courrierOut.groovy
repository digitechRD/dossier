import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.NavigationUtils;

import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

ScriptResultValueDocumentInitializer result = output.getValue();

log.debug("Script triggered on asking visa : askVisa_courrierOut.groovy --- Start");

try
{
	log.debug("Préparation du visa");
	CourrierScriptUtils.prepareVisa(theDocument);
	log.debug("Ajout d'un historique");
	CourrierScriptUtils.addHistoForWorkflow(theDocument, usrContext, "Envoi en visa");
	log.debug("Sauvegarde du document");
	CourrierScriptUtils.markDocumentToNotifyUser(theDocument);
	DocumentUtils.saveDocument(theDocument);
	
	// Display search results
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));
		
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
	result.setMessageSummary("Envoi en visa");
	result.setMessageDetail("Le document a été envoyé en visa");
}
catch(Exception e)
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Envoi en visa");
	result.setMessageDetail("Une erreur s'est produite lors de l'envoi du document en visa");
	log.error(e.getLocalizedMessage());
}

log.debug("Script triggered on asking visa : askVisa_courrierOut.groovy --- End");