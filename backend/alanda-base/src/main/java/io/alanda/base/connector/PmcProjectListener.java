package io.alanda.base.connector;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.camunda.bpm.engine.task.Task;

import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.PmcProjectPhaseDto;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectCard;
import io.alanda.base.entity.PmcProjectPhase;
import io.alanda.base.entity.PmcProjectProcess;
import io.alanda.base.service.PmcPropertyService;

public interface PmcProjectListener {

  String getListenerIdName();

  void beforeEntityCreation(PmcProject project, Collection<PmcPropertyDto> properties);

  void beforeProcessStart(PmcProject project, Map<String, Object> variables);

  void afterProcessStart(PmcProject project);

  void beforeProcessCancellation(PmcProjectProcess process, boolean canceledByProject, String reason);

  void beforeProjectUpdate(PmcProjectDto newProject, PmcProject oldProject);

  void afterProjectFinished(PmcProject project);

  void beforeProjectCancelled(PmcProject project, String reason);

  void afterProjectCancelled(PmcProject project);

  //Relation methods
  void afterRelationUpdate(PmcProject project);

  // MS methods

  /**
   * @deprecated Does not get called when creating ms from GUI, use
   *             {@link #beforeMilestoneUpgrade(PmcProjectMilestoneDto , Date , Date , String , PmcProjectDto )} instead
   * @param ms
   */
  @Deprecated
  void beforeMilestoneCreation(PmcProjectMilestoneDto ms);

  void beforeMilestoneUpgrade(PmcProjectMilestoneDto oldMs, Date newFc, Date newAct, String reason, PmcProjectDto project);

  void onSetCustomerProject(PmcProject project);

  // Phase methods
  void afterPhaseUpdate(PmcProjectPhaseDto oldPhase, PmcProjectPhase newPhase);

  void afterCardUpdate(PmcProject project, PmcProjectCard card);

  void addMetaInfo(PmcProjectDto project);

  void beforeRoleChange(PmcProjectDto project, String roleName, Long roleValue, String source);

  // property methods
  void afterSetProperty(Long pmcProjectGuid, String key, Object value, PmcPropertyService.PmcPropertyType valueType);

  default void afterTaskDueDateSet(Long pmcProjectGuid, Task task, Date dueDate) {
  }
  
  default void afterOfferApproval(Long pmcProjectGuid, String banfId) {
    
  }
}
