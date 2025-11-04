import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.Organization;
import com.digitech.jcorbairs.Organization.OrganizationProperties;
import com.digitech.dossier.common.model.backend.airs.IField;

import static ScriptUtils;

//Input parameters
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("In facture_initPanelChangerCDPActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Get the user list
List<User> users = new ArrayList<User>(); 
log.debug("Script triggered on init custom Modal Panel : récupération de la liste des utilisateurs traitants");
users = ScriptUtils.getUsersWithProfil("PF_CDP") ;
log.debug("Script triggered on init custom Modal Panel : récupération de " + users.size() + " utilisateur(s) traitant(s)");

//List<OrganizationProperties> orgaList = new ArrayList<OrganizationProperties>(); 
//log.debug("Script triggered on init custom Modal Panel : récupération de la liste des orga traitantes");
//orgaList = ScriptUtils.getServerMgr().getOrganizationsProperties(UserUtils.getAdminUserContext().getJeton());
//log.debug("Script triggered on init custom Modal Panel : récupération de " + orgaList.size() + " orga(s) traitante(s)");

//Init pour la selection de l'orga
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.getModalPanelModel().put("userList", users);
customActionModel.getModalPanelModel().put("userItems", getAvailableUsers(users));
customActionModel.getModalPanelModel().put("selectedUser","Aucun");

//Init pour la saisie du commentaire
customActionModel.getModalPanelModel().put("comment","");

//Set size of custom panel
Integer witdhModalPanel = Integer.parseInt(ScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_WIDTH"));
Integer heightModalPanel = Integer.parseInt(ScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_HEIGHT"));
customActionModel.setModalPanelWidth(witdhModalPanel);
customActionModel.setModalPanelHeight(heightModalPanel);

// Set title of custom panel
int UserId = usrContext.getUser().getId()
String UserName = ScriptUtils.getListItemValueFromId(UserId, IField.REFERENCE_TYPE_USER);
customActionModel.setModalPanelTitle("Sélection d'un nouveau chef de projet pour remplacer :"+UserName);

// Set parameter to fix display bug (multiple modal panel one after the other)
customActionModel.getModalPanelModel().put("displayPanelCommentAndUser", true);

log.debug("Out facture_initPanelChangerCDPActionUnitView.groovy");

/* 
 * Construct and return availableUsers the selectItems users list  
 *@param users the users list
 *@return availableUsers the selectItems users list
 */
public List<SelectItem> getAvailableUsers(List<User> users) {
  List<SelectItem> availableUsers = new ArrayList<SelectItem>();
  if(users != null && users.size() > 0) {
    for(User user : users) {
      availableUsers.add(new SelectItem(user.getId(), user.getFirstName() + " " + user.getName()));
    }
  }
  return availableUsers;
}