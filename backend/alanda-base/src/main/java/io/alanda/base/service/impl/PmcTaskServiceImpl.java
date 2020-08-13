package io.alanda.base.service.impl;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcListenerService;
import io.alanda.base.service.PmcTaskService;
import io.alanda.base.service.impl.task.TaskRetrieverService;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.ElasticUtil;
import io.alanda.base.util.UserContext;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.ArrayUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.cdi.BusinessProcessEvent;
import org.camunda.bpm.engine.cdi.BusinessProcessEventType;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cmd.SaveTaskCmd;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class PmcTaskServiceImpl implements PmcTaskService {

  private static final Logger log = LoggerFactory.getLogger(PmcTaskServiceImpl.class);

  @Inject
  private TaskService taskService;

  @Inject
  private HistoryService historyService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private ElasticService elasticService;

  @Inject
  private PmcListenerService pmcListenerService;

  @Inject
  private TaskRetrieverService taskRetrieverService;

  private final FastDateFormat taskListDateFormat = FastDateFormat.getInstance("yyyy-MM-dd");

  @Override
  public PmcTaskDto getTask(String taskId) {
    return getTask((TaskEntity) taskService.createTaskQuery().initializeFormKeys().taskId(taskId).singleResult());
  }

  @Override
  public PmcTaskDto getTask(TaskEntity t) {
    if (t == null) {
      log.warn("Can't map null task, returning null as well");
      return null;
    }

    return taskRetrieverService.getTask(t);
  }

  @Override
  public Collection<PmcTaskDto> getTasksForProcessInstanceId(String pid) {

    Collection<PmcTaskDto> result = new ArrayList<>();

    List<Task> tasks = this.taskService.createTaskQuery().processInstanceId(pid).initializeFormKeys().list();
    for (Task task : tasks) {
      result.add(getTask((TaskEntity) task));
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<PmcTaskDto> getTasksForPmcProjectFromElastic(Long pmcProjectGuid) {

    Collection<PmcTaskDto> result = new ArrayList<>();

    String query = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"project.guid\":\"{{pmcProjectGuid}}\"}}," +
      "{\"term\":{\"task.state.raw\":\"ACTIVE\"}}]}},\"_source\":[\"task.process_instance_id\",\"task.task_id\",\"task.actinst_type\",\"task.task_name\",\"task.candidateGroups\",\"task.created\",\"task.due\",\"task.follow_up\",\"task.formKey\",\"task.assignee\",\"task.state\"],\"size\": 1000}";

    Map<String, Object> params = new HashMap<>();
    params.put("pmcProjectGuid", pmcProjectGuid);
    SearchHit[] hits = elasticService.findByTemplateFromTaskIndex(query, params, null, 0, 10000, true);
    for (SearchHit h : Arrays.asList(hits)) {

      PmcTaskDto t = new PmcTaskDto();
      Map<String, Object> taskMap = (Map<String, Object>) h.getSourceAsMap().get("task");
      t.setProcessInstanceId((String) taskMap.get("process_instance_id"));
      t.setTaskId((String) taskMap.get("task_id"));
      t.setTaskName((String) taskMap.get("task_name"));
      t.setCreated((String) taskMap.get("created"));
      t.setDue((String) taskMap.get("due"));
      t.setFormKey((String) taskMap.get("formKey"));
      t.setAssignee((String) taskMap.get("assignee"));
      t.setFollowUp((String) taskMap.get("follow_up"));
      t.setActivityInstanceType((String) taskMap.get("actinst_type"));
      String tState = (String) taskMap.get("state");
      if (tState != null)
        t.setState(PmcProjectState.valueOf(tState));
      if (taskMap.get("candidateGroups") != null)
        t.setCandidateGroups((List<String>) (List) taskMap.get("candidateGroups"));
      result.add(t);
    }
    return result;
  }

  @Override
  public PagedResultDto<Object> getTasksElastic(Map<String, Object> filterOptions, Map<String, Object> sortOptions, int from, int size) {
    List<Object> results = new ArrayList<>();

    PmcUserDto user = UserContext.getUser();

    filterOptions.put("task.state", ElasticUtil.raw(PmcProjectState.ACTIVE));
    filterOptions.put("task.actinst_type", ElasticUtil.raw("task"));
    if (sortOptions == null)
      sortOptions = new HashMap<>();
    if (sortOptions.isEmpty()) {
      sortOptions.put("task.created", ElasticUtil.sort(1, SortOrder.DESC));
    }

    if (filterOptions.containsKey("mytasks")) {
      filterOptions.remove("mytasks");
      filterOptions.remove("task.candidateGroups.raw");
      filterOptions.put("task.assignee_id", ElasticUtil.raw(UserContext.getUser().getGuid().toString()));
    } else {
      if (user.isAdmin()) {
        filterOptions.remove("task.candidateGroups.raw");
      } else {
        List<PmcGroupDto> groups = user.getGroupList();
        List<String> groupIds = new ArrayList<>();
        for (PmcGroupDto group : groups) {
          groupIds.add(group.getLongName());
        }
        filterOptions.put("task.candidateGroups.raw", groupIds);
      }
    }
    if (filterOptions.containsKey("hideSnoozedTasks")) {
      filterOptions.remove("hideSnoozedTasks");
      filterOptions.put("!task.follow_up", QueryBuilders.rangeQuery("task.follow_up").gt(taskListDateFormat.format(new Date())));
    }

    SearchHits hits = elasticService.findTasks(filterOptions, sortOptions, from, size);
    for (SearchHit hit : hits.getHits()) {
      Map<String, Object> project = (Map<String, Object>) hit.getSourceAsMap();//.get("project");
      results.add(project);
    }
    PagedResultDto<Object> res = new PagedResultDto<>();
    res.setResults(results);
    res.setTotal(hits.getTotalHits());
    return res;
  }

  @Override
  public void updateAllTasks(int firstResult, int maxResults, boolean inverse) {
    if (inverse) {
      checkStaleElasticTasks(firstResult, maxResults);
      return;
    }
    List<Task> tl = taskService.createTaskQuery().orderByTaskCreateTime().asc().initializeFormKeys().listPage(firstResult, maxResults);
    log.info("updateAllTasks, params: firstResult: {}, maxResults: {}, size: {}", firstResult, maxResults, tl.size());
    int i = 0;
    for (Task task : tl) {
      i++ ;
      if (i % 100 == 0) {
        log.info("updateAllTasks, status: {}/{}", i, tl.size());
      }
      try {
        long startTime = System.currentTimeMillis();
        updateTask(task);
        long endTime = System.currentTimeMillis();
        if (endTime - startTime > 300) {
          log.warn("{} millis for Indexing task #{} ({}) for process #{}", endTime - startTime, task.getId(), task.getName(), task.getProcessInstanceId());
        }
      } catch (Exception ex) {
        log.warn("Error indexing task #{} ({}) for process #{}", task.getId(), task.getName(), task.getProcessInstanceId());
        throw ex;
      }
    }
  }

  private void checkStaleElasticTasks(int firstResult, int maxResults) {
    Map<String, Object> sortOptions = new HashMap<>();
    sortOptions.put("task.created", ElasticUtil.sort(1, SortOrder.ASC));
    PagedResultDto<Object> dto = this.getTasksElastic(new HashMap<String, Object>(), sortOptions, firstResult, maxResults);
    for (Object o : dto.getResults()) {
      @SuppressWarnings("unchecked")
      Map<String, Object> te = (Map<String, Object>) o;
      @SuppressWarnings("unchecked")
      Map<String, Object> tm = (Map<String, Object>) te.get("task");
      String id = (String) tm.get("task_id");
      Task theTask = this.taskService.createTaskQuery().taskId(id).singleResult();
      log.info("Task #{}, exists: {}", id, theTask != null);
      if (theTask == null) {
        elasticService.deleteTask(id);
      }
    }

  }

  @Override
  public void updateTask(String taskId) {
    Task task = taskService.createTaskQuery().initializeFormKeys().taskId(taskId).singleResult();
    updateTask(task);
  }

  @Override
  public void updateTask(Task task) {
    PmcTaskDto dto = getTask((TaskEntity) task);
    elasticService.updateTask(dto);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @Override
  public void taskEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) BusinessProcessEvent businessProcessEvent) {
    // Only observe task Events
    if (businessProcessEvent.getTask() == null) {
      return;
    }

    log.info("Update for Task #{}({}) action={}", businessProcessEvent.getTaskId(),
            businessProcessEvent.getTask().getName(),businessProcessEvent.getType().getTypeName());
    final PmcTaskDto dto = getTask((TaskEntity) businessProcessEvent.getTask());

    if (businessProcessEvent.getType().equals(BusinessProcessEventType.COMPLETE_TASK)) {
      dto.setState(PmcProjectState.COMPLETED);
    } else if (businessProcessEvent.getType().equals(BusinessProcessEventType.DELETE_TASK)) {
      dto.setState(PmcProjectState.CANCELED);
    } else {
      dto.setState(PmcProjectState.ACTIVE);
    }
    dto.setActivityInstanceType("task");

    elasticService.updateTask(dto);
  }

  private static List<String> activityTypesToHandle;
  {
    activityTypesToHandle = Arrays
      .asList(
        "boundaryMessage",
        "boundarySignal",
        "boundaryTimer",
        "eventBasedGateway",
        "intermediateTimer",
        "intermediateMessageCatch",
        "intermediateConditional");
  }

  @Override
  public void activityEvent(@Observes BusinessProcessEvent businessProcessEvent) {
    if (businessProcessEvent.getActivityId() == null || businessProcessEvent.getTask() != null) {
      return;
    }

    ActivityInstance ai = getActivityInstance(businessProcessEvent.getProcessInstanceId(),
        businessProcessEvent.getActivityId(),
        businessProcessEvent.getExecutionId());

    if (ai == null) {
      log.warn("no activity instance found for pid={}, activityId={}, executionId={}",
              businessProcessEvent.getProcessInstanceId(), businessProcessEvent.getActivityId(), businessProcessEvent.getExecutionId());
      return;
    }

    if (!activityTypesToHandle.contains(ai.getActivityType())) {
      return;
    }

    log.info(String.format("Update for activity #%s (%s) action=%s",
        ai.getId(),
        ai.getActivityName(),
        businessProcessEvent.getType().getTypeName()));

    PmcTaskDto dto = new PmcTaskDto();
    if (businessProcessEvent.getType().equals(BusinessProcessEventType.START_ACTIVITY)) {
      dto.setState(PmcProjectState.ACTIVE);
      dto.setCreated(taskListDateFormat.format(new Date()));
    } else if (businessProcessEvent.getType().equals(BusinessProcessEventType.END_ACTIVITY)) {
      dto.setState(PmcProjectState.COMPLETED);
    } else {
      log.warn("Unexpected BusinessProcessEventType {}", businessProcessEvent.getType().getTypeName());
    }

    dto.setActivityInstanceType("activity");
    dto.setTaskId(ai.getId());
    dto.setTaskType(ai.getActivityType());
    if (ai.getActivityName() != null) {
      dto.setTaskName(ai.getActivityName());
    } else {
      dto.setTaskName(ai.getActivityType());
      log.warn("Activity name is null!");
    }
    dto.setExecutionId(businessProcessEvent.getExecutionId());
    dto.setProcessDefinitionId(businessProcessEvent.getProcessDefinition().getId());
    dto.setProcessInstanceId(businessProcessEvent.getProcessInstanceId());
    dto.setProcessName(businessProcessEvent.getProcessDefinition().getName());
    dto.setProcessDefinitionKey(businessProcessEvent.getProcessDefinition().getKey());
    Long pmcProjectGuid = (Long) runtimeService.getVariable(businessProcessEvent.getExecutionId(), ProcessVariables.PMC_PROJECT_GUID);
    dto.setPmcProjectGuid(pmcProjectGuid);
    elasticService.updateTask(dto);
  }

  private ActivityInstance getActivityInstance(String pid, String activityId, String executionId) {
    ActivityInstance processAi = runtimeService.getActivityInstance(pid);
    if (processAi == null) {
      log.warn("could not load process activity instance");
      return null;
    }
    ActivityInstance[] ais = processAi.getActivityInstances(activityId);
    if (ais == null) {
      log.warn("could not load child activity instances");
      return null;
    }
    for (ActivityInstance ai : ais) {
      if (ArrayUtils.contains(ai.getExecutionIds(), executionId)) {
        return ai;
      }
    }
    return null;
  }

  @Override
  public int snoozeTask(String taskDefinitionKey, Long pmcProjectGuid, Date followUpDate) {
    int modified = 0;
    List<Task> taskList = taskService.createTaskQuery()
                                     .processVariableValueEquals(ProcessVariables.PMC_PROJECT_GUID, pmcProjectGuid)
                                     .taskDefinitionKey(taskDefinitionKey)
                                     .list();
    log.info("Setting followUpDate to {} for Tasks: {}", followUpDate, taskList);
    for (Task t : taskList) {
      updateFollowUpDateOfTask(t.getId(), followUpDate);
      modified++ ;
    }
    return modified;
  }

  @Override
  public void updateFollowUpDateOfTask(String taskId, Date followUpDate) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    updateFollowUpDateOfTask(task, followUpDate);
  }

  @Override
  public void updateFollowUpDateOfTask(Task task, Date followUpDate) {
    ProcessEngineConfigurationImpl configuration = ((ProcessEngineImpl) ProcessEngines.getDefaultProcessEngine()).getProcessEngineConfiguration();
    CommandExecutor commandExecutor = configuration.getCommandExecutorTxRequired();
    commandExecutor.execute(new UpdateFollowUpDateCommand(task, followUpDate));
  }

  public class UpdateFollowUpDateCommand extends SaveTaskCmd {

    Date followUpDate;

    private static final long serialVersionUID = -8847556702903010159L;

    public UpdateFollowUpDateCommand(Task task, Date followUpDate) {
      super(task);
      this.followUpDate = followUpDate;
    }

    @Override
    public Void execute(CommandContext commandContext) {
      task.setFollowUpDate(followUpDate);
      task.fireEvent(TaskListener.EVENTNAME_ASSIGNMENT);
      return super.execute(commandContext);
    }

  }

  @Override
  public void updateDueDateOfTask(String taskId, Date dueDate) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    ProcessEngineConfigurationImpl configuration = ((ProcessEngineImpl) ProcessEngines.getDefaultProcessEngine()).getProcessEngineConfiguration();
    CommandExecutor commandExecutor = configuration.getCommandExecutorTxRequired();
    commandExecutor.execute(new UpdateDueDateCommand(task, dueDate));

    Long pmcProjectGuid = (Long) runtimeService.getVariable(task.getExecutionId(), ProcessVariables.PMC_PROJECT_GUID);
    if (pmcProjectGuid != null) {
      for (PmcProjectListener l : pmcListenerService.getListenerForProject(pmcProjectGuid)) {
        l.afterTaskDueDateSet(pmcProjectGuid, task, dueDate);
      }
    }

  }

  public class UpdateDueDateCommand extends SaveTaskCmd {

    Date dueDate;

    private static final long serialVersionUID = -8847556702903010159L;

    public UpdateDueDateCommand(Task task, Date dueDate) {
      super(task);
      this.dueDate = dueDate;
    }

    @Override
    public Void execute(CommandContext commandContext) {
      task.setDueDate(dueDate);
      task.fireEvent(TaskListener.EVENTNAME_ASSIGNMENT);
      return super.execute(commandContext);
    }

  }

  @Override
  public boolean checkTaskAccess(String taskId) {
    Task task = taskService.createTaskQuery().initializeFormKeys().taskId(taskId).singleResult();
    return checkTaskAccess(task);
  }

  @Override
  public boolean checkTaskAccess(Task task) {
    PmcUserDto user = UserContext.getUser();
    if (user.isAdmin()) {
      return true;
    }

    List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
    Set<String> taskGroupIdSet = new HashSet<>();
    for (IdentityLink idLink : identityLinksForTask) {
      if (idLink.getGroupId() != null) {
        taskGroupIdSet.add(idLink.getGroupId());
      }
    }
    log.info("AccessCheck for task #{} ({}) assignee: {}, candidateGroups: {}, user: {}, groups: {}", task.getId(), task.getName(), task.getAssignee(), taskGroupIdSet, user.getLoginName(), user.getGroups());

    if (user.getGuid().toString().equals(task.getAssignee())) {
      log.info("User ist Assignee");
      return true;
    }
    for (String group : user.getGroups()) {
      if (taskGroupIdSet.contains(group)) {
        log.info("Task ist Group: {} zugeordnet.", group);
        return true;
      }
    }
    log.info("AccessCheck for task #{} ({}) assignee: {}, candidateGroups: {}, user: {}, groups: {}. Access Denied", task.getId(), task.getName(), task.getAssignee(), taskGroupIdSet, user.getLoginName(), user.getGroups());
    return false;
  }

}
