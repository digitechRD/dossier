import Constants
import Methods
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.DocumentAccess

/*************************************************************************************************
 *   					    			SetSecretLevelForLFATask - EXEC
 **************************************************************************************************
 Date : 02.08.2016
 Auteur : MTO

 Description : Permet de mettre à jour le niveau de secret pour la visualisation des documents LFA
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET SECRET LEVEL FOR LFA TASK - START")

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null
List<Integer> listLFADocument = null

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  listDocumentToTreat = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_NEW_LFA_DOCUMENTS)
  if(!listDocumentToTreat.isEmpty()) {
    for(Integer id : listDocumentToTreat) {
      try {
        Document doc = new Document(DossierCoreContext.getAdminJeton(), id)
        try {
          for(DocumentAccess documentAccess : doc.getUserAccessList()) {
            scriptLogger.debug("[CUSTOM ACTION] - SetSecretLevelForLFATask - DEBUG : " + documentAccess.getId() + " - " + documentAccess.getAccessLevel())
            doc.getUserAccessList().remove(documentAccess)
          }

          doc.getUserAccessList().clear()
          doc.setUserAccess(null)

          scriptLogger.debug("[CUSTOM ACTION] - SetSecretLevelForLFATask - DEBUG Count : " + doc.getUserAccessList().size())

        } catch(Exception ex) {
          scriptLogger.warn("[CUSTOM ACTION] - SetSecretLevelForLFATask - ERROR lors de la suppresion du DocumentAcess n° " + id, ex)
        }
        doc.setSecretLevel(Constants.SECRET_LEVEL_LFA)
        doc.updateContent()
        scriptLogger.debug("[CUSTOM ACTION] - SetSecretLevelForConfidentialFoldersTask - Ajout niveau de secret LFA - Document n° " + id)
      } catch(Exception ex) {
        scriptLogger.warn("[CUSTOM ACTION] - SetSecretLevelForLFATask - ERROR lors de l'ajout du niveau de secret LFA Document n° " + id, ex)
      }
    }
  }
} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - SetSecretLevelForLFATask - ERROR :  ", e)
}

scriptLogger.debug("[CUSTOM ACTION] - SET SECRET LEVEL FOR LFA TASK - END")