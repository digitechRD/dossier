import Constants
import Methods
import com.digitech.dossier.admin.Utils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.jcorbairs.Document

/*************************************************************************************************
 * 							Groovy executé lors d'une sauvegarde
 **************************************************************************************************
 Date : 11.03.2016
 Auteur : MTO

 Description : Permet d'effectuer une action supplémentaire non visible par l'utilisateur lors d'une sauvegarde
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN INSERT EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueChecker result = new ScriptResultValueChecker()
Document doc = null
String nss = null
String nssTmp = null
String groupId = null
try {
    result.setMessageSummary(BundleUtils.getTranslation("groovy_save_document_action"))
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ExecutionInInsert - ERREUR : ", e)
    return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    result.setValid(true)
    doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())

    if (doc != null) {
        // Nom - prénom
        String name = null

        nss = doc.getContent().getFieldValue(Constants.FIELD_NSS_CODE)
        nssTmp = document.getField(Constants.FIELD_NSS_CODE).getValue().toString()

        if (nssTmp != null) {
            nssTmp = nssTmp.replaceAll("[^0-9\\*\\+]", "")
        }
        scriptLogger.debug("[CUSTOM ACTION] - ExecutionInInsert - DEBUG - NSS : " + nss + " / NSS Temp : " + nssTmp)
        if (nssTmp.length() == Constants.NSS_COUNT_CARACTERS) { //&& Methods.isNSSValid(nssTmp)) {
            try {
                List<String> names = Methods.getRequestInWebAI(nssTmp, "name")
                name = (names.isEmpty()) ? "" : names.get(0)
                if (name.length() > 25)
                    name = name.substring(0, 25)
            } catch (Exception e) {
                scriptLogger.warn("[CUSTOM ACTION] - ExecutionInInsert - ATTENTION :  ", e)
            }

            document.getField(Constants.FIELD_NSS_CODE).setValue(nssTmp)
            Methods.defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, nssTmp)
            Methods.defineDocumentIndex(doc, Constants.FIELD_NAME_CODE, name)
            Methods.defineDocumentIndex(doc, Constants.FIELD_CREATEUR_CODE, String.valueOf(UserContext.getInstance().getUser().getId()))
            if (Constants.USE_GROUP_LIST) {
                if (document.getField(Constants.LIST_GROUPES_DOCUMENT_CODE).getValue().toString() == null) {
                    Methods.defineDocumentIndex(doc, Constants.LIST_GROUPES_DOCUMENT_CODE, Methods.getGroupeByTypeDoc(
                            document.getField(Constants.LIST_TYPES_DOCUMENT_CODE).getValue().toString()))
                }
            }
            doc.updateContent()
            result.setValid(true)
        } else {
            document.getField(Constants.FIELD_NSS_CODE).setValue(nss)
            result.setValid(false)
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_incorrect"))
            doc.unlock()
        }

        // check mandatory PJ
        if (result.valid && document.getAttachments(new UserCoreContext(DossierCoreContext.getAdminJeton())).size() == 0) {
            result.setValid(false)
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
            result.setMessageDetail(BundleUtils.getTranslation("groovy_attachment_empty"))
            doc.unlock()
        }
    }
    //Mise a jour du champ
    Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
    Utils.getDocumentCreationController().getSelectPageController().getModel().setDeleteSourcePages(false)

} catch (Exception e) {
    result.setValid(false)
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
    scriptLogger.error("[CUSTOM ACTION] - ExecutionInInsert - ERROR :  ", e)
}
output.setValue(result)

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN INSERT EXEC - END")