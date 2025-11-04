
/**
 * Name :     updateZipCodes
 * Desc :     script used to update/compute zip code into the database
 *
 * Target :   CityWeb France
 * Version :  1.0.0
 * Date :     20/03/12
 */

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;

import com.digitech.common.dal.dto.CustomSession
import com.digitech.common.dal.dto.ICode
import com.digitech.common.dal.dto.typedef.EnumValueType
import com.digitech.common.dal.sgbd.JdbcUtils
import com.digitech.common.dal.utils.ResourceLoaderUtils
import com.digitech.common.dal.utils.SessionUtils
import com.digitech.common.lib.utils.StringUtils;
import com.digitech.common.manager.ServiceManager
import com.digitech.common.tools.file.CSVParser
import com.digitech.common.tools.file.FlatFileReader
import com.digitech.common.tools.file.separator.CsvRecordSeparatorPolicy
import com.digitech.common.tools.utils.DStringUtils
import com.digitech.dossier.common.utils.ApplicationUtils;
import com.digitech.population.dto.CityParameterType
import com.digitech.population.service.ICityParameterTypeService
import com.digitech.population.service.ServiceConstants

getLog().info(">>> updateZipCodes");

final String APP_PROP_KEY = "CODE";
final String APP_PROP_COMPARE_VALUE = "1.0.2";

FlatFileReader csvFile = null;
Connection connection = null;
try {
  long dwStart = System.currentTimeMillis();
  final String DEFAULT_FR_ZIP_CODES = ApplicationUtils.getConfigFolderRelativePath() + "/templates/courrier/csv/CP-INSEE_updated.csv";
  final String POSTAL_CODE_VAR = "CP";

  csvFile = new FlatFileReader(ResourceLoaderUtils.getResource(DEFAULT_FR_ZIP_CODES).getInputStream(), DEFAULT_FR_ZIP_CODES);
  csvFile.setLinesToSkip(0);
  csvFile.setRecordSeparatorPolicy(new CsvRecordSeparatorPolicy());

  CSVParser parser = new CSVParser();

  Map<String, String> insee2ZipCodeMap = new HashMap<String, String>();

  String values = csvFile.readLine(true, false);
  String[] valArray = parser.parseLine(values);
  int posInseeCode = 1;
  int posZipCode = 0;

  int rowTreated = 0;

  // pop connection
  SessionFactory sf = SessionUtils.getSessionFactory();
  ConnectionProvider cp = ((SessionFactoryImplementor) sf).getConnectionProvider();
  connection = cp.getConnection();

  if(checkAppPropVersion(APP_PROP_KEY, APP_PROP_COMPARE_VALUE)){

    while((values = csvFile.readLine(true, false)) != null) {
      if(DStringUtils.isBlank(values)) {
        continue;
      }

      if(rowTreated == 0) {
        // log start info !!
        getLog().info("computing '{}' file", DEFAULT_FR_ZIP_CODES);
      }

      // extract values
      valArray = parser.parseLine(values);
      insee2ZipCodeMap.put(valArray[posInseeCode], valArray[posZipCode]);

      ++rowTreated;
    }

    if(!insee2ZipCodeMap.isEmpty()) {
      getLog().info("'{}' zip codes read", insee2ZipCodeMap.size());

      ICityParameterTypeService cityParameterTypeSrv = (ICityParameterTypeService) ServiceManager.getInstance().getService(ServiceConstants.SERVICE_CITY_PARAMETER_TYPE);
      CityParameterType cpCityType = cityParameterTypeSrv.getFromCode(POSTAL_CODE_VAR);
      if(cpCityType == null) {
        cpCityType = new CityParameterType(POSTAL_CODE_VAR, POSTAL_CODE_VAR, EnumValueType.STRING);
        cityParameterTypeSrv.create(cpCityType);
      }

      boolean batchUpdateEnabled = connection.getMetaData().supportsBatchUpdates();
      boolean autoCommitMode = connection.getAutoCommit();
      connection.setAutoCommit(!batchUpdateEnabled);

      CustomSession session = null;

      Statement stmt = null;
      PreparedStatement batchInsert = null;
      PreparedStatement batchDelete = null;
      ResultSet rs = null;
      try {

        String sqlQuery = "SELECT CTY_ID, CODE, GEZ_ID FROM CITY";
        getLog().debug("executing '{}' query", sqlQuery);
        stmt = JdbcUtils.createStatement(sqlQuery, connection, batchUpdateEnabled ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_SENSITIVE,
            batchUpdateEnabled ? ResultSet.CONCUR_READ_ONLY : ResultSet.CONCUR_UPDATABLE);

        stmt.setQueryTimeout(300);
        try {
          // use cursor-type for resulset to avoid out-of-memory exception
          stmt.setFetchSize(Integer.MIN_VALUE);
        }
        catch(SQLException sqle) {
          getLog().warn("fetchSize not allowed ?!? (msg: '{}')", sqle.getLocalizedMessage());
        }
        rs = ((PreparedStatement) stmt).executeQuery();
        getLog().debug("query done");

        if(batchUpdateEnabled) {
          // Note : it's mandatory to get a new connection to perform batch update, when resultSet is on cursor-mode
          session = SessionUtils.getHibernateSession();
          Connection batchCon = session.getNativeSession().connection();

          batchDelete = batchCon.prepareStatement("DELETE FROM PARAM_CITY WHERE CTY_ID=? AND TPC_ID=?");
          batchInsert = batchCon.prepareStatement("INSERT INTO PARAM_CITY (CTY_ID, TPC_ID, SVALUE) VALUES(?,?,?)");

          BatchData bd = new BatchData();
          String originalInseeCode, newInseeCode;

          int total = 0;
          while(rs.next()) {
            ++bd.currentStepNumber;

            batchDelete.setLong(1, rs.getLong(1));
            batchDelete.setLong(2, cpCityType.getId());
            batchDelete.addBatch();

            String deptNumber = computeDeptCode(rs.getString(3));

            originalInseeCode = deptNumber + DStringUtils.leftPad(rs.getString(2), 3, (char) '0');
            newInseeCode = insee2ZipCodeMap.get(originalInseeCode);
            if(DStringUtils.isNotBlank(newInseeCode)) {
              batchInsert.setLong(1, rs.getLong(1));
              batchInsert.setLong(2, cpCityType.getId());
              batchInsert.setString(3, newInseeCode);
              batchInsert.addBatch();
              ++total;
            }
            else {
              getLog().debug("WARN : zip code for insee '{}' not found", originalInseeCode);
            }
            commitBatch(batchDelete, batchInsert, bd, false, batchCon);
          }
          commitBatch(batchDelete, batchInsert, bd, true, batchCon);
        }
      }
      catch(SQLException sqle) {
        try {
          connection.rollback();
        }
        catch(SQLException sqle2) {
          getLog().error("error wile rollbacking query");
          // we don't throw this exception to keep first cause
        }
        throw sqle;
      }
      finally {

        JdbcUtils.closeStatementQuietly(stmt);
        JdbcUtils.closeStatementQuietly(batchInsert);
        JdbcUtils.closeStatementQuietly(batchDelete);
        if(rs != null) {
          rs.close();
        }
        SessionUtils.closeSession(session);

        connection.setAutoCommit(autoCommitMode);
        log.debug("<<< execute");
      }
    }

    insee2ZipCodeMap.clear();
    updateDbCode(APP_PROP_KEY, APP_PROP_COMPARE_VALUE);
  }
}
catch (Exception e){
  getLog().error(e.getLocalizedMessage(),e);
}
finally {
  if(!connection.isClosed()){
    connection.close();
  }
  if(csvFile != null) {
    try {
      csvFile.close();
    }
    catch(IOException ioe) {
      // silent
    }
  }
  getLog().info("<<< updateZipCodes");
}

private Logger getLog(){
  return scriptLogger;
}

private boolean checkAppPropVersion(String key,String compareValue){
  boolean returnValue = false;
  //create prepare statement

  Connection cnx = SessionUtils.getHibernateSession().getNativeSession().connection();

  PreparedStatement ps = null;
  PreparedStatement insertPs = null;

  try{
    ps = cnx.prepareStatement( "SELECT CODE, SVALUE FROM APP_PROPERTIES WHERE CODE = ?");
    //set CODE
    ps.setString(1, key);

    ResultSet rs = ps.executeQuery();
    if (rs.next()){
      String dbValue = rs.getString(2);
      if(dbValue < compareValue){
        return true;
      }
    }
    else {
      insertPs = cnx.prepareStatement("INSERT INTO APP_PROPERTIES (PROP_ID, CODE, LABEL, TYPE_VALUE, SVALUE) VALUES (APP_PROP_ID_SEQ.nextval,?,?,1,?)");
      insertPs.setString(1, key);
      insertPs.setString(2, "Numero de version de CSV ZIPCODE");
      insertPs.setString(3, "0.0.0");
      insertPs.execute();
      cnx.commit();
      insertPs.close();
      return true;
    }
    ps.close();
    return returnValue;
  }
  catch(Exception e){
    throw e;
  }
  finally{
    if(insertPs != null) {
      insertPs.close()
    }
    if(ps != null) {
      ps.close();
    }
  }
}

private void updateDbCode(String key, String updateValue){
  //create prepare statement
  Connection cnx = SessionUtils.getHibernateSession().getNativeSession().connection();
  PreparedStatement psUpdate = null;

  try{

    psUpdate = cnx.prepareStatement("UPDATE APP_PROPERTIES SET SVALUE=? WHERE CODE=?");
    // set values
    psUpdate.setString(1, updateValue);
    psUpdate.setString(2, key);
    // execute query
    psUpdate.execute();
    cnx.commit();
  }
  catch(Exception e){
    throw e;
  }
  finally{
    if(psUpdate != null){
      psUpdate.close();
    }
  }
}

private String computeDeptCode(String dbCode){

  // Corse du Sud
  if(dbCode.equals("201")){
    return "2A";
  }
  // Corse du Nord
  else if(dbCode.equals("202")){
    return "2B";
  }
  // DOM
  else if(dbCode != null && dbCode.size()>2){
    return dbCode.substring(0, 2);
  }

  return DStringUtils.leftPad(dbCode, 2, (char) '0');
}

private int commitBatch(Statement batchDelete, Statement batchInsert, BatchData batchData, boolean force, Connection connection)
throws SQLException {

  if(batchData.currentStepNumber > 0 && (force || (batchData.currentStepNumber % batchData.commitSize) == 0)) {
    getLog().debug("applying batch updates");
    batchDelete.executeBatch();
    batchDelete.clearBatch();
    connection.commit();

    batchInsert.executeBatch();
    batchInsert.clearBatch();
    connection.commit();

    if(getLog().isDebugEnabled() && batchData.currentStepNumber > 0) {
      getLog().debug("commit done");
      batchData.startNanoTime = System.nanoTime();
    }
    else {
      getLog().debug("commit done");
    }
    batchData.currentStepNumber = 0;
  }
  return batchData.currentStepNumber;
}

public class BatchData {
  /** current step number */
  public int        currentStepNumber;
  /** commit size */
  public int        commitSize;
  /** start time **/
  public long       startNanoTime;

  private final int STEP_BATCH_UPDATES = 20000;

  /**
   * constructor
   */
  public BatchData() {
    commitSize = STEP_BATCH_UPDATES;
    currentStepNumber = 0;
    startNanoTime = System.nanoTime();
  }
}