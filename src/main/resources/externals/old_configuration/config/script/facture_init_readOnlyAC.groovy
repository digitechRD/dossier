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
import com.digitech.dossier.common.utils.UserUtils

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'...userContext
// IDocument document le document
/************************************************/

PROFIL_ADMIN_CODE = "PF_ADMINISTRATEUR"   
PROFIL_ASSISTANTE_CODE = "PF_ASSISTANTE"
PROFIL_COMPT_CODE = "PF_COMPTABLE"

UserContext usrContext = userContext;
org.slf4j.Logger logger = scriptLogger;
IDocument theDocument = document;

ScriptResultModel<ScriptResultValueFieldInitializer> theOutput = output;

logger.debug("Script field initialization: readOnly_init_factureAC.groovy --- Start");

if(UserUtils.hasProfile(usrContext, PROFIL_ADMIN_CODE) || 
   UserUtils.hasProfile(usrContext, PROFIL_COMPT_CODE)) {
    theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "false");
}
else
{
    theOutput.getValue().getProperties().put(FieldProperty.READ_ONLY, "true");
}

logger.debug("Script field initialization: readOnly_init_factureAC.groovy --- End");

