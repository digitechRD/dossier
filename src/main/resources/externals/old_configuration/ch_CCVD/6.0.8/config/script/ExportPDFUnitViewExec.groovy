import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;

import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import java.util.*;

import Methods;


/*************************************************************************************************
 *							          Export fichier au format PDF - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet l'export en téléchargement d'un fichier au format PDF
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
ScriptResultValueDocumentInitializer result = null;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    result = output.getValue();
    result.setMessageSummary("ACTION EXPORT AU FORMAT PDF : ");

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'initialisation du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFUnitViewExec - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW EXEC - END");
        return
    }

    File folder=data.get("folder");
    Methods.deleteFile(folder);

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - La génération du fichier PDF est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - ExportPDFUnitViewExec - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - EXPORT PDF UNIT VIEW EXEC - END");