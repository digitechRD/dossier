import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.report.service.impl.docx.AsposeConverterService
import com.digitech.jcorbairs.PrimaryDocument

/************************************************************************************************************************************
 * Auteur 	  	: LBE
 * Date         : 28/08/2025
 * Description  : Script qui transforme les pj docx en pdf
 ************************************************************************************************************************************/

//Constantes
final String SCRIPT_NAME = "action_transform_pj_docx_pdf"
scriptLogger.info("Script groovy : " + SCRIPT_NAME + " --- Start")

ScriptResultValueDocumentInitializer result = output.getValue()
boolean status = true
String pathUpload = "E:\\Arcade_PAP\\arcadeged\\apache-tomcat\\webapps\\ArcadeGed\\tmp\\upload"
try {
    if (binding.variables.containsKey("document")) {
        IDocument doc = document
        List<PrimaryDocument> primDocs = doc.getAirsDocument().getPrimaryDocList()
        AsposeConverterService acs = new AsposeConverterService()
        for (PrimaryDocument primDoc : primDocs) {
            if (primDoc.getFileName().toLowerCase().endsWith(".docx")) {
                String filename = primDoc.getFileName()
                doc.getAirsDocument().getPrimaryDoc(primDoc, pathUpload)
                File fInput = new File(pathUpload + File.separator + filename)
                File fOuput = new File(pathUpload + File.separator + filename.substring(0, filename.length() - 5) + ".pdf")
                acs.convert(fInput, fOuput)
                doc.getAirsDocument().insertPrimaryDoc(new PrimaryDocument(filename.substring(0, filename.length() - 5) + ".pdf", primDoc.getLabel()), pathUpload)
                doc.getAirsDocument().deletePrimaryDoc(primDoc)
                fInput.delete()
                fOuput.delete()
            }
        }
    }

    status = true
    scriptLogger.info("Script groovy : " + SCRIPT_NAME + " --- End")
}
catch (Exception e) {
    scriptLogger.error(SCRIPT_NAME + " - ERREUR : " + e.getLocalizedMessage())
    status = false
}
finally {
    if (status) {
        result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO)
        result.setMessageSummary("Les documents ont bien été transformés")
        result.setMessageDetail("Les documents ont bien été transformés")
    }
    else {
        result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR)
        result.setMessageSummary("Une erreur s'est produite lors de la transformation des documents en pdf")
        result.setMessageDetail("Une erreur s'est produite lors de la transformation des documents en pdf")
    }
    output.setValue(result)
}