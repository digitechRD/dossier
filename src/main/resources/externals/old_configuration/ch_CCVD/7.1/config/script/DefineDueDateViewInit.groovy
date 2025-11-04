import Constants
import Methods
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.CustomActionController
import com.digitech.dossier.common.model.backend.UserContext

/*************************************************************************************************
 * 								Définition de la date d'échéance - INIT
 **************************************************************************************************
 Date : 04.11.2014
 Auteur : MTO

 Description : Permet la définition de l'index date d'échéance d'un document
 **************************************************************************************************/

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE DUE DATE VIEW INIT - START")

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
  _scriptLogger.error("[CUSTOM ACTION] - DefineDueDateViewInit - ERREUR : ", e)
  return
}

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_RENTES_1_ID ||
      UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_RENTES_2_ID ||
      UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_RENTES_3_ID ||
      UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_RENTES_4_ID ||
      UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_ACCORDS_BI_ID ||
      UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_ESTIMATIONS_ID ||
      UserContext.getInstance().getCurrentOrgId() == Constants.ORGANIZATION_APG_ID)
    calendar.add(Calendar.DATE, Constants.CCVD_DAY_NUMBER_DATE_DUE_1)
  else calendar.add(Calendar.DATE, Constants.CCVD_DAY_NUMBER_DATE_DUE_2)
  data.put("date", calendar.getTime())
  data.put("comment", "")
  _scriptLogger.debug("Date par défaut :" + calendar.getTime())
} catch(Exception e) {
  Methods.addStateMessage(data, "DATA_ERROR_MSG", "Erreur lors de l'initialisation du traitement. Veuillez contacter votre administrateur", false)
  _scriptLogger.error("[CUSTOM ACTION] - DefineDueDateViewInit - ERREUR : ", e)
  return
}

_scriptLogger.debug("[CUSTOM ACTION] - DEFINE DUE DATE VIEW INIT - END")
