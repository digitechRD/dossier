// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors de l'action "Chager de Directeur" sur
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

import static ScriptUtils

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

// Paramètres passés au script
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("In facture_ChangerDIRActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean valeurRetour = true;
Integer valideurDirecteurId;

// On récupère la valeur du Directeur
valideurDirecteurId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_DIR"));

if (valideurDirecteurId == null || valideurDirecteurId == 0)
{
	valeurRetour = false;

	messageSummary = BundleUtils.getTranslation("error_directeurInvalide");
	messageDetail = BundleUtils.getTranslation("error_directeurInvalideDetail");

	if (afficheLesLog)
		log.debug("facture_ChangerDIRActionUnitView.groovy : On bloque, champ Directeur vide");
}

if (valeurRetour)
{
	if (userContext.getUser().getId() == valideurDirecteurId)
	{
		valeurRetour = false;

		messageSummary = BundleUtils.getTranslation("error_directeurIdem");
		messageDetail = BundleUtils.getTranslation("error_directeurIdemDetail");

		if (afficheLesLog)
			log.debug("facture_ChangerDIRActionUnitView.groovy : On bloque, champ Directeur idem");
	}
	else
	{
		ScriptUtils.addHistoWrk(theDocument, usrContext, BundleUtils.getTranslation("event_changementDirecteur"));

		if (afficheLesLog)
			log.debug("facture_ChangerDIRActionUnitView.groovy : On change de Directeur [" + userContext.getUser().getId() + "->" + valideurDirecteurId + "]");
	}
}

if (valeurRetour)
{
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));

	log.debug("Out facture_ChangerDIRActionUnitView.groovy : Ok");
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

	log.debug("Out facture_ChangerDIRActionUnitView.groovy : Ko [" + messageSummary + "]");
}

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
