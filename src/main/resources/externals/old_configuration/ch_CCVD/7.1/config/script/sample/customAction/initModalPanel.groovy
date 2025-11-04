import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.UserContext

CustomActionController customActionController = Utils.getCustomActionController()
Map<String, Object> data = customActionController.getModel().getModalPanelModel()
data.put("user", UserContext.getInstance().getUserFullName())