import com.digitech.dossier.common.resources.BundleUtils

import java.util.*;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.model.backend.airs.IDocument;

import Methods;

/*************************************************************************************************
 *							Ajout d'un commentaire - INIT
 **************************************************************************************************
 Date : 25.02.2016
 Auteur : MTO

 Description : Permet l'ajout d'un commentaire à un ensemble de documents
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - ADD COMMENT SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/
CustomActionController customActionController = null;
Map<String, Object> data = null;
List<IDocument> docs = null;
IDocument document = null;

try {
	customActionController = Utils.getCustomActionController();
	data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
	Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
	scriptLogger.error("[CUSTOM ACTION] - AddCommentSimpleViewInit - ERREUR : ",e);
	return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
	docs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();
	if(docs.size() == 0){
		Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_documents_empty"), false);
		return;
	}else if(docs.size() > 1){
		Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_selected_one_document_only"), false);
		return;
	}

	data.put("DATA_COMMENT", "");
	data.put("DATA_PAGE", "");
}catch(Exception e){
	Methods.addStateMessage(data, "DATA_ERROR_MSG", BundleUtils.getTranslation("groovy_traitment_init_error"), false);
	scriptLogger.error("[CUSTOM ACTION] - AddCommentSimpleViewInit - ERREUR : ",e);
	return;
}

scriptLogger.debug("[CUSTOM ACTION] - ADD COMMENT SIMPLE VIEW INIT - END");