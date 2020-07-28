package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.value.LongValue;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dao.PmcProjectPhaseDao;
import io.alanda.base.dao.PmcProjectProcessDao;
import io.alanda.base.dto.PmcHistoryLogDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.ProcessDefinitionDto;
import io.alanda.base.entity.PmcHistoryLog.Action;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectPhase;
import io.alanda.base.entity.PmcProjectProcess;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcHistoryService;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcTaskService;
import io.alanda.base.service.TemplateService;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.type.PmcProjectTypeConfiguration;
import io.alanda.base.type.ProcessRelation;
import io.alanda.base.type.ProcessResultStatus;
import io.alanda.base.type.ProcessState;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.TimerUtil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Stateless
@Named("pmcProcessService")
public class PmcProcessServiceImpl implements PmcProcessService {

  private static final Logger log = LoggerFactory.getLogger(PmcProcessServiceImpl.class);

  private final String ES_QUERY_PROCESS_TEMPLATE = "es_query_process.template";

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcProjectProcessDao pmcProjectProcessDao;

  @Inject
  private PmcProjectDao pmcProjectDao;

  @Inject
  private PmcProjectPhaseDao pmcProjectPhaseDao;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private PmcProjectService pmcProjectService;

  @Inject
  private PmcTaskService pmcTaskService;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  ElasticService elasticService;

  @Inject
  public TemplateService templateService;

  @Inject
  private PmcHistoryService pmcHistoryService;

  private LoadingCache<String, ProcessDefinitionDto> processDefinitionCache;

  public PmcProcessServiceImpl() {

    processDefinitionCache = CacheBuilder
      .newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(24, TimeUnit.HOURS)
      .build(new CacheLoader<String, ProcessDefinitionDto>() {

        public ProcessDefinitionDto load(String processKey) {
          ProcessDefinition pd = repositoryService
            .createProcessDefinitionQuery()
            .processDefinitionKey(processKey)
            .latestVersion()
            .singleResult();
          if (pd == null) {
            log.warn("no procDef found for key: {}", processKey);
            return new ProcessDefinitionDto(processKey, processKey);
          }
          return new ProcessDefinitionDto(pd.getName(), pd.getKey());
        }
      });
  }

  @Override
  public PmcProjectProcessDto saveProjectProcess(Long projectGuid, PmcProjectProcessDto processDto) {
    PmcProjectProcess process;

    if (processDto.getGuid() == null) {
      process = dozerMapper.map(processDto, PmcProjectProcess.class);
      process.setPmcProject(pmcProjectDao.getById(projectGuid));
      if (process.getProcessInstanceId() == null) {
        process.setProcessInstanceId("");
      }
      process = pmcProjectProcessDao.create(process);
    } else {
      process = getByGuid(processDto.getGuid());
      if ( !process.getVersion().equals(processDto.getVersion())) {
        throwWebAppException(
          "PmcProjectProcess (guid=" +
            process.getGuid() +
            ") versions are not equal (" +
            process.getVersion() +
            " != " +
            processDto.getVersion() +
            ")",
          "Can not update process due to a version conflict. Please reload page!");
      }
      if (process.getStatus() != null && !process.getStatus().equals(ProcessState.NEW)) {
        throwWebAppException(
          "not allowed to update PmcProjectProcess (guid=" + process.getGuid() + ") in state " + process.getStatus(),
          "Not allowed to update process in state " + process.getStatus());
      }

      process.setBusinessObject(processDto.getBusinessObject());
      process.setProcessInstanceId(processDto.getProcessInstanceId());
      process.setRelation(ProcessRelation.valueOf(processDto.getRelation()));
      process.setStatus(ProcessState.valueOf(processDto.getStatus()));
      process.setWorkDetails(processDto.getWorkDetails());

    }
    process.setProcessKey(processDto.getProcessKey());
    if (processDto.getProcessKey().indexOf(":") > 0) {
      String[] splitKey = processDto.getProcessKey().split(":");
      process.setProcessKey(splitKey[1]);
      process.setPhase(splitKey[0]);
    }
    pmcProjectProcessDao.getEntityManager().flush();
    return dozerMapper.map(process, PmcProjectProcessDto.class);
  }

  @Override
  public PmcProjectProcessDto startProjectProcess(Long processGuid) {
    return startProjectProcess(processGuid, null);
  }

  @Override
  public PmcProjectProcessDto startProjectProcess(Long processGuid, Map<String, Object> processVars) {

    log.info("starting project process with guid {}", processGuid);

    PmcProjectProcess process = getByGuid(processGuid);
    PmcProjectProcess mainProcess = pmcProjectProcessDao.getMainProcessByProject(process.getPmcProject().getGuid());

    if (process.getStatus() != null && !process.getStatus().equals(ProcessState.NEW)) {
      throwWebAppException(
        "not allowed to start PmcProjectProcess (guid=" + process.getGuid() + ") in state " + process.getStatus(),
        "Not allowed to start process in state " + process.getStatus());
    }

    // load config from PmcProjectType
    PmcProjectTypeConfiguration configuration = new PmcProjectTypeConfiguration(process.getPmcProject().getPmcProjectType());

    // set pmcProjectTag
    if (configuration.existsSubProcessTagMapping()) {
      Map<String, String> tagMapping = configuration.getSubProcessTagMapping();
      if (tagMapping.containsKey(process.getProcessKey())) {
        String tag = tagMapping.get(process.getProcessKey());
        List<String> tags;
        if (process.getPmcProject().getTag() != null) {
          tags = new ArrayList<>(Arrays.asList(process.getPmcProject().getTag()));
        } else {
          tags = new ArrayList<>();
        }
        Boolean foundTag = false;
        for (String t : tags) {
          if (t.equals(tag))
            foundTag = true;
        }
        if ( !foundTag) {
          tags.add(tag);
          process.getPmcProject().setTag(tags.toArray(new String[tags.size()]));
        }
      }
    }

    // start sub process by sending message
    String messageName = configuration.getStartSubProcessMessageName();

    String variableName = configuration.getStartSubprocessVariable();

    String executionIdToCall = process.getParentExecutionId();
    List<EventSubscription> subs = this.runtimeService
      .createEventSubscriptionQuery()
      .processInstanceId(mainProcess.getProcessInstanceId())
      .eventName(messageName)
      .list();
    if (subs.size() != 1) {
      throw new IllegalArgumentException(
        "ProcessInstance #" + mainProcess.getProcessInstanceId() + " hat " + subs.size() + " Subscriptions für Message " + messageName);
    } else if (subs.size() == 1) {
      executionIdToCall = subs.get(0).getExecutionId();
    }

    if (executionIdToCall == null) {
      throw new IllegalStateException("no execution id found to call subprocess");
    }

    Map<String, Object> vars = new HashMap<>();
    vars.put(variableName, process.getProcessKey());
    vars.put(ProcessVariables.PMC_PROJECT_PROCESS_GUID, process.getGuid());
    vars.put(ProcessVariables.PMC_PROJECT_GUID, process.getPmcProject().getGuid());
    vars.put("commentSub", process.getWorkDetails());
    if (processVars != null) {
      vars.putAll(processVars);
    }

    pmcProjectProcessDao.getEntityManager().flush();
    pmcProjectProcessDao.getEntityManager().detach(process);

    this.runtimeService.messageEventReceived(messageName, executionIdToCall, vars);
    process = pmcProjectProcessDao.getById(process.getGuid());
    pmcProjectProcessDao.getEntityManager().flush();
    pmcProjectProcessDao.getEntityManager().detach(process);
    log.info(" now reload Process: {}", process.getGuid());
    process = getByGuid(process.getGuid());
    if ( !validProcessInstanceId(process.getProcessInstanceId())) {

      List<ProcessInstance> piList = this.runtimeService
        .createProcessInstanceQuery()
        .variableValueEquals(ProcessVariables.PMC_PROJECT_PROCESS_GUID, process.getGuid())
        .processDefinitionKey(process.getProcessKey())
        .list();

      if (piList == null || piList.size() == 0 || piList.size() > 1) {
        throw new IllegalStateException(
          "wrong number (" + piList.size() + ")of processes with projectProcessGuid " + process.getGuid() + " found");
      }

      ProcessInstance pi = piList.get(0);
      log.info("found process instance id: {}", pi.getProcessInstanceId());
      process.setProcessInstanceId(pi.getProcessInstanceId());
      process.setLabel(repositoryService.getProcessDefinition(pi.getProcessDefinitionId()).getName());
    }

    process.setStatus(ProcessState.ACTIVE);

    pmcProjectProcessDao.getEntityManager().flush();
    elasticService.updateEntry(pmcProjectService.getProjectByGuid(process.getPmcProject().getGuid(), PmcProjectService.Mode.RELATIONIDS));
    return dozerMapper.map(process, PmcProjectProcessDto.class);
  }

  private boolean validProcessInstanceId(String processInstanceId) {
    if (StringUtils.isBlank(processInstanceId))
      return false;
    if (processInstanceId.equals("-"))
      return false;
    // Fix for Batch Import
    //    return (runtimeService.createProcessInstanceQuery()
    //        .processInstanceId(processInstanceId).singleResult() != null);
    return true;
  }

  @Override
  public void setProcessData(Long pmcProjectProcessGuid, String processInstanceId, String label) {
      log
              .info("setProcessData: pmcProjectProcessGuid: {}, processInstanceId: {}, label: {}", pmcProjectProcessGuid, processInstanceId, label);
    PmcProjectProcess pp = this.pmcProjectProcessDao.getById(pmcProjectProcessGuid);
    if ( !validProcessInstanceId(pp.getProcessInstanceId())) {
      pp.setProcessInstanceId(processInstanceId);
      pp.setLabel(label);
    }
    pmcProjectProcessDao.getEntityManager().flush();
  }

  @Override
  public PmcProjectProcessDto cancelProjectProcess(Long processGuid) {
    return cancelProjectProcess(processGuid, null, null, false, null);
  }

  @Override
  public PmcProjectProcessDto cancelProjectProcess(
      Long processGuid,
      String resultStatus,
      String resultComment,
      boolean canceledByProject,
      String reason) {

    PmcProjectProcess process = getByGuid(processGuid);
    if ( !(process.getStatus().equals(ProcessState.ACTIVE) || process.getStatus().equals(ProcessState.SUSPENDED))) {
      log.warn("Process {} can not be stopped because it is not ACTIVE OR SUSPENDED!", process.getGuid());
      return dozerMapper.map(process, PmcProjectProcessDto.class);
    }

    for (PmcProjectListener l : pmcProjectService.getListener(process.getPmcProject())) {
      l.beforeProcessCancellation(process, canceledByProject, reason);
    }

    PmcProjectProcess mainProcess = pmcProjectProcessDao.getMainProcessByProject(process.getPmcProject().getGuid());

    if (processGuid.equals(mainProcess.getGuid())) {

      runtimeService.deleteProcessInstance(process.getProcessInstanceId(), "user requested deletion");

    } else {

      List<Execution> exList = runtimeService.createExecutionQuery().processInstanceId(mainProcess.getProcessInstanceId()).list();
      boolean foundParentActivity = false;

      for (Execution ex : exList) {
        Long pmcProjectProcessGuid = null;
        try {
          pmcProjectProcessGuid = (Long) runtimeService.getVariableLocal(ex.getId(), ProcessVariables.PMC_PROJECT_PROCESS_GUID);
        } catch (Exception e) {
          log.info("Execution {} macht Problem: {}", ex.getId(), e.getMessage(), e);
        }
        if (pmcProjectProcessGuid != null && pmcProjectProcessGuid.equals(process.getGuid())) {
          foundParentActivity = true;
          ActivityInstance ai = findActivityForExecution(
            runtimeService.getActivityInstance(mainProcess.getProcessInstanceId()),
            ex.getId());
          if (ai == null) {
            throw new IllegalStateException("no activity found for execution " + ex.getId());
          }
          log.info("Found activity to kill: {}-{}", ai.getActivityId(), ai.getActivityName());
          runtimeService.createProcessInstanceModification(mainProcess.getProcessInstanceId()).cancelActivityInstance(ai.getId()).execute();
          break;
        }
      }

      if ( !foundParentActivity) {
        // process has not been started via main process and will be canceled directly
        runtimeService.deleteProcessInstance(process.getProcessInstanceId(), "user requested deletion");
      }
    }

    process.setStatus(ProcessState.CANCELED);
    process.setResultComment(reason);

    if (resultStatus != null)
      process.setResultStatus(ProcessResultStatus.valueOf(resultStatus));
    if (resultComment != null)
      process.setResultComment(resultComment);

    pmcProjectProcessDao.update(process);
    pmcProjectProcessDao.getEntityManager().flush();

    createHistory(process, reason, Action.CANCEL);

    return dozerMapper.map(process, PmcProjectProcessDto.class);
  }

  @Override
  public Collection<PmcProjectProcessDto> getAllProjectProcesses(Long projectGuid) {
    return dozerMapper.mapCollection(pmcProjectProcessDao.getAllChildProcesses(projectGuid), PmcProjectProcessDto.class);
  }

  @Override
  public PmcProjectProcessDto getMainProjectProcess(Long projectGuid) {
    return dozerMapper.map(pmcProjectProcessDao.getMainProcessByProject(projectGuid), PmcProjectProcessDto.class);
  }

  @Override
  public void removeProjectProcess(Long processGuid, String reason) {
    PmcProjectProcess process = getByGuid(processGuid);
    if (process.getStatus().equals(ProcessState.ACTIVE)) {
      cancelProjectProcess(processGuid);
    } else if (process.getStatus().equals(ProcessState.NEW)) {
      process.setStatus(ProcessState.DELETED);
      pmcProjectProcessDao.update(process);
      createHistory(process, reason, Action.DELETE);
    } else {
      throwWebAppException(
        "not allowed to cancel/delete PmcProjectProcess (guid=" + process.getGuid() + ") in state " + process.getStatus(),
        "Not allowed to cancel/delete process in state " + process.getStatus());
    }
  }

  private void createHistory(PmcProjectProcess process, String reason, Action theAction) {
    pmcHistoryService.createHistory(PmcHistoryLogDto.createForProcess(process).withChange(theAction, reason));
  }

  private ActivityInstance findActivityForExecution(ActivityInstance activityInstance, String executionid) {
    if (activityInstance.getExecutionIds() != null) {
      for (String eid : activityInstance.getExecutionIds()) {
        if (eid.equals(executionid))
          return activityInstance;
      }
    }
    if (activityInstance.getChildActivityInstances() != null) {
      for (ActivityInstance child : activityInstance.getChildActivityInstances()) {
        ActivityInstance found = findActivityForExecution(child, executionid);
        if (found != null)
          return found;
      }
    }
    return null;
  }

  @Override
  public Map<String, Object> getProcessesAndTasksForProject(Long projectGuid, boolean useLegacy) {

    final String ALLOWED_PROCESSES_DEFAULT_PHASE = "default";

    Map<String, Object> result = new HashMap<>();

    TimerUtil tu = TimerUtil.createStarted("load");
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);

    Map<String, String> phaseNameMap = new HashMap<>();

    // loading allowed sub processes
    Map<String, Collection<ProcessDefinitionDto>> allowedProcessesList = new HashMap<>();
    result.put(ALLOWED_PROCESSES, allowedProcessesList);

    allowedProcessesList.put(ALLOWED_PROCESSES_DEFAULT_PHASE, project.getPmcProjectType().getProcessDefinitions());

    for (PmcProjectPhase phase : pmcProjectPhaseDao.getForProject(projectGuid)) {
      if (phase.getEnabled() == null || phase.getEnabled().equals(false))
        continue;
      String phaseName = phase.getPmcProjectPhaseDefinition().getIdName();
      phaseNameMap.put(phaseName, phase.getPmcProjectPhaseDefinition().getDisplayName());
      Collection<ProcessDefinitionDto> allowedProcesses = new ArrayList<ProcessDefinitionDto>();
      for (String processDefinition : phase.getPmcProjectPhaseDefinition().getAllowedProcesses()) {
        ProcessDefinitionDto pd = getProcessDefinition(processDefinition);
        if (pd == null)
          continue;
        allowedProcesses.add(pd);
      }
      allowedProcessesList.put(phaseName, allowedProcesses);
    }
    result.put(PHASE_NAME_MAP, phaseNameMap);

    // loading active processes and tasks
    List<PmcProjectProcessDto> activeProcesses = new ArrayList<>();
    result.put(ACTIVE_PROCESSES, activeProcesses);

    if (project.getProcesses() == null)
      return result;

    List<PmcProjectProcessDto> projectProcesses = new ArrayList<>(project.getProcesses());
    Collections.sort(projectProcesses, new Comparator<PmcProjectProcessDto>() {

      public int compare(PmcProjectProcessDto o1, PmcProjectProcessDto o2) {
        Long diff = o1.getGuid() - o2.getGuid();
        return diff.intValue();
      }
    });
    tu.stop();
    tu.start("sub");
    for (PmcProjectProcessDto proc : projectProcesses) {

      int insertIndex = 0;
      if (proc.getRelation().equals(ProcessRelation.MAIN.toString())) {
        insertIndex = 0;
      } else {
        insertIndex = activeProcesses.size();
      }

      if (useLegacy) {
        activeProcesses.addAll(insertIndex, getSubProcesses(proc.getProcessInstanceId(), proc.getGuid()));
      }
      activeProcesses.add(insertIndex, proc);

    }

    if ( !useLegacy) {
      addProcessInfoFromElastic(projectGuid, activeProcesses);
    }
    tu.stop();
    tu.start("tasks");

    Map<String, Collection<PmcTaskDto>> taskToPidMap = new HashMap<>();
    if ( !useLegacy) {
      Collection<PmcTaskDto> allTasks = pmcTaskService.getTasksForPmcProjectFromElastic(projectGuid);
      for (PmcTaskDto t : allTasks) {
        if (taskToPidMap.get(t.getProcessInstanceId()) == null) {
          taskToPidMap.put(t.getProcessInstanceId(), new ArrayList<PmcTaskDto>());
        }
        taskToPidMap.get(t.getProcessInstanceId()).add(t);
      }
    }

    PmcProjectTypeConfiguration ptConfig = new PmcProjectTypeConfiguration(project.getPmcProjectType());
    addCustomRefObjectConfig(ptConfig.getProcessCustomRefObject(), activeProcesses);

    for (PmcProjectProcessDto process : activeProcesses) {
      if (validProcessInstanceId(process.getProcessInstanceId())) {
        tu.switchTo("loadTask");
        if (useLegacy)
          process.setTasks(pmcTaskService.getTasksForProcessInstanceId(process.getProcessInstanceId()));
        else
          process.setTasks(taskToPidMap.get(process.getProcessInstanceId()));
        tu.switchTo("tasks");
        if (process.getLabel() == null || process.getLabel().equals("")) {
          tu.switchTo("procDef");
          determineProcessLabel(process);
          tu.switchTo("tasks");
        }
      }
    }
    tu.stop();
    log.info("Duration: {}", tu);
    return result;
  }

  private void addCustomRefObjectConfig(Map<String, Boolean> customRefObjects, Collection<PmcProjectProcessDto> processes) {
    if (customRefObjects != null) {
      for (PmcProjectProcessDto proc : processes) {
        Boolean b = null;
        if (proc.getProcessKey() != null) {
          b = customRefObjects.get(proc.getProcessKey());
        }
        if (b != null) {
          proc.setCustomRefObject(b);
        } else {
          proc.setCustomRefObject(true);
        }
      }
    } else {
      for (PmcProjectProcessDto proc : processes) {
        proc.setCustomRefObject(true);
      }
    }
  }

  private void determineProcessLabel(PmcProjectProcessDto process) {
    String processDefinitionKey = process.getProcessKey();
    if ( !StringUtils.isEmpty(processDefinitionKey)) {
      process.setLabel(getProcessDefinition(processDefinitionKey).getProcessName());
      return;
    } else {
      log.info("process: {} misses processDefinitionKey.", process);
      //      process.setLabel("-");
      //      return;
    }
    String processDefinitionId = process.getProcessDefinitionId();
    if (processDefinitionId == null) {
      List<ProcessInstance> piList = this.runtimeService
        .createProcessInstanceQuery()
        .processInstanceId(process.getProcessInstanceId())
        .list();
      if (piList.size() == 1) {
        processDefinitionId = piList.get(0).getProcessDefinitionId();
      }
    }
    if (processDefinitionId != null)
      process.setLabel(repositoryService.getProcessDefinition(processDefinitionId).getName());
    else
      log.warn("can not determine label for process {}", process.getProcessInstanceId());
  }

  private void addProcessInfoFromElastic(Long pmcProjectGuid, List<PmcProjectProcessDto> activeProcesses) {

    Map<String, Object> variables = new HashMap<>();
    PmcProjectTypeConfiguration ptConfig = new PmcProjectTypeConfiguration(
      pmcProjectService.getProjectByGuid(pmcProjectGuid).getPmcProjectType());
    variables.put("processDefsToHide", ptConfig.getProcessDefsToHide());
    String query = templateService.getTemplateString(ES_QUERY_PROCESS_TEMPLATE, variables);

    Map<String, Object> params = new HashMap<>();
    params.put("pmcProjectGuid", pmcProjectGuid);
    SearchHit[] hits = elasticService.findByTemplate(query, params, null, 0, 10000, true);
    for (SearchHit h : Arrays.asList(hits)) {
      Map<String, Object> source = h.getSourceAsMap();
      String pid = (String) source.get("processInstanceId");
      String superPid = (String) source.get("superProcessInstanceId");
      String processDefinitionKey = (String) source.get("processDefinitionKey");
      String processDefinitionId = (String) source.get("processDefinitionId");
      Long startTime = (Long) source.get("startTime");
      Long endTime = (Long) source.get("endTime");
      String businessKey = (String) source.get("businessKey");
      String deleteReason = (String) source.get("deleteReason");

      boolean processFound = false;
      for (PmcProjectProcessDto p : activeProcesses) {
        if (p.getProcessInstanceId() != null && p.getProcessInstanceId().equals(pid)) {
          processFound = true;
          if (startTime != null)
            p.setStartTime(new Date(startTime));
          if (endTime != null)
            p.setEndTime(new Date(endTime));
        }
      }
      if ( !processFound) {
        PmcProjectProcessDto pToInsert = new PmcProjectProcessDto();
        pToInsert.setProcessKey(processDefinitionKey);
        pToInsert.setProcessInstanceId(pid);
        if (startTime != null)
          pToInsert.setStartTime(new Date(startTime));
        if (endTime != null)
          pToInsert.setEndTime(new Date(endTime));
        if (endTime == null)
          pToInsert.setStatus(ProcessState.ACTIVE.toString());
        else if (deleteReason != null)
          pToInsert.setStatus(ProcessState.CANCELED.toString());
        else
          pToInsert.setStatus(ProcessState.COMPLETED.toString());
        pToInsert.setBusinessObject(businessKey);
        pToInsert.setRelation(ProcessRelation.CHILD.toString());
        pToInsert.setProcessDefinitionId(processDefinitionId);

        boolean foundParentProcess = false;
        for (int i = 0; i < activeProcesses.size(); i++ ) {
          PmcProjectProcessDto p = activeProcesses.get(i);
          if (p.getProcessInstanceId() != null && p.getProcessInstanceId().equals(superPid)) {
            foundParentProcess = true;
            activeProcesses.add(i + 1, pToInsert);
            break;
          }
        }
        if ( !foundParentProcess) {
          activeProcesses.add(pToInsert);
        }
      }
    }
  }

  private List<PmcProjectProcessDto> getSubProcesses(String pid, Long pmcProjectProcessGuid) {

    List<PmcProjectProcessDto> subProcesses = new ArrayList<>();

    List<ProcessInstance> piList = this.runtimeService
      .createProcessInstanceQuery()
      .variableValueEquals(ProcessVariables.PMC_PROJECT_PROCESS_GUID, pmcProjectProcessGuid)
      .list();

    for (ProcessInstance pi : piList) {
      if (pi.getProcessInstanceId().equals(pid))
        continue;

      ProcessDefinition processDefinition = repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
      PmcProjectProcessDto subProc = new PmcProjectProcessDto();

      subProc.setBusinessObject(pi.getBusinessKey());
      subProc.setProcessInstanceId(pi.getProcessInstanceId());
      subProc.setLabel(processDefinition.getName());
      subProc.setProcessKey(processDefinition.getKey());
      subProc.setRelation(ProcessRelation.CHILD.toString());
      subProc.setStatus(ProcessState.ACTIVE.toString());

      subProcesses.add(subProc);
    }
    return subProcesses;
  }

  @Override
  public void mainProcessEnded(DelegateExecution execution) {

    log.info("Main process end handling started...");

    LongValue pmcProjectGuid = execution.getVariableTyped(ProcessVariables.PMC_PROJECT_GUID);
    PmcProject p = pmcProjectDao.getById(pmcProjectGuid.getValue());
    p.setStatus(PmcProjectState.COMPLETED);

    PmcProjectProcess projectProcess = null;
    for (PmcProjectProcess process : p.getProcesses()) {
      if (process.getRelation() == ProcessRelation.MAIN) {
        projectProcess = process;
      }
    }
    projectProcess.setStatus(ProcessState.COMPLETED);
    pmcProjectDao.getEntityManager().flush();
  }

  @Override
  public void mainProcessCanceled(DelegateExecution execution) {

    log.info("Main process end handling started...");

    LongValue pmcProjectGuid = execution.getVariableTyped(ProcessVariables.PMC_PROJECT_GUID);
    PmcProject p = pmcProjectDao.getById(pmcProjectGuid.getValue());
    p.setStatus(PmcProjectState.CANCELED);

    PmcProjectProcess projectProcess = null;
    for (PmcProjectProcess process : p.getProcesses()) {
      if (process.getRelation() == ProcessRelation.MAIN) {
        projectProcess = process;
      }
    }
    projectProcess.setStatus(ProcessState.CANCELED);
    pmcProjectDao.getEntityManager().flush();
  }

  @Override
  public ProcessDefinitionDto getProcessDefinition(String processKey) {
    try {
      return processDefinitionCache.get(processKey);
    } catch (ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public PmcProjectProcessDto createAndStartProcess(
      Long pmcProjectGuid,
      String processKey,
      String workDetails,
      Map<String, Object> processVars) {
    PmcProjectDto pmcProjectDto = pmcProjectService.getProjectByGuid(pmcProjectGuid);
    log.info("starting process {} in project {}", processKey, pmcProjectDto.toShortString());
    PmcProjectProcessDto pPPD = new PmcProjectProcessDto();
    pPPD.setRelation(ProcessRelation.CHILD.toString());
    pPPD.setWorkDetails(workDetails);
    pPPD.setBusinessObject(pmcProjectDto.getRefObjectIdName());
    pPPD.setProcessInstanceId("");
    pPPD.setProcessKey(processKey);
    PmcProjectProcessDto pmcProjectProcessDto = saveProjectProcess(pmcProjectDto.getGuid(), pPPD);
    startProjectProcess(pmcProjectProcessDto.getGuid(), processVars);
    return pPPD;
  }

  private PmcProjectProcess getByGuid(Long guid) {
    PmcProjectProcess process = pmcProjectProcessDao.getById(guid);
    if (process == null) {
      throw new IllegalArgumentException("no PmcProjectProcess found with guid=" + guid);
    }
    return process;
  }

  private void throwWebAppException(String logMessage, String responseMessage) {
    log.error(logMessage);
    throw new WebApplicationException(
      Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage).type(MediaType.TEXT_PLAIN).build());
  }
}
