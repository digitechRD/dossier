import java.lang.*;
import java.util.*;
import javax.faces.*;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.apache.commons.lang.*;

import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.Utils;

import com.digitech.dossier.common.*;

import java.lang.Double;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;

import com.digitech.dossier.common.model.backing.factory.*;
import javax.faces.component.html.*;
import javax.faces.component.*;
import javax.faces.model.*;

import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.jcorbairs.Term;

/** Fichier : DependanceSousCategorie.groovy
* 	Auteur  : JMU
* 	Date 	: 23/05/13
* 	But     : Filtre une liste d'autorité sur la valeur de la clé de tri en fonction de la valeur du champ Catégorie
*/

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'userContext
// IField updatedField le champ de référence pour la dépendance  => RH_CATEGORIE
// IField fieldToUpdate le champ à mettre a jour				 => RH_SOUS_CATEGORIE
/************************************************/
SCRIPT_NAME = "DependanceSousCategorie.groovy";

scriptLogger.info("Lancement du script : "+SCRIPT_NAME); 

//Récupération de la catégorie
String fieldCatDocvalue = getFieldValue( updatedField );

scriptLogger.debug("Valeur de la categorie : " + fieldCatDocvalue );

String catSortKey;

if(fieldCatDocvalue!=null)
{
	//Récupération de la clé de tri de la catégorie
	Term termCat = getAuthorityListService().getTerm((Integer) updatedField.getValue());
	catSortKey = termCat.getSortKey();
	if(catSortKey!=null)
	{
		//on recupère la liste des items pour les sous categories
		List<SelectItem> selectItems = new SelectItemFactory().getAuthorities(fieldToUpdate.getCode());
		//Parcours des sous-catégories (début du filtrage)
		Iterator<SelectItem> iter = selectItems.iterator();
		
		 while (iter.hasNext()) 
		{
			SelectItem ItemElement = iter.next();
			if(ItemElement.getValue()!=null)
			{
				scriptLogger.info("ItemElement value => "+ItemElement.getValue());
				scriptLogger.info("ItemElement label => "+ItemElement.getLabel());
				Term termSousCat = getAuthorityListService().getTerm((Integer) ItemElement.getValue());
				String sousCatSortKey = termSousCat.getSortKey();
				
				if(sousCatSortKey!=null)
				{	
					//Récupération des 2 premières lettres de la clé de tri
					sousCatSortKey = sousCatSortKey.substring(0,2);
					scriptLogger.info("Comparaison des clés de tri : [sousCatSortKey] => " + sousCatSortKey + " et [catSortKey] => "+catSortKey);
					//Comparaison des 2 premieres lettres de la clé de tri de la sous-catégorie avec la clé de tri de la categorie
					if( sousCatSortKey.compareToIgnoreCase(catSortKey) != 0 )
					{
						scriptLogger.info("On filtre la sous-catégorie");
						iter.remove();
					}				
				}
			}
		}
		HtmlSelectOneMenu component = ((HtmlSelectOneMenu)  ((UIComponent)fieldToUpdate.getComponent()));
		component.getChildren().get(0).setValue(selectItems );
	}
}

scriptLogger.info("Fin du script : "+SCRIPT_NAME);

String getFieldValue( IField field )
{
	List<?> values = field.getValues();
	String fieldvalue;
	if(values != null && !values.isEmpty()) {
		fieldvalue =getServerMgr().getFieldValues(values, field.getAirsField());   
		scriptLogger.info("Field Value : " + field.getCode() + " - " + fieldvalue );
	}else
	{
		scriptLogger.info("the field value is null or empty");
	}
	
	return fieldvalue;
}

private IServer getServerMgr() {
	return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

  /**
   * @return IAuthorityList the Authority List
   */
  public static IAuthorityList getAuthorityListService() {
    return (IAuthorityList) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_AUTHORITYLIST_MGR);
  }