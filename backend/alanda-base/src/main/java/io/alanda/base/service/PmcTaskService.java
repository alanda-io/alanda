package io.alanda.base.service;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.camunda.bpm.engine.cdi.BusinessProcessEvent;
import org.camunda.bpm.engine.task.Task;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcTaskDto;

public interface PmcTaskService {

  PmcTaskDto getTask(String taskId);

  Collection<PmcTaskDto> getTasksForProcessInstanceId(String pid);

  Collection<PmcTaskDto> getTasksForPmcProjectFromElastic(Long pmcProjectGuid);

  PagedResultDto<Object> getTasksElastic(Map<String, Object> filterOptions, Map<String, Object> sortOptions, int from, int size);

  void updateTask(String taskId);

  void updateTask(Task task);

  void updateAllTasks(int firstResult, int maxResults, boolean inverse);

  void taskEvent(BusinessProcessEvent businessProcessEvent);

  void activityEvent(BusinessProcessEvent businessProcessEvent);

  PmcTaskDto getTask(Task t);

  void updateDueDateOfTask(String taskId, Date dueDate) throws ParseException;

  boolean checkTaskAccess(String taskId);

  boolean checkTaskAccess(Task task);

  void updateFollowUpDateOfTask(String taskId, Date followUpDate) throws ParseException;

  int snoozeTask(String taskDefinitionKey, Long pmcProjectGuid, Date followUpDate);

  void updateFollowUpDateOfTask(Task task, Date followUpDate);

}
