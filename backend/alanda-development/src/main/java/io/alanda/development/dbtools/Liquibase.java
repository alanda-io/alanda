package io.alanda.development.dbtools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class Liquibase {

  public static void update(
      DbConfig config, List<String> contexts, String liquibaseConfigFile) {
    contexts = addDefaultContext(config, contexts);
    try {
      liquibase.Liquibase liquibase = initLiquibase(config, liquibaseConfigFile);
      liquibase.update(new Contexts(contexts), new LabelExpression());
    } catch (SQLException | LiquibaseException e) {
      throw new RuntimeException(e);
    }
  }

  public static void dropAll(DbConfig config, String liquibaseConfigFile) {
    try {
      liquibase.Liquibase liquibase = initLiquibase(config, liquibaseConfigFile);
      liquibase.dropAll();
    } catch (SQLException | LiquibaseException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> getUnrunChangesets(
      DbConfig config, List<String> contexts, String liquibaseConfigFile) {
    contexts = addDefaultContext(config, contexts);
    try {
      liquibase.Liquibase liquibase = initLiquibase(config, liquibaseConfigFile);
      List<ChangeSet> changeSets =
          liquibase.listUnrunChangeSets(new Contexts(contexts), new LabelExpression());
      List<String> result = new ArrayList<>();
      for (ChangeSet cs : changeSets) {
        result.add(cs.toString());
      }
      return result;

    } catch (SQLException | LiquibaseException e) {
      throw new RuntimeException(e);
    }
  }

  public static void markNextChangeSet(
      DbConfig config, List<String> contexts, String liquibaseConfigFile) {
    contexts = addDefaultContext(config, contexts);
    try {
      liquibase.Liquibase liquibase = initLiquibase(config, liquibaseConfigFile);
      liquibase.markNextChangeSetRan(new Contexts(contexts), new LabelExpression());
    } catch (SQLException | LiquibaseException e) {
      throw new RuntimeException(e);
    }
  }

  private static liquibase.Liquibase initLiquibase(DbConfig config, String liquibaseConfigFile)
      throws SQLException, LiquibaseException {
    Connection jdbcConnection = DriverManager.getConnection(config.url, config.user, config.pass);
    Database database =
        DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(jdbcConnection));
    return new liquibase.Liquibase(
        liquibaseConfigFile, new ClassLoaderResourceAccessor(), database);
  }

  private static List<String> addDefaultContext(DbConfig config, List<String> contexts) {
    List<String> result = new ArrayList<>();
    if (contexts != null) {
      result.addAll(contexts);
    }
    result.add(config.label);
    return result;
  }
}
