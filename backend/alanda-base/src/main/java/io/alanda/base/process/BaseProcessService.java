package io.alanda.base.process;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.value.LongValue;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dao.PmcProjectProcessDao;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectProcess;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcPropertyService.PmcPropertyType;
import io.alanda.base.type.ProcessState;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.cache.UserCache;

public abstract class BaseProcessService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

  @Inject
  protected PmcProjectDao pmcProjectDao;

  @Inject
  protected PmcProjectProcessDao pmcProjectProcessDao;

  @Inject
  @Named("pmcProjectData")
  protected PmcProjectData pmcProjectData;

  @Inject
  protected PmcProjectService pmcProjectService;

  @Inject
  protected PmcPropertyService pmcPropertyService;

  @Inject
  protected BusinessProcess businessProcess;

  @Inject
  protected PmcProcessService pmcProcessService;

  @Inject
  protected RepositoryService repositoryService;

  @Inject
  protected UserCache userCache;

  @Inject
  protected ConfigService configService;

  public void setProjectState(DelegateExecution execution, String status) {
    PmcProjectDto pmcProjectDto = pmcProjectService.getProjectByGuid(pmcProjectData.getPmcProjectGuid());
    pmcProjectDto.setStatus(status);
    pmcProjectService.updateProject(pmcProjectDto);
  }

  public void activatePhase(DelegateExecution execution, String phaseIdName) {
    pmcProjectService.activatePhase(pmcProjectData.getPmcProject().getProjectId(), phaseIdName);
  }

  public void deactivatePhase(DelegateExecution execution, String phaseIdName) {
    pmcProjectService.deactivatePhase(pmcProjectData.getPmcProject().getProjectId(), phaseIdName);
  }

  /**
   * Sets the PmcProject to completed
   * 
   * @param execution
   */
  public void mainProcessCompleted(DelegateExecution execution) {

    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    if (pmcProjectGuid == null)
      throw new RuntimeException("No pmcProjectGuid set!");
    logger.info("mainProcessCompleted: " + pmcProjectGuid);
    pmcProjectService.projectFinished(pmcProjectGuid);
  }

  public void subProcessFinished(DelegateExecution execution) {
    saveProcessResult(execution);
  }

  public void subProcessStartedListener(DelegateExecution execution) {
    String exIdToUse = execution.getId();
    logger
      .info("EXEC: " + execution.getId() + "/" + execution.getParentId() + "/" + execution.getProcessInstanceId() + " using: " + exIdToUse);
    Map<String, Object> varMap = execution.getVariables();
    logger.info("subProcessStartedListener - variables: " + varMap);

    PmcProject p = pmcProjectData.getPmcProject();

    LongValue projectProcessGuid = execution.getVariableTyped(ProcessVariables.PMC_PROJECT_PROCESS_GUID);

    execution.removeVariable(ProcessVariables.PMC_PROJECT_PROCESS_GUID);
    execution.setVariableLocal(ProcessVariables.PMC_PROJECT_PROCESS_GUID, projectProcessGuid);

    PmcProjectProcess process = pmcProjectProcessDao.getById(projectProcessGuid.getValue());

    String refObjectIdName = process.getBusinessObject();
    String refObjectType = p.getRefObjectType();

    PmcRefObjectConnector con = pmcProjectService.getRefObjectLoader(refObjectType);
    if (refObjectIdName != null && con != null) {
      RefObject refObject = con.getRefObjectByName(refObjectIdName);
      if (refObject != null) {
        execution.setVariableLocal(ProcessVariables.REFOBJECTID, refObject.getRefObjectId());
        execution.setVariableLocal(ProcessVariables.REFOBJECTIDNAME, refObject.getIdName());
        execution.setVariableLocal(ProcessVariables.REFOBJECTTYPE, refObject.getRefObjectType());
      }
    }

    process.setParentExecutionId(execution.getId());
    process.setStatus(ProcessState.ACTIVE);
    pmcProjectProcessDao.getEntityManager().flush();
  }

  public void saveProcessResult(DelegateExecution execution) {
    String exIdToUse = execution.getId();
    logger
      .info("EXEC: " + execution.getId() + "/" + execution.getParentId() + "/" + execution.getProcessInstanceId() + " using: " + exIdToUse);
    Map<String, Object> varMap = execution.getVariables();
    logger.info("save process result - variables: " + varMap);

    LongValue projectProcessGuid = execution.getVariableLocalTyped(ProcessVariables.PMC_PROJECT_PROCESS_GUID);

    PmcProjectProcess process = pmcProjectProcessDao.getById(projectProcessGuid.getValue());

    process.setStatus(ProcessState.COMPLETED);
    pmcProjectProcessDao.getEntityManager().flush();
  }

  public void setProcessInstanceId(DelegateExecution execution) {
    Long projectProcessGuid = (Long) execution.getVariable(ProcessVariables.PMC_PROJECT_PROCESS_GUID);
    if (projectProcessGuid != null) {
      pmcProcessService
        .setProcessData(
          projectProcessGuid,
          execution.getProcessInstanceId(),
          repositoryService.getProcessDefinition(execution.getProcessDefinitionId()).getName());
    }
  }

  public void checkStringProcessVar(String varName, String defaultValue) {
    if (StringUtils.isBlank(varName)) {
      throw new IllegalArgumentException("checkProcessVar: provide valid varName");
    }
    if (StringUtils.isBlank(defaultValue)) {
      throw new IllegalArgumentException("checkProcessVar: provide valid defaultValue");
    }
    String value = pmcProjectData.getVariable(varName);
    if (StringUtils.isEmpty(value)) {
      pmcProjectData.setVariable(varName, defaultValue);
    }
  }

  public void incMainIteration(String type) {
    String name = type + "MainIteration";
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    Integer mainIteration = pmcPropertyService.getIntegerProperty(null, null, pmcProjectGuid, name);
    if (mainIteration == null) {
      mainIteration = 1;
    } else {
      mainIteration = mainIteration + 1;
    }
    pmcPropertyService.setProperty(null, null, pmcProjectGuid, name, mainIteration, PmcPropertyType.INTEGER);
    pmcProjectData.setVariable(name, mainIteration);

  }

  public void nextIteration(String processPrefix, String[] reqNames) {

    // archive values of approvals from last iteration and reset actual ones
    int iterator;
    try {
      iterator = pmcProjectData.getVariable(processPrefix + "Iteration");
    } catch (Exception ex) {
      logger.warn("Could not determine dsIteration - setting to 1 instead.");
      iterator = 1;
    }

    for (String name : reqNames) {
      copyApprovalResult(processPrefix, name, iterator);
    }

    iterator++ ;

    pmcProjectData.setVariable(processPrefix + "Iteration", iterator);

    requiredChecks(processPrefix, reqNames);
  }

  public void requiredChecks(String processPrefix, String[] reqNames) {
    Integer mainIteration = pmcProjectData.getVariable(processPrefix + "MainIteration");
    if (mainIteration == null) {
      incMainIteration(processPrefix);
      mainIteration = pmcProjectData.getVariable(processPrefix + "MainIteration");
    }
    Integer iteration = pmcProjectData.getVariable(processPrefix + "Iteration");
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    for (String name : reqNames) {
      Boolean req = (Boolean) pmcProjectData.getVariable(processPrefix + name + "Required");
      String value = "NOT_REQUIRED";
      if (req != null && req) {
        value = "PENDING";
      }
      String projectVarName = processPrefix + name + "Approved" + "_" + mainIteration + "_" + iteration;
      pmcPropertyService.setProperty(null, null, pmcProjectGuid, projectVarName, value, PmcPropertyType.STRING);
    }
    pmcPropertyService
      .setProperty(null, null, pmcProjectGuid, processPrefix + "MainIteration_" + mainIteration, iteration, PmcPropertyType.INTEGER);
  }

  public void copyProjectPropertyIntoVariable(String propertyKey, String variableName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    Serializable prop = (Serializable) pmcPropertyService.getProperty(null, null, pmcProjectGuid, propertyKey);
    pmcProjectData.setVariable(variableName, prop);
  }

  private void copyApprovalResult(String prefix, String name, int oldIteration) {
    pmcProjectData.setVariable(prefix + name + "_" + oldIteration, pmcProjectData.getVariable(prefix + name));
    pmcProjectData.setVariable(prefix + name, null);
  }

  public void deleteProcessVariable(DelegateExecution execution, String variableName) {
    Object varValue = execution.getVariable(variableName);
    if (varValue != null) {
      execution.removeVariable(variableName);
    }
  }

  public void processEndedListener(DelegateExecution execution) {
    PmcProject pmcProj = pmcProjectData.getPmcProject();
    logger.info("ending " + pmcProj.getHumanReadableId() + "  process!");
    pmcProjectService.projectFinished(pmcProjectData.getPmcProjectGuid());
  }

  public String getNowWithOffset(String period) {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    Period p = periodFormatter.parsePeriod(period);
    DateTime dt = new DateTime().plus(p);
    return dateFormat.format(dt.toDate());
  }
}
