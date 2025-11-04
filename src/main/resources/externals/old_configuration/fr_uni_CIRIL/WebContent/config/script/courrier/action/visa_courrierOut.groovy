import javax.faces.context.FacesContext;

import com.digitech.dossier.common.SessionManager;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.faces.controller.viewer.DocumentViewerController;

import static CourrierScriptUtils;

// param
org.slf4j.Logger logger = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

logger.debug("Script triggered on visa : visaVisibility_courrierOut.groovy --- Start");

CourrierScriptUtils.doVisa(usrContext, theDocument);

// Remove double viewers from session
//SessionManager.getRequestSession(FacesContext.getCurrentInstance()).removeAttribute(DocumentViewerController.getSessionKey("documentViewerIn"));
//SessionManager.getRequestSession(FacesContext.getCurrentInstance()).removeAttribute(DocumentViewerController.getSessionKey("documentViewerOut"));

logger.debug("Script triggered on visa : visaVisibility_courrierOut.groovy --- End");