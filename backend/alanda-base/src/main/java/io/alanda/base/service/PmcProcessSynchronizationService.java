/**
 * 
 */
package io.alanda.base.service;

import java.util.Date;
import java.util.List;

import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.WaitingExecutionDto;

/**
 * @author jlo
 */
public interface PmcProcessSynchronizationService {

  List<PmcProjectMilestoneDto> getModifiedMilestones(Date date);

  List<WaitingExecutionDto> getAllMilestoneMessageEvents();

  List<WaitingExecutionDto> getMilestoneMessageEvents(Date lastModified);

}
