// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script qui permet d'afficher ou non le bouton d'action "Rejeter" 
//         sur un type de document Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 06/01/2014 |   PRO   |   1.0   | Création du scripte

import java.util.ArrayList
import java.util.List
import org.apache.commons.lang.StringUtils;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.utils.FieldUtils
import com.digitech.dossier.common.utils.UserUtils
import com.digitech.dossier.script.model.impl.result.ScriptResultModel
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDisplayRule

import static ScriptUtils

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

// Paramètres passés au script
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;
ScriptResultModel<ScriptResultValueDisplayRule> outputParam = output;

log.debug("In facture_RejeterVisibilityUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean boutonVisible = false;
String fieldState;

// On récupère la valeur de l'état de la facture
fieldState = ScriptUtils.getFieldValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"));

if (StringUtils.isNotBlank(fieldState))
{
	if (afficheLesLog)
		log.debug("facture_RejeterVisibilityUnitView.groovy : fieldState [" + fieldState + "]");

	if (fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_VERIF")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_PREPA_PAIE")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_LITIGE")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_A_PAYER")))
	{
		if (afficheLesLog)
			log.debug("facture_RejeterVisibilityUnitView.groovy : cas Compta [" + fieldState + "]");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_CMPT")))
		{
			if (afficheLesLog)
				log.debug("facture_RejeterVisibilityUnitView.groovy : utilisateur avec le profil Compta");

			boutonVisible = true;
		}
	}
	else if (fieldState.equals(ScriptUtils.getConstant("CODE_VALID_ACHAT")))
	{
		if (afficheLesLog)
			log.debug("facture_RejeterVisibilityUnitView.groovy : cas validation achat");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_ACHAT")))
		{
			Integer valideurAchatId;

			valideurAchatId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_ACHAT"));

			if (afficheLesLog)
				log.debug("facture_RejeterVisibilityUnitView.groovy : valideurAchatId [" + valideurAchatId + "]");

			if (usrContext.getUser().getId() == valideurAchatId)
			{
				if (afficheLesLog)
					log.debug("facture_RejeterVisibilityUnitView.groovy : utilisateur selectionne dans le champ");

				boutonVisible = true;
			}
		}
	}
	else if (fieldState.equals(ScriptUtils.getConstant("CODE_VALID_CTRLGEST")))
	{
		if (afficheLesLog)
			log.debug("facture_RejeterVisibilityUnitView.groovy : cas validation ctrl gestion");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_CTRLGEST")))
		{
			Integer valideurCdg1Id;
			Integer valideurCdg2Id;
			Integer valideurCdg3Id;

			valideurCdg1Id = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST1"));
			valideurCdg2Id = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST2"));
			valideurCdg3Id = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST3"));

			if (afficheLesLog)
				log.debug("facture_RejeterVisibilityUnitView.groovy : valideurCdgId(s) [" + valideurCdg1Id + "/" + valideurCdg2Id + "/" + valideurCdg3Id + "]");

			if (usrContext.getUser().getId() == valideurCdg1Id ||
				usrContext.getUser().getId() == valideurCdg2Id ||
				usrContext.getUser().getId() == valideurCdg3Id)
			{
				if (afficheLesLog)
					log.debug("facture_RejeterVisibilityUnitView.groovy : utilisateur selectionne dans le champ");

				boutonVisible = true;
			}
		}
	}
	else if (fieldState.equals(ScriptUtils.getConstant("CODE_VALID_CDP")))
	{
		if (afficheLesLog)
			log.debug("facture_RejeterVisibilityUnitView.groovy : cas validation cdp");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_CDP")))
		{
			Integer valideurCdp1Id;
			Integer valideurCdp2Id;
			Integer valideurCdp3Id;

			valideurCdp1Id = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP1"));
			valideurCdp2Id = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP2"));
			valideurCdp3Id = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP3"));

			if (afficheLesLog)
				log.debug("facture_RejeterVisibilityUnitView.groovy : valideurCdpId(s) [" + valideurCdp1Id + "/" + valideurCdp2Id + "/" + valideurCdp3Id + "]");

			if (usrContext.getUser().getId() == valideurCdp1Id ||
				usrContext.getUser().getId() == valideurCdp2Id ||
				usrContext.getUser().getId() == valideurCdp3Id)
			{
				if (afficheLesLog)
					log.debug("facture_RejeterVisibilityUnitView.groovy : utilisateur selectionne dans le champ");

				boutonVisible = true;
			}
		}
	}
	else if (fieldState.equals(ScriptUtils.getConstant("CODE_VALID_DAF")))
	{
		if (afficheLesLog)
			log.debug("facture_RejeterVisibilityUnitView.groovy : cas validation daf");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_DAF")))
		{
			if (afficheLesLog)
				log.debug("facture_RejeterVisibilityUnitView.groovy : utilisateur avec le profil Daf");

			boutonVisible = true;
		}
	}
	else if (fieldState.equals(ScriptUtils.getConstant("CODE_VALID_DIR")))
	{
		if (afficheLesLog)
			log.debug("facture_RejeterVisibilityUnitView.groovy : cas validation dir");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_DIR")))
		{
			Integer valideurDirecteurId;

			valideurDirecteurId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_DIR"));

			if (afficheLesLog)
				log.debug("facture_RejeterVisibilityUnitView.groovy : valideurDirecteurId [" + valideurDirecteurId + "]");

			if (usrContext.getUser().getId() == valideurDirecteurId)
			{
				if (afficheLesLog)
					log.debug("facture_RejeterVisibilityUnitView.groovy : utilisateur selectionne dans le champ");

				boutonVisible = true;
			}
		}
	}
	else
	{
		log.info("facture_RejeterVisibilityUnitView.groovy : cas inconnu ou non gere par le script");
	}
}
else
{
	log.debug("facture_RejeterVisibilityUnitView.groovy : fieldState empty !");
}

// This is the end :
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
if (boutonVisible){
	result.setValid(true);
	log.debug("Out facture_RejeterVisibilityUnitView.groovy : Visible");
} else {
	result.setValid(false);
	log.debug("Out facture_RejeterVisibilityUnitView.groovy : Invisible");
}
outputParam.setValue(result);

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
