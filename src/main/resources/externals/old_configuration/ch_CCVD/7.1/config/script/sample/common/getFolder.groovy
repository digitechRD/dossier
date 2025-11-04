import com.digitech.airs3dossiers.airs.AirsFolder
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.service.ServiceManager

// import standart pour un script de type AIRSFLOW

List<IDocument> listDoc = new ArrayList<IDocument>()


if(airsDocument != null) {
  AirsFolder airsFolder = null
  if(!airsDocument.isFolder()) {
    List<Integer> parentListId = airsDocument.getAirsDocument().getParentListId()
    if(parentListId != null && parentListId.size() == 1) {
      IDocument docToAdd = getDocumentService().getDocument(userContext.getJeton(), parentListId.get(0).intValue())
      airsFolder = (AirsFolder) docToAdd.getAirsDocument()
    }
  }
  else if(airsDocument.isFolder()) {
    airsFolder = (AirsFolder) airsDocument.getAirsDocument()
  }


  if(airsFolder != null) {
    listDoc.add(airsDocument)

    List<Integer> childIds
    try {
      childIds = airsFolder.getChildListId()
      if(childIds != null) {
        for(Integer childId : childIds) {
          IDocument docToAdd = getDocumentService().getDocument(userContext.getJeton(), childId.intValue())
          listDoc.add(docToAdd)
        }
      }
    }
    catch(Exception e) {
      _scriptLogger.error(e.getLocalizedMessage(), e)
    }
  }
}
output.getValue().setDocumentList(listDoc)
return output

com.digitech.dossier.common.service.IDocument getDocumentService() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
}


