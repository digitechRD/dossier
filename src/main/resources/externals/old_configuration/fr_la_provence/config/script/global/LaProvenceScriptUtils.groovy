import java.text.SimpleDateFormat;

import java.text.ParsePosition;

import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.service.ServiceConstants
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.service.impl.ScriptMgr
import com.digitech.jcorbairs.Term
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
      scriptLogger.error(e.getLocalizedMessage(), e);
    }
    return d;
  }

  /**
  * @return IAuthorityList the Authority List
  */
 public static IAuthorityList getAuthorityListService() {
   return (IAuthorityList) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_AUTHORITYLIST_MGR);
 }
}