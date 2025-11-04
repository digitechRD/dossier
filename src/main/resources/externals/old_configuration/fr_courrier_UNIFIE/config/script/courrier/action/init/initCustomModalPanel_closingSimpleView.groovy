import org.slf4j.Logger

import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.resources.BundleUtils

Logger log = scriptLogger;

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_closingSimpleView.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
String witdhModalPanel = CourrierScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_WIDTH");
String heightModalPanel = CourrierScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_HEIGHT");
customActionModel.setModalPanelWidth(Integer.parseInt(witdhModalPanel));
customActionModel.setModalPanelHeight(Integer.parseInt(heightModalPanel));

Object[] params = new Object[1];
params[0] = Integer.valueOf(Utils.getSearchResultTableController().getModel().getSelectedRows().size());
customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelClosing_titleSimpleView", params));

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_closingSimpleView.groovy --- Start");