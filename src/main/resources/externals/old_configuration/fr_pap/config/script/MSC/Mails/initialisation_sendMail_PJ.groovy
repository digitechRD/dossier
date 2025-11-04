import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.converter.AttachmentConverter
import com.digitech.dossier.common.service.ServiceUtils
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
  scriptLogger.debug("{} --- Forcage de emailFrom: '{}'", SCRIPT_NAME, userContext.getUser().getEmail())
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
  _scriptLogger.debug("mailTypeSelectList: {}", mailTypeSelectList?.size())
  _customModel.addPanelData("mailTypeSelectList", mailTypeSelectList)
  _customModel.addPanelData("mailTemplatesMap", MAIL_TEMPLATES)

  _customModel.addPanelData("attachmentConverter", new AttachmentConverter(true))
  // récupération des PJs du document sélectionné OU tri par noeud virtuel
  List<SelectItem> attachments = new ArrayList<>()
  Map<String, List<SelectItem>> sortedAttachments = new HashMap<>()
  IDocument document = _userContext.getCurrentDocument()
  WSHierarchicalResult docHierarchy = ServiceUtils.getHierarchicalTreeService().buildDocumentTreeHierarchy(_userContext, document)
  for (final WSNode node in docHierarchy.trees) {
    sortAttachments(sortedAttachments, attachments, node, document.getAirsRefId())
  }
  _customModel.addPanelData("emailSortedAttachments", convertToList(sortedAttachments))
  _customModel.addPanelData("emailAttachments", convertToList(Collections.singletonMap("fake_value", attachments)))
}
catch (Exception e) {
  scriptLogger.error("{} --- ERREUR: '{}'", SCRIPT_NAME, e.getLocalizedMessage(), e)
}

class SortAttachment {
  int order
  String title
  List<SelectItem> attachments
  List<Integer> selectedAttachments = new ArrayList<>()
}

static List<SortAttachment> convertToList(Map<String, List<SelectItem>> _asMap) {
  List<SortAttachment> ret = new ArrayList<>()

  int counter = 0
  for (final Map.Entry<String, List<SelectItem>> ee in _asMap.entrySet()) {
    SortAttachment sa = new SortAttachment()
    sa.order = counter++
    sa.title = ee.getKey()
    sa.attachments = ee.getValue()
    ret.add(sa)
  }

  return ret
}

private void sortAttachments(sortedAttachments, attachments, node, docId) {
  if (node?.children != null) {
    for (final WSNode childNode in node?.children) {
      if (docId == childNode.id) {
        if (childNode.children != null) {
          for (final WSNode computedNode in childNode?.children) {
            if (computedNode.children != null) {
              List<SelectItem> items = new ArrayList<>()
              for (final WSNode docNode in computedNode?.children) {
                putNodeAttachmentsInList(docNode, items)
                sortedAttachments.put(computedNode.title, items)
              }
            }
          }
        } else {
          putNodeAttachmentsInList(childNode, attachments)
        }
      }
      sortAttachments(sortedAttachments, attachments, childNode, docId)
    }
  }
}

private void putNodeAttachmentsInList(node, list) {
  IDocument nodeDoc = ServiceUtils.getDocumentService().getDocument(_userContext, node.id)
  List<IAttachment> docAtts = nodeDoc.getAttachments(_userContext, false)
  if (!docAtts?.isEmpty()) {
    for (final IAttachment att in docAtts) {
      list.add(new SelectItem(att.id, StringUtils.defaultIfBlank(att.getLabel(), att.getFileName())))
    }
  }
}

scriptLogger.debug("{} --- End", SCRIPT_NAME)