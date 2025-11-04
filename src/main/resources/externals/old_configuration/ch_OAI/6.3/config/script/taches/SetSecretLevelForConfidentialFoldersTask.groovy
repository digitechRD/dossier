import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.Document

import Constants
import Methods

/*************************************************************************************************
 * 			    			SetSecretLevelForConfidentialFoldersTask - EXEC
 **************************************************************************************************
 Date : 02.08.2016
 Auteur : MTO

 Description : Permet de mettre à jour le niveau de secret pour les dossiers confidentiels
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET SECRET LEVEL FOR CONFIDENTIAL FOLDERS TASK - START");

/**
 * INITIALISATION
 **************************************************************************************************/
List<String> listFolderToTreat = null;
List<String> listConfidentialDocument = null;
List<String> listConfidentialWebAI = new ArrayList<String>();

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
    listFolderToTreat = Methods.getRequestInWebAI(null, "confidential");
    listConfidentialDocument = Methods.getListNSSDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_CONFIDENTIAL_DOCUMENTS);
    if (!listFolderToTreat.isEmpty()) {
        for (String nss : listFolderToTreat) {
            try {
                nss = nss.replaceAll("[^0-9\\*\\+]", "");
                listConfidentialWebAI.add(nss);
                // Traitement des dossiers confidentiels
                List<Integer> listDocumentsToTreat = Methods.getDocumentsListIdByNSS(Constants.CTY_DOCUMENT_ASSURE, nss);
                Document doc = null;
                for (Integer id : listDocumentsToTreat) {
                    try {
                        doc = new Document(DossierCoreContext.getAdminJeton(), id);
                        if (doc.getSecretLevel() == Constants.SECRET_LEVEL_DEFAULT) {
                            doc.setSecretLevel(Constants.SECRET_LEVEL_CONFIDENTIEL);
                            doc.updateContent();
                            scriptLogger.debug("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - Mise en confidentialité - Document n° "+id+" / Dossier n°"+nss);
                        }
                    } catch (Exception e) {
                        scriptLogger.error("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - ERROR lors de l'ajout de la confidentialité pour le document n° :  " + id, e);
                    }
                }


            } catch (Exception ex) {
                scriptLogger.error("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - ERROR Dossier n° : " + nss, ex);
            }
        }
    }

    if(!listConfidentialDocument.isEmpty()){
        for(String nss : listConfidentialDocument) {
            try {
                // Vérification des dossiers en cours s'ils sont toujours confidentiels
                //scriptLogger.debug("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask " + nss + " / " + listConfidentialWebAI);
                if (!listConfidentialWebAI.contains(nss)) {
                    List<Integer> listDocumentsWithoutConfidential = Methods.getDocumentsListIdByNSS(Constants.CTY_DOCUMENT_ASSURE, nss);
                    Document doc = null;
                    for (Integer id : listDocumentsWithoutConfidential) {
                        try {
                            doc = new Document(DossierCoreContext.getAdminJeton(), id);
                            if (doc.getSecretLevel() == Constants.SECRET_LEVEL_CONFIDENTIEL) {
                                doc.setSecretLevel(Constants.SECRET_LEVEL_DEFAULT);
                                doc.updateContent();
                                scriptLogger.debug("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - Retrait confidentialité - Document n° " + id + " / Dossier n°" + nss);
                            }
                        } catch (Exception e) {
                            scriptLogger.error("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - ERROR lors du retrait de la confidentialité pour le document n° :  " + id, e);
                        }
                    }
                }
            } catch (Exception ex) {
                scriptLogger.error("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - ERROR Dossier n° : " + nss, ex);
            }
        }
    }
} catch (Exception e) {
    scriptLogger.error("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - ERROR :  ", e);
}

scriptLogger.debug("[CUSTOM ACTION] - SET SECRET LEVEL FOR CONFIDENTIAL FOLDERS TASK - END");
