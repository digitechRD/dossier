// ****************************************************************************
// Projet : SAB - Airs Capture / Airs Dossier
// Flux : BK (Flux Bancaire)
// Objet : Script appelé lors d'une action avec saisie d'un commentaire
// Condition d'exécution : N/A
// Description :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 20/08/2018 |   PRO   |   1.0   | Création du scripte

String versionScript = "1.0";

// Java imports
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

// Digitech imports
import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.CustomActionModel;

//import static BK_ScriptUtilsProject;

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

/********************* PARAM ********************/
// Logger scriptLogger     : le Logger
// UserContext userContext : l'...userContext
// IDocument document      : le document courant
/************************************************/

scriptLogger.debug("In WKF_initValiderDoc.groovy [v" + versionScript + "] [idDoc=" + document.getAirsRefId() + "]");

// Set parameter for custom panel
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.getModalPanelModel().put("comment","");

// Set size of custom panel
Integer witdhModalPanel = Integer.parseInt("520");
Integer heightModalPanel = Integer.parseInt("320");
customActionModel.setModalPanelWidth(witdhModalPanel);
customActionModel.setModalPanelHeight(heightModalPanel);

// Set title of custom panel
customActionModel.setModalPanelTitle(BundleUtils.getTranslation("WKF_modalPanelComment_title"));

// Set parameter to fix display bug (multiple modal panel one after the other)
customActionModel.getModalPanelModel().put("displayPanelComment", true);

scriptLogger.debug("Out WKF_initValiderDoc.groovy");
