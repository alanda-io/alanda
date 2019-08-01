package io.alanda.development.dbtools;

import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.assertion.DbComparisonFailure;
import org.dbunit.assertion.DiffCollectingFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IRowValueProvider;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.RowFilterTable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;
import org.dbunit.dataset.filter.IRowFilter;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.dbunit.dataset.xml.XmlDataSet;
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

public class DbUnit {

  private static final Logger log = LoggerFactory.getLogger(DbUnit.class);

  public static void tableExport(DbConfig config, DbTable[] tables, String dataPath) {
    if (dataPath == null) {
      throw new IllegalArgumentException("no dataPath provided");
    }
    try {
      IDatabaseConnection connection = getConnection(config, tables);
      for (DbTable t : tables) {
        log.info("Exporting " + t.tableName + " to file " + t.fileName + " (" + t.selectSql + ")");
        QueryDataSet qds = new QueryDataSet(connection);
        qds.addTable(t.tableName, t.selectSql);
        XmlDataSet.write(qds, new FileOutputStream(dataPath + "/" + t.fileName));
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void tableImport(DbConfig config, DbTable[] tables, String dataPath, boolean relativeResourcePath) {
    if (relativeResourcePath) {
      dataPath = AlandaDatabaseMigration.getResourceUri(dataPath).getPath();
    }
    try {
      IDatabaseConnection connection = getConnection(config, tables);
      for (DbTable t : tables) {
        log.info("Importing " + t.tableName + " from file " + t.fileName);
        String xmlData = loadXml(t, dataPath);
        IDataSet dataSet = new XmlDataSet(new StringReader(xmlData));
        ITableFilter filter = new IncludeTableFilter(new String[] {t.tableName});
        DatabaseOperation.REFRESH.execute(connection, new FilteredDataSet(filter, dataSet));
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String tableCompare(
      DbConfig config, DbTable[] tables, String dataPath, String logPath, String prefix, boolean relativeResourcePath) {
    if (relativeResourcePath) {
      dataPath = AlandaDatabaseMigration.getResourceUri(dataPath).getPath();
    }
    try {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
      String actualDateTime = LocalDateTime.now().format(dtf);

      File xmlFile =
          new File(logPath + "/" + prefix + "_dbunit-diff_" + actualDateTime + "_" + config.label + "_xml.txt");
      BufferedWriter xmlWriter = new BufferedWriter(new FileWriter(xmlFile));
      File dbFile =
          new File(logPath + "/" + prefix + "_dbunit-diff_" + actualDateTime + "_" + config.label + "_db.txt");
      BufferedWriter dbWriter = new BufferedWriter(new FileWriter(dbFile));
      IDatabaseConnection connection = getConnection(config, tables);
      StringBuffer sb = new StringBuffer();
      for (DbTable t : tables) {
        String xmlData = loadXml(t, dataPath);
        IDataSet dataSet = new XmlDataSet(new StringReader(xmlData));
        SortedTable xmlTable = new SortedTable(dataSet.getTable(t.tableName));
        SortedTable dbTable =
            new SortedTable(
                connection.createDataSet().getTable(t.tableName), xmlTable.getTableMetaData());

        Set<String> displayCols = new HashSet<>();
        for (Column c : Sets.newHashSet(dbTable.getTableMetaData().getPrimaryKeys())) {
          displayCols.add(c.getColumnName());
        }
        if (t.displayColumns != null) {
          displayCols.addAll(t.displayColumns);
        }

        SortedSet<String> pkNames = new TreeSet<>();
        for (Column c : dbTable.getTableMetaData().getPrimaryKeys()) {
          pkNames.add(c.getColumnName());
        }
        Map<String, Integer> xmlPkValues = getPkValuesFromTable(xmlTable, pkNames);
        Map<String, Integer> dbPkValues = getPkValuesFromTable(dbTable, pkNames);

        Set<String> onlyXml = Sets.newHashSet(xmlPkValues.keySet());
        onlyXml.removeAll(dbPkValues.keySet());
        for (String s : onlyXml) {
          String meta =
              t.tableName
                  + "["
                  + getDisplayColValues(xmlTable, displayCols, xmlPkValues.get(s))
                  + "]";
          String row = getRowString(xmlTable, xmlPkValues.get(s));
          sb.append("A " + meta);
          xmlWriter.write("A " + meta + "\n" + row);
        }
        Set<String> onlyDb = Sets.newHashSet(dbPkValues.keySet());
        onlyDb.removeAll(xmlPkValues.keySet());
        for (String s : onlyDb) {
          String meta =
              t.tableName
                  + "["
                  + getDisplayColValues(dbTable, displayCols, dbPkValues.get(s))
                  + "]";
          String row = getRowString(dbTable, dbPkValues.get(s));
          sb.append("R " + meta);
          dbWriter.write("R " + meta + "\n" + row);
        }

        RowFilterTable xmlTableFiltered = createRowFilterTable(xmlTable, onlyXml, pkNames);
        RowFilterTable dbTableFiltered = createRowFilterTable(dbTable, onlyDb, pkNames);

        DiffCollectingFailureHandler dcfh = new DiffCollectingFailureHandler();
        try {
          Assertion.assertEquals(xmlTableFiltered, dbTableFiltered, dcfh);
        } catch (DbComparisonFailure e) {
          System.err.println(e);
        }
        for (Object o : dcfh.getDiffList()) {
          Difference d = (Difference) o;
          String colValue = getDisplayColValues(xmlTable, displayCols, d.getRowIndex());
          String meta = "M " + t.tableName + "[" + colValue + "]." + d.getColumnName();
          sb.append(meta);
          xmlWriter.write(meta + ":\n" + d.getExpectedValue() + "\n\n");
          dbWriter.write(meta + ":\n" + d.getActualValue() + "\n\n");
        }
      }
      xmlWriter.flush();
      xmlWriter.close();
      dbWriter.flush();
      dbWriter.close();
      return sb.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static String getDisplayColValues(SortedTable table, Set<String> displayCols, int index)
      throws DataSetException {
    String colValue = "";
    for (String dc : displayCols) {
      colValue += dc + ": " + table.getValue(index, dc) + ", ";
    }
    if (colValue.length() > 0) {
      colValue = colValue.substring(0, colValue.length() - 2);
    }
    return colValue;
  }

  private static String getRowString(SortedTable table, int index) throws DataSetException {
    String result = "";
    for (Column c : Sets.newHashSet(Sets.newHashSet(table.getTableMetaData().getColumns()))) {
      result += c.getColumnName() + ":\n" + table.getValue(index, c.getColumnName()) + "\n\n";
    }
    return result;
  }

  private static String loadXml(DbTable t, String dataPath) throws IOException {
    final String pattern = "<value>\\$\\$(.*)\\$\\$</value>";
    String xmlData =
        new String(Files.readAllBytes(Paths.get(dataPath + "/" + t.fileName)), StandardCharsets.UTF_8);
    Matcher m = Pattern.compile(pattern).matcher(xmlData);
    while (m.find()) {
      String replaceFileName = m.group(1);
      String replaceData =
          new String(
              Files.readAllBytes(Paths.get(dataPath + "/" + t.tableName + "/" + replaceFileName)),
              StandardCharsets.UTF_8);
      xmlData =
          xmlData.replaceAll(
              pattern.replace("(.*)", replaceFileName),
              "<value><![CDATA[" + replaceData + "]]></value>");
    }
    return xmlData;
  }

  private static IDatabaseConnection getConnection(DbConfig config, DbTable[] dbTables)
      throws DatabaseUnitException, SQLException {
    // database connection
    Connection jdbcConnection = DriverManager.getConnection(config.url, config.user, config.pass);
    IDatabaseConnection db = new DatabaseConnection(jdbcConnection, config.schema, true);
    db.getConfig()
        .setProperty(
            DatabaseConfig.PROPERTY_DATATYPE_FACTORY, resolveDataTypeFactory(jdbcConnection));
    db.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
    db.getConfig()
        .setProperty(
            DatabaseConfig.PROPERTY_PRIMARY_KEY_FILTER,
            new IColumnFilter() {

              Map<String, Set<String>> tablePrimaryKeyMap = new HashMap<>();

              {
                for (DbTable t : dbTables) {
                  if (t.primaryKeys != null) tablePrimaryKeyMap.put(t.tableName, t.primaryKeys);
                }
              }

              @Override
              public boolean accept(String tableName, Column column) {
                if (tablePrimaryKeyMap.containsKey(tableName.toUpperCase())) {
                  return tablePrimaryKeyMap
                      .get(tableName.toUpperCase())
                      .contains(column.getColumnName().toUpperCase());
                } else {
                  return column.getColumnName().toUpperCase().equals("GUID");
                }
              }
            });
    return db;
  }

  private static IDataTypeFactory resolveDataTypeFactory(Connection connection)
      throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    String databaseName = metaData.getDatabaseProductName();

    // The structure for this is drawn from Hibernate's StandardDialectResolver. I figure if these
    // checks are robust
    // enough for Hibernate, they should be more than robust enough for our unit tests.
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
          "No IDataTypeFactory was resolved for {}. Using default DataTypeFactory. This may result in "
              + "test failures..",
          databaseName);
      return new DefaultDataTypeFactory();
    }
    return factory;
  }

  private static Map<String, Integer> getPkValuesFromTable(SortedTable t, SortedSet<String> pkNames)
      throws DataSetException {
    HashMap<String, Integer> result = new HashMap<>();
    for (int i = 0; i < t.getRowCount(); i++) {
      String pkValue = "";
      for (String s : pkNames) {
        pkValue += t.getValue(i, s) + "$";
      }
      result.put(pkValue, i);
    }
    return result;
  }

  private static RowFilterTable createRowFilterTable(
      ITable t, Set<String> valuesToFilter, SortedSet<String> pkNames) throws DataSetException {
    IRowFilter f =
        new IRowFilter() {
          @Override
          public boolean accept(IRowValueProvider rowValueProvider) {
            String pkValue = "";
            for (String s : pkNames) {
              try {
                pkValue += rowValueProvider.getColumnValue(s).toString() + "$";
              } catch (DataSetException e) {
              }
            }
            return !valuesToFilter.contains(pkValue);
          }
        };
    RowFilterTable result = new RowFilterTable(t, f);
    return result;
  }
}
