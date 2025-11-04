import com.digitech.dossier.common.model.backend.airs.impl.comment.Comment
// ****************************************************************************
// Projet : PAP - Arcade GED
// Flux : Montée sur cale
// Objet : Script appelé lors de l'action Acceptation de visa
// ****************************************************************************
// Suivi des modifications
//    Date    |   Qui   | Version | Commentaire
// 19/11/2015 |   PRO   |   1.0   | Création du script
// 07/07/2025 |   NGR   |   1.1   | Commentaire devenu facultatif via constante
// 06/08/2025 |   NGR   |   1.2   | Ajout fonction envoi mail personnalisé
// 28/08/2025 |   NGR   |   1.5   | Amélioration fonction envoi mail personnalisé

String versionScript = "1.5";

// === Imports ===
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.List
import javax.faces.application.FacesMessage

import org.apache.commons.lang.StringUtils

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.impl.comment.IComment
import com.digitech.dossier.common.model.backend.airs.impl.Document
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.common.utils.AppConfigurationUtilsCore
import com.digitech.dossier.common.utils.ApplicationUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.User
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson
import com.digitech.dossier.common.model.backend.report.value.IReportComplexValue
import org.slf4j.Logger

import static ScriptUtilsProject
import static MSC_Utils

// === Params ===
//Logger logger = scriptLogger

// === Constantes ===
final boolean COMMENTAIRE_OBLIGATOIRE = false // ⬅️ Passe à true pour rendre le commentaire obligatoire
// Notification standard
final String emailFrom = "nicolas.grinan@digitech.fr"
final String emailCc = ""
final String emailBcc = ""
final String emailToMSC = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"
final String emailToCourrier = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"
final String emailToDirAdj = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"
final String emailToCharEnv = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"
final String emailToComPort = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"
final String emailToAC = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"
final String emailToDG = "nicolas.grinan@digitech.fr,nico.grinan@gmail.com"

final String emailSubject = "Notification de tâche - Montée sur cale : "
final String emailContentTemplate = """
Bonjour,

Nous vous informons que le dossier de la montée sur cale N°%s a avancé dans son traitement.
Il est désormais positionné à l’étape : %s.

L'équipe de la cale de halage.
""".trim()
final String emailAttachments = ""
// Notification capitainerie
final String emailFromCap = "nicolas.grinan@digitech.fr"
final String emailCcCap = ""
final String emailBccCap = ""
final String emailToNotifCap = "nicolas.grinan@digitech.fr"
final String emailSubjectCap = "Notification de montée sur cale finalisée"
final String emailContentTemplateCap = """
Ia Ora Na,

Une saisie sur Oracle est à réaliser avant la facturation concernant le %s - %s du %s au %s

Poste et quai 002 Ber

Maururuu.
""".trim()
final String emailAttachmentsCap = ""

final String emailSendLinkToDocument = "true"
final String emailSendLinkToDocumentCap = "false"


// === Variables ===
boolean bRetour = true
String messageSummary
String messageDetail
String nextEtape = null
String etapeAvantLabel = null
String nextEtapeLabel = null

scriptLogger.debug("In WKF_ActionValiderDocProcess.groovy [v${versionScript}] [idDoc=${document.getAirsRefId()}]")

// Récupération du commentaire
CustomActionModel customActionModel = Utils.getCustomActionController().getModel()
String comment = customActionModel.getModalPanelModel().get("comment")

if (StringUtils.isNotBlank(comment)) {
	IComment commentObj = new Comment()
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

// Récupération des informations de la fiche
def etapeCode = ScriptUtilsProject.getTermCode(
    "MSC_ETAPE_EN_COURS", 
    FieldUtils.getValue(document,"MSC_ETAPE_EN_COURS")
)
def numMSC = FieldUtils.getValue(document,"MSC_NUM_MONTEE")
def nomNavire = FieldUtils.getValue(document,"MSC_NOM_NAVIRE")
def immatNavire = FieldUtils.getValue(document,"MSC_IMMAT_NAVIRE")
def dateDebut = FieldUtils.getValue(document, "MSC_DATE_DEBUT")
def dateFin = FieldUtils.getValue(document, "MSC_DATE_FIN")
def dateDebutStr = ""
if (dateDebut) {
    def sdf = new SimpleDateFormat("dd/MM/yyyy")
    dateDebutStr = sdf.format(dateDebut)
}
def dateFinStr = ""
if (dateFin) {
    def sdf = new SimpleDateFormat("dd/MM/yyyy")
    dateDebutStr = sdf.format(dateFin)
}

etapeAvantLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", etapeCode, 0)

// === Gestion du workflow ===
switch(etapeCode) {
	case ["reception_demande", "", null]:
		nextEtape = "controle_demande"
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "recue")}")		
		break
	case "controle_demande":
		nextEtape = "constitution_dossier"
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")}")		
		break
	case "constitution_dossier":
		nextEtape = "apposition_numero_courrier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToCourrier, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "apposition_numero_courrier":
		nextEtape = "envoi_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "planifiee")}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToMSC, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "envoi_dossier":
		nextEtape = "reception_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_client")}")
		break
	case "reception_dossier":
		nextEtape = "verification_dossier" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_receptionne")}")
		break
	case "verification_dossier":
		nextEtape = "visa_dir_adj_infra_exploit" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_verifie")}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToDirAdj, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "visa_dir_adj_infra_exploit":
		nextEtape = "visa_charge_environnement" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToCharEnv, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "visa_charge_environnement":
		nextEtape = "visa_commandant_port" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToComPort, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "visa_commandant_port":
		nextEtape = "visa_agent_comptable" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToAC, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "visa_agent_comptable":
		nextEtape = "visa_dg" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToDG, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "visa_dg":
		nextEtape = "signature" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_vise")}")
		break
	case "signature":
		nextEtape = "preparation_montee_sur_cale" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "en_instruction_signe")}")
		
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContent = String.format(emailContentTemplate, numMSC, nextEtapeLabel)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToMSC, emailFrom, emailCc, emailBcc, emailSubject+numMSC+" du "+dateDebutStr, emailContent, emailSendLinkToDocument)
		break
	case "preparation_montee_sur_cale":
		nextEtape = "montee_sur_cale" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_en_cours")}")
		
		// == Envoi de mail à la Capitainerie == //
		// On récupère le libellé de la nouvelle étape
		nextEtapeLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", nextEtape, 0)
		scriptLogger.debug("Workflow : Prochaine etape "+nextEtapeLabel)
		// On construit le contenu de mail avec ce libellé
		def emailContentCap = String.format(emailContentTemplateCap, nomNavire, immatNavire, dateDebutStr, dateFinStr)
		
		// Envoi du mail
		MSC_Utils.sendMail(document, userContext, emailToNotifCap, emailFromCap, emailCcCap, emailBccCap, emailSubjectCap, emailContentCap, emailSendLinkToDocumentCap)
		break
	case "montee_sur_cale":
		nextEtape = "facturation_montee_sur_cale" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_a_facturer")}")
		break
	case "facturation_montee_sur_cale":
		nextEtape = "montee_sur_cale_terminee" 
		FieldUtils.setValue(document, "MSC_ETAPE_EN_COURS", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
		FieldUtils.setValue(document, "MSC_ETAT_MONTEE", "${ScriptUtilsProject.getTermID("MSC_ETAT_MONTEE", "montee_sur_cale_terminee")}")
		break
}

//Historique
scriptLogger.debug("Workflow : validation etape "+etapeAvantLabel)
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
