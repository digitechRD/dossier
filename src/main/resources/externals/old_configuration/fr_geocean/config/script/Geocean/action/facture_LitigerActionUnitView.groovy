// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors de l'action "Litige" sur un type de document
//         Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 06/01/2014 |   PRO   |   1.0   | Création du scripte

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import org.apache.commons.lang.StringUtils;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.impl.Document;
import com.digitech.dossier.common.model.backend.MessagesModel;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.service.ScriptRunner;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import static ScriptUtils

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

// Paramètres passés au script
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("In facture_LitigerActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean valeurRetour = true;
String messageSummary;
String messageDetail;

// On récupère le commentaire
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
String comment = customActionModel.getModalPanelModel().get("comment");

if (comment != null && !comment.equals(""))
{
	IComment commentObj = new Document.Comment();
	
	commentObj.setComment(comment);
	theDocument.getComments().add(commentObj);
	
	if (afficheLesLog)
		log.debug("facture_LitigerActionUnitView.groovy : Ajout commentaire [" + comment + "]");
	
	ScriptUtils.getDocumentMgr().updateDocumentComments(usrContext, theDocument);
}
else
{
	valeurRetour = false;

	messageSummary = BundleUtils.getTranslation("error_commentOblig");
	messageDetail = BundleUtils.getTranslation("error_commentObligDetail");

	if (afficheLesLog)
		log.debug("facture_LitigerActionUnitView.groovy : Aucun commentaire");
}

if (valeurRetour)
{
	ActionWorkflow(theDocument, usrContext, "CODE_CMPT_LITIGE", "CODE_NON", "event_litiger");
}

if (valeurRetour)
{
//	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));
  Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoCurrentPage(true, true, theDocument));

	log.debug("Out facture_LitigerActionUnitView.groovy : Ok");
}
else
{
	ScriptResultValueDocumentInitializer result = output.getValue();

	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary(messageSummary);
	result.setMessageDetail(messageDetail);

	FacesMessage msgScript = ScriptRunner.computeMessage(output);
	List<FacesMessage> list = new ArrayList();
	list.add(msgScript);
	MessagesModel.getInstance().addPersistantFacesMessages(list);

	log.debug("Out facture_LitigerActionUnitView.groovy : Ko [" + messageSummary + "]");
}

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
public boolean ActionWorkflow(IDocument theDocument, UserCoreContext usrContext,
	String sCodeValEtat, String sCodeValRetour, String sCodeMessage) {

	// On modifie nos 2 valeurs de champ ETAT et RETOUR
	FieldUtils.setValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"),
		ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"), ScriptUtils.getConstant(sCodeValEtat)));
	FieldUtils.setValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_RETOUR"),
		ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant("FIELD_FAC_RETOUR"), ScriptUtils.getConstant(sCodeValRetour)));
	// Save the document
	DocumentUtils.saveDocument(theDocument);

	ScriptUtils.addHistoWrk(theDocument, usrContext, BundleUtils.getTranslation(sCodeMessage));

	return true;
}
