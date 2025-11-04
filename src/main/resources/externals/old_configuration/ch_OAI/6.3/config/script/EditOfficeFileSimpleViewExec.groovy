import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.jcorbairs.Document

import java.util.Calendar;
import java.text.SimpleDateFormat;

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment

import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController


/*************************************************************************************************
 *							Edit Office File Simple View - EXEC
 **************************************************************************************************
 Date : 04.03.2016
 Auteur : MTO

 Description : Ouvre le document Office pour modification
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EDIT OFFICE FILE SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueChecker result = new ScriptResultValueChecker();
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
IRight rightMgr = null;

try {
	result.setMessageSummary(BundleUtils.getTranslation("groovy_edit_office_action"));

	customActionController = Utils.getCustomActionController();
	data = customActionController.getModel().getModalPanelModel();

	docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

	if (data != null)
	{
		data.remove("DATA_MESSAGE");
		data.remove("DATA_STATE")
		data.remove("DATA_FILE");
	}
}catch(Exception e){
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
	scriptLogger.error("[CUSTOM ACTION] - EditOfficeFileSimpleViewExec - ERREUR : ",e);
	return;
}


/**
 * TRAITEMENT
 **************************************************************************************************/
try {

	if (docs.size() != 1) {
		result.setValid(false);
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail(BundleUtils.getTranslation("groovy_selected_one_document_only"));
		return;
	}

	File exportPathFile = new File(ExportUtils.getExportPDFDirectory());
	if (!exportPathFile.exists()) exportPathFile.mkdirs();

	Document doc = new Document(userContext.getJeton(), docs.get(0).getAirsRefId());
	File file = doc.getPrimaryDocument(doc.getPrimaryDocList().get(0), exportPathFile.getAbsolutePath());
	if(!StringUtils.isExtensionIgnoreCase(file.getName().toUpperCase(), Constants.APPLICATION_OFFICE_WORD_EXTENSION) && !StringUtils.isExtensionIgnoreCase(file.getName().toUpperCase(), Constants.APPLICATION_OFFICE_EXCEL_EXTENSION)){
		result.setValid(false);
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail(BundleUtils.getTranslation("groovy_edit_office_no_word"));
		return;
	}

	File treatedFile = new File(exportPathFile.getAbsolutePath()+"/AIRS_"+doc.getId()+"@"+Constants.APPLICATION_AIRSSERVEUR_HOST+"@"+doc.getDomain().getCode()+"@"+file.getName());

	if(treatedFile.exists()){
		try {
			treatedFile.delete();
		}
		catch(Exception e) {
			scriptLogger.error("[CUSTOM ACTION] - EditOfficeFileSimpleViewExec - ERREUR - Suppression fichier temporaire impossible : ", e);
		}
	}
	file.renameTo(treatedFile);

	String concatFile = treatedFile.toString().substring(Constants.APPLICATION_AIRSDOSSIER_FOLDER.length()).replace("\\","/");
	String fileUrl = Constants.APPLICATION_AIRSDOSSIER_URL+concatFile;

	if(StringUtils.isExtensionIgnoreCase(file.getName().toUpperCase(), Constants.APPLICATION_OFFICE_WORD_EXTENSION)) {
		scriptLogger.debug("[CUSTOM ACTION] - EditOfficeFileSimpleViewExec - DEBUG - Path : " + " ms-word:ofe|u|" + fileUrl);
		data.put("DATA_FILE", "ms-word:ofe|u|"+ fileUrl);
	}else if(StringUtils.isExtensionIgnoreCase(file.getName().toUpperCase(), Constants.APPLICATION_OFFICE_EXCEL_EXTENSION)){
		scriptLogger.debug("[CUSTOM ACTION] - EditOfficeFileSimpleViewExec - DEBUG - Path : " + " ms-excel:ofe|u|" + fileUrl);
		data.put("DATA_FILE", "ms-excel:ofe|u|"+ fileUrl);
	}

	data.put("DATA_MESSAGE", BundleUtils.getTranslation("groovy_edit_office_download"));
	data.put("DATA_STATUT", "OK");
	
  
}catch(Exception e){
	result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
	result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
	scriptLogger.error("[CUSTOM ACTION] - EditOfficeFileSimpleViewExec - ERREUR : ",e);
	return;
}

scriptLogger.debug("[CUSTOM ACTION] - EDIT OFFICE FILE SIMPLE VIEW EXEC - END");