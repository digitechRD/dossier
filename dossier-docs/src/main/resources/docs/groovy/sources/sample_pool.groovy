import com.digitech.dossier.common.utils.sql.SqlUtils


def POOL_NAME = "dbPool"

// création ou récupération
def dbPool1 = SqlUtils.getOrCreatePool(POOL_NAME, "jdbc:oracle:thin:@//XXX", "<user>", "<pwd>", "oracle.jdbc.OracleDriver")

// récupération
def dbPool2 = SqlUtils.getPool(POOL_NAME)

// Libération
SqlUtils.releasePool(POOL_NAME)

// récupération d'une connexion à la base de données
try(def conn = SqlUtils.getPoolConnection(POOL_NAME)) {
  // vous pouvez alors travailler avec cette connexion à la base de données
}