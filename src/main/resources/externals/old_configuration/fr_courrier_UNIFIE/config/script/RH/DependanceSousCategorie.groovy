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

/** Fichier  : DependanceSousCategorie.groovy
 * 	Auteur   : JMU
 * 	Date 	   : 07/02/17
 * 	But      : Filtre une liste d'autorité sur la valeur de la clé de tri en fonction de la valeur du champ Catégorie
 * 	Versions : 1.1 - 07/02/17 - [NFE]: code review, enable list to be reset (see #24812)
 * 	           1.0 - 24/05/13 - [JMU]: initial script
 */

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'userContext
// IField updatedField le champ de référence pour la dépendance  => RH_CATEGORIE
// IField fieldToUpdate le champ à mettre a jour				 => RH_SOUS_CATEGORIE
/** **********************************************/
SCRIPT_NAME = "DependanceSousCategorie.groovy"

scriptLogger.debug("Lancement du script: {}", SCRIPT_NAME)

//Récupération de la catégorie
String fieldCatDocvalue = getFieldValue((IField) updatedField)

scriptLogger.debug("Valeur de la categorie: {}", fieldCatDocvalue)

List<SelectItem> selectItems = new ArrayList<>()

if (fieldCatDocvalue != null) {
  //Récupération de la clé de tri de la catégorie
  Term termCat = getAuthorityListService().getTerm((Integer) updatedField.getValue())
  String catSortKey = termCat != null ? termCat.getSortKey() : null
  if (catSortKey != null) {
    //on recupère la liste des items pour les sous categories
    selectItems = new SelectItemFactory().getAuthorities((AbstractField) fieldToUpdate.getConfigField())
    //Parcours des sous-catégories (début du filtrage)
    Iterator<SelectItem> iter = selectItems.iterator()

    while (iter.hasNext()) {
      SelectItem item = iter.next()
      if (item.getValue() != null) {
        scriptLogger.debug("item (value: '{}', label: '{}')", item.getValue(), item.getLabel())

        Term termSousCat = getAuthorityListService().getTerm((Integer) item.getValue())
        String sousCatSortKey = termSousCat != null ? termSousCat.getSortKey() : null

        if (sousCatSortKey != null && sousCatSortKey.length() >= 2) {
          //Récupération des 2 premières lettres de la clé de tri
          sousCatSortKey = sousCatSortKey.substring(0, 2)
          scriptLogger.debug("Comparaison des clés de tri : [sousCatSortKey] => {} et [catSortKey] => {}", sousCatSortKey, catSortKey)
          //Comparaison des 2 premieres lettres de la clé de tri de la sous-catégorie avec la clé de tri de la categorie
          if (!sousCatSortKey.equalsIgnoreCase(catSortKey)) {
            scriptLogger.debug("sous-catégorie (filtrée) supprimée de la liste")
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