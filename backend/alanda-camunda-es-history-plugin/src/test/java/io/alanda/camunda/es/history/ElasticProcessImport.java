package io.alanda.camunda.es.history;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.camunda.es.history.entity.ElasticSearchActivityHistory;
import io.alanda.camunda.es.history.entity.ElasticSearchProcessInstanceHistoryEntity;
import io.alanda.camunda.es.history.entity.ElasticSearchVariable;
import io.alanda.camunda.es.history.jackson.JsonTransformer;
import ch.qos.logback.classic.Level;

public class ElasticProcessImport {

  private static final Logger log = LoggerFactory.getLogger(ElasticProcessImport.class);

  private static String _configFile = "db/db-migration.properties";

  private static String _configFileFallback = "db/db-migration-default.properties";

  private static Map<String, DbConfig> dbConfigMap = new HashMap<>();

  private static String currentMode = "LOCAL";

  private static Set<String> toUpdate = new HashSet<String>();

  protected static JsonTransformer transformer = new JsonTransformer();

  public static class DbConfig {

    final String url;

    final String user;

    final String pass;

    final String schema;

    final String elasticHost;

    final int elasticPort;

    final String elasticIndex;

    public DbConfig(String url, String user, String pass, String elasticHost, int elasticPort, String elasticIndex) {
      super();
      this.url = url;
      this.user = user;
      this.pass = pass;
      this.schema = user;
      this.elasticHost = elasticHost;
      this.elasticPort = elasticPort;
      this.elasticIndex = elasticIndex;
    }

    @Override
    public String toString() {
      return "DbConfig [url=" + url + ", user=" + user + ", pass=" + pass + ", schema=" + schema + "]";
    }
  }

  private static Map<String, Consumer<String[]>> actionMap = new HashMap<>();
  static {
    loadConfig();
    actionMap.put("readProc", ElasticProcessImport::readProc);
    actionMap.put("impProc", ElasticProcessImport::impProc);
    actionMap.put("impMultiProc", ElasticProcessImport::impMultiProc);
    actionMap.put("cmpProcDef", ElasticProcessImport::cmpProcDef);
    actionMap.put("impProcDef", ElasticProcessImport::impProcDef);
    actionMap.put("cmpMultiProcDef", ElasticProcessImport::cmpMultiProcDef);
    actionMap.put("impSaved", ElasticProcessImport::impSaved);
    actionMap.put("setDb", ElasticProcessImport::setDb);
    actionMap.put("exit", (x) -> System.exit(0));
  }

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
      .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.INFO);
    log.info("Starting!!!");
    Scanner sc;
    //call actions via command line or via interactive shell
    if (args.length > 0) {
      sc = new Scanner(Arrays.stream(args).collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator())));
    } else {
      sc = new Scanner(System.in);
    }

    log.info("Commands are: {}", actionMap.keySet());
    while (sc.hasNextLine()) {
      String cmd = sc.next();
      String[] cmdArgs = sc.nextLine().trim().split(" ");
      Consumer<String[]> c = actionMap.get(cmd);
      if (c == null) {
        log.info("Unknown command {}, commands are: {}", cmd, actionMap.keySet());
      } else {
        log.info("Calling function {}", cmd);
        c.accept(cmdArgs);
      }
    }
  }

  private static URI getResourceUri(String name) {
    try {
      return ElasticProcessImport.class.getClassLoader().getResource(name).toURI();
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void loadConfig() {
    Properties prop = new Properties();
    File configFile = new File(getResourceUri(_configFile));
    if ( !configFile.isFile()) {
      configFile = new File(_configFileFallback);
    }
    try {
      prop.load(new FileInputStream(configFile));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    for (String s : new String[] {"LOCAL", "TEST", "PROD"}) {
      dbConfigMap
        .put(
          s.toUpperCase(),
          new DbConfig(
            prop.getProperty("db." + s.toLowerCase() + ".url"),
            prop.getProperty("db." + s.toLowerCase() + ".user"),
            prop.getProperty("db." + s.toLowerCase() + ".password"),
            prop.getProperty("db." + s.toLowerCase() + ".elastic_host"),
            Integer.parseInt(prop.getProperty("db." + s.toLowerCase() + ".elastic_port")),
            prop.getProperty("db." + s.toLowerCase() + ".elastic_index")));
    }

  }

  private static void setDb(String[] args) {
    log.info("started setDb {}", (Object) args);
    String db = args[0];
    if ( !dbConfigMap.containsKey(db)) {
      log.warn("Db Config {} not found, configs are: {}", db, dbConfigMap.keySet());
      return;
    }
    currentMode = db;
    log.info("Using  config: {}", currentMode);

  }

  private static void readProc(String[] args) {
    log.info("started readProc {}", (Object) args);
    String id = args[0];
    try (Connection con = getConnection()) {
      HistoricProcessInstanceEventEntity proc = readProcInst(con, id);
      if (proc != null) {
        log.info(proc.toString());
      }
      List<HistoricActivityInstanceEntity> aiList = readActInstForProc(con, id);
      List<HistoricTaskInstanceEventEntity> tiList = readTaskInstForProc(con, id);
      List<HistoricVariableUpdateEventEntity> viList = readVarInstForProc(con, id);
      log.info("{},\n{},\n{}", aiList, tiList, viList);
    } catch (SQLException ex) {
      log.warn("Error in readProc", ex);
    }

  }

  private static void impProc(String[] args) {
    DbConfig config = dbConfigMap.get(currentMode);
    log.info("started impProc {}", (Object) args);
    String id = args[0];

    try (Connection con = getConnection()) {
      RestHighLevelClient client = getClient();

      impOneProcess(config, id, con, client);
      client.close();
      //      log.info(res.getGetResult().);
    } catch (Exception ex) {
      log.warn("Error in impProc", ex);
    }

  }

  private static void impMultiProc(String[] args) {
    DbConfig config = dbConfigMap.get(currentMode);
    log.info("started impMultiProc {}", (Object) args);

    try (Connection con = getConnection()) {
      RestHighLevelClient client = getClient();
      for (String id : args) {
        log.info("Importing Process: {}", id);
        impOneProcess(config, id, con, client);
      }
      client.close();
      //      log.info(res.getGetResult().);
    } catch (Exception ex) {
      log.warn("Error in impMultiProc", ex);
    }

  }

  /**
   * Imports one Process Definition from DB to Elastic Search
   */
  private static void impProcDef(String[] args) {
    DbConfig config = dbConfigMap.get(currentMode);
    log.info("started impProcDef {}", (Object) args);
    String procDefKey = args[0];
    if (procDefKey.trim().length() == 0) {
      log.warn("Usage: impProcDef <PROC_DEF_KEY>");
      return;
    }

    try (Connection con = getConnection()) {
      RestHighLevelClient client = getClient();
      log.info("Query db");
      Set<String> inDb = listProcInstForProcDefDb(con, procDefKey);
      int count = inDb.size();
      int i = 0;
      log.info("importing {} results for Process {} to ES", count, procDefKey);
      for (String id : inDb) {
        log.info("Importing Process: {}", id);
        impOneProcess(config, id, con, client);
        log.info("{}/{}", ++i, count);
      }
      client.close();
      //      log.info(res.getGetResult().);
    } catch (Exception ex) {
      log.warn("Error in impMultiProc", ex);
    }

  }

  private static void impSaved(String[] args) {
    DbConfig config = dbConfigMap.get(currentMode);
    log.info("started impMultiProc {}", (Object) args);
    int count = toUpdate.size();
    int i = 0;
    log.info("importing previous {} results to ES: {}", count, toUpdate);

    try (Connection con = getConnection()) {
      RestHighLevelClient client = getClient();
      for (Iterator<String> it = toUpdate.iterator(); it.hasNext();) {
        String id = it.next();
        log.info("Importing Process: {}", id);
        impOneProcess(config, id, con, client);
        it.remove();
        log.info("{}/{}", ++i, count);
      }
      client.close();
      //      log.info(res.getGetResult().);
    } catch (Exception ex) {
      log.warn("Error in impMultiProc", ex);
    }

  }

  private static void cmpMultiProcDef(String[] args) {
    DbConfig config = dbConfigMap.get(currentMode);

    try (Connection con = getConnection()) {
      RestHighLevelClient client = getClient();
      log.info("started cmpMultiProcDef {}", (Object) args);
      for (String procDefKey : args) {

        log.info("Query db for {}", procDefKey);
        Set<String> inDb = listProcInstForProcDefDb(con, procDefKey);
        log.info("Query es for {}", procDefKey);
        Set<String> inEs = listProcInstForProcDefElastic(config, client, procDefKey);
        log.info("Db: {}, Es: {}", inDb.size(), inEs.size());
        Set<String> onlyDb = new HashSet<>(inDb);
        onlyDb.removeAll(inEs);
        Set<String> onlyEs = new HashSet<>(inEs);
        onlyEs.removeAll(inDb);
        log.info("onylInDb({}): {}, onlyInEs({}): {}", onlyDb.size(), onlyDb, onlyEs.size(), onlyEs);
        log.info("Saving {} procInstIds for Update to ES ", onlyDb.size());
        ElasticProcessImport.toUpdate.addAll(onlyDb);
      }
      client.close();
    } catch (Exception ex) {
      log.warn("Error in cmpProcDef", ex);
    }

  }

  private static void cmpProcDef(String[] args) {
    DbConfig config = dbConfigMap.get(currentMode);
    log.info("started cmpProcDef {}", (Object) args);
    String procDefKey = args[0];

    try (Connection con = getConnection()) {
      RestHighLevelClient client = getClient();
      log.info("Query db");
      Set<String> inDb = listProcInstForProcDefDb(con, procDefKey);
      log.info("Query es");
      Set<String> inEs = listProcInstForProcDefElastic(config, client, procDefKey);
      log.info("Db: {}, Es: {}", inDb.size(), inEs.size());
      Set<String> onlyDb = new HashSet<>(inDb);
      onlyDb.removeAll(inEs);
      Set<String> onlyEs = new HashSet<>(inEs);
      onlyEs.removeAll(inDb);
      log.info("onylInDb({}): {}, onlyInEs({}): {}", onlyDb.size(), onlyDb, onlyEs.size(), onlyEs);
      log.info("Saving {} procInstIds for Update to ES ", onlyDb.size());
      ElasticProcessImport.toUpdate.addAll(onlyDb);
      client.close();
    } catch (Exception ex) {
      log.warn("Error in cmpProcDef", ex);
    }

  }

  private static void impOneProcess(DbConfig config, String id, Connection con, RestHighLevelClient client)
      throws SQLException,
      IOException {
    HistoricProcessInstanceEventEntity proc = readProcInst(con, id);
    if (proc != null) {
      log.info(proc.toString());
    }
    List<HistoricActivityInstanceEntity> aiList = readActInstForProc(con, id);
    List<HistoricTaskInstanceEventEntity> tiList = readTaskInstForProc(con, id);
    List<HistoricVariableUpdateEventEntity> viList = readVarInstForProc(con, id);
    //    log.info(aiList);
    //    log.info(tiList);
    //    log.info(viList);
    ElasticSearchProcessInstanceHistoryEntity h = ElasticSearchProcessInstanceHistoryEntity.createFromHistoryEvent(proc);

    List<ElasticSearchActivityHistory> activities = new ArrayList<>();
    List<ElasticSearchVariable> variables = new ArrayList<>();
    for (HistoricActivityInstanceEntity ai : aiList) {
      if (ai.getActivityType().endsWith("Gateway") || ai.getActivityType().equals("userTask")) {
        continue;
      }
      ElasticSearchActivityHistory esh = new ElasticSearchActivityHistory(ai);
      activities.add(esh);
    }
    for (HistoricTaskInstanceEventEntity ti : tiList) {
      ElasticSearchActivityHistory esh = new ElasticSearchActivityHistory(ti);
      activities.add(esh);
    }
    activities.sort((x, y) -> x.getStartTime().compareTo(y.getStartTime()));
    h.setActivities(activities);
    for (HistoricVariableUpdateEventEntity vi : viList) {
      ElasticSearchVariable esv = new ElasticSearchVariable(vi);
      variables.add(esv);
    }
    h.setVariables(variables);
    String json = transformer.transformToJson(h);
    UpdateRequest updateRequest = new UpdateRequest(config.elasticIndex, "process", h.getProcessInstanceId())
      .doc(json, XContentType.JSON)
      .docAsUpsert(true);
    client.update(updateRequest, RequestOptions.DEFAULT);
  }

  public static RestHighLevelClient getClient() {
    DbConfig config = dbConfigMap.get(currentMode);
    InetAddress ia;
    try {
      ia = InetAddress.getByName(config.elasticHost);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException(config.elasticHost + " is not a valid address", e);
    }
    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(config.elasticHost, config.elasticPort, "http")));

    log.info("Successfully connected to {}:{}", config.elasticHost, config.elasticPort);
    return client;
  }

  private static Set<String> listProcInstForProcDefDb(Connection con, String id) throws SQLException {
    final Set<String> values = new HashSet<>();
    String sql = "select ID_ FROM ACT_HI_PROCINST WHERE PROC_DEF_KEY_=?";
    try (PreparedStatement st = con.prepareStatement(sql)) {
      st.setString(1, id);
      try (ResultSet res = st.executeQuery()) {
        while (res.next()) {
          values.add(res.getString("ID_"));
        }
      }
    }
    return values;
  }

  private static Set<String> listProcInstForProcDefElastic(DbConfig config, RestHighLevelClient client, String id)
      throws SQLException,
      IOException {
    final Set<String> values = new HashSet<>();
    SearchResponse res = client
      .search(
        new SearchRequest(config.elasticIndex)
          .source(
            SearchSourceBuilder.searchSource().query(QueryBuilders.termQuery("processDefinitionKey", id)).fetchSource(false).size(10000)),
        RequestOptions.DEFAULT);
    res.getHits().forEach(x -> values.add(x.getId()));
    return values;
  }

  private static HistoricProcessInstanceEventEntity readProcInst(Connection con, String id) throws SQLException {
    String sql = "select * FROM ACT_HI_PROCINST WHERE ID_=?";
    try (PreparedStatement st = con.prepareStatement(sql)) {
      st.setString(1, id);
      try (ResultSet res = st.executeQuery()) {
        res.next();
        HistoricProcessInstanceEventEntity proc = readHistProc(res);
        return proc;
      }
    }

  }

  private static List<HistoricActivityInstanceEntity> readActInstForProc(Connection con, String procInstId) throws SQLException {

    String sql = "select * FROM ACT_HI_ACTINST WHERE PROC_INST_ID_=? order by START_TIME_ asc";
    try (PreparedStatement st = con.prepareStatement(sql)) {
      st.setString(1, procInstId);
      try (ResultSet res = st.executeQuery()) {
        List<HistoricActivityInstanceEntity> activityList = new ArrayList<>();
        while (res.next()) {
          activityList.add(mapHistoryActivity(res));
        }
        return activityList;
      }
    }
  }

  private static HistoricActivityInstanceEntity mapHistoryActivity(ResultSet res) throws SQLException {
    HistoricActivityInstanceEntity i = new HistoricActivityInstanceEntity();
    i.setId(res.getString("ID_"));
    i.setActivityInstanceId(res.getString("ID_"));
    i.setParentActivityInstanceId(res.getString("PARENT_ACT_INST_ID_"));
    i.setProcessDefinitionId(res.getString("PROC_DEF_ID_"));
    i.setProcessInstanceId(res.getString("PROC_INST_ID_"));
    i.setExecutionId(res.getString("EXECUTION_ID_"));
    i.setActivityId(res.getString("ACT_ID_"));
    i.setTaskId(res.getString("TASK_ID_"));
    i.setCalledProcessInstanceId(res.getString("CALL_PROC_INST_ID_"));
    i.setActivityName(res.getString("ACT_NAME_"));
    i.setActivityType(res.getString("ACT_TYPE_"));
    i.setTaskAssignee(res.getString("ASSIGNEE_"));
    i.setStartTime(res.getTimestamp("START_TIME_"));
    i.setEndTime(res.getTimestamp("END_TIME_"));
    i.setDurationInMillis(getLong(res.getObject("DURATION_")));
    i.setActivityInstanceState(res.getInt("ACT_INST_STATE_"));
    i.setCalledCaseInstanceId(res.getString("CALL_CASE_INST_ID_"));
    i.setProcessDefinitionKey(res.getString("PROC_DEF_KEY_"));
    i.setSequenceCounter(res.getLong("SEQUENCE_COUNTER_"));
    i.setTenantId(res.getString("TENANT_ID_"));
    return i;
  }

  private static Long getLong(Object object) {
    if (object == null)
      return null;
    Number n = (Number) object;
    return n.longValue();
  }

  private static List<HistoricTaskInstanceEventEntity> readTaskInstForProc(Connection con, String procInstId) throws SQLException {

    String sql = "select * FROM ACT_HI_TASKINST WHERE PROC_INST_ID_=? order by START_TIME_ asc";
    try (PreparedStatement st = con.prepareStatement(sql)) {
      st.setString(1, procInstId);
      try (ResultSet res = st.executeQuery()) {
        List<HistoricTaskInstanceEventEntity> taskList = new ArrayList<>();
        while (res.next()) {
          taskList.add(mapHistoryTask(res));
        }
        return taskList;
      }
    }
  }

  private static HistoricTaskInstanceEventEntity mapHistoryTask(ResultSet res) throws SQLException {
    HistoricTaskInstanceEventEntity t = new HistoricTaskInstanceEventEntity();
    t.setId(res.getString("ID_"));
    t.setProcessDefinitionKey(res.getString("PROC_DEF_ID_"));
    t.setTaskDefinitionKey(res.getString("TASK_DEF_KEY_"));
    t.setProcessInstanceId(res.getString("PROC_INST_ID_"));
    t.setExecutionId(res.getString("EXECUTION_ID_"));
    t.setActivityInstanceId(res.getString("ACT_INST_ID_"));
    t.setParentTaskId(res.getString("PARENT_TASK_ID_"));
    t.setName(res.getString("NAME_"));
    t.setDescription(res.getString("DESCRIPTION_"));
    t.setOwner(res.getString("OWNER_"));
    t.setAssignee(res.getString("ASSIGNEE_"));
    t.setStartTime(res.getTimestamp("START_TIME_"));
    t.setEndTime(res.getTimestamp("END_TIME_"));
    t.setDurationInMillis(getLong(res.getObject("DURATION_")));
    t.setDeleteReason(res.getString("DELETE_REASON_"));
    t.setPriority(res.getInt("PRIORITY_"));
    t.setDueDate(res.getTimestamp("DUE_DATE_"));
    t.setFollowUpDate(res.getTimestamp("FOLLOW_UP_DATE_"));
    t.setCaseExecutionId(res.getString("CASE_EXECUTION_ID_"));
    t.setCaseInstanceId(res.getString("CASE_INST_ID_"));
    t.setCaseDefinitionId(res.getString("CASE_DEF_ID_"));
    t.setProcessDefinitionKey(res.getString("PROC_DEF_KEY_"));
    t.setCaseDefinitionKey(res.getString("CASE_DEF_KEY_"));
    t.setTenantId(res.getString("TENANT_ID_"));
    return t;
  }

  private static List<HistoricVariableUpdateEventEntity> readVarInstForProc(Connection con, String procInstId) throws SQLException {

    String sql = "select * FROM ACT_HI_VARINST WHERE PROC_INST_ID_=? and PROC_INST_ID_=EXECUTION_ID_";
    try (PreparedStatement st = con.prepareStatement(sql)) {
      st.setString(1, procInstId);
      try (ResultSet res = st.executeQuery()) {
        List<HistoricVariableUpdateEventEntity> variableList = new ArrayList<>();
        while (res.next()) {
          variableList.add(mapHistoryVariable(res));
        }
        return variableList;
      }
    }

  }

  private static HistoricVariableUpdateEventEntity mapHistoryVariable(ResultSet res) throws SQLException {
    HistoricVariableUpdateEventEntity v = new HistoricVariableUpdateEventEntity();
    v.setId(res.getString("ID_"));
    v.setVariableInstanceId(res.getString("ID_"));
    v.setProcessInstanceId(res.getString("PROC_INST_ID_"));
    v.setExecutionId(res.getString("EXECUTION_ID_"));
    v.setActivityInstanceId(res.getString("ACT_INST_ID_"));
    v.setScopeActivityInstanceId(res.getString("ACT_INST_ID_"));
    v.setTaskId(res.getString("TASK_ID_"));
    v.setVariableName(res.getString("NAME_"));
    v.setSerializerName(res.getString("VAR_TYPE_"));
    v.setRevision(res.getInt("REV_"));
    v.setByteArrayId(res.getString("BYTEARRAY_ID_"));
    v.setDoubleValue(res.getDouble("DOUBLE_"));
    v.setLongValue(getLong(res.getObject("LONG_")));
    v.setTextValue(res.getString("TEXT_"));
    v.setTextValue2(res.getString("TEXT2_"));
    v.setCaseInstanceId(res.getString("CASE_INST_ID_"));
    v.setCaseExecutionId(res.getString("CASE_EXECUTION_ID_"));
    v.setProcessDefinitionKey(res.getString("PROC_DEF_KEY_"));
    v.setProcessDefinitionId(res.getString("PROC_DEF_ID_"));
    v.setCaseDefinitionKey(res.getString("CASE_DEF_KEY_"));
    v.setCaseDefinitionId(res.getString("CASE_DEF_ID_"));
    v.setTenantId(res.getString("TENANT_ID_"));
    return v;
  }

  private static HistoricProcessInstanceEventEntity readHistProc(ResultSet res) throws SQLException {
    HistoricProcessInstanceEventEntity p = new HistoricProcessInstanceEventEntity();

    p.setId(res.getString("ID_"));
    p.setProcessInstanceId(res.getString("PROC_INST_ID_"));
    p.setBusinessKey(res.getString("BUSINESS_KEY_"));
    p.setProcessDefinitionId(res.getString("PROC_DEF_ID_"));
    p.setStartTime(res.getTimestamp("START_TIME_"));
    p.setEndTime(res.getTimestamp("END_TIME_"));
    p.setDurationInMillis(getLong(res.getObject("DURATION_")));
    p.setStartUserId(res.getString("START_USER_ID_"));
    p.setStartActivityId(res.getString("START_ACT_ID_"));
    p.setEndActivityId(res.getString("END_ACT_ID_"));
    p.setSuperProcessInstanceId(res.getString("SUPER_PROCESS_INSTANCE_ID_"));
    p.setDeleteReason(res.getString("DELETE_REASON_"));
    p.setCaseInstanceId(res.getString("CASE_INST_ID_"));
    p.setSuperCaseInstanceId(res.getString("SUPER_CASE_INSTANCE_ID_"));
    p.setProcessDefinitionKey(res.getString("PROC_DEF_KEY_"));
    p.setTenantId(res.getString("TENANT_ID_"));
    p.setState(res.getString("STATE_"));
    return p;
  }

  public static Connection getConnection() throws SQLException {
    // database connection
    DbConfig config = dbConfigMap.get(currentMode);
    return getConnection(config);
  }

  public static Connection getConnection(DbConfig config) throws SQLException {
    // database connection
    Connection jdbcConnection = DriverManager.getConnection(config.url, config.user, config.pass);
    return jdbcConnection;
  }
}
