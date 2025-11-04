/** Fichier : documentRanking_dependency.groovy
*   Auteur  : JMU
*   Date  : 18/02/14
*   Description     : Filtre la liste du plan de classement en fonction du classeur selectionné
*   Paramètres d'entrée : 
*   - scriptLogger
*	- userContext
*   - IField updatedField : le champ de référence pour la dépendance => GEN_DOS_PCLASS
*   - IField fieldToUpdate : le champ à mettre a jour => GEN_ROOT_NOM
*/
import org.apache.commons.lang.*;

import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.IAuthorityList
import com.digitech.dossier.common.utils.*;
import com.digitech.jcorbairs.*;

import javax.faces.component.html.*;
import javax.faces.component.*;
import javax.faces.model.*;


import GenScriptUtils;
String SCRIPT_NAME="documentRanking_dependency";

scriptLogger.debug("Script groovy de type dépendance : "+SCRIPT_NAME+" --- Start");
IDocument currentDocument = userContext.getCurrentDocument();

//Récupération du code du classeur
Integer rankingId = updatedField.getValue();
String rankingCode;
try {
  Term term = ((IAuthorityList)GenScriptUtils.getAuthorityListService()).getTerm(rankingId);
  
  if(term != null)
  {
  	rankingCode = term.getCode();
    if (fieldToUpdate.getComponent() != null && fieldToUpdate.getComponent() instanceof HtmlSelectOneMenu){
    	HtmlSelectOneMenu component = ((HtmlSelectOneMenu) fieldToUpdate.getComponent());
    	component.getChildren().get(0).setValue(GenScriptUtils.getSelectItemsFromParent(rankingCode, fieldToUpdate));
    }
  }
}
catch(Exception e) {
  scriptLogger.error(e.getLocalizeMessage(), e);
}

scriptLogger.debug("Script groovy de type dépendance : "+SCRIPT_NAME+" --- End  "); 