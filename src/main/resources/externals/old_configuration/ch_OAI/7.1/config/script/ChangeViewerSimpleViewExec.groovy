import Constants
import com.digitech.dossier.common.Utils
import com.digitech.dossier.common.controller.NavigationController

/*************************************************************************************************
 *   					    			ChangeViewerSimpleView - EXEC
 **************************************************************************************************
 Date : 18.03.2016
 Auteur : MTO

 Description : Permet de switcher du viewer Digitech vers Adobe et inversement
 **************************************************************************************************/

scriptLogger.debug("[CUSTOM ACTION] - CHANGE VIEWER SIMPLE VIEW EXEC - START")

/**
 * TRAITEMENT
 **************************************************************************************************/
try {
  if(Constants.APPLICATION_VIEWER_DIGITECH_VALUE.equals(userContext.getUserSpace().getPreferenceFromCode(Constants.APPLICATION_VIEWER_CODE).getValue())) {
    userContext.getUserSpace().setPreferenceValue(Constants.APPLICATION_VIEWER_CODE, Constants.APPLICATION_VIEWER_ADOBE_VALUE, true)
  }
  else
    userContext.getUserSpace().setPreferenceValue(Constants.APPLICATION_VIEWER_CODE, Constants.APPLICATION_VIEWER_DIGITECH_VALUE, true)

  Utils.getSearchResultController().replay()
  Utils.getSimpleViewAttachmentController().getModel().refreshDocument()
  Utils.getCustomActionController().getModel().setOutcome(NavigationController.OUTCOME_SEARCH_RESULT_SIMPLE)

} catch(Exception e) {
  scriptLogger.error("[CUSTOM ACTION] - ChangeViewerSimpleViewExec - ERROR :  ", e)
}

scriptLogger.debug("[CUSTOM ACTION] - CHANGE VIEWER SIMPLE VIEW EXEC - END")