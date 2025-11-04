import java.util.*;
import org.apache.commons.lang.*;
import org.slf4j.Logger;
import com.digitech.courrier.common.model.backend.CourrierConstants;
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.params.CourrierOrga;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty;
import com.digitech.jcorbairs.Term;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.ServerException;

import static CourrierScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: U_VALIDEUR_init_read_courrierIn.groovy --- Start");

// regarder le type du courrier
Integer value = (Integer) theDocument.getField(CourrierScriptUtils.getConstant("FIELD_CODE_T_TYPE")).getValue();
if (value != null) {
  try {
    // On récupère le type du courrier
    Term term = CourrierScriptUtils.getAuthorityListService().getTerm(value);
    if (term != null) {
      com.digitech.dossier.common.model.backend.params.CourrierType courrierType = DossierCoreContext.getCourrierInfos().getCourrierType(usrContext.getCurrentOrgId(), term.getCode(), FlowType.IN);
      if (courrierType != null) {
        // On regarde si un validator est requis
        theOutput.getValue().getProperties().put(FieldProperty.REQUIRED, courrierType.isValidatorRequired().toString());
      }
    }
  } catch (IdentificationException e) {
    logger.error(e.getMessage(),e);
  } catch (ServerException e) {
    logger.error(e.getMessage(),e);
  }
}

List states = new ArrayList();
states.add(CourrierScriptUtils.getConstant("STATE_CODE_DIFFUSE"));
states.add(CourrierScriptUtils.getConstant("STATE_CODE_REPONDU"));
states.add(CourrierScriptUtils.getConstant("STATE_CODE_CLOTURE"));
states.add(CourrierScriptUtils.getConstant("STATE_CODE_A_VALIDER"));

if( CourrierScriptUtils.hasState(usrContext, theDocument, states) && !UserUtils.hasProfile(usrContext, CourrierScriptUtils.getConstant("PROFILE_CODE_ADMIN")) ) {
  theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
}

logger.debug("Script field initialization: U_VALIDEUR_init_read_courrierIn.groovy --- End");