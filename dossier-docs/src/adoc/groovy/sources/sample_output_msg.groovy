import com.digitech.dossier.common.resources.BundleUtils

try {
  // opération ici
  // ...
  _result.fillOKResult("User '$_userContext.userFullName' successfully did the job!")

} catch (Exception e) {
  _scriptLogger.error("Error while blabla...: '{}'", e.getLocalizedMessage(), e)
  _result.fillKOResult("Error while blabla...", e)
}


// Si le message doit être récupérés à partir des ressources, pour une gestion multi-langues, le code suivant doit être utilisé

_result.fillOKResult(BundleUtils.getTranslation("msg_key_OK", new Objevt[]{ _userContext.userFullName }))
