package io.alanda.rest.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.ProcessDto;
import io.alanda.base.type.ProcessVariables;

import io.alanda.rest.ProcessInfoBaseRestService;


public class PmcProcessInfoRestServiceImpl implements ProcessInfoBaseRestService {
  
  private final Logger logger = LoggerFactory.getLogger(PmcProcessInfoRestServiceImpl.class);
  
  @Inject
  private RuntimeService runtimeService;
  
  @Inject
  private TaskService taskService;
  
  @Inject
  private RepositoryService repositoryService;

  @Override
  public List<ProcessDto> processData(String processInstanceId, final String processPackageKey) {
    logger.info(processInstanceId);
    logger.info(processPackageKey);

    List<ProcessDto> retVal = new ArrayList<>();
    List<ProcessInstance> piList = this.runtimeService
      .createProcessInstanceQuery()
      .variableValueEquals(ProcessVariables.PROCESS_PACKAGE_KEY, processPackageKey) //(VariableNames.processPackageKey.getValue(), processPackageKey)
      .list();
    //
    if (piList != null) {
      logger.info(piList.toString());
    } else {
      logger.info("query did not return processInstances for processPackageKey " + processPackageKey);
    }

    for (ProcessInstance pi : piList) {
      System.err.println("found process instances " + pi);
      ProcessDto dto = new ProcessDto();
      ProcessDefinition processDefinition = repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
      dto.setBusinessKey(pi.getBusinessKey());
      dto.setProcessDefinitionId(pi.getProcessDefinitionId());
      dto.setProcessInstanceId(pi.getProcessInstanceId());
      dto.setProcessName(processDefinition.getName());
      dto.setTasks(new ArrayList<>());
      List<Task> tasks = this.taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
      for (Task task : tasks) {
        dto.getTasks().add(entryFromTask(task));
      }
      retVal.add(dto);
    }
    Collections.sort(retVal, new Comparator<ProcessDto>() {

      @Override
      public int compare(ProcessDto o1, ProcessDto o2) {
        if (o1.getProcessInstanceId().equals(processPackageKey))
          return -1;
        if (o2.getProcessInstanceId().equals(processPackageKey))
          return 1;
        return 0;
      }
    });

    logger.info("retVal size " + retVal.size());
    return retVal;
  }

  @Override
  public ProcessDto processData(String processInstanceId) {
    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    if (pi == null) {
      throw new RestException("no process instance found for process instance id '" + processInstanceId + "'");
    }
    ProcessDto dto = new ProcessDto();
    ProcessDefinition processDefinition = repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
    dto.setBusinessKey(pi.getBusinessKey());
    dto.setProcessDefinitionId(pi.getProcessDefinitionId());
    dto.setProcessInstanceId(pi.getProcessInstanceId());
    dto.setProcessName(processDefinition.getName());
    return dto;
  }
  
  private PmcTaskDto entryFromTask(Task task) {
    SimpleDateFormat taskListDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    PmcTaskDto entry = new PmcTaskDto();
    entry.setDue(task.getDueDate() != null ? taskListDateFormat.format(task.getDueDate()) : "");
    entry.setAssignee(task.getAssignee());
    entry.setAssigneeId(task.getAssignee());
    entry.setTaskId(task.getId());
    entry.setTaskName(task.getName());
    entry.setCreated(taskListDateFormat.format(task.getCreateTime()));

    //entry.setFormKey(task.getFormKey());
    return entry;
  }

}
