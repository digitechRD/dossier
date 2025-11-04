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
import com.digitech.dossier.script.model.IScriptResultModel
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

String SELECT_KO = "SELECTION_INCORRECTE";
String SELECT_KO_DETAIL = "Selectionner un seul document";
String DOC_LOCK = "DOCUMENT_VERROUILLE";
String DOC_LOCK_DETAIL = "Un autre utilisateur travaille sur ce document";

String appPath = "/opt/digitech/apache-tomcat-webapps/AirsDossier/Dossier";
String xmlPath = "/opt/digitech/apache-tomcat-webapps/AirsDossier/tmp/editDocument/";

//scriptLogger.debug(">>> Starting exportToEdit.groovy...");

UserCoreContext usrContext = userContext;

String ExportPath = com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory();

//scriptLogger.debug("Export path is {}", ExportPath);
new File(com.digitech.dossier.common.utils.ExportUtils.getExportPDFDirectory()).mkdirs();
new File(ExportPath).mkdirs();

ScriptResultValueDocumentInitializer result = null;

CustomActionController customActionController = Utils.getCustomActionController();
Map<String, Object> data = customActionController.getModel().getModalPanelModel();
data.clear();

List<IDocument> docs = null;

try{

	result = output.getValue();
    result.setMessageSummary("MODIFICATION D'UN DOCUMENT : ");
	
	docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

	if (docs == null || docs.size() == 0){
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail("ATTENTION : Veuillez sélectionner un document à éditer");
		return;
	}

	IDocument document = docs.get(0);

	List<IAttachment> attachments = document.getAttachments(usrContext);

	Integer fileId = attachments.get(0).getId();
	String xmlFile = xmlPath+fileId;

	String userLogin = usrContext.getUser().getLogin();


	if(isLocked(xmlFile,userLogin))
	{
		result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
		result.setMessageDetail("ATTENTION : Le document est vérouillé par un autre utilisateur");
		return;
	}
	else
	{
		//generateXmlFile(xmlFile, userLogin)
		
		document.getAirsDocument().getInnerDocument().getPrimaryDocument(attachments.get(0).getAirsAttachment(), ExportPath);
		String exportDestFileName = ExportPath+"/"+attachments.get(0).getFileName();

		File originalFile = new File(exportDestFileName);
		File treatedFile = new File(ExportPath+"/AIRS_"+document.getAirsDocument().getId()+"@"+document.getAirsDocument().getDomain().getCode()+"@"+originalFile.getName()); 

		originalFile.renameTo(treatedFile);

		String concatFile = treatedFile.toString().substring(appPath.length());
		concatFile = concatFile.replace("\\","/");

		String fileUrl = Constants.AIRS_DOSSIER_URL+concatFile;
		scriptLogger.debug("Fichier chemin : "+fileUrl);
		
		data.put("message","Telechargement du document en cours ...");
		data.put("state","OK");
		data.put("file",fileUrl);
	}
}catch(Exception e){
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail("ERREUR - Téléchargement de la pièce jointe est impossible. Veuillez contacter votre administrateur");
	scriptLogger.error("Erreur lors du téléchargement de la pièce jointe :",e);
	return;
}

output.setValue( result );

private void generateXmlFile(String file, String userLogin)
{
    Element racine = new Element("ROOT");
    org.jdom2.Document xmlDoc = new org.jdom2.Document(racine);
   
    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    String today = formatter.format( new Date());
    
    racine.addContent(new Element("DATE").addContent(today));
    racine.addContent(new Element("USER").addContent(userLogin));
    
    try
    {            
        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        sortie.output(xmlDoc, new FileOutputStream(file));
    }
    catch (Exception e)
    {
		scriptLogger.error("Erreur à la création du fichier xml de verrou" +e);
    }      
}

private boolean isLocked(String file, String userLogin)
{
    //scriptLogger.debug("----- Test de verrou");
	
	File f = new File(file);
	boolean result = true;
	
	if (f.exists())
	{
	
		//scriptLogger.debug("Le fichier de verrou existe");
		org.jdom2.Document document = null;

		try 
		{
			SAXBuilder sxb = new SAXBuilder();
			document = sxb.build(file);
		}
		catch (Exception e)
		{
			scriptLogger.error("Erreur au lancement de la lecture du fichier xml de verrou" +e);
		}
	
		try
		{
			Element racine = document.getRootElement();       
			DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String today = formatter.format( new Date());
			
			if (racine.getChildText("DATE").equals(today))
			{
				scriptLogger.debug("Le fichier de verrou existe en date du jour");
				
				if(racine.getChildText("USER").equals(userLogin))
				{
					scriptLogger.debug("Le fichier de verrou existe pour le user");
					result = false;
				}
				else
				{
					scriptLogger.debug("Le fichier de verrou est inexistant pour le user");
					result = true;
				}							
			}
			else
			{
				scriptLogger.debug("Aucun verrou pour ce fichier en date du jour");
				result = false;
			}
		} 
		catch (Exception e) 
		{
			scriptLogger.error("Erreur à la création du fichier xml de verrou" +e);
		}
	}
	else
	{
		scriptLogger.debug("Le fichier de verrou est inexistant");
		result = false;
	}
    
    return result;   
}


//data.put("url",dataUrl);

/*
ScriptResultValueExportInitializer result = new ScriptResultValueExportInitializer();
result.setFileResultName(attachments.get(0).getFileName());
result.setFileResultPath(exportDestFileName);
 
output.setValue( result );

return output;*/