package io.alanda.base.connector;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.PmcProjectPhaseDto;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectCard;
import io.alanda.base.entity.PmcProjectPhase;
import io.alanda.base.entity.PmcProjectProcess;
import io.alanda.base.service.PmcPropertyService;

public abstract class PmcProjectListenerBase implements PmcProjectListener {

  @Override
  public abstract String getListenerIdName();

  @Override
  public void beforeEntityCreation(PmcProject project, Collection<PmcPropertyDto> properties) {
  }

  @Override
  public void beforeProcessStart(PmcProject project, Map<String, Object> variables) {
  }

  @Override
  public void afterProcessStart(PmcProject project) {
  }

  @Override
  public void beforeProcessCancellation(PmcProjectProcess process, boolean canceledByProject, String reason) {
  }

  @Override
  public void beforeProjectUpdate(PmcProjectDto newProject, PmcProject oldProject) {
  }

  @Override
  public void afterProjectFinished(PmcProject project) {
  }

  @Override
  public void beforeProjectCancelled(PmcProject project, String reason) {
  }

  @Override
  public void afterProjectCancelled(PmcProject project) {
  }

  @Override
  public void beforeMilestoneCreation(PmcProjectMilestoneDto ms) {
  }

  @Override
  public void beforeMilestoneUpgrade(PmcProjectMilestoneDto oldMs, Date newFc, Date newAct, String reason, PmcProjectDto project) {
  }

  @Override
  public void onSetCustomerProject(PmcProject project) {
  }

  @Override
  public void afterPhaseUpdate(PmcProjectPhaseDto oldPhase, PmcProjectPhase newPhase) {
  }

  @Override
  public void afterRelationUpdate(PmcProject project) {
  }

  @Override
  public void afterCardUpdate(PmcProject project, PmcProjectCard card) {
  }

  @Override
  public void addMetaInfo(PmcProjectDto project) {
  }

  @Override
  public void beforeRoleChange(PmcProjectDto project, String roleName, Long roleValue, String source) {
  }

  @Override
  public void afterSetProperty(Long pmcProjectGuid, String key, Object value, PmcPropertyService.PmcPropertyType valueType) {
  }
}
