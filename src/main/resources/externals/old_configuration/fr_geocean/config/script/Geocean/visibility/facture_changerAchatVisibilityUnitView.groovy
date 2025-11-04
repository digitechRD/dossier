// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script qui permet d'afficher ou non le bouoton d'action "Changer de
//         gestionnaire Achat" sur un type de document Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 13/01/2014 |   PRO   |   1.0   | Création du scripte

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

log.debug("In facture_ChangerAchatVisibilityUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean boutonVisible = false;
String fieldState;

// On récupère la valeur de l'état de la facture
fieldState = ScriptUtils.getFieldValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"));

if (StringUtils.isNotBlank(fieldState))
{
	if (afficheLesLog)
		log.debug("facture_ChangerAchatVisibilityUnitView.groovy : fieldState [" + fieldState + "]");

	if (fieldState.equals(ScriptUtils.getConstant("CODE_VALID_ACHAT")))
	{
		if (afficheLesLog)
			log.debug("facture_ChangerAchatVisibilityUnitView.groovy : cas validation achat");

		if (UserUtils.hasProfile(usrContext, ScriptUtils.getConstant("PROFIL_ACHAT")))
		{
			Integer valideurId;

			valideurId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_ACHAT"));

			if (afficheLesLog)
				log.debug("facture_ChangerAchatVisibilityUnitView.groovy : valideurId [" + valideurId + "]");

			if (usrContext.getUser().getId() == valideurId)
			{
				if (afficheLesLog)
					log.debug("facture_ChangerAchatVisibilityUnitView.groovy : utilisateur selectionne dans le champ");

				boutonVisible = true;
			}
		}
	}
	else
	{
		log.info("facture_ChangerAchatVisibilityUnitView.groovy : cas inconnu ou non gere par le script");
	}
}
else
{
	log.debug("facture_ChangerAchatVisibilityUnitView.groovy : fieldState empty !");
}

// This is the end :
ScriptResultValueDisplayRule result = new ScriptResultValueDisplayRule();
if (boutonVisible){
	result.setValid(true);
	log.debug("Out facture_ChangerAchatVisibilityUnitView.groovy : Visible");
} else {
	result.setValid(false);
	log.debug("Out facture_ChangerAchatVisibilityUnitView.groovy : Invisible");
}
outputParam.setValue(result);

// ----------------------------------------------------------------------------
// Fonctions spécifiques de traitement
// ----------------------------------------------------------------------------
