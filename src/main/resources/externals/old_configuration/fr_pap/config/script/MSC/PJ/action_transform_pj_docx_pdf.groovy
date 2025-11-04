import com.aspose.words.Document
import com.aspose.words.DocumentProperty
import com.aspose.words.HeaderFooter
import com.aspose.words.LoadOptions
import com.aspose.words.PageSetup
import com.aspose.words.PdfSaveOptions
import com.aspose.words.Section
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.report.service.impl.docx.AsposeConverterService
import com.digitech.jcorbairs.PrimaryDocument
import org.apache.commons.lang.StringUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.utils.NavigationUtils

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
                Document officeDoc = new Document(fInput.getAbsolutePath(), new LoadOptions())
                DocumentProperty property = officeDoc.getCustomDocumentProperties().get("multiFooter");
                if (property != null && property.getValue() instanceof Boolean && (Boolean)property.getValue()) {
                    HeaderFooter defaultFooter = null;
                    Iterator var5 = officeDoc.getSections().iterator();

                    while(var5.hasNext()) {
                        Section section = (Section)var5.next();
                        if (defaultFooter == null) {
                            defaultFooter = section.getHeadersFooters().getByHeaderFooterType(3);
                        } else {
                            PageSetup pageSetup = section.getPageSetup();
                            pageSetup.setDifferentFirstPageHeaderFooter(false);
                            pageSetup.setOddAndEvenPagesHeaderFooter(false);
                            HeaderFooter firstFooter = section.getHeadersFooters().getByHeaderFooterType(5);
                            HeaderFooter primaryFooter = section.getHeadersFooters().getByHeaderFooterType(3);
                            HeaderFooter evenFooter = section.getHeadersFooters().getByHeaderFooterType(2);
                            if (firstFooter != null && StringUtils.equals(firstFooter.getText(), defaultFooter.getText())) {
                                firstFooter.removeAllChildren();
                            }

                            if (primaryFooter != null && StringUtils.equals(primaryFooter.getText(), defaultFooter.getText())) {
                                primaryFooter.removeAllChildren();
                            }

                            if (evenFooter != null && StringUtils.equals(evenFooter.getText(), defaultFooter.getText())) {
                                evenFooter.removeAllChildren();
                            }
                        }
                    }
                }
                PdfSaveOptions opts = new PdfSaveOptions()
                opts.setPreserveFormFields(true)
                officeDoc.save(fOuput.getAbsolutePath(), opts)
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
    Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, document))
}