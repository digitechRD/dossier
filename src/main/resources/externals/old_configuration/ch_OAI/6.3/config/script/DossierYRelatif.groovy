import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.common.model.backend.UserContext;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.script.model.impl.result.ScriptResultValueDocumentInitializer;

import com.digitech.jcorbairs.*;
import com.digitech.jcorbairs.admin.UsersManager;
import com.digitech.jcorbairs.admin.UserAdmin;
import com.digitech.jcorbairs.Application.ApplicationName;
import com.digitech.jcorbairs.exception.IdentificationException

import com.ibm.db2.jcc.DB2Driver;

import org.apache.commons.codec.binary.Base64;

/********** VARIABLES ************/

String DOSSIER_FIELD_CODE = "N_NSS";
String TOMCAT_SERVER = "https://eduairs4.gilai.oai.ch/AirsDossier/faces/redirect.jsp?authentication=";
String ORGID = "&orgId=";
String OUTCOME = "&outcome=gotoSimpleView";
String CTY = "&cty=DOCUMENT_ASSURE"
String FIELD_VALUE = "&field1=N_NSS&value1=";

String LINK = "LINK";
String LINK_LABEL = "LINK_LABEL";

/********************************/


ScriptResultValueDocumentInitializer result = output.getValue();
CustomActionController customActionController = Utils.getCustomActionController();
Map<String, Object> data = customActionController.getModel().getModalPanelModel();

scriptLogger.error("------------------------------- INTITIALISATION DOSSIER Y RELATIF ------------------------------");

try
{

	

	// LISTE DOCUMENTS SELECTIONNE
	List<com.digitech.dossier.common.model.backend.airs.IDocument> selectedDocs = Utils.getSearchResultController().getModel().getSearchResultTableModel().getSelectedDocuments();

	if(selectedDocs.size() > 1)
	{
		throw new Exception("Plusieurs documents ont \u00e9t\u00e9 s\u00e9lectionn\u00e9s. Le dossier ne sera pas affich\u00e9.");
	}

	com.digitech.dossier.common.model.backend.airs.IDocument document = selectedDocs.get(0);

	//création du lien permettant de voir l'ensemble du dossier AIRS du document courant
	//on récupère la valuer de dossier
	String foldervalue = document.getField(DOSSIER_FIELD_CODE).getValue().toString();
	scriptLogger.error("DOSSIER COURANT : " + foldervalue);
	Token jeton = getAdmintoken();
	UserAdmin user = UsersManager.load(jeton,userContext.getUser().getId());
	scriptLogger.error("AUTH USER COURANT : " + user.getLogin() + ":" + user.getPassword());
	//on récupère la chaine d'authentification de l'utilisateur courant
	String auth = encode64(user.getLogin() + ":" + Methods.getPassWordOfUser(user.getLogin()));
	scriptLogger.error("AUTH USER COURANT BASE 64 : " + auth);
	//on récupère l'organisation courante
	int orgid = userContext.getCurrentOrgId();
	scriptLogger.error("CURRENT ORGA : " + orgid);
	//on concatene les éléments pour avoir le lien
	String folderlink = TOMCAT_SERVER + auth + ORGID + orgid + OUTCOME + CTY + FIELD_VALUE + foldervalue;
	scriptLogger.error("PASSAGE DE CONTEXTE OBTENU : " + folderlink);
	//on formate le nom du lien sur la JSP avec le numero de dossier
	String linklabel = "" + foldervalue;
	scriptLogger.error("LABEL DE LIEN OBTENU : " + linklabel);
	//on pousse le lien vers la JSP
	data.put(LINK_LABEL, linklabel);
	data.put(LINK, folderlink);	
	data.put("state","OK");	
	deconnectToAirs(jeton);
	
}
catch(Exception e)
{
	result.setMessageSeverity(com.digitech.dossier.script.model.IScriptResultValueModel.Severity.ERROR);
	result.setMessageSummary("Erreur a l'affichage du dossier y relatif : " + e.getMessage());
	scriptLogger.error("Erreur a l'initialisation du dossier y relatif : ",e);
	output.setValue(result);
}

scriptLogger.error("----------------------------- FIN INTITIALISATION DOSSIER Y RELATIF ------------------------------\n\n\n");

private String encode64(String auth)
{
	String res;

	byte[] encodedBytes = Base64.encodeBase64(auth.getBytes());
	res = new String (encodedBytes);
	scriptLogger.error ("CHAINE ENCODEE EN BASE 64 : " + res);

	return res;
}

private  Token getAdmintoken()
{
			Token jeton = null;
	        
	        try
	        {
	            String login = "VSAIRS";
	            String pass = "D1g1tech";

	            com.digitech.jcorbairs.Connection co = new com.digitech.jcorbairs.Connection("ai0airs5", 5000);
	            jeton = co.login(login, pass, Application.ApplicationName.AIRS);
	        }
	        catch (Exception e)
	        {
	            scriptLogger.fatal("Impossible de se connecter au serveur AIRS(GED). "+ e);
	        }
	        
	        return jeton;
}

  public void deconnectToAirs(Token jeton)
	  {
		  scriptLogger.info("Déconnexion du serveur AIRS...");
		    try {
		      com.digitech.jcorbairs.Connection.logout(jeton);
		    } catch (IdentificationException ex) {
		      scriptLogger.fatal(ex.getMessage());
		    }
		    scriptLogger.info("Déconnecté du serveur AIRS");
	  }
