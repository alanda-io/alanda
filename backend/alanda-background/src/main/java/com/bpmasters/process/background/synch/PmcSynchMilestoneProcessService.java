/**
 * 
 */
package com.bpmasters.process.background.synch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dto.WaitingExecutionDto;
import io.alanda.base.service.PmcProcessSynchronizationService;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.timer.DueDateCalculatorData;
import io.alanda.base.util.timer.DueDateCalculatorUtil;
import io.alanda.base.util.timer.PmcDueDateCalculator;

/**
 * @author jlo
 */
@Named("pmcSynchMilestoneProcessService")
public class PmcSynchMilestoneProcessService {

  private static final Logger logger = LoggerFactory.getLogger(PmcSynchMilestoneProcessService.class);

  @Inject
  private ManagementService managementService;

  @Inject
  private PmcDueDateCalculator dueDateCalculator;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private BusinessProcess businessProcess;

  @Inject
  private MilestoneSyncData data;

  @Inject
  PmcProcessSynchronizationService synchService;

  private final int timerMaxTimerSyncResults = 200;

  /**
   * 01.01.2100
   */
  private static final long DATE_IN_FAR_FUTURE = 4_102_441_200_000L;

  public static final long ONE_MINUTE_BUFFER = 60000L;

  @Inject
  private PmcProjectDao pmcProjectDao;

  public void synchWaitingExecutions() throws Exception {
    boolean complete = false;
    logger.info("Starting , status: " + data.getCursor() + "/" + data.getWaitingExecutions().size());

    //WaitingExecutionDto current=null;
    for (int i = 0; i < 500; i++ ) {
      if ( !data.hasNext()) {
        complete = true;
        break;
      }

      synchMilestone(data.next());
    }
    if (complete)
      data.setSyncFinished(Boolean.TRUE);
    else
      data.setSyncFinished(Boolean.FALSE);
  }

  public void synchMilestone(WaitingExecutionDto we) {

    //read pmcProjectGuid
    if (we.getPmcProjectGuid() == null) {
      try {
        Execution e = runtimeService.createExecutionQuery().executionId(we.getExecutionId()).singleResult();
        if (e != null) {
          we.setPmcProjectGuid((Long) runtimeService.getVariable(we.getExecutionId(), ProcessVariables.PMC_PROJECT_GUID));
        } else {
          logger.warn("Error while fetching variables - the following execution (id) does not exist: " + we.getExecutionId());
        }
      } catch (Exception e) {
        logger.info("Error reading vars for WE: " + we.getExecutionId() + ", message: " + e.getMessage(), e);

      }
    }

    if (we.getPmcProjectGuid() == null || we.getExecutionId() == null) {
      logger.info("Required variables pmcProjectGuid: " + we.getPmcProjectGuid() + ", executionId: " + we.getExecutionId() + " missing.");
    }
    DueDateCalculatorData ddcd = we.getDueDateCalculatorData();
    if (ddcd == null) {
      ddcd = DueDateCalculatorUtil.parseMilestoneTimerId(we.getMessageName());
    }
    Date dueDate = dueDateCalculator.calcDueDate(ddcd, we.getPmcProjectGuid());
    //logger.info("synch ddcd: " + ddcd + ", executionId: " + we.getExecutionId() + " dueDate: " + dueDate);
    if (dueDate != null && dueDate.before(new Date())) {
      sendMessage(we.getMessageName(), we.getExecutionId());
    }
  }

  private void sendMessage(String messageName, String executionId) {
    try {
      runtimeService.messageEventReceived(messageName, executionId);
      logger.info("sending message '{}' to execution '{}'", messageName, executionId);
    } catch (Exception e) {
      logger.error("message " + messageName + " could not be delivered to execution " + executionId + " because of: " + e.getMessage(), e);
    }
  }

  public void fetchWaitingExecutions() throws Exception {

    if (data.getPerformFullSync()) {
      data.setWaitingExecutions(synchService.getAllMilestoneMessageEvents());
    } else {
      data.setWaitingExecutions(synchService.getAllMilestoneMessageEvents());
      /*Date cmpDate = new Date(data.getLastSyncDate().getTime() - ONE_MINUTE_BUFFER);
      List<PmcProjectMilestoneDto> modifiedMilestones = synchService.getModifiedMilestones(cmpDate);
      List<WaitingExecutionDto> modEvents = synchService.getMilestoneMessageEvents(cmpDate);
      logger.info("{} modified milestones found, {} modified events", modifiedMilestones.size(), modEvents.size());
      for (PmcProjectMilestoneDto ppmd : modifiedMilestones) {
        if (ppmd.getAct() != null && ppmd.getAct().before(new Date())) {
          modEvents.add(new WaitingExecutionDto(ppmd.getMilestone().getIdName(), null, ppmd.getProject().getGuid(), ppmd.getAct()));
        }
      }
      data.setWaitingExecutions(modEvents);*/
    }
  }

  public void prepareMessageSynch() throws Exception {

    data.setCurrentSyncDate(new Date());
    data.setPerformFullSync(isFirstExecution());
    //data.setPerformFullSync(true);
    data.setEndProcess(endDateReached());
  }

  public void prepareNextMessageSynch() throws Exception {

    if (data.getPerformFullSync()) {
      String endDate = DueDateCalculatorUtil.getNextDateForTime(0, 30, 0);
      data.setEndDate(DueDateCalculatorUtil.stringToDate(endDate));
    }
    data.setLastSyncDate(data.getCurrentSyncDate());

  }

  private boolean isFirstExecution() {
    return data.getEndDate() == null;
  }

  /**
   * @return true if the and date has been reached or the end date date is null or not parsable
   */
  private boolean endDateReached() {
    Date endDate = data.getEndDate();
    if (endDate != null) {
      return endDate.before(new Date());
    }
    return false;
  }

  public void prepareTimerSynch() {
    List<Integer> firstResultList = fillFirstResultList();
    businessProcess.setVariable("timerFirstResultList", firstResultList);

  }

  public void performTimerSynch() throws Exception {

    int firstSyncResult = (int) businessProcess.getVariable("firstSyncResult");

    for (Job job : queryTimers(firstSyncResult)) {
      if (job instanceof TimerEntity) {
        TimerEntity timer = (TimerEntity) job;
        DueDateCalculatorData data = DueDateCalculatorUtil
          .parseMilestoneTimerId(((TimerEntity) job).getJobHandlerConfiguration().toCanonicalString());
        if (data == null) {
          continue;
        }
        String executionId = timer.getExecutionId();
        Long pmcProjectGuid = (Long) runtimeService.getVariable(executionId, "pmcProjectGuid");
        Date dueDate = dueDateCalculator.calcDueDate(data, pmcProjectGuid);

        //        logger.info(
        //          "Synching timer " +
        //            timer.getJobHandlerConfiguration() +
        //            ", data: " +
        //            data +
        //            ", pmcProjectGuid: " +
        //            pmcProjectGuid +
        //            ", dueDate:" +
        //            dueDate);
        if (dueDate == null)
          dueDate = new Date(DATE_IN_FAR_FUTURE);
        managementService.setJobDuedate(timer.getId(), dueDate);
      }
    }

  }

  private List<Job> queryTimers(int firstSyncResult) {
    return managementService.createJobQuery().timers().orderByJobId().desc().listPage(firstSyncResult, timerMaxTimerSyncResults);
  }

  private long queryTimerCount() {
    return managementService.createJobQuery().timers().count();
  }

  private List<Integer> fillFirstResultList() {
    long timerCount = queryTimerCount();
    List<Integer> firstResultList = new ArrayList<>();
    for (int i = 0; i < timerCount; i += timerMaxTimerSyncResults) {
      firstResultList.add(i);
    }
    return firstResultList;
  }

}
