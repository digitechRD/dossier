/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Methods
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.jcorbairs.admin.AuthorityListTermAdmin

List<com.digitech.jcorbairs.Document> documentResult = getDocument(userContext)

for(Integer i : documentResult) {
  try {
    com.digitech.jcorbairs.Document document = new com.digitech.jcorbairs.Document(userContext.getJeton(), i)
    if(document != null) {

      List<AuthorityListTermAdmin> listValues = AuthorityListTermAdmin.loadTerms(token, 9)
      String description = null
      for(AuthorityListTermAdmin alta : listValues) {
        if(document.getContent().getFieldValue("AL_TYPE").toString().equals(alta.getId().toString())) {
          description = alta.getValue1()
          break
        }
      }

      if(description == null) {
        _scriptLogger.debug("Decription nulle : " + document.getContent().getFieldValue("AL_TYPE").toString())
        return
      }

      Methods.defineDocumentIndex(document, "COM", description)
      document.updateContent()

      _scriptLogger.debug("Traitement document : " + i)
    }
  } catch(Exception e) {
    _scriptLogger.error("Erreur lors du traitement : ", e)
  } finally {
    if(resultSet != null) resultSet.close()
    if(preparedStatement != null) preparedStatement.close()
  }
}

private List<Integer> getDocument(UserCoreContext user) {
  List<Integer> documentResult = new ArrayList<Integer>()
  //String requete = "select refd.doc_id from db2inst1.docref_document_affilie refd inner join db2inst1.doc_document_affilie d on refd.doc_id = d.doc_id where refd.aui_id in (select aui_id from db2inst1.authority_item where aui_code like 'AFA%' OR aui_code like 'PAP%' OR aui_code like 'PCF%' OR aui_code like 'PPC%' OR aui_code like 'PRE%' OR aui_code like 'PRF%') AND d.N_NSS IS NULL AND d.d_creat > to_date('22-11-2015','DD-MM-YYYY')";
  String requete = "select refd.doc_id from db2inst1.docref_document_affilie refd inner join db2inst1.doc_document_affilie d on refd.doc_id = d.doc_id where refd.aui_id in (select aui_id from db2inst1.authority_item where aui_code like 'PIJ%') AND d.d_creat > to_date('22-11-2015','DD-MM-YYYY')"
  return documentResult
}
