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

/** Fichier : DependanceTypeDocument.groovy
* 	Auteur  : JMU
* 	Date 	: 24/05/13
* 	But     : Filtre une liste d'autorité sur la valeur de la clé de tri en fonction de la valeur du champ sous-catégorie
*/

/********************* PARAM ********************/
// Logger scriptLogger le Logger
// UserContext userContext l'userContext
// IField updatedField le champ de référence pour la dépendance		=> RH_SOUS_CATEGORIE
// IField fieldToUpdate le champ à mettre a jour					=> RH_TYPEDOC
/************************************************/

SCRIPT_NAME = "DependanceTypeDocument.groovy";

scriptLogger.info("Lancement du script : "+SCRIPT_NAME);

//Récupération de la sous catégorie
String fieldSousCatDocvalue = getFieldValue( updatedField );

scriptLogger.debug("Valeur de la categorie : " + fieldSousCatDocvalue );

String sousCatSortKey;

if(fieldSousCatDocvalue!=null)
{
	//Récupération de la clé de tri de la sous-catégorie
	Term termSousCat = getAuthorityListService().getTerm((Integer) updatedField.getValue());
	sousCatSortKey = termSousCat.getSortKey();

	if(sousCatSortKey!=null)
	{
		//on recupère la liste des items pour les types de document
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
				Term termTypeDoc = getAuthorityListService().getTerm((Integer) ItemElement.getValue());
				String TypeDocSortKey = termTypeDoc.getSortKey();
				
				if(TypeDocSortKey!=null)
				{	
					//Récupération des 2 premières lettres de la clé de tri
					TypeDocSortKey = TypeDocSortKey.substring(0,4);
					scriptLogger.info("Comparaison des clés de tri : [TypeDocSortKey] => " + TypeDocSortKey + " et [sousCatSortKey] => "+sousCatSortKey);
					//Comparaison des 2 premières lettres de la clé de tri du type de document avec la clé de tri de la sous-categorie
					if( TypeDocSortKey.compareToIgnoreCase(sousCatSortKey) != 0 )
					{
						scriptLogger.info("On filtre le type de document");
						iter.remove();
					}
					
					HtmlSelectOneMenu component = ((HtmlSelectOneMenu)  ((UIComponent)fieldToUpdate.getComponent()));
					component.getChildren().get(0).setValue(selectItems);
				}
			}
		}
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