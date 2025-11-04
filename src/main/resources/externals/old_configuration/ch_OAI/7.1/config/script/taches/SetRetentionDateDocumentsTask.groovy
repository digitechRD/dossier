import Constants
import Methods
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.Document

import java.text.SimpleDateFormat

/*************************************************************************************************
 * 			    			    SetRetentionDateDocumentsTask - EXEC
 **************************************************************************************************
 Date : 04.08.2016
 Auteur : MTO

 Description : Permet de mettre à jour la date de rétention pour les documents nouveaux et restaurés
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET RETENTION DATE DOCUMENTS TASK - START")

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listNewDocuments = null
List<Integer> listNewFolders = null
List<Integer> listRemoveDocuments = null
List<Integer> listHasRemoveFolders = null
List<Integer> listHasBeenRemoveDocuments = null

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  listNewDocuments = Methods.getDocumentsListIdByDay(Constants.CTY_DOCUMENT_ASSURE, new Date(), false)
  //listNewFolders = Methods.getDocumentsListIdByDay(Constants.CTY_FOLDER_ASSURE, new Date(), false);
  listRemoveDocuments = Methods.getDocumentsListIdByDay(Constants.CTY_FOLDER_ASSURE, new Date(), true)
  listHasRemoveFolders = Methods.getFoldersHasRemove(new Date())
  listHasBeenRemoveDocuments = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_DELETED_DOCUMENTS)

  // Gestion des nouveaux documents
  if(!listNewDocuments.isEmpty()) {
    for(Integer id : listNewDocuments) {
      String dateNaissance = null
      try {
        // Mise à jour du document
        Document document = new Document(DossierCoreContext.getAdminJeton(), id)

        Calendar calendarDocument = Calendar.getInstance()
        calendarDocument.setTime(new Date())
        calendarDocument.add(Calendar.YEAR, Constants.DGD_DESACTVATION_YEAR)
        calendarDocument.add(Calendar.DATE, -1)
        Date retentionDateDocument = calendarDocument.getTime()

        Calendar yesterdayDocument = Calendar.getInstance()
        yesterdayDocument.setTime(new Date())
        yesterdayDocument.add(Calendar.DATE, -1)
        Date yesterdayDateDocument = yesterdayDocument.getTime()

        Methods.
            defineDocumentIndex(document, Constants.FIELD_DATE_RETENTION_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(retentionDateDocument))
        Methods.defineDocumentIndex(document, Constants.FIELD_DATE_RETENTION_MODIFY_CODE,
                                    new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(yesterdayDateDocument))
        document.setSecretLevel(Constants.SECRET_LEVEL_DEFAULT)
        scriptLogger.debug("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - Ajout de la date de rétention - Document n° " + id)
        document.updateContent()

        // Mise à jour du dossier du document
        List<Integer> documentsList = Methods.getDocumentsListIdByNSS(Constants.CTY_FOLDER_ASSURE, Methods.getFieldValue(document, Constants.FIELD_NSS_CODE))
        if(!documentsList.isEmpty()) {
          Document folder = new Document(DossierCoreContext.getAdminJeton(), Methods.getDocumentsListIdByNSS(Constants.CTY_FOLDER_ASSURE, Methods.getFieldValue(
              document, Constants.FIELD_NSS_CODE)).get(0))
          if(Methods.getFieldValue(folder, Constants.FIELD_DATE_RETENTION_CODE) == null) {
            // Date de rétention depuis la date de naissance
            List<String> datesNaissance = Methods.getRequestInWebAI(Methods.getFieldValue(document, Constants.FIELD_NSS_CODE), "birthday")
            if(!datesNaissance.isEmpty() && datesNaissance != null) {
              dateNaissance = Methods.getRequestInWebAI(Methods.getFieldValue(document, Constants.FIELD_NSS_CODE), "birthday").get(0)
              Calendar calendarFolder = Calendar.getInstance()
              calendarFolder.setTime(new SimpleDateFormat(Constants.DATE_FORMAT_INPUT).parse(dateNaissance))
              calendarFolder.add(Calendar.YEAR, Constants.DGD_MAXIMAL_RENTETION_YEAR)
              Date retentionDateFolder = calendarFolder.getTime()
              Methods.defineDocumentIndex(folder, Constants.FIELD_DATE_RETENTION_FINAL_CODE,
                                          new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(retentionDateFolder))
              Methods.defineDocumentIndex(folder, Constants.FIELD_DATE_RETENTION_CODE,
                                          new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(retentionDateDocument))
              Methods.
                  defineDocumentIndex(folder, Constants.FIELD_DATE_RETENTION_MODIFY_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
              scriptLogger.debug(
                  "[CUSTOM ACTION] - SetRetentionDateDocumentsTask - Ajout de la date de rétention - Dossier n° " + folder.getId() + " du Document n° " + id)
              folder.updateContent()
            }
            else {
              scriptLogger.error("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - Ajout de la date de rétention - Dossier n° " +
                                     folder.getId() + " --> Date de naissance inconnu")
            }
          }
        }
      } catch(Exception e) {
        scriptLogger.error("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - ERROR lors de l'ajout de la date de rétention pour le document n° :  " +
                               id + " - Date de naissance : " + dateNaissance, e)
      }
    }
  }

  // Gestion des documents supprimés
  if(!listRemoveDocuments.isEmpty()) {
    for(Integer id : listNewFolders) {
      try {
        Document document = new Document(DossierCoreContext.getAdminJeton(), id)

        Calendar calendarDocument = Calendar.getInstance()
        calendarDocument.setTime(new Date())
        calendarDocument.add(Calendar.YEAR, Constants.DGD_MINIMAL_RENTETION_YEAR)
        Date retentionDateDocument = calendarDocument.getTime()

        Methods.
            defineDocumentIndex(document, Constants.FIELD_DATE_RETENTION_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(retentionDateDocument))
        Methods.defineDocumentIndex(document, Constants.FIELD_DATE_RETENTION_MODIFY_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
        document.setSecretLevel(100)
        document.updateContent()
        scriptLogger.debug("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - Ajout de la date de rétention - document supprimé n° " + id)
      } catch(Exception e) {
        scriptLogger.
            error("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - ERROR lors de l'ajout de la date de rétention pour le document supprimé n° :  " + id, e)
      }
    }
  }

  // Gestion des documents à supprimer car date de rétention échue
  if(!listHasRemoveFolders.isEmpty()) {
    for(Integer id : listHasRemoveFolders) {
      try {
        Document document = new Document(DossierCoreContext.getAdminJeton(), id)
        document.destroy()
        scriptLogger.debug("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - Supression du document n° " + id)
      } catch(Exception e) {
        scriptLogger.error("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - ERROR lors de la suppression du document n° :  " + id, e)
      }
    }
  }

  // Gestion des documents qui ont été supprimé aujourd'hui
  if(!listHasBeenRemoveDocuments.isEmpty()) {
    for(Integer id : listHasBeenRemoveDocuments) {
      try {
        Document document = new Document(DossierCoreContext.getAdminJeton(), id)
        Methods.defineDocumentIndex(document, Constants.FIELD_DATE_RETENTION_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
        Methods.defineDocumentIndex(document, Constants.FIELD_DATE_RETENTION_MODIFY_CODE, new SimpleDateFormat(Constants.DATE_FORMAT_AIRS).format(new Date()))
        scriptLogger.debug("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - Ajout de la date de rétention du document supprimé n° " + id)
        document.updateContent()
      } catch(Exception e) {
        scriptLogger.error("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - ERROR lors de la suppression du document n° :  " + id, e)
      }
    }
  }

} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - SetRetentionDateDocumentsTask - ERROR :  ", e)
}

scriptLogger.debug("[CUSTOM ACTION] - SET RETENTION DATE DOCUMENTS TASK - END")
