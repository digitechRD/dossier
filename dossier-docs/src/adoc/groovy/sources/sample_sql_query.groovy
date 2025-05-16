import com.digitech.dossier.common.utils.sql.SqlUtils

def POOL_NAME = "dbPool"
List<Map<String, Object>> users = SqlUtils.runQuery(POOL_NAME, "select * from users")


// Il est possible de mapper les résultats directement dans un POJO
// soit l'attribut et le nom dans le pojo doivent correspondre
// soit l'alias et le nom dans le pojo doivent correspondre
List<PersonData> toto = SqlUtils.runQuery(POOL_NAME, "SELECT u.USR_ID, u.USR_LOGIN as \"login\" FROM users u", PersonData.class)

@groovy.transform.ToString
class PersonData {
  Integer usr_id
  String login
}