import com.digitech.dossier.common.model.backend.airs.IField
import com.digitech.dossier.common.model.backend.params.abstracts.AbstractField
import com.digitech.dossier.common.model.backing.factory.SelectItemFactory
import com.digitech.dossier.common.service.Constants
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.jcorbairs.Term

import javax.faces.component.UIComponent
import javax.faces.component.html.HtmlSelectOneMenu
import javax.faces.model.SelectItem

/** Fichier  : DependanceTypeDocument.groovy
 * 	Auteur   : JMU
 * 	Date 	   : 07/02/17
 * 	But      : Filtre une liste d'autorité sur la valeur de la clé de tri en fonction de la valeur du champ sous-catégorie
 * 	Versions : 1.1 - 07/02/17 - [NFE]: code review, enable list to be reset (see #24812)
 * 	           1.0 - 24/05/13 - [JMU] : initial script
 */

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'userContext
// IField updatedField le champ de référence pour la dépendance		=> RH_SOUS_CATEGORIE
// IField fieldToUpdate le champ à mettre a jour					=> RH_TYPEDOC
/** **********************************************/

SCRIPT_NAME = "DependanceTypeDocument.groovy"

scriptLogger.debug("Lancement du script: {}", SCRIPT_NAME)

//Récupération de la sous catégorie
String fieldSousCatDocvalue = getFieldValue((IField) updatedField)

scriptLogger.debug("Valeur de la categorie: {}", fieldSousCatDocvalue)

List<SelectItem> selectItems = new ArrayList<>()
if (fieldSousCatDocvalue != null) {
  //Récupération de la clé de tri de la sous-catégorie
  Term termSousCat = getAuthorityListService().getTerm((Integer) updatedField.getValue())
  String sousCatSortKey = termSousCat != null ? termSousCat.getSortKey() : null

  if (sousCatSortKey != null) {
    //on récupère la liste des items pour les types de document
    selectItems = new SelectItemFactory().getAuthorities((AbstractField) fieldToUpdate.getConfigField())
    //Parcours des sous-catégories (début du filtrage)
    Iterator<SelectItem> iter = selectItems.iterator()

    while (iter.hasNext()) {
      SelectItem item = iter.next()
      if (item.getValue() != null) {
        scriptLogger.debug("item (value: '{}', label: '{}')", item.getValue(), item.getLabel())
        Term termTypeDoc = getAuthorityListService().getTerm((Integer) item.getValue())
        String typeDocSortKey = termTypeDoc != null ? termTypeDoc.getSortKey() : null

        if (typeDocSortKey != null && typeDocSortKey.length() >= 4) {
          //Récupération des 2 premières lettres de la clé de tri
          typeDocSortKey = typeDocSortKey.substring(0, 4)
          scriptLogger.debug("Comparaison des clés de tri : [typeDocSortKey] => {} et [sousCatSortKey] => {}", typeDocSortKey, sousCatSortKey)
          //Comparaison des 2 premières lettres de la clé de tri du type de document avec la clé de tri de la sous-categorie
          if (!typeDocSortKey.equalsIgnoreCase(sousCatSortKey)) {
            scriptLogger.debug("type de document supprimé (filtré) de la liste")
            iter.remove()
          }
        }
      }
    }
  }
}

// reset value
fieldToUpdate.resetValue()
fieldToUpdate.airsValue = null
// and update content
UIComponent comp = (UIComponent) fieldToUpdate.getComponent()
if (comp != null && comp instanceof HtmlSelectOneMenu) {
  HtmlSelectOneMenu component = (HtmlSelectOneMenu) comp
  component.getChildren().get(0).setValue(selectItems)
}

scriptLogger.debug("Fin du script: {}", SCRIPT_NAME)

String getFieldValue(IField field) {
  List<?> values = field.getValues()
  if (values != null && !values.isEmpty()) {
    String fieldvalue = getServerMgr().getFieldValues(values, field.getAirsField())
    scriptLogger.debug("Field code='{}' Value='{}'", field.getCode(), fieldvalue)
    return fieldvalue
  }

  scriptLogger.debug("the field value is null or empty")
  return null
}

private static IServer getServerMgr() {
  return (IServer) ServiceManager.getInstance().getService(Constants.SERVICE_AIRS_SERVER_MGR)
}

/**
 * @return IAuthorityList the Authority List
 */
static IAuthorityList getAuthorityListService() {
  return (IAuthorityList) ServiceManager.getInstance().getService(Constants.SERVICE_AIRS_AUTHORITYLIST_MGR)
}