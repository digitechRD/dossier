import groovy.util.ScriptException

import java.io.File
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*

import java.text.ParsePosition;

import org.apache.commons.lang.StringUtils

import com.digitech.airs3dossiers.constantes.DAirsDossierStringConstants
import com.digitech.courrier.common.utils.CourrierUtils
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.report.value.airs.ReportPerson;
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backing.DefaultSharingModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.service.Constants
import com.digitech.dossier.common.service.IAuditService
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.service.ICounter
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.IUser
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.dossier.common.utils.DateUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.service.impl.ScriptMgr
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.User;
import com.digitech.jcorbairs.admin.OrganizationAdmin
import com.digitech.jcorbairs.admin.OrganizationsManager
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException


/**
 * Utility methods for Dossier La Provence
 */
class LaProvenceScriptUtils {
  private static File constantsFile = null;
  private static Properties properties = new Properties();
  static {
    constantsFile = new File(DossierCoreContext.getApplicationPath() + File.separator + ScriptMgr.SCRIPT_RELATIVE_PATH  + File.separator + "global"  + File.separator + "constants.properties");
    constantsFile.withInputStream {  stream -> properties.load(stream) }
  }

  public static String getConstant(String code) {
    if (!properties.containsKey(code)) {
      throw new ScriptException("Key '" + code + "' not found in file " + constantsFile);
    }
    return properties.getProperty(code);
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
  public static String getTermCode(String fieldCode, Integer termId)
  throws IdentificationException, ServerException {
   List<Term> termList = getAuthorityListService().getTerms(UserUtils.getAdminUserContext().getJeton(), fieldCode);
   for(Term term : termList) {
     if(term.getId().equals(termId)) {
       return term.getCode();
     }
   }
   return -1;
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
    List<Term> termList = getAuthorityListService().getTerms(UserUtils.getAdminUserContext().getJeton(), fieldCode);
    for(Term term : termList) {
      if(term.getCode().equals(termCode)) {
        return term.getId();
      }
    }
    return -1;
  }
  
  /**
  * Gets a term ID.
  * @param sDate the date to format
  * @return d the Date formatted
  */
  public static Date stringToDate(String sDate) {
    Date d = null;
    try {
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
      ParsePosition pos = new ParsePosition(0);
      d = formatter.parse(sDate, pos);
    }
    catch(RuntimeException e) {
      e.printStackTrace();
    }
    return d;
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
  
  /**
  * @return IAuthorityList the Authority List
  */
 public static IAuthorityList getAuthorityListService() {
   return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
 }
}