package io.alanda.base.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.legacy.ProcessState;
import io.alanda.base.service.PmcFinderService;
import io.alanda.base.service.PmcUserService;

@Stateless
public class PmcFinderServiceImpl implements PmcFinderService {

  private static final Logger log = LoggerFactory.getLogger(PmcFinderServiceImpl.class);

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private HistoryService historyService;

  @Inject
  private PmcUserService pmcUserService;

  private final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";

  @Override
  public List<Map<String, Object>> getFinderGridSearchResult(String searchTerm, Boolean onlyActive) {
    searchTerm = "%" + searchTerm + "%";

    Set<ProcessDefinition> pDefs = new HashSet<>();
    Set<HistoricProcessInstance> pInstances = new HashSet<>();
    HistoricProcessInstanceQuery q;


    pDefs.addAll(repositoryService.createProcessDefinitionQuery().processDefinitionKeyLike(searchTerm).latestVersion().list());
    pDefs.addAll(repositoryService.createProcessDefinitionQuery().processDefinitionNameLike(searchTerm).latestVersion().list());
    for (ProcessDefinition pd : pDefs) {
      q = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(pd.getKey());
      if (onlyActive) {
        q = q.unfinished();
      }
      pInstances.addAll(q.list());
    }

    q = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKeyLike(searchTerm);
    if (onlyActive) {
      q = q.unfinished();
    }
    pInstances.addAll(q.list());

    List<Map<String, Object>> result = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    for (HistoricProcessInstance pi : pInstances) {

      String startDate = pi.getStartTime() != null ? dateFormat.format(pi.getStartTime()) : "";
      String endDate = pi.getEndTime() != null ? dateFormat.format(pi.getEndTime()) : "";

      Map<String, Object> instanceMap = new HashMap<String, Object>();
      instanceMap.put("id", pi.getId());
      instanceMap.put("state", getProcessState(pi).getState());
      instanceMap.put("process_name", pi.getProcessDefinitionName());
      instanceMap.put("process_type", "???");
      instanceMap.put("business_key", pi.getBusinessKey());
      instanceMap.put("start", startDate);
      instanceMap.put("due", "");
      instanceMap.put("end", endDate);
      instanceMap.put("additional_info", pi.getDeleteReason());
      result.add(instanceMap);
    }
    return result;
  }

  private ProcessState getProcessState(HistoricProcessInstance pi) {
    int incidentCount = runtimeService.createIncidentQuery().processInstanceId(pi.getId()).list().size();

    if (incidentCount > 0) {
      return ProcessState.ERROR;
    }

    switch (pi.getState()) {
      case HistoricProcessInstance.STATE_ACTIVE: return ProcessState.ACTIVE;
      case HistoricProcessInstance.STATE_SUSPENDED: return ProcessState.SUSPENDED;
      case HistoricProcessInstance.STATE_COMPLETED: return ProcessState.COMPLETED;
      case HistoricProcessInstance.STATE_EXTERNALLY_TERMINATED: return ProcessState.CANCELLED;
      case HistoricProcessInstance.STATE_INTERNALLY_TERMINATED: return ProcessState.CANCELLED;
      default: return ProcessState.UNKNOWN;
    }
  }


  private static final Set<String> defaultActivityTypes = new HashSet<>();
  {
    defaultActivityTypes.add(ActivityTypes.TASK_USER_TASK);
    defaultActivityTypes.add(ActivityTypes.CALL_ACTIVITY);
    defaultActivityTypes.add(ActivityTypes.TASK_SEND_TASK);
    defaultActivityTypes.add(ActivityTypes.START_EVENT);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_MESSAGE);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_NONE);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_TERMINATE);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_CANCEL);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_COMPENSATION);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_ERROR);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_ESCALATION);
    defaultActivityTypes.add(ActivityTypes.END_EVENT_SIGNAL);
  }

  @Override
  public List<Map<String, Object>> getPIOActivitiesResult(String pid, boolean extendedView) {

    List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery().processInstanceId(pid).list();

    if (extendedView) {
      activities = activities
        .stream()
        .filter(act -> !ActivityTypes.GATEWAY_EXCLUSIVE.equals(act.getActivityType()))
        .sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
        .collect(Collectors.toList());
    } else {
      activities = activities
        .stream()
        .filter(act -> defaultActivityTypes.contains(act.getActivityType()))
        .sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
        .collect(Collectors.toList());
    }

    List<Map<String, Object>> result = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    for (HistoricActivityInstance act : activities) {
      String startDate = act.getStartTime() != null ? dateFormat.format(act.getStartTime()) : "";
      String endDate = act.getEndTime() != null ? dateFormat.format(act.getEndTime()) : "";

      String dueDate = "", assignee = "";
      if (ActivityTypes.TASK_USER_TASK.equals(act.getActivityType())) {
        HistoricTaskInstance t = historyService.createHistoricTaskInstanceQuery().taskId(act.getTaskId()).singleResult();
        dueDate = t.getDueDate() != null ? dateFormat.format(t.getDueDate()) : "";
        if (t.getAssignee() != null) {
          PmcUserDto user = pmcUserService.getUserByUserId(Long.parseLong(t.getAssignee()));
          assignee = user.getFirstName() + " " + user.getSurname();
        }
      }

      HistoricProcessInstance processInstance = historyService
        .createHistoricProcessInstanceQuery()
        .processInstanceId(act.getProcessInstanceId())
        .singleResult();

      Map<String, Object> activitiesMap = new HashMap<String, Object>();
      activitiesMap.put("type", act.getActivityType());
      activitiesMap.put("name", act.getActivityName());
      activitiesMap.put("start", startDate);
      activitiesMap.put("due", dueDate);
      activitiesMap.put("end", endDate);
      activitiesMap.put("candidate_group", "???");
      activitiesMap.put("assignee", assignee);
      activitiesMap.put("call_proc_inst_id", act.getCalledProcessInstanceId());
      activitiesMap.put("business_key", processInstance.getBusinessKey());
      activitiesMap.put("process_name", processInstance.getProcessDefinitionName());
      result.add(activitiesMap);
    }
    return result;
  }
}
