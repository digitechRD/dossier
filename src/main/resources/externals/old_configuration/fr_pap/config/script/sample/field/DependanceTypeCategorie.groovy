import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.ServiceManager
import org.apache.commons.lang3.StringUtils

import javax.faces.component.UIComponent
import javax.faces.component.html.HtmlSelectOneMenu
import javax.faces.model.SelectItem

DOC_CAT_FIELD_CODE = "RH_DOC_CAT"
DOC_TYPE_DOC_FIELD_CODE = "RH_TYPE_DOC"

DOC_CAT_A_ID = "706"
DOC_CAT_A_CODE = "A"

DOC_CAT_B_ID = "707"
DOC_CAT_B_CODE = "B"

DOC_CAT_C_ID = "708"
DOC_CAT_C_CODE = "C"

DOC_CAT_D_ID = "709"
DOC_CAT_D_CODE = "D"

DOC_CAT_E_ID = "710"
DOC_CAT_E_CODE = "E"

DOC_CAT_F_ID = "711"
DOC_CAT_F_CODE = "F"

DOC_CAT_G_ID = "712"
DOC_CAT_G_CODE = "G"

DOC_CAT_H_ID = "713"
DOC_CAT_H_CODE = "H"

DOC_CAT_I_ID = "714"
DOC_CAT_I_CODE = "I"

DOC_CAT_J_ID = "715"
DOC_CAT_J_CODE = "J"


IField fieldCat_Doc = fieldMap.get(DOC_CAT_FIELD_CODE);
String fieldCatDocvalue = getFieldValue(fieldCat_Doc);

IField fieldType_Doc = fieldMap.get(DOC_TYPE_DOC_FIELD_CODE);

scriptLogger.warn("Valeur de la categorie : " + fieldCatDocvalue);
List<SelectItem> selectItems = SelectItemFactory.getInstance().getAuthorities(fieldType_Doc.getConfigField());

String sLettre;
String sColor = "green";

switch( fieldCatDocvalue ) {
  case DOC_CAT_A_ID:
    sLettre = DOC_CAT_A_CODE;
    sColor = "#CC6699";
    break;

  case DOC_CAT_B_ID:
    sLettre = DOC_CAT_B_CODE;
    sColor = "blue";
    break;

  case DOC_CAT_C_ID:
    sLettre = DOC_CAT_C_CODE;
    break;

  case DOC_CAT_D_ID:
    sLettre = DOC_CAT_D_CODE;
    break;

  case DOC_CAT_E_ID:
    sLettre = DOC_CAT_E_CODE;
    break;

  case DOC_CAT_F_ID:
    sLettre = DOC_CAT_F_CODE;
    break;

  case DOC_CAT_G_ID:
    sLettre = DOC_CAT_G_CODE;
    break;

  case DOC_CAT_H_ID:
    sLettre = DOC_CAT_H_CODE;
    break;

  case DOC_CAT_I_ID:
    sLettre = DOC_CAT_I_CODE;
    break;

  case DOC_CAT_J_ID:
    sLettre = DOC_CAT_J_CODE;
    break;

  default:
    sLettre = "";
    break;
}

Iterator<SelectItem> iter = selectItems.iterator();
while(iter.hasNext()) {
  SelectItem ItemElement = iter.next();
  String strLabel = ItemElement.getLabel();

  if(StringUtils.isNotEmpty(strLabel)) {
    scriptLogger.warn("Item : " + strLabel);

    String PremLettre = strLabel.substring(0, 1);
    scriptLogger.warn("PremLettre : " + PremLettre);

    if(PremLettre.compareToIgnoreCase(sLettre) == 0) {
      //on fait rien
      scriptLogger.warn("Je fais rien ");
    }
    else {
      iter.remove();
      scriptLogger.warn("Je supprime ");
    }
  }
}

HtmlSelectOneMenu component = ((HtmlSelectOneMenu) ((UIComponent) fieldType_Doc.getComponent()));
component.getChildren().get(0).setValue(selectItems);
component.getAttributes().put("style", "background-color: " + sColor);


String getFieldValue(IField field) {
  List<?> values = field.getValues();
  String fieldvalue;
  if(values != null && !values.isEmpty()) {
    fieldvalue = getServerMgr().getFieldValues(values, field.getAirsField());
    scriptLogger.info("Field Value : " + field.getCode() + " - " + fieldvalue);
  }
  else {
    scriptLogger.info("the field value is null or empty");
  }

  return fieldvalue;
}

private IServer getServerMgr() {
  return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument)
      ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}
