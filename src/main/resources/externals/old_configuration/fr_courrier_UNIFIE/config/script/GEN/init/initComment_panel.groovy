import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.CustomActionModel;

/**
* Auteur : JMU
* Date : 18/07/14
* Flux : Généque
* Description : Script permettant d'initialiser le panel d'affichage pour la saisie de commentaire suite à une action workflow  
* Version : 1.0
* Paramêtres d'entrée :
*   - scriptLogger
*   - document
*   - userContext
**/

import GenScriptUtils;

String SCRIPT_NAME = "initComment_panel.groovy";

scriptLogger.debug("Script groovy de type initialisation de panel : "+SCRIPT_NAME+" --- Start");

// Paramêtre à passer au module jsp (le commentaire saisi par l'utilisateur)
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.getModalPanelModel().put("comment","");

// Initialisation de la taille du panel
Integer witdhModalPanel = Integer.parseInt(GenScriptUtils.getConstant("COMMENT_PANEL_WIDTH"));
Integer heightModalPanel = Integer.parseInt(GenScriptUtils.getConstant("COMMENT_PANEL_HEIGHT"));
customActionModel.setModalPanelWidth(witdhModalPanel);
customActionModel.setModalPanelHeight(heightModalPanel);

// Set title of custom panel
customActionModel.setModalPanelTitle(BundleUtils.getTranslation("panel_comment_title"));

customActionModel.getModalPanelModel().put("displayPanelComment", true);

scriptLogger.debug("Script groovy de type initialisation de panel : "+SCRIPT_NAME+" --- End");