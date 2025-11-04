import org.slf4j.Logger;

import com.digitech.courrier.common.model.VisaModel;
import com.digitech.courrier.common.utils.CourrierUtils;
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backing.attachment.AttachmentModel;
import com.digitech.dossier.common.resources.BundleUtils;

import static CourrierScriptUtils;

Logger log = scriptLogger;

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_visa.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
VisaModel visaModel = CourrierUtils.getVisaController().getModel();
visaModel.clear();

Integer witdhModalPanel = Integer.parseInt(CourrierScriptUtils.getConstant("CUSTOM_PANEL_VISA_WIDTH"));
if (visaModel.getAvailableAttachmentsIn().isEmpty()) {
  witdhModalPanel = (witdhModalPanel / 2) + 50;
}
Integer heightModalPanel = Integer.parseInt(CourrierScriptUtils.getConstant("CUSTOM_PANEL_VISA_HEIGHT"));

customActionModel.setModalPanelWidth(witdhModalPanel);
customActionModel.setModalPanelHeight(heightModalPanel);
customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelVisa_title"));
customActionModel.setCancelReRender(AttachmentModel.FORM_ID + ":" + CustomActionController.ID_ATTACHMENT_LAYOUT_UNIT_CONTENT);

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_visa.groovy --- End");