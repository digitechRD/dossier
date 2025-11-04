import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer;
import com.digitech.dossier.script.model.impl.result.ScriptResultModel;
import java.util.*

import org.apache.commons.lang.*
import org.slf4j.Logger

import com.digitech.courrier.common.model.backend.CourrierConstants
import com.digitech.courrier.common.model.backend.CourrierConstants.FlowType
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.params.CourrierOrga
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer
import com.digitech.dossier.script.model.impl.result.ScriptResultValueFieldInitializer.FieldProperty
import com.digitech.jcorbairs.Term
import com.digitech.jcorbairs.exception.IdentificationException
import com.digitech.jcorbairs.exception.ServerException

import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.dossier.common.utils.DateUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import static ScriptUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

PROFIL_ADMIN_CODE = "PF_ADMIN"
PROFIL_COMPTA_CODE = "PF_COMPTABLE"
PROFIL_ACHAT_CODE = "PF_ACHAT"

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: facture_init_readOnlyIfNo_AdminComptaAchat.groovy --- Start");

if(!UserUtils.hasProfile(usrContext, PROFIL_ADMIN_CODE)) {

  boolean IsUserHaveProfilCompta = UserUtils.hasProfile(usrContext, PROFIL_COMPTA_CODE);
  boolean IsUserHaveProfilAchat = UserUtils.hasProfile(usrContext, PROFIL_ACHAT_CODE);
 
  if(!IsUserHaveProfilCompta && !IsUserHaveProfilAchat ) {
    theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
  }

}
logger.debug("Script field initialization: facture_init_readOnlyIfNo_AdminComptaAchat.groovy --- End");

