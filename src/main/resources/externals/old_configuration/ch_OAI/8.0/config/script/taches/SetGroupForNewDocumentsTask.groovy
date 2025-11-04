import Constants
import Methods
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.jcorbairs.Document
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin
import com.digitech.jcorbairs.admin.AuthorityListsManager

/*************************************************************************************************
 *   					    			SetGroupForNewDocument - EXEC
 **************************************************************************************************
 Date : 12.04.2016
 Auteur : MTO

 Description : Permet de mettre à jour le groupe du document
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - SET GROUP FOR NEW DOCUMENT TASK - START")

/**
 * INITIALISATION
 **************************************************************************************************/
List<Integer> listDocumentToTreat = null
Integer countDocumentTreating = 0

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  listDocumentToTreat = Methods.getListIdDocumentbyRequest(Constants.DB_AIRS_REQUEST_GET_CREATE_DOCUMENTS)
  scriptLogger.debug("[CUSTOM ACTION] - SET GROUP FOR NEW DOCUMENT TASK - Nombre de documents à traiter : " + String.valueOf(listDocumentToTreat.size()))
  if(!listDocumentToTreat.isEmpty()) {
    for(Integer id : listDocumentToTreat) {
      try {
        Document doc = new Document(DossierCoreContext.getAdminJeton(), id)
        String typeId = null
        try {
          typeId = doc.getContent().getFieldValue(Constants.LIST_TYPES_DOCUMENT_CODE)
        } catch(Exception e) {
          Methods.defineDocumentIndex(doc, Constants.LIST_TYPES_DOCUMENT_CODE, null)
          doc.updateContent()
        }
        if(typeId != null) {
          AuthorityListTermAdmin altm = AuthorityListsManager.loadTerm(DossierCoreContext.getAdminJeton(), Integer.parseInt(typeId))
          String groupCode = altm.getValue5().replaceAll(";", "")
          String groupId = null
          List<AuthorityListTermAdmin> listGroup = AuthorityListsManager.loadTerms(DossierCoreContext.getAdminJeton(), Constants.LIST_GROUPES_DOCUMENT_ID)
          for(AuthorityListTermAdmin alta : listGroup) {
            if(groupCode.equals(alta.getCode())) {
              groupId = String.valueOf(alta.getId())
              break
            }
          }

          Methods.defineDocumentIndex(doc, Constants.LIST_GROUPES_DOCUMENT_CODE, groupId)
          doc.updateContent()
          countDocumentTreating++
        }
      } catch(Exception ex) {
        scriptLogger.warn("[CUSTOM ACTION] - SetGroupForNewDocumentTask - ERROR Document n° :  " + id, ex)
      }
    }
  }
  scriptLogger.debug("[CUSTOM ACTION] - SET GROUP FOR NEW DOCUMENT TASK - Nombre de documents traités : " + countDocumentTreating)
} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - SetGroupForNewDocumentTask - ERROR :  ", e)
}

scriptLogger.debug("[CUSTOM ACTION] - SET GROUP FOR NEW DOCUMENT TASK - END")