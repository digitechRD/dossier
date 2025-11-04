import java.util.ArrayList;
import com.digitech.dossier.administration.model.backend.Link;
import com.digitech.dossier.common.exception.InvalidConfigurationException
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.DossierCoreContext
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.common.service.IServer;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.model.backend.params.UpdateOrga;
import com.digitech.dossier.common.model.backend.params.UpdateContentType;
import com.digitech.jcorbairs.Domain;
import com.digitech.airs3dossiers.airs.AirsFolder;
import com.digitech.airs3dossiers.airs.AirsFile;

List<IDocument> listDoc = new ArrayList<IDocument>();

// code du lien recto verso :
private static String LINK_CODE = "RECTO-VERSO"


//code des champs que l'on va traiter
private static String SENSIMG_FIELD_CODE = "SEN_IMG";
private static String TYP_DOC_FIELD_CODE = "TYP_DOC";

// valeur des champs a traiter
private static String SENSIMG_FIELD_VALUE_RECTO   = "303";
private static String SENSIMG_FIELD_VALUE_VERSO   = "304";
private static String TYP_DOC_FIELD_VALUE_CHEQUE  = "203";
private static String TYP_DOC_FIELD_VALUE_REMISE  = "204";


/*
 * Generation de la remise complete pour OCSD :
 * 
 *  la 1ère page correspond uniquement à la remise,
 *  les pages suivantes aux chèques, avec pour chaque page, le recto, en haut, et le verso, en bas, d'un même chèque. 
 * 
 */

if(airsDocument != null) {
	AirsFolder airsFolder = null ;	
	/*  premier test, on regarde si on est sur un folder ou sur un file 
	 * tout le traitement se fait a partir du root, donc le but et de recuperer 
	 * le pere si on est sur un file, et de conserver le folder si on est sur un folder
	 * */
	
	if(!airsDocument.isFolder())
	{		// cas du file, on recupere son pere
		List<Integer> parentListId = airsDocument.getAirsDocument().getParentListId();	  
		if( parentListId != null && parentListId.size() == 1)
		{
			IDocument docToAdd = getDocumentService().getDocument(userContext.getJeton(), parentListId.get(0).intValue());
			airsFolder = (AirsFolder) docToAdd.getAirsDocument();
		}
	}
	else if(airsDocument.isFolder())
	{// cas du folder, on le conserve
	  airsFolder = (AirsFolder) airsDocument.getAirsDocument();
	}

	
	
	if(  airsFolder != null  ) {
		listDoc.add(airsDocument);	
/*
 *  debut du traitement, l'idée etant de trier les documents fils suivant les specs du clients
 * 
 * */
		List<Integer> childIds;
		try {
			childIds = airsFolder.getChildListId();
			List<IDocument> docIds_trait2 = new ArrayList<IDocument>();
			if(childIds != null) {
				// on ne traite que les remise dans un premier temps
				for(Integer childId : childIds) {
					// pour chacun des documents fils, on va regarder ce que c'est ( remise r/v, cheque r/v )  
					IDocument docToAdd = getDocumentService().getDocument(userContext.getJeton(), childId.intValue());
					String typDocValue = docToAdd.getField( TYP_DOC_FIELD_CODE ).getValue();
					String sensImgValue = docToAdd.getField( SENSIMG_FIELD_CODE ).getValue();
					
					scriptLogger.debug(" [OCSD] - getFolder - " +	 TYP_DOC_FIELD_CODE + " : " + typDocValue + "_" + SENSIMG_FIELD_CODE + " : " + sensImgValue);
					
					if( typDocValue.equalsIgnoreCase( TYP_DOC_FIELD_VALUE_REMISE )
					&& sensImgValue.equalsIgnoreCase(SENSIMG_FIELD_VALUE_RECTO )        )
					{
						scriptLogger.debug(" [OCSD] - getFolder - Addinfg docId : " + childId + "( Remise ) to final list.")
						listDoc.add(docToAdd) ;
					}
					else if( typDocValue.equalsIgnoreCase( TYP_DOC_FIELD_VALUE_CHEQUE ) 
					    && sensImgValue.equalsIgnoreCase(SENSIMG_FIELD_VALUE_RECTO ) )
					{
						scriptLogger.debug(" [OCSD] - getFolder - Addinfg docId : " + childId + " to check list.")
						docIds_trait2.add( docToAdd );
					}					
				}
				
				for(IDocument docTocheck : docIds_trait2) {
					// pour chacun des documents de cette liste, on recupere le document lié
					listDoc.add(docTocheck) ;
					scriptLogger.debug(" [OCSD] - getFolder - Addinfg docId : " + docTocheck.getAirsRefId() + " ( Check ) to final list.")
					
					for( IDocument docLinked : getLinkedDocument( UserContext.getInstance(), docTocheck, LINK_CODE ))
					{
						scriptLogger.debug(" [OCSD] - getFolder - Addinfg docId : " + docLinked.getAirsRefId() + " ( Check _ verso ) to final list.")
						listDoc.add( docLinked ); 
					}
				}				
			}
		}
		catch(Exception e) {
			scriptLogger.error(e.getLocalizedMessage(), e);
		}
	}
}
output.getValue().setDocumentList( listDoc );
return output;


private List<IDocument> getLinkedDocument(UserContext userContext, IDocument document, String linkName) {
	List<IDocument> linkedDocuments = new ArrayList<IDocument>();
	if (linkName == null) {
		return linkedDocuments;
	}
	
	UpdateOrga updateOrga = getUpdateOrga(userContext);
	for(UpdateContentType updateContentType : updateOrga.getContentTypes()) {
		List<Link> updateLinks = updateContentType.getUpdateLinks();
		for(Link updateLink : updateLinks) {
			String curLinkName = updateLink.getName();
			if (!linkName.equals(curLinkName)) {
				continue;
			}
			
			try {
				Domain domain = getServerMgr().getDomain(DossierCoreContext.getAdminJeton(), updateLink.getContentTypeCode());
				Integer docId = document.getAirsDocument().getId();
				
				List<Integer> linkedDocumentIds = getDocumentsMgr().getLinkedDocuments(UserContext.getInstance(), docId.intValue(), domain, curLinkName,
						updateLink.getDirection());
				for(Integer linkedDocumentId : linkedDocumentIds) {
					IDocument linkedDocument = getDocumentService().getDocument(UserContext.getInstance().getJeton(), linkedDocumentId);
					linkedDocuments.add(linkedDocument);
				}
			}
			catch(Exception e) {
				scriptLogger.error(e.getLocalizedMessage(), e);
			}
		}
	}
	return linkedDocuments;
}

private UpdateOrga getUpdateOrga(UserContext userContext) {
	UpdateOrga updateOrga = null;
	try {
		updateOrga = DossierCoreContext.getUpdateInfos().getOrganizationOrDefault(userContext.getCurrentOrgId());
	}
	catch(InvalidConfigurationException e) {
		InvalidConfigurationException ex = new InvalidConfigurationException(InvalidConfigurationException.CONFIG_TYPE_UPDATE, e.getMessage());
		scriptLogger.error(ex.getLocalizedMessage(), ex);
		throw new RuntimeException(ex);
	}
	return updateOrga;
}

private com.digitech.dossier.common.service.IDocument getDocumentService() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}

private IServer getServerMgr() {
	return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocuments getDocumentsMgr() {
	return (com.digitech.dossier.common.service.IDocuments) ServiceManager.getInstance().getService(
	com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENTS_MGR);
}


