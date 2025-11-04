import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.converter.AttachmentConverter
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.webservices.ws.hierarchical.EnumTypeNode
import com.digitech.dossier.webservices.ws.hierarchical.WSHierarchicalResult
import com.digitech.dossier.webservices.ws.hierarchical.WSNode
import org.apache.commons.lang3.StringUtils

import javax.faces.model.SelectItem

/************************************************************************************************************************************
 * Auteur 	  	: PRO+JMU
 * Date 		  	: 13/11/2018
 * Description   : Script permettant d'initialiser le panel d'envoi de mail spécifique avec lien
 * ------------------------------------------------------------------------------------------------------------------------------------
 * Paramètres d'entree :
 *   - scriptLogger
 *   - document
 *   - userContext
 ************************************************************************************************************************************/
// Version script
String versionScript = "1.6"

// Constantes
final String SCRIPT_NAME = "initialisation_sendMail_PJ [v${versionScript}]"
final String MAILTO = "MSC_MAIL_CLIENT"
final String SENDMAILAVECLIEN_PANEL_WIDTH = "900"
final String SENDMAILAVECLIEN_PANEL_HEIGHT = "650"

// Liste des types de mails prédéfinis
final Map<String, Map<String, String>> MAIL_TEMPLATES = [
    "EMPTY"            : [subject: "", content: ""],
    "SEND_PJ_MANQUANTE": [subject: "Montée sur cale - Document manquant", content: ""],
    "SEND_DOSSIER"     : [subject: "Montée sur cale - Dossier à renvoyer", content: ""]
]

scriptLogger.debug("{} --- Start", SCRIPT_NAME)

def mailContactRaw = document.getFieldValue(MAILTO)
String mailContact = mailContactRaw?.toString()?.trim() ?: ""
scriptLogger.debug("{} --- mailContact : {}", SCRIPT_NAME, mailContact)

try {

  // Paramètre à passer au module jsp
  Utils.getViewUnitController().getModel().getMailModel().clear()
  _customModel.addPanelData("sendMail_PJ", "")

  // Initialisation de la taille du panel
  Integer witdhModalPanel = Integer.parseInt(SENDMAILAVECLIEN_PANEL_WIDTH)
  Integer heightModalPanel = Integer.parseInt(SENDMAILAVECLIEN_PANEL_HEIGHT)
  _customModel.setModalPanelWidth(witdhModalPanel)
  _customModel.setModalPanelHeight(heightModalPanel)

  // Initialisation du titre du panel
  //_customModel.setModalPanelTitle(SENDMAILAVECLIEN_PANEL_TITLE);
  //_customModel.addPanelData("displayPanelSendMailAvecLien", true);

  _customModel.addPanelData("emailFrom", userContext.getUser().getEmail())
  scriptLogger.debug("{} --- Forcage de emailFrom: '{}'",SCRIPT_NAME, userContext.getUser().getEmail())
  _customModel.addPanelData("emailTo", mailContact)
  _customModel.addPanelData("emailCc", "")
  _customModel.addPanelData("emailBcc", "")
  _customModel.addPanelData("emailSubject", "")
  _customModel.addPanelData("emailContent", "")

  // Injection des modèles de mail
  List<SelectItem> mailTypeSelectList = new ArrayList<>()
  MAIL_TEMPLATES.each { key, value ->
    mailTypeSelectList.add(new SelectItem(key, value.subject))
  }
  _customModel.addPanelData("mailTypeSelectList", mailTypeSelectList)
  _customModel.addPanelData("mailTemplatesMap", MAIL_TEMPLATES)

  _customModel.addPanelData("attachmentConverter", new AttachmentConverter(true))
  // récupération de tous les PJs de l'arborescence
  List<SelectItem> attachments = new ArrayList<>()
  WSHierarchicalResult docHierarchy = ServiceUtils.getHierarchicalTreeService().buildDocumentTreeHierarchy(_userContext, _userContext.getCurrentDocument())
  for (final WSNode node in docHierarchy.trees) {
    getAllAttachments(attachments, node, scriptLogger)
  }
  _customModel.addPanelData("emailAttachments", attachments)
  _customModel.addPanelData("attachments", new ArrayList<Integer>())
}
catch (Exception e) {
  scriptLogger.error("{} --- ERREUR: '{}'",SCRIPT_NAME, e.getLocalizedMessage(), e)
}

private void getAllAttachments(attachments, node, _log) {
  _log.debug(">>> getAllAttachments(node: '{}')", node)
  if (node.id != null && (EnumTypeNode.FILE == node.type || EnumTypeNode.FOLDER == node.type)) {
    IDocument nodeDoc = ServiceUtils.getDocumentService().getDocument(_userContext, node.id)
    List<IAttachment> docAtts = nodeDoc.getAttachments(_userContext, false)
    if (!docAtts?.isEmpty()) {
      _log.debug("adding {} attachment(s)", docAtts.size())
      for (final IAttachment att in docAtts) {
        _log.debug("+ '{}'", att.getFileName())
        attachments.add(new SelectItem(att.id, att.getFileName(), att.getFileName()))
      }
    }
  }
  if (node?.children != null) {
    _log.debug("{} children", node.children.size())
    for (final WSNode childNode in node?.children) {
      getAllAttachments(attachments, childNode, _log)
    }
  }
  _log.debug("<<< getAllAttachments({})", attachments.size())
}

scriptLogger.debug("{} --- End", SCRIPT_NAME)