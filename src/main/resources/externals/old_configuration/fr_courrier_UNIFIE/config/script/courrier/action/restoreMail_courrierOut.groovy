import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import static CourrierScriptUtils;

org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on restore mail : restoreMail_courrierOut.groovy --- Start");

CourrierScriptUtils.doRestore(usrContext, theDocument);

log.debug("Script triggered on response mail : restoreMail_courrierOut.groovy --- End");
