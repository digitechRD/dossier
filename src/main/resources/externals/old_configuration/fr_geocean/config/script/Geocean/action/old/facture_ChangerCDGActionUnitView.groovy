// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors de l'action "Chager de Contrôleur De Gestion" sur
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

log.debug("In facture_ChangerCDGActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean valeurRetour = true;
String newUtils;

for (int i=1;i<4 && valeurRetour;i++)
{
	Integer valueFieldUsrId;
	Integer valueFieldValId;
	String valueFieldVal;

	valueFieldUsrId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST" + i));
	valueFieldValId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_VAL_CTRLGEST" + i));

	if (valueFieldValId != null && valueFieldValId != 0)
		valueFieldVal = ScriptUtils.getTermCode(ScriptUtils.getConstant("FIELD_FAC_VAL_CTRLGEST" + i), valueFieldValId);

	if (valueFieldVal != null && valueFieldVal.equals(ScriptUtils.getConstant("CODE_TODO")) && (valueFieldUsrId == null || valueFieldUsrId == 0))
	{
		valeurRetour = false;

		messageSummary = BundleUtils.getTranslation("error_cdgInvalide");
		messageDetail = BundleUtils.getTranslation("error_cdgInvalideDetail");

		if (afficheLesLog)
			log.debug("facture_ChangerCDGActionUnitView.groovy : On bloque, champ Cdg vide [" + i + "]");
	}
	else if ((valueFieldUsrId != null && valueFieldUsrId != 0))
	{
		if (StringUtils.isNotBlank(newUtils))
			newUtils += " / " + ScriptUtils.getListItemValueFromId(valueFieldUsrId, IField.REFERENCE_TYPE_USER);
		else
			newUtils = ScriptUtils.getListItemValueFromId(valueFieldUsrId, IField.REFERENCE_TYPE_USER);

		newUtils += " (" + i + ")";

		if (afficheLesLog)
			log.debug("facture_ChangerCDGActionUnitView.groovy : newUtils [" + newUtils + "]");
	}
}

if (valeurRetour)
{
	String comment;

	IComment commentObj = new Document.Comment();
	comment = BundleUtils.getTranslation("msg_changementCdg") + newUtils;
	commentObj.setComment(comment);
	theDocument.getComments().add(commentObj);
	ScriptUtils.getDocumentMgr().updateDocumentComments(usrContext, theDocument);

	ScriptUtils.addHistoWrk(theDocument, usrContext, BundleUtils.getTranslation("event_changementCdg"));

	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));

	log.debug("Out facture_ChangerCDGActionUnitView.groovy : Ok");
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

	log.debug("Out facture_ChangerCDGActionUnitView.groovy : Ko [" + messageSummary + "]");
}

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
