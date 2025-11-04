import com.digitech.dossier.administration.model.backend.Organization;

import java.util.List

import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.jcorbairs.Organization;
import com.digitech.jcorbairs.User

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// List<User> users
// IDocument document
/************************************************/

List<User> usersList = users;
IDocument doc = document;
UserCoreContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierOut.groovy --- Start");

Integer ownerOrgId = usrContext.getCurrentOrgId();

IServer serverMgr = (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
List<Organization> organizations = serverMgr.getOrganizationDescendents(DossierCoreContext.getAdminJeton(), ownerOrgId);

List<Integer> organizationIds = new ArrayList<Integer>();
for (Organization organization : organizations) {
  organizationIds.add(organization.getProperties().getId());
}

Iterator<User> usersIter = usersList.iterator()
while (usersIter.hasNext()) {
  User user = usersIter.next();
  if (!UserUtils.isInOrganization(user, organizationIds)) {
    usersIter.remove();
  }
}

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierOut.groovy --- End");
