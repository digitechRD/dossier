import com.digitech.dossier.common.model.backend.airs.IField
import org.apache.commons.lang.StringUtils

import javax.faces.component.UIComponent

IField field01 = fieldMap.get("N_COUR")
IField field02 = fieldMap.get("N_PIECE")
if(updatedField.getValue().equals("0")) {
  ((UIComponent) field01.getComponent()).getAttributes().put("disabled", Boolean.TRUE)
  String styleClass = (String) ((UIComponent) field01.getComponent()).getAttributes().get("styleClass")
  ((UIComponent) field01.getComponent()).getAttributes().put("styleClass", styleClass + " disabled")

  ((UIComponent) field02.getComponent()).getAttributes().put("style", "background-color: green")
}
else {
  ((UIComponent) field01.getComponent()).getAttributes().put("disabled", Boolean.FALSE)
  String styleClass = (String) ((UIComponent) field01.getComponent()).getAttributes().get("styleClass")
  ((UIComponent) field01.getComponent()).getAttributes().put("styleClass", StringUtils.removeEnd(styleClass, " disabled"))

  ((UIComponent) field02.getComponent()).getAttributes().put("style", null)
}
field01.setValue("1 : previous value is " + updatedField.getValue())
field02.setValue("2 : previous value is " + updatedField.getValue())
