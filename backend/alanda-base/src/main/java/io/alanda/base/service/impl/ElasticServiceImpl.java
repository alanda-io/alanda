package io.alanda.base.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.TransactionSynchronizationRegistry;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.connector.ProjectTypeElasticListener;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcHistoryLogDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.dto.SimpleMilestoneDto;
import io.alanda.base.dto.SimplePhaseDto;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcAuthorizationService;
import io.alanda.base.service.PmcHistoryService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcProjectService.Mode;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.cdi.ElasticBulkUpdate;
import io.alanda.base.service.cdi.ElasticUpdate;
import io.alanda.base.util.LabelValueBean;
import io.alanda.base.util.cache.UserCache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

@Singleton
@ApplicationScoped()
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
@Named("elasticService")
public class ElasticServiceImpl implements ElasticService {

  public static final DateTimeFormatter DEFAULT_DATE_PRINTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

  @Inject
  @Any
  Event<ElasticUpdate> updateEvent;

  @Inject
  @Any
  Event<ElasticBulkUpdate> bulkUpdateEvent;

  private final Logger logger = LoggerFactory.getLogger(ElasticServiceImpl.class);

  @Inject
  private ConfigService configService;

  @Inject
  private Instance<PmcRefObjectConnector> refObjectConnectors;

  private Map<String, PmcRefObjectConnector> refObjectLoaders;

  @Inject
  PmcProjectService projectService;

  @Resource
  TransactionSynchronizationRegistry tsr;

  @Inject
  PmcAuthorizationService authorizationService;

  @Inject
  private UserCache userCache;

  @Inject
  private PmcPropertyService propertyService;

  @Inject
  private PmcHistoryService historyService;

  @Inject
  private Instance<ProjectTypeElasticListener> projectTypeElasticListeners;

  private Map<String, ProjectTypeElasticListener> elasticListeners;

  private RestHighLevelClient client;

  private ObjectMapper objectMapper;

  private String indexName = "pmc";

  private String taskIndexName = "pmc-task";

  private boolean active = false;

  public static final String KEY = "com.bpmasters.pmc.base.service.impl.ElasticServiceImpl.data";

  public ElasticServiceImpl() {
  }

  @PreDestroy
  private void closeELasticConnection() {
    if (this.client != null) {
      try {
        client.close();
      } catch (IOException e) {
        logger.warn("Error closing Elasticsearch Client: " + e, e);
      }
    }
  }

  @PostConstruct
  private void initElasticClient() {
    active = configService.getBooleanProperty(ConfigService.ELASTIC_ACTIVE);
    if ( !active) {
      logger.info("Elastic Service NOT active!!!!.");
      return;
    }

    try {
      logger.info("~host: " + configService.getProperty(ConfigService.ELASTIC_HOST));
      logger.info("~port: " + configService.getProperty(ConfigService.ELASTIC_PORT));
      client = new RestHighLevelClient(
        RestClient
          .builder(
            new HttpHost(
              configService.getProperty(ConfigService.ELASTIC_HOST),
              Integer.parseInt(configService.getProperty(ConfigService.ELASTIC_PORT)),
              "http")));
    } catch (Exception uhe) {
      logger.error("Could not connect to Elastic search!", uhe);
    }
    this.objectMapper = new ObjectMapper();
    this.indexName = configService.getProperty(ConfigService.ELASTIC_INDEX);
    this.taskIndexName = configService.getProperty(ConfigService.ELASTIC_TASK_INDEX);
    this.refObjectLoaders = new HashMap<>();
    for (PmcRefObjectConnector connector : refObjectConnectors) {
      logger.info("Connector found! " + connector.getClass());
      if (connector.getRefObjectType() != null) {
        logger.info("Connector for ObjectType: " + connector.getRefObjectType());
        refObjectLoaders.put(connector.getRefObjectType(), connector);
      }
    }

    this.elasticListeners = new HashMap<>();
    for (ProjectTypeElasticListener listener : projectTypeElasticListeners) {
      logger.info("Found elastic listener " + listener.getName());
      elasticListeners.put(listener.getName(), listener);
    }
  }

  @Override
  public void synchData(Integer ttlInMinutes) {
    if ( !active)
      return;
    QueryBuilder q = null;
    QueryBuilder exProj = existsQuery("project.guid");
    if (ttlInMinutes != null) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -ttlInMinutes);
      QueryBuilder activeProjects = boolQuery()
        .must(termsQuery("project.status.raw", "NEW", "PREPARED", "ACTIVE"))
        .must(rangeQuery("lastSyncTime").lt(DEFAULT_DATE_PRINTER.print(cal.getTime().getTime())));
      cal.add(Calendar.MINUTE, -ttlInMinutes * 100);
      QueryBuilder lastSync = rangeQuery("lastSyncTime")
        .lt(DEFAULT_DATE_PRINTER.print(cal.getTime().getTime()));
      QueryBuilder missing = boolQuery().mustNot(existsQuery("lastSyncTime"));
      QueryBuilder qOr = boolQuery().should(activeProjects).should(lastSync).should(missing);
      q = boolQuery().must(exProj).must(qOr);
    } else {
      q = exProj;
    }
    SearchRequest request = new SearchRequest(indexName)
      .types("process")
      .source(
        new SearchSourceBuilder()
          .query(q)
          .storedFields(Arrays.asList("project.guid", "lastSyncTime"))
          .sort(SortBuilders.fieldSort("lastSyncTime").order(SortOrder.ASC).missing("_first"))
          .size(300)
          .fetchSource(false));
    String sRequest = null;
    if (logger.isDebugEnabled()) {
      sRequest = request.toString();
    }
    SearchResponse res;

    try {
      res = client.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (logger.isDebugEnabled()) {
      logger
        .info(
          "synchData, ttlInMinutes: " +
            ttlInMinutes +
            " -> Found " +
            res.getHits().getTotalHits() +
            " entries for sync, query was: " +
            q.toString() +
            ", full: " +
            sRequest);
    }

    for (SearchHit sh : res.getHits().getHits()) {
      DocumentField shfLastSyncTime = sh.getFields().get("lastSyncTime");
      Date lastSyncTime = null;
      if (shfLastSyncTime != null) {
        Object o = shfLastSyncTime.getValue();
        if (o instanceof Date) {
          lastSyncTime = (Date) o;
          logger.info("~Date: " + o);
        } else if (o instanceof String) {
          try {
            lastSyncTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) o);
            logger.info("~lastSyncTime: " + lastSyncTime);
          } catch (ParseException e) {
            throw new RuntimeException("Cannot parse Date from String " + o + " " + e);
          }
          logger.info("~String-Date: " + o);
        } else {
          lastSyncTime = new Date((Long) o);
          logger.info("~Long-Date: " + o);
        }

      }
      Long pmcProjectGuid = null;
      Object oGuid = sh.getFields().get("project.guid").getValue();
      if (oGuid instanceof Integer) {
        pmcProjectGuid = ((Integer) oGuid).longValue();
      } else {
        pmcProjectGuid = (Long) oGuid;
      }
      String id = sh.getId();
      if (logger.isDebugEnabled()) {
        logger.debug("Found: id: " + id + ", guid: " + pmcProjectGuid + ", sync: " + lastSyncTime);
      }
      try {
        updateEntry(id, pmcProjectGuid);
      } catch (RuntimeException ex) {
        logger.warn("Error doing sync for Process " + id + ", pmcProjectGuid: " + pmcProjectGuid);
        if (tsr.getRollbackOnly()) {
          logger.warn("ROLLBACKONLY IS SET.. better quit now than later!!");
          return;
        }
      }
    }
  }

  public void updateEntry(Long pmcProjectGuid) {
    updateEntry(this.projectService.getProjectByGuid(pmcProjectGuid, Mode.RELATIONIDS));
  }

  @Override
  public void updateEntry(PmcProjectDto pmcProject) {
    updateEntry(pmcProject, false);
  }

  @Override
  public void updateEntry(PmcProjectDto pmcProject, boolean syncTasks) {
    //QueryBuilder exProj = QueryBuilders.termQuery("project.guid", pmcProject.getGuid());
    PmcProjectProcessDto mainProcess = pmcProject.mainProcess();
    if (mainProcess != null) {
      updateEntry(mainProcess.getProcessInstanceId(), pmcProject, syncTasks);
    }
  }

  private void updateEntry(String processInstanceId, Long pmcProjectGuid) {
    PmcProjectDto project = this.projectService.getProjectByGuid(pmcProjectGuid, Mode.RELATIONIDS);
    updateEntry(processInstanceId, project, false);
  }

  private void updateEntry(String processInstanceId, PmcProjectDto project, boolean syncTasks) {
    try {
      if (project == null) {
        logger.info("Deleting stale project for process: " + processInstanceId);
        client.delete(new DeleteRequest(indexName).type("process").id(processInstanceId), RequestOptions.DEFAULT);
        return;
      }
      if (project.getOwnerId() != null) {
        if (project.getOwnerName() == null) {
          PmcUserDto user = userCache.get(project.getOwnerId());
          if (user != null) {
            project.setOwnerName(user.getFirstName() + " " + user.getSurname());
          }
        }
      }
      project.setPropertiesMap(propertyService.getPropertiesMapForProject(project.getGuid()));
      PmcRefObjectConnector connector = null;
      Object refObject = null;
      Map<String, InternalContactDto> ic = new HashMap<>();
      if (project.getRefObjectType() != null) {
        connector = refObjectLoaders.get(project.getRefObjectType());
        if (connector == null) {
          throw new RuntimeException("no refObject connector found for type " + project.getRefObjectType());
        }
        refObject = connector.getRefObjectById(project.getRefObjectId());
        ic = getInternalContacts(connector, project.getGuid(), project.getRefObjectId());
      }
      Object customerProject = null;
      if (project.getCustomerProjectId() != null) {
        PmcRefObjectConnector reconConnector = refObjectLoaders.get("RECON");
        customerProject = reconConnector.getRefObjectById(project.getCustomerProjectId());
      }

      Set<String> roles = this.authorizationService.getReadRightGoups(project, ic);
      Map<String, Object> entry = new HashMap<>();

      ProjectTypeElasticListener l = elasticListeners.get(project.getPmcProjectType().getIdName());
      if (l != null) {
        l.filterInternalContacts(project, ic);
      }
      Map<String, SimplePhaseDto> phaseMap = new HashMap<>();
      for (SimplePhaseDto ph : project.getPhases()) {
        phaseMap.put(ph.getIdName(), ph);
      }

      project.setMilestones(projectService.getSimpleMilestonesPerProject(project.getGuid()));
      @SuppressWarnings("unchecked")
      Map<String, Object> msMap = new HashMap();
      for (SimpleMilestoneDto ms : project.getMilestones()) {
        msMap.put(ms.getIdName(), ms);
      }
      project.setMilestonesMap(msMap);
      project.setHistory(getHistoryForProject(project.getGuid()));
      entry.put("project", project);
      entry.put("refObject", refObject);
      entry.put("contacts", ic);
      entry.put("roles", roles);
      entry.put("customerProject", customerProject);
      entry.put("phaseMap", phaseMap);

      entry.put("lastSyncTime", new Date());
      byte[] json = objectMapper.writeValueAsBytes(entry);

      UpdateRequest updateReq = new UpdateRequest(indexName, "process", processInstanceId).doc(json, XContentType.JSON).docAsUpsert(true);
      if (logger.isDebugEnabled()) {
        logger.debug("Updated project and refObject for process " + processInstanceId);
      }
      addToSessionUR(processInstanceId, updateReq);

      // Experimental - Synch Task Index
      QueryBuilder qBuilder = QueryBuilders
        .boolQuery()
        .must(QueryBuilders.termQuery("project.guid", project.getGuid()))
        .must(QueryBuilders.termQuery("task.state.raw", "ACTIVE"));
      SearchResponse sr = client
        .search(
          new SearchRequest(taskIndexName).types("task").source(new SearchSourceBuilder().query(qBuilder).fetchSource(false)),
          RequestOptions.DEFAULT);
      if (sr.getHits().getTotalHits() > 0) {
        entry = new HashMap<>();
        entry.put("project", project);
        entry.put("lastSyncTime", new Date());
        json = objectMapper.writeValueAsBytes(entry);
        for (SearchHit sh : sr.getHits().getHits()) {
          updateReq = new UpdateRequest(taskIndexName, "task", sh.getId()).doc(json, XContentType.JSON);
          client.update(updateReq, RequestOptions.DEFAULT);
          addToSessionUR(sh.getId(), updateReq);
        }
      }
    } catch (Exception e) {
      logger.warn("Error indexing data for " + project.getRefObjectType() + " #" + project.getRefObjectId() + " :" + e.getMessage(), e);
      throw new RuntimeException(e);
    }

  }

  private List<PmcHistoryLogDto> getHistoryForProject(Long guid) {
    PmcHistoryLogDto dto = new PmcHistoryLogDto();
    dto.setPmcProjectGuid(guid);
    return historyService.searchHistory(dto, 1, 10000).getContent();
  }

  @Override
  public void updateTask(PmcTaskDto task) {
    try {
      PmcProjectDto project = null;
      Object refObject = null;

      if (task.getPmcProjectGuid() != null) {
        project = this.projectService.getProjectByGuid(task.getPmcProjectGuid(), Mode.RELATIONIDS);
      }
      if (task.getTaskType() != null && task.getObjectId() != null) {
        PmcRefObjectConnector connector = refObjectLoaders.get(task.getTaskType());
        if (connector == null || task.getObjectId() == null) {
          logger.warn("No connector found for " + task.getTaskType() + ", id: " + task.getObjectId() + ", name: " + task.getObjectName());
        } else {
          refObject = connector.getRefObjectById(task.getObjectId());
        }
      }
      //logger.info("Found refObject: " + refObject);
      Map<String, Object> entry = new HashMap<>();
      //entry.put("process", process);
      if (refObject instanceof RefObject) {
        RefObject ir = (RefObject) refObject;
        task.setObjectName(ir.getObjectName());
      }
      entry.put("task", task);
      entry.put("refObject", refObject);
      entry.put("project", project);
      entry.put("lastSyncTime", new Date());
      byte[] json = objectMapper.writeValueAsBytes(entry);
      if (task.getTaskId() != null) {
        UpdateRequest updateReq = new UpdateRequest(taskIndexName, "task", task.getTaskId()).doc(json, XContentType.JSON).docAsUpsert(true);
        client.update(updateReq, RequestOptions.DEFAULT);
        addToSessionUR(task.getProcessInstanceId(), updateReq);
      }
    } catch (Exception e) {
      logger.warn("Error indexing data for " + task.getTaskId() + " :" + e.getMessage(), e);
    }

  }

  @Override
  public void collectUpdates(@Observes(during = TransactionPhase.BEFORE_COMPLETION) ElasticUpdate event) {
    try {
      @SuppressWarnings("unchecked")
      List<UpdateRequest> transactionUpdateList = (List<UpdateRequest>) this.tsr.getResource(KEY);
      logger.info("BeforeCompletion, elasticUpdateList: " + transactionUpdateList.size());
      ElasticBulkUpdate ebu = new ElasticBulkUpdate();
      ebu.setBulkRequest(new BulkRequest());
      for (UpdateRequest ur : transactionUpdateList) {
        ebu.add("noid", ur);
      }
      this.bulkUpdateEvent.fire(ebu);
    } catch (Exception ex) {
      logger.info("Error in sessionEventAT " + ex.getMessage(), ex);
    }
  }

  @Override
  public void performUpdates(@Observes(during = TransactionPhase.AFTER_SUCCESS) ElasticBulkUpdate event) {
    try {
      logger.info("AfterSuccess, elasticBulkUpdate: " + event.getIds().size());
      BulkResponse br = client.bulk(event.getBulkRequest(), RequestOptions.DEFAULT);
      if (br.hasFailures()) {
        logger.info("Result: " + br.getItems().length + " responses, hasFailure: " + br.hasFailures() + " - " + br.buildFailureMessage());
      }
      logger.info("AfterSuccess, performed bulk update");
    } catch (Exception ex) {
      logger.info("Error in sessionEventAT " + ex.getMessage(), ex);
    }
  }

  private void addToSessionUR(String processInstanceId, UpdateRequest ur) {
    @SuppressWarnings("unchecked")
    List<UpdateRequest> transactionUpdateList = (List<UpdateRequest>) this.tsr.getResource(KEY);
    if (transactionUpdateList == null) {
      transactionUpdateList = new ArrayList<>();
      this.tsr.putResource(KEY, transactionUpdateList);
      this.updateEvent.fire(new ElasticUpdate());
    }
    transactionUpdateList.add(ur);

  }

  private Map<String, InternalContactDto> getInternalContacts(PmcRefObjectConnector connector, Long pmcProjectGuid, Long refObjectId) {
    List<InternalContactDto> contacts = projectService.getContacts(pmcProjectGuid);
    List<InternalContactDto> refObjectContacts = null;
    Map<String, InternalContactDto> retVal = new HashMap<>();
    if (connector != null) {
      refObjectContacts = connector.getContacts(refObjectId);
      if (refObjectContacts != null) {
        for (InternalContactDto ic : refObjectContacts) {
          retVal.put(ic.getRoleName(), ic);
        }
      }
    }
    for (InternalContactDto ic : contacts) {
      retVal.put(ic.getRoleName(), ic);
    }
    return retVal;
  }

  @Override
  public void addProcess(String processInstanceId, PmcProjectProcessDto process, PmcProjectDto project, boolean updateRelated) {
    if ( !active)
      return;
    updateEntry(processInstanceId, project, false);
    if (updateRelated) {
      if (project.getParentIds() != null) {
        for (Long parentId : project.getParentIds()) {
          updateEntry(parentId);
        }
      }
    }
  }

  @Override
  public void updateProcessInfo(String processInstanceId, PmcProjectProcessDto process) {
    if ( !active)
      return;
    try {
      Map<String, Object> entry = new HashMap<>();
      entry.put("process", process);

      byte[] json = objectMapper.writeValueAsBytes(entry);

      client.update(new UpdateRequest(indexName, "process", processInstanceId).doc(json, XContentType.JSON), RequestOptions.DEFAULT);
    } catch (Exception e) {
      logger.error("Error updating process info in index!", e);
    }
  }

  @Override
  public void updateRefObject(String processInstanceId, String refObjectType, Long refObjectId) {
    if ( !active)
      return;
    try {
      PmcRefObjectConnector connector = refObjectLoaders.get(refObjectType);
      Object refObject = connector.getRefObjectById(refObjectId);
      Map<String, Object> entry = new HashMap<>();
      entry.put("refObject", refObject);
      byte[] json = objectMapper.writeValueAsBytes(entry);
      client.update(new UpdateRequest(indexName, "process", processInstanceId).doc(json, XContentType.JSON), RequestOptions.DEFAULT);
    } catch (Exception e) {
      logger.error("Error updating RefObject in index!", e);
    }
  }

  @Override
  public SearchHit[] findProcessesForProject(String projectId) {
    QueryBuilder queryB = matchQuery("project.projectId", projectId);
    SearchResponse resp;
    try {
      resp = client
        .search(new SearchRequest(indexName).types("process").source(new SearchSourceBuilder().query(queryB)), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return resp.getHits().getHits();
  }

  @Override
  public SearchHit[] findByProjectType(String projectType) {
    QueryBuilder queryB = matchQuery("project.pmcProjectType.idName", projectType);
    SearchResponse resp;
    try {
      resp = client
        .search(
          new SearchRequest(indexName)
            .types("process")
            .source(new SearchSourceBuilder().query(queryB).storedFields(Arrays.asList("project.guid")).size(10000).fetchSource(true)),
          RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return resp.getHits().getHits();
  }

  @Override
  public SearchHit[] findByTemplate(String query, Map<String, Object> params, String[] fields, int from, int size, boolean fetchSource) {
    return findByTemplate("process", indexName, query, params, fields, from, size, fetchSource);
  }

  @Override
  public SearchHit[] findByTemplateFromTaskIndex(
      String query,
      Map<String, Object> params,
      String[] fields,
      int from,
      int size,
      boolean fetchSource) {
    return findByTemplate("task", taskIndexName, query, params, fields, from, size, fetchSource);
  }

  // by default only 100mb can be returned by a request which is not enough for us
  private static RequestOptions LARGE_RESPONSE_OPTIONS;
  {
    RequestOptions.Builder builder= RequestOptions.DEFAULT.toBuilder();
    builder.setHttpAsyncResponseConsumerFactory(
        new HttpAsyncResponseConsumerFactory
            .HeapBufferedResponseConsumerFactory(400 * 1024 * 1024));
    LARGE_RESPONSE_OPTIONS = builder.build();
  }

  private SearchHit[] findByTemplate(
      String type,
      String indexName,
      String query,
      Map<String, Object> params,
      String[] fields,
      int from,
      int size,
      boolean fetchSource) {
    SearchSourceBuilder ssb= new SearchSourceBuilder();
    if (fields != null && fields.length == 0) {
      ssb.storedFields(Arrays.asList(fields));
    }
    ssb.from(from).size(size).fetchSource(fetchSource);
    SearchTemplateRequest request = new SearchTemplateRequest();
    request.setScriptType(ScriptType.INLINE);
    request.setScript(query);
    request.setScriptParams(params);
    request
      .setRequest(
        new SearchRequest(indexName)
          .types(type)
          .source(ssb));

    SearchTemplateResponse str;
    try {
      BytesReference br= XContentHelper.toXContent(request, XContentType.JSON, true);
      logger.debug("Request: " + XContentHelper.convertToJson(br, false, XContentType.JSON));
      str = client.searchTemplate(request, LARGE_RESPONSE_OPTIONS);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return str.getResponse().getHits().getHits();
  }

  @Override
  public SearchHits findProjects(Map<String, Object> filterParams, Map<String, Object> sortParams, int from, int size) {
    return findEntries(filterParams, sortParams, from, size, null, null, indexName);
  }

  @Override
  public SearchHits findProjects(
      Map<String, Object> filterParams,
      Map<String, Object> sortParams,
      int from,
      int size,
      String[] sourceIncludes,
      String[] sourceExcludes) {
    return findEntries(filterParams, sortParams, from, size, sourceIncludes, sourceExcludes, indexName);
  }

  private SearchHits findEntries(
      Map<String, Object> filterParams,
      Map<String, Object> sortParams,
      int from,
      int size,
      String[] sourceIncludes,
      String[] sourceExcludes,
      String indexName) {

    if ( !active) {
      return SearchHits.empty();
    }

    BoolQueryBuilder builder = boolQuery();
    if (filterParams != null && !filterParams.isEmpty()) {
      for (String field : filterParams.keySet()) {
        QueryBuilder criteria = null;
        boolean not = false;
        Object tmp = filterParams.get(field);
        if (tmp instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, Object> tmpm = (Map<String, Object>) tmp;
          String fieldType = (String) tmpm.get("fieldType");
          String value = (String) tmpm.get("value");
          if (value.startsWith("!")) {
            value = value.substring(1);
            not = true;
          }
          String tmps = field;
          if ( !Strings.isNullOrEmpty(fieldType)) {
            tmps += "." + fieldType;
          }
          if ("raw".equals(fieldType)) {
            criteria = termQuery(tmps, value);
          } else { //TODO: extend for further types; currently only RAW and NGRAM
            criteria = matchQuery(tmps, value);
          }
        } else if (tmp instanceof QueryBuilder) {
          criteria = (QueryBuilder) tmp;
          if (field.startsWith("!"))
            not = true;
        } else if (tmp instanceof Collection) {
          criteria = termsQuery(field, (Collection) tmp);
        } else if (tmp instanceof String) {
          String filterValue = (String) tmp;
          if ( !filterValue.isEmpty()) {
            if (filterValue.startsWith("!")) {
              filterValue = filterValue.substring(1);
              not = true;
            }
            String tmps = field + ".ngram";
            criteria = matchQuery(tmps, filterValue).operator(Operator.AND);
          }
        } else if (tmp instanceof Integer || tmp instanceof Long) {
          criteria = termQuery(field, tmp);
        } else if (tmp instanceof Date) {
          criteria = termQuery(field, tmp);
        }

        if ((tmp != null) && (criteria != null)) {
          if (not) {
            builder.mustNot(criteria);
          } else {
            builder.must(criteria);
          }
        }
      }
    }
    if ( !builder.hasClauses() && (this.indexName.equals(indexName))) {
      builder.must(existsQuery("project"));
    }
    String type = "process";
    if (this.taskIndexName.equals(indexName)) {
      type = "task";
    }
    SearchSourceBuilder searchSourcB = new SearchSourceBuilder().query(builder).from(from).size(size).fetchSource(true);
    if (sortParams != null && !sortParams.isEmpty()) {
      SortedMap<Integer, LabelValueBean> sorted = orderSortOptionsByPriority(sortParams);
      for (Integer sort : sorted.keySet()) {
        LabelValueBean entry = sorted.get(sort);
        SortOrder order = entry.getValue().equals("asc") ? SortOrder.ASC : SortOrder.DESC;
        searchSourcB.sort(entry.getLabel(), order);
      }
    }

    if (sourceIncludes != null || sourceExcludes != null) {
      searchSourcB.fetchSource(sourceIncludes, sourceExcludes);
    }
    logger.info(searchSourcB.toString());
    SearchResponse resp;
    try {
      resp = client.search(new SearchRequest(indexName).types(type).source(searchSourcB), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return resp.getHits();
  }

  @Override
  public SearchHits findTasks(Map<String, Object> filterParams, Map<String, Object> sortParams, int from, int size) {
    return findEntries(filterParams, sortParams, from, size, null, null, taskIndexName);
  }

  private SortedMap<Integer, LabelValueBean> orderSortOptionsByPriority(Map<String, Object> sortOptions) {

    SortedMap<Integer, LabelValueBean> sorted = new TreeMap<>();
    for (Entry<String, Object> option : sortOptions.entrySet()) {
      @SuppressWarnings("unchecked")
      Map<String, Object> entry = (Map<String, Object>) option.getValue();
      Integer p = (Integer) entry.get("prio");
      LabelValueBean lvb = new LabelValueBean(option.getKey(), (String) entry.get("dir"));
      sorted.put(p, lvb);
    }
    return sorted;
  }

  @Override
  public SearchHit[] findSubProcessesForProject(Integer projectGuid) {
    QueryBuilder boolQueryB = boolQuery()
      .must(matchQuery("variables.varName", "pmcProjectGuid"))
      .must(matchQuery("variables.textValue", projectGuid));
    QueryBuilder nestedQueryB = nestedQuery("variables", boolQueryB, org.apache.lucene.search.join.ScoreMode.None);
    QueryBuilder queryB = boolQuery().must(wildcardQuery("superProcessInstanceId", "*")).filter(nestedQueryB);
    SearchResponse resp;
    try {
      resp = client
        .search(
          new SearchRequest(indexName).types("process").source(new SearchSourceBuilder().query(queryB).size(10000)),
          RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return resp.getHits().getHits();
  }

  @Override
  public void refreshTaskIndex() {
    try {
      client.indices().refresh(new RefreshRequest(taskIndexName), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void refreshProcessIndex() {
    try {
      client.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteTask(String id) {
    try {
      client.delete(new DeleteRequest(taskIndexName, "task", id), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Map<String, String> getProcessVariablesForProcess(String pid) {
    QueryBuilder queryB = matchQuery("processInstanceId", pid);
    SearchResponse resp;
    try {
      resp = client
        .search(
          new SearchRequest(indexName).types("process").source(new SearchSourceBuilder().query(queryB).fetchSource("variables", null)),
          RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    SearchHit[] hits = resp.getHits().getHits();
    if (hits == null || hits.length == 0) {
      return null;
    } else if (hits.length > 1) {
      logger.warn("found multiple entries in " + indexName + " and type process for pid=" + pid);
    }
    Map<String, String> variables = new HashMap<>();
    @SuppressWarnings("unchecked")
    ArrayList<Map<String, String>> vars = (ArrayList<Map<String, String>>) hits[0].getSourceAsMap().get("variables");
    if (vars == null) {
      return variables;
    }
    for (Map<String, String> var : vars) {
      variables.put(var.get("varName"), var.get("textValue"));
    }
    return variables;
  }
}
