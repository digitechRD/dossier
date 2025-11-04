// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors de l'action "Chager de Gestionnaire Achat" sur
//         un type de document Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 13/01/2014 |   PRO   |   1.0   | Création du scripte

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import org.apache.commons.lang.StringUtils;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IDocument.IComment;
import com.digitech.dossier.common.model.backend.airs.impl.Document;
import com.digitech.dossier.common.model.backend.MessagesModel;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.service.ScriptRunner;
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.DocumentUtils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.NavigationUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import static ScriptUtils;

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

// Paramètres passés au script
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("In facture_ChangerAchatActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean valeurRetour = true;
Integer valideurAchatId;

// On récupère la valeur du gestionnaire Achat
valideurAchatId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_ACHAT"));

if (valideurAchatId == null || valideurAchatId == 0)
{
	valeurRetour = false;

	messageSummary = BundleUtils.getTranslation("error_gestionnaireAchatInvalide");
	messageDetail = BundleUtils.getTranslation("error_gestionnaireAchatInvalideDetail");

	if (afficheLesLog)
		log.debug("facture_ChangerAchatActionUnitView.groovy : On bloque, champ gestionnaire Achat vide");
}

if (valeurRetour)
{
	if (userContext.getUser().getId() == valideurAchatId)
	{
		valeurRetour = false;

		messageSummary = BundleUtils.getTranslation("error_gestionnaireAchatIdem");
		messageDetail = BundleUtils.getTranslation("error_gestionnaireAchatIdemDetail");

		if (afficheLesLog)
			log.debug("facture_ChangerAchatActionUnitView.groovy : On bloque, champ gestionnaire Achat idem");
	}
	else
	{
		ScriptUtils.addHistoWrk(theDocument, usrContext, BundleUtils.getTranslation("event_changementAchat"));

		if (afficheLesLog)
			log.debug("facture_ChangerAchatActionUnitView.groovy : On change de gestionnaire Achat [" + userContext.getUser().getId() + "->" + valideurAchatId + "]");
	}
}

if (valeurRetour)
{
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));

	log.debug("Out facture_ChangerAchatActionUnitView.groovy : Ok");
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

	log.debug("Out facture_ChangerAchatActionUnitView.groovy : Ko [" + messageSummary + "]");
}

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------

