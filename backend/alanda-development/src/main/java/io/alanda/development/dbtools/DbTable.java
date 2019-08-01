package io.alanda.development.dbtools;

import java.util.Set;

public class DbTable {

  public final String tableName;
  public final Set<String> primaryKeys;
  public final Set<String> displayColumns;
  public final String selectSql;
  public final String fileName;

  public DbTable(
      String tableName, Set<String> primaryKeys, Set<String> displayColumns, String selectSql) {
    super();
    this.tableName = tableName;
    this.primaryKeys = primaryKeys;
    this.displayColumns =
        displayColumns; // columns which help humans to identify rows in addition to primary key(s)
    this.selectSql = selectSql;
    this.fileName = tableName + ".xml";
  }
}
