// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors de l'action "Invalider" sur un type de document
//         Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 29/01/2014 |   PRO   |   1.0   | Création du scripte

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import static ScriptUtils;

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

// Paramètres passés au script
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("In facture_InvaliderActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

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
		log.debug("facture_InvaliderActionUnitView.groovy : Ajout commentaire [" + comment + "]");
	
	ScriptUtils.getDocumentMgr().updateDocumentComments(usrContext, theDocument);
}
else
{
	valeurRetour = false;

	messageSummary = BundleUtils.getTranslation("error_commentOblig");
	messageDetail = BundleUtils.getTranslation("error_commentObligDetail");

	if (afficheLesLog)
		log.debug("facture_InvaliderActionUnitView.groovy : Aucun commentaire");
}

if (valeurRetour)
{
	String codeFieldValCdpMaj;
	String valueFieldValCdpMaj;
	boolean auMoinsUnNon = false;
	boolean auMoinsUnTodo = false;
	boolean auMoinsUnCdg = false;

	for (int i=1;i<4 && valeurRetour;i++)
	{
		Integer valueFieldCdpId;

		valueFieldCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP" + i));

		if (usrContext.getUser().getId() == valueFieldCdpId)
		{
			codeFieldValCdpMaj = "FIELD_FAC_VAL_CDP" + i;
			valueFieldValCdpMaj = "CODE_OUI_INVALIDE";
		}
		else
		{
			Integer valueFieldValCdpId;
			String valueFieldValCdp;
			String tmp;

			valueFieldValCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_VAL_CDP" + i));

			if (valueFieldValCdpId != null && valueFieldValCdpId != 0)
				valueFieldValCdp = ScriptUtils.getTermCode(ScriptUtils.getConstant("FIELD_FAC_VAL_CDP" + i), valueFieldValCdpId);

			if (StringUtils.isNotBlank(valueFieldValCdp))
			{
				if (valueFieldValCdp.equals(ScriptUtils.getConstant("CODE_NON")))
					auMoinsUnNon = true;
				else if (valueFieldValCdp.equals(ScriptUtils.getConstant("CODE_TODO")))
					auMoinsUnTodo = true;
			}
		}

		Integer valueFieldCdgId;

		valueFieldCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST" + i));

		if ((valueFieldCdgId != null) && (valueFieldCdgId != 0))
			auMoinsUnCdg = true;
	} // For i

	String etapeSuivante;
	String codeValRetour;
	String valueValRetour;
	String codeFieldDate;
	String codeFieldCdpValide;
	String valueFieldCdpValide;
	String event;
	
	if (auMoinsUnTodo)
		event = "event_invalidationCdpInter";
	else if (auMoinsUnNon)
	{
		if (auMoinsUnCdg)
			etapeSuivante = "CODE_VALID_CTRLGEST";
		else
			etapeSuivante = "CODE_CMPT_VERIF";

		codeValRetour = "CODE_OUI";
		event = "event_invalidationCdpRejet";
	}
	else
	{
		etapeSuivante = "CODE_CMPT_PREPA_PAIE";
		codeValRetour = "CODE_NON";
		codeFieldCdpValide = "FIELD_FACT_CDPVALIDE";
		valueFieldCdpValide = "CODE_INVALIDE";
		codeFieldDate = "FIELD_FACT_DT_CDP";
		event = "event_invalidationCdp";
	}

	ActionWorkflow(log, theDocument, usrContext, etapeSuivante, codeValRetour,
		codeFieldDate, "**DATEDUJOUR**",
		"", "", "", "", "", "",
		codeFieldValCdpMaj, valueFieldValCdpMaj, codeFieldCdpValide, valueFieldCdpValide, "", "",
		event);

	if (afficheLesLog)
		log.debug("facture_InvaliderActionUnitView.groovy : validation CDP [" + etapeSuivante + "/" + codeValRetour + "/" + codeFieldDate + "/" + event + "/" + codeFieldCdpValide + "/" + valueFieldCdpValide + "]");
}

if (valeurRetour)
{
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));

	log.debug("Out facture_InvaliderActionUnitView.groovy : Ok");
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

	log.debug("Out facture_InvaliderActionUnitView.groovy : Ko [" + messageSummary + "]");
}

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
public boolean ActionWorkflow(org.slf4j.Logger log, IDocument theDocument, UserCoreContext usrContext,
	String sCodeValEtat, String sCodeValRetour,
	String sCodeSup, String sValueSup,
	String sCodeSupCdg1, String sValueSupCdg1, String sCodeSupCdg2, String sValueSupCdg2, String sCodeSupCdg3, String sValueSupCdg3,
	String sCodeSupCdp1, String sValueSupCdp1, String sCodeSupCdp2, String sValueSupCdp2, String sCodeSupCdp3, String sValueSupCdp3,
	String sCodeMessage) {

	if (StringUtils.isNotBlank(sCodeValEtat))
	{
		log.info("ActionWorkflow sCodeValEtat ["+ sCodeValEtat + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"), ScriptUtils.getConstant(sCodeValEtat)));
	}

	if (StringUtils.isNotBlank(sCodeValRetour))
	{
		log.info("ActionWorkflow sCodeValRetour ["+ sCodeValRetour + "]");
		
		FieldUtils.setValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_RETOUR"),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant("FIELD_FAC_RETOUR"), ScriptUtils.getConstant(sCodeValRetour)));
	}

	if (StringUtils.isNotBlank(sCodeSup) && StringUtils.isNotBlank(sValueSup))
	{
		log.info("ActionWorkflow sCodeSup ["+ sCodeSup + "] sValueSup [" + sValueSup + "]");

		if (sValueSup.equals("**DATEDUJOUR**"))
		{
			Date myDateToday = new Date();
			SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			sValueSup = mySimpleDateFormat.format(myDateToday);
		}

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSup), sValueSup);
	}

	if (StringUtils.isNotBlank(sCodeSupCdg1) && StringUtils.isNotBlank(sValueSupCdg1))
	{
		log.info("ActionWorkflow sCodeSupCdg1 ["+ sCodeSupCdg1 + "] sValueSupCdg1 [" + sValueSupCdg1 + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSupCdg1),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant(sCodeSupCdg1), ScriptUtils.getConstant(sValueSupCdg1)));
	}
	if (StringUtils.isNotBlank(sCodeSupCdg2) && StringUtils.isNotBlank(sValueSupCdg2))
	{
		log.info("ActionWorkflow sCodeSupCdg2 ["+ sCodeSupCdg2 + "] sValueSupCdg2 [" + sValueSupCdg2 + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSupCdg2),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant(sCodeSupCdg2), ScriptUtils.getConstant(sValueSupCdg2)));
	}
	if (StringUtils.isNotBlank(sCodeSupCdg3) && StringUtils.isNotBlank(sValueSupCdg3))
	{
		log.info("ActionWorkflow sCodeSupCdg3 ["+ sCodeSupCdg3 + "] sValueSupCdg3 [" + sValueSupCdg3 + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSupCdg3),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant(sCodeSupCdg3), ScriptUtils.getConstant(sValueSupCdg3)));
	}
	if (StringUtils.isNotBlank(sCodeSupCdp1) && StringUtils.isNotBlank(sValueSupCdp1))
	{
		log.info("ActionWorkflow sCodeSupCdp1 ["+ sCodeSupCdp1 + "] sValueSupCdp1 [" + sValueSupCdp1 + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSupCdp1),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant(sCodeSupCdp1), ScriptUtils.getConstant(sValueSupCdp1)));
	}
	if (StringUtils.isNotBlank(sCodeSupCdp2) && StringUtils.isNotBlank(sValueSupCdp2))
	{
		log.info("ActionWorkflow sCodeSupCdp2 ["+ sCodeSupCdp2 + "] sValueSupCdp2 [" + sValueSupCdp2 + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSupCdp2),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant(sCodeSupCdp2), ScriptUtils.getConstant(sValueSupCdp2)));
	}
	if (StringUtils.isNotBlank(sCodeSupCdp3) && StringUtils.isNotBlank(sValueSupCdp3))
	{
		log.info("ActionWorkflow sCodeSupCdp3 ["+ sCodeSupCdp3 + "] sValueSupCdp3 [" + sValueSupCdp3 + "]");

		FieldUtils.setValue(theDocument, ScriptUtils.getConstant(sCodeSupCdp3),
			ScriptUtils.getTermID(theDocument, ScriptUtils.getConstant(sCodeSupCdp3), ScriptUtils.getConstant(sValueSupCdp3)));
	}

	// Save the document
	DocumentUtils.saveDocument(theDocument);

	log.info("ActionWorkflow ["+ sCodeMessage + "/" + BundleUtils.getTranslation(sCodeMessage) + "]");

	ScriptUtils.addHistoWrk(theDocument, usrContext, BundleUtils.getTranslation(sCodeMessage));

	return true;
} // ActionWorkflow
