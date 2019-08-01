package io.alanda.base.service;

import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.ProcessDefinitionDto;

public interface PmcProcessService {

  String ACTIVE_PROCESSES = "active";
  String ALLOWED_PROCESSES = "allowed";
  String PHASE_NAME_MAP = "phaseNames";

  PmcProjectProcessDto saveProjectProcess(Long projectGuid, PmcProjectProcessDto process);
  
  PmcProjectProcessDto startProjectProcess(Long processGuid);
  
  PmcProjectProcessDto startProjectProcess(Long processGuid, Map<String, Object> processVars);

  PmcProjectProcessDto cancelProjectProcess(Long processGuid);

  PmcProjectProcessDto cancelProjectProcess(
      Long processGuid,
      String resultState,
      String resultComment,
      boolean canceledByProject,
      String reason);
    
  void removeProjectProcess(Long processGuid, String reason);
  /*
   * returns only the child processes not the main process
   */
  Collection<PmcProjectProcessDto> getAllProjectProcesses(
      Long projectGuid);
  
  PmcProjectProcessDto getMainProjectProcess(Long projectGuid);
  
  Map<String, Object> getProcessesAndTasksForProject(Long projectGuid, boolean useLegacy);
  
  void mainProcessEnded(DelegateExecution execution);

  void mainProcessCanceled(DelegateExecution execution);

  void setProcessData(Long pmcProjectProcessGuid, String processInstanceId, String label);
  
  ProcessDefinitionDto getProcessDefinition(String processKey);

  PmcProjectProcessDto createAndStartProcess(Long pmcProjectGuid, String processKey, String workDetails, Map<String, Object> processVars);

}
