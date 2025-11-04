import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger

import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.model.backend.airs.ILocutionModel
import com.digitech.dossier.common.model.backend.airs.ISearchModel
import com.digitech.dossier.common.model.backend.airs.ITask
import com.digitech.dossier.common.model.backend.airs.ILocutionModel.Operator
import com.digitech.dossier.common.model.backend.airs.impl.LocutionModel
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.dossier.common.service.ServiceConstants

import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import com.digitech.dossier.workflow.model.impl.WFTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.common.framework.bdd.DBConnectionManager;

import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.jcorbairs.Term;

import static LaProvenceScriptUtils;

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document courant
/************************************************/

// Input parameters
UserContext usrContext = userContext;
IDocument theDocument = document;
Logger log = scriptLogger;


log.debug("Script triggered onDeleteFacBefore : beforeDelete_unlockEng.groovy --- Start");

FAC_TYPE_DOC_FIELD_CODE = LaProvenceScriptUtils.getConstant("FAC_TYPE_DOC_FIELD_CODE");
ENG_NUM_FIELD_CODE = LaProvenceScriptUtils.getConstant("ENG_NUM_FIELD_CODE");
AL_FAC_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_FIELD_CODE");
AL_FAC_DUP_FIELD_CODE = LaProvenceScriptUtils.getConstant("AL_FAC_DUP_FIELD_CODE");
AL_AVOIR_FIELD_CODE  = LaProvenceScriptUtils.getConstant("AL_AVOIR_FIELD_CODE");

// On vérifie qu'on n'est pas dans le cas d'un contrat avant d'éxécuter le script
String facTypeDoc = theDocument.getField(FAC_TYPE_DOC_FIELD_CODE).getValue();
if (String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_FIELD_CODE)).compareTo(facTypeDoc) == 0 ||
String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_FAC_DUP_FIELD_CODE)).compareTo(facTypeDoc) == 0||
String.valueOf(LaProvenceScriptUtils.getTermID(FAC_TYPE_DOC_FIELD_CODE, AL_AVOIR_FIELD_CODE)).compareTo(facTypeDoc) == 0){

String ENG_NUM_CODE = LaProvenceScriptUtils.getConstant("ENG_NUM_CODE");
String ENG_VERROU_CODE = LaProvenceScriptUtils.getConstant("ENG_VERROU_CODE");
String D_DOC_ENG_CODE = LaProvenceScriptUtils.getConstant("D_DOC_ENG_CODE");
String YES_CODE = LaProvenceScriptUtils.getConstant("YES_CODE");
String NO_CODE = LaProvenceScriptUtils.getConstant("NO_CODE");

String engNumValue = FieldUtils.getValue(theDocument, ENG_NUM_CODE);

if (StringUtils.isNotBlank(engNumValue)){
  //on vérifie qu'il existe un engagement avec ce numéro
  ILocutionModel locutionModel = new LocutionModel();
  DocumentUtils.buildLocutionModel(locutionModel, ENG_NUM_CODE, Operator.OPERATOR_VALUE_EQUAL, engNumValue);

  // Compute search for the COURRIER_IN
  List<IDocument> documentList = DocumentUtils.search(UserUtils.getAdminUserContext(), locutionModel, DocumentUtils.getSearchContentTypeList(D_DOC_ENG_CODE), null);
  if (documentList != null && documentList.size() == 1){
    log.debug("Script triggered onDeleteFacBefore : un engagement a ete trouve");
    for (IDocument docEng : documentList) {
      // On regarde si l'engagement est vérouillé

      String verrou = LaProvenceScriptUtils.getTermCode(ENG_VERROU_CODE, docEng.getField(ENG_VERROU_CODE).getValue());
      if (verrou == null || YES_CODE.compareTo(verrou) == 0) {
        log.debug("Script triggered onDeleteFacBefore : deverouillage de l engagement");
        // on vérouille le document
        docEng.getField(ENG_VERROU_CODE).setValue(LaProvenceScriptUtils.getTermID(ENG_VERROU_CODE, NO_CODE));
        // on sauvegade l'engagement
        com.digitech.dossier.common.service.IDocument documentMgr = (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_AIRS_DOCUMENT_MGR);
        documentMgr.updateDocument(usrContext, docEng, false);
      }
      else {
        log.debug("Script triggered onDeleteFacBefore : engagement non verouille");
      }
    }
  }
  else {
    log.debug("Script triggered onDeleteFacBefore : aucun engagement trouve");
  }
}
ScriptResultValueChecker result = new ScriptResultValueChecker();
result.setValid(true);
output.setValue(result);
} else {
  ScriptResultValueChecker result = new ScriptResultValueChecker();
  result.setValid(true);
  output.setValue(result);
}
log.debug("Script triggered onDeleteFacBefore : beforeDelete_unlockEng.groovy --- End");

