import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger

import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.common.utils.DocumentUtils
import com.digitech.dossier.common.utils.NavigationUtils
import com.digitech.dossier.common.resources.BundleUtils
import com.digitech.dossier.common.controller.NavigationController;
import com.digitech.dossier.common.model.backing.CustomActionModel
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;
import com.digitech.dossier.common.model.backend.Constants.AdvancedAuditType;
import com.digitech.jcorbairs.Term;
import com.digitech.dossier.common.service.IAuthorityList;
import com.digitech.dossier.common.model.backend.Constants;
import com.digitech.jcorbairs.User
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.impl.Document;
import com.digitech.dossier.common.model.backend.airs.IField;

import static ScriptUtils

// param
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;
String facStatutMessage = "Attribué UG";

//log.debug("Script triggered on allocated UG : facture_allocatedUGUnitView.groovy --- Start");
log.debug("In facture_ChangerCdpActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

//On récupère le commentaire entré par l'utilisateur
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
String comment = customActionModel.getModalPanelModel().get("comment");


// chercher le CDP à Changer 1 ou 2 ou  3 FACT_USR_CTRLGEST1    FACT_USR_CTRLGEST2     FACT_USR_CTRLGEST3
// on le trouve en comparant les id de ctrl en place
int CurentUserId = usrContext.getUser().getId();
int oldUser1Id =  FieldUtils.getValue(theDocument, "FACT_USR_CDP1");
int oldUser2Id =  FieldUtils.getValue(theDocument, "FACT_USR_CDP2");
int oldUser3Id =  FieldUtils.getValue(theDocument, "FACT_USR_CDP3");
String FieldName;
String Name;
int nNrGest=0;

if(CurentUserId == oldUser1Id)   {
  nNrGest=1;
  FieldName = "FACT_USR_CDP1";
  Name = "Chef de projet 1";
}
else if(CurentUserId == oldUser2Id)   { 
  nNrGest=2;
  FieldName = "FACT_USR_CDP2";
  Name = "Chef de projet 2";
}
else if(CurentUserId == oldUser3Id)   { 
  nNrGest=3;
  FieldName = "FACT_USR_CDP3";
  Name = "Chef de projet 3";
}

if(  (nNrGest>0) && (comment != null && !comment.equals(""))){

  //On récupère les ID de l'orga traitante pour l'historique
  int oldUserId =  FieldUtils.getValue(theDocument, FieldName);
  int newUserId = Integer.valueOf(customActionModel.getModalPanelModel().get("selectedUser"));
    if(oldUserId== null) {
      oldUserId=-1;
    }
  
  
  //Changement du user Achat
  FieldUtils.setValue(theDocument, FieldName, newUserId);
  
  //Save the document
  DocumentUtils.saveDocument(theDocument);
  
  //Update pour le user
  String  oldValue = ScriptUtils.getListItemValueFromId(oldUserId, IField.REFERENCE_TYPE_USER);
  String newValue = ScriptUtils.getListItemValueFromId(newUserId, IField.REFERENCE_TYPE_USER);
  ScriptUtils.addHistoWrkNewUser(theDocument, usrContext, "Changement "+Name,  oldValue, newValue);
  
  //Gestion de l'historique pour le statut
  //String oldValue = ScriptUtils.getListItemValueFromId(oldStatus, IField.REFERENCE_TYPE_AUTHORITY);
  //Term term = ((IAuthorityList)ScriptUtils.getAuthorityListService()).getTerm(ScriptUtils.getTermID(theDocument, fieldCodeStatus, ScriptUtils.getConstant("CODE_ATTRIB_UG")));
  //String newValue = term.getPreferedValue();
  //ScriptUtils.addHistoForField(theDocument, usrContext, fieldCodeStatus, "",  oldValue, newValue);
  
  ScriptResultValueDocumentInitializer result = output.getValue();
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
  result.setMessageSummary("Mise à jour du "+Name);
  result.setMessageDetail("Le nouveau Chef de projet est désormais : "+newValue);
  //result.setMessageDetail("...");
  
  //Ajout du commentaire si la chaine n'est pas vide
  if(!comment.equals("")) {
  	IComment commentObj = new Document.Comment();
  	commentObj.setComment(comment);
  	theDocument.getComments().add(commentObj);
  	log.debug("Script : facture_ChangerCdpActionUnitView.groovy : Enregistrement du commentaire : "+comment);
  	ScriptUtils.getDocumentMgr().updateDocumentComments(usrContext, theDocument);
  }
  else {
  	log.debug("Script : facture_ChangerCdpActionUnitView.groovy : Aucun commentaire");
  }
  
  //Refresh the view to display document change
  Utils.getCustomActionController().getModel().setOutcome(Utils.getNavigationController().gotoViewUnit());
  //Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
  Utils.getCustomActionController().getModel().clear();
  Utils.getCustomActionController().getModel().setModalPanelPageKey(null);
  
  log.debug("Out facture_ChangerCdpActionUnitView.groovy ");

}
else if(nNrGest<=0){

// pas de user gestioneaire correspondant à l'utilisateur courrant !!!! 
  ScriptResultValueDocumentInitializer result = output.getValue();
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR );
  result.setMessageSummary("Impossible de mettre à jour le Chef de projet");
  result.setMessageDetail("L'utilisateur courant n'existe pas dans la liste des Chef de projet");

  //Refresh the view to display document change
  Utils.getCustomActionController().getModel().setOutcome(Utils.getNavigationController().gotoViewUnit());
  Utils.getCustomActionController().getModel().clear();
  Utils.getCustomActionController().getModel().setModalPanelPageKey(null);
  
  log.debug("Out1 facture_ChangerCdpActionUnitView.groovy ");

}
else
{
// pas de user gestioneaire correspondant à l'utilisateur courrant !!!! 
  ScriptResultValueDocumentInitializer result = output.getValue();
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR );
  result.setMessageSummary( BundleUtils.getTranslation("error_commentOblig"));
  result.setMessageDetail(BundleUtils.getTranslation("error_commentObligDetail"));

  //Refresh the view to display document change
  Utils.getCustomActionController().getModel().setOutcome(Utils.getNavigationController().gotoViewUnit());
  Utils.getCustomActionController().getModel().clear();
  Utils.getCustomActionController().getModel().setModalPanelPageKey(null);
  
  log.debug("Out2 facture_ChangerCdpActionUnitView.groovy ");

}
