// ****************************************************************************
// Projet : SAB - Airs Capture / Airs Dossier
// Flux : BK (Flux Bancaire)
// Objet : Script appelé lors de l'action "Archivage du document"
// ****************************************************************************
// Suivi des modifications
//    Date    |   Qui   | Version | Commentaire
// 19/11/2015 |   PRO   |   1.0   | Création du script
// 07/07/2025 |   GPT   |   1.1   | Commentaire devenu facultatif via constante

String versionScript = "1.1";

// === Imports ===
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.List
import javax.faces.application.FacesMessage

import org.apache.commons.lang.StringUtils

import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment
import com.digitech.dossier.common.model.backend.airs.impl.Document
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker

import static ScriptUtilsProject

// === Constantes ===
final boolean COMMENTAIRE_OBLIGATOIRE = false // ⬅️ Passe à true pour rendre le commentaire obligatoire

// === Variables ===
boolean bRetour = true
String messageSummary
String messageDetail
String etapeActuelle = null
String etapeActuelleLabel = null

scriptLogger.debug("In WKF_ActionValiderDocProcess.groovy [v${versionScript}] [idDoc=${document.getAirsRefId()}]")

// Récupération du commentaire
CustomActionModel customActionModel = Utils.getCustomActionController().getModel()
String comment = customActionModel.getModalPanelModel().get("comment")

if (StringUtils.isNotBlank(comment)) {
	IComment commentObj = new Document.Comment()
	commentObj.setComment(comment)
	document.getComments().add(commentObj)

	scriptLogger.debug("Ajout commentaire : ${comment}")

	ScriptUtilsProject.getDocumentMgr().updateDocumentComments(userContext, document)
	
} else {
	if (COMMENTAIRE_OBLIGATOIRE) {
		bRetour = false
		messageSummary = BundleUtils.getTranslation("WKF_error_commentOblig")
		messageDetail = BundleUtils.getTranslation("WKF_error_commentObligDetail")
		scriptLogger.debug("Aucun commentaire - blocage activé")
	} else {
		scriptLogger.debug("Aucun commentaire - autorisé (commentaire facultatif)")
	}
}

// === Gestion du workflow ===
switch(ScriptUtilsProject.getTermCode("MSC_ETAPE_EN_COURS", FieldUtils.getValue(document,"MSC_ETAPE_EN_COURS"))) {
	case ["reception_demande", "", null]:
		etapeActuelle = "reception_demande"
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "controle_demande")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "recue")}")
		break
	case "controle_demande":
		etapeActuelle = "controle_demande"
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "constitution_dossier")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")}")
		break
	case "constitution_dossier":
		etapeActuelle = "constitution_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "apposition_numero_courrier")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")}")
		break
	case "apposition_numero_courrier":
		etapeActuelle = "apposition_numero_courrier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "envoi_dossier")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")}")
		break
	case "envoi_dossier":
		etapeActuelle = "envoi_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "reception_dossier")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_client")}")
		break
	case "reception_dossier":
		etapeActuelle = "reception_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_dossier")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")}")
		break
	case "verification_dossier":
		etapeActuelle = "verification_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "visa_dir_adj_infra_exploit")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_verifie")}")
		break
	case "visa_dir_adj_infra_exploit":
		etapeActuelle = "visa_dir_adj_infra_exploit" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "visa_charge_environnement")}")
		break
	case "visa_charge_environnement":
		etapeActuelle = "visa_charge_environnement" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "visa_commandant_port")}")
		break
	case "visa_commandant_port":
		etapeActuelle = "visa_commandant_port" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "visa_agent_comptable")}")
		break
	case "visa_agent_comptable":
		etapeActuelle = "visa_agent_comptable" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "signature")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_vise")}")
		break
	case "signature":
		etapeActuelle = "signature" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "preparation_montee_sur_cale")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_signe")}")
		break
	case "preparation_montee_sur_cale":
		etapeActuelle = "preparation_montee_sur_cale" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "verification_ber")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_a_venir")}")
		break
	case "verification_ber":
		etapeActuelle = "verification_ber" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "montee_sur_cale")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_en_cours")}")
		break
	case "montee_sur_cale":
		etapeActuelle = "montee_sur_cale" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "facturation_montee_sur_cale")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_a_facturer")}")
		break
	case "facturation_montee_sur_cale":
		etapeActuelle = "facturation_montee_sur_cale" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", "montee_sur_cale_terminee")}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_terminee")}")
		break
}

//Historique
etapeAvantLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", etapeActuelle, 0)
scriptLogger.debug("Workflow : validation etape "+etapeActuelleLabel)
ScriptUtilsProject.getAuditMgr().addDocumentEvent(userContext, document, AdvancedAuditType.ADV_EVENT_WF_TASK_SUBMIT.name(), BundleUtils.getTranslation("WKF_ActionValiderDocProcess")+" : "+etapeAvantLabel)

// Sauvegarde du document et redirection
DocumentUtils.saveDocument(document)
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document))

// Résultat
ScriptResultValueChecker result = new ScriptResultValueChecker()
result.setValid(bRetour)
if (!bRetour) {
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.WARN)
	result.setMessageSummary(messageSummary)
	result.setMessageDetail(messageDetail)
}
output.setValue(result)

scriptLogger.debug("Out WKF_ActionValiderDocProcess.groovy")
