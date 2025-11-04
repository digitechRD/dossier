import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType
import com.digitech.dossier.common.model.backend.airs.IAttachment
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.service.ServiceUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.script.model.IScriptResultValueModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer
import com.digitech.jcorbairs.Token
import org.apache.commons.collections4.CollectionUtils

// === Variables ===
boolean bRetour = true
String nextEtapeLabel = null
String etapeAvantLabel = null


_scriptLogger.info(">>> after_otpSignatureAccepted")


// Récupération des informations de la fiche
def etapeCode = ScriptUtilsProject.getTermCode(
    "MSC_TYPE_DOCUMENT", 
    FieldUtils.getValue(document,"MSC_TYPE_DOCUMENT")
)
// Récupération des informations de la fiche
//ScriptUtilsProject.getTermCode("MSC_ETAPE_EN_COURS",FieldUtils.getValue(_document,"MSC_TYPE_DOCUMENT"));


etapeAvantLabel = ScriptUtilsProject.getTermValue("MSC_ETAPE_EN_COURS", etapeCode, 0)



//FieldUtils.setValue(document, "MSC_TYPE_DOCUMENT", "${ScriptUtilsProject.getTermID("MSC_ETAPE_EN_COURS", nextEtape)}")
//FieldUtils.setValue(_document, "MSC_TYPE_DOCUMENT", "${ScriptUtilsProject.getTermID("MSC_TYPE_DOCUMENT", "Dossier validé et signé")}")	
FieldUtils.setValue(document, "MSC_TYPE_DOCUMENT", "${ScriptUtilsProject.getTermID("MSC_TYPE_DOCUMENT", "Dossier validé et signé")}");
//_document.setFieldValue(<STATUS>, <NEW_VALUE>)
ServiceUtils.getDocumentService().updateDocument(_userContext, _document)

_scriptLogger.info("<<< after_otpSignatureAccepted")