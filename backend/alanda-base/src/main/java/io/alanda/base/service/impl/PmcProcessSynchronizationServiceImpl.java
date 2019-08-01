/**
 * 
 */
package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;

import io.alanda.base.dao.PmcProjectMilestoneDao;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.WaitingExecutionDto;
import io.alanda.base.service.PmcProcessSynchronizationService;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.timer.DueDateCalculatorData;
import io.alanda.base.util.timer.DueDateCalculatorUtil;
import io.alanda.base.util.timer.PmcDueDateCalculator;

/**
 * @author jlo
 */
@Singleton
@ApplicationScoped()
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Named("pmcProcessSynchronizationService")
public class PmcProcessSynchronizationServiceImpl implements PmcProcessSynchronizationService {

  public static final long ONE_MINUTE_BUFFER = 60000L;

  @Inject
  private PmcProjectMilestoneDao pmcProjectMilestoneDao;

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcDueDateCalculator dueDateCalculator;

  @Inject
  private RuntimeService runtimeService;

  public void synchMessage(String messageName, Long pmcProjectGuid) {
    dueDateCalculator.calcDueDate(messageName, pmcProjectGuid);
  }

  @Override
  public List<PmcProjectMilestoneDto> getModifiedMilestones(Date date) {
    return dozerMapper.mapCollection(pmcProjectMilestoneDao.getModifiedMilestones(date), PmcProjectMilestoneDto.class);
  }

  @Override
  public List<WaitingExecutionDto> getAllMilestoneMessageEvents() {
    ArrayList<WaitingExecutionDto> waitingExecutions = new ArrayList<>();

    for (EventSubscription event : runtimeService.createEventSubscriptionQuery().eventType("message").orderByCreated().asc().list()) {
      //filter milestone depended messages with regex
      DueDateCalculatorData ddcd = DueDateCalculatorUtil.parseMilestoneTimerId(event.getEventName());
      if (ddcd != null) {
        waitingExecutions.add(new WaitingExecutionDto(ddcd, event.getExecutionId(), null, null).withMessageName(event.getEventName()));
      }
    }
    return waitingExecutions;
  }

  @Override
  public List<WaitingExecutionDto> getMilestoneMessageEvents(Date lastModified) {

    ArrayList<WaitingExecutionDto> waitingExecutions = new ArrayList<>();
    for (EventSubscription event : runtimeService.createEventSubscriptionQuery().eventType("message").orderByCreated().desc().list()) {
      //check created date or filter relevant messageNames
      if (event.getCreated().before(lastModified))
        break;
      DueDateCalculatorData ddcd = DueDateCalculatorUtil.parseMilestoneTimerId(event.getEventName());
      if (ddcd != null) {
        waitingExecutions.add(new WaitingExecutionDto(ddcd, event.getExecutionId(), null, null));
      }
    }
    return waitingExecutions;
  }
}
