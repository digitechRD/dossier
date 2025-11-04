import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.controller.NavigationController;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import java.util.*;

import Constants;
import Methods;

/*************************************************************************************************
 *								Définition du scanneur - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Définit ou redéfinit l’utilisateur étant le scanner du document.
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DEFINE SCANNER USER SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/

ScriptResultValueDocumentInitializer result = null;
List<IDocument> docs = null;
String lockedDocuments = null;
CustomActionController customActionController = null;
Map<String, Object> data = new HashMap<String, Object>();

try {
    result = output.getValue();
    result.setMessageSummary("ACTION DEFINITION SCANNEUR : ");

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

}catch(Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DefineScannerUserSimpleViewExec - ERREUR : ", e.localizedMessage);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try
{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - DEFINE SCANNER USER SIMPLE VIEW EXEC - END");
        return
    }

    docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

    for(IDocument doc:docs)
    {
        if(Constants.UNLOCK_TYPE.equals(doc.getLockType()))
        {
            Methods.defineDocumentIndex(doc.getAirsDocument().getInnerDocument(), Constants.FIELD_SCANNER_USER_CODE, data.get("user"));
            doc.getAirsDocument().updateContents();

            String historic = "Nouveau scanneur défini : " +  Methods.getUserMgr().getUser(Integer.valueOf(data.get("user"))).getName();
            Methods.getAuditMgr().addDocumentEvent(userContext, doc, Constants.ADV_EVENT_FIELDCHANGE, historic);

        }else{
            if(lockedDocuments == null) lockedDocuments = doc.getAirsRefId().toString();
            else lockedDocuments += ", "+doc.getAirsRefId().toString();
        }
    }

    Utils.getSearchResultController().replay();
    Utils.getSimpleViewAttachmentController().getModel().refreshDocument();
    Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE);

    if(lockedDocuments == null){
        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
        result.setMessageDetail("INFORMATION - Affectation du nouveau scanneur effectuée sur tous les documents avec succès.");
    }else{
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Les documents suivants n'ont pas été mis à jour car ils sont bloqués :"+ lockedDocuments +".");
    }
}
catch(Exception e)
{
    scriptLogger.error("[CUSTOM ACTION] - DefineScannerUserSimpleViewExec - ERREUR : ",e);
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'enregistrement des informations saisiesest impossible. Veuillez contacter votre administrateur");
}

scriptLogger.debug("[CUSTOM ACTION] - DEFINE SCANNER USER SIMPLE VIEW EXEC - END");
