package io.alanda.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcProjectCardDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectPhaseDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.security.PmcShiroAuthorizationService;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcProjectService.Mode;
import io.alanda.base.type.PmcProjectResultStatus;

import io.alanda.rest.ProjectRestService;

public class ProjectRestServiceImpl implements ProjectRestService {

  private static final Logger log = LoggerFactory.getLogger(ProjectRestServiceImpl.class);

  @Inject
  private PmcProjectService pmcProjectService;

  @Inject
  private PmcProcessService pmcProcessService;

  @Inject
  private ElasticService elasticService;

  @Inject
  private PmcShiroAuthorizationService pmcShiroAuthorizationService;

  @Override
  public PmcProjectDto getByGuid(Long guid, Boolean tree) throws IOException {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(guid, tree ? Mode.TREE : Mode.DEFAULT);
    return checkPermissionsForProject(project, "read", true);
  }

  @Override
  public PmcProjectDto getByProjectId(String projectId) throws IOException {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    return checkPermissionsForProject(project, "read", true);
  }

  @Override
  public PmcProjectDto updateByProjectId(String projectId, PmcProjectDto projectDto) throws IOException {

    if ( !projectDto.getProjectId().equals(projectId))
      throw new IllegalArgumentException("URL does not match to POST resource!");

    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", true);

    PmcProjectDto ret = pmcProjectService.updateProject(projectDto);
    ret.setAuthBase(pmcShiroAuthorizationService.getAuthBaseForProject(ret));
    return ret;
  }

  @Override
  public PmcProjectDto updateGuStatusByProjectId(String projectId, String guStatus) throws IOException {

    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    project = checkPermissionsForProject(project, "write", true);
    // guStatus does not influence authBase, so no getAuthBase necessary
    return pmcProjectService.updateProjectGuStatus(project.getGuid(), guStatus, true);
  }

  @Override
  public PmcProjectDto updateProjectRelations(
      String projectId,
      String additionalChildren,
      String removeChildren,
      String additionalParents,
      String removeParents) {

    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    project = checkPermissionsForProject(project, "write", true);

    return pmcProjectService.updateProjectRelations(project, additionalChildren, removeChildren, additionalParents, removeParents);

  }

  @Override
  public PagedResultDto getProjectsElastic(Map<String, Object> params) {
    Integer pageNumber = (Integer) params.get("pageNumber");
    if (pageNumber == null)
      pageNumber = 1;
    Integer pageSize = (Integer) params.get("pageSize");
    if (pageSize == null)
      pageSize = 15;
    Map<String, Object> filterOptions = (Map<String, Object>) params.get("filterOptions");
    Map<String, Object> sortOptions = (Map<String, Object>) params.get("sortOptions");
    return pmcProjectService.getProjectsElastic(filterOptions, sortOptions, (pageNumber - 1) * pageSize, pageSize);
  }

  @Override
  public PmcProjectTypeDto getProjectTypeByName(String name) {
    if ( !checkPermissionsForProjectType(name, "read")) {
      throw new ForbiddenException("Not allowed to acccess this project type!");
    }
    PmcProjectTypeDto pt = pmcProjectService.getProjectType(name);
    return pt;
  }

  @Override
  public List<PmcProjectTypeDto> searchCreateableProjectTypes(String searchterm) {
    return searchProjectTypeByName(searchterm, "create");
  }

  @Override
  public List<PmcProjectTypeDto> searchCreateableChildProjectTypes(String searchterm, Long projectParentGuid) {
    return searchChildProjectTypeByName(searchterm, projectParentGuid, "create");
  }

  @Override
  public List<PmcProjectTypeDto> searchProjectTypeByName(String searchterm) {
    return searchProjectTypeByName(searchterm, "read");
  }

  @Override
  public List<PmcProjectTypeDto> searchChildProjectTypeByName(String searchterm, Long projectParentGuid) {
    return searchChildProjectTypeByName(searchterm, projectParentGuid, "read");
  }

  private List<PmcProjectTypeDto> searchProjectTypeByName(String searchterm, String permissions) {
    List<PmcProjectTypeDto> result = pmcProjectService.searchProjectTypes(searchterm);
    Iterator<PmcProjectTypeDto> it = result.iterator();
    while (it.hasNext()) {
      PmcProjectTypeDto pt = it.next();
      if ( !checkPermissionsForProjectType(pt.getIdName(), permissions))
        it.remove();
    }
    return result;
  }

  private List<PmcProjectTypeDto> searchChildProjectTypeByName(String searchterm, Long projectParentGuid, String permissions) {
    List<PmcProjectTypeDto> result = pmcProjectService.searchChildProjectTypes(searchterm, projectParentGuid);
    Iterator<PmcProjectTypeDto> it = result.iterator();
    while (it.hasNext()) {
      PmcProjectTypeDto pt = it.next();
      if ( !checkPermissionsForProjectType(pt.getIdName(), permissions))
        it.remove();
    }
    return result;
  }

  @Override
  public List<PmcProjectTypeDto> getChildTypes(String idName) {
    List<PmcProjectTypeDto> result = pmcProjectService.getChildTypes(idName);
    return result;
  }

  @Override
  public List<PmcProjectTypeDto> getParentTypes(String idName) {
    List<PmcProjectTypeDto> result = pmcProjectService.getParentTypes(idName);
    return result;
  }

  @Override
  public PmcProjectDto createProject(PmcProjectDto project) {

    if ( !checkPermissionsForProjectType(project.getPmcProjectType().getIdName(), "create")) {
      throw new ForbiddenException("Not allowed to create projects type " + project.getPmcProjectType().getName() + "!");
    }

    PmcProjectDto retVal = pmcProjectService.addProject(project);
    elasticService.refreshTaskIndex();
    return retVal;
  }

  @Override
  public PmcProjectDto cancelProject(Long projectGuid, String reason) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);

    if ( !checkPermissionsForProjectType(project.getPmcProjectType().getIdName(), "write")) {
      throw new ForbiddenException("Not allowed to cancel projects type " + project.getPmcProjectType().getName() + "!");
    }

    return pmcProjectService.cancelProject(projectGuid, reason);
  }

  @Override
  public PmcProjectDto setResult(Long projectGuid, PmcProjectDto project) {
    checkPermissionsForProject(project, "write", true);
    return pmcProjectService.setResult(projectGuid, PmcProjectResultStatus.valueOf(project.getResultStatus()), project.getResultComment());
  }

  // Process related REST calls

  @Override
  public PmcProjectProcessDto saveProjectProcess(Long projectGuid, PmcProjectProcessDto process) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    checkPermissionsForProcess(project, "create", null);
    return pmcProcessService.saveProjectProcess(projectGuid, process);
  }

  @Override
  public PmcProjectProcessDto startProjectProcess(Long processGuid) {
    PmcProjectDto project = pmcProjectService.getProjectForProcess(processGuid);
    List<String> phases = getPhasesForProject(project, processGuid);
    checkPermissionsForProcess(project, "start", phases);
    PmcProjectProcessDto retVal = pmcProcessService.startProjectProcess(processGuid);
    elasticService.refreshProcessIndex();
    elasticService.refreshTaskIndex();
    return retVal;
  }

  @Override
  public PmcProjectProcessDto cancelProjectProcess(Long processGuid, String resultStatus, String resultComment, String reason) {

    PmcProjectDto project = pmcProjectService.getProjectForProcess(processGuid);
    List<String> phases = getPhasesForProject(project, processGuid);
    checkPermissionsForProcess(project, "start", phases);
    PmcProjectProcessDto retVal = pmcProcessService.cancelProjectProcess(processGuid, resultStatus, resultComment, false, reason);
    elasticService.refreshTaskIndex();
    elasticService.refreshProcessIndex();
    return retVal;
  }

  @Override
  public Response removeProjectProcess(Long processGuid, String reason) {
    PmcProjectDto project = pmcProjectService.getProjectForProcess(processGuid);
    checkPermissionsForProcess(project, "create", null);
    pmcProcessService.removeProjectProcess(processGuid, reason);
    return Response.accepted().build();
  }

  @Override
  public Collection<PmcProjectProcessDto> getAllProjectProcesses(Long projectGuid) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    checkPermissionsForProject(project, "read", false);
    return pmcProcessService.getAllProjectProcesses(projectGuid);
  }

  @Override
  public PmcProjectProcessDto getMainProjectProcess(Long projectGuid) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    checkPermissionsForProject(project, "read", false);
    return pmcProcessService.getMainProjectProcess(projectGuid);
  }

  @Override
  public Map<String, Object> getProcessesAndTasks(Long projectGuid, Boolean useLegacy) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    checkPermissionsForProject(project, "read", false);
    return pmcProcessService.getProcessesAndTasksForProject(projectGuid, Boolean.TRUE.equals(useLegacy));
  }

  @Override
  public Collection<PmcProjectPhaseDto> getProjectPhases(Long projectGuid) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    Collection<PmcProjectPhaseDto> phases = pmcProjectService.getPhasesForProject(projectGuid);
    for (Iterator<PmcProjectPhaseDto> i = phases.iterator(); i.hasNext();) {
      PmcProjectPhaseDto ph = i.next();
      if ( !pmcShiroAuthorizationService.checkPermissionsForPhase(project, ph.getPmcProjectPhaseDefinition().getIdName(), "read")) {
        i.remove();
      }
    }
    return phases;
  }

  @Override
  public PmcProjectPhaseDto getProjectPhase(Long projectGuid, String phaseDefIdName) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    if ( !pmcShiroAuthorizationService.checkPermissionsForPhase(project, phaseDefIdName, "read")) {
      throw new ForbiddenException("You are not allowed to access the PmcPhase!");
    }
    return pmcProjectService.getPhase(projectGuid, phaseDefIdName);
  }

  @Override
  public PmcProjectPhaseDto updateProjectPhase(Long projectGuid, String phaseDefinitionIdName, PmcProjectPhaseDto phase) {
    PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
    if ( !pmcShiroAuthorizationService.checkPermissionsForPhase(project, phaseDefinitionIdName, "write")) {
      throw new ForbiddenException("You are not allowed to access the PmcPhase!");
    }
    return pmcProjectService.updatePhase(projectGuid, phaseDefinitionIdName, phase);
  }

  @Override
  public List<PmcProjectCardDto> getCardListForProject(String projectId, String cardListIdName, boolean createIfEmpty) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "read", false);
    return pmcProjectService.getCardListForProject(projectId, cardListIdName, createIfEmpty);
  }

  @Override
  public PmcProjectCardDto addCardForProject(String projectId, String cardListIdName) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", false);
    return pmcProjectService.addCardForProject(project.getGuid(), cardListIdName);
  }

  @Override
  public PmcProjectCardDto updateCardTitle(String projectId, Long cardId, String title) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", false);
    return pmcProjectService.updateCardTitle(project.getGuid(), cardId, title);
  }

  @Override
  public PmcProjectCardDto updateCardComment(String projectId, Long cardId, String comment) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", false);
    return pmcProjectService.updateCardComment(project.getGuid(), cardId, comment);
  }

  @Override
  public PmcProjectCardDto updateCardStatus(String projectId, Long cardId, String status) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", false);
    return pmcProjectService.updateCardStatus(project.getGuid(), cardId, status);
  }

  @Override
  public PmcProjectCardDto updateCardCategory(String projectId, Long cardId, String category) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", false);
    return pmcProjectService.updateCardCategory(project.getGuid(), cardId, category);
  }

  @Override
  public PmcProjectCardDto updateCardOwner(String projectId, Long cardId, String owner) throws Exception {
    PmcProjectDto project = pmcProjectService.getProjectByProjectId(projectId);
    checkPermissionsForProject(project, "write", false);
    return pmcProjectService.updateCardOwner(project.getGuid(), cardId, owner);
  }

  // private authorization related methods

  private PmcProjectDto checkPermissionsForProject(PmcProjectDto project, String permissions, boolean checkRelatives) {
    PmcProjectDto ret = pmcShiroAuthorizationService.checkPermissionsForProject(project, permissions, checkRelatives);
    if (ret == null)
      throw new ForbiddenException("You are not allowed to access the PmcProject!");
    return ret;
  }

  private boolean checkPermissionsForProjectType(String typeIdName, String permissions) {
    String key = "project:" + permissions + ":" + typeIdName;
    Subject subject = SecurityUtils.getSubject();
    return subject.isPermitted(key);
  }

  private boolean checkPermissionsForProcess(PmcProjectDto project, String permissions, List<String> phases) {
    String key = "proc:" + permissions + ":" + project.getPmcProjectType().getIdName(); //TODO: check: maybe use authBase
    Subject subject = SecurityUtils.getSubject();
    if (phases != null) {
      String[] keys = new String[phases.size()];
      for (int i = 0; i < phases.size(); i++ ) {
        keys[i] = key + ":" + phases.get(i);
      }
      boolean[] perms = subject.isPermitted(keys);
      for (boolean perm : perms) {
        if (perm)
          return true;
      }
      return false;
    } else {
      return subject.isPermitted(key);
    }
  }

  private List<String> getPhasesForProject(PmcProjectDto project, Long projectProcessGuid) {
    PmcProjectProcessDto ppp = pmcProjectService.getProjectProcess(projectProcessGuid);
    String processKey = ppp.getProcessKey();

    List<PmcProjectPhaseDto> phases = pmcProjectService.getPhasesForProject(project.getGuid());
    List<String> phaseNames = new ArrayList<>();
    for (PmcProjectPhaseDto phase : phases) {
      if (phase.getPmcProjectPhaseDefinition().getAllowedProcesses().contains(processKey)) {
        phaseNames.add(phase.getPmcProjectPhaseDefinition().getIdName());
      }
    }
    return phaseNames;
  }

  @Override
  public Response synchProject(Long projectGuid) {
    this.pmcProjectService.synchProject(projectGuid);
    return Response.accepted().build();
  }

}
