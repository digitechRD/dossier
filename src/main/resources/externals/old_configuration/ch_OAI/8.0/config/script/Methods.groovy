import Constants
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.service.IAuditService
import com.digitech.dossier.common.service.IRight
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.ged.common.dal.exception.DocumentException
import com.digitech.jcorbairs.*
import com.digitech.jcorbairs.admin.*
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.report.service.IDocumentConvertionService
import com.digitech.report.service.impl.ooo.DocumentConvertionService
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.RandomAccessFileOrArray
import com.itextpdf.text.pdf.codec.TiffImage
import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.faces.model.SelectItem
import javax.swing.text.MaskFormatter
import javax.xml.bind.DatatypeConverter
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import java.nio.charset.Charset
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.SimpleDateFormat

/*************************************************************************************************
 *   					    			    METHODES
 **************************************************************************************************
 Date : 08.05.2016
 Auteur : MTO
 Version : 2.0.1

 Description : Regroupe l'ensemble des méthodes utilisées dans les différents groovy

 **************************************************************************************************/

class Methods {


    static String getAirsRequestForUser(String tmpRequest, UserCoreContext userContext) {
        for (FieldAdmin fa : FieldsManager.loadAll(userContext.getInstance().getJeton())) {
            if (tmpRequest != null && tmpRequest.toUpperCase().contains(fa.getCode())) {
                if (fa.getFieldType().toString().equals("R")) {
                    if (!fa.getCode().startsWith("U_")) {
                        String critereTmp = tmpRequest.substring(tmpRequest.indexOf(fa.getCode() + ".ROOTITEM"), tmpRequest.length())
                        String critereComplet = critereTmp.substring(0, critereTmp.indexOf("\"", critereTmp.indexOf("\"") + 1) + 1)
                        String id = critereComplet.substring(critereComplet.indexOf("\"") + 1, critereComplet.lastIndexOf("\""))
                        AuthorityListTermAdmin term = AuthorityListsManager.loadTerm(userContext.getInstance().getJeton(), Integer.parseInt(id))
                        String resTmp = critereComplet.replace(fa.code + ".ROOTITEM", fa.getDescription())
                        resTmp = resTmp.replace(id, term.getValue2())
                        tmpRequest = tmpRequest.replace(critereComplet, resTmp)
                    }else if (tmpRequest.toUpperCase().contains(fa.getCode()+".USR")) {
                        String critereTmp = tmpRequest.substring(tmpRequest.indexOf(fa.getCode() + ".USR"), tmpRequest.length())
                        String critereComplet = critereTmp.substring(0, critereTmp.indexOf("\"", critereTmp.indexOf("\"") + 1) + 1)
                        String id = critereComplet.substring(critereComplet.indexOf("\"") + 1, critereComplet.lastIndexOf("\""))
                        UserAdmin user = UsersManager.load(userContext.getInstance().getJeton(), Integer.parseInt(id))
                        String resTmp = critereComplet.replace(fa.code + ".USR", fa.getDescription())
                        resTmp = resTmp.replace(id, user.getLogin())
                        tmpRequest = tmpRequest.replace(critereComplet, resTmp)
                    }

                }else{
                    if(fa.getCode().equals(Constants.FIELD_NSS_CODE)) {
                        String critereTmp = tmpRequest.substring(tmpRequest.indexOf(fa.getCode()), tmpRequest.length())
                        String critereComplet = critereTmp.substring(0, critereTmp.indexOf("\"", critereTmp.indexOf("\"") + 1) + 1)
                        String nss = critereComplet.substring(critereComplet.indexOf("\"") + 1, critereComplet.lastIndexOf("\""))
                        String resTmp = critereComplet.replace(fa.code, fa.getDescription())
                        //resTmp= resTmp.replace(nss,Methods.formatString(nss, Constants.NSS_MASK));
                        tmpRequest = tmpRequest.replace(critereComplet, resTmp)
                    }else{
                        tmpRequest = tmpRequest.replace(fa.getCode(), fa.getDescription())
                    }

                }
            }
        }

        return tmpRequest
    }


    /***
     * Définit une valeur à un index d'un document
     *
     * @param doc
     * @param index
     * @param value
     * @throws Exception
     */
    static void defineDocumentIndex(Document doc, String index, String value) throws Exception {
        try {
            doc.getContent().modifyFieldValue(index, value)
        } catch (Exception e) {
            try {
                doc.getContent().addFieldValue(index, value)
            } catch (Exception ex) {
                throw new Exception("Exception de la definition du champ : " + index + " avec la valeur" + value + " : ", ex)
            }
        }
    }

    /***
     * Retourne un IDocument
     *
     *
     * @return
     */
    static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
        return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
    }

    /***
     * Retourne un objet IAuditService
     *
     *
     * @return
     */
    static IAuditService getAuditMgr() {
        return (IAuditService) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR)
    }
    /***
     *
     *
     * @return
     */
    static IUser getUserMgr() {
        return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR)
    }

    /***
     *
     *
     * @return
     */
    static IRight getRightMgr() {
        return (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR)
    }


    /***
     * Ajout un message dans l'objet data en fonction de son type
     * Le Mesaage peut être ajouté ou écrasé
     *
     * @param data
     * @param key
     * @param message
     * @param overwrite
     */
    static void addStateMessage(Map<String, Object> data, String key, String message, Boolean overwrite) {
        String previousMsg = data.get(key)
        if (overwrite || previousMsg == null) {
            data.put(key, message)
        } else {
            data.put(key, previousMsg + "<br/>" + message)
        }
    }

    /***
     * Retourne le père (dossier) du document courant
     *
     * @param document
     * @param contentType
     * @param field
     * @param value
     * @param SecretLevel
     * @return
     * @throws Exception
     */
    static Document getDossier(IDocument document, String contentType, String field, String value, Integer SecretLevel) throws Exception {
        Document dossier = null
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType))

        Request req = new Request()
        req.addLocution(field, Request.Operator.OPERATOR_EQUAL, value)

        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain)
        int count = search.getNbResults()
        if (count >= 1) {
            for (int i = 0; i < count; i++) {
                if (search.getDocumentByIndex(i).getSecretLevel() == SecretLevel) dossier = search.getDocumentByIndex(i)
                else {
                    //document.deleteDocument(DossierCoreContext., search.getDocumentByIndex(i));
                    getDocumentMgr().deleteDocument(new UserCoreContext(DossierCoreContext.getAdminJeton()), search.getDocumentByIndex(i).getId())
                    return getDossier(document, contentType, field, value, SecretLevel)
                }
            }
        } else {
            dossier = new Document(DossierCoreContext.getAdminJeton(), (Domain) listDomain.get(0), SecretLevel)
            defineDocumentIndex(dossier, field, value)
            dossier.updateContent()
        }
        return dossier
    }
    /***
     * Retourne une liste de Document correspondant à un NSS entre des dates données
     *
     * @param contentType
     * @param nss
     * @param field
     * @param values
     * @param beginDate
     * @param endDate
     * @param isAdmin
     * @return
     * @throws Exception
     */
    static List<Document> getDocumentsListByNSS(String contentType, String nss, String field, List<String> values, String beginDate, String endDate, boolean isAdmin, UserCoreContext userCoreContext) throws Exception {
        List<Document> result = new ArrayList<Document>()
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(userCoreContext.getJeton(), contentType))

        Request req = new Request()
        req.addLocution(Constants.FIELD_NSS_CODE, Request.Operator.OPERATOR_EQUAL, nss)
        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND)
        req.addLocution(Constants.FIELD_DATE_DOCUMENT_CODE + "[\"" + beginDate + "\", \"" + endDate + "\"]")
        if (!isAdmin && !values.isEmpty()) {
            boolean isRequestCreated = true
            for (int i = 0; i < values.size(); i++) {
                if (!"".equals(values.get(i))) {
                    if (!isRequestCreated) {
                        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_OR)
                        req.addLocution((field.startsWith("AL_")) ? field + ".ROOTITEM" : field, Request.Operator.OPERATOR_EQUAL, values.get(i))
                        if (i == values.size() - 1) req.addLocution(")")
                    } else {
                        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND)
                        req.addLocution("(")
                        req.addLocution((field.startsWith("AL_")) ? field + ".ROOTITEM" : field, Request.Operator.OPERATOR_EQUAL, values.get(i))
                        if (i == values.size() - 1) req.addLocution(")")
                    }
                    isRequestCreated = false
                }
            }
        }

        Search search = new Search(userCoreContext.getJeton(), req, listDomain)
        List<Integer> listDocumentsId = search.getResultIds()
        for (Integer id : listDocumentsId) {
            result.add(new Document(DossierCoreContext.getAdminJeton(), id))
        }

        return result
    }

    static List<Document> getDocumentsListByNSS(List<IDocument> documentList, String field, List<String> values, Date beginDate, Date endDate, boolean isAdmin) throws Exception {
        List<Document> result = new ArrayList<Document>()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS)
        for (IDocument doc : documentList) {
            Document document = new Document(DossierCoreContext.getAdminJeton(), doc.getAirsRefId())
            if (!isAdmin && (values == null || values.contains(String.format("%05d", Integer.valueOf(Methods.getFieldValue(document, field)))))
                    && simpleDateFormat.parse(Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE)).compareTo(beginDate) >= 0
                    && simpleDateFormat.parse(Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE)).compareTo(endDate) <= 0) {
                result.add(document)
            } else if (isAdmin) {
                result.add(document)
            }
        }
        return result
    }

    static List<Integer> getDocumentsListIdByNSS(List<IDocument> documentList, Map<String, String> filters, Date beginDate, Date endDate, boolean isAdmin, org.w3c.dom.Document xmlDocument, String idFilter, String xPathRequest) throws Exception {
        List<Integer> result = new ArrayList<Integer>()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS)
        for (IDocument doc : documentList) {
            boolean toAdd = true
            Document document = new Document(DossierCoreContext.getAdminJeton(), doc.getAirsRefId())
            if (!isAdmin && simpleDateFormat.parse(Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE)).compareTo(beginDate) >= 0
                    && simpleDateFormat.parse(Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE)).compareTo(endDate) <= 0) {
                if (filters == null) {
                    result.add(doc.getAirsRefId())
                } else {
                    Set cles = filters.keySet()
                    Iterator it = cles.iterator()
                    while (it.hasNext()) {

                        String field = (String) it.next()
                        boolean hasExcluded = ("0".equals(getContent(xmlDocument, xPathRequest.replace("##REPLACE_VALUE##", field).replace("##ID##", idFilter).toString())) ? true : false)
                        String values = (List<String>) filters.get(field)
                        if (toAdd && ((!hasExcluded && !values.contains(String.format("%05d", Integer.valueOf(Methods.getFieldValue(document, field))))) || (hasExcluded && values.contains(String.format("%05d", Integer.valueOf(Methods.getFieldValue(document, field))))))) {
                            toAdd = false
                        }
                    }
                    if (toAdd) {
                        result.add(doc.getAirsRefId())
                    }
                }
            } else if (isAdmin) {
                result.add(doc.getAirsRefId())
            }
        }
        return result
    }


    static List<Integer> getDocumentsListIdByNSS(List<IDocument> documentList, String field, List<String> values, Date beginDate, Date endDate, boolean isAdmin) throws Exception {
        List<Integer> result = new ArrayList<Integer>()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS)
        for (IDocument doc : documentList) {
            Document document = new Document(DossierCoreContext.getAdminJeton(), doc.getAirsRefId())
            if (!isAdmin && (values == null || values.contains(String.format("%05d", Integer.valueOf(Methods.getFieldValue(document, field)))))
                    && simpleDateFormat.parse(Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE)).compareTo(beginDate) >= 0
                    && simpleDateFormat.parse(Methods.getFieldValue(document, Constants.FIELD_DATE_DOCUMENT_CODE)).compareTo(endDate) <= 0) {
                result.add(doc.getAirsRefId())
            } else if (isAdmin) {
                result.add(doc.getAirsRefId())
            }
        }
        return result
    }

    static String getGroupeByTypeDoc(String typeId) throws Exception {
        String groupId = null
        if (typeId != null) {
            AuthorityListTermAdmin altm = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Integer.parseInt(typeId))
            String groupCode = altm.getValue5().replaceAll(";", "")

            List<AuthorityListTermAdmin> listGroup = AuthorityListsManager.loadTerms(DossierCoreContext.getAdminJeton(), Constants.LIST_GROUPES_DOCUMENT_ID)
            for (AuthorityListTermAdmin alta : listGroup) {
                if (groupCode.equals(alta.getCode())) {
                    groupId = String.valueOf(alta.getId())
                    break
                }
            }
        }
        return groupId


    }

    /***
     * Retourne une liste d'id de document correspondant à un NSS entre des dates données
     *
     * @param contentType
     * @param nss
     * @param field
     * @param values
     * @param beginDate
     * @param endDate
     * @param isAdmin
     * @return
     * @throws Exception
     */
    static List<Integer> getDocumentsListIdByNSS(String contentType, String nss, String field, List<String> values, String beginDate, String endDate, boolean isAdmin) throws Exception {
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType))

        Request req = new Request()
        req.addLocution(Constants.FIELD_NSS_CODE, Request.Operator.OPERATOR_EQUAL, nss)
        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND)
        req.addLocution(Constants.FIELD_DATE_DOCUMENT_CODE + "[\"" + beginDate + "\", \"" + endDate + "\"]")
        if (!isAdmin && values != null) {
            boolean isRequestCreated = true
            for (int i = 0; i < values.size(); i++) {
                if (!"".equals(values.get(i))) {
                    if (!isRequestCreated) {
                        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_OR)
                        req.addLocution((field.startsWith("AL_")) ? field + ".ROOTITEM" : field, Request.Operator.OPERATOR_EQUAL, values.get(i))
                        if (i == values.size() - 1) req.addLocution(")")
                    } else {
                        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND)
                        req.addLocution("(")
                        req.addLocution((field.startsWith("AL_")) ? field + ".ROOTITEM" : field, Request.Operator.OPERATOR_EQUAL, values.get(i))
                        if (i == values.size() - 1) req.addLocution(")")
                    }
                    isRequestCreated = false
                }
            }
        }

        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain)
        return search.getResultIds()
    }

    /***
     * Retourne une liste d'id de document correspondant à un NSS entre des dates données
     *
     * @param contentType
     * @param nss
     * @param field
     * @param values
     * @param beginDate
     * @param endDate
     * @param isAdmin
     * @return
     * @throws Exception
     */
    static List<Integer> getDocumentsListIdByNSS(String contentType, String nss, String field, List<String> values, String beginDate, String endDate, boolean isAdmin, UserCoreContext userContext) throws Exception {
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(userContext.getJeton(), contentType))

        Request req = new Request()
        req.addLocution(Constants.FIELD_NSS_CODE, Request.Operator.OPERATOR_EQUAL, nss)
        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND)
        req.addLocution(Constants.FIELD_DATE_DOCUMENT_CODE + "[\"" + beginDate + "\", \"" + endDate + "\"]")
        if (!isAdmin && values != null) {
            boolean isRequestCreated = true
            for (int i = 0; i < values.size(); i++) {
                if (!"".equals(values.get(i))) {
                    if (!isRequestCreated) {
                        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_OR)
                        req.addLocution(field, Request.Operator.OPERATOR_EQUAL, values.get(i))
                        if (i == values.size() - 1) req.addLocution(")")
                    } else {
                        req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND)
                        req.addLocution("(")
                        req.addLocution(field, Request.Operator.OPERATOR_EQUAL, values.get(i))
                        if (i == values.size() - 1) req.addLocution(")")
                    }
                    isRequestCreated = false
                }
            }
        }

        Search search = new Search(userContext.getJeton(), req, listDomain)
        return search.getResultIds()
    }

    /***
     * Retourne une liste d'id de document correspondant à un NSS
     *
     * @param contentType
     * @param nss
     * @return
     * @throws Exception
     */
    static List<Integer> getDocumentsListIdByNSS(String contentType, String nss) throws Exception {
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType))
        Request req = new Request()
        req.addLocution(Constants.FIELD_NSS_CODE, Request.Operator.OPERATOR_EQUAL, nss)
        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain)
        return search.getResultIds()
    }

    /***
     * Retourne une liste d'id de document correspondant à un NSS créé un jour donnée
     *
     * @param contentType
     * @param nss
     * @param date
     * @return
     * @throws Exception
     */
    static List<Integer> getDocumentsListIdByDay(String contentType, Date date, boolean isDeleted) throws Exception {
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), contentType))
        Request req = new Request()
        req.addLocution(Constants.FIELD_DATE_CREATION_CODE, Request.Operator.OPERATOR_EQUAL, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(date))
        if (!isDeleted) {
            Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain)
            return search.getResultIds()
        } else {
            Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain, Search.ResultSearchLevel.DELETED_DOCUMENTS)
            return search.getResultIds()
        }
    }

    /***
     * Retourne une liste d'id de dossier à supprimer car la date de rétention est atteinte
     *
     * @param date
     * @return
     * @throws Exception
     */
    static List<Integer> getFoldersHasRemove(Date date) throws Exception {
        List<Domain> listDomain = new ArrayList()
        listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), Constants.CTY_FOLDER_ASSURE))
        Request req = new Request()
        req.addLocution(Constants.FIELD_DATE_RETENTION_CODE, Request.Operator.OPERATOR_INF, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(date))
        Search search = new Search(DossierCoreContext.getAdminJeton(), req, listDomain)
        return search.getResultIds()
    }

    /***
     * Suprimer un fichier
     * @param folder
     */
    static void deleteFile(File folder) {
        if (folder.isDirectory()) {
            for (File f : folder.listFiles()) {
                deleteFile(f)
            }
        } else {
            folder.delete()
        }
    }
    /***
     * Telechargement d'une pièce jointe d'un document a l'endroit précisé
     *
     * @param contentType
     * @param nss
     * @param field
     * @param values
     * @param beginDate
     * @param endDate
     * @param isAdmin
     * @return
     */
    static String downloadAttachment(String tempFilePath, IDocument document, IAttachment attachment)
            throws ServerException, IdentificationException, DocumentException, IOException {
        String strPathClient = tempFilePath
        document.getAirsDocument().getInnerDocument().getPrimaryDocument(attachment.getAirsAttachment(), strPathClient)
        File originalFile = new File(strPathClient + File.separator + attachment.getFileName())
        File uniqueFile = new File(strPathClient + File.separator + attachment.getId() + attachment.getFileName())
        originalFile.renameTo(uniqueFile)
        return strPathClient + File.separator + attachment.getId() + attachment.getFileName()

    }

    static void writeHeader(PdfPTable table, String noDoc, String ndem, String date, String typeDoc, String author, String nbPage, boolean borderBottom) throws Exception {
        // Max 30 caractères pour le type de document + auteur
        if (typeDoc.length() > 30)
            typeDoc = typeDoc.substring(0, 29).concat(".")

        if (author.length() > 30)
            author = author.substring(0, 29).concat(".")

        PdfPCell numDoc = new PdfPCell(new Phrase(noDoc, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell dossiers = new PdfPCell(new Phrase(ndem, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell dateExport = new PdfPCell(new Phrase(date, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell docType = new PdfPCell(new Phrase(typeDoc, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell auteur = new PdfPCell(new Phrase(author, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell nbrePage = new PdfPCell(new Phrase(nbPage, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))

        if (borderBottom) {
            numDoc.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            numDoc.setBorder(Rectangle.BOTTOM)
            numDoc.setBorder(Rectangle.TOP)
            dossiers.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dossiers.setBorder(Rectangle.BOTTOM)
            dossiers.setBorder(Rectangle.TOP)
            dateExport.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dateExport.setBorder(Rectangle.BOTTOM)
            dateExport.setBorder(Rectangle.TOP)
            docType.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            docType.setBorder(Rectangle.BOTTOM)
            docType.setBorder(Rectangle.TOP)
            auteur.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            auteur.setBorder(Rectangle.BOTTOM)
            auteur.setBorder(Rectangle.TOP)
            nbrePage.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            nbrePage.setBorder(Rectangle.BOTTOM)
            nbrePage.setBorder(Rectangle.TOP)
        }else {
            numDoc.setBorder(Rectangle.NO_BORDER)
            numDoc.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT)
            dossiers.setBorder(Rectangle.NO_BORDER)
            dossiers.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT)
            dateExport.setBorder(Rectangle.NO_BORDER)
            dateExport.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            docType.setBorder(Rectangle.NO_BORDER)
            docType.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            auteur.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            auteur.setBorder(Rectangle.NO_BORDER)
            nbrePage.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT)
            nbrePage.setBorder(Rectangle.NO_BORDER)
        }

        table.addCell(numDoc)
        table.addCell(dossiers)
        table.addCell(dateExport)
        table.addCell(docType)
        table.addCell(auteur)
        table.addCell(nbrePage)

    }

    static void writeHeader(PdfPTable table, String noDoc, String ndem, String date, String typeDoc, String author, String nbPage, String dateEmission, boolean borderBottom) throws Exception {

        // Max 30 caractères pour le type de document
        if (typeDoc.length() > 30)
            typeDoc = typeDoc.substring(0, 29).concat(".")

        if (author.length() > 30)
            author = author.substring(0, 29).concat(".")

        PdfPCell numDoc = new PdfPCell(new Phrase(noDoc, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell dossiers = new PdfPCell(new Phrase(ndem, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell dateExport = new PdfPCell(new Phrase(date, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell docType = new PdfPCell(new Phrase(typeDoc, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell auteur = new PdfPCell(new Phrase(author, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))
        PdfPCell nbrePage = new PdfPCell(new Phrase(nbPage, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))

        PdfPCell dateEmissions = new PdfPCell(new Phrase(dateEmission, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)))

        if (borderBottom) {
            numDoc.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            numDoc.setBorder(Rectangle.BOTTOM)
            numDoc.setBorder(Rectangle.TOP)
            dossiers.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dossiers.setBorder(Rectangle.BOTTOM)
            dossiers.setBorder(Rectangle.TOP)
            dateExport.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dateExport.setBorder(Rectangle.BOTTOM)
            dateExport.setBorder(Rectangle.TOP)
            docType.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            docType.setBorder(Rectangle.BOTTOM)
            docType.setBorder(Rectangle.TOP)
            auteur.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            auteur.setBorder(Rectangle.BOTTOM)
            auteur.setBorder(Rectangle.TOP)
            nbrePage.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            nbrePage.setBorder(Rectangle.BOTTOM)
            nbrePage.setBorder(Rectangle.TOP)
            dateEmissions.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dateEmissions.setBorder(Rectangle.BOTTOM)
            dateEmissions.setBorder(Rectangle.TOP)
        }else {
            numDoc.setBorder(Rectangle.NO_BORDER)
            numDoc.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dossiers.setBorder(Rectangle.NO_BORDER)
            dossiers.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT)
            dateExport.setBorder(Rectangle.NO_BORDER)
            dateExport.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            docType.setBorder(Rectangle.NO_BORDER)
            docType.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            auteur.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            auteur.setBorder(Rectangle.NO_BORDER)
            nbrePage.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT)
            nbrePage.setBorder(Rectangle.NO_BORDER)
            dateEmissions.setHorizontalAlignment(PdfPCell.ALIGN_CENTER)
            dateEmissions.setBorder(Rectangle.NO_BORDER)
        }

        table.addCell(numDoc)
        table.addCell(dossiers)
        table.addCell(dateExport)
        table.addCell(docType)
        table.addCell(auteur)
        table.addCell(dateEmissions)
        table.addCell(nbrePage)


    }
    /***
     * Écris les informations reçu en paramètre dans le PdfContentByte reçu en paramètre
     *
     * @param cb
     * @param date
     * @param nss
     * @param type
     * @param page
     * @param numDocument
     * @param position
     */

    static void writeHeader(PdfContentByte cb, String date, String nss, String type, String page, String numDocument, float position) throws Exception {
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
        float y = 400f
        float scaleY = -15f
        float dateX = 50f
        float nssX = 120f
        float typeX = 160f
        float pageX = 450f
        float numeroX = 500f

        //setting titles
        //setting titles
        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, date, dateX, position, 0f)
        cb.endText()

        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, nss, nssX, position, 0f)
        cb.endText()

        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, type, typeX, position, 0f)
        cb.endText()

        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, page, pageX, position, 0f)
        cb.endText()

        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, numDocument, numeroX, position, 0f)
        cb.endText()
    }
    /***
     * Écris les informations reçu en paramètre dans le PdfContentByte reçu en paramètre
     *
     * @param cb
     * @param date
     * @param nss
     * @param type
     * @param ndem
     * @param type
     * @param page
     * @param numDocument
     * @param position
     */

    static void writeHeader(PdfContentByte cb, String date, String nss, String ndem, String type, String page, String numDocument, float position) throws Exception {
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
        float y = 400f
        float scaleY = -15f
        float pageX = 450f
        float numeroX = 500f
        List<Float> espaceColonne = new ArrayList()
        espaceColonne.add(50f)
        espaceColonne.add(110f)
        espaceColonne.add(190f)
        espaceColonne.add(250f)
        int cpt = 0
        //setting titles
        //setting titles
        if (date != null) {
            cb.beginText()
            cb.setFontAndSize(bf, 10)
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, date, espaceColonne.get(cpt), position, 0f)
            cb.endText()
            cpt = cpt + 1
        }

        if (nss != null) {
            cb.beginText()
            cb.setFontAndSize(bf, 10)
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, nss, espaceColonne.get(cpt), position, 0f)
            cb.endText()
            cpt = cpt + 1
        }
        if (ndem != null) {
            cb.beginText()
            cb.setFontAndSize(bf, 10)
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, ndem, espaceColonne.get(cpt), position, 0f)
            cb.endText()
            cpt = cpt + 1
        }
        if (type != null) {
            cb.beginText()
            cb.setFontAndSize(bf, 10)
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, type, espaceColonne.get(cpt), position, 0f)
            cb.endText()
            cpt = cpt + 1
        }

        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, page, pageX, position, 0f)
        cb.endText()

        cb.beginText()
        cb.setFontAndSize(bf, 10)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, numDocument, numeroX, position, 0f)
        cb.endText()
    }
    /***
     * Écris les informations reçu en paramètre dans le PdfContentByte reçu en paramètre
     * Utiliser pour l'export de documents
     *
     * @param file Représente le fichier à traiter
     * @param exporPath Répertoire ou sera stocké le document traité
     * @param numeroPage List<Integer> contenant la page où un nouveau document début
     */
    static void setPagingPage(String file, String exportPath, List<Integer> numeroPage, List<String> labelNumDossier, List<String> labelTypeDoc, List<String> labelDateDoc, Properties conf) throws Exception {
        try {

            int cptList = -1
            int numeroDocument = 0
            PdfReader reader = new PdfReader(file)
            String fileTmp = exportPath + "/fileTmp.pdf"
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(fileTmp))
            int nombrePage = reader.getNumberOfPages()
            for (int i = 1; i <= nombrePage; i++) {

                if (numeroPage.contains(i)) {
                    numeroDocument = numeroDocument + 1
                    cptList++
                }
                Phrase p = new Phrase(labelNumDossier.get(cptList) + " - " + labelDateDoc.get(cptList) + " - " + labelTypeDoc.get(cptList) + " - " + conf.get("xml_configuration_label_page") + " " + Integer.toString(i) + " / " + Integer.toString(nombrePage) + " - " + conf.get("xml_configuration_label_gravage_tb_numDoc") + " : " + numeroDocument, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10))
                float x = reader.getPageSize(i).getWidth() / 20
                float y = reader.getPageSize(i).getTop(20)
                ColumnText.showTextAligned(
                        stamper.getOverContent(i), com.lowagie.text.Element.ALIGN_LEFT,
                        p, x, y, 0)
            }

            stamper.close()
            File fileInput = new File(file)
            File fileOuput = new File(fileTmp)
            fileInput.delete()
            fileOuput.renameTo(fileInput)


        } catch (IOException ex) {
            throw new Exception("Ereur lors de la numérotation des pages", ex)
        } catch (DocumentException ex) {
            throw new Exception("Ereur lors de la numérotation des pages", ex)
        }
    }

    static void setPagingPage(String file, String exportPath, List<Integer> numeroPage) throws Exception {
        try {
            int numeroDocument = 1
            PdfReader reader = new PdfReader(file)
            String fileTmp = exportPath + "/fileTmp.pdf"
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(fileTmp)) // output PDF
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED) // set font
            int nombrePage = reader.getNumberOfPages()
            for (int i = 1; i <= nombrePage; i++) {
                PdfContentByte cb = stamper.getOverContent(i)

                if (numeroPage.contains(i)) {
                    cb.beginText()
                    cb.setFontAndSize(bf, 10)
                    //cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Integer.toString(numeroDocument), 550f, 820f, 0);
                    cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Integer.toString(numeroDocument), (float) (reader.getPageSize(i).getWidth() - 30), (float) (reader.getPageSize(i).getHeight() - 10 - 30), (float) 0)
                    cb.endText()
                    numeroDocument = numeroDocument + 1
                }

                cb.beginText()
                cb.setFontAndSize(bf, 9)
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER, BundleUtils.getTranslation("xml_configuration_label_page") + " " + Integer.toString(i) + " / " + Integer.toString(nombrePage), 275, 5, 0)
                cb.endText()
            }

            stamper.close()
            File fileInput = new File(file)
            File fileOuput = new File(fileTmp)
            fileInput.delete()
            fileOuput.renameTo(fileInput)


        } catch (IOException ex) {
            throw new Exception("Ereur lors de la numérotation des pages", ex)
        } catch (DocumentException ex) {
            throw new Exception("Ereur lors de la numérotation des pages", ex)
        }
    }
    /***
     * Permet de savoir si un utilisateur est associé au profil reçu en paramètre
     *
     * @param userId
     * @param profilId
     * @return
     * @throws Exception
     */
    static boolean isActorInProfil(int userId, int profilId) throws Exception {
        ProfilAdmin profil = ProfilsManager.load(DossierCoreContext.getAdminJeton(), profilId)
        return profil.getUserIds().contains(userId)
    }

    static boolean isActorInProfil(int userId, String profilCode) throws Exception {
        for (ProfilAdmin profilAdmin : ProfilsManager.loadAll(DossierCoreContext.getAdminJeton())) {
            if (profilAdmin.getCode().equalsIgnoreCase(profilCode)) return true
        }
        return false
    }

    /***
     * Permet de savoir si un utilisateur est associé au profil reçu en paramètre
     *
     * @param userId
     * @param profilId
     * @return
     * @throws Exception
     */
    static boolean isActorInProfil(int userId, List<String> profilsId) throws Exception {
        for (String profilId : profilsId) {
            ProfilAdmin profil = ProfilsManager.load(DossierCoreContext.getAdminJeton(), Integer.parseInt(profilId))
            if (profil.getUserIds().contains(userId)) {
                return true
            }
        }
        return false
    }

    /***
     * Converti un document office en PDF et l'ajout aux pièces jointes du document reçu en paramètre
     *
     * @param document
     * @param filleOffice
     * @throws Exception
     */
    static void convertAttachment(IDocument document, File fileOffice) throws Exception {
        String filePDFName = getPDFFileName(fileOffice.getName())
        File filePDF = new File(fileOffice.getParent() + "/" + filePDFName)
        getDocumentConversionService().convert(fileOffice, filePDF)
        PrimaryDocument primaryDoc = new PrimaryDocument(filePDF.getName(), "00-" + filePDF.getName())
        document.getAirsDocument().getInnerDocument().addOrUpdatePrimaryDocument(primaryDoc, filePDF.getParent())
        document.getAirsDocument().getInnerDocument().updateContent()
    }
    /***
     * Retourne le nom du fichier reçu en paramètre
     *
     * @param fileName
     * @return
     */
    static String getPDFFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length())
        fileName = fileName.replaceAll("\\(", "_").replaceAll("\\)", "_").replaceAll(" ", "_")
        return fileName.replaceAll(extension, Constants.APPLICATION_PDF_EXTENSION.toLowerCase())
    }

    static IDocumentConvertionService getDocumentConversionService() {
        IDocumentConvertionService docConversionService = new DocumentConvertionService()
        return docConversionService
    }
    /***
     * Retourne le contenu associé à l'id d'un item d'une liste d'autorité
     *
     * @param id
     * @param alId
     * @return
     * @throws IdentificationException
     * @throws ServerException
     */
    static String getAlTermValue(Integer id, Integer alId) throws IdentificationException, ServerException {
        String result = null
        List<AuthorityListTermAdmin> listValues = AuthorityListsManager.loadTerms(DossierCoreContext.getAdminJeton(), alId)
        for (AuthorityListTermAdmin a : listValues) {
            if (id == a.getId()) {
                result = a.getValue1()
                break
            }
        }
        return result
    }
    /***
     *
     *
     * @param document
     * @param fieldCode
     * @return
     * @throws Exception
     */
    static List<Object> getFieldMultiValue(IDocument document, String fieldCode) throws Exception {
        IField field = document.getField(fieldCode)
        if (field != null) {
            return field.getValues()
        } else {
            return new ArrayList()
        }
    }

    /***
     *
     *
     * @param document
     * @param fieldCode
     * @return
     * @throws Exception
     */
    static String getFieldValue(Document document, String fieldCode) throws Exception {
        try {
            return document.getContent().getFieldValue(fieldCode)
        } catch (Exception e) {
            return null
        }
    }
    /***
     *
     *
     * @param document
     * @param fieldCode
     * @param item
     * @return
     * @throws Exception
     * Option de suppression de l'élément validateur
     */
    static String addValueInFieldMultiValue(IDocument document, String fieldCode, Map<Integer, Boolean> items, String userId) throws Exception {
        String result = ""
        List<Object> list = getFieldMultiValue(document, fieldCode)
        if (list != null) {
            for (Map.Entry<String, String> entry : items.entrySet()) {
                if (!list.contains(entry.getKey()) && entry.getValue()) {
                    list.add(entry.getKey())
                }
            }
            for (Object i : list) {
                if (!String.valueOf(i).equals(userId) || (String.valueOf(i).equals(userId) && list.size() == 1)) result += String.valueOf(i) + ";"
            }
        } else {
            for (Map.Entry<String, String> entry : items.entrySet()) {
                if (entry.getValue()) result += String.valueOf(entry.getKey()) + ";"
            }
        }
        return (result.endsWith(";")) ? result.substring(0, result.length() - 1) : result
    }

    /***
     *
     *
     * @param document
     * @param fieldCode
     * @param item
     * @return
     * @throws Exception
     */
    static String addValueInFieldMultiValue(IDocument document, String fieldCode, Integer item) throws Exception {
        String result = ""
        List<Object> list = getFieldMultiValue(document, fieldCode)
        if (list != null) {
            if (!list.contains(item)) {
                list.add(item)
                for (Object i : list) {
                    result += String.valueOf(i) + ";"
                }
            } else {
                for (Object i : list) {
                    result += String.valueOf(i) + ";"
                }
            }
        } else result = String.valueOf(item)
        return result
    }

    /***
     *
     *
     * @param document
     * @param fieldCode
     * @param item
     * @return
     * @throws Exception
     */

    static String removeValueInFieldMultiValue(IDocument document, String fieldCode, Integer item) throws Exception {
        String result = ""
        List<Object> list = getFieldMultiValue(document, fieldCode)
        if (list != null && list.size() > 1 && list.contains(item)) {
            for (Object i : list) {
                if (Integer.valueOf(String.valueOf(i)) != item)
                    result += String.valueOf(i) + ";"
            }
            StringBuilder res = new StringBuilder(result)
            res.replace(result.lastIndexOf(";"), result.lastIndexOf(";") + 1, "")
            result = res.toString()
        }
        return result
    }
    /***
     * Retourne les utilisateurs pour une organisation donnée
     *
     * @param organizationId
     * @return
     * @throws Exception
     */
    static Map<Integer, String> getUsersByOrganizationMap(int organizationId) throws Exception {
        Map<Integer, String> result = new HashMap()
        OrganizationAdmin organizationUserAdmin = OrganizationsManager.load(DossierCoreContext.getAdminJeton(), organizationId)
        List<OrganizationUserAdmin> users = organizationUserAdmin.getUsers()
        for (OrganizationUserAdmin user : users) {
            try {
                // Id des Administrateurs
                if (user.getUser().getActive() && user.getUser().getId() != 1) {
                    if (user.getUser().getFirstName() == null || user.getUser().getFirstName().length() < 2)
                        result.put(user.getUser().getId(), user.getUser().getName())
                    else result.put(user.getUser().getId(), user.getUser().getName() + " " + user.getUser().getFirstName().substring(0, 1) + ".")
                }
            } catch (Exception e) {
                throw new Exception("Erreur à la création de la liste des utilisateurs par organisation : " + user.getUser().getName() + " - ", e)
            }
        }
        return result
    }

    /***
     * Retourne les utilisateurs pour une organisation donnée
     *
     * @param organizationId
     * @return
     * @throws Exception
     */
    static List<SelectItem> getUsersByOrganizationListOfSelectItem(int organizationId) throws Exception {
        List<SelectItem> result = new ArrayList<SelectItem>()
        OrganizationAdmin organizationUserAdmin = OrganizationsManager.load(DossierCoreContext.getAdminJeton(), organizationId)
        List<OrganizationUserAdmin> users = organizationUserAdmin.getUsers()
        result.add(new SelectItem(0, ""))
        for (OrganizationUserAdmin user : users) {
            try {
                // Id des Administrateurs
                if (user.getUser().getActive() && user.getUser().getId() != 1) {
                    if (user.getUser().getFirstName() == null || user.getUser().getFirstName().length() < 2)
                        result.add(new SelectItem(user.getUser().getId(), user.getUser().getName()))
                    else result.add(new SelectItem(user.getUser().getId(), user.getUser().getName() + " " + user.getUser().getFirstName().substring(0, 1) + "."))
                }
                Collections.sort(result, new CustomComparator())
            } catch (Exception e) {
                throw new Exception("Erreur à la création de la liste des utilisateurs par organisation : " + user.getUser().getName() + " - ", e)
            }
        }
        return result
    }

    static List<SelectItem> getUsersByProfilListOfSelectItem(int organizationId) throws Exception {
        List<SelectItem> result = new ArrayList<SelectItem>()

        ProfilAdmin profilUserAdmin = ProfilsManager.load(DossierCoreContext.getAdminJeton(), organizationId)
        List<UserAdmin> users = profilUserAdmin.getUsers()
        result.add(new SelectItem(0, ""))
        for (UserAdmin user : users) {
            try {
                // Id des Administrateurs
                if (user.getActive() && user.getId() != 1) {
                    if (user.getFirstName() == null || user.getFirstName().length() < 2)
                        result.add(new SelectItem(user.getId(), user.getName()))
                    else result.add(new SelectItem(user.getId(), user.getName() + " " + user.getFirstName().substring(0, 1) + "."))
                }
                Collections.sort(result, new CustomComparator())
            } catch (Exception e) {
                throw new Exception("Erreur à la création de la liste des utilisateurs par organisation : " + user.getName() + " - ", e)
            }
        }
        return result
    }
    /***
     * Retourne tous les items reçu de la liste d'autorité reçu en paramètre
     *
     * @param authorityListId
     * @return
     * @throws Exception
     */
    static List<SelectItem> getAuthorityListOfSelectItem(int authorityListId) throws Exception {
        List<SelectItem> result = new ArrayList<SelectItem>()
        List<AuthorityListTermAdmin> terms = AuthorityListTermAdmin.loadTermRoots(DossierCoreContext.getAdminJeton(), authorityListId)
        Collections.sort(terms, new CustomComparatorTerm())
        for (AuthorityListTermAdmin term : terms) {
            if (term.getActive()) {
                if (term.getValue1().startsWith("al_")) result.add(new SelectItem(term.getId().toString(), BundleUtils.getTranslation(term.getValue1())))
                else result.add(new SelectItem(term.getId().toString(), term.getValue1()))
                if (!term.loadChildren().isEmpty()) {
                    List<AuthorityListTermAdmin> childs = term.loadChildren()
                    Collections.sort(childs, new CustomComparatorTerm())
                    for (AuthorityListTermAdmin child : childs) {
                        if (child.getActive()) {
                            if (child.getValue1().startsWith("al_")) result.add(new SelectItem(child.getId().toString(), "-- " + BundleUtils.getTranslation(child.getValue1())))
                            else result.add(new SelectItem(child.getId().toString(), "-- " + child.getValue1()))
                            if (!child.loadChildren().isEmpty()) {
                                List<AuthorityListTermAdmin> listInfant = child.loadChildren()
                                Collections.sort(listInfant, new CustomComparatorTerm())
                                for (AuthorityListTermAdmin infant : listInfant) {
                                    if (infant.getValue1().startsWith("al_")) result.add(new SelectItem(infant.getId().toString(), "   -- " + BundleUtils.getTranslation(infant.getValue1())))
                                    else result.add(new SelectItem(infant.getId().toString(), "   -- " + infant.getValue1()))
                                }
                            }
                        }
                    }
                }
            }
        }
        return result
    }
    /***
     * Retourne tous les items reçu de la liste d'autorité reçu en paramètre
     *
     * @param authorityListId
     * @return
     * @throws Exception
     */
    static List<SelectItem> getAuthorityListOfSelectItemWithEmptyItem(int authorityListId) throws Exception {
        List<SelectItem> result = new ArrayList<SelectItem>()
        List<AuthorityListTermAdmin> terms = AuthorityListTermAdmin.loadTermRoots(DossierCoreContext.getAdminJeton(), authorityListId)
        result.add(new SelectItem(null, " "))
        Collections.sort(terms, new CustomComparatorTerm())
        for (AuthorityListTermAdmin term : terms) {
            if (term.getActive()) {
                if (term.getValue1().startsWith("al_")) result.add(new SelectItem(term.getId().toString(), BundleUtils.getTranslation(term.getValue1())))
                else result.add(new SelectItem(term.getId().toString(), term.getValue1()))
                if (!term.loadChildren().isEmpty()) {
                    List<AuthorityListTermAdmin> childs = term.loadChildren()
                    Collections.sort(childs, new CustomComparatorTerm())
                    for (AuthorityListTermAdmin child : childs) {
                        if (child.getActive()) {
                            if (child.getValue1().startsWith("al_")) result.add(new SelectItem(child.getId().toString(), "-- " + BundleUtils.getTranslation(child.getValue1())))
                            else result.add(new SelectItem(child.getId().toString(), "-- " + child.getValue1()))
                            if (!child.loadChildren().isEmpty()) {
                                List<AuthorityListTermAdmin> listInfant = child.loadChildren()
                                Collections.sort(listInfant, new CustomComparatorTerm())
                                for (AuthorityListTermAdmin infant : listInfant) {
                                    if (infant.getValue1().startsWith("al_")) result.add(new SelectItem(infant.getId().toString(), "   -- " + BundleUtils.getTranslation(infant.getValue1())))
                                    else result.add(new SelectItem(infant.getId().toString(), "   -- " + infant.getValue1()))
                                }
                            }
                        }
                    }
                }
            }
        }
        return result
    }


    static Map<String, List<SelectItem>> getDependencyTypesGroups() {
        Map<String, List<SelectItem>> result = new HashMap()
        List<AuthorityListTermAdmin> termsType = AuthorityListTermAdmin.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_TYPES_DOCUMENT_ID)
        List<AuthorityListTermAdmin> termsGroup = AuthorityListTermAdmin.loadTermRoots(DossierCoreContext.getAdminJeton(), Constants.LIST_GROUPES_DOCUMENT_ID)
        List<SelectItem> listType = null
        for (AuthorityListTermAdmin termGroup : termsGroup) {
            listType = new ArrayList()
            for (AuthorityListTermAdmin termType : termsType) {
                if (termGroup.getCode().equalsIgnoreCase(termType.getValue5().replaceAll(";", ""))) {
                    if (termType.getValue1().startsWith("al_")) listType.add(new SelectItem(termType.getId().toString(), BundleUtils.getTranslation(termType.getValue1())))
                    else listType.add(new SelectItem(termType.getId().toString(), termType.getValue1()))
                }
            }
            result.put(termGroup.getId().toString(), listType)
        }
        return result
    }

    /***
     * Génère un mot de passe aléatoire
     *
     * @return
     * @throws Exception
     */
    static String generateRandomPassword() throws Exception {
        String result = ""
        for (int i = 0; i < 3; i++) {
            Random r = new Random()
            int c = r.nextInt(26) + (byte) 'a'
            result = result + (char) c

        }
        for (int j = 0; j < 2; j++) {
            Random rn = new Random()
            int num = rn.nextInt(10 - 1 + 1) + 1
            result = result + num
        }
        return result.toUpperCase()
    }
    /***
     * Execute une request XPath dans le document reçu en paramètre
     *
     * @param xmlDocument
     * @param request
     * @return
     * @throws Exception
     */
    static List<SelectItem> getContentsListOfSelectItem(org.w3c.dom.Document xmlDocument, String request) throws Exception {
        List<SelectItem> result = new ArrayList()
        XPath xPath = XPathFactory.newInstance().newXPath()
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET)
        for (int i = 0; i < nodeList.length; i++) {
            Node node = (Node) nodeList.item(i)
            Element parent = (Element) node.getParentNode()
            result.add(new SelectItem(parent.getAttribute("id"), BundleUtils.getTranslation(node.getTextContent())))
        }
        //Collections.sort(result, new CustomComparator());
        return result
    }

    /***
     * Execute une request XPath dans le document reçu en paramètre
     *
     * @param xmlDocument
     * @param request
     * @return
     * @throws Exception
     */
    static List<String> getContentsList(org.w3c.dom.Document xmlDocument, String request) throws Exception {
        List<String> result = new ArrayList()
        XPath xPath = XPathFactory.newInstance().newXPath()
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET)
        Node node = (Node) nodeList.item(0)
        if (node != null && !node.getTextContent().isEmpty()) result = Arrays.asList(node.getTextContent().split("::"))
        return result
    }

    /***
     * Execute une request XPath dans le document reçu en paramètre
     *
     * @param xmlDocument
     * @param request
     * @return
     * @throws Exception
     */
    static List<String> getContentsListWithFormat(org.w3c.dom.Document xmlDocument, String request, String format) throws Exception {
        List<String> result = new ArrayList()
        XPath xPath = XPathFactory.newInstance().newXPath()
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET)
        Node node = (Node) nodeList.item(0)
        if (node != null && !node.getTextContent().isEmpty()) {
            for (String s : node.getTextContent().split("::")) {
                if (s != null && !s.isEmpty()) result.add(String.format(format, Integer.valueOf(s)))
            }
        }
        return result
    }

    static Map<String, List<String>> getContentsMapWithFormat(org.w3c.dom.Document xmlDocument, String request, String format) throws Exception {
        Map<String, String> result = new HashMap<>()

        XPath xPath = XPathFactory.newInstance().newXPath()
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET)
        for (int i = 0; i < nodeList.getLength(); i++) {
            List<String> tmp = new ArrayList<>()
            for (String s : nodeList.item(i).getTextContent().split("::")) {
                if (s != null && !s.isEmpty()) tmp.add(String.format(format, Integer.valueOf(s)))
            }
            result.put(nodeList.item(i).getNodeName(), tmp)
        }
        return result
    }


    /***
     * Execute une request XPath dans le document reçu en paramètre
     * Contrôle si l'élément recherché dans le fichier XML existe
     *
     * @param xmlDocument
     * @param request
     * @return
     * @throws Exception
     */
    static boolean isContentExist(org.w3c.dom.Document xmlDocument, String request) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath()
        NodeList nodeList = (NodeList) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODESET)
        return (nodeList.length == 1)
    }

    /***
     * Execute une request XPath dans le document reçu en paramètre
     *
     *
     * @param xmlDocument
     * @param request
     * @return
     * @throws Exception
     */
    static String getContent(org.w3c.dom.Document xmlDocument, String request) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath()
        Node node = (Node) xPath.compile(request).evaluate(xmlDocument, XPathConstants.NODE)
        return (node != null) ? node.getTextContent() : ""
    }

    /***
     * Convertir une liste de String vers une liste de SelectItem
     *
     *
     * @param xmlDocument
     * @param request
     * @return
     * @throws Exception
     */
    static List<SelectItem> listToSelectItem(List<String> list) throws Exception {
        List<SelectItem> result = new ArrayList()
        for (String item : list) {
            result.add(new SelectItem(item, item))
        }
        return result
    }


    /***
     *
     *
     * @param validators
     * @param validatorsDone
     * @param validatorCurrent
     * @return
     * @throws Exception
     */

    static boolean hasAllValidatorsDone(List<?> validators, List<?> validatorsDone, Integer validatorCurrent) throws Exception {
        if (validatorsDone == null) validatorsDone = new ArrayList()
        validatorsDone.add(validatorCurrent)
        return validators.containsAll(validatorsDone)
    }
    /***
     * Nettoie le numéro de sécurité sociale reçu en paramètre et le retourne
     *
     * @param nss
     * @return
     * @throws Exception
     */
    static String completeNssForSearch(String nss) throws Exception {
        nss = nss.replaceAll("[^0-9\\*\\+]", "")

        if (!nss.startsWith("756") && !nss.startsWith("099") && !nss.startsWith("999") && !nss.startsWith("000") && !nss.startsWith("*") && !nss.startsWith("+") && !nss.endsWith("999") && !nss.endsWith("000") && !nss.endsWith("+") && !nss.endsWith("*")) {
            nss = "756" + nss
        }
        if (nss.length() < 13 && !nss.contains("+")) {
            nss = nss + "+"
        }
        return nss
    }
    /***
     * Permet de savoir si un numéro NSS est valide ou non
     *
     * @param nss
     * @return
     * @throws Exception
     */
    static boolean isNSSValid(String nss) throws Exception {
        // NSS Temporaire valide
        if (nss.startsWith("999") || nss.startsWith("000") || nss.endsWith("999") || nss.endsWith("000")) {
            return true
            // NSS Traditionnel
        } else {
            int checkDigit = 0
            for (int i = 0; i <= 11; i++) {
                int digit = Integer.parseInt(nss.substring(i, i + 1))
                if (i % 2 == 0) {
                    checkDigit = checkDigit + digit * 1
                } else {
                    checkDigit += 3 * digit
                }
            }
            checkDigit = checkDigit % 10
            checkDigit = (10 - checkDigit) % 10
            return (checkDigit != Integer.parseInt(nss.substring(12)))
        }
    }
    /***
     * Convertie une date reçue en paramètre pour AirsDossier
     *
     * @param date
     * @return
     * @throws Exception
     */
    static String convertDateForAIRS(String date) throws Exception {
        SimpleDateFormat sdfInput = new SimpleDateFormat(Constants.DATE_FORMAT_INPUT, Locale.ENGLISH)
        SimpleDateFormat sdfOutput = new SimpleDateFormat(Constants.DATE_FORMAT_AIRS)
        Date dateInput = sdfInput.parse(date)
        return sdfOutput.format(dateInput)
    }

    /***
     * Retourne une liste d'id de document de la BD
     *
     * @param request
     * @return
     * @throws Exception
     */
    static List<String> getListNSSDocumentbyRequest(String request) throws Exception {
        List<String> result = new ArrayList<String>()
        java.sql.Connection conn = null
        Statement ps = null
        ResultSet rs = null

        try {
            Class.forName(Constants.DB_AIRS_DRIVER)
            conn = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            ps = conn.createStatement()
            rs = ps.executeQuery(request)
            while (rs.next()) {
                result.add(rs.getString(1))
            }
        } catch (Exception e) {
            throw new Exception("Récupération des éléments de la liste des documents à traiter impossible : ", e)
        } finally {
            if (rs != null) {
                try {
                    rs.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (ps != null) {
                try {
                    ps.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (conn != null) {
                try {
                    conn.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }
        }
        return result
    }

    /***
     * Retourne une liste d'id de document de la BD
     *
     * @param request
     * @return
     * @throws Exception
     */
    static List<Integer> getListIdDocumentbyRequest(String request) throws Exception {
        List<Integer> result = new ArrayList<Integer>()
        java.sql.Connection conn = null
        Statement ps = null
        ResultSet rs = null

        try {
            Class.forName(Constants.DB_AIRS_DRIVER)
            conn = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            ps = conn.createStatement()
            rs = ps.executeQuery(request)
            while (rs.next()) {
                result.add(rs.getInt(1))
            }
        } catch (Exception e) {
            throw new Exception("Récupération des éléments de la liste des documents à traiter impossible : ", e)
        } finally {
            if (rs != null) {
                try {
                    rs.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (ps != null) {
                try {
                    ps.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (conn != null) {
                try {
                    conn.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }
        }
        return result
    }

    static Integer executeRequest(String request) throws Exception {
        Integer result = 0
        java.sql.Connection conn = null
        Statement ps = null

        try {
            Class.forName(Constants.DB_AIRS_DRIVER)
            conn = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            ps = conn.createStatement()
            result = ps.executeUpdate(request)
            conn.commit()
        } catch (Exception e) {
            throw new Exception("Execution de la requête impossible : " + request, e)
        } finally {
            if (ps != null) {
                try {
                    ps.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (conn != null) {
                try {
                    conn.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }
        }
        return result
    }

    static String executeQuery(String request) throws Exception {
        String result = ""
        java.sql.Connection conn = null
        Statement ps = null
        ResultSet rs = null

        try {
            Class.forName(Constants.DB_AIRS_DRIVER)
            conn = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            ps = conn.createStatement()
            rs = ps.executeQuery(request)
            while (rs.next()) {
                result = rs.getString(1)
                break
            }
        } catch (Exception e) {
            throw new Exception("Execution de la requête impossible : " + request, e)
        } finally {
            if (ps != null) {
                try {
                    ps.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (conn != null) {
                try {
                    conn.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }
        }
        return result
    }


    static void concatPdf(PdfCopy copy, String file) throws Exception {
        PdfReader reader = null
        try {
            int n = 0
            reader = new PdfReader(file)
            n = reader.getNumberOfPages()
            for (int page = 0; page < n;) {
                copy.addPage(copy.getImportedPage(reader, ++page))
            }
            copy.freeReader(reader)
        } catch (Exception ex) {
            throw new Exception("ConcatPdf : Erreur lors de la concaténation : " + file, ex)
        } finally {
            if (reader != null) reader.close()
        }

    }

    static void concatPdf(PdfCopy copy, String file, ArrayList<HashMap<String, Object>> outlines) throws Exception {
        try {
            PdfReader reader
            int n = 0
            reader = new PdfReader(file)
            n = reader.getNumberOfPages()
            for (int page = 0; page < n;) {
                copy.addPage(copy.getImportedPage(reader, ++page))

            }
            copy.setOutlines(outlines)
            copy.freeReader(reader)
            reader.close()
        } catch (FileNotFoundException ex) {
            throw new Exception("ConcatPdf : Fichier en paramètre non trouvé", ex)
        } catch (IOException ex) {
            throw new Exception("ConcatPdf :  Erreur lors de la concaténation", ex)
        } catch (BadPdfFormatException ex) {
            throw new Exception("ConcatPdf :  Erreur lors de la concaténation", ex)
        } catch (DocumentException ex) {
            throw new Exception("ConcatPdf : Erreur lors de la concaténation", ex)
        }

    }

    static String getUserName(Integer userId) {
        String result = ""
        try {
            UserAdmin user = UsersManager.load(DossierCoreContext.getAdminJeton(), userId)
            result = user.getFirstName() + " " + user.getName()
        } catch (Exception e) {
            result = ""
        }
        return result
    }

    static ProfilAdmin hasActorGeneric(List<Object> users, Integer userId) {
        List<ProfilAdmin> profiles = ProfilsManager.loadAll(DossierCoreContext.getAdminJeton())
        for (Object id : users) {
            UserAdmin user = UsersManager.load(DossierCoreContext.getAdminJeton(), Integer.parseInt(String.valueOf(id)))
            String profilName = "CORBEILLE_" + user.getLogin()
            String profilName2 = "OAITI_WF_" + user.getLogin()
            String serviceName = "SERVICE_" + user.getLogin().split("::")[0]
            for (ProfilAdmin profil : profiles) {
                if ((profil.getCode().equals(profilName2) || profil.getCode().equals(profilName) || profil.getCode().equals(serviceName)) && profil.getUserIds().contains(userId)) return profil
            }
        }
        return null
    }

    static ProfilAdmin hasActorGeneric(List<Object> users) {
        List<ProfilAdmin> profiles = ProfilsManager.loadAll(DossierCoreContext.getAdminJeton())
        for (Object id : users) {
            UserAdmin user = UsersManager.load(DossierCoreContext.getAdminJeton(), Integer.parseInt(String.valueOf(id)))
            String profilName = "CORBEILLE_" + user.getLogin()
            String profilName2 = "OAITI_WF_" + user.getLogin()
            String serviceName = "SERVICE_" + user.getLogin().split("::")[0]
            for (ProfilAdmin profil : profiles) {
                if (profil.getCode().equals(profilName2) || profil.getCode().equals(profilName) || profil.getCode().equals(serviceName)) return profil
            }
        }
        return null
    }

    static String getActors(String nss) throws Exception {
        String result = null
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        String query = null
        String webAiGest = null
        Map<String, String> usersMap = new HashMap()
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            List<UserAdmin> userAdminList = UsersManager.loadAll(DossierCoreContext.getAdminJeton())
            for (UserAdmin userAdmin : userAdminList) {
                if (userAdmin.getActive()) {
                    usersMap.put(userAdmin.getLogin(), String.valueOf(userAdmin.getId()))
                }
            }

            nss = formatString(nss, Constants.NSS_MASK)

            try {
                if (nss != null && (nss.startsWith("756") || nss.startsWith("099"))) {
                    String res = getInformationFromWebAI(conf.getProperty("webai.url.gestionnaire"), conf.getProperty("webai.json.request.nss.gestionnaire").replace("##NSS##", nss), conf.getProperty("webai.json.request.gestionnaire.nss.information"))
                    if (!res.isEmpty()) {

                        if ("archive".equalsIgnoreCase(res)) {
                            result = "archive"
                        } else {
                            String[] arr$ = res.split(conf.getProperty("separateur.webai"))
                            int len$ = arr$.length

                            for (int i$ = 0; i$ < len$; ++i$) {
                                String tmpGest = arr$[i$]
                                webAiGest = (String) usersMap.get(tmpGest)
                                if (webAiGest != null) {
                                    if (result != null) {
                                        if (!result.contains(webAiGest)) {
                                            result = result + ";" + webAiGest
                                        }
                                    } else {
                                        result = webAiGest
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new Exception("Error getActors : ", e)
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des données WEB@AI : " + query, e)
        } finally {
            if(inputStreamReader != null){
                try{
                    inputStreamReader.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture du fichier de configuration impossible : ", ex)
                }
            }
        }

        return result
    }

    static Integer getUserIdBasketByProfil(ProfilAdmin profilAdmin) {
        String userLogin = profilAdmin.getCode().replaceAll("CORBEILLE_", "")
        String userLogin2 = profilAdmin.getCode().replaceAll("OAITI_WF_", "")
        String serviceLogin = profilAdmin.getCode().replaceAll("SERVICE_", "").split("::")[0]
        for (UserAdmin userAdmin : profilAdmin.getUsers()) {
            if (userLogin.equals(userAdmin.getLogin()) || userLogin2.equals(userAdmin.getLogin()) || userAdmin.getLogin().startsWith(serviceLogin)) return userAdmin.getId()
        }
        return 0
    }

    static Integer getUserIdBasketByProfil(ProfilAdmin profilAdmin, List<Integer> listUsers) {
        String userLogin = profilAdmin.getCode().replaceAll("CORBEILLE_", "")
        String userLogin2 = profilAdmin.getCode().replaceAll("OAITI_WF_", "")
        String serviceLogin = profilAdmin.getCode().replaceAll("SERVICE_", "").split("::")[0]
        for (UserAdmin userAdmin : profilAdmin.getUsers()) {
            for (Integer id : listUsers) {
                if (userLogin2.equals(userAdmin.getLogin()) || userLogin.equals(userAdmin.getLogin()) || (userAdmin.getLogin().startsWith(serviceLogin) && listUsers.contains(userAdmin.getId()))) return userAdmin.getId()
            }
        }
        return 0
    }

    static String formatString(String string, String mask)
            throws java.text.ParseException {
        javax.swing.text.MaskFormatter mf =
                new javax.swing.text.MaskFormatter(mask)
        mf.setValueContainsLiteralCharacters(false)
        return mf.valueToString(string)
    }

    static String getUserId(String login) throws Exception {
        String result = null
        List<UserAdmin> users = UsersManager.loadAll(DossierCoreContext.getAdminJeton())
        for (UserAdmin user : users) {
            if (login.equalsIgnoreCase(user.getLogin())) {
                result = user.getId()
                break
            }
        }
        return result
    }

    static String isConfidentiel(String nss) throws Exception {
        String result = null
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        java.sql.Connection connection = null
        Statement statement = null
        ResultSet resultSet = null
        String query = null
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            Class.forName(conf.getProperty("webai.db.class"))
            connection = DriverManager.getConnection(conf.getProperty("webai.db.url"), conf.getProperty("webai.db.user"), new String(DatatypeConverter.parseBase64Binary(conf.getProperty("webai.db.password")), "UTF-8"))
            statement = connection.createStatement()
            resultSet = statement.executeQuery(conf.getProperty("webai.db.confidential").replaceAll(conf.getProperty("replace.value"), nss))
            while (resultSet.next()) {
                result = getUserId(conf.getProperty("doc.webai_gestionnaire_login"))
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des données WEB@AI : " + query, e)
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (connection != null) {
                try {
                    connection.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture du fichier de configuration impossible : ", ex)
                }
            }
        }

        return result
    }

    static boolean isWSConfidentiel(String nss) throws Exception {
        boolean result = null
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            MaskFormatter mf = new MaskFormatter(Constants.NSS_MASK)
            mf.setValueContainsLiteralCharacters(false)
            result = Boolean.valueOf(getInformationFromWebAI(conf.getProperty("webai.url.isconfidentiel"), conf.getProperty("webai.json.request.isconfidentiel.by.nss").replace("##NSS##", mf.valueToString(nss)), conf.getProperty("webai.json.request.isconfidentiel.information")))
        } catch (Exception e) {
            return result
        }
        return result
    }

    static List<SelectItem> getStakeholdersInWebAI(String nss) throws Exception {
        List<SelectItem> result = new ArrayList()
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        java.sql.Connection connection = null
        Statement statement = null
        ResultSet resultSet = null
        String query = null
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            MaskFormatter mf = new MaskFormatter(Constants.NSS_MASK)
            mf.setValueContainsLiteralCharacters(false)
            query = conf.getProperty("webai.db.requete.get_intervenants").replaceAll(conf.getProperty("replace.value"), mf.valueToString(nss))

            Class.forName(conf.getProperty("webai.db.class"))
            connection = DriverManager.getConnection(conf.getProperty("webai.db.url"), conf.getProperty("webai.db.user"), new String(DatatypeConverter.parseBase64Binary(conf.getProperty("webai.db.password")), "UTF-8"))
            statement = connection.createStatement()
            resultSet = statement.executeQuery(query)
            while (resultSet.next()) {
                result.add(new SelectItem(resultSet.getString(1), resultSet.getString(2)))
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des données WEB@AI : " + query, e)
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (connection != null) {
                try {
                    connection.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }

            if(inputStreamReader != null){
                try{
                    inputStreamReader.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture du fichier de configuration impossible : ", ex)
                }
            }
        }

        return result
    }

    static List<SelectItem> getStakeholdersInWSWebAI(String nss) throws Exception {
        List<SelectItem> result = new ArrayList()
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        String res = ""
        java.sql.Connection connection = null
        Statement statement = null
        ResultSet resultSet = null
        String query = null
        try {
            Class.forName(Constants.DB_AIRS_DRIVER)
            connection = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            MaskFormatter mf = new MaskFormatter(Constants.NSS_MASK)
            mf.setValueContainsLiteralCharacters(false)

            res = getInformationFromWebAI(conf.getProperty("webai.url.intervenants"), conf.getProperty("webai.json.request.intervenants").replace("##NSS##", mf.valueToString(nss)), conf.getProperty("webai.json.request.intervenant.information"))
            if (!res.isEmpty()) {

                JSONArray jsonObjRes = new JSONArray(res)

                for (int cpt = 0; cpt < jsonObjRes.length(); cpt++) {
                    if (Constants.SEULEMENT_INTERVENANTS_RECONNUS) {
                        query = Constants.GET_INTERVENANS_RECONNUS.replace("##EMAIL##", jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")))
                        statement = connection.createStatement()
                        resultSet = statement.executeQuery(query)
                        if (resultSet.next()) {
                            result.add(new SelectItem("OK-" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")) + "-" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.nom")), jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.nom")) + "(" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")) + ")"))
                        }
                    } else {
                        query = Constants.GET_INTERVENANS_RECONNUS.replace("##EMAIL##", jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")))
                        statement = connection.createStatement()
                        resultSet = statement.executeQuery(query)
                        if (resultSet.next()) {
                            result.add(new SelectItem("OK-" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")) + "-" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.nom")), jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.nom")) + "(" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")) + ")"))
                        } else {
                            result.add(new SelectItem("KO-" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")) + "-" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.nom")), jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.nom")) + "(" + jsonObjRes.getJSONObject(cpt).getString(conf.getProperty("webai.json.request.intervenant.information.email")) + ")"))
                        }
                    }
                }
            }
        } catch (Exception e) {
            return result
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (connection != null) {
                try {
                    connection.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture du fichier de configuration impossible : ", ex)
                }
            }
        }
        return result
    }

    static List<String> getRequestInWebAI(String nss, String typeQuery) throws Exception {
        List<String> result = new ArrayList()
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        java.sql.Connection connection = null
        Statement statement = null
        ResultSet resultSet = null
        String query = null
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            Class.forName(conf.getProperty("webai.db.class"))
            connection = DriverManager.getConnection(conf.getProperty("webai.db.url"), conf.getProperty("webai.db.user"), new String(DatatypeConverter.parseBase64Binary(conf.getProperty("webai.db.password")), "UTF-8"))
            if ("name".equals(typeQuery)) query = conf.getProperty("webai.db.requete.get_assure").replaceAll("##num_assure##", nss)
            else if ("confidential".equals(typeQuery)) query = conf.getProperty("webai.db.confidential_all")
            else if ("birthday".equals(typeQuery)) query = conf.getProperty("webai.db.birthday").replaceAll(conf.getProperty("replace.value"), nss)
            else if ("death".equals(typeQuery)) query = conf.getProperty("webai.db.death").replaceAll(conf.getProperty("replace.value"), nss)
            else throw new Exception("Type de requete inconnu : " + typeQuery)

            statement = connection.createStatement()
            resultSet = statement.executeQuery(query)
            while (resultSet.next()) {
                if ("birthday".equals(typeQuery) || "death".equals(typeQuery)) result.add(resultSet.getDate(1).toString())
                else result.add(resultSet.getString(1))
            }
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des données WEB@AI : " + query, e)
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (statement != null) {
                try {
                    statement.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (connection != null) {
                try {
                    connection.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }

            if(inputStreamReader != null){
                try {
                    inputStreamReader.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture du fichier de configuration impossible : ", ex)
                }
            }
        }

        return result
    }

    static String getNameInWSWebAI(String nss) throws Exception {
        String result = ""
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null

        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)
            nss = formatString(nss, Constants.NSS_MASK)
            result = getInformationFromWebAI(conf.getProperty("webai.url.name.by.nss"), conf.getProperty("webai.json.request.name.by.nss").replace("##NSS##", nss), conf.getProperty("webai.json.request.name.by.nss.information"))
        } catch (Exception e) {
            return ""
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture du fichier de configuration impossible : ", ex)
                }
            }
        }

        return result
    }

    static void convertTiffToPDF(File tifFile, File pdfFile) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.LETTER, 0, 0, 0, 0)
        try {
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream(pdfFile))
            int pages = 0
            document.open()
            com.itextpdf.text.pdf.PdfContentByte cb = writer.getDirectContent()
            RandomAccessFileOrArray ra = null
            int comps = 0
            try {
                ra = new RandomAccessFileOrArray(tifFile.getAbsolutePath())
                comps = TiffImage.getNumberOfPages(ra)
            } catch (Throwable e) {
                throw new Exception("Erreur à la conversion du document ", e)
            }

            for (int c = 1; c <= comps; ++c) {
                try {
                    Image img = TiffImage.getTiffImage(ra, c)
                    if (img != null) {
                        img.scalePercent((float) 7200f / img.getDpiX(), (float) 7200f / img.getDpiY())
                        document.setPageSize(new com.itextpdf.text.Rectangle(img.getScaledWidth(), img.getScaledHeight()))
                        img.setAbsolutePosition(0, 0)
                        cb.addImage(img)
                        document.newPage()
                        ++pages
                    }
                } catch (Throwable e) {
                    throw new Exception("Erreur à la conversion du document ", e)
                }
            }
            ra.close()
            document.close()


        } catch (Exception ex) {
            throw new Exception("Ereur lors de la conversion du TIF", ex)
        }
    }

    static String getPassWordOfUser(String login) {
        String result = null
        java.sql.Connection conn = null
        Statement ps = null
        ResultSet rs = null

        try {
            Class.forName(Constants.DB_AIRS_DRIVER)
            conn = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            ps = conn.createStatement()
            rs = ps.executeQuery("SELECT usr_password FROM users WHERE usr_login = '" + login + "'")
            while (rs.next()) {
                return rs.getString(1)
            }
        } catch (Exception e) {
            throw new Exception("Récupération des éléments de la liste des documents à traiter impossible : ", e)
        } finally {
            if (rs != null) {
                try {
                    rs.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (ResultSet): ", ex)
                }
            }
            if (ps != null) {
                try {
                    ps.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (conn != null) {
                try {
                    conn.close()
                } catch (SQLException ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }
        }
        return result
    }

    static boolean copyFile(File source, File dest) throws Exception {
        try {
            InputStream sourceFile = new FileInputStream(source)
            OutputStream destinationFile = new FileOutputStream(dest)
            byte[] buffer = new byte[512 * 1024]
            int nbLecture
            while ((nbLecture = sourceFile.read(buffer)) != -1) {
                destinationFile.write(buffer, 0, nbLecture)
            }
        } catch (IOException e) {
            throw new Exception("Erreur lors de la copie du fichier : ", e)
        }
        return true // Résultat OK
    }


    static void logActionUser(String action, String filter, String numberOfDocument, String userLogin, String criteria) {
        int res = 0
        java.sql.Connection conn = null
        Statement ps = null
        String query = null
        try {
            query = Constants.DB_AIRS_REQUEST_INSERT_ACTION.replace("##ACTION##", action)
            query = query.replace("##UTILISATEUR##", userLogin)
            query = query.replace("##CRITERE##", criteria)
            query = query.replace("##FILTER##", filter)
            query = query.replace("##NUMBEROFDOCUMENT##", numberOfDocument)
            Class.forName(Constants.DB_AIRS_DRIVER)
            conn = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, new String(DatatypeConverter.parseBase64Binary(Constants.DB_AIRS_PASSWORD), "UTF-8"))
            ps = conn.createStatement()
            res = ps.executeUpdate(query)
        } catch(Exception e){
            throw new Exception("Problème lors de la journalisation d'une action : " + query, e)
        } finally {
            if (ps != null) {
                try {
                    ps.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture de la connexion impossible (Statement): ", ex)
                }
            }
            if (conn != null) {
                try {
                    conn.close()
                } catch (Exception ex) {
                    throw new Exception("Cloture de la connexion impossible (Connection): ", ex)
                }
            }
        }
    }

    static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,})\$)"
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern)
        java.util.regex.Matcher m = p.matcher(email)
        return m.matches()
    }

    static List<String> getConfidentials() {

        String resTmp = ""
        String res = ""
        List<String> confidentials = new ArrayList()
        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            if (res.isEmpty()) {
                String tokenRequest = getToken()
                if (!tokenRequest.isEmpty()) {
                    java.net.URL url = new URL(conf.getProperty("webai.url.confidentials"))
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection()
                    conn.setDoOutput(true)
                    conn.setRequestMethod("GET")
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("Authorization", "Bearer " + tokenRequest)


                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode())
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())))

                    String output
                    while ((output = br.readLine()) != null) {
                        resTmp += output
                    }
                    if (!resTmp.isEmpty()) {
                        JSONObject jsonObj = new JSONObject(resTmp)
                        res = jsonObj.getString(conf.getProperty("webai.json.request.confidential"))
                    }
                    JSONArray jsonObjRes = new JSONArray(res)
                    for (int cpt = 0; cpt < jsonObjRes.length(); cpt++) {
                        confidentials.add(jsonObjRes.getString(cpt))
                    }


                    conn.disconnect()
                }
            }
        } catch (Exception e) {
            throw new Exception("[WS - ERREUR] - getInformationFromWebAI " + e.getMessage(), e)
        }
        return confidentials

    }

    static String getInformationFromWebAI(String URL, String request, String informationRequest) {

        String resTmp = ""
        String res = ""
        Properties conf = new Properties()
        try {

            if (res.isEmpty()) {
                String tokenRequest = Methods.executeQuery(Constants.DB_AIRS_REQUEST_GET_WEBAI_TOKEN)
                if (!tokenRequest.isEmpty()) {
                    java.net.URL url = new URL(URL)
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection()
                    conn.setDoOutput(true)
                    conn.setRequestMethod("POST")
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("Authorization", "Bearer " + tokenRequest)

                    OutputStream os = conn.getOutputStream()
                    os.write(request.getBytes())
                    os.flush()

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode())
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream()), Charset.forName("UTF-8")))

                    String output
                    while ((output = br.readLine()) != null) {
                        resTmp += output
                    }
                    if (!resTmp.isEmpty()) {
                        JSONObject jsonObj = new JSONObject(resTmp)
                        res = jsonObj.getString(informationRequest)
                    }
                    conn.disconnect()
                }
            }
        } catch (Exception e) {
            throw new Exception("[WS - ERREUR] - getInformationFromWebAI " + e.getMessage(), e)
        }
        return res

    }

    private static String getToken() throws JSONException {

        Properties conf = new Properties()
        InputStreamReader inputStreamReader = null
        String resultat = ""
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(Constants.XML_WEBSERVICES_CONFIGURATION_PATH))
            conf.load(inputStreamReader)

            java.net.URL url = new URL(conf.getProperty("webai.url.token"))
            HttpURLConnection conn = (HttpURLConnection) url.openConnection()
            conn.setDoOutput(true)
            conn.setRequestMethod("POST")
            conn.setRequestProperty("Content-Type", "application/json")
            String input = conf.get("webai.json.request.token")
            OutputStream os = conn.getOutputStream()
            os.write(input.getBytes())
            os.flush()

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode())
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())))
            String output
            String res = ""
            while ((output = br.readLine()) != null) {
                res += output

            }
            conn.disconnect()
            if (!res.isEmpty()) {
                JSONObject jsonObj = new JSONObject(res)
                resultat = jsonObj.getString("accessToken")

            } else {
                resultat = ""
            }
            return resultat
        } catch (Exception e) {
            throw new Exception("[WS - ERREUR] - getToken " + e.getMessage(), e)
        }
    }

    static String sendRequestAPI(String jsonRequest, String api) {
        URL url
        HttpURLConnection conn = null
        try {

            url = new URL(Constants.SERVEUR_DIGITAL_API + api)

            conn = (HttpURLConnection) url.openConnection()
            conn.setDoOutput(true)
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Content-Type", "application/json")
            OutputStream os = conn.getOutputStream()
            os.write(jsonRequest.getBytes())
            os.flush()

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error DigitalAPI :"
                        + conn.getResponseCode() + "  -  " + conn.getResponseMessage())
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream()), Charset.forName("UTF-8")))

            String output
            while ((output = br.readLine()) != null) {
                return output
            }

            conn.disconnect()
        } catch (Exception exc) {
            throw new Exception(exc)
        } finally {
            conn.disconnect()
        }

    }


}

class CustomComparator implements Comparator<SelectItem> {
    @Override
    int compare(SelectItem o1, SelectItem o2) {
        return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase())
    }
}

class CustomComparatorTerm implements Comparator<AuthorityListTermAdmin> {

    @Override
    int compare(AuthorityListTermAdmin o1, AuthorityListTermAdmin o2) {
        return o1.getValue().compareTo(o2.getValue())
    }
}
