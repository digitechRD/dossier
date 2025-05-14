/**
 * Script de type {DocumentInitializer}
 * Variables disponible: _scriptLogger, _resultn _userContext, _document
 */

_scriptLogger.debug(">>> sample_action (doc:'{}', ucc: '{}')", _document, _userContext)

try {
	// ajouter votre code ici...

  _result.fillOKResult("We did it!")

} catch (Exception e) {

  _scriptLogger.error("Error while ...: '{}'", e.getLocalizedMessage(), e)
  _result.fillKOResult("blabla error", e)
}

_scriptLogger.debug("<<< sample_action")
