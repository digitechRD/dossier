import FDOC_Utils;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.ExportUtils;
import java.io.File;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.NavigationUtils;
import org.apache.commons.lang.StringUtils;

/************************************************************************************************************************************
* Auteur 	  	: JMU
* Date 		  	: 29/06/2016
* Description   : Script permet de generer la pièce jointe editable
*------------------------------------------------------------------------------------------------------------------------------------  
* Parametres d'entree :
*   - scriptLogger
*   - document
*   - userContext
************************************************************************************************************************************/

//Constantes
final String SCRIPT_NAME = "action_generation_document";
final String ATTACHMENT_TYPE_EDIT = FDOC_Utils.getConstant("ATTACHMENT_TYPE_EDIT");

boolean ret = true;
ScriptResultValueDocumentInitializer result = output.getValue();

scriptLogger.debug("Script groovy de type workflow : "+SCRIPT_NAME+" --- Start");

try {
	// Recuperation du modèle de document ODT associe en fonction du type de document
	String ODTModelName = FDOC_Utils.getODTModel(document, userContext);

	// Fusion des champs + Ajout de l'ODT en PJ du document AIRS
	String exportODTFilePath = ExportUtils.getODTDirectory() + File.separator + ODTModelName;
	
	String exportDestFileName = null;

	exportDestFileName = ExportUtils.getPdfFileName();

	// On definit les chemins 
	String ExportPath = ExportUtils.getExportPDFDirectory();
	String exportDestPath = ExportPath + exportDestFileName;
	String exportDestPathODT = ExportPath + exportDestFileName.replace(".pdf",".odt");
	String exportDestPathPDF = ExportPath + exportDestFileName;

	scriptLogger.debug(SCRIPT_NAME+" : ExportPath = "+ExportPath);
	scriptLogger.debug(SCRIPT_NAME+" : exportDestPath = "+exportDestPath);
	scriptLogger.debug(SCRIPT_NAME+" : exportDestPathODT = "+exportDestPathODT);
	scriptLogger.debug(SCRIPT_NAME+" : ExportPath = "+exportDestPathPDF);
	
	new File(ExportUtils.getExportPDFDirectory()).mkdirs();
	new File(ExportPath).mkdirs();
	
	List<IDocument> selectedDocumentsList = new ArrayList<IDocument>();
	selectedDocumentsList.add(document);
	
	scriptLogger.debug(SCRIPT_NAME+" : generation du document ODT");
	FDOC_Utils.generateDocument(exportODTFilePath, exportDestPathODT, exportDestPath, ExportPath, selectedDocumentsList);
	
	scriptLogger.debug(SCRIPT_NAME+" : Ajout de la PJ");
	FDOC_Utils.addPrimaryDoc(document, exportDestPathODT, ATTACHMENT_TYPE_EDIT);
	
	// Ajout d'un historique
	scriptLogger.debug(SCRIPT_NAME+" : Ajout de l'historique");
    FDOC_Utils.addHistoForWorkflow(document, userContext, "Generation de document");
	
	// Sauvegarde du document
    scriptLogger.debug(SCRIPT_NAME+" : Sauvegarde du document");
    DocumentUtils.saveDocument(document);

	// On raffiche la page courante et on rafraichit la page des résultats de recherche
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document));
}
catch(Exception e) {
	ret = false;
	scriptLogger.error(SCRIPT_NAME+" : ERREUR : "+e.getLocalizedMessage());
}

// Gestion des messages à afficher pour l'utilisateur
if(ret)
{
    result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO);
    result.setMessageSummary("Generation de document");
    result.setMessageDetail("La generation du document a ete realisee avec succès");
}
else
{
    result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
    result.setMessageSummary("Generation de document");
    result.setMessageDetail("Une erreur s'est produite au cours de la generation du document");
}

scriptLogger.debug("Script groovy de type workflow : "+SCRIPT_NAME+" --- End");