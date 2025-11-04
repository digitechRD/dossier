import com.digitech.dossier.administration.model.backend.Organization;

import java.util.List

import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.Organization;
import com.digitech.jcorbairs.User;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// List<User> elements
// IDocument document
/************************************************/

List<User> usersList = elements;
IDocument doc = document;
UserCoreContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierOut.groovy --- Start");

Integer ownerOrgId = usrContext.getCurrentOrgId();

Domain contentType =  doc.getDomain();

IServer serverMgr = (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);

Iterator<User> usersIter = usersList.iterator()
while (usersIter.hasNext()) {
  User user = usersIter.next();
  if (!UserUtils.hasContentTypeRight(user, contentType)) {
    usersIter.remove();
  }
}

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierOut.groovy --- End");
