import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.UserContext

/*************************************************************************************************
 * 								Définition de la date d'archive - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la définition de l'index date d'archivage d'un document
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE ARCHIVE DATE VIEW INIT - START")

/**
 * INITIALISATION
 **************************************************************************************************/
Calendar calendar = null
CustomActionController customActionController = null
Map<String, Object> data = null

try {
  customActionController = Utils.getCustomActionController()
  data = customActionController.getModel().getModalPanelModel()
  calendar = Calendar.getInstance()
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - DefineArchiveDateViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_APG_ID)
    calendar.add(Calendar.DATE, Constants.CCVD_DAY_NUMBER_DATE_DUE_1)
  else calendar.add(Calendar.DATE, Constants.CCVD_DAY_NUMBER_DATE_DUE_2)
  data.put("date", calendar.getTime())
  _scriptLogger.debug("[CUSTOM ACTION] - DefineArchiveDateViewInit - DEBUG - Date par défaut :" + calendar.getTime())
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - DefineArchiveDateViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE ARCHIVE DATE VIEW INIT - END")