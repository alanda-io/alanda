package io.alanda.base.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.MilestoneDto;
import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcProjectCardDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.PmcProjectPhaseDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.dto.SimpleMilestoneDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectType;
import io.alanda.base.type.PmcProjectResultStatus;
import io.alanda.base.type.ProcessRelation;

public interface PmcProjectService {

  enum Mode {
      DOCU("docu"),
      TREE("tree"),
      RELATIONIDS("relationIds"),
      /**
       * Alles ausser Provider
       */
      DEFAULT(null);

    private final String mode;

    Mode(String mode) {
      this.mode = mode;

    }

    public String getMode() {
      return mode;
    }

  }

  PmcProjectDto addProject(PmcProjectDto project);

  PmcProjectDto cancelProject(Long projectGuid, String reason);

  /**
   * Sets the status of the project and all the corresponding processes to CANCELD. The camunda processes will only be
   * canceled if <code>stopProcesses</code> is set to <code>true</code>.
   * 
   * @param projectGuid
   * @param stopProcesses
   * @return
   */
  PmcProjectDto cancelProject(Long projectGuid, boolean stopProcesses, String reason);

  PmcProjectDto setResult(Long projectGuid, PmcProjectResultStatus resultStatus, String resultComment);

  PmcProjectDto getProjectByGuid(Long guid);

  PmcProjectDto getProjectByGuid(Long guid, Mode mode);

  PmcProjectDto getProjectByProjectId(String projectId);

  List<PmcProjectDto> getProjects();

  PagedResultDto<Map<String, Object>> getProjectsElastic(
      Map<String, Object> filterParams,
      Map<String, Object> sortParams,
      int from,
      int size);

  List<PmcProjectDto> getProjectsByProjectTypeAndTags(Collection<String> types, Collection<String> tags);

  List<PmcProjectDto> getProjectsByProjectType(Collection<String> types);

  List<PmcProjectDto> getProjectsByProjectTags(Collection<String> tags);

  List<PmcProjectDto> getProjectsByTypeAndRefObject(String typeIdName, RefObject refObject, Mode mode);

  List<PmcProjectDto> getByCustomerProjectId(Long id, Mode mode);

  List<PmcProjectDto> getChildProjects(String parent);

  List<PmcProjectDto> getParentProjects(String child);

  String createProjectPrefix(PmcProjectType projectType);

  PmcProjectTypeDto getProjectType(String idName);

  List<PmcProjectTypeDto> searchProjectTypes(String searchTerm);

  List<PmcProjectTypeDto> searchChildProjectTypes(String searchTerm, Long projectParentGuid);

  List<PmcProjectTypeDto> getChildTypes(String idName);

  List<PmcProjectTypeDto> getParentTypes(String idName);

  void addProcess(long pmcProjectGuid, String processInstanceId, ProcessRelation relation);

  void setCustomerProject(long pmcProjectGuid, Long customerProjectId);

  PmcProjectDto updateProject(PmcProjectDto project);

  PmcProjectDto updateProjectGuStatus(long pmcProjectGuid, String guStatus, boolean callListener);

  PmcProjectDto updateProjectRelations(
      PmcProjectDto project,
      String additionalChildren,
      String removeChildren,
      String additionalParents,
      String removeParents);

  /**
   * Return the single user having the <code>roleName</code> for project with <code>pmcProjectGuid</code>.
   *
   * If the role is not set in this project, get user set as <code>roleName</code> via PmcRefObjectConnector
   * for <code>refObjectType</code> and <code>refObjectId</code>.
   *
   * @param refObjectType
   * @param refObjectId
   * @param roleName
   * @param pmcProjectGuid
   * @return <code>null</code> if no user is found.
   */
  PmcUserDto getUserForRole(String refObjectType, Long refObjectId, String roleName, Long pmcProjectGuid);

  List<InternalContactDto> getContacts(Long pmcProjectGuid);

  PmcProjectDto updateDueDate(long pmcProjectGuid, Date dueDate, boolean callListener);

  PmcProjectDto addProject(
      String projectTypeIdName,
      String subtype,
      String[] tag,
      Long refObjectId,
      String refObjectIdName,
      String refObjectType,
      Long ownerId,
      Long customerProjectId,
      boolean executeStartProcess,
      String comments,
      String title,
      Date startDate,
      Date dueDate,
      String processInstanceId);

  PmcProjectDto addProjectWithExtraProcessVariables(
                           String projectTypeIdName,
                           String subtype,
                           String[] tag,
                           Long refObjectId,
                           String refObjectIdName,
                           String refObjectType,
                           Long ownerId,
                           Long customerProjectId,
                           boolean executeStartProcess,
                           String comments,
                           String title,
                           Date startDate,
                           Date dueDate,
                           String processInstanceId,
                           Map<String, Object> processVariables);

  void projectFinished(Long pmcProjectGuid);

  Collection<PmcProjectListener> getListener(PmcProject p);

  PmcRefObjectConnector getRefObjectLoader(String refObjectType);

  // project phase methods

  List<PmcProjectPhaseDto> getPhasesForProject(Long projectGuid);

  PmcProjectPhaseDto getPhase(Long projectGuid, String phaseDefIdName);

  void initPhasesForProject(String projectId, Collection<PmcPropertyDto> properties);

  void initPhasesForProjects();

  void enablePhase(String projectId, String projectPhaseDefinitionIdName);

  void disablePhase(String projectId, String projectPhaseDefinitionIdName);

  void activatePhase(String projectId, String projectPhaseDefinitionIdName);

  void deactivatePhase(String projectId, String projectPhaseDefinitionIdName);

  PmcProjectPhaseDto updatePhase(PmcProjectPhaseDto phaseDto);

  PmcProjectPhaseDto updatePhase(Long pmcProjectGuid, String phaseDefinitionIdName, PmcProjectPhaseDto updateDto);

  PmcProjectPhaseDto updatePhase(Long pmcProjectGuid, String phaseDefinitionIdName, PmcProjectPhaseDto updateDto, boolean setAutoDate);

  // project milestone methods

  List<MilestoneDto> listAllMilestones();

  MilestoneDto getMileById(Long guid);

  Long createMilestone(MilestoneDto milestoneDto);

  Response updateMilestone(MilestoneDto milestoneDto);

  Response deleteMilestone(Long guid);

  List<PmcProjectMilestoneDto> getMilestonesPerProject(Long pmcProjectGuid);

  List<SimpleMilestoneDto> getSimpleMilestonesPerProject(Long pmcProjectGuid);

  PmcProjectMilestoneDto getProjectMilestoneById(Long guid);

  PmcProjectMilestoneDto getProjectMilestoneByProjectAndMsIdName(String projectId, String msIdName);

  PmcProjectMilestoneDto getProjectMilestoneByProjectAndMsIdName(Long pmcProjectGuid, String msIdName);

  PmcProjectTypeDto getMilestoneDefinitionsPerProjectType(Long projectTypeId);

  Long createProjectMilestone(Long projectGuid, String milestoneIdName, Date fc, Date act, Date baseline, String reason);

  PmcProjectMilestoneDto updateProjectMilestone(String projectId, String msIdName, Date fc, Date act, String reason);

  PmcProjectMilestoneDto updateProjectMilestone(Long projectGuid, String msIdName, Date fc, Date act, String reason);

  PmcProjectMilestoneDto updateProjectMilestone(
      Long projectGuid,
      String msIdName,
      Date fc,
      boolean clearFc,
      Date act,
      boolean clearAct,
      String reason);

  PmcProjectMilestoneDto updateProjectMilestone(
      Long projectMilestoneGuid,
      Date fc,
      boolean clearFc,
      Date act,
      boolean clearAct,
      String reason);

  /**
   * @param projectId
   * @param phaseDefinitionIdName
   * @return <code>true</code>, <code>false</code> or <code>null</code> if phase enabled is not set yet
   */
  Boolean isPhaseEnabled(String projectId, String phaseDefinitionIdName);

  PmcProjectDto getProjectForProcess(Long projectProcessGuid);

  PmcProjectProcessDto getProjectProcess(Long projectProcessGuid);

  /**
   * Returns true iff the project p is the only PMC project for the related customer project or if p has been created
   * first.
   * 
   * @param p
   * @return
   */
  boolean isMainProjectForCustomerProject(PmcProjectDto p);

  void freezePhase(String projectId, String projectPhaseDefinitionIdName);

  List<PmcProjectCardDto> getCardListForProject(String projectId, String listName, boolean createIfEmpty) throws Exception;

  PmcProjectCardDto addCardForProject(Long projectGuid, String listName) throws Exception;

  PmcProjectCardDto updateCardTitle(Long projectGuid, Long cardId, String title) throws Exception;

  PmcProjectCardDto updateCardComment(Long projectGuid, Long cardId, String comment) throws Exception;

  PmcProjectCardDto updateCardStatus(Long projectGuid, Long cardId, String status) throws Exception;

  PmcProjectCardDto updateCardCategory(Long projectGuid, Long cardId, String category) throws Exception;

  PmcProjectCardDto updateCardOwner(Long projectGuid, Long cardId, String owner) throws Exception;

  PmcProjectMilestoneDto getMilestoneByIdNameAndAct(String idName, Date act);

  List<PmcProjectDto> getChildProjects(Long pmcProjectGuid);

  PmcProjectDto synchProject(Long guid);

  /**
   *
   * @param refObjectType
   * @param refObjectId
   * @param projType can be passed for additional filtering but is optional. Set to null if not needed.
   * @return
   */
  List<PmcProjectDto> getByTypeAndRefObjectId(String refObjectType, long refObjectId, Long projType);

}
