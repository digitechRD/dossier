import org.slf4j.Logger

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.search.SearchResultTableRowModel;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import static CourrierScriptUtils;

Logger log = scriptLogger;
UserCoreContext usrContext = userContext;

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_validateSimpleView.groovy --- Start");

CustomActionModel customActionModel = Utils.getCustomActionController().getModel();

String witdhModalPanel = CourrierScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_WIDTH");
String heightModalPanel = CourrierScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_HEIGHT");
customActionModel.setModalPanelWidth(Integer.parseInt(witdhModalPanel));
customActionModel.setModalPanelHeight(Integer.parseInt(heightModalPanel));

List<SearchResultTableRowModel> selectedRows = Utils.getSearchResultTableController().getModel().getSelectedRows();
int documentValidable=0;
for(SearchResultTableRowModel row : selectedRows){
  IDocument theDocument = row.getDocument();
  if(CourrierScriptUtils.canValidate(theDocument, userContext)){
    documentValidable++;
  }
}

if(documentValidable==0){
  customActionModel.setModalPanelButtonOkRendered(false);
  customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelValidate_titleNoDocumentToValidate"));
}else{
  Object[] params = new Object[1];
  customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelValidate_titleDocumentsToValidate", Integer.valueOf(documentValidable)));
  customActionModel.getModalPanelModel().put("documentValidable", Integer.valueOf(documentValidable));
}

log.debug("Script triggered on init custom Modal Panel: initCustomModalPanel_validateSimpleView.groovy --- End");