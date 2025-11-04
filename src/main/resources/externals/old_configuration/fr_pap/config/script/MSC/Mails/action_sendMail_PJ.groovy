import MSC_Utils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Token

/************************************************************************************************************************************
 * Auteur 	  	: JMU-PRO
 * Date         : 13/11/2018
 * Description  : Script lançant l'envoi de mail spécifique avec lien
 * ------------------------------------------------------------------------------------------------------------------------------------
 * Paramètres d'entree :
 *   - _scriptLogger
 *   - document
 *   - userContext
 ************************************************************************************************************************************/

//Constantes
final String SCRIPT_NAME = "action_sendMail_PJ"

_scriptLogger.debug("Script groovy de type workflow [Envoi du document par mail avec lien] : " + SCRIPT_NAME + " --- Start")

ScriptResultValueDocumentInitializer result = output.getValue()
boolean ret = true

try {
  String emailFrom = _customModel.getPanelData("emailFrom")
  _scriptLogger.debug(SCRIPT_NAME + " : emailFrom = " + emailFrom)

  String emailTo = _customModel.getPanelData("emailTo")
  _scriptLogger.debug(SCRIPT_NAME + " : emailTo = " + emailTo)

  String emailCc = _customModel.getPanelData("emailCc")
  _scriptLogger.debug(SCRIPT_NAME + " : emailCc = " + emailCc)

  String emailBcc = _customModel.getPanelData("emailBcc")
  _scriptLogger.debug(SCRIPT_NAME + " : emailBcc = " + emailBcc)

  String emailSubject = _customModel.getPanelData("emailSubject")
  _scriptLogger.debug(SCRIPT_NAME + " : emailSubject = " + emailSubject)

  String emailContent = _customModel.getPanelData("emailContent")
  _scriptLogger.debug(SCRIPT_NAME + " : emailContent = " + emailContent)

  List<IAttachment> emailAttachments = new ArrayList<>()

  def sortAttachmentsList = _customModel.getPanelData("emailSortedAttachments")
  if (sortAttachmentsList != null) {
    for (final def sa in sortAttachmentsList) {
      for (final def attId in sa?.selectedAttachments) {
        IAttachment lAtt = ServiceUtils.getAttachmentService().loadAttachment(_userContext.getJeton() as Token, attId)
        if (lAtt != null) {
          emailAttachments.add(lAtt)
        }
      }
    }
  }

  _scriptLogger.debug(SCRIPT_NAME + " : Nb pj = " + emailAttachments?.size())

  // Envoi du mail.
  _scriptLogger.debug(SCRIPT_NAME + " : Envoi du mail.")

  if (MSC_Utils.sendMail_PJ(document, userContext,
      emailTo, emailFrom, emailCc, emailBcc, emailSubject, emailContent, "false", emailAttachments)) {

    // Ajout d'un historique.
    _scriptLogger.debug(SCRIPT_NAME + " : Ajout de l'historique.")
    MSC_Utils.addHisto(document, userContext, AdvancedAuditType.ADV_EVENT_MAIL.name(), "Envoi du mail à " + emailTo)

    // On sauve le document
    _scriptLogger.debug(SCRIPT_NAME + " : Sauvegarde du document")
    DocumentUtils.saveDocument(document)

    // On raffiche la page courante et on rafraichit la page des résultats de recherche
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document))
  } else {
    ret = false
  }

}
catch (Exception e) {
  _scriptLogger.error(SCRIPT_NAME + " : ERREUR : " + e.getLocalizedMessage())
  ret = false
}

// Gestion des messages à afficher pour l'utilisateur
if (ret) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.INFO)
  result.setMessageSummary("Envoi du document par mail")
  result.setMessageDetail("L'envoi de l'E-mail a bien ete prise en compte")
} else {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageSummary("Envoi du document par mail")
  result.setMessageDetail("Une erreur s'est produite au cours de l'envoi de l'E-mail")
}

_scriptLogger.debug("Script groovy de type workflow [Envoi du document par mail avec lien] : " + SCRIPT_NAME + " --- End")