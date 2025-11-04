import javax.faces.model.SelectItem;

import java.sql.Connection;
import java.sql.DriverManager
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.digitech.dossier.common.Utils;
import com.digitech.dossier.common.controller.CustomActionController;

import Constants;
import Methods;

/*************************************************************************************************
 *								Définition du taxateur - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Définit ou redéfinit l’utilisateur étant le taxateur du document.
               La liste des utilisateurs comprend seulement les utilisateurs ayant accès à l’organisation courante
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - DEFINE TAXING USER SIMPLE VIEW INIT - START");

/**
 * INITIALISATION
 **************************************************************************************************/

CustomActionController customActionController = null;
Map<String, Object> data = null;
Connection connection = null;
PreparedStatement preparedStatement = null;
ResultSet resultSet = null;
List<SelectItem> items = new ArrayList<SelectItem>();

try {
    customActionController = Utils.getCustomActionController();
    data = customActionController.getModel().getModalPanelModel();
}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement", false);
    scriptLogger.error("[CUSTOM ACTION] - DefineUserSimpleViewInit - ERREUR : ",e);
    return;
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try{
    Class.forName(Constants.DB_AIRS_DRIVER);
    connection = DriverManager.getConnection(Constants.DB_AIRS_URL, Constants.DB_AIRS_USERNAME, Constants.DB_AIRS_PASSWORD);
    preparedStatement = connection.prepareStatement(Constants.DB_AIRS_REQUEST_GET_USERS_BY_ORGANIZATION);
    preparedStatement.setInt(1, userContext.getCurrentOrgId());

    resultSet = preparedStatement.executeQuery();
    items.add(new SelectItem(0,"Choisir un utilisateur"));
    while(resultSet.next()){
        items.add(new SelectItem(resultSet.getInt(1), resultSet.getString(2)));
    }
    data.put("users", items);

}catch(Exception e){
    Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement", false);
    scriptLogger.error("[CUSTOM ACTION] - DefineUserSimpleViewInit - ERREUR : ",e);
    return;
}finally{
    if(resultSet != null) resultSet.close();
    if(preparedStatement != null) preparedStatement.close();
    if(connection != null) connection.close();
}

scriptLogger.debug("[CUSTOM ACTION] - DEFINE TAXING USER SIMPLE VIEW INIT - END");
 
