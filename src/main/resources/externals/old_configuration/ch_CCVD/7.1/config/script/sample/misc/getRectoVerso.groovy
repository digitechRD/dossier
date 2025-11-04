import com.digitech.dossier.administration.model.backend.Link
import com.digitech.dossier.common.exception.InvalidConfigurationException
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.common.model.backend.params.UpdateContentType
import com.digitech.dossier.common.model.backend.params.UpdateOrga
import com.digitech.dossier.common.service.IServer
import com.digitech.dossier.common.service.ServiceManager
import com.digitech.jcorbairs.Domain

// import standart pour un script de type AIRSFLOW

List<IDocument> documents = new ArrayList<IDocument>()
documents.add(airsDocument)
if(airsDocument != null) {
  documents.addAll(getLinkedDocument(userContext, airsDocument, "RECTO-VERSO"))
}
output.getValue().setDocumentList(documents)
return output


private List<IDocument> getLinkedDocument(UserContext userContext, IDocument document, String linkName) {
  List<IDocument> linkedDocuments = new ArrayList<IDocument>()
  if(linkName == null) {
    return linkedDocuments
  }

  UpdateOrga updateOrga = getUpdateOrga(userContext)
  for(UpdateContentType updateContentType : updateOrga.getContentTypes()) {
    List<Link> updateLinks = updateContentType.getUpdateLinks()
    for(Link updateLink : updateLinks) {
      String curLinkName = updateLink.getName()
      if(!linkName.equals(curLinkName)) {
        continue
      }

      try {
        Domain domain = getServerMgr().getDomain(DossierCoreContext.getAdminJeton(), updateLink.getContentTypeCode())
        Integer docId = document.getAirsDocument().getId()

        List<Integer> linkedDocumentIds = getDocumentsMgr().getLinkedDocuments(UserContext.getInstance(), docId.intValue(), domain, curLinkName,
            updateLink.getDirection())
        for(Integer linkedDocumentId : linkedDocumentIds) {
          IDocument linkedDocument = getDocumentMgr().getDocument(UserContext.getInstance().getJeton(), linkedDocumentId)
          linkedDocuments.add(linkedDocument)
        }
      }
      catch(Exception e) {
        _scriptLogger.error(e.getLocalizedMessage(), e)
      }
    }
  }
  return linkedDocuments
}

private UpdateOrga getUpdateOrga(UserContext userContext) {
  UpdateOrga updateOrga = null
  try {
    updateOrga = DossierCoreContext.getUpdateInfos().getOrganizationOrDefault(userContext.getCurrentOrgId())
  }
  catch(InvalidConfigurationException e) {
    InvalidConfigurationException ex = new InvalidConfigurationException(InvalidConfigurationException.CONFIG_TYPE_UPDATE, e.getMessage())
    _scriptLogger.error(ex.getLocalizedMessage(), ex)
    throw new RuntimeException(ex)
  }
  return updateOrga
}

private com.digitech.dossier.common.service.IDocument getDocumentService() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
}

private IServer getServerMgr() {
  return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR)
}

private com.digitech.dossier.common.service.IDocuments getDocumentsMgr() {
  return (com.digitech.dossier.common.service.IDocuments) ServiceManager.getInstance().getService(
      com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENTS_MGR)
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
  return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(
      com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR)
}
