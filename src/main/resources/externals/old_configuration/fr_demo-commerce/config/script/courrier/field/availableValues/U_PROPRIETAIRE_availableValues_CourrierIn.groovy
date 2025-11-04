import java.util.List

import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.UserUtils
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

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierIn.groovy --- Start");

Integer ownerOrgId = (Integer) doc.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();

Iterator<User> usersIter = usersList.iterator()
while (usersIter.hasNext()) {
  User user = usersIter.next();
  if (!UserUtils.isInOrganization(user, ownerOrgId)) {
    usersIter.remove();
  }
}

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierIn.groovy --- End");
