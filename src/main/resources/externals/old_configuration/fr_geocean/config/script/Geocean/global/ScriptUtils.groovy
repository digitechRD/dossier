import com.digitech.dossier.common.model.backend.airs.IProfile;

import groovy.util.ScriptException

import java.io.File
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*

import net.sf.jooreports.templates.DocumentTemplateException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;

import com.digitech.airs3dossiers.constantes.DAirsDossierStringConstants
import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.model.backend.export.PdfRenumberFontModel;
import com.digitech.dossier.common.model.backend.report.ReportAttachment
import com.digitech.dossier.common.model.backend.report.ReportDocument
import com.digitech.dossier.common.model.backend.report.ReportModel
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson;
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backing.DefaultSharingModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.report.service.IDocumentConvertionService
import com.digitech.report.service.IDocumentGenerationService
import com.digitech.report.service.IDocumentInspectorService
import com.digitech.report.service.IDocumentMergingService
import com.digitech.report.service.impl.ooo.DocumentConvertionService
import com.digitech.report.service.impl.ooo.DocumentGenerationService
import com.digitech.report.service.impl.ooo.DocumentInspectorService
import com.digitech.report.service.impl.ooo.DocumentMergingService
import com.digitech.report.service.impl.ooo.OfficeManager
import com.digitech.report.service.impl.ooo.SectionModel
import com.digitech.toolbox.document.exception.DocumentOperationException
import com.digitech.toolbox.document.service.IOperationService
import com.digitech.dossier.common.service.Constants;
import com.digitech.dossier.common.service.IAuditService;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.service.ICounter;
import com.digitech.dossier.common.service.IRight;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceConstants;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.export.IOdtGenerator;
import com.digitech.dossier.common.service.export.impl.OdtGenerator;
import com.digitech.report.service.impl.ooo.OfficeManager;
import com.digitech.report.service.impl.ooo.SectionModel;
import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.dossier.common.utils.DateUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultValueExportInitializer;
import com.digitech.dossier.script.service.impl.ScriptMgr
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.admin.OrganizationAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager
import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.admin.UsersManager;
import com.digitech.jcorbairs.exception.DocumentException;
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException
import com.digitech.jcorbairs.admin.ProfilAdmin
import com.digitech.jcorbairs.admin.UserOrganizationAdmin
import com.digitech.report.service.IDocumentConvertionService;
import com.digitech.report.service.IDocumentGenerationService;
import com.digitech.report.service.IDocumentInspectorService;
import com.digitech.report.service.IDocumentMergingService;
import com.digitech.toolbox.document.exception.DocumentOperationException;
import com.digitech.toolbox.document.service.IOperationService;
import com.digitech.report.model.MergingModel.Type;
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType;
import com.digitech.dossier.common.model.backend.Constants.*;

/**
 * Utility methods for courrier light
 */
class ScriptUtils {
  private static File constantsFile = null;
  private static Properties properties = new Properties();
  static {
    constantsFile = new File(DossierCoreContext.getApplicationPath() + File.separator + ScriptMgr.SCRIPT_RELATIVE_PATH + File.separator + "Geocean" + File.separator + "global"  + File.separator + "constants.properties");
    constantsFile.withInputStream {  stream ->
      properties.load(stream)
    }
  }

  public static String getConstant(String code) {
    if (!properties.containsKey(code)) {
      throw new ScriptException("Key '" + code + "' not found in file " + constantsFile);
    }
    return properties.getProperty(code);
  }


  /**
   * @return IAuthorityList the Authority List
   */
  public static IAuthorityList getAuthorityListService() {
    return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
  }

  /** TODEL
   * Checks if the courrier field "T_ETAT_COURRIER" value is one of possible states.
   * @param theDocument the document
   * @param states the possible states
   * @return true if the courrier field "T_ETAT_COURRIER" value is one of possible states
   */
  public static boolean hasStateToDel(IDocument theDocument, List<String> states){
    return hasStateToDel(null, theDocument, states);
  }

  /** NEW
   * Checks if the field value in parameter is one of possible values.
   * @param theDocument the document
   * @param fieldCode constant of the field code to check
   * @param values the possible values
   * @return true if the field value is one of possible values
   */
  public static boolean hasValueInList(IDocument theDocument, String fieldCode, List<String> values){
    String fieldCodeValue = getConstant(fieldCode);
    Integer valueId = FieldUtils.getValue(theDocument, fieldCodeValue);
    if (valueId != null){
      Term term = ((IAuthorityList)getAuthorityListService()).getTerm(valueId);
      if (term == null) {
        throw new IllegalStateException("No term with ID '" + valueId + "' found for authority list " + fieldCodeValue);
      }
      return values.contains(term.getCode());
    }
    return false;
  }

  /** NEW
   * Return the field value in parameter.
   * @param theDocument the document
   * @param field the field
   * @return the value of the field
   */
  public static String getFieldValue(IDocument theDocument, String field){
    Integer valueId = FieldUtils.getValue(theDocument, field);
    if (valueId != null){
      Term term = ((IAuthorityList)getAuthorityListService()).getTerm(valueId);
      if (term == null) {
        throw new IllegalStateException("No term with ID '" + valueId + "' found for authority list " + field);
      }
      return term.getCode();
    }
    return "";
  }

  /** TODEL
   * Checks if the courrier field "T_ETAT_COURRIER" value is one of possible states.
   * @param userContext the user context
   * @param theDocument the document
   * @param states the possible states
   * @return true if the courrier field "T_ETAT_COURRIER" value is one of possible states
   */
  public static boolean hasStateToDel(UserContext userContext, IDocument theDocument, List<String> states){
    String fieldCodeCourrierState = getConstant("FIELD_CODE_T_ETAT_COURRIER");
    Integer stateId = FieldUtils.getValue(theDocument, fieldCodeCourrierState);
    if (stateId != null){
      Term term = ((IAuthorityList)getAuthorityListService()).getTerm(stateId);
      if (term == null) {
        throw new IllegalStateException("No term with ID '" + stateId + "' found for authority list " + fieldCodeCourrierState);
      }
      return states.contains(term.getCode());
    }
    return false;
  }

  /** TODEL
   * Checks if the DOS_FAC document field "FAC_STATUS" value is one of possible states.
   * @param theDocument the document
   * @param states the possible states
   * @return true if the courrier field "FAC_STATUS" value is one of possible states
   */
  public static boolean facDoshasStateToDel(IDocument theDocument, List<String> states){
    String fieldCodeCourrierState = getConstant("FIELD_FAC_STATUS");
    Integer stateId = FieldUtils.getValue(theDocument, fieldCodeCourrierState);
    if (stateId != null){
      Term term = ((IAuthorityList)getAuthorityListService()).getTerm(stateId);
      if (term == null) {
        throw new IllegalStateException("No term with ID '" + stateId + "' found for authority list " + fieldCodeCourrierState);
      }
      return states.contains(term.getCode());
    }
    return false;
  }

  /**
   * Defines if the document has an accepted visa.
   * @param userContext the user context
   * @param theDocument the document
   * @return
   */
  public static boolean hasVisaAccepted(UserContext userContext, IDocument theDocument) {
    String etatVisaFieldCode = ScriptUtils.getConstant("FIELD_CODE_T_ETAT_VISA");
    Integer visaState = FieldUtils.getValue(theDocument, etatVisaFieldCode);
    return ScriptUtils.getTermID(etatVisaFieldCode, ScriptUtils.getConstant("STATE_CODE_VISA_ACCEPTE")).equals(visaState);
  }

  /**
   * Checks if the user context is the owner of the document (base on field U_PROPRIETAIRE).
   * @param userContext the user context
   * @param theDocument the document
   * @return true if the user context is the owner of the document
   */
  public static boolean isOwnerUser(UserContext userContext, IDocument theDocument) {
    Integer docUserOwner = null;
    Integer docOrgOwner = null

    String fieldCodeCourrierOwnerUser = getConstant("FIELD_CODE_U_PROPRIETAIRE");
    docUserOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerUser);
    if (docUserOwner == null) {
      String fieldCodeCourrierOwnerGroup = getConstant("FIELD_CODE_O_PROPRIETAIRE");
      try {
        docOrgOwner = FieldUtils.getValue(theDocument, fieldCodeCourrierOwnerGroup);
      }
      catch(IllegalStateException ise) {
      }
    }
    return (docUserOwner == null && docOrgOwner != null &&  UserUtils.isInOrganization(userContext, docOrgOwner, true)) || (docUserOwner != null && docUserOwner.equals(userContext.getUser().getId()));
  }

  /**
   * Checks if the user context is the validator of the document (base on field U_VALIDEUR).
   * @param userContext the user context
   * @param theDocument the document
   * @return true if the user context is the validator of the document
   */
  public static boolean isValidatorUser(UserContext userContext, IDocument theDocument) {
    String fieldCodeCourrierValidatorUser = getConstant("FIELD_CODE_U_VALIDEUR");
    Integer docValidator = null;
    try {
      docValidator = FieldUtils.getValue(theDocument, fieldCodeCourrierValidatorUser);
    }
    catch(IllegalStateException ise) {
    }
    return docValidator != null && docValidator.equals(userContext.getUser().getId());
  }

  /**
   * Gets a term ID.
   * @param fieldCode the field code
   * @param termCode the term code
   * @return the term ID
   * @throws IdentificationException
   * @throws ServerException
   */
  public static Integer getTermID(String fieldCode, String termCode)
  throws IdentificationException, ServerException {
    return getTermID(null, fieldCode, termCode);
  }

  /**
   * Gets a term ID.
   * @param theDocument the document
   * @param fieldCode the field code
   * @param termCode the term code
   * @return the term ID
   * @throws IdentificationException
   * @throws ServerException
   */
  public static Integer getTermID(IDocument theDocument, String fieldCode, String termCode)
  throws IdentificationException, ServerException {
    List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(UserUtils.getAdminUserContext().getJeton(), fieldCode);
    for(Term term : termList) {
      if(term.getCode().equals(termCode)) {
        return term.getId();
      }
    }
    return -1;
  }

  /**
   * Gets a term Code.
   * @param fieldCode the field code
   * @param termId the term Id
   * @return the term Code
   * @throws IdentificationException
   * @throws ServerException
   */
  private static String getTermCode(String fieldCode, Integer termId)
  throws IdentificationException, ServerException {
    return getTermCode(null, fieldCode, termId);
  }

  /**
   * Gets a term Code.
   * @param theDocument the document
   * @param fieldCode the field code
   * @param termId the term Id
   * @return the term Code
   * @throws IdentificationException
   * @throws ServerException
   */
  private static String getTermCode(IDocument theDocument, String fieldCode, Integer termId)
  throws IdentificationException, ServerException {
    List<Term> termList = CourrierUtils.getAuthorityListMgr().getTerms(UserUtils.getAdminUserContext().getJeton(), fieldCode);
    for(Term term : termList) {
      if(term.getId().equals(termId)) {
        return term.getCode();
      }
    }
    return null;
  }

  public static List<User> getUsersWithProfilAndOrg(String profileCode, Integer orgId){
    List<User> users = new ArrayList<User>();
    List<User> allUsers = getUserMgr().getUsers();
    Integer profilId = -1;
    if (allUsers != null && !allUsers.isEmpty()){
      for(IProfile profile : getRightService().getExistingProfiles()) {
        if(profile.getCode().equals(profileCode)) {
          profilId = profile.getId().intValue();
        }
      }
      for (User user : allUsers){
        if (userHaveProfilAndOrga(user,profilId,orgId)){
          users.add(user);
        }
      }
    }
    return users;
  }

  public static List<User> getUsersWithProfil(String profileCode){
    List<User> users = new ArrayList<User>();
    List<User> allUsers = getUserMgr().getUsers();
    Integer profilId = -1;
    if (allUsers != null && !allUsers.isEmpty()){
      for(IProfile profile : getRightService().getExistingProfiles()) {
        if(profile.getCode().equals(profileCode)) {
          profilId = profile.getId().intValue();
        }
      }
      for (User user : allUsers){
        if (userHaveProfil(user,profilId)){
          users.add(user);
        }
      }
    }
    return users;
  }

  public static boolean userHaveProfilAndOrga(User user, Integer idProfil, Integer orgId){
    UserAdmin userAdmin = UsersManager.load(UserUtils.getAdminUserContext().getJeton(), user.getId());
    if (userHaveProfil(idProfil,userAdmin) && userHaveOrga(orgId,userAdmin)) {
      return true;
    }
    return false;
  }

  public static boolean userHaveProfil(User user, Integer idProfil){
    UserAdmin userAdmin = UsersManager.load(UserUtils.getAdminUserContext().getJeton(), user.getId());
    if (userHaveProfil(idProfil,userAdmin)) {
      return true;
    }
    return false;
  }

  public static boolean userHaveOrga(Integer orgId, UserAdmin user){
    for (UserOrganizationAdmin orgaAdmin : user.getOrganizations()){
      if (orgaAdmin.getOrganization().getId().equals(orgId)){
        return true;
      }
    }
    return false;
  }

  public static boolean userHaveProfil(Integer idProfil, UserAdmin userAdmin){
    for (ProfilAdmin profil : userAdmin.getProfils()){
      if (profil.getId().equals(idProfil)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param orgId the organization id
   * @param userContext the user context
   * @return service the service code
   */
  private static String getServiceCode(Integer orgId, UserContext userContext) throws IdentificationException, ServerException {
    String service = null;
    OrganizationAdmin orgAdm = OrganizationsManager.load(userContext.getJeton(), orgId);
    if (orgAdm != null){
      service = orgAdm.getCode();
    }
    return service;
  }

  /**
   * Generate the service label
   * @param numBordereauDateFormat the wished pattern of the date
   * @return dateFormated the formated date
   */
  public static String getFormattedDate(String numBordereauDateFormat) {
    String dateFormated = null;
    if (numBordereauDateFormat != null){
      dateFormated = new SimpleDateFormat(numBordereauDateFormat).format(new Date());
    } else {
      String message = "numBordereauDateFormat generation impossible due to misconfiguration of NUM_CHRONO_DATE_FORMAT property";
      throw new ScriptException(message);
    }
    return dateFormated;
  }

  public static IDocument createDocument(UserContext userContext, String ctyCode, Integer secretLevel, Map<String, String> mapCodeFieldValues){
    IDocument theNewDocument = getDocumentMgr().createDocument(userContext, ctyCode, secretLevel, mapCodeFieldValues);
    return theNewDocument;
  }

  public static void generateListing(String odt_templateFilePath, String odt_resultFilePath, String resultFilePath, String tempFilePath,
  List<IDocument> docList)
  throws FileNotFoundException, ServerException, IdentificationException, DocumentException, IOException, DocumentTemplateException,
  com.lowagie.text.DocumentException, DocumentOperationException {
    DefaultOfficeManagerConfiguration configuration =  DossierCoreContext.getParamsInfos().getOfficeConfiguration()
    // model to set the position of the stamper
    PdfRenumberFontModel pdfFont = new PdfRenumberFontModel();

    OfficeManager.getInstance().startOffice(configuration);

    try {
      File odtModelFile = new File(odt_templateFilePath);
      File odtResultFile = new File(odt_resultFilePath);
      File resultFile = new File(resultFilePath);


      // defining object
      Map<String, ReportAttachment> sectionAttachmentMap = new HashMap<String, ReportAttachment>();
      ReportModel reportDataModel = getOdtGeneration().buildReportModel(UserContext.getInstance(), docList, sectionAttachmentMap);
      Map<String, ReportModel> data = new HashMap<String, ReportModel>();
      Map<String, ReportAttachment> mapReportAttachement = new HashMap<String, ReportAttachment>();
      data.put( OdtGenerator.REPORT_MODEL_NAME, reportDataModel);


      // first step
      // fusion template with variable

      getDocumentGenerationService().generate(odtModelFile, odtResultFile, data);

      // refresh the section
      Map<String, SectionModel> fileSections = getDocumentInspectorService().getSectionModels(odtResultFile,  OdtGenerator.NAME_FILE_PREFIX);


      // for the document list, attachment traitment
      for(ReportDocument reportDoc : reportDataModel.getReportDocumentList()) {
        mapReportAttachement.putAll(reportDoc.getSectionAttachmentMap());
        // second step
        // merge file which can merge with ODT
        fileSections = getOdtGeneration().mergeFilesBeforeConversion(odt_templateFilePath, tempFilePath, odtResultFile, Type.SECTION, mapReportAttachement, fileSections);

        // update the section
        fileSections = getDocumentInspectorService().getSectionModels(odtResultFile, OdtGenerator.NAME_FILE_PREFIX);

        // third step
        // adding blank page for the pdf or file to be converted in pdf format. convert the unconverted file too
        getOdtGeneration().addBlankPage(odtResultFile, tempFilePath, mapReportAttachement, fileSections);

        // update the section
        fileSections = getDocumentInspectorService().getSectionModels(odtResultFile,  OdtGenerator.NAME_FILE_PREFIX);

        // renumber pdf file

        getOdtGeneration().renumberPdfFile( mapReportAttachement, fileSections, pdfFont);

      }

      // third step
      // generate output file
      getDocumentConversionService().convert(odtResultFile, resultFile);
      if(resultFile.exists() && resultFile.canWrite()) {
        // fourth step
        // concant the final result file with the others
        Map<String, SectionModel> fileSectionsPDF = getDocumentInspectorService().getSectionModels(odtResultFile, OdtGenerator.NAME_FILE_PREFIX);
        getOdtGeneration().concatWithPdfOutput(resultFile, tempFilePath, mapReportAttachement, fileSectionsPDF, configuration);
      }
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
    /*finally {
      OfficeManager.getInstance().stopOffice();
    } */
  }
  
  /**
  * AddHistoWrk : Ajoute un  evenement d'historique etape Workflow pour un champ
  */
  private static addHistoWrk(IDocument airsDocument, UserCoreContext userContext, String commentEvent) {
	 if(airsDocument.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER)
	 {
		 //Ajout dans l'historique
		 getAuditMgr().addDocumentEvent(userContext, airsDocument, AdvancedAuditType.ADV_EVENT_WF_TASK_SUBMIT.name(), commentEvent);
	 }
  }

  /**
  * AddHistoWrk : Ajoute un  evenement Workflow changement de user
  */
  private static addHistoWrkNewUser(IDocument airsDocument, UserCoreContext userContext, String fieldCode,  String oldValue, String newValue) {
	 if(airsDocument.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER)
	 {
		 //V   rification du Champ
		 if (newValue != null && oldValue.compareToIgnoreCase(newValue) != 0 ) {
			 //Ajout dans l'historique

			 String commentEvent = fieldCode+" : "+oldValue+" :"+newValue;
			 getAuditMgr().addDocumentEvent(userContext, airsDocument, AdvancedAuditType.ADV_EVENT_WF_TASK_SUBMIT.name(), commentEvent);

		 }
	 }
  }

  /**
  * AddHistoForField : Ajoute un  ©v ¨nement d'historique pour un champ
  */
  private static addHistoForField(IDocument airsDocument, UserCoreContext userContext, String fieldCode, String fieldType, String oldValue, String newValue) {
	 if(airsDocument.getLockType() != com.digitech.dossier.common.model.backend.Constants.DOC_LOCKED_BYOTHER)
	 {
		 //V   rification du Champ
		 if (newValue != null && oldValue.compareToIgnoreCase(newValue) != 0 && !fieldCode.equals("D_MODIF")) {
			 //Ajout dans l'historique

			 String commentEvent = fieldCode+" : "+oldValue+" :"+newValue;
			 getAuditMgr().addDocumentEvent(userContext, airsDocument, AdvancedAuditType.ADV_EVENT_FIELDCHANGE.name(), commentEvent);

		 }
	 }
  }
  
  /**
  * getListItemValueFromId : Conversion d'un id en item de liste (fonctionne pour les listes d'autorit ©s, les listes d'organisations et les listes d'utilisateurs)
  */
 private static String getListItemValueFromId(int id, int listType) {
	 
	 String itemValue=null;
	 if(id==-1) {
		 return "";
	 }
	 //Cas d'une liste d'autorit ©
	 if(listType.equals(IField.REFERENCE_TYPE_AUTHORITY)) {
		 
		 Term term = ((IAuthorityList)getAuthorityListService()).getTerm(id);
		 itemValue = term.getPreferedValue();
	 }
	 //Cas d'une liste d'utilisateurs
	 else if(listType.equals(IField.REFERENCE_TYPE_USER)) {
		
		 User airsUser = getUserMgr().getUser(id);
		 itemValue = airsUser.getFirstName()+" "+airsUser.getName();
	 }
	 //Cas d'une liste d'organisation
	 else if(listType.equals(IField.REFERENCE_TYPE_ORGANIZATION)) {
		
		 itemValue = getServerMgr().getOrganizationProperties(UserUtils.getAdminUserContext().getJeton(),id).getLabel();
	 }
	 
	 return itemValue;
 }
  
  private static com.digitech.dossier.common.service.impl.AuditMgr getAuditMgr() {
	  return (com.digitech.dossier.common.service.impl.AuditMgr) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AUDIT_DOC_MGR);
  }

  private static IDocumentConvertionService getDocumentConversionService() {
    IDocumentConvertionService docConversionService = new DocumentConvertionService();
    return docConversionService;
  }

  private static IDocumentGenerationService getDocumentGenerationService() {
    IDocumentGenerationService docGenerationService = new DocumentGenerationService();
    return docGenerationService;
  }

  private static IDocumentInspectorService getDocumentInspectorService() {
    IDocumentInspectorService docInspetorService = new DocumentInspectorService();
    return docInspetorService;
  }

  private static IDocumentMergingService getDocumentMergeService() {
    IDocumentMergingService odtMergingService = new DocumentMergingService();
    return odtMergingService;
  }

  private static IDocumentMergingService getDocumentPdfMergeService() {
    IDocumentMergingService odtMergingService = new com.digitech.report.service.impl.pdf.DocumentMergingService();
    return odtMergingService;
  }

  private static IDocumentInspectorService getDocumentPdfInspectorService() {
    IDocumentInspectorService odtMergingService = new com.digitech.report.service.impl.pdf.DocumentInspectorService();
    return odtMergingService;
  }

  private static IOperationService getDocumentTiffConvectorService() {
    IOperationService tifOperationService = new com.digitech.toolbox.document.service.impl.tiff.TIFFOperationService();
    return tifOperationService;
  }

  private static IOdtGenerator getOdtGeneration()  {
    IOdtGenerator OdtGeneratorService = new OdtGenerator();
    return OdtGeneratorService;
  }

  private static IRight getRightService() {
    return (IRight) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_RIGHT_MGR);
  }

  private static IUser getUserMgr() {
    return (IUser) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_USER_MGR);
  }

  private static IServer getServerMgr() {
    return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
  }

  private static com.digitech.dossier.common.service.IDocument getDocumentMgr() {
    return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
  }
  
}
