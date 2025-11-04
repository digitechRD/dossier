import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.jcorbairs.exception.ConnectionException;
import com.digitech.jcorbairs.exception.IdentificationException;
import com.digitech.jcorbairs.exception.LicenseException;
import com.digitech.jcorbairs.exception.ServerException;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker;

String NSS = "N_NSS";

String requestAirs = search.getAirsRequest(UserContext.getInstance());

ScriptResultValueChecker result = new ScriptResultValueChecker();

//scriptLogger.warn("Requete initiale :  " +requestAirs );

result.setValid( true );

if ((requestAirs != null) && (requestAirs != ""))
{
	if (requestAirs.contains(NSS+"=\""))
	{
		String oldnss = requestAirs.substring(requestAirs.indexOf(NSS+"=")+NSS.length()+2, requestAirs.indexOf("\"", requestAirs.indexOf(NSS+"=")+NSS.length()+2));
		String newnss = treatNss(oldnss);
		String newreq = requestAirs.replace(NSS+"=\""+oldnss, NSS+"=\""+newnss);
		
		search.setAirsRequest(newreq);
		//scriptLogger.warn("Requete finale :  " +newreq );
	}
}

output.setValue( result );

public String treatNss(String nss)
{
	// Enléve tous les caractères non numériques d'une chaine de caractères
	nss = nss.replaceAll("[^0-9\\*\\+]", "");
	return nss;
}