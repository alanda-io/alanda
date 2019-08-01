package io.alanda.rest.impl;

import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcTaskService;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.util.UserContext;

import io.alanda.rest.PmcTaskRestService;

public class PmcTaskRestServiceImpl implements PmcTaskRestService {

  private final Logger logger = LoggerFactory.getLogger(PmcTaskRestServiceImpl.class);

  @Inject
  private TaskService taskService;

  @Inject
  private PmcTaskService pmcTaskService;

  @Inject
  private PmcUserService pmcUserService;

  @Inject
  private ElasticService elasticService;

  @Override
  public PmcTaskDto getTask(String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    return pmcTaskService.getTask(taskId);
  }

  @Override
  public void completeTask(String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    PmcUserDto user = UserContext.getUser();
    String userId = user.getGuid().toString();
    logger.info("Completing task: " + taskId + " for User: " + user.getLoginName());
    taskService.setAssignee(taskId, userId);
    taskService.complete(taskId);
    elasticService.refreshTaskIndex();
    logger.info("Completed task: " + taskId + " for User: " + user.getLoginName());
  }

  @Override
  public PagedResultDto getTasks(Map<String, Object> serverOptions) {
    Integer pageNumber = (Integer) serverOptions.get("pageNumber");
    if (pageNumber == null)
      pageNumber = 1;
    Integer pageSize = (Integer) serverOptions.get("pageSize");
    if (pageSize == null)
      pageSize = 15;
    Map<String, Object> filterOptions = (Map<String, Object>) serverOptions.get("filterOptions");
    Map<String, Object> sortOptions = (Map<String, Object>) serverOptions.get("sortOptions");
    return pmcTaskService.getTasksElastic(filterOptions, sortOptions, (pageNumber - 1) * pageSize, pageSize);
  }

  @Override
  public void updateAllTasks(Integer firstResult, Integer maxResults, boolean inverse) {
    if (firstResult == null) {
      firstResult = 0;
      maxResults = 10000;
    } else {
      if (maxResults == null)
        maxResults = 2000;
    }
    pmcTaskService.updateAllTasks(firstResult, maxResults, inverse);
  }

  private boolean checkTaskAccess(Task task) {
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
    logger.info(
      "AccessCheck for task #" +
        task.getId() +
        " (" +
        task.getName() +
        ") assignee: " +
        task.getAssignee() +
        ", candidateGroups: " +
        taskGroupIdSet +
        ", user: " +
        user.getLoginName() +
        ", groups: " +
        user.getGroups());

    if (user.getGuid().toString().equals(task.getAssignee())) {
      logger.info("User ist Assignee");
      return true;
    }
    for (String group : user.getGroups()) {
      if (taskGroupIdSet.contains(group)) {
        logger.info("Task ist Group: " + group + " zugeordnet.");
        return true;
      }
    }
    logger.info(
      "AccessCheck for task #" +
        task.getId() +
        " (" +
        task.getName() +
        ") assignee: " +
        task.getAssignee() +
        ", candidateGroups: " +
        taskGroupIdSet +
        ", user: " +
        user.getLoginName() +
        ", groups: " +
        user.getGroups() +
        ". Access Denied");
    return false;
  }

  @Override
  public void unclaim(String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    taskService.setAssignee(taskId, null);

  }

  @Override
  public void assign(String taskId, PmcUserDto assignee) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    taskService.setAssignee(taskId, assignee.getGuid().toString());

  }

  @Override
  public List<PmcUserDto> candidates(String taskId) {
    final Collator col = Collator.getInstance(new Locale("de", "DE"));
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
    Set<Long> taskGroupIdSet = new HashSet<>();
    for (IdentityLink idLink : identityLinksForTask) {
      if (StringUtils.isNumeric(idLink.getGroupId())) {
        taskGroupIdSet.add(Long.valueOf(idLink.getGroupId()));
      }
    }
    Map<Long, PmcUserDto> users = new HashMap<>();
    for (Long groupId : taskGroupIdSet) {
      List<PmcUserDto> usersForGroup = pmcUserService.getUserByGroupId(groupId);
      for (PmcUserDto dto : usersForGroup) {
        users.put(dto.getGuid(), dto);
      }
    }
    List<PmcUserDto> retVal = new ArrayList<>(users.values());
    Collections.sort(retVal, new Comparator<PmcUserDto>() {

      @Override
      public int compare(PmcUserDto o1, PmcUserDto o2) {
        return col.compare(o1.getSurname(), o2.getSurname());
      }
    });
    return retVal;
  }

  @Override
  public List<PmcTaskDto> searchTasks(String processInstanceId, String taskDefinitionKey) {
    TaskQuery tq = taskService.createTaskQuery().processInstanceId(processInstanceId).initializeFormKeys();
    if ( !StringUtils.isBlank(taskDefinitionKey)) {
      if (taskDefinitionKey.contains(",")) {
        String[] split = taskDefinitionKey.split(",");
        tq.taskDefinitionKeyIn(split);
      } else {
        tq.taskDefinitionKey(taskDefinitionKey);
      }
    }
    List<Task> lTasks = tq.list();

    List<PmcTaskDto> retVal = new ArrayList<>();
    for (Task t : lTasks) {
      if ( !checkTaskAccess(t)) {
        continue;
      }
      retVal.add(pmcTaskService.getTask(t));
    }
    return retVal;
  }

  @Override
  public VariableValueDto getVariable(String taskId, String variableName, boolean deserializeValue) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    TypedValue tv = this.taskService.getVariableTyped(taskId, variableName);

    VariableValueDto dto = new VariableValueDto();
    if (tv != null) {
      dto.setValue(tv.getValue());
      String sType = tv.getType().getName();
      sType = sType.substring(0, 1).toUpperCase() + sType.substring(1);
      dto.setType(sType);
    }
    return dto;
  }

  @Override
  public void putVariable(String taskId, String variableName, VariableValueDto variable) {
    if (variable != null) {
      logger.info(
        "task " + taskId + ": setting variable \"" + variableName + "\" to \"" + variable.getValue() + " (" + variable.getType() + ")\"");
    }
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if ( !checkTaskAccess(task)) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    if (variable.getType() == null)
      variable.setType("String");
    if (variable.getType().equalsIgnoreCase("Boolean")) {
      this.taskService
        .setVariable(taskId, variableName, variable.getValue() != null ? Boolean.valueOf(variable.getValue().toString()) : null);
    } else if (variable.getType().equalsIgnoreCase("Long")) {
      this.taskService.setVariable(taskId, variableName, variable.getValue() != null ? Long.valueOf(variable.getValue().toString()) : null);
    } else if (variable.getType().equalsIgnoreCase("Integer")) {
      this.taskService
        .setVariable(taskId, variableName, variable.getValue() != null ? Integer.valueOf(variable.getValue().toString()) : null);
    } else {
      this.taskService.setVariable(taskId, variableName, variable.getValue());
    }

  }

  @Override
  public Response snoozeTask(String taskId, int days) {

    logger.info("Snoozing task (id=" + taskId + ") for " + days + " days");
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, days);
    Date followUpDate = calendar.getTime();
    logger.info("followUpdate " + followUpDate);

    try {
      pmcTaskService.updateFollowUpDateOfTask(taskId, followUpDate);
      logger.info("Set followUpDate of Task " + taskId + "to " + followUpDate);
    } catch (ParseException e) {
      logger.warn("Error setting followUpDate of task: " + taskId + " to :" + followUpDate + ", reason: " + e.getMessage(), e);
    }
    return Response.ok().build();
  }

  @Override
  public Response updateDueDateOfTask(String taskId, String sDueDate) {
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date dueDate = null;
    try {
      dueDate = formatter.parse(sDueDate);
    } catch (ParseException e) {
      return Response.serverError().build();
    }
    try {
      pmcTaskService.updateDueDateOfTask(taskId, dueDate);
    } catch (ParseException e) {
      logger.warn("Error setting dueDate of task: " + taskId + " to :" + dueDate + ", reason: " + e.getMessage(), e);
    }
    return Response.ok().build();
  }

}
