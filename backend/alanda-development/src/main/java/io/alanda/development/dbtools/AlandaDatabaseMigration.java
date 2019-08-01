package io.alanda.development.dbtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class AlandaDatabaseMigration {

  private static String _alandaBaseDir = "db-migration/alanda/";

  private static String _alandaLiquibaseConfigMigrate =
      _alandaBaseDir + "migrate/changelog.migrate.xml";
  private static String _alandaLiquibaseConfigInit = _alandaBaseDir + "init/changelog.init.xml";

  protected static String _exportPath;

  protected static String _configFile = "db-migration/db-migration.properties";

  protected Map<String, DbConfig> dbConfigMap = new HashMap<>();

  protected Map<String, Consumer<String[]>> actionMap = new TreeMap<>();

  protected DbConfig actualDbConfig;

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    AlandaDatabaseMigration adm = new AlandaDatabaseMigration();
    adm.mainInitDatabaseMigration(args);
  }

  protected void mainInitDatabaseMigration(String[] args) {
    loadConfig();
    fillActionMap();
    startScanner(args);
  }

  protected void fillActionMap() {
    actionMap.put("init", this::initAlanda);
    actionMap.put("migrate", this::migrateAlanda);
    actionMap.put("markAlanda", this::markAlanda);
    actionMap.put("changes", this::changesAlanda);
    actionMap.put("setDb", this::setDb);
    actionMap.put("exit", (x) -> System.exit(0));
  }

  private void initAlanda(String[] args) {
    init();
  }

  private void migrateAlanda(String[] args) {
    boolean approved = true;
    if (!actualDbConfig.skipUserApproval) {
      printChangesLiquibase(changesLiquibase());
      approved = requestUserApproval();
    }
    if (approved) {
      migrate(true, true);
    }
  }

  private void markAlanda(String[] args) {
    Liquibase.markNextChangeSet(actualDbConfig, null, _alandaLiquibaseConfigMigrate);
  }

  private void changesAlanda(String[] args) {
    printChangesLiquibase(changesLiquibase());
  }

  protected void startScanner(String[] args) {
    Scanner sc;
    // call actions via command line or via interactive shell
    if (args.length > 0) {
      sc =
          new Scanner(
              Arrays.stream(args)
                  .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator())));
    } else {
      sc = new Scanner(System.in);
    }

    System.out.println("Commands are: " + actionMap.keySet());
    while (sc.hasNextLine()) {
      String cmd = sc.next();
      String[] cmdArgs = sc.nextLine().trim().split(" ");
      Consumer c = actionMap.get(cmd);
      if (c == null) {
        System.out.println("Unknown command " + cmd + ", commands are: " + actionMap.keySet());
      } else {
        System.out.println("Calling function " + cmd);
        try {
          c.accept(cmdArgs);
        } catch (RuntimeException ex) {
          ex.printStackTrace();
        }
        System.out.println("Finished with " + cmd);
      }
    }
  }

  protected static URI getResourceUri(String name) {
    try {
      return AlandaDatabaseMigration.class.getClassLoader().getResource(name).toURI();
    } catch (URISyntaxException ex) {
      throw new RuntimeException("could not get URI for " + name, ex);
    }
  }

  protected void loadConfig() {
    Properties prop = new Properties();
    File configFile = new File(getResourceUri(_configFile));
    if (!configFile.isFile()) {
      throw new IllegalStateException(
          MessageFormat.format("no config file ({0}) found", _configFile));
    }
    try {
      prop.load(new FileInputStream(configFile));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    _exportPath = prop.getProperty("fs.export");
    loadDbConfigs(prop);
  }

  private void loadDbConfigs(Properties prop) {
    Set<String> dbConfigKeys = new HashSet<>();
    String pattern = "^db\\.(.*)\\.url$";
    for (String propName : prop.stringPropertyNames()) {
      if (propName.matches(pattern)) {
        dbConfigKeys.add(propName.replaceAll(pattern, "$1"));
      }
    }
    for (String dbConfigKey : dbConfigKeys) {
      String allowInitString = prop.getProperty("db." + dbConfigKey + ".allowInit");
      boolean allowInit = allowInitString != null ? Boolean.valueOf(allowInitString) : false;
      String skipUserApprovalString = prop.getProperty("db." + dbConfigKey + ".skipUserApproval");
      boolean skipUserApproval =
          skipUserApprovalString != null ? Boolean.valueOf(skipUserApprovalString) : false;
      dbConfigMap.put(
          dbConfigKey,
          new DbConfig(
              prop.getProperty("db." + dbConfigKey + ".url"),
              prop.getProperty("db." + dbConfigKey + ".user"),
              prop.getProperty("db." + dbConfigKey + ".password"),
              allowInit,
              skipUserApproval,
              dbConfigKey));
    }

    if (dbConfigMap.containsKey("local")) {
      actualDbConfig = dbConfigMap.get("local");
      System.out.println("Set DbConfig to 'local'.");
    } else if (dbConfigMap.keySet().size() > 0) {
      actualDbConfig = dbConfigMap.get(dbConfigMap.keySet().iterator().next());
      System.out.println(String.format("Set DbConfig to '%s'.", actualDbConfig.label));
    } else {
      System.out.println("No DbConfig found!");
    }
  }

  protected void setDb(String[] args) {
    System.out.println("started setDb " + Arrays.toString(args));
    String db = args[0];
    if (!dbConfigMap.containsKey(db)) {
      System.out.println("Db Config " + db + " not found, configs are: " + dbConfigMap.keySet());
      return;
    }
    actualDbConfig = dbConfigMap.get(db);
    System.out.println("Using  config: " + actualDbConfig);
  }

  protected void init() {
    if (!actualDbConfig.allowInit) {
      throw new RuntimeException(
          "\"init\" is not allowed for db config \"" + actualDbConfig.label + "\"");
    }
    Liquibase.dropAll(actualDbConfig, _alandaLiquibaseConfigInit);
    Liquibase.update(actualDbConfig, null, _alandaLiquibaseConfigInit);
    Liquibase.update(actualDbConfig, null, _alandaLiquibaseConfigMigrate);
  }

  protected void migrate(boolean execLiqui, boolean execDbu) {
    if (execLiqui) {
      Liquibase.update(actualDbConfig, null, _alandaLiquibaseConfigMigrate);
    }
  }

  protected List<String> changesLiquibase() {
    List<String> changes = new ArrayList<>();
    changes.addAll(
        Liquibase.getUnrunChangesets(actualDbConfig, null, _alandaLiquibaseConfigMigrate));
    return changes;
  }

  protected void printChangesLiquibase(List<String> changes) {
    if (changes.size() == 0) {
      System.out.println("\nNo liquibase changeSets to run, your db is up to date.\n");
    } else {
      System.out.println("\nThe following liquibase changeSets will be executed:\n");
    }

    for (String s : changes) {
      System.out.println(s);
    }
  }

  protected List<String> changesDbUnit() {
    List<String> changes = new ArrayList<>();
    return changes;
  }

  protected void printChangesDbUnit(List<String> changes) {
    if (changes.size() == 0 || (changes.size() == 1 && StringUtils.isBlank(changes.get(0)))) {
      System.out.println("\nDbUnit will update NO tables, your db is up to date.\n");
    } else {
      System.out.println("\nThe following DbUnit changes will be applied:\n");
    }

    for (String s : changes) {
      System.out.println(s);
    }
  }

  protected static boolean requestUserApproval() {
    System.out.println("\nAre you sure you want to continue (enter 'SURE!' to proceed)?");
    Scanner sc = new Scanner(System.in);
    String cmd = sc.next();
    if (!cmd.equals("SURE!")) {
      System.out.println("aborting...");
      return false;
    }
    return true;
  }
}
