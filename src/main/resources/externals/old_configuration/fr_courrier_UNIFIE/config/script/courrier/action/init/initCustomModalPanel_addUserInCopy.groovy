package courrier.action.init

import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.picker.SelectUserController
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.AbstractFormLocutionModel
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backing.document.ViewUnitContentPanelModel
import com.digitech.dossier.common.model.backing.search.LocutionModel
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.jcorbairs.User
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger

import javax.faces.component.UISelectItems
import javax.faces.component.html.HtmlPanelGroup
import javax.faces.model.SelectItem


final String GROOVY_NAME = "initCustomModalPanel_addUserInCopy";
final String U_COPIES_FIELDNAME = "U_COPIES";

getLog().info("Script triggered on init custom Modal Panel:" + GROOVY_NAME + "--- Start ");

try {
    CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
    String widthModalPanel = 600;
    String heightModalPanel = 850;
    customActionModel.setModalPanelWidth(Integer.parseInt(widthModalPanel));
    customActionModel.setModalPanelHeight(Integer.parseInt(heightModalPanel));
    customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelAddUserInCopy_title"));

    //get the locution model
    LocutionModel locution = Utils.getViewUnitController().getContentPanelController().getModel().getLocutionModelMap().get(U_COPIES_FIELDNAME);
    if (locution == null) {
        throw new NullPointerException("locution model for field " + U_COPIES_FIELDNAME + "is null.");
    }

    //init the SelectUserAdvancedController
    Utils.getSelectUserAdvancedController().init(locution, null);

}
catch (Exception e) {
    getLog().error(e.getLocalizedMessage(), e);
}
finally {
    getLog().info("Script triggered on before save: " + GROOVY_NAME + " --- End");
}

/**
 * Build user name
 * @param user
 * @return
 */
private String buildUserName(User user) {
    return StringUtils.trim(user.getFirstName() + " " + user.getName());
}

private Logger getLog() {
    return (Logger) scriptLogger;
}
