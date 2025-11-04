import org.slf4j.Logger

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.search.SearchResultTableRowModel;

import static CourrierScriptUtils;

Logger log = scriptLogger;
UserCoreContext usrContext = userContext;

log.debug("Script triggered on init custom search action: initRemoveFromCopy_SimpleView.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

String witdhModalPanel = CourrierScriptUtils.getConstant("CUSTOM_PANEL_MARK_AS_READ_WIDTH");
String heightModalPanel = CourrierScriptUtils.getConstant("CUSTOM_PANEL_MARK_AS_READ_HEIGHT");
customActionModel.setModalPanelWidth(Integer.parseInt(witdhModalPanel));
customActionModel.setModalPanelHeight(Integer.parseInt(heightModalPanel));

List<SearchResultTableRowModel> selectedRows = Utils.getSearchResultTableController().getModel().getSelectedRows();
int numberDocument=0;
for(SearchResultTableRowModel row : selectedRows){
  com.digitech.dossier.common.model.backend.airs.IDocument document = row.getDocument();
  if(CourrierScriptUtils.canMarkAsRead(usrContext, document) && CourrierScriptUtils.isDocumentAvailableForUser(usrContext, document)
      && !CourrierScriptUtils.alreadyMarkAsRead(usrContext, document)){
    numberDocument++;
  }
}

customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelMarkAsRead_titleSimpleView"));

if(numberDocument==0){
  customActionModel.setModalPanelButtonOkRendered(false);
  customActionModel.getModalPanelModel().put("textBodyModal", BundleUtils.getTranslation("modalPanelMarkAsRead_noDocument"));
}else{
  Object[] params = new Object[1];
  customActionModel.getModalPanelModel().put("textBodyModal", BundleUtils.getTranslation("modalPanelMarkAsRead_numberDocuments", Integer.valueOf(numberDocument)));
}

log.debug("Script triggered on init custom search action: initRemoveFromCopy_SimpleView.groovy --- End");