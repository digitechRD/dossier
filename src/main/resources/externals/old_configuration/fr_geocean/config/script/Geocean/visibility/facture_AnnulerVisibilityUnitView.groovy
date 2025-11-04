// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script qui permet d'afficher ou non le bouton d'action "Annuler" 
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

log.debug("In facture_AnnulerVisibilityUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean boutonVisible = false;
String fieldState;
String fieldRetour;
boolean haveRightProfilCmpt;
boolean haveRightProfilAchat;
boolean haveRightProfilCdg;
boolean haveRightProfilCdp;
boolean haveRightProfilDaf;
boolean haveRightProfilDir;

// On récupère la valeur de l'état de la facture
fieldState = ScriptUtils.getFieldValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"));
// On récupère la valeur si c'est une retour de workflow de la facture
fieldRetour = ScriptUtils.getFieldValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_RETOUR"));

if (StringUtils.isNotBlank(fieldState))
{
	if (afficheLesLog)
		log.debug("facture_AnnulerVisibilityUnitView.groovy : fieldState [" + fieldState + "]");

	if (fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_TRANS")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_VERIF")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_PREPA_PAIE")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_CMPT_LITIGE")) ||
		fieldState.equals(ScriptUtils.getConstant("CODE_A_PAYER")))
	{
		if (afficheLesLog)
			log.debug("facture_AnnulerVisibilityUnitView.groovy : cas Compta [" + fieldState + "]");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_CMPT")))
		{
			if (afficheLesLog)
				log.debug("facture_AnnulerVisibilityUnitView.groovy : utilisateur avec le profil Compta");

			boutonVisible = true;
		}
	}
	else
	{
		log.info("facture_AnnulerVisibilityUnitView.groovy : cas inconnu ou non gere par le script");
	}
}
else
{
	log.debug("facture_AnnulerVisibilityUnitView.groovy : fieldState empty !");
}

// This is the end :
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
if (boutonVisible){
	result.setValid(true);
	log.debug("Out facture_AnnulerVisibilityUnitView.groovy : Visible");
} else {
	result.setValid(false);
	log.debug("Out facture_AnnulerVisibilityUnitView.groovy : Invisible");
}
outputParam.setValue(result);

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
