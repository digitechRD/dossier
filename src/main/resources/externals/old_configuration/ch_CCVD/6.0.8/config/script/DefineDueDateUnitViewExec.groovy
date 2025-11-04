import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.controller.NavigationController;

import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.script.model.IScriptResultValueModel;

import com.digitech.jcorbairs.Note;
import com.digitech.jcorbairs.Document;

import java.text.SimpleDateFormat;
import java.util.*;

import Constants;
import Methods;

/*************************************************************************************************
 *								Définition de la date d'échéance - EXEC
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la définition de l'index date d'échéance d'un document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DEFINE DUE DATE UNIT VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;

try {
    result = output.getValue();
    result.setMessageSummary("ACTION DATE ECHEANCE : ");

    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'exécution du traitement est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DefineDueDateUnitViewExec - ERREUR : ",e);
    return;
}


/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    // Vérifie que l'initialisation de l'action n'a pas rencontré d'erreur
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - DEFINE DUE DATE UNIT VIEW EXEC - END");
        return
    }
    // Vérifie que le document n'est pas verouillé
    if (Constants.UNLOCK_TYPE.equals(document.getLockType())) {
        //Définition date d'échéance
        Document doc = document.getAirsDocument().getInnerDocument();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date d = data.get("date");
        String formatDate = "";
        if (d != null) formatDate = sdf.format(d);
        Methods.defineDocumentIndex(doc, Constants.FIELD_DATE_DUE_CODE, formatDate);

        //Définition commentaire
        String comment = data.get("comment");
        if (!comment.equalsIgnoreCase("")) {
            Note newNote = new Note(Constants.AIRS_NOTE_ID);
            newNote.setText(comment);
            newNote.setPublic();
            document.getAirsDocument().addComment(newNote);
        }
        doc.updateContent();

        //Définition de l'historique
        String historic = "Date d'échéance définie : " + formatDate;
        Methods.getAuditMgr().addDocumentEvent(userContext, document, Constants.ADV_EVENT_FIELDCHANGE, historic);

        Utils.getViewUnitController().edit();
        Utils.getViewUnitController().save();
        Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_VIEW_UNIT);
        scriptLogger.debug("La date d'échéance suivante " + formatDate + " a été enregistré avec succès");

        result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
        result.setMessageDetail("INFORMATION - La date d'échéance suivante " + formatDate + " a été enregistrée avec succès");
    } else {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail("ATTENTION - Le date d'échéance ne peut être enregistrée car le document est vérouillé");
    }

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - L'enregistrement des informations saisies est impossible. Veuillez contacter votre administrateur");
    scriptLogger.error("[CUSTOM ACTION] - DefineDueDateUnitViewExec - ERREUR : ",e);
    return;
}

scriptLogger.debug("[CUSTOM ACTION] - DEFINE DUE DATE UNIT VIEW EXEC - END");
