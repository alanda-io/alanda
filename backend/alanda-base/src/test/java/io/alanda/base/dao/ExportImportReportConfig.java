/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author developer
 */
public class ExportImportReportConfig {
  
  private static final Logger log = LoggerFactory.getLogger(ExportImportReportConfig.class);

  private static String _testDir = "/home/developer/";

  private static String _dbFile = "dbDump.xml";

  private static String _driverClass = "oracle.jdbc.driver.OracleDriver";

  private static final DbConfig LOCAL = new DbConfig("jdbc:oracle:thin:@127.0.0.1:1521:xe", "CAMUNDA", "cam123");

  private static final DbConfig TEST = new DbConfig("jdbc:oracle:thin:@127.0.0.1:1521:xe", "CAMUNDA", "cam123");

  private static final DbConfig PROD = new DbConfig("jdbc:oracle:thin:@127.0.0.1:1521:xe", "CAMUNDA", "cam123");

  private static final DbConfig PGLOCAL = new DbConfig("jdbc:postgresql://localhost:5432/bpmc2", "bpmc2", "bpmc2", "public");
  
  public static class DbConfig {

    final String url;

    final String user;

    final String pass;
    
    final String schema;

    public DbConfig(String url, String user, String pass) {
      this.url = url;
      this.user = user;
      this.pass = pass;
      this.schema = user;
    }
    
    public DbConfig(String url, String user, String pass, String schema) {
      super();
      this.url = url;
      this.user = user;
      this.pass = pass;
      this.schema = schema;
    }

  }

  public static void main(String[] args) throws ClassNotFoundException, DatabaseUnitException, IOException, SQLException {

    String[] tablenames = {"PMC_REPORTCONFIG"};

    tableExport(PGLOCAL, new File(_testDir, "report_test.xml"), tablenames);
  }

  public static void tableExport(DbConfig config, File file, String... tableNames)
      throws ClassNotFoundException,
      DatabaseUnitException,
      DataSetException,
      IOException,
      SQLException {
    IDatabaseConnection connection = getConnection(config);

    ITableFilter filter = new IncludeTableFilter(tableNames);
    IDataSet dataset = new FilteredDataSet(filter, connection.createDataSet());
    FlatXmlDataSet.write(dataset, new FileOutputStream(file));
  }

  public static void fullDatabaseImport(DbConfig config, File file)
      throws ClassNotFoundException,
      DatabaseUnitException,
      IOException,
      SQLException {
    IDatabaseConnection connection = getConnection(config);
    FlatXmlProducer p = new FlatXmlProducer(new InputSource(new FileInputStream(file)), true);
    IDataSet dataSet = new FlatXmlDataSet(p);

    DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
  }

  public static IDatabaseConnection getConnection(DbConfig config) throws ClassNotFoundException, DatabaseUnitException, SQLException {
    // database connection
//    Class driverClass = Class.forName(_driverClass);
    Connection jdbcConnection = DriverManager.getConnection(config.url, config.user, config.pass);
    IDatabaseConnection db = new DatabaseConnection(jdbcConnection, config.schema, true);
    db.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, resolveDataTypeFactory(jdbcConnection));

    return db;
  }

  private static IDataTypeFactory resolveDataTypeFactory(Connection connection) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    String databaseName = metaData.getDatabaseProductName();

    //The structure for this is drawn from Hibernate's StandardDialectResolver. I figure if these checks are robust
    //enough for Hibernate, they should be more than robust enough for our unit tests.
    IDataTypeFactory factory;
    if ("HSQL Database Engine".equals(databaseName)) {
      log.debug("Using HSQL DataTypeFactory");
      factory = new HsqldbDataTypeFactory();
    } else if ("H2".equals(databaseName)) {
      log.debug("Using H2 DataTypeFactory");
      factory = new H2DataTypeFactory();
    } else if ("MySQL".equals(databaseName)) {
      log.debug("Using MySQL DataTypeFactory");
      factory = new MySqlDataTypeFactory();
    } else if ("PostgreSQL".equals(databaseName)) {
      log.debug("Using Postgres DataTypeFactory");
      factory = new PostgresqlDataTypeFactory();
    } else if (databaseName.startsWith("Microsoft SQL Server")) {
      log.debug("Using SQL Server DataTypeFactory");
      factory = new MsSqlDataTypeFactory();
    } else if ("Oracle".equals(databaseName)) {
      if (metaData.getDatabaseMajorVersion() < 10) {
        log.debug("Using Oracle DataTypeFactory for 10g and later");
        factory = new OracleDataTypeFactory();
      } else {
        log.debug("Using Oracle DataTypeFactory for 9i and earlier");
        factory = new Oracle10DataTypeFactory();
      }
    } else {
      log.warn(
        "No IDataTypeFactory was resolved for {}. Using default DataTypeFactory. This may result in " + "test failures..",
        databaseName);
      return new DefaultDataTypeFactory();
    }

    return factory;
  }
  
}
