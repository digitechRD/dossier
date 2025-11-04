// ****************************************************************************
// Projet : SAB - Airs Capture / Airs Dossier
// Flux : BK (Flux Bancaire)
// Objet : Script appelé lors de l'action "Archivage du document"
// Condition d'exécution : N/A
// Description :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 19/11/2015 |   PRO   |   1.0   | Création du scripte

String versionScript = "1.0";

// Java imports
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import org.apache.commons.lang.StringUtils;

// Digitech imports
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.impl.Document;
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

// Other imports

import static ScriptUtilsProject;
//import static BK_ScriptUtilsProject;

/********************* PARAM ********************************************/
// Logger scriptLogger                             : le Logger
// UserContext userContext                         : l'...userContext
// IDocument document                              : le document courant
/************************************************************************/

// ***************************************************************
// ***************************************************************
// ************************** Déclaration des constantes du script
// ***************************************************************
// ***************************************************************

// ***************************************************************
// ***************************************************************
// ****************** Déclaration des variables globales du script
// ***************************************************************
// ***************************************************************
boolean bRetour = true;
String messageSummary;
String messageDetail;
String etapeActuelle = null
String etapeActuelleLabel = null

// ***************************************************************
// ***************************************************************
// *********************************************** Début du script
// ***************************************************************
// ***************************************************************
scriptLogger.debug("In WKF_ActionRefuserDocProcess.groovy [v" + versionScript + "] [idDoc=" + document.getAirsRefId() + "]");

// On récupère le commentaire
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
String comment = customActionModel.getModalPanelModel().get("comment");

if (StringUtils.isNotBlank(comment)) {
	IComment commentObj = new Document.Comment();

	commentObj.setComment(comment);
	document.getComments().add(commentObj);

	scriptLogger.debug("WKF_ActionValiderDocProcess.groovy : Ajout commentaire [" + comment + "]");

	ScriptUtilsProject.getDocumentMgr().updateDocumentComments(userContext, document);
	
	switch(ScriptUtilsProject.getTermCode("MSC_ETAPE_EN_COURS", FieldUtils.getValue(document,"MSC_ETAPE_EN_COURS"))) {
		case "facturation_montee_sur_cale":
			etapeActuelle = "facturation_montee_sur_cale"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "montee_sur_cale")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_en_cours")));
			break
		case "montee_sur_cale":
			etapeActuelle = "montee_sur_cale"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_ber")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_a_venir")));
			break
		case "verification_ber":
			etapeActuelle = "verification_ber"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "preparation_montee_sur_cale")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_finalise")));
			break
		case "preparation_montee_sur_cale":
			etapeActuelle = "preparation_montee_sur_cale"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "signature")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_vise")));
			break
		case "signature":
			etapeActuelle = "signature"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")));
			break
		case "visa_agent_comptable":
			etapeActuelle = "visa_agent_comptable"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")));
			break
		case "visa_commandant_port":
			etapeActuelle = "visa_commandant_port"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")));
			break
		case "visa_charge_environnement":
			etapeActuelle = "visa_charge_environnement"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")));
			break
		case "visa_dir_adj_infra_exploit":
			etapeActuelle = "visa_dir_adj_infra_exploit"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")));
			break
		case "verification_dossier":
			etapeActuelle = "verification_dossier"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "reception_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_client")));
			break
		case "reception_dossier":
			etapeActuelle = "reception_dossier"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "envoi_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")));
			break
		case "envoi_dossier":
			etapeActuelle = "envoi_dossier"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "apposition_numero_courrier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")));
			break
		case "apposition_numero_courrier":
			etapeActuelle = "apposition_numero_courrier"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "constitution_dossier")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")));
			break
		case "constitution_dossier":
			etapeActuelle = "constitution_dossier"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "controle_demande")));
			FieldUtils.setValue(document, "MSC_ETAT_MONTEE", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "recue")));
			break
		case "controle_demande":
			etapeActuelle = "controle_demande"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "reception_demande")));
			break
		case "reception_demande":
			etapeActuelle = "reception_demande"
			FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", Integer.toString(ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "")));
			break	
	}
	
	//Historique
	etapeAvantLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", etapeActuelle, 0)
	scriptLogger.debug("Workflow : refus etape "+etapeActuelleLabel)
	ScriptUtilsProject.getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_WF_TASK_SUBMIT.name(), BundleUtils.getTranslation("WKF_ActionValiderDocProcess")+" : Refus "+etapeAvantLabel)

	//FieldUtils.setValue(document, ScriptUtilsProject.getConstant("STATUT_WORKFLOW"),
	//FieldUtils.setValue(document, "STATUT_WORKFLOW", Integer.toString(ScriptUtilsProject.getTermID("STATUT_WORKFLOW", "VALIDATION")));
	//document.getAirsDocument().setSecretLevel(Integer.valueOf(ScriptUtilsProject.getConstant("VALUE_SECRET_ARCHIVE")));
	DocumentUtils.saveDocument(document);
	
	// on reste sur le meme document 
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
}
else {
	bRetour = false;

	messageSummary = BundleUtils.getTranslation("WKF_error_commentOblig");
	messageDetail = BundleUtils.getTranslation("WKF_error_commentObligDetail");

	scriptLogger.debug("WKF_ActionValiderDocProcess.groovy : Aucun commentaire");
}

ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(bRetour);
if (bRetour != true) {
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN);
	result.setMessageSummary(messageSummary);
	result.setMessageDetail(messageDetail);
}
output.setValue(result);

scriptLogger.debug("Out WKF_ActionRefuserDocProcess.groovy");

// Fin du script

// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
