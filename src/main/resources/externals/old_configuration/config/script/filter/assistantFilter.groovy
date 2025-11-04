import java.lang.*;
import org.apache.commons.lang.*;
import java.util.List;
import org.slf4j.Logger;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.jcorbairs.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.digitech.common.exceptions.DigiInternalException;
import com.digitech.common.framework.bdd.DBConnectionManager;

import static LaProvenceScriptUtils;

CONNECT_BDD_AIRS_DOSSIER_LOGIN=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_LOGIN");
CONNECT_BDD_AIRS_DOSSIER_MDP=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_MDP");
CONNECT_BDD_AIRS_DOSSIER_SERVEUR=LaProvenceScriptUtils.getConstant("CONNECT_BDD_AIRS_DOSSIER_SERVEUR");

POOL_NAME_DOSSIER=LaProvenceScriptUtils.getConstant("POOL_NAME_DOSSIER");

Iterator<User> iter = users.iterator();
List<Integer> assistantList = getAssistantIds();
while(iter.hasNext()) {
  User user = iter.next();
  if (!assistantList.contains(user.getId())) {
    iter.remove();
  }
}


List<Integer> getAssistantIds() {
  List<Integer> assistantList = null;
  Connection dbConnection = null;
  Statement dbStatement = null;
  ResultSet rsSet = null;

  DBConnectionManager connectManager = DBConnectionManager.getInstance();

  // On vérifie que le pool de connexion n'existe pas déja
  if(connectManager.getPool(POOL_NAME_DOSSIER) == null) {
    connectManager.release();
    connectManager.loadDriver("com.mysql.jdbc.Driver");
    connectManager.addPool(POOL_NAME_DOSSIER, CONNECT_BDD_AIRS_DOSSIER_SERVEUR, CONNECT_BDD_AIRS_DOSSIER_LOGIN, CONNECT_BDD_AIRS_DOSSIER_MDP, 5, "com.digitech.common.framework.bdd.mysql.SequenceMyImpl");
  }
  try {
    dbConnection = connectManager.getConnection(POOL_NAME_DOSSIER);
    dbStatement = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    String request = "SELECT ASSISTANT from CORRESP_ENG";
    rsSet = dbStatement.executeQuery(request);
    if(rsSet != null) {
      assistantList = new ArrayList<Integer>();
      while(rsSet.next()) {
        String assistant = rsSet.getString("ASSISTANT");
        if (StringUtils.isNotBlank(assistant)){
          assistantList.add(Integer.valueOf(assistant));
        }
      }
    }
    rsSet.close();
  }
  catch(DigiInternalException e) {
    // TODO Auto-generated catch block
  }
  catch(SQLException e) {
    // TODO Auto-generated catch block
  }
  finally{
    if (dbStatement != null){
      dbStatement.close();
    }
  }
  connectManager.freeConnection(POOL_NAME_DOSSIER, dbConnection);
  return assistantList;
}
