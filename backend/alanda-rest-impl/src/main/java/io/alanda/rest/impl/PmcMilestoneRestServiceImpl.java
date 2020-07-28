package io.alanda.rest.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.MilestoneDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.security.PmcShiroAuthorizationService;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcAuthorizationService;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.service.PmcProjectService;

import io.alanda.rest.PmcMilestoneRestService;

public class PmcMilestoneRestServiceImpl implements PmcMilestoneRestService {

  private static final Logger log = LoggerFactory.getLogger(PmcMilestoneRestServiceImpl.class);

  @Inject
  private PmcProjectService pmcProjectService;

  @Inject
  private PmcProcessService pmcProcessService;

  @Inject
  private PmcAuthorizationService pmcAuthorizationService;

  @Inject
  private ElasticService elasticService;

  @Inject
  private PmcShiroAuthorizationService pmcShiroAuthorizationService;

  @Override
  public List<MilestoneDto> listAllMilestones() {
    return pmcProjectService.listAllMilestones();
  }

  @Override
  public MilestoneDto getMileByGuid(Long milestoneGuid) {
    log.info("milestoneGuid: {}", milestoneGuid);
    return pmcProjectService.getMileById(milestoneGuid);
  }

  @Override
  public Long createMilestone(MilestoneDto milestoneDto) {
    return pmcProjectService.createMilestone(milestoneDto);
  }

  @Override
  public Response updateMilestone(MilestoneDto milestoneDto) {
    return pmcProjectService.updateMilestone(milestoneDto);
  }

  @Override
  public Response deleteMilestone(Long milestoneGuid) {
    return pmcProjectService.deleteMilestone(milestoneGuid);
  }

  @Override
  public List<PmcProjectMilestoneDto> milestonesPerProject(Long projectId) {
    return pmcProjectService.getMilestonesPerProject(projectId);
  }

  @Override
  public PmcProjectMilestoneDto milestonesById(Long guid) {
    return pmcProjectService.getProjectMilestoneById(guid);
  }

  @Override
  public PmcProjectTypeDto milestoneDefinitionsPerProjectType(Long projectTypeId) {
    return pmcProjectService.getMilestoneDefinitionsPerProjectType(projectTypeId);
  }

  @Override
  public Long createProjectMilestone(String reason, PmcProjectMilestoneDto projectMilestoneDto) {
    log.info("projectMilestoneDto: {}", projectMilestoneDto);
    Long projectGuid = projectMilestoneDto.getProject().getGuid();
    String milestoneIdName = projectMilestoneDto.getMilestone().getIdName();
    Date fc = projectMilestoneDto.getFc();
    Date act = projectMilestoneDto.getAct();
    Date baseline = projectMilestoneDto.getFc();
    log.info("reason: {}", reason);
    return pmcProjectService.createProjectMilestone(projectGuid, milestoneIdName, fc, act, baseline, reason);
  }

  @Override
  public Response updateProjectMilestone(Long projectMilestoneGuid, String reason, PmcProjectMilestoneDto projectMilestoneDto) {
    log.info("projectMilestoneGuid: {}", projectMilestoneGuid);
    log.info("projectMilestoneDto: {}", projectMilestoneDto);
    Date fc = projectMilestoneDto.getFc();
    Date act = projectMilestoneDto.getAct();
    log.info("reason: {}", reason);
    pmcProjectService.updateProjectMilestone(projectMilestoneGuid, fc, false, act, false, reason);
    return Response.ok().build();
  }

  @Override
  public PmcProjectMilestoneDto getByProjectAndMsIdName(String projectId, String msIdName) {

    PmcProjectMilestoneDto ms = pmcProjectService.getProjectMilestoneByProjectAndMsIdName(projectId, msIdName);
    if (ms == null)
      return null;
    checkPermissionsForMilestone(ms.getProject().getGuid(), ms.getMilestone().getIdName(), null, "read");
    return ms;
  }

  @Override
  public Response updateProjectMilestone(String projectId, String msIdName, String reason, PmcProjectMilestoneDto projectMilestoneDto) {
    Date fc = projectMilestoneDto.getFc();
    Date act = projectMilestoneDto.getAct();
    String postFix = "";
    if (fc != null) {
      postFix = ":fc";
    }
    if (act != null) {
      postFix = ":act";
    }
    if ((act != null) && (fc != null)) {
      postFix = ":fc,act";
    }
    PmcProjectDto p = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForMilestone(p.getGuid(), msIdName, postFix, "write");
    pmcProjectService.updateProjectMilestone(projectId, msIdName, fc, act, reason);
    return Response.ok().build();
  }

  private void checkPermissionsForMilestone(Long pmcProjectGuid, String msIdName, String postfix, String permissions) {
    Subject subject = SecurityUtils.getSubject();
    String key = pmcShiroAuthorizationService.getAuthKeyForMilestone(pmcProjectGuid, msIdName, permissions);
    if (postfix != null)
      key += postfix;
    if ( !subject.isPermitted(key)) {
      throw new ForbiddenException("You are not allowed to access the Milestone, key was: " + key);
    }
  }
}
