package io.alanda.development.dbtools;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class DbUnitInit {

  private static DbTable loadDbTableConfig(Path p, Map<String, String> queries) {
    Properties prop = loadPropFile(p.toUri().getPath(), false);
    String query = prop.getProperty("query");
    for (String qKey : queries.keySet()) {
      query = query.replace("$" + qKey, queries.get(qKey));
    }

    String pkString = prop.getProperty("pk");
    HashSet<String> pk;
    if (pkString != null) {
      pk = Sets.newHashSet(StringUtils.stripAll(pkString.split(",")));
    } else {
      pk = null;
    }
    return new DbTable(prop.getProperty("name"), pk, null, query);
  }

  private static Properties loadPropFile(String filePath, boolean relativeResourcePath) {
    Properties prop = new Properties();
    File configFile;

    if (relativeResourcePath) {
      URI fileURI = getResourceUri(filePath);
      configFile = new File(fileURI);
    } else {
      configFile = new File(filePath);
    }
    if (!configFile.isFile()) {
      throw new IllegalStateException(
          MessageFormat.format("no init data config file ({0}) found", filePath));
    }
    try {
      prop.load(new FileInputStream(configFile));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return prop;
  }

  public static DbTable[] loadInitDataConfig(String propertiesFilePath, String tableConfigDir) {
    Properties prop = loadPropFile(propertiesFilePath, true);
    List<DbTable> initDataTables = new ArrayList<>();
    Set<String> propNames = prop.stringPropertyNames();
    Map<String, String> queries = new HashMap<>();
    for (String n : propNames) {
      String q = prop.getProperty(n);
      for (String replace : propNames) {
        q = q.replace("$" + replace, prop.getProperty(replace));
      }
      queries.put(n, q);
    }
    DirectoryStream<Path> ds;
    try {
      URI tablePathURI = getResourceUri(tableConfigDir);
      ds = Files.newDirectoryStream(Paths.get(tablePathURI));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<Path> files = new ArrayList<>();
    ds.forEach(files::add);
    files.sort(Comparator.comparing(Path::getFileName));
    for (Path t : files) {
      if (t.toUri().getPath().endsWith(".properties")) {
        initDataTables.add(loadDbTableConfig(t, queries));
      }
    }
    return initDataTables.toArray(new DbTable[0]);
  }

  private static URI getResourceUri(String name) {
    try {
      return AlandaDatabaseMigration.class.getClassLoader().getResource(name).toURI();
    } catch (URISyntaxException ex) {
      throw new RuntimeException("could not get URI for " + name, ex);
    }
  }
}
