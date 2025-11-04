import com.digitech.dossier.admin.Utils
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;
import com.digitech.jcorbairs.Document;

import Constants;
import Methods;

/*************************************************************************************************
 * 							Groovy executé lors d'une sauvegarde
 **************************************************************************************************
 Date : 11.03.2016
 Auteur : MTO

 Description : Permet d'effectuer une action supplémentaire non visible par l'utilisateur lors d'une sauvegarde depuis la vue unitaire de modification
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN UPDATE EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueChecker result = new ScriptResultValueChecker();
Document doc = null;
String nss = null
String nssTmp = null;
try {
    result.setMessageSummary(BundleUtils.getTranslation("groovy_save_document_action"));
} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
    scriptLogger.error("[CUSTOM ACTION] - ExecutionInUpdate - ERREUR : ", e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    result.setValid(true);
    doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId());

    // Nom - prénom
    String name = null;


    if (doc != null) {
        nss = doc.getContent().getFieldValue(Constants.FIELD_NSS_CODE);
        nssTmp = document.getField(Constants.FIELD_NSS_CODE).getValue().toString();
        if (nssTmp != null) {
            nssTmp = nssTmp.replaceAll("[^0-9\\*\\+]", "");
            try {
                List<String> names = Methods.getRequestInWebAI(nssTmp, "name");
                name = (names.isEmpty()) ? "" : names.get(0);
                if (name.length() > 25) name = name.substring(0, 25);
            } catch (Exception e) {
                scriptLogger.warn("[CUSTOM ACTION] - ExecutionInUpdate - ATTENTION :  ", e);
            }
        }
        if (nssTmp.length() == Constants.NSS_COUNT_CARACTERS) {
            document.getField(Constants.FIELD_NSS_CODE).setValue(nssTmp);
            Methods.defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, nssTmp);
            document.getField(Constants.FIELD_NAME_CODE).setValue(name);
            Methods.defineDocumentIndex(doc, Constants.FIELD_NAME_CODE, name);
        } else {
            document.getField(Constants.FIELD_NSS_CODE).setValue(nss);
            result.setValid(false);
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
            result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_incorrect"));
        }

        if (Constants.USE_GROUP_LIST) {
            if (document.getField(Constants.LIST_GROUPES_DOCUMENT_CODE).getValue() == null || "".equalsIgnoreCase(document.getField(Constants.LIST_GROUPES_DOCUMENT_CODE).getValue().toString())) {
                String value = Methods.getGroupeByTypeDoc(document.getField(Constants.LIST_TYPES_DOCUMENT_CODE).getValue().toString());
                Methods.defineDocumentIndex(doc, Constants.LIST_GROUPES_DOCUMENT_CODE, value);
                if(value != null) document.getField(Constants.LIST_GROUPES_DOCUMENT_CODE).setValue(Integer.parseInt(value));
            }
        }
    }
} catch (Exception e) {
    result.setValid(false);
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
    scriptLogger.error("[CUSTOM ACTION] - ExecutionInUpdate - ERROR :  ", e);
}
output.setValue(result);

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN UPDATE EXEC - END");