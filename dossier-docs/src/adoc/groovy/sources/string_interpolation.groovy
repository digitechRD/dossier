

def who = "n.felix"
_scriptLogger.info("hello ${who}!")
_scriptLogger.info("hello $who!")


def whoPerson = new PersonData(1, "n.felix")

_scriptLogger.info("hello $whoPerson.login! (id: $whoPerson.id")

class PersonData {
  Integer id
  String login
}