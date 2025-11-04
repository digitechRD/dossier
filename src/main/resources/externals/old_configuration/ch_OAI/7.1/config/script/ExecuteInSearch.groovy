import Constants
import Methods
import com.digitech.dossier.common.model.backend.UserContext
import com.digitech.dossier.script.model.impl.result.ScriptResultValueChecker

import java.sql.ResultSet
import java.sql.Statement

/*************************************************************************************************
 * 							Groovy executé lors d'une recherche
 **************************************************************************************************
 Date : 11.03.2016
 Auteur : MTO

 Description : Permet d'effectuer un filtre supplémentaire non visible par l'utilisateur lors d'une recherche
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN SEARCH EXEC - START")

/**
 * INITIALISATION
 **************************************************************************************************/
ScriptResultValueChecker result = new ScriptResultValueChecker()
String currentRequest = null
String finalRequest = null

/**
 * TRAITEMENT
 **************************************************************************************************/
try {

  java.sql.Connection conn = null
  Statement ps = null
  ResultSet rs = null

  result.setValid(true)
  currentRequest = search.getAirsRequest(UserContext.getInstance())
  if(currentRequest != null && !currentRequest.isEmpty()) {
    if(currentRequest.contains(Constants.FIELD_NSS_CODE + "=\"")) {
      String currentNss = currentRequest.substring(currentRequest.indexOf(Constants.FIELD_NSS_CODE + "=") + Constants.FIELD_NSS_CODE.length() + 2,
                                                   currentRequest.indexOf("\"", currentRequest.indexOf(Constants.FIELD_NSS_CODE + "=") +
                                                       Constants.FIELD_NSS_CODE.length() + 2))
      String finalNss = Methods.completeNssForSearch(currentNss)
      finalRequest = currentRequest.replace(Constants.FIELD_NSS_CODE + "=\"" + currentNss, Constants.FIELD_NSS_CODE + "=\"" + finalNss)
      search.setAirsRequest(finalRequest)
    }

    if(currentRequest.contains(Constants.FIELD_GESTIONNAIRES_CODE) && !currentRequest.contains(Constants.FIELD_GESTIONNAIRES_HISTORIQUE_CODE)) {
      if(finalRequest != null)
        currentRequest = finalRequest
      if(Constants.LIST_WK_STATUT_CODE.startsWith("AL_"))
        finalRequest = currentRequest + " ET " + Constants.LIST_WK_STATUT_CODE + ".ROOTITEM=\"" + Constants.LIST_WK_STATUT_TRANSFERT_PEC + "\""
      else
        finalRequest = currentRequest + " ET " + Constants.LIST_WK_STATUT_CODE + "=\"" + Constants.LIST_WK_STATUT_TRANSFERT_PEC + "\""
      search.setAirsRequest(finalRequest)
    }
    scriptLogger.debug("[CUSTOM ACTION] - ExecuteInSearch - DEBUG - requete :  " + finalRequest)
  }
  //Methods.logActionUser(Constants.ACTION_RECHERCHE,userContext.getUser().getLogin(),finalRequest);

  output.setValue(result)

} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - ExecuteInSearch - ERROR :  ", e)
  return
}

scriptLogger.debug("[CUSTOM ACTION] - EXECUTION IN SEARCH EXEC - END")