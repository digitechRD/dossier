import org.slf4j.Logger

import com.digitech.airs3dossiers.airs.AirsFile;
import com.digitech.airs3dossiers.airs.AirsDocument;
import com.digitech.airs3dossiers.airs.DocumentFactory;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backing.document.DocumentCreationModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.resources.BundleUtils;

import static CourrierScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/
UserContext usrContext = userContext;
Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: U_COPIES_LUES_markAsRead_CourrierIn.groovy --- Start");

// On récupère la liste des utilisateurs qui ont lu le courrier
List<Integer> readCopyUsers = FieldUtils.getValues(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES_LUES"));
List<Integer> copyUsers = FieldUtils.getValues(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES"));
List<Integer> copyOrg = FieldUtils.getValues(theDocument, CourrierScriptUtils.getConstant("FIELD_CODE_O_COPIES"));
Integer userId = usrContext.getUserId();
Integer orgId = usrContext.getCurrentOrgId();
// On vérifie que l'utilisateur est bien en copie du courrier
if ((copyUsers != null && copyUsers.contains(userId)) || (copyOrg != null && copyOrg.contains(orgId))){
  if (!theDocument.isLocked() || CourrierScriptUtils.getDocumentMgr().isDocumentLockedByUser(usrContext, theDocument)){
    if (readCopyUsers == null) {
      readCopyUsers = new ArrayList<Integer>();
    }
    // on ajoute l'utilisateur s'il n'a pas déjà lu le courrier (normalement inutile ..)
    if (!readCopyUsers.contains(userId)) {
      readCopyUsers.add(userId);
    }
    // On sauvegarde le champ
    com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
    IDocument theNewDoc = new com.digitech.dossier.common.model.backend.airs.impl.Document(DocumentFactory.getInstance().getDocument(UserUtils.getAdminUserContext().getJeton(), theDocument.getAirsRefId()))
    FieldUtils.setValues(theNewDoc, CourrierScriptUtils.getConstant("FIELD_CODE_U_COPIES_LUES"), readCopyUsers);
    documentMgr.updateDocument(UserUtils.getAdminUserContext(), theNewDoc, java.lang.Boolean.TRUE);
  } else {
    // On affiche un message d'information indiquant que le document ne peut pas être marqué comme lu
    ScriptResultValueFieldInitializer result =  theOutput.getValue();
    result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
    result.setMessageSummary(BundleUtils.getTranslation("msg_info_document_lockbyuser_summary"));
    result.setMessageDetail(BundleUtils.getTranslation("msg_info_document_not_read_summary"));
    theOutput.setValue(result);
  }
}

logger.debug("Script field initialization: U_COPIES_LUES_markAsRead_CourrierIn.groovy --- End");
