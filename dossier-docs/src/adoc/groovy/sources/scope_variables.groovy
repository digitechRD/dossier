
logger = Logger.getLogger("scopeVariables")
y = 2

class GlobalProps {
  static def POOL_NAME = "dbName"
}

def localFunction() {
  def q = 333
  logger.info("y value: '{}'", y) // ok, print '2'
  logger.info("q value: '{}'", q) // ok, print '333'

  z = 666
}

static def staticFunction() {
  logger.info("dbName value: '{}'", GlobalProps.POOL_NAME) // // ok, print 'dbName'
  logger.info("y value: '{}'", y) // this will fail!
}

localFunction()
staticFunction()

logger.info("- Local variable doesn't exist outside")
logger.info("y value: '{}'", y) // ok, print '2'
logger.info("7 value: '{}'", z) // ok, print '666'
logger.info("q value: '{}'", q) // this will fail!