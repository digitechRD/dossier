import org.slf4j.Logger

import com.digitech.courrier.common.model.ResponseModel;
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.resources.BundleUtils

import static CourrierScriptUtils

Logger log = scriptLogger;

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_response.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
ResponseModel responseModel = CourrierUtils.getResponseController().getModel();
responseModel.clear();

// Pregenerates the first template
CourrierUtils.getResponseController().generate();

Integer witdhModalPanel = Integer.parseInt(CourrierScriptUtils.getConstant("CUSTOM_PANEL_RESPONSE_WIDTH"));
if (responseModel.getAvailableAttachments().isEmpty()) {
  witdhModalPanel = (witdhModalPanel / 2) + 20;
}
Integer heightModalPanel = Integer.parseInt(CourrierScriptUtils.getConstant("CUSTOM_PANEL_RESPONSE_HEIGHT"));

customActionModel.setModalPanelWidth(witdhModalPanel);
customActionModel.setModalPanelHeight(heightModalPanel);
customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelWriteCourrier_title"));

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_response.groovy --- End");