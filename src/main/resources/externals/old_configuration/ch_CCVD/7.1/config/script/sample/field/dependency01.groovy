import org.apache.commons.lang.StringUtils

import javax.faces.component.UIComponent

if(updatedField.getValue().equals("0")) {
  ((UIComponent) fieldToUpdate.getComponent()).getAttributes().put("disabled", Boolean.TRUE)
  String styleClass = (String) ((UIComponent) fieldToUpdate.getComponent()).getAttributes().get("styleClass")
  ((UIComponent) fieldToUpdate.getComponent()).getAttributes().put("styleClass", styleClass + " disabled")
}
else {
  ((UIComponent) fieldToUpdate.getComponent()).getAttributes().put("disabled", Boolean.FALSE)
  String styleClass = (String) ((UIComponent) fieldToUpdate.getComponent()).getAttributes().get("styleClass")
  ((UIComponent) fieldToUpdate.getComponent()).getAttributes().put("styleClass", StringUtils.removeEnd(styleClass, " disabled"))
}
fieldToUpdate.setValue("Previous value is " + updatedField.getValue())
