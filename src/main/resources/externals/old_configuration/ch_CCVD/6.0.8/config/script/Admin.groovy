
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.List;
import java.util.ArrayList;
import Methods;
import com.digitech.dossier.common.model.backend.UserCoreContext
import com.digitech.dossier.common.model.backend.airs.IDocument
import com.digitech.dossier.workflow.model.IWFTaskModel
import Constants;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.akazi.flowmind.api.DataSet;
import com.akazi.flowmind.api.DataSetException;
import com.akazi.flowmind.api.InvalidNameException;
import com.akazi.flowmind.api.InvalidValueException;
import com.digitech.dossier.common.model.backend.DossierCoreContext;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.service.IUser;
import com.digitech.dossier.common.service.ServiceManager;
import com.digitech.dossier.workflow.ConstantsWF;
import com.digitech.dossier.workflow.model.IWFProcessModel;
import com.digitech.dossier.workflow.model.IWFTaskModel;
import com.digitech.dossier.workflow.service.IWFProcessService;
import com.digitech.dossier.workflow.service.IWFSearchService;
import com.digitech.dossier.workflow.service.IWFUpdateService;
import com.digitech.jcorbairs.Domain;
import com.digitech.jcorbairs.Request;
import com.digitech.jcorbairs.Search;
import com.digitech.jcorbairs.Token;
import com.digitech.jcorbairs.admin.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


//PreparedStatement preparedStatement = null;
//ResultSet resultSet = null;
List<com.digitech.jcorbairs.Document> documentResult = getDocument(userContext);

/*
Class.forName(Constants.DB_GLOBAZ_DRIVER);
Connection connection = null;
try{
	connection = DriverManager.getConnection(Constants.DB_GLOBAZ_URL, Constants.DB_GLOBAZ_USERNAME, Constants.DB_GLOBAZ_PASSWORD);
}catch(Exception e){
	scriptLogger.error("Erreur connection : ",e);
} 

if(connection != null){
*/
	for(Integer i : documentResult){
		try{
			com.digitech.jcorbairs.Document document = new com.digitech.jcorbairs.Document(userContext.getJeton(), i);
			if(document != null)
			{	
				/*String nip = document.getContent().getFieldValue("N_AFF");
				String nss = null;
				String title = null;
				String lastName = null;
				String firstName = null;
				String adress = null;
				preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_NSS_BY_NAFF);
				preparedStatement.setString(1, nip);
				resultSet = preparedStatement.executeQuery();
				if(resultSet.next()){
					nss = resultSet.getString(1).trim().replaceAll("\\.", "");
				}else{
					//result.setMessageSeverity(IScriptResultValueModel.Severity.ERROR);
					//result.setMessageDetail("ERREUR - Le NIP suivant n'est pas valide : "+nip);
					scriptLogger.debug("[CUSTOM ACTION] - SYNCHRONIZATION WEB@AVS SIMPLE VIEW EXEC - ERROR : Le NIP suivant n'est pas valide : "+nip);
					//return;	
				}


				preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_IDENTITY_BY_NAFF);
				preparedStatement.setString(1, nip);
				resultSet = preparedStatement.executeQuery();
				if(resultSet.next()){
					// Définition du titre
					if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MR) title = Constants.LIST_TITLE_ITEM_MR_ID;
					else if(resultSet.getInt(1) == Constants.DB_GLOBAZ_CODE_TITLE_MME) title =  Constants.LIST_TITLE_ITEM_MME_ID;

					// Définition du nom et prénom
					lastName = resultSet.getString(2);
					firstName = resultSet.getString(3);
				}

				// Définition des adresses
				preparedStatement = connection.prepareStatement(Constants.DB_GLOBAZ_REQUEST_GET_LISTS_ADRESS_BY_NAFF);
				preparedStatement.setString(1, nip);
				resultSet = preparedStatement.executeQuery();
				adress = "";
				if(resultSet.next()){
					if("".equals(adress)) adress = resultSet.getString(1);
					else adress += " ; "+resultSet.getString(1);
				}
				
				Methods.defineDocumentIndex(document, Constants.FIELD_NSS_CODE, nss);
				Methods.defineDocumentIndex(document, Constants.LIST_TITLE_CODE, title);
				Methods.defineDocumentIndex(document, Constants.FIELD_LASTNAME_AFF_CODE, lastName);
				Methods.defineDocumentIndex(document, Constants.FIELD_FIRSTNAME_AFF_CODE, firstName);
				Methods.defineDocumentIndex(document, Constants.FIELD_ADRESS_AFF_CODE, adress);*/
				
				List<AuthorityListTermAdmin> listValues = AuthorityListTermAdmin.loadTerms(token, 9);
				String description = null;
                for (AuthorityListTermAdmin alta : listValues) {
                    if (document.getContent().getFieldValue("AL_TYPE").toString().equals(alta.getId().toString())) {
                        description = alta.getValue1();
                        break;
                    }
                }
				
				if(description == null){
					scriptLogger.debug("Decription nulle : "+document.getContent().getFieldValue("AL_TYPE").toString());
					return;
				}
				
				Methods.defineDocumentIndex(document, "COM", description);
				document.updateContent();
				
				scriptLogger.debug("Traitement document : "+i);
			}
		}catch(Exception e){
			scriptLogger.error("Erreur lors du traitement : ",e);
		}finally{
			if(resultSet != null) resultSet.close();
			if(preparedStatement != null) preparedStatement.close();
		}
	}
	//if(connection != null) connection.close();
/*}else scriptLogger.error("Connection null");*/

private List<Integer> getDocument(UserCoreContext user) {
	List<Integer> documentResult = new ArrayList<Integer>();
	//String requete = "select refd.doc_id from db2inst1.docref_document_affilie refd inner join db2inst1.doc_document_affilie d on refd.doc_id = d.doc_id where refd.aui_id in (select aui_id from db2inst1.authority_item where aui_code like 'AFA%' OR aui_code like 'PAP%' OR aui_code like 'PCF%' OR aui_code like 'PPC%' OR aui_code like 'PRE%' OR aui_code like 'PRF%') AND d.N_NSS IS NULL AND d.d_creat > to_date('22-11-2015','DD-MM-YYYY')";
	String requete = "select refd.doc_id from db2inst1.docref_document_affilie refd inner join db2inst1.doc_document_affilie d on refd.doc_id = d.doc_id where refd.aui_id in (select aui_id from db2inst1.authority_item where aui_code like 'PIJ%') AND d.d_creat > to_date('22-11-2015','DD-MM-YYYY')";
	/*Connection connection2 = null;
	PreparedStatement preparedStatement2 = null;
	ResultSet resultSet2 = null;
	try {
		Class.forName(Constants.DB_AIRS_DRIVER);
		connection2 = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, Constants.DB_AIRS_PASSWORD);
		preparedStatement2 = connection2.prepareStatement(requete);
		resultSet2 = preparedStatement2.executeQuery();
    
		while(resultSet2.next())
		{
			documentResult.add(resultSet2.getInt(1));
		}

	} catch (Exception e) {
		scriptLogger.error("Erreur à la récuération du document", e);
	}finally{
		if(resultSet2 != null) resultSet2.close();
		if(preparedStatement2 != null) preparedStatement2.close();
		if(connection2 != null) connection2.close();
	}*/
	return documentResult;
}

/*ArrayList<Domain> listDomain = new ArrayList<Domain>();
listDomain.add(new Domain(user.getJeton(), "DOCUMENT_AFFILIE"));

Request req = new Request();
req.addLocution("N_AFF", Request.Operator.OPERATOR_SUP, "0");
req.addOperatorLocution(Request.LocutionOperator.OPERATOR_AND);
req.addLocution("N_NSS", Request.UnaryOperator.OPERATOR_ISNULL);

Search search = new Search(user.getJeton(), req, listDomain);
documentResult = search.getResultIds();*/