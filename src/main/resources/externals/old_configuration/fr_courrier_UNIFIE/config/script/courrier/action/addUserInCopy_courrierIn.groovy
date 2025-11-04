import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.model.backing.picker.SelectUserAdvancedModel;
import com.digitech.dossier.common.utils.MessageUtils;
import com.digitech.jcorbairs.User;
import org.slf4j.Logger;
import static CourrierScriptUtils;

final String METHOD = "addUserInCopy_courrierIn";
// param
UserCoreContext usrContext = userContext;
IDocument theDocument = document;
final String U_COPIES_FIELDNAME = "U_COPIES";

getLog().info("Script triggered on closing : " + METHOD + " --- Start");
isValueChanged = false;
try {

    //Récupère la liste des nouveaux utilisateurs en copy.
    List<SelectUserAdvancedModel.UserRow> rows = Utils.getSelectUserAdvancedController().getModel().getCheckedUserRowOrdered();
    IField field = theDocument.getField(U_COPIES_FIELDNAME);
    List<Integer> userIds = (List<Integer>) field.getValues();
    if (rows.isEmpty()) {
        return;
    }

    if (userIds == null) {
        userIds = new ArrayList<Integer>();
    }
    for (SelectUserAdvancedModel.UserRow row : rows) {
        User user = row.getAirsUser();
        if (!userIds.contains(user.getId())) {
            userIds.add(user.getId());
            isValueChanged = true;
        }
    }

    if (isValueChanged) {
        field.setValues(userIds);
        CourrierScriptUtils.saveDocument(usrContext, theDocument);
    }
}
catch (Exception e) {
    getLog().error(e.getLocalizedMessage(), e);
    MessageUtils.setErrorMessage(UserContext.getInstance(), null, e.getLocalizedMessage(), "", e, true);
}
finally {
    getLog().info("Script triggered on closing : " + METHOD + " --- Stop");
}

private Logger getLog() {
    return (Logger) scriptLogger;
}

