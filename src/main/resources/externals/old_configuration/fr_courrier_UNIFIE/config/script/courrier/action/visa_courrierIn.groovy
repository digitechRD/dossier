import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import static CourrierScriptUtils;

// param
org.slf4j.Logger logger = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

logger.debug("Script triggered on visa : visaVisibility_courrierIn.groovy --- Start");

CourrierScriptUtils.doVisa(usrContext, theDocument);


// Remove double viewers from session
//SessionManager.getRequestSession(FacesContext.getCurrentInstance()).removeAttribute(DocumentViewerController.getSessionKey("documentViewerIn"));
//SessionManager.getRequestSession(FacesContext.getCurrentInstance()).removeAttribute(DocumentViewerController.getSessionKey("documentViewerOut"));

logger.debug("Script triggered on visa : visaVisibility_courrierIn.groovy --- End");