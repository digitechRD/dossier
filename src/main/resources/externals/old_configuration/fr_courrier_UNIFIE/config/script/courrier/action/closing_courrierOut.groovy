import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import static CourrierScriptUtils;

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;


log.debug("Script triggered on closing : closing_courrierOut.groovy --- Start");

CourrierScriptUtils.doClosing(usrContext, theDocument)

log.debug("Script triggered on closing : closing_courrierOut.groovy --- End");