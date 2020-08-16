/**
 * 
 */
package io.alanda.rest.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.AuthorizationException;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.runtime.ActivityInstanceDto;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.camunda.bpm.engine.rest.exception.RestException;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.ProcessDto;
import io.alanda.base.mail.IncomingMailService;
import io.alanda.base.mail.MailServerConfiguration;
import io.alanda.base.service.ConfigService;
import io.alanda.base.type.ProcessVariables;

import io.alanda.rest.PmcProcessRestService;
import io.alanda.rest.util.HistoricProcessInstanceDto;

/**
 * @author jlo
 */
public class PmcProcessRestServiceImpl implements PmcProcessRestService {

  private static final Logger log = LoggerFactory.getLogger(PmcProcessRestServiceImpl.class);

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private TaskService taskService;

  @Inject
  private HistoryService historyService;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private ConfigService configService;

  @Inject
  private IncomingMailService incomingMailService;

  /**
   * 
   */
  public PmcProcessRestServiceImpl() {
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.PmcProcessRestService#processesForPackage(java.lang.String)
   */
  @Override
  public List<ProcessDto> processesForPackage(final String processPackageKey) {
    log.info("ProcessPackageKey: {}", processPackageKey);

    List<ProcessDto> retVal = new ArrayList<ProcessDto>();
    List<ProcessInstance> piList = this.runtimeService
      .createProcessInstanceQuery()
      .variableValueEquals(ProcessVariables.PROCESS_PACKAGE_KEY, processPackageKey)
      .list();
    //
    if (piList != null) {
      log.info(piList.toString());
    } else {
      log.info("query did not return processInstances for processPackageKey {}", processPackageKey);
    }

    for (ProcessInstance pi : piList) {
      log.info("found process instance {}", pi);
      ProcessDto dto = new ProcessDto();
      ProcessDefinition processDefinition = repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
      dto.setBusinessKey(pi.getBusinessKey());
      dto.setProcessDefinitionId(pi.getProcessDefinitionId());
      dto.setProcessInstanceId(pi.getProcessInstanceId());
      dto.setProcessName(processDefinition.getName());
      dto.setTasks(new ArrayList<PmcTaskDto>());
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

    return retVal;
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

    return entry;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.PmcProcessRestService#processesForPackage(java.lang.Long)
   */
  @Override
  public List<ProcessDto> processesForPackage(Long pmcProjectGuid) {
    log.info("PmcProjectGuid: {}", pmcProjectGuid);

    List<ProcessDto> retVal = new ArrayList<ProcessDto>();
    List<ProcessInstance> piList = this.runtimeService
      .createProcessInstanceQuery()
      .variableValueEquals(ProcessVariables.PMC_PROJECT_GUID, pmcProjectGuid)
      .list();
    //
    if (piList != null) {
      log.info(piList.toString());
    } else {
      log.info("query did not return processInstances for pmcProjectGuid {}", pmcProjectGuid);
    }

    for (ProcessInstance pi : piList) {
      log.info("found process instance {}", pi);
      ProcessDto dto = new ProcessDto();
      ProcessDefinition processDefinition = repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
      dto.setBusinessKey(pi.getBusinessKey());
      dto.setProcessDefinitionId(pi.getProcessDefinitionId());
      dto.setProcessInstanceId(pi.getProcessInstanceId());
      dto.setProcessName(processDefinition.getName());
      dto.setTasks(new ArrayList<PmcTaskDto>());
      List<Task> tasks = this.taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
      for (Task task : tasks) {
        dto.getTasks().add(entryFromTask(task));
      }
      retVal.add(dto);
    }
    //    Collections.sort(retVal, new Comparator<ProcessDto>() {
    //
    //      @Override
    //      public int compare(ProcessDto o1, ProcessDto o2) {
    //        if (o1.getProcessInstanceId().equals(processPackageKey))
    //          return -1;
    //        if (o2.getProcessInstanceId().equals(processPackageKey))
    //          return 1;
    //        return 0;
    //      }
    //    });

    return retVal;
  }

  @Override
  public VariableValueDto getVariable(String processInstanceId, String variableName, boolean deserializeValue) {
    TypedValue tv = this.runtimeService.getVariableTyped(processInstanceId, variableName);
    VariableValueDto dto = new VariableValueDto();
    dto.setValue(tv.getValue());
    String sType = tv.getType().getName();
    sType = sType.substring(0, 1).toUpperCase() + sType.substring(1);
    dto.setType(sType);
    return dto;
  }

  @Override
  public ProcessDto processForProjectProcess(Long pmcProjectProcessId) {
    log.info("getting processInfo for PmcProjectProcess {}", pmcProjectProcessId);

    List<ProcessInstance> piList = this.runtimeService
      .createProcessInstanceQuery()
      .variableValueEquals(ProcessVariables.PMC_PROJECT_PROCESS_GUID, pmcProjectProcessId)
      .list();

    if (piList == null || piList.size() == 0) {
      log.warn("no process found");
      return null;
    }

    if (piList.size() > 1) {
      log.warn("multiple processes found");
    }

    for (ProcessInstance pi : piList) {
      log.info("found: {}", pi);
    }

    ProcessInstance pi = piList.get(0);

    ProcessDto foundProcess = new ProcessDto();
    ProcessDefinition processDefinition = repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
    foundProcess.setBusinessKey(pi.getBusinessKey());
    foundProcess.setProcessDefinitionId(pi.getProcessDefinitionId());
    foundProcess.setProcessInstanceId(pi.getProcessInstanceId());
    foundProcess.setProcessName(processDefinition.getName());
    foundProcess.setTasks(new ArrayList<PmcTaskDto>());
    List<Task> tasks = this.taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
    for (Task task : tasks) {
      foundProcess.getTasks().add(entryFromTask(task));
    }

    return foundProcess;
  }

  @Override
  public HistoricProcessInstanceDto processStatus(String processInstanceId) {
    HistoricProcessInstanceDto retVal = null;
    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    if (pi != null) {
      retVal = new HistoricProcessInstanceDto() {
      };
      retVal.setProcessDefinitionId(pi.getProcessDefinitionId());
      retVal.setBusinessKey(pi.getBusinessKey());

    } else {
      HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
      retVal = HistoricProcessInstanceDto.fromHistoricProcessInstance(hpi);
    }
    return retVal;
  }

  @Override
  public ActivityInstanceDto activities(String processInstanceId) {
    ActivityInstance activityInstance = null;
    try {
      activityInstance = runtimeService.getActivityInstance(processInstanceId);
    } catch (AuthorizationException e) {
      throw e;
    } catch (ProcessEngineException e) {
      throw new InvalidRequestException(Status.INTERNAL_SERVER_ERROR, e, e.getMessage());
    }
    if (activityInstance == null) {
      throw new InvalidRequestException(Status.NOT_FOUND, "Process instance with id " + processInstanceId + " does not exist");
    }
    ActivityInstanceDto result = ActivityInstanceDto.fromActivityInstance(activityInstance);
    return result;
  }

  @Override
  public ProcessDefinitionDiagramDto definitionXml(String processInstanceId) {
    HistoricProcessInstanceDto hpi = processStatus(processInstanceId);

    InputStream processModelIn = null;
    try {
      processModelIn = repositoryService.getProcessModel(hpi.getProcessDefinitionId());
      byte[] processModel = IoUtil.readInputStream(processModelIn, "processModelBpmn20Xml");
      return ProcessDefinitionDiagramDto.create(hpi.getProcessDefinitionId(), new String(processModel, "UTF-8"));
    } catch (AuthorizationException e) {
      throw e;
    } catch (ProcessEngineException e) {
      throw new InvalidRequestException(Status.BAD_REQUEST, e, "No matching definition with id " + hpi.getProcessDefinitionId());
    } catch (UnsupportedEncodingException e) {
      throw new RestException(Status.INTERNAL_SERVER_ERROR, e);
    } finally {
      IoUtil.closeSilently(processModelIn);
    }
  }

  @Override
  public Response checkMail() {
    MailServerConfiguration configuration = new MailServerConfiguration(
      configService.getProperty("mail.incoming.host"),
      Integer.parseInt(configService.getProperty("mail.incoming.port")),
      configService.getProperty("mail.incoming.username"),
      configService.getProperty("mail.incoming.password"),
      true);
    this.incomingMailService.processMail(configuration);
    return Response.accepted().build();
  }

  @Override
  public List<ProcessDto> queryProcess(String query) {
    // TODO query existing camunda processes for string
    // return all for empty query?

    return null;
  }

  @Override
  public List<ActivityInstanceDto> queryProcessActivities(String query) {
    // TODO query existing camunda activities of a process for string
    // return all for empty query?

    return null;
  }
}
