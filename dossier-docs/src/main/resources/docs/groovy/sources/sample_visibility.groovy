/**
 * Script de type {DisplayRule}
 * Variables en entrée: _scriptLogger, _result, _userContext, _document
 */

_scriptLogger.debug(">>> sample_visibility (result: '{}', doc:'{}')", _result, _document)

_result.setValid(_document?.getAttachments(_userContext)?.size > 0)

_scriptLogger.debug("<<< sample_visibility")
