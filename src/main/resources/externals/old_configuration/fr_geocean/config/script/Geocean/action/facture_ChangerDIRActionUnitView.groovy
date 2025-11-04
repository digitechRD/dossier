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
log.debug("In facture_ChangerDirActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

//On récupère le commentaire entré par l'utilisateur
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
String comment = customActionModel.getModalPanelModel().get("comment");

//On récupère les ID de l'orga traitante pour l'historique
int oldUserId =  FieldUtils.getValue(theDocument, "FACT_USR_DIR");
int newUserId = Integer.valueOf(customActionModel.getModalPanelModel().get("selectedUser"));
  if(oldUserId== null) {
    oldUserId=-1;
  }

ScriptResultValueDocumentInitializer result = output.getValue();

//Ajout du commentaire si la chaine n'est pas vide
if(!comment.equals("")) {
	IComment commentObj = new Document.Comment();
	commentObj.setComment(comment);
	theDocument.getComments().add(commentObj);
	log.debug("Script : facture_ChangerDirActionUnitView.groovy : Enregistrement du commentaire : "+comment);
	ScriptUtils.getDocumentMgr().updateDocumentComments(usrContext, theDocument);


  //Changement du user Direction
  FieldUtils.setValue(theDocument, "FACT_USR_DIR", newUserId);
  
  //Save the document
  DocumentUtils.saveDocument(theDocument);
  
  //Update pour le user
  String  oldValue = ScriptUtils.getListItemValueFromId(oldUserId, IField.REFERENCE_TYPE_USER);
  String newValue = ScriptUtils.getListItemValueFromId(newUserId, IField.REFERENCE_TYPE_USER);
  //ScriptUtils.addHistoForField(theDocument, usrContext, "Changement gestionaire Direction ", "",  oldValue, newValue);
  ScriptUtils.addHistoWrkNewUser(theDocument, usrContext, "Changement direction",  oldValue, newValue);

  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.INFO );
  result.setMessageSummary("Mise à jour du gestionaire Direction");
  result.setMessageDetail("Le nouveau gestionaire est désormais : "+newValue);
  //result.setMessageDetail("...");
	
  //Refresh the view to display document change
  Utils.getCustomActionController().getModel().setOutcome(Utils.getNavigationController().gotoViewUnit());
  //Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
  Utils.getCustomActionController().getModel().clear();
  Utils.getCustomActionController().getModel().setModalPanelPageKey(null);
  
  //log.debug("Script triggered on allocated UG : facture_allocatedUGUnitView.groovy --- End");
  log.debug("Out facture_ChangerDirActionUnitView.groovy ");	
	
}
else {
	log.debug("Script : facture_ChangerDirActionUnitView.groovy : Aucun commentaire");
	
// pas de user gestioneaire correspondant à l'utilisateur courrant !!!! 
  result.setMessageSeverity(  com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR );
  result.setMessageSummary( BundleUtils.getTranslation("error_commentOblig"));
  result.setMessageDetail(BundleUtils.getTranslation("error_commentObligDetail"));

  //Refresh the view to display document change
  Utils.getCustomActionController().getModel().setOutcome(Utils.getNavigationController().gotoViewUnit());
  Utils.getCustomActionController().getModel().clear();
  Utils.getCustomActionController().getModel().setModalPanelPageKey(null);
  
  log.debug("Out2 facture_ChangerDirActionUnitView.groovy ");
	
}	




