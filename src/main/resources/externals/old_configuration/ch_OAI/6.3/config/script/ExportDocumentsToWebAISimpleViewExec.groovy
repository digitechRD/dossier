import Constants
import Methods
import com.digitech.common.lib.utils.StringUtils
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager
import com.digitech.toolbox.document.service.impl.tiff.TIFFOperationService
import org.apache.commons.io.FilenameUtils
import org.w3c.dom.Document
import com.aspose.pdf.facades.PdfFileEditor;
import com.aspose.pdf.facades.IPdfFileEditor

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.text.SimpleDateFormat
import com.aspose.pdf.facades.PdfFileInfo;
/*************************************************************************************************
 * 							    ExportDocumentToWebAISimpleView - EXEC
 **************************************************************************************************
 Date : 03.07.2019
 Auteur : MTO

 Description : Export document vers Web@AI
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - START");

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueDocumentInitializer result = null;
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
String errorDocuments = null;
String warnDocuments = null;
FileInputStream file = null;
DocumentBuilderFactory builderFactory = null;
DocumentBuilder builder = null;
Document xmlDocument = null;
List<com.digitech.jcorbairs.Document> listDocumentsExport = new ArrayList();
int successExport = 0;

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();

    // Chargement du XML Configuration
    file = new FileInputStream(new File(Constants.XML_ACTIONS_CONFIGURATION_PATH));
    builderFactory = DocumentBuilderFactory.newInstance();
    builder = builderFactory.newDocumentBuilder();
    xmlDocument = builder.parse(file);

    result = output.getValue();
    result.setMessageSummary(BundleUtils.getTranslation("groovy_export_webai_action"));

} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
    scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR : ", e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    if (data.get("DATA_ERROR_MSG") != null || data.get("DATA_WARN_MSG") != null) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END");
        return
    }

    boolean isExportFolder = Methods.isContentExist(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_IS_EXPORT_FOLDER_BY_ID.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER"))));
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - FILTRE ID " + String.valueOf(data.get("DATA_FILTER") + " / " + Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_BY_ID_CODE.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER")))));
    List<String> filters = Methods.getContentsListWithFormat(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_BY_ID_CODE.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER"))), "%05d");
    String filterField = Methods.getContent(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_FILTERS_FIELD);
    String folder = data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_DEPOSIT_FOLDER_PATH").toString();
    File folderFile = new File(folder);
    if (!folderFile.exists()) folderFile.mkdirs();
    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - PATH XML : " + data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH").toString());
    File xml = new File(data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_XML_FILE_PATH").toString());
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS);
    Date beginDate = (Date) data.get("DATA_BEGIN_DATE");
    Date endDate = (Date) data.get("DATA_END_DATE");
    if (isExportFolder && !filters.isEmpty()) {
        scriptLogger.debug(String.valueOf(data.get("DATA_NSS")) + " " + filterField + " " + filters + " " + simpleDateFormat.format(beginDate) + " " + simpleDateFormat.format(endDate));
        //listDocumentsExport = Methods.getDocumentsListByNSS(Constants.CTY_DOCUMENT_ASSURE, String.valueOf(data.get("DATA_NSS")), filterField, filters, simpleDateFormat.format(data.get("DATA_BEGIN_DATE")), simpleDateFormat.format(data.get("DATA_END_DATE")), false, userContext);
        listDocumentsExport = Methods.getDocumentsListByNSS(Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments(), filterField, filters, (Date)data.get("DATA_BEGIN_DATE"), (Date) data.get("DATA_END_DATE"), false);
    } else if (isExportFolder && filters.isEmpty()) {
        scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - DEBUG : DOSSIER COMPLET");
        docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getAllDocuments();
        for (IDocument document : docs) {
            Date date = (Date) document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue();
            if ((date.after(beginDate) && date.before(endDate)) || (date.compareTo(beginDate) == 0) || (date.compareTo(endDate) == 0))
                listDocumentsExport.add(new com.digitech.jcorbairs.Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId()));
        }
    } else {
        docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
        for (IDocument document : docs) {
            Date date = (Date) document.getField(Constants.FIELD_DATE_DOCUMENT_CODE).getValue();
            if (filters.isEmpty()) {
                if ((date.after(beginDate) && date.before(endDate)) || (date.compareTo(beginDate) == 0) || (date.compareTo(endDate) == 0))
                    listDocumentsExport.add(new com.digitech.jcorbairs.Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId()));
            } else {
                for (String filter : filters) {
                    if (filter.equals(String.format("%05d", Integer.parseInt(String.valueOf(document.getField(filterField).getValue())))) && ((date.after(beginDate) && date.before(endDate)) || (date.compareTo(beginDate) == 0) || (date.compareTo(endDate) == 0)))
                        listDocumentsExport.add(new com.digitech.jcorbairs.Document(DossierCoreContext.getAdminJeton(), document.getAirsRefId()));
                }
            }
        }
    }

    scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - COUNT DOCUMENT : " + listDocumentsExport.size());

    if (!listDocumentsExport.isEmpty()) {

        ExportSedex exportSedex = new ExportSedex(data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_OFFICE"), data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_LOT_ID"),
                String.valueOf(data.get("DATA_NSS")).replaceAll("\\.", ""), data.get("DATA_INFORMATIONS_WEBAI").get("XML_WEBAI_REQUEST_EXPORT_WEBAI_UID"), folder, new SimpleDateFormat(Constants.DATE_FORMAT_SWISS).format(new Date()), new SimpleDateFormat(Constants.HOURS_FORMAT_SWISS).format(new Date()),
                Constants.SUPPLIER_NAME, Constants.GED_VERSION, Constants.BUILD_VERSION);

        List<Row> rows = new ArrayList<Row>();

        for (com.digitech.jcorbairs.Document document : listDocumentsExport) {
            try {
                if (document.getPrimaryDocList().size() > 0) {
                    File pj = document.getPrimaryDocument(document.getPrimaryDocList().get(0), folder);
                    Date documentDate = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).parse(document.getContent().getFieldValue(Constants.FIELD_DATE_DOCUMENT_CODE));
                    String documentDateOutput = new SimpleDateFormat(Constants.DATE_FORMAT_SWISS).format(documentDate);
                    AuthorityListTermAdmin authorityListTermAdmin = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Integer.parseInt(document.getContent().getFieldValue(Constants.LIST_TYPES_DOCUMENT_CODE)));
                    String documentTypeFrench = authorityListTermAdmin.getValue1();
                    String documentTypeGerman = null;
                    String documentTypeItalian = null;
                    if (documentTypeFrench.startsWith("al_")){
                        documentTypeFrench = authorityListTermAdmin.getValue2();
                        documentTypeItalian = authorityListTermAdmin.getValue2();
                        documentTypeGerman = authorityListTermAdmin.getValue3();
                    }else{
                        documentTypeGerman = documentTypeFrench;
                        documentTypeItalian = documentTypeFrench;
                    }
                    File export = new File(folder + File.separator + document.getId() + Constants.APPLICATION_PDF_EXTENSION.toLowerCase());

                    if (StringUtils.isExtensionIgnoreCase(pj.getName(), Constants.APPLICATION_PDF_EXTENSION)) {
                        com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(pj.getAbsolutePath());
                        com.aspose.pdf.License license = new com.aspose.pdf.License();
                        license.setLicense(Constants.APPLICATION_LICENCE_ASPOSE_PDF);

                        PdfFileInfo pfi = new PdfFileInfo(pj.getAbsolutePath());
                        if (Math.abs((com.aspose.pdf.PageSize.getA4().getHeight() - pfi.getPageHeight(1))) > 50 || (Math.abs((com.aspose.pdf.PageSize.getA4().getWidth() - pfi.getPageWidth(1))) > 50)) {
                            // Resize contents of resultant PDF
                            int[] page_cnt1 = new int[pdfDocument.getPages().size()];
                            for (int i = 0; i < pdfDocument.getPages().size(); i++) {
                                page_cnt1[i] = i + 1;
                            }
                            PdfFileEditor pfe = new PdfFileEditor();
                            pfe.resizeContents(pdfDocument, page_cnt1,  IPdfFileEditor.ContentsResizeParameters.pageResize(com.aspose.pdf.PageSize.getA4().getWidth(), com.aspose.pdf.PageSize.getA4().getHeight()));
                            // Save output as PDF format
                            String path = pj.getAbsolutePath();
                            if(pj.delete()) pdfDocument.save(export.getAbsolutePath());
                            pfi.close();
                        }else{
							if(pj.delete()) pdfDocument.save(export.getAbsolutePath());
                            pfi.close();
                            //pj.renameTo(export);
                        }
                        rows.add(new Row(FilenameUtils.removeExtension(export.getName()), Constants.APPLICATION_PDF_EXTENSION.toLowerCase().replaceAll("\\.",""), Constants.APPLICATION_PDF_EXTENSION.toLowerCase().replaceAll("\\.",""),
                                documentDateOutput, org.apache.commons.lang.StringUtils.leftPad(document.getContent().getFieldValue(Constants.LIST_TYPES_DOCUMENT_CODE), Constants.ID_TYPE_DOCUMENT_EXPORT_COUNT_DIGIT, "0").trim(), documentTypeFrench,
                                documentTypeItalian, documentTypeGerman));
                        successExport++;
                    } else if (StringUtils.isExtensionIgnoreCase(pj.getName(), Constants.APPLICATION_TIF_EXTENSION)) {
                        new TIFFOperationService().convert(pj, export, Collections.EMPTY_MAP);
                        rows.add(new Row(FilenameUtils.removeExtension(export.getName()), Constants.APPLICATION_PDF_EXTENSION.toLowerCase().replaceAll("\\.",""), Constants.APPLICATION_PDF_EXTENSION.toLowerCase().replaceAll("\\.",""),
                                documentDateOutput, org.apache.commons.lang.StringUtils.leftPad(document.getContent().getFieldValue(Constants.LIST_TYPES_DOCUMENT_CODE), Constants.ID_TYPE_DOCUMENT_EXPORT_COUNT_DIGIT, "0").trim(), documentTypeFrench,
                                documentTypeItalian, documentTypeGerman));
                        successExport++;
                    } else if (StringUtils.isExtensionIgnoreCase(pj.getName(), Constants.APPLICATION_OFFICE_WORD_EXTENSION) &&
                            Methods.isContentExist(xmlDocument, Constants.XML_ACTIONS_REQUEST_EXPORT_WEBAI_WITH_EXPORT_NOT_PDF.replaceAll("##ID##", String.valueOf(data.get("DATA_FILTER"))))) {
                        Methods.getDocumentConversionService().convert(pj, export);
                        rows.add(new Row(FilenameUtils.removeExtension(export.getName()), Constants.APPLICATION_PDF_EXTENSION.toLowerCase().replaceAll("\\.",""), Constants.APPLICATION_PDF_EXTENSION.toLowerCase().replaceAll("\\.",""),
                                documentDateOutput, org.apache.commons.lang.StringUtils.leftPad(document.getContent().getFieldValue(Constants.LIST_TYPES_DOCUMENT_CODE), Constants.ID_TYPE_DOCUMENT_EXPORT_COUNT_DIGIT, "0").trim(), documentTypeFrench,
                                documentTypeItalian, documentTypeGerman));
                        successExport++;
                    } else {
                        if (warnDocuments == null) warnDocuments = document.getId().toString() + " (" + BundleUtils.getTranslation("groovy_format_file_invalid") + ")";
                        else warnDocuments += ", " + document.getId().toString() + " (" + BundleUtils.getTranslation("groovy_format_file_invalid") + ")";
                    }
                } else {
                    if (errorDocuments == null) errorDocuments = document.getId().toString() + " (" + BundleUtils.getTranslation("groovy_empty_file") + ")";
                    else errorDocuments += ", " + document.getId().toString() + " (" + BundleUtils.getTranslation("groovy_empty_file") + ")";
                }
            } catch (Exception e) {
                if (errorDocuments == null) errorDocuments = document.getId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")";
                else errorDocuments += ", " + document.getId().toString() + " (" + BundleUtils.getTranslation("groovy_error_document") + ")";
                scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR - Document n" + document.getId() + " : ", e);
            }
        }

        // Génération du XML de sortie
        if(!rows.isEmpty()){
            exportSedex.setRow(rows);

            JAXBContext context = JAXBContext.newInstance(ExportSedex.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("jaxb.formatted.output",Boolean.TRUE);
            marshaller.marshal(exportSedex, xml);
        }

        if (errorDocuments == null && warnDocuments == null && xml.exists()) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.INFO);
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success") + " - " + listDocumentsExport.size() + " " + BundleUtils.getTranslation("groovy_export_webai_export_documents"));
        } else if(warnDocuments != null && xml.exists()) {
            result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_success") + " - " +listDocumentsExport.size() + " " + BundleUtils.getTranslation("groovy_export_webai_export_documents")+" - "+warnDocuments);
        } else {
            result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
            result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_warn") + errorDocuments);
        }
    } else {
        result.setMessageSeverity(IScriptResultValueModel.Severity.WARN);
        result.setMessageDetail(BundleUtils.getTranslation("groovy_list_file"));
    }

} catch (Exception e) {
    result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
    result.setMessageDetail(BundleUtils.getTranslation("groovy_traitment_exec_error"));
    scriptLogger.error("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ERREUR : ", e);
    return;
} finally{
    if(file != null) {
        try{
            file.close();
        }catch(Exception e){
            scriptLogger.warn("[CUSTOM ACTION] - ExportDocumentsToWebAISimpleViewExec - ATTENTION - Fichier XML de configuration non cloturé : ",e);
        }
    }
}

scriptLogger.debug("[CUSTOM ACTION] - EXPORT WEB@AI SIMPLE VIEW EXEC - END");

@XmlAccessorType( XmlAccessType.NONE )
@XmlRootElement (name = "root")
class ExportSedex {
    @XmlElement
    String env_ged;
    @XmlElement
    String id_lot_sedex;
    @XmlElement
    String nss;
    @XmlElement
    String uid;
    @XmlElement
    String folder;
    @XmlElement
    String date_export;
    @XmlElement
    String hours_export;
    @XmlElement
    String supplier;
    @XmlElement
    String ecm_version;
    @XmlElement
    String ecm_build;
    @XmlElement
    List<Row> row;

    ExportSedex() {
    }

    ExportSedex(String env_ged, String id_lot_sedex, String nss, String uid, String folder, String date_export, String hours_export, String supplier, String ecm_version, String ecm_build) {
        this.env_ged = env_ged
        this.id_lot_sedex = id_lot_sedex
        this.nss = nss
        this.uid = uid
        this.folder = folder
        this.date_export = date_export
        this.hours_export = hours_export
        this.supplier = supplier
        this.ecm_version = ecm_version
        this.ecm_build = ecm_build
    }

    void setRow(List<Row> row) {
        this.row = row
    }
}

@XmlAccessorType( XmlAccessType.NONE )
@XmlRootElement
class Row{
    @XmlElement
    String filename;
    @XmlElement
    String original_extension;
    @XmlElement
    String current_extension;
    @XmlElement
    String date_document;
    @XmlElement
    String id_type_doc;
    @XmlElement
    String label_fr;
    @XmlElement
    String label_it;
    @XmlElement
    String label_de;

    Row() {
    }

    Row(String filename, String original_extension, String current_extension, String date_document, String id_type_doc, String label_fr, String label_it, String label_de) {
        this.filename = filename
        this.original_extension = original_extension
        this.current_extension = current_extension
        this.date_document = date_document
        this.id_type_doc = id_type_doc
        this.label_fr = label_fr
        this.label_it = label_it
        this.label_de = label_de
    }
}
