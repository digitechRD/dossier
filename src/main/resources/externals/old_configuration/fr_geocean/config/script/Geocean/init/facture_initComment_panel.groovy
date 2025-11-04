// ****************************************************************************
// Projet : GEOCEAN - Airs Capture / Airs Dossier
// Objet : Script appelé lors d'une action avec saisie d'un commentaire sur
//         un type de document Facture
// Descritpions :
//  - 
// ****************************************************************************
//                          Suivi des modifications
// ****************************************************************************
//    Date    |   Qui   | Version |                Commentaire
// 14/01/2014 |   PRO   |   1.0   | Création du scripte

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.utils.FieldUtils;
import com.digitech.dossier.common.utils.UserUtils;
import com.digitech.dossier.common.resources.BundleUtils;
import com.digitech.dossier.common.model.backing.CustomActionModel;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.jcorbairs.User;

import static ScriptUtils

// Mettre false lors de la mise en production
boolean afficheLesLog = true;

// Paramètres passés au script
org.slf4j.Logger log = scriptLogger;
UserCoreContext usrContext = userContext;
IDocument theDocument = document;

log.debug("In facture_initComment_panel.groovy v1.0 (id [" + theDocument.getAirsRefId() + "])");

// Set parameter for custom panel
CustomActionModel customActionModel = Utils.getCustomActionController().getModel();
customActionModel.getModalPanelModel().put("comment","");

// Set size of custom panel
Integer witdhModalPanel = Integer.parseInt(ScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_WIDTH"));
Integer heightModalPanel = Integer.parseInt(ScriptUtils.getConstant("CUSTOM_PANEL_COMMENT_HEIGHT"));
customActionModel.setModalPanelWidth(witdhModalPanel);
customActionModel.setModalPanelHeight(heightModalPanel);

// Set title of custom panel
customActionModel.setModalPanelTitle(BundleUtils.getTranslation("modalPanelComment_title"));

// Set parameter to fix display bug (multiple modal panel one after the other)
customActionModel.getModalPanelModel().put("displayPanelComment", true);

log.debug("Out facture_initComment_panel.groovy");
