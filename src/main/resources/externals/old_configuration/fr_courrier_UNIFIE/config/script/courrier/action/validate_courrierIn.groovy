import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.utils.NavigationUtils;

import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on validate : validate_courrierIn.groovy --- Start");

CourrierScriptUtils.validateCourrier(theDocument, usrContext, log);

// Reset Boolean, new mail will be send
CourrierScriptUtils.markDocumentToNotifyUser(document);
log.debug("Document [{}] has been mark to notified owner by mail.", theDocument.getAirsRefId());

// On affiche la page des résultats de recherche
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));

log.debug("Script triggered on validate : validate_courrierIn.groovy --- End");