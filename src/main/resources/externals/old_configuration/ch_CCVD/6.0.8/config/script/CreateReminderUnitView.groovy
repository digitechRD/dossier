import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.HashMap
import java.util.List
import java.util.Map
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter

import javax.faces.application.FacesMessage.Severity

import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.script.model.IScriptResultValueModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.export.PdfRenumberFontModel
import com.digitech.dossier.common.model.backend.report.ReportAttachment
import com.digitech.dossier.common.model.backend.report.ReportDocument
import com.digitech.dossier.common.model.backend.report.ReportModel
import com.digitech.dossier.common.service.export.IOdtGenerator
import com.digitech.dossier.common.service.export.impl.OdtGenerator
import com.digitech.dossier.common.utils.ExportUtils
import com.digitech.jcorbairs.exception.DocumentException
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.report.model.MergingModel.Type
import com.digitech.toolbox.document.exception.DocumentOperationException
import com.digitech.toolbox.document.service.IOperationService

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IField;

import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.PrimaryDocument

import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

String appPath = "/opt/digitech/apache-tomcat-webapps/AirsDossier/Dossier";
String ExportPath = com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory();

new File(com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory()).mkdirs();
new File(ExportPath).mkdirs();

ScriptResultValueDocumentInitializer result = null;

CustomActionController customActionController = Utils.getCustomActionController();
Map<String, Object> data = customActionController.getModel().getModalPanelModel();
data.clear();

try{
	
	result = output.getValue();
    result.setMessageSummary("CREATION D'UN RAPPEL : ");

	List<IAttachment> attachments = document.getAttachments(userContext);

	//Integer fileId = attachments.get(0).getId();

	String userLogin = userContext.getUser().getLogin();
	
	IAttachment attachment = null;
	if(attachments.size()==1){
		if(attachments.get(0).getFileName().toUpperCase().contains(Constants.APPLICATION_WORD_EXTENSION)) attachment = attachments.get(0);
	}else if(attachments.size()>1){
		if(attachments.get(0).getFileName().toUpperCase().contains(Constants.APPLICATION_WORD_EXTENSION)) attachment = attachments.get(0);
		else if(attachments.get(1).getFileName().toUpperCase().contains(Constants.APPLICATION_WORD_EXTENSION)) attachment = attachments.get(1);
	}else{
		result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
		result.setMessageDetail("ERREUR - Le document ne contient pas de pièce jointe");
		scriptLogger.error("Erreur le document ne contient pas de pièce jointe");
		return;
	}
	
	if(attachment == null){
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail("ATTENTION - Le document ne contient pas de pièce jointe de type Microsoft Word");
		scriptLogger.warn("Le document ne contient pas de pièce jointe de type Microsoft Word");
		return;
	}

	document.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), ExportPath);
	String exportDestFileName = ExportPath+"/"+attachment.getFileName();

	File originalFile = new File(exportDestFileName);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String formattedDate = sdf.format(new Date());
	File treatedFile = new File(ExportPath+"/Rappel_"+document.getField(Constants.FIELD_AFF_CODE).getValue()+"_"+formattedDate+"_"+originalFile.getName());

    // Vérifie s'il possède les propriétés WORD sinon ils sont ajoutés
    com.aspose.words.Document doc = new com.aspose.words.Document(originalFile.getAbsolutePath());
    if(!hasCommentPropertyWord(doc)){
        String adress = document.getField("ADRESSE_AFF").getValue().toString();
        String caisse = getCustomPropertyWord(doc, "AIRSORG");
        String typeDoc = getCustomPropertyWord(doc, "DOCINF/DDDOCDCTID");
        String category = getCustomPropertyWord(doc, "AIRSTYPE");
        String value = adress + "::" + caisse + "::" + typeDoc + "::" + category;
        //scriptLogger.info("VALUE : "+value);
        setCommentPropertyWord(originalFile, treatedFile, value);
    }else originalFile.renameTo(treatedFile);

	String concatFile = treatedFile.toString().substring(appPath.length());
	concatFile = concatFile.replace("\\","/");

	String fileUrl = Constants.AIRS_DOSSIER_URL+concatFile;
	
	data.put("message","Telechargement du document en cours ...");
	data.put("state","OK");
	data.put("file",fileUrl);

}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - Téléchargement de la pièce jointe est impossible. Veuillez contacter votre administrateur");
	scriptLogger.error("Erreur lors du téléchargement de la pièce jointe :",e);
	return;
}

private void setCommentPropertyWord(File originalFile, File treatedFile, String value) throws Exception{
    NPOIFSFileSystem fs = new NPOIFSFileSystem(originalFile);
    HPSFPropertiesOnlyDocument doc = new HPSFPropertiesOnlyDocument(fs);

    SummaryInformation si = doc.getSummaryInformation();
    if (si == null) doc.createInformationProperties();

    si.setComments(value);

    FileOutputStream out = new FileOutputStream(treatedFile.getAbsolutePath());
    doc.write(out);
    out.close();
    originalFile.delete();
}

private boolean hasCommentPropertyWord(com.aspose.words.Document doc) throws Exception{
	return (doc.getBuiltInDocumentProperties().getComments() != null && !"".equals(doc.getBuiltInDocumentProperties().getComments()));
}

private String getCustomPropertyWord(com.aspose.words.Document doc, String property) throws Exception{
	return doc.getCustomDocumentProperties().get(property);
}