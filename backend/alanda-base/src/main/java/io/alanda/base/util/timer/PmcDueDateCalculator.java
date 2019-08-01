package io.alanda.base.util.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.type.ProcessVariables;

/**
 * Calculate Due Date for a timer according to site,network element, link and project
 * 
 * @author Julian Löffelhardt
 */
@Named
@Stateless
public class PmcDueDateCalculator {

  private final static Logger logger = LoggerFactory.getLogger(PmcDueDateCalculator.class);

  public static final String DATE_IN_FAR_FUTURE = "2100-01-01T00:00:00";

  @Inject
  private PmcProjectService pmcProjectService;

  /**
   * @param execution
   * @return the milestone date minus offset or a date in far future if no milestone date is set
   */
  public String getDueDate(DelegateExecution execution) {
    return getDueDate(execution, null);
  }

  public String getDueDate(DelegateExecution execution, String jobHandlerConfiguration) {
    String activityId;
    if (execution instanceof ExecutionEntity) {
      List<JobEntity> jobs = ((ExecutionEntity) execution).getJobs();
      if (jobHandlerConfiguration != null) {
        jobs = jobs
          .stream()
          .filter(j -> j.getJobHandlerConfiguration().toCanonicalString().equals(jobHandlerConfiguration))
          .collect(Collectors.toList());
      }
      if (jobs.size() == 1) {
        activityId = jobs.get(0).getJobDefinition().getActivityId();
      } else {
        throw new RuntimeException("found wrong number of jobs (" +
            jobs.size() +
            ") for execution " +
            execution.getId() +
            " in process " +
            execution.getProcessInstanceId());
      }
    } else {
      activityId = execution.getCurrentActivityId();
    }

    Long pmcProjectGuid = (Long) execution.getVariable(ProcessVariables.PMC_PROJECT_GUID);

    Date dueDate = calcDueDate(activityId, pmcProjectGuid);

    if (dueDate != null) {
      String retVal = DueDateCalculatorUtil.dateToString(dueDate);
      execution.setVariable("dueDateCalculator-" + activityId, retVal);
      return retVal;
    } else {
      return DATE_IN_FAR_FUTURE;
    }
  }

  public String getDueDateForTask(DelegateExecution execution) {
    // TODO
    return null;
  }

  public Date calcDueDate(String timerDefinition, Long pmcProjectGuid) {
    logger.info("activityId: " + timerDefinition);

    DueDateCalculatorData data = DueDateCalculatorUtil.parseMilestoneTimerId(timerDefinition);
    Date dueDate = null;
    if (data != null) {
      dueDate = calcDueDate(data, pmcProjectGuid);
    } else {
      logger.info("Could not parse acticvityId: " + timerDefinition);
    }
    return dueDate;
  }

  public Date calcDueDate(DueDateCalculatorData data, Long pmcProjectGuid) {

    List<Long> projectCandidates = new ArrayList<>();
    if (data.isChildMs()) {
      projectCandidates.addAll(pmcProjectService.getProjectByGuid(pmcProjectGuid, PmcProjectService.Mode.RELATIONIDS).getChildrenIds());
    } else if (data.isParentMs()) {
      projectCandidates.addAll(pmcProjectService.getProjectByGuid(pmcProjectGuid, PmcProjectService.Mode.RELATIONIDS).getParentIds());
    } else {
      projectCandidates.add(pmcProjectGuid);
    }

    Date dueDate = null;
    logger.debug("Config: " + data.toString());
    Date msDate = null;
    for (Long pId : projectCandidates) {
      if (data.getField().equals("FC")) {
        msDate = getMsFc(pId, data.getMsName());
      } else if (data.getField().equals("ACT")) {
        msDate = getMsAct(pId, data.getMsName());
      } else {
        throw new IllegalArgumentException("field " + data.getField() + " not supported with PMC milestones");
      }
      if (msDate != null) break;
    }

    if (msDate != null) {
      dueDate = data.calcDueDate(msDate);
      logger.info("pmcProjectGuid: " + pmcProjectGuid + ", msName: " + data.getMsName() + " ->" + dueDate);
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("No MsDate found for: project:" + pmcProjectGuid + ", msName:" + data.getMsName());
      }
    }
    return dueDate;
  }

  private Date getMsAct(Long pmcProjectGuid, String msName) {
    PmcProjectMilestoneDto ppmd = pmcProjectService.getProjectMilestoneByProjectAndMsIdName(pmcProjectGuid, msName);
    if (ppmd != null)
      return ppmd.getAct();
    return null;
  }

  private Date getMsFc(Long pmcProjectGuid, String msName) {
    PmcProjectMilestoneDto ppmd = pmcProjectService.getProjectMilestoneByProjectAndMsIdName(pmcProjectGuid, msName);
    if (ppmd != null)
      return ppmd.getFc();
    return null;
  }
}
