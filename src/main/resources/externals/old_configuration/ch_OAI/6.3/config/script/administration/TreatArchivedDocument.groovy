import org.apache.commons.lang.time.DateUtils
import java.text.SimpleDateFormat;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.jcorbairs.*;


/*********************************************************************************************
 *                   CHECK - DOCUMENT ARCHIVE INIT
 **********************************************************************************************
 Author   : MTO

 Description :
 - Permet le retrait de la boite d'archive lorqu'un document est archiv� et date d'archivage de plus de 3 mois

 CONTENT_TYPE                        : Nom du type de contenu
 WKF_STATE_FIELD		         	 : Nom du champs AIRS de l'etat Workflow
 D_ARCHIVE_FIELD                     : Nom du champs AIRS de la date d'archivage
 WKF_STATE_ARCH_ACTIVITY      		 : Identifiant AIRS de l'etat de l'activit�e Archiver
 MONTH_AFTER                         : Nombre de mois apr�s la date d'archivage
 **********************************************************************************************/
final String CONTENT_TYPE = "DOCUMENT_ASSURE";
final String WKF_STATE_FIELD = "WKF_STATUS";
final String WKF_STATE_ARCH_ACTIVITY = "Archive";
final String D_ARCHIVE_FIELD = "D_ARCHIVE";
final Integer MONTH_AFTER = 3;

/**
 * TRAITEMENT
 ************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - CHECK - DOCUMENT ARCHIVE INIT - START");


try{
    scriptLogger.debug("[CUSTOM ACTION] - CHECK - DOCUMENT INIT - Traitement des documents archives");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    ArrayList<Domain> listDomain = new ArrayList<Domain>();
    listDomain.add(new Domain(DossierCoreContext.getAdminJeton(), CONTENT_TYPE));
    Request req = new Request();
    req.addLocution(WKF_STATE_FIELD, Request.Operator.OPERATOR_EQUAL, WKF_STATE_ARCH_ACTIVITY);
    ExecuteInSearch search = new ExecuteInSearch(DossierCoreContext.getAdminJeton(), req, listDomain);
    int count = search.getNbResults();
    if (count >= 0)
    {
        for(int i = 0 ; i < count ; i++) {
            try{
                Document doc = search.getDocumentByIndex(i);
                Date dateArchive = DateUtils.addMonths(sdf.parse(doc.getContent().getFieldValue(D_ARCHIVE_FIELD)), MONTH_AFTER);
                if (dateArchive.compareTo(new Date())<0) {
                    scriptLogger.debug("Traitement du document -- id : " + doc.getId());
                    defineIndex(doc, WKF_STATE_FIELD, "");
                    doc.updateContent();
                }
            }catch(Exception e){
                scriptLogger.error("[CUSTOM ACTION] - CHECK - DOCUMENT INIT - ERROR - Traitement impossible du document impossible : ", e);
                return;
            }
        }
    }
}catch(Exception e){
    scriptLogger.error("[CUSTOM ACTION] - CHECK - DOCUMENT INIT - ERROR - Traitement impossible : ", e);
    return;
}


scriptLogger.debug("[CUSTOM ACTION] - CHECK - DOCUMENT INIT - END");

/**
 * METHODES
 ************************************************************************************/

private void defineIndex(Document doc, String index, String value)
{
    try
    {
        doc.getContent().modifyFieldValue(index, value);

    } catch (Exception e)
    {
        try
        {
            doc.getContent().addFieldValue(index, value);

        } catch (Exception ex)
        {
            scriptLogger.error("Erreur a l'ajout de la valeur " + value + " pour l'index " + index + ".");
        }
    }
}