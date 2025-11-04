import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils

import com.digitech.courrier.common.controller.ResponseController
import com.digitech.courrier.common.model.ResponseModel
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.SessionManager;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.attachment.SelectionAttachmentModel
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.faces.controller.viewer.DocumentViewerController;


// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on response : DownloadPJ_action.groovy --- Start");




Utils.getCustomActionController().getModel().clear();
Utils.getCustomActionController().getModel().setModalPanelPageKey(null);


// On raffiche la page courante et on rafraichit la page des résultats de recherche
//Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, theDocument));

log.debug("Script triggered on response : DownloadPJ_action.groovy --- End");