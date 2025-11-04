import Constants
import Methods
import com.digitech.dossier.admin.Utils
import com.digitech.dossier.common.controller.NavigationController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.jcorbairs.Document

/*************************************************************************************************
 * 							Groovy executé lors d'une sauvegarde
 **************************************************************************************************
 Date : 11.03.2016
 Auteur : MTO

 Description : Permet d'effectuer une action supplémentaire non visible par l'utilisateur lors d'une sauvegarde
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN SAVE EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueChecker result = new ScriptResultValueChecker()
Document doc = null
String nss = null
String nssTmp = null
try {
  result.setMessageSummary(BundleUtils.getTranslation("groovy_save_document_action"))
} catch(Exception e) {
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - ExecutionInSave - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  result.setValid(true)
  doc = new Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId())

  if(doc != null) {
    nss = doc.getContent().getFieldValue(Constants.FIELD_NSS_CODE)
    nssTmp = document.getField(Constants.FIELD_NSS_CODE).getValue().toString()
    if(nssTmp != null) {
      nssTmp = nssTmp.replaceAll("[^0-9\\*\\+]", "")
    }
    if(nssTmp.length() == Constants.NSS_COUNT_CARACTERS) { //&& Methods.isNSSValid(nssTmp)) {
      document.getField(Constants.FIELD_NSS_CODE).setValue(nssTmp)
      Methods.defineDocumentIndex(doc, Constants.FIELD_NSS_CODE, nssTmp)
    }
    else {
      document.getField(Constants.FIELD_NSS_CODE).setValue(nss)
      result.setValid(false)
      result.setMessageSeverity(IScriptResultValueModel.Severity.WARN)
      result.setMessageDetail(BundleUtils.getTranslation("groovy_copypaste_nss_incorrect"))
      doc.unlock()
    }
  }
  //Mise a jour du champ
  Utils.getSearchResultController().replay()
  Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

} catch(Exception e) {
  result.setValid(false)
  result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR)
  result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"))
  scriptLogger.error("[CUSTOM ACTION] - ExecutionInSave - ERROR :  ", e)
}
output.setValue(result)

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN SAVE EXEC - END")