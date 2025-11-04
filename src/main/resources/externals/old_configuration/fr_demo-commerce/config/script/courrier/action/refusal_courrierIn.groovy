import com.digitech.courrier.common.model.backend.CourrierAdvancedAuditType
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backing.AbstractSharingModel;
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.NavigationUtils

import static CourrierScriptUtils

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("Script triggered on refusal : refusal_courrierIn.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

// Mise à jour de l'état du document
String fieldCode = CourrierScriptUtils.getConstant("FIELD_CODE_T_ETAT_COURRIER");
FieldUtils.setValue(theDocument, fieldCode, CourrierScriptUtils.getTermID(theDocument, fieldCode, CourrierScriptUtils.getConstant("STATE_CODE_REJECT") ));
CourrierScriptUtils.saveDocument(usrContext, theDocument, CourrierAdvancedAuditType.ADV_EVENT_COURRIER_REFUSED, true);

// Ajout du commentaire
AbstractSharingModel sharingModel = CourrierScriptUtils.getSharingModel((Boolean)customActionModel.getModalPanelModel().get("public"), theDocument);
DocumentUtils.addComment(theDocument, (String)customActionModel.getModalPanelModel().get("comment"), sharingModel);

// On affiche la page des résultats de recherche
Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, theDocument));

log.debug("Script triggered on refusal : refusal_courrierIn.groovy --- End");

