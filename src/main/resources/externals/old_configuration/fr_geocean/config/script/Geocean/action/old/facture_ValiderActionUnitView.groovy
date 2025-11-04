// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors de l'action "Valider" sur un type de document
//         Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 06/01/2014 |   PRO   |   1.0   | Création du scripte

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

log.debug("In facture_ValiderActionUnitView.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Déclaration des variables du script
boolean valeurRetour = true;
String messageSummary;
String messageDetail;
String valueFieldState;

// On récupère la valeur de l'état de la facture
valueFieldState = ScriptUtils.getFieldValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_ETAT"));

if (StringUtils.isNotBlank(valueFieldState))
{
	if (afficheLesLog)
		log.debug("facture_ValiderActionUnitView.groovy : valueFieldState [" + valueFieldState + "]");

	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 1 : 1er Contrôle comptabilité
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	if (valueFieldState.equals(ScriptUtils.getConstant("CODE_CMPT_TRANS")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas 1er controle Compta");

		Integer valueFieldAchatId;

		valueFieldAchatId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_ACHAT"));

		if (valueFieldAchatId == null || valueFieldAchatId == 0)
		{
			valeurRetour = false;

			messageSummary = BundleUtils.getTranslation("error_gestionnaireAchatInvalide");
			messageDetail = BundleUtils.getTranslation("error_gestionnaireAchatInvalideDetail");

			if (afficheLesLog)
				log.debug("facture_ValiderActionUnitView.groovy : On bloque, champ gestionnaire Achat vide");
		}
		else
		{
			if (afficheLesLog)
				log.debug("facture_ValiderActionUnitView.groovy : champ gestionnaire Achat (ID) [" + valueFieldAchatId + "]");

			ActionWorkflow(log, theDocument, usrContext, "CODE_VALID_ACHAT", "CODE_NON",
				"", "",
				"", "", "", "", "", "",
				"", "", "", "", "", "",
				"event_validationControleCompta");
		}
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 2 : Validation Achat
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_VALID_ACHAT")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas validation achat");

		String validationTrace;

		for (int i=1;i<4 && valeurRetour;i++)
		{
			String valueFieldCodeChantier;
			Integer valueFieldCdpId;
			Integer valueFieldCdgId;

			valueFieldCodeChantier = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_CODE_CHANT" + i));
			valueFieldCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP" + i));
			valueFieldCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST" + i));

			if (i==1 && !StringUtils.isNotBlank(valueFieldCodeChantier))
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_codeChantierInvalide");
				messageDetail = BundleUtils.getTranslation("error_codeChantierInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, premier code chantier vide");
			}
			else if (StringUtils.isNotBlank(valueFieldCodeChantier) && (valueFieldCdpId == null || valueFieldCdpId == 0))
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_cdpInvalide");
				messageDetail = BundleUtils.getTranslation("error_cdpInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, cdp vide " + i);
			}
			else if ((valueFieldCdgId != null) && (valueFieldCdgId != 0) && (valueFieldCdpId == null || valueFieldCdpId == 0))
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_cdgInvalide");
				messageDetail = BundleUtils.getTranslation("error_cdgInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, cdg sans cdp " + i);
			}
			else if (!StringUtils.isNotBlank(valueFieldCodeChantier) && valueFieldCdpId != null && valueFieldCdpId != 0)
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_codeChantierInvalide");
				messageDetail = BundleUtils.getTranslation("error_codeChantierInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, code chantier vide avec cdp [" + i + "]");
			}
			else if (StringUtils.isNotBlank(valueFieldCodeChantier))
			{
				validationTrace = validationTrace + " - [" + valueFieldCodeChantier + "/";
				if ((valueFieldCdgId != null) && (valueFieldCdgId != 0))
					validationTrace = validationTrace + valueFieldCdgId;
				validationTrace = validationTrace + "/";
				if ((valueFieldCdpId != null) && (valueFieldCdpId != 0))
					validationTrace = validationTrace + valueFieldCdpId;
				validationTrace = validationTrace + "]";
			}
		} // For i

		if (valeurRetour)
		{
			if (afficheLesLog)
				log.debug("facture_ValiderActionUnitView.groovy : saisie " + validationTrace);

			ActionWorkflow(log, theDocument, usrContext, "CODE_CMPT_VERIF", "CODE_NON",
				"FIELD_FACT_DT_ACHAT", "**DATEDUJOUR**",
				"", "", "", "", "", "",
				"", "", "", "", "", "",
				"event_validationAchat");
		}
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 3 : Comptabilisation Compta vérification)
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_CMPT_VERIF")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas verif Compta");

		String valueFieldNumChrono;

		valueFieldNumChrono = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_CHRONO"));

		if (!StringUtils.isNotBlank(valueFieldNumChrono))
		{
			valeurRetour = false;

			messageSummary = BundleUtils.getTranslation("error_numChronoInvalide");
			messageDetail = BundleUtils.getTranslation("error_numChronoInvalideDetail");

			if (afficheLesLog)
				log.debug("facture_ValiderActionUnitView.groovy : On bloque, num chrono vide");
		}
		else
		{
			if (afficheLesLog)
				log.debug("facture_ValiderActionUnitView.groovy : champ num chrono [" + valueFieldNumChrono + "]");
		}

		String validationTrace;

		for (int i=1;i<4 && valeurRetour;i++)
		{
			String valueFieldCodeChantier;
			Integer valueFieldCdpId;
			Integer valueFieldCdgId;

			valueFieldCodeChantier = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_CODE_CHANT" + i));
			valueFieldCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP" + i));
			valueFieldCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST" + i));

			if (i==1 && !StringUtils.isNotBlank(valueFieldCodeChantier))
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_codeChantierInvalide");
				messageDetail = BundleUtils.getTranslation("error_codeChantierInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, code chantier vide");
			}
			else if (StringUtils.isNotBlank(valueFieldCodeChantier) && (valueFieldCdpId == null || valueFieldCdpId == 0))
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_cdpInvalide");
				messageDetail = BundleUtils.getTranslation("error_cdpInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, cdp vide " + i);
			}
			else if ((valueFieldCdgId != null) && (valueFieldCdgId != 0) && (valueFieldCdpId == null || valueFieldCdpId == 0))
			{
				valeurRetour = false;

				messageSummary = BundleUtils.getTranslation("error_cdgInvalide");
				messageDetail = BundleUtils.getTranslation("error_cdgInvalideDetail");

				if (afficheLesLog)
					log.debug("facture_ValiderActionUnitView.groovy : On bloque, cdg sans cdp " + i);
			}
			else if (StringUtils.isNotBlank(valueFieldCodeChantier))
			{
				validationTrace = validationTrace + " - [" + valueFieldCodeChantier + "/";
				if ((valueFieldCdgId != null) && (valueFieldCdgId != 0))
					validationTrace = validationTrace + valueFieldCdgId;
				validationTrace = validationTrace + "/";
				if ((valueFieldCdpId != null) && (valueFieldCdpId != 0))
					validationTrace = validationTrace + valueFieldCdpId;
				validationTrace = validationTrace + "]";
			}
		} // For i

		if (valeurRetour)
		{
			if (afficheLesLog)
				log.debug("facture_ValiderActionUnitView.groovy : saisie" + validationTrace);

			String etapeSuivante = "CODE_VALID_CDP";
			String valueFieldCdg1;
			String valueFieldCdg2;
			String valueFieldCdg3;
			String valueFieldCdp1;
			String valueFieldCdp2;
			String valueFieldCdp3;

			for (int i=1;i<4 && valeurRetour;i++)
			{
				Integer valueFieldCdgId;

				valueFieldCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST" + i));

				if ((valueFieldCdgId != null) && (valueFieldCdgId != 0))
				{
					Integer valueFieldValCdgId;
					String valueFieldValCdg;
					String tmp;

					valueFieldValCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_VAL_CTRLGEST" + i));

					if (valueFieldValCdgId != null && valueFieldValCdgId != 0)
						valueFieldValCdg = ScriptUtils.getTermCode(ScriptUtils.getConstant("FIELD_FAC_VAL_CTRLGEST" + i), valueFieldValCdgId);

					if (StringUtils.isNotBlank(valueFieldValCdg))
					{
						if (valueFieldValCdg.equals(ScriptUtils.getConstant("CODE_NON")))
							tmp = "CODE_TODO";
					}
					else
						tmp = "CODE_TODO";

					if (i == 1)
						valueFieldCdg1 = tmp;
					else if (i == 2)
						valueFieldCdg2 = tmp;
					else if (i == 3)
						valueFieldCdg3 = tmp;

					etapeSuivante = "CODE_VALID_CTRLGEST";
				}

				Integer valueFieldCdpId;

				valueFieldCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP" + i));

				if ((valueFieldCdpId != null) && (valueFieldCdpId != 0))
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
							tmp = "CODE_TODO";
					}
					else
						tmp = "CODE_TODO";

					if (i == 1)
						valueFieldCdp1 = tmp;
					else if (i == 2)
						valueFieldCdp2 = tmp;
					else if (i == 3)
						valueFieldCdp3 = tmp;
				}
			} // For i

			ActionWorkflow(log, theDocument, usrContext, etapeSuivante, "CODE_NON",
				"FIELD_FACT_DT_COMPTA", "**DATEDUJOUR**",
				"FIELD_FAC_VAL_CTRLGEST1", valueFieldCdg1, "FIELD_FAC_VAL_CTRLGEST2", valueFieldCdg2, "FIELD_FAC_VAL_CTRLGEST3", valueFieldCdg3,
				"FIELD_FAC_VAL_CDP1", valueFieldCdp1, "FIELD_FAC_VAL_CDP2", valueFieldCdp2, "FIELD_FAC_VAL_CDP3", valueFieldCdp3,
				"event_validationVerifCompta");
		}
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 4 : Validation Contrôleur de Gestion
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_VALID_CTRLGEST")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas validation ctrl gestion");

		String codeFieldValCdgMaj;
		String valueFieldValCdgMaj;
		String codeFieldValCdpMaj;
		String valueFieldValCdpMaj;
		boolean auMoinsUnNon = false;
		boolean auMoinsUnTodo = false;

		for (int i=1;i<4 && valeurRetour;i++)
		{
			Integer valueFieldCdgId;

			valueFieldCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CTRLGEST" + i));

			if (usrContext.getUser().getId() == valueFieldCdgId)
			{
				codeFieldValCdgMaj = "FIELD_FAC_VAL_CTRLGEST" + i;
				valueFieldValCdgMaj = "CODE_OUI";
				codeFieldValCdpMaj = "FIELD_FAC_VAL_CDP" + i;
				valueFieldValCdpMaj = "CODE_TODO";
			}
			else
			{
				Integer valueFieldValCdgId;
				String valueFieldValCdg;

				valueFieldValCdgId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_VAL_CTRLGEST" + i));

				if (valueFieldValCdgId != null && valueFieldValCdgId != 0)
					valueFieldValCdg = ScriptUtils.getTermCode(ScriptUtils.getConstant("FIELD_FAC_VAL_CTRLGEST" + i), valueFieldValCdgId);

				if (StringUtils.isNotBlank(valueFieldValCdg))
				{
					if (valueFieldValCdg.equals(ScriptUtils.getConstant("CODE_NON")))
						auMoinsUnNon = true;
					else if (valueFieldValCdg.equals(ScriptUtils.getConstant("CODE_TODO")))
						auMoinsUnTodo = true;
				}
			}
		} // For i

		String etapeSuivante;
		String codeValRetour;
		String codeFieldDate;
		String event;
		
		if (auMoinsUnTodo)
			event = "event_validationCdgInter";
		else if (auMoinsUnNon)
		{
			etapeSuivante = "CODE_CMPT_VERIF";
			codeValRetour = "CODE_OUI";
			event = "event_validationCdgRejet";
		}
		else
		{
			etapeSuivante = "CODE_VALID_CDP";
			codeValRetour = "CODE_NON";
			codeFieldDate = "FIELD_FACT_DT_CTRLGEST";
			event = "event_validationCdg";
		}

		ActionWorkflow(log, theDocument, usrContext, etapeSuivante, codeValRetour,
			codeFieldDate, "**DATEDUJOUR**",
			codeFieldValCdgMaj, valueFieldValCdgMaj, "", "", "", "",
			codeFieldValCdpMaj, valueFieldValCdpMaj, "", "", "", "",
			event);

		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : validation CDG [" + etapeSuivante + "/" + codeValRetour + "/" + codeFieldDate + "/" + event + "/" + codeFieldValCdgMaj + "/" + valueFieldValCdgMaj + "]");
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 5 : Validation Chef de Projet
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_VALID_CDP")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas validation cdp");

		String codeFieldValCdpMaj;
		String valueFieldValCdpMaj;
		boolean auMoinsUnNon = false;
		boolean auMoinsUnTodo = false;
		boolean auMoinsUnCdg = false;
		boolean auMoinsUnCdpInvalide = false;

		for (int i=1;i<4 && valeurRetour;i++)
		{
			Integer valueFieldCdpId;

			valueFieldCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_CDP" + i));

			if (usrContext.getUser().getId() == valueFieldCdpId)
			{
				codeFieldValCdpMaj = "FIELD_FAC_VAL_CDP" + i;
				valueFieldValCdpMaj = "CODE_OUI";
			}
			else
			{
				Integer valueFieldValCdpId;
				String valueFieldValCdp;

				valueFieldValCdpId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FAC_VAL_CDP" + i));

				if (valueFieldValCdpId != null && valueFieldValCdpId != 0)
					valueFieldValCdp = ScriptUtils.getTermCode(ScriptUtils.getConstant("FIELD_FAC_VAL_CDP" + i), valueFieldValCdpId);

				if (StringUtils.isNotBlank(valueFieldValCdp))
				{
					if (valueFieldValCdp.equals(ScriptUtils.getConstant("CODE_NON")))
						auMoinsUnNon = true;
					else if (valueFieldValCdp.equals(ScriptUtils.getConstant("CODE_TODO")))
						auMoinsUnTodo = true;
					else if (valueFieldValCdp.equals(ScriptUtils.getConstant("CODE_OUI_INVALIDE")))
						auMoinsUnCdpInvalide = true;
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
			event = "event_validationCdpInter";
		else if (auMoinsUnNon)
		{
			if (auMoinsUnCdg)
				etapeSuivante = "CODE_VALID_CTRLGEST";
			else
				etapeSuivante = "CODE_CMPT_VERIF";

			codeValRetour = "CODE_OUI";
			event = "event_validationCdpRejet";
		}
		else
		{
			etapeSuivante = "CODE_CMPT_PREPA_PAIE";
			codeValRetour = "CODE_NON";
			codeFieldCdpValide = "FIELD_FACT_CDPVALIDE";
			if (auMoinsUnCdpInvalide)
				valueFieldCdpValide = "CODE_INVALIDE";
			else
				valueFieldCdpValide = "CODE_VALIDE";
			codeFieldDate = "FIELD_FACT_DT_CDP";
			event = "event_validationCdp";
		}

		ActionWorkflow(log, theDocument, usrContext, etapeSuivante, codeValRetour,
			codeFieldDate, "**DATEDUJOUR**",
			"", "", "", "", "", "",
			codeFieldValCdpMaj, valueFieldValCdpMaj, codeFieldCdpValide, valueFieldCdpValide, "", "",
			event);

		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : validation CDP [" + etapeSuivante + "/" + codeValRetour + "/" + codeFieldDate + "/" + event + "/" + codeFieldValCdpMaj + "/" + valueFieldValCdpMaj + "]");
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 6 : Préparation paiement (compta)
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_CMPT_PREPA_PAIE")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas prépa paiement compta");

		ActionWorkflow(log, theDocument, usrContext, "CODE_VALID_DAF", "CODE_NON",
			"", "",
			"", "", "", "", "", "",
			"", "", "", "", "", "",
			"event_validationPrepaPaiementCompta");
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 6' : Litige (Compta)
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_CMPT_LITIGE")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas litige compta");

		ActionWorkflow(log, theDocument, usrContext, "CODE_VALID_DAF", "CODE_NON",
			"", "",
			"", "", "", "", "", "",
			"", "", "", "", "", "",
			"event_validationLitigeCompta");
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 7 : Validation DAF
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_VALID_DAF")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas validation daf");

		String etapeSuivante;
		Integer valueFieldDirecteurId;

		valueFieldDirecteurId = FieldUtils.getValue(theDocument, ScriptUtils.getConstant("FIELD_FACT_USR_DIR"));

		if (valueFieldDirecteurId == null || valueFieldDirecteurId == 0)
			etapeSuivante = "CODE_A_PAYER";
		else
			etapeSuivante = "CODE_VALID_DIR";

		ActionWorkflow(log, theDocument, usrContext, etapeSuivante, "CODE_NON",
			"FIELD_FACT_DT_DAF", "**DATEDUJOUR**",
			"", "", "", "", "", "",
			"", "", "", "", "", "",
			"event_validationDaf");
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 8 : Validation Directeur
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_VALID_DIR")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas validation dir");

		ActionWorkflow(log, theDocument, usrContext, "CODE_A_PAYER", "CODE_NON",
			"FIELD_FACT_DT_DIR", "**DATEDUJOUR**",
			"", "", "", "", "", "",
			"", "", "", "", "", "",
			"event_validationDir");
	}
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	// Etape 9 : Enregistrement paiement (Compta)
	// **************************************************************************************************************************************
	// **************************************************************************************************************************************
	else if (valueFieldState.equals(ScriptUtils.getConstant("CODE_A_PAYER")))
	{
		if (afficheLesLog)
			log.debug("facture_ValiderActionUnitView.groovy : cas enreg paiement compta");

		ActionWorkflow(log, theDocument, usrContext, "CODE_PAYEE", "CODE_NON",
			"FIELD_FACT_DT_PMNT", "**DATEDUJOUR**",
			"", "", "", "", "", "",
			"", "", "", "", "", "",
			"event_validationPaiementCompta");
	}
	else
	{
		log.info("facture_ValiderActionUnitView.groovy : cas inconnu ou non gere par le script");
	}
}
else
{
	log.debug("facture_ValiderActionUnitView.groovy : valueFieldState empty !");
}

if (valeurRetour)
{
	Utils.getCustomActionController().getModel().setOutcome(NavigationUtils.gotoSimpleView(true, null));

	log.debug("Out facture_ValiderActionUnitView.groovy : Ok");
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

	log.debug("Out facture_ValiderActionUnitView.groovy : Ko [" + messageSummary + "]");
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
