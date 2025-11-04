import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.admin.UserAdmin
import com.digitech.jcorbairs.admin.UsersManager
import org.apache.commons.codec.binary.Base64

import java.nio.charset.StandardCharsets

/*************************************************************************************************
 * 							Visualisation du dossier assuré - EXEC
 **************************************************************************************************
 Date : 06.07.2016
 Auteur : MTO

 Description : Permet de visualiser le dossier du document sélectionné
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - VIEW INSURED FOLDER SIMPLE VIEW EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null
List<IDocument> docs = null
Map<String, Object> data = null
CustomActionController customActionController = null

try {
    result = output.getValue()
    result.setMessageSummary(BundleUtils.getTranslation("groovy_view_insured_folder_action"))
    customActionController = Utils.getCustomActionController()
    data = customActionController.getModel().getModalPanelModel()
    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments()
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ViewInsuredFolderSimpleView - ERREUR : ", e)
    return
}


/**
 * TRAITEMENT
 **************************************************************************************************/

try {
    if (docs.size() != 1) {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
        result.setMessageDetail(BundleUtils.getTranslation("groovy_selected_one_document_only"))
        return
    }

    IDocument document = docs.get(0)
    UserAdmin userAdmin = UsersManager.load(DossierCoreContext.getAdminJeton(), userContext.getUser().getId())
    String authentification = userAdmin.getLogin() + ":" + Methods.getPassWordOfUser(userAdmin.getLogin())
    String userEncode = new String(Base64.encodeBase64(authentification.getBytes(StandardCharsets.UTF_8)))
    String linkLabel = document.getField(Constants.FIELD_NSS_CODE).getValue().toString()
    String link = Constants.APPLICATION_AIRSDOSSIER_URL + "faces/redirect.jsp?authentication=" + userEncode + "&orgId=" + userContext.getCurrentOrgId() +
            "&outcome=gotoSimpleView&cty=" +
            Constants.CTY_DOCUMENT_ASSURE + "&field1=" + Constants.FIELD_NSS_CODE + "&value1=" + document.getField(Constants.FIELD_NSS_CODE).getValue().toString()

    data.put("LINK_LABEL", linkLabel)
    data.put("LINK", link)
    data.put("STATE", "OK")

    scriptLogger.debug("[CUSTOM ACTION] - VIEW INSURED FOLDER SIMPLE VIEW EXEC - URL : " + link)

    Utils.getSearchResultController().replay()

} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ViewInsuredFolderSimpleView - ERREUR : ", e)
    return
}


scriptLogger.debug("[CUSTOM ACTION] - VIEW INSURED FOLDER SIMPLE VIEW EXEC - END")