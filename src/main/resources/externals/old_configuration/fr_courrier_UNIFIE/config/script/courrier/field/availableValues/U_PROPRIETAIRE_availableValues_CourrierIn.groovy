import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;

import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.params.Flow;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.admin.UsersManager;

import static CourrierScriptUtils

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

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierIn.groovy --- Start");

Integer ownerOrgId = (Integer) doc.getField(CourrierScriptUtils.getConstant("FIELD_CODE_O_PROPRIETAIRE")).getValue();

Iterator<User> usersIter = usersList.iterator()
while (usersIter.hasNext()) {
  User user = usersIter.next();
  Domain contentType =  doc.getDomain(); 
  // if (!UserUtils.isInOrganization(user, ownerOrgId)) {
  if (!UserUtils.isInOrganization(user, ownerOrgId) || !UserUtils.hasContentTypeRight(user, contentType)) {
    usersIter.remove();
  }
}

logger.debug("Script field available values: U_PROPRIETAIRE_availableValues_CourrierIn.groovy --- End");