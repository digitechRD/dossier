import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.dossier.common.resources.BundleUtils
import java.util.List
import javax.faces.model.SelectItem
import com.digitech.jcorbairs.PrimaryDocument
import com.digitech.dossier.common.utils.DocumentUtils
import org.apache.commons.lang3.StringUtils
import com.digitech.dossier.common.model.backend.UserContext
import java.io.File
import org.slf4j.Logger


/************************************************************************************************************************************
 * Auteur 	  	: SNE
 * Date         : 13/08/2025
 * Description  : Script qui récupère les modèles docx
 * ------------------------------------------------------------------------------------------------------------------------------------
 * Paramètres d'entree :
 *   - scriptLogger
 *   - document
 *   - userContext
 ************************************************************************************************************************************/

//Constantes
final String SCRIPT_NAME = "action_GenerateDocs"
final String DOCX_MODELES_FOLDER = "E:\\Arcade_PAP\\ModelesDocuments\\MONTEE_SUR_CALE"

scriptLogger.info("Script groovy : " + SCRIPT_NAME + " --- Start")

ScriptResultValueDocumentInitializer result = output.getValue()
boolean status = true
String statusMessage = ""

try {
	// Check des valeurs récupérées
	List<SelectItem> filenamesList = _customModel.getModalPanelModel().get("filenamesList")
	List<String> selectedFiles = _customModel.getModalPanelModel().get("selectedFiles")

	List<SelectItem> filenamesOtherList = _customModel.getModalPanelModel().get("linkedDocList")
	List<String> selectedFilesOther = _customModel.getModalPanelModel().get("selectedFilesOther")

	// =========== Traitement ===========
	String sMSC_NUM_MONTEE = document.getField("MSC_NUM_MONTEE").getValue()
	scriptLogger.info(SCRIPT_NAME + " - numéro du document parent = " + sMSC_NUM_MONTEE)
	if(selectedFiles.size() == 0 && selectedFilesOther.size() == 0) {
		statusMessage = "Aucun document sélectionné!"
		throw new Exception(statusMessage) as java.lang.Throwable
	}

	if(sMSC_NUM_MONTEE != null) {
		Integer itemID = MSC_Utils.getTermID("MSC_TYPE_DOCUMENT", "DOSSIER")
		List<Integer> readWriteSecretLevels = userContext.getReadWriteSecretLevelMap().get("DOC_MONTEE")
		Map<String, String> mapFieldsNewDoc = new HashMap<>()
		mapFieldsNewDoc.put("MSC_TYPE_DOCUMENT", String.valueOf(itemID))
		mapFieldsNewDoc.put("MSC_NUM_MONTEE", sMSC_NUM_MONTEE)

		// 1) ======= Documents standards =======
		createDocWithAttachments(userContext, selectedFiles, filenamesList, mapFieldsNewDoc, readWriteSecretLevels, SCRIPT_NAME, DOCX_MODELES_FOLDER, scriptLogger)

		// 2) ======= Autres documents liés aux travaux =======
		createDocWithAttachments(userContext, selectedFilesOther, filenamesOtherList, mapFieldsNewDoc, readWriteSecretLevels, SCRIPT_NAME, DOCX_MODELES_FOLDER, scriptLogger)
	}
	else {
		throw new Exception("Numéro de montée absent. Impossible de créer le document enfant!") as java.lang.Throwable
	}

	scriptLogger.info("Script groovy : " + SCRIPT_NAME + " --- End")
}
catch (Exception e) {
	scriptLogger.error(SCRIPT_NAME + " - ERREUR : " + e.getLocalizedMessage())
	status = false
}
finally {
	if (status) {
		result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO)
		result.setMessageSummary(BundleUtils.getTranslation("MSC_action_GenerateDocs_title"))
		result.setMessageDetail("Les documents ont bien été ajoutés")
	}
	else {
		result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR)
		result.setMessageSummary(BundleUtils.getTranslation("MSC_action_GenerateDocs_title"))
		result.setMessageDetail(StringUtils.isEmpty(statusMessage) ? "Une erreur s'est produite lors de l'ajout des documents" : statusMessage)
	}
	output.setValue(result)
}


private void createDocWithAttachments(UserContext uc, List<String> selectedFiles, List<SelectItem> filenamesList, Map<String, String> mapFieldsNewDoc,
																			List<Integer> readWriteSecretLevels, String SCRIPT_NAME, String docxFolder, Logger logger) {
	for(fileName in selectedFiles) {
		// Check
		File _file = new File(docxFolder + File.separator + fileName)
		if(!_file.exists()) {
			continue
		}

		//		a) label de la pj
		def item = filenamesList.find { it.value == fileName }
		def label = item?.label ?: ""
		mapFieldsNewDoc.put("MSC_NOM_DOCUMENT", label)

		// 		b) Création d'un nouveau document enfant
		IDocument childDocument = getDocumentMgr().createDocument(uc, "DOC_MONTEE", readWriteSecretLevels.get(0), mapFieldsNewDoc)
		DocumentUtils.saveDocument(childDocument)
		logger.info(SCRIPT_NAME + " - child document with id = " + childDocument.getAirsRefId())

		// 		c) Ajout de la PJ au document enfant
		PrimaryDocument pjNew = new PrimaryDocument(fileName, label)
		pjNew.setType(PrimaryDocument.ORIGINAL)
		childDocument.getAirsDocument().insertPrimaryDoc(pjNew, docxFolder)
		childDocument.getAirsDocument().updateContents()

		DocumentUtils.saveDocument(childDocument)
	}
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
}