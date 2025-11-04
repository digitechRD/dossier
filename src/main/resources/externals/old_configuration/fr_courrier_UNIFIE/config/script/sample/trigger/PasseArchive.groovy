import java.util.List;
import java.util.Map;
import java.lang.Double;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.service.IServer;
import org.slf4j.Logger;
import com.digitech.dossier.common.model.backend.airs.IDocument;
import com.digitech.dossier.common.model.backend.airs.IField;
import com.digitech.dossier.common.Utils;

ETAT_FIELD_CODE = "RH_ETAT"
ETAT_DOC_FINAL_VALUE = "730";

if(airsDocument != null)
{
	IField fieldEtat_Doc = airsDocument.getField(ETAT_FIELD_CODE);  
  fieldEtat_Doc.setValue( ETAT_DOC_FINAL_VALUE );
  airsDocument.getFieldMap().put(ETAT_FIELD_CODE, fieldEtat_Doc );
  scriptLogger.warn("Setting field value : " + fieldEtat_Doc.getCode() + " - " + ETAT_DOC_FINAL_VALUE );	  	
}
else
{
		scriptLogger.warn("DOCUMENT NULLLLLLLLLLL");
}

private IServer getServerMgr() {
	return (IServer) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_SERVER_MGR);
}

private com.digitech.dossier.common.service.IDocument getDocumentMgr() {
	return (com.digitech.dossier.common.service.IDocument) ServiceManager.getInstance().getService(com.digitech.dossier.common.service.Constants.SERVICE_AIRS_DOCUMENT_MGR);
}
