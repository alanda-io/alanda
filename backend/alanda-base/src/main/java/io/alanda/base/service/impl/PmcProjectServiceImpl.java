package io.alanda.base.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.connector.ProjectTypeElasticListener;
import io.alanda.base.dao.CardDao;
import io.alanda.base.dao.CardListDao;
import io.alanda.base.dao.IdCounterDao;
import io.alanda.base.dao.MilestoneDao;
import io.alanda.base.dao.PmcProjectCardDao;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dao.PmcProjectMilestoneDao;
import io.alanda.base.dao.PmcProjectPhaseDao;
import io.alanda.base.dao.PmcProjectProcessDao;
import io.alanda.base.dao.PmcProjectTypeDao;
import io.alanda.base.dao.PmcPropertyDao;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.MilestoneDto;
import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcHistoryLogDto;
import io.alanda.base.dto.PmcProjectCardDto;
import io.alanda.base.dto.PmcProjectCompactDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.PmcProjectPhaseDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.ProcessDefinitionDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.dto.SimpleMilestoneDto;
import io.alanda.base.entity.Card;
import io.alanda.base.entity.CardList;
import io.alanda.base.entity.Milestone;
import io.alanda.base.entity.PmcHistoryLog.Action;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectCard;
import io.alanda.base.entity.PmcProjectMilestone;
import io.alanda.base.entity.PmcProjectPhase;
import io.alanda.base.entity.PmcProjectPhaseDefinition;
import io.alanda.base.entity.PmcProjectProcess;
import io.alanda.base.entity.PmcProjectType;
import io.alanda.base.security.PmcShiroAuthorizationService;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcGroupService;
import io.alanda.base.service.PmcHistoryService;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcPropertyService.PmcPropertyType;
import io.alanda.base.service.PmcRoleService;
import io.alanda.base.type.PmcProjectResultStatus;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.type.ProcessRelation;
import io.alanda.base.type.ProcessState;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.UserContext;
import io.alanda.base.util.cache.UserCache;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
@Named("PmcProjectService")
public class PmcProjectServiceImpl implements PmcProjectService {

  private final Logger logger = LoggerFactory.getLogger(PmcProjectServiceImpl.class);

  private final String MS_LOG_DATE_FORMAT = "dd.MM.yyyy";

  @Inject
  private PmcProjectTypeDao pmcProjectTypeDao;

  @Inject
  private PmcProjectDao pmcProjectDao;

  @Inject
  private PmcPropertyDao pmcPropertyDao;

  @Inject
  private PmcProjectProcessDao pmcProjectProcessDao;

  @Inject
  private IdCounterDao idCounterDao;

  @Inject
  private PmcProjectPhaseDao pmcProjectPhaseDao;

  @Inject
  private PmcProjectCardDao pmcProjectCardDao;

  @Inject
  private CardListDao cardListDao;

  @Inject
  private CardDao cardDao;

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private HistoryService historyService;

  @Inject
  private PmcPropertyService pmcPropertyService;

  @Inject
  private ElasticService elasticService;

  @Inject
  private Instance<PmcProjectListener> pmcProjectListener;

  private Map<String, PmcProjectListener> startListenerMap;

  @Inject
  private Instance<PmcRefObjectConnector> refObjectConnectors;

  private Map<String, PmcRefObjectConnector> refObjectLoaders;

  @Inject
  private Instance<ProjectTypeElasticListener> projectTypeElasticListeners;

  @Inject
  private PmcRoleService pmcRoleService;

  @Inject
  private PmcGroupService pmcGroupService;

  private Map<String, ProjectTypeElasticListener> elasticListeners;

  @Inject
  private PmcProcessService pmcProcessService;

  @Inject
  private UserCache userCache;

  @Inject
  private PmcProjectMilestoneDao pmcProjectMilestoneDao;

  @Inject
  private MilestoneDao milestoneDao;

  @Inject
  private PmcHistoryService pmcHistoryService;

  @Inject
  private PmcShiroAuthorizationService authorizationService;

  private static final String[] MONITOR_FIELDS_INCLUDE = new String[] {
    "project.*",
    "project.pmcProjectType.idName",
    "contacts.*",
    "refObject.clusterIdName",
    "refObject.*"};

  private static final String[] MONITOR_FIELDS_EXCLUDE = new String[] {
    "project.processes",
    "project.history",
    "project.pmcProjectType.docuConfigs",
    "project.pmcProjectType.processDefinitions",
    "project.pmcProjectType.allowedTags",
    "project.pmcProjectType.allowedTagList",
    "project.pmcProjectType.subTypes",
    "project.pmcProjectType.allowedSubtypeList",
    "project.pmcProjectType.readRightGroups",
    "project.pmcProjectType.deleteRightGroups",
    "project.pmcProjectType.writeRightGroups",
    "project.pmcProjectType.allowedProcesses",
    "project.pmcProjectType.writeRights",
    "project.pmcProjectType.additionalProperties",
    "project.pmcProjectType.deleteRights",
    "project.pmcProjectType.configuration",
    "project.pmcProjectType.createRights",
    "project.pmcProjectType.allowedSubtypes",
    "project.pmcProjectType.allowedMilestones",
    "project.pmcProjectType.readRights",
    "project.pmcProjectType.createRightGroups",};

  @PostConstruct
  private void initPmcProjectService() {
    startListenerMap = new HashMap<>();
    for (PmcProjectListener listener : pmcProjectListener) {
      logger.info("Found PmcProjectStartListener with idName " + listener.getListenerIdName() + ".");
      startListenerMap.put(listener.getListenerIdName(), listener);
    }

    this.refObjectLoaders = new HashMap<>();
    for (PmcRefObjectConnector connector : refObjectConnectors) {
      logger.info("Connector found! " + connector.getClass());
      if (connector.getRefObjectType() != null) {
        logger.info("Connector for ObjectType: " + connector.getRefObjectType());
        refObjectLoaders.put(connector.getRefObjectType(), connector);
      }
    }
    this.elasticListeners = new HashMap<>();
    for (ProjectTypeElasticListener listener : projectTypeElasticListeners) {
      logger.info("Found elastic listener " + listener.getName());
      elasticListeners.put(listener.getName(), listener);
    }
  }

  @Override
  public PmcProjectDto addProject(PmcProjectDto project) {
    Collection<PmcPropertyDto> properties = project.getProperties();
    PmcProject p = dozerMapper.map(project, PmcProject.class);

    // get the managed entities instead of the dozerMapper created ones
    PmcProjectType pt = pmcProjectTypeDao.getById(project.getPmcProjectType().getGuid());
    p.setPmcProjectType(pt);

    if (project.getParents() != null) {
      List<PmcProject> parents = new ArrayList<>();
      for (PmcProjectDto pDto : project.getParents()) {
        parents.add(pmcProjectDao.getById(pDto.getGuid()));
      }
      p.setParents(parents);
    }

    if (project.getChildren() != null) {
      List<PmcProject> children = new ArrayList<>();
      for (PmcProjectDto pDto : project.getChildren()) {
        children.add(pmcProjectDao.getById(pDto.getGuid()));
      }
      p.setChildren(children);
    }
    if (project.getCreateUserParameter() != null) {
      p.setOwnerId(project.getCreateUserParameter());
    } else {
      p.setOwnerId(UserContext.getUser().getGuid());
    }

    return addProject(p, true, properties, null, project.getProcessPackageParameter(), project.getCommentKey());
  }

  @Override
  public PmcProjectDto cancelProject(Long projectGuid, String reason) {
    return cancelProject(projectGuid, true, reason);
  }

  @Override
  public PmcProjectDto cancelProject(Long projectGuid, boolean stopProcesses, String reason) {

    PmcProject project = pmcProjectDao.getById(projectGuid);
    if (project == null)
      throw new RuntimeException("No project with guid " + projectGuid + " found!");

    if (project.getStatus().equals(PmcProjectState.COMPLETED) || project.getStatus().equals(PmcProjectState.CANCELED)) {
      throwWebAppException("not allowed to cancel project (guid=" + project.getGuid() + ") in state " + project.getStatus(),
                           "Not allowed to cancel the project in state " + project.getStatus());
    }

    PmcUserDto user = UserContext.getUser();
    String name = "[NULL]";
    if (user != null)
      name = user.getDisplayName();
    logger.warn("CANCEL for Project " + project.getProjectId() + " , called by User: " + name);

    Collection<PmcProjectListener> listener = getListener(project);
    for (PmcProjectListener l : listener) {
      l.beforeProjectCancelled(project, reason);
    }

    if (stopProcesses) {
      PmcProjectProcess mainProcess = null;
      for (PmcProjectProcess process : project.getProcesses()) {
        if (process.getRelation().equals(ProcessRelation.MAIN)) {
          mainProcess = process;
        } else {
          pmcProcessService.cancelProjectProcess(process.getGuid(), null, null, true, null);
        }
      }
      pmcProcessService.cancelProjectProcess(mainProcess.getGuid(), null, null, true, null);
    } else {
      for (PmcProjectProcess process : project.getProcesses()) {
        if (process.getStatus().equals(ProcessState.ACTIVE) || process.getStatus().equals(ProcessState.SUSPENDED))
          process.setStatus(ProcessState.CANCELED);
      }
    }

    project.setStatus(PmcProjectState.CANCELED);
    project.setResultComment(reason);

    for (PmcProjectListener l : listener) {
      l.afterProjectCancelled(project);
    }

    pmcProjectDao.getEntityManager().flush();
    PmcProjectDto pmcProjectDto = mapProject(project, null);//dozerMapper.map(project, PmcProjectDto.class);
    elasticService.updateEntry(pmcProjectDto);

    pmcHistoryService.createHistory(PmcHistoryLogDto.createForProject(project).withChange(Action.CANCEL, reason));

    authorizationService.addOrUpdateBaseAuthKeyForProject(pmcProjectDto);
    return pmcProjectDto;
  }

  @Override
  public PmcProjectDto setResult(Long projectGuid, PmcProjectResultStatus resultStatus, String resultComment) {

    PmcProject project = pmcProjectDao.getById(projectGuid);
    if (project == null)
      throw new RuntimeException("No project with guid " + projectGuid + " found!");

    project.setResultStatus(resultStatus);
    project.setResultComment(resultComment);

    pmcProjectDao.getEntityManager().flush();
    PmcProjectDto pmcProjectDto = mapProject(project, null);//dozerMapper.map(project, PmcProjectDto.class);
    elasticService.updateEntry(pmcProjectDto);
    authorizationService.addOrUpdateBaseAuthKeyForProject(pmcProjectDto);
    return pmcProjectDto;
  }

  @Override
  public void setCustomerProject(long pmcProjectGuid, Long customerProjectId) {
    PmcProject p = this.pmcProjectDao.getById(pmcProjectGuid);
    p.setCustomerProjectId(customerProjectId);

    Collection<PmcProjectListener> listener = getListener(p);
    for (PmcProjectListener l : listener) {
      l.onSetCustomerProject(p);
    }
  }

  @Override
  public PmcProjectDto addProject(String projectTypeIdName,
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
                                  String processInstanceId) {
    PmcProject p = new PmcProject();
    p.setPmcProjectType(pmcProjectTypeDao.getByIdName(projectTypeIdName));
    p.setSubtype(subtype);
    p.setTag(tag);
    p.setRefObjectId(refObjectId);
    p.setRefObjectIdName(refObjectIdName);
    p.setRefObjectType(refObjectType);
    p.setPriority(2);
    p.setCustomerProjectId(customerProjectId);
    p.setOwnerId(ownerId);
    p.setComment(comments);
    p.setTitle(title);
    p.setDueDate(dueDate);
    return addProject(p, executeStartProcess, null, processInstanceId, null, null);
  }

  @Override
  public PmcProjectDto addProjectWithExtraProcessVariables(String projectTypeIdName,
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
                                  Map<String, Object> processVariables) {
    PmcProject p = new PmcProject();
    p.setPmcProjectType(pmcProjectTypeDao.getByIdName(projectTypeIdName));
    p.setSubtype(subtype);
    p.setTag(tag);
    p.setRefObjectId(refObjectId);
    p.setRefObjectIdName(refObjectIdName);
    p.setRefObjectType(refObjectType);
    p.setPriority(2);
    p.setCustomerProjectId(customerProjectId);
    p.setOwnerId(ownerId);
    p.setComment(comments);
    p.setTitle(title);
    p.setDueDate(dueDate);
    return addProjectWithExtraProcessVariables(p, executeStartProcess, null, processInstanceId, null, null,processVariables);
  }


  protected PmcProjectDto addProject(
                                     PmcProject p,
                                     boolean executeStartProcess,
                                     Collection<PmcPropertyDto> properties,
                                     String processInstanceId,
                                     String processPackageKeyParameter,
                                     String commentKey) {
    return addProjectWithExtraProcessVariables(p, executeStartProcess, properties, processInstanceId,processPackageKeyParameter, commentKey, null);
  }

  protected PmcProjectDto addProjectWithExtraProcessVariables(PmcProject p,
                                     boolean executeStartProcess,
                                     Collection<PmcPropertyDto> properties,
                                     String processInstanceId,
                                     String processPackageKeyParameter,
                                     String commentKey,
                                     Map<String, Object> processVariables) {

    Collection<PmcProjectListener> listener = getListener(p);

    if (properties == null)
      properties = new ArrayList<>();

    p.setProjectId(createProjectId(p.getPmcProjectType()));
    p.setStatus(PmcProjectState.ACTIVE);

    for (PmcProjectListener l : listener)
      l.beforeEntityCreation(p, properties);
    logger.info("Creating pmc Project " + p.getProjectId() + " for type " + p.getPmcProjectType().getIdName());
    p = pmcProjectDao.create(p);
    if (p.getParents() != null) {
      for (PmcProject parent : p.getParents()) {
        if ( !parent.getChildren().contains(p)) {
          parent.getChildren().add(p);
        }
      }
    }
    if (p.getChildren() != null) {
      for (PmcProject child : p.getChildren()) {
        if ( !child.getParents().contains(p)) {
          child.getParents().add(p);
        }
      }
    }
    pmcProjectDao.getEntityManager().flush();

    initPhasesForProject(p.getProjectId(), properties);

    // add roles not yet defined in the properties
    for (String role : p.getPmcProjectType().roles()) {
      PmcPropertyDto roleProp = findProperty("role_" + role, properties);
      if (roleProp == null || StringUtils.isEmpty(roleProp.getValue())) {
        Long pmcParentGuid = p.firstParentGuid();
        PmcUserDto u = getUserForRole(p.getRefObjectType(), p.getRefObjectId(), role, pmcParentGuid);
        if (u != null) {
          if (roleProp != null) {
            roleProp.setValue(u.getGuid().toString());
          } else {
            roleProp = new PmcPropertyDto();

            roleProp.setKey("role_" + role);
            roleProp.setValueType("STRING");
            roleProp.setValue(u.getGuid().toString());
            properties.add(roleProp);

          }
        }
      }
    }

    for (PmcPropertyDto prop : properties) {
      if (prop.getValueType() != null)
        pmcPropertyService.setProperty(null,
                                       null,
                                       p.getGuid(),
                                       prop.getKey(),
                                       prop.getValue(),
                                       PmcPropertyType.valueOf(prop.getValueType()));
      else
        pmcPropertyService.setProperty(null, null, p.getGuid(), prop.getKey(), prop.getValue(), PmcPropertyType.STRING);
    }
    pmcPropertyDao.getEntityManager().flush();

    PmcProjectDto projectDto;

    if (p.getPmcProjectType().getStartProcess() != null && executeStartProcess) {

      Map<String, Object> variables = new HashMap<>();

      if (processPackageKeyParameter != null) {
        variables.put(ProcessVariables.PROCESS_PACKAGE_KEY, processPackageKeyParameter);
      } else if (p.getParents() != null && !p.getParents().isEmpty()) {
        logger.info("addProject - parents found");
        for (PmcProject parent : p.getParents()) {

          logger.info("parent -" + parent.getProjectId() + " #" + parent.getRefObjectIdName() + ", main Process: " + parent.mainProcess());
        }
        PmcProjectProcess parentMain = p.getParents().iterator().next().mainProcess();
        if (parentMain != null) {
          HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery()
                                                       .processInstanceId(parentMain.getProcessInstanceId())
                                                       .variableName(ProcessVariables.PROCESS_PACKAGE_KEY)
                                                       .singleResult();
          String processPackageKey = (String) hvi.getValue();
          logger.info("Found parentMain, ppKey: " + processPackageKey);
          if ( !StringUtils.isEmpty(processPackageKey)) {
            variables.put(ProcessVariables.PROCESS_PACKAGE_KEY, processPackageKey);
          }
        }
      }

      if (p.getRefObjectIdName() != null && !p.getRefObjectIdName().equals(""))
        variables.put("refObjectIdName", p.getRefObjectIdName());

      if (commentKey != null)
        variables.put(ProcessVariables.COMMENT_KEY, commentKey);
      variables.put("refObjectId", p.getRefObjectId());
      variables.put("refObjectType", p.getRefObjectType());
      variables.put("pmcProjectGuid", p.getGuid());

      for (PmcProjectListener l : listener)
        l.beforeProcessStart(p, variables);

      if (processVariables != null) {
        variables.putAll(processVariables);
      }
      logger.info("Process Variables for new project: " + variables);
      //Check.. alle Objecte aus dem Context werfen
      pmcProjectDao.getEntityManager().flush();
      pmcProjectDao.getEntityManager().clear();
      logger.info("Clearing persistence context: " + p.getGuid());
      ProcessInstance pi = runtimeService.startProcessInstanceByKey(p.getPmcProjectType().getStartProcess(),
                                                                    p.getRefObjectIdName(),
                                                                    variables);
      p = pmcProjectDao.getById(p.getGuid());
      PmcProjectProcess process = new PmcProjectProcess();
      process.setProcessInstanceId(pi.getProcessInstanceId());
      process.setLabel(repositoryService.getProcessDefinition(pi.getProcessDefinitionId()).getName());
      process.setPmcProject(p);
      process.setRelation(ProcessRelation.MAIN);
      process.setStatus(ProcessState.ACTIVE);
      process.setProcessKey(p.getPmcProjectType().getStartProcess());
      process = pmcProjectProcessDao.create(process);
      logger.info("Created main process: " + process);
      //Needed to be able to retrieve the process in the same transaction
      p.addProcess(process);
      pmcProjectProcessDao.getEntityManager().flush();

      for (PmcProjectListener l : listener)
        l.afterProcessStart(p);

      projectDto = mapProject(p, Mode.RELATIONIDS);
      PmcProjectProcessDto processDto = dozerMapper.map(process, PmcProjectProcessDto.class);

      elasticService.addProcess(pi.getProcessInstanceId(), processDto, projectDto, true);
    } else if ( !executeStartProcess && processInstanceId != null) {
      PmcProjectProcess process = new PmcProjectProcess();
      process.setProcessInstanceId(processInstanceId);
      process.setPmcProject(p);
      process.setRelation(ProcessRelation.MAIN);
      process.setStatus(ProcessState.ACTIVE);

      process = pmcProjectProcessDao.create(process);
      p.addProcess(process);
      logger.info("Created main processObject for already started instance: " + process);
      pmcProjectProcessDao.getEntityManager().flush();
      projectDto = mapProject(p, Mode.RELATIONIDS);
      PmcProjectProcessDto processDto = dozerMapper.map(process, PmcProjectProcessDto.class);
      elasticService.addProcess(processInstanceId, processDto, projectDto, true);
    } else {
      throw new IllegalArgumentException("startProcess false ohne processInstanceId");
    }

    pmcRoleService.syncProjectRoles(dozerMapper.map(p, PmcProjectDto.class));
    authorizationService.addOrUpdateBaseAuthKeyForProject(projectDto);
    return projectDto;
  }

  private PmcPropertyDto findProperty(String key, Collection<PmcPropertyDto> props) {
    for (PmcPropertyDto p : props) {
      if (key.equals(p.getKey()))
        return p;
    }
    return null;
  }

  @Override
  public void addProcess(long pmcProjectGuid, String processInstanceId, ProcessRelation relation) {
    PmcProject p = this.pmcProjectDao.getById(pmcProjectGuid);
    PmcProjectProcess process = new PmcProjectProcess();
    process.setProcessInstanceId(processInstanceId);
    process.setPmcProject(p);
    process.setRelation(relation);
    pmcProjectProcessDao.create(process);
  }

  @Override
  public PmcProjectDto getProjectByGuid(Long guid) {
    return getProjectByGuid(guid, Mode.DEFAULT);
  }

  @Override
  public PmcProjectDto getProjectByGuid(Long guid, Mode mode) {
    PmcProject pmcProject = pmcProjectDao.getById(guid);
    if (pmcProject == null)
      return null;
    PmcProjectDto pmcProjectDto = mapProject(pmcProject, mode);
    addMetaInfo(pmcProjectDto, null);
    return pmcProjectDto;
  }

  private PmcProjectDto mapProject(PmcProject pmcProject, Mode mode) {
    boolean getRelationIds = false;

    if (mode == null)
      mode = Mode.DEFAULT;

    if (mode == Mode.RELATIONIDS) {
      mode = Mode.DEFAULT;
      getRelationIds = true;
    }

    PmcProjectDto result;
    result = dozerMapper.map(pmcProject, PmcProjectDto.class, mode.getMode());

    Set<String> activePhases = new HashSet<>();
    for (PmcProjectPhase phase : pmcProject.getPhases()) {
      if (phase.getEnabled() != null && phase.getEnabled() && phase.getActive()) {
        activePhases.add(phase.getPmcProjectPhaseDefinition().getIdName());
      }
    }
    result.setActivePhases(activePhases.toArray(new String[0]));

    if (getRelationIds) {
      mapRelationIds(pmcProject, result);
    }

    mapProjectTypeDtoFields(result.getPmcProjectType());

    if (mode == Mode.TREE) {
      mapChildrenProjectTypeDtoFields(result);
      mapParentProjectTypeDtoFields(result);
    }

    PmcUserDto user = userCache.get(result.getOwnerId());
    if (user != null) {
      result.setOwnerName(user.getFirstName() + " " + user.getSurname());
    }
    return result;
  }

  private void mapChildrenProjectTypeDtoFields(PmcProjectDto p) {
    for (PmcProjectDto c : p.getChildren()) {
      mapProjectTypeDtoFields(c.getPmcProjectType());
      mapChildrenProjectTypeDtoFields(c);
    }
  }

  private void mapParentProjectTypeDtoFields(PmcProjectDto p) {
    for (PmcProjectDto parent : p.getParents()) {
      mapProjectTypeDtoFields(parent.getPmcProjectType());
      mapParentProjectTypeDtoFields(parent);
    }
  }

  @Override
  public PmcProjectDto getProjectByProjectId(String projectId) {
    PmcProjectDto result = mapProject(pmcProjectDao.getByProjectId(projectId), null);
    return result;
  }

  @Override
  public List<PmcProjectDto> getProjects() {
    return dozerMapper.mapCollection(pmcProjectDao.getAll(), PmcProjectDto.class);
  }

  @Override
  public List<PmcProjectDto> getProjectsByProjectTypeAndTags(Collection<String> types, Collection<String> tags) {
    return dozerMapper.mapCollection(pmcProjectDao.getProjectsByProjectTypeAndTags(types, tags), PmcProjectDto.class);
  }

  @Override
  public List<PmcProjectDto> getProjectsByProjectType(Collection<String> types) {
    return dozerMapper.mapCollection(pmcProjectDao.getProjectsByProjectType(types), PmcProjectDto.class);
  }

  @Override
  public List<PmcProjectDto> getProjectsByProjectTags(Collection<String> tags) {
    return dozerMapper.mapCollection(pmcProjectDao.getProjectsByProjectTags(tags), PmcProjectDto.class);
  }

  @Override
  public List<PmcProjectDto> getProjectsByTypeAndRefObject(String typeIdName, RefObject refObject, Mode mode) {

    if (mode == null)
      mode = Mode.DEFAULT;

    return dozerMapper.mapCollection(pmcProjectDao.getByTypeAndRefObject(typeIdName, refObject), PmcProjectDto.class, mode.getMode());
  }

  @Override
  public List<PmcProjectDto> getByCustomerProjectId(Long id, Mode mode) {
    List<PmcProjectDto> result = new ArrayList<>();
    for (PmcProject p : pmcProjectDao.getByCustomerProjectId(id)) {
      result.add(mapProject(p, mode));
    }
    return result;
  }

  @Override
  public List<PmcProjectDto> getChildProjects(String parent) {

    PmcProject parentProject = pmcProjectDao.getByProjectId(parent);
    if (parentProject == null)
      throw new RuntimeException("Parent pmcProject not found!");

    return dozerMapper.mapCollection(pmcProjectDao.getChildProjects(parentProject), PmcProjectDto.class);
  }

  @Override
  public List<PmcProjectDto> getChildProjects(Long pmcProjectGuid) {

    PmcProject parentProject = pmcProjectDao.getById(pmcProjectGuid);
    if (parentProject == null)
      throw new RuntimeException("Parent pmcProject not found!");

    return dozerMapper.mapCollection(pmcProjectDao.getChildProjects(parentProject), PmcProjectDto.class);
  }

  @Override
  public List<PmcProjectDto> getParentProjects(String child) {

    PmcProject childProject = pmcProjectDao.getByProjectId(child);
    if (childProject == null)
      throw new RuntimeException("Parent pmcProject not found!");

    return dozerMapper.mapCollection(pmcProjectDao.getParentProjects(childProject), PmcProjectDto.class);
  }

  @Override
  public PagedResultDto<Map<String, Object>> getProjectsElastic(Map<String, Object> filterParams,
                                                                Map<String, Object> sortParams,
                                                                int from,
                                                                int size) {

    List<Map<String, Object>> results = new ArrayList<>();
    PmcUserDto user = UserContext.getUser();
    List<PmcGroupDto> groups = user.getGroupList();
    List<String> groupIds = new ArrayList<>();
    for (PmcGroupDto group : groups) {
      groupIds.add(group.getGroupName());
    }
    filterParams.put("roles.raw", groupIds);

    SearchHits hits = elasticService.findProjects(filterParams, sortParams, from, size, MONITOR_FIELDS_INCLUDE, MONITOR_FIELDS_EXCLUDE);
    for (SearchHit hit : hits.getHits()) {
      Map<String, Object> project = (Map<String, Object>) hit.getSourceAsMap();//.get("project");
      results.add(project);
    }
    PagedResultDto<Map<String, Object>> res = new PagedResultDto<>();
    res.setResults(results);
    res.setTotal(hits.getTotalHits());
    return res;
  }

  private Map<String, Object> mapSearchHit(SearchHit hit, String[] fields) {
    Map<String, Object> result = new HashMap<>();
    for (String field : fields) {
      result.put(field, hit.field(field).getValue());
    }
    return result;
  }

  @Override
  public String createProjectPrefix(PmcProjectType projectType) {
    DateFormat df = new SimpleDateFormat("yy");
    String year = df.format(GregorianCalendar.getInstance().getTime());

    String pref = "P";
    if ("REQUEST".equals(projectType.getIdName())) {
      pref = "UR";
    }
    return pref + year + ".";
  }

  private String createProjectId(PmcProjectType projectType) {
    String praefix = createProjectPrefix(projectType);
    Long num = idCounterDao.getNext(praefix);
    return praefix.concat(StringUtils.leftPad(num.toString(), 6, "0"));
  }

  @Override
  public PmcProjectTypeDto getProjectType(String idName) {
    PmcProjectType type = this.pmcProjectTypeDao.getByIdName(idName);
    PmcProjectTypeDto dto = this.dozerMapper.map(type, PmcProjectTypeDto.class);
    mapProjectTypeDtoFields(dto);
    return dto;
  }

  @Override
  public List<PmcProjectTypeDto> searchProjectTypes(String searchTerm) {
    List<PmcProjectType> types = this.pmcProjectTypeDao.searchByName(searchTerm);

    List<PmcProjectTypeDto> dtos = this.dozerMapper.mapCollection(types, PmcProjectTypeDto.class);
    for (PmcProjectTypeDto pmcProjectTypeDto : dtos) {
      mapProjectTypeDtoFields(pmcProjectTypeDto);
    }
    return dtos;
  }

  @Override
  public List<PmcProjectTypeDto> searchChildProjectTypes(String searchTerm, Long projectParentGuid) {
    List<PmcProjectType> types = this.pmcProjectTypeDao.searchByNameAndParentType(searchTerm, projectParentGuid);
    List<PmcProjectTypeDto> dtos = this.dozerMapper.mapCollection(types, PmcProjectTypeDto.class);
    for (PmcProjectTypeDto pmcProjectTypeDto : dtos) {
      mapProjectTypeDtoFields(pmcProjectTypeDto);
    }
    return dtos;
  }

  @Override
  public List<PmcProjectTypeDto> getChildTypes(String idName) {
    return dozerMapper.mapCollection(pmcProjectTypeDao.getChildTypes(idName), PmcProjectTypeDto.class);
  }

  @Override
  public List<PmcProjectTypeDto> getParentTypes(String idName) {
    return dozerMapper.mapCollection(pmcProjectTypeDao.getParentTypes(idName), PmcProjectTypeDto.class);
  }

  @Override
  public PmcProjectDto updateProject(PmcProjectDto projectDto) {

    PmcProject project = getByGuid(projectDto.getGuid());

    if ( !project.getVersion().equals(projectDto.getVersion())) {
      throwWebAppException("PmcProject (guid=" +
        project.getGuid() +
        ") versions are not equal (" +
        project.getVersion() +
        " != " +
        projectDto.getVersion() +
        ")", "Can not update project due to a version conflict. Please reload page!");
    }

    updateAllowedCheck(project);

    Collection<PmcProjectListener> listener = getListener(project);
    for (PmcProjectListener l : listener) {
      l.beforeProjectUpdate(projectDto, project);
    }

    project.setComment(projectDto.getComment());
    project.setCustomerProjectId(projectDto.getCustomerProjectId());
    project.setDetails(projectDto.getDetails());
    project.setDueDate(projectDto.getDueDate());
    project.setOwnerId(projectDto.getOwnerId());
    project.setPriority(projectDto.getPriority());
    project.setRisk(projectDto.getRisk());
    project.setStatus(PmcProjectState.valueOf(projectDto.getStatus()));
    project.setTag(projectDto.getTag());
    project.setTitle(projectDto.getTitle());
    project.setGuStatus(projectDto.getGuStatus());
    pmcProjectDao.update(project);
    pmcProjectDao.getEntityManager().flush();
    PmcProjectDto result = mapProject(project, Mode.RELATIONIDS);
    this.elasticService.updateEntry(result, true);
    authorizationService.addOrUpdateBaseAuthKeyForProject(result);
    return result;
  }

  @Override
  public PmcProjectDto updateProjectGuStatus(long pmcProjectGuid, String guStatus, boolean callListener) {

    PmcProject project = getByGuid(pmcProjectGuid);
    updateAllowedCheck(project);
    if (callListener) {
      PmcProjectDto projectDto = dozerMapper.map(project, PmcProjectDto.class);
      projectDto.setGuStatus(guStatus);
      Collection<PmcProjectListener> listener = getListener(project);
      for (PmcProjectListener l : listener) {
        l.beforeProjectUpdate(projectDto, project);
      }
    }

    project.setGuStatus(guStatus);
    pmcProjectDao.getEntityManager().flush();

    PmcProjectDto result = mapProject(project, Mode.DEFAULT);
    this.elasticService.updateEntry(result, true);
    authorizationService.addOrUpdateBaseAuthKeyForProject(result);
    return result;

  }

  @Override
  public PmcProjectDto updateProjectRelations(PmcProjectDto project,
                                              String additionalChildren,
                                              String removeChildren,
                                              String additionalParents,
                                              String removeParents) {

    PmcProject p = pmcProjectDao.getById(project.getGuid());

    List<String> idStrings = new ArrayList<>();

    if ( !StringUtils.isEmpty(removeParents)) {
      Collection<String> pa = Arrays.asList(StringUtils.stripAll(removeParents.split(",")));
      for (String idName : pa) {
        if (idName.equals("*")) {
          for (PmcProject parent : p.getParents()) {
            idStrings.add(parent.getProjectId());
            parent.getChildren().remove(p);
          }
          p.getParents().clear();
          break;
        } else {
          idStrings.add(idName);
          PmcProject parent = pmcProjectDao.getByProjectId(idName);
          p.getParents().remove(parent);
          parent.getChildren().remove(p);
        }
      }
    }

    if ( !StringUtils.isEmpty(removeChildren)) {
      Collection<String> c = Arrays.asList(StringUtils.stripAll(removeChildren.split(",")));
      for (String idName : c) {
        if (idName.equals("*")) {
          for (PmcProject child : p.getChildren()) {
            idStrings.add(child.getProjectId());
            child.getParents().remove(p);
          }
          p.getChildren().clear();
          break;
        } else {
          idStrings.add(idName);
          PmcProject child = pmcProjectDao.getByProjectId(idName);
          p.getChildren().remove(child);
          child.getParents().remove(p);
        }
      }
    }

    if ( !StringUtils.isEmpty(additionalChildren)) {
      Collection<String> c = Arrays.asList(StringUtils.stripAll(additionalChildren.split(",")));
      for (String idName : c) {
        idStrings.add(idName);
        PmcProject child = pmcProjectDao.getByProjectId(idName);
        p.getChildren().add(child);
        child.getParents().add(p);
      }
    }

    if ( !StringUtils.isEmpty(additionalParents)) {
      Collection<String> pa = Arrays.asList(StringUtils.stripAll(additionalParents.split(",")));
      for (String idName : pa) {
        idStrings.add(idName);
        PmcProject parent = pmcProjectDao.getByProjectId(idName);
        p.getParents().add(parent);
        parent.getChildren().add(p);
      }
    }

    pmcProjectDao.getEntityManager().flush();
    for (String projectId : idStrings) {
      PmcProject pToUpdate = pmcProjectDao.getByProjectId(projectId);
      for (PmcProjectListener l : getListener(pToUpdate)) {
        l.afterRelationUpdate(pToUpdate);
      }
      PmcProjectDto pToUpdateDto = dozerMapper.map(pToUpdate, PmcProjectDto.class);
      mapRelationIds(pToUpdate, pToUpdateDto);
      elasticService.updateEntry(pToUpdateDto);
    }
    for (PmcProjectListener l : getListener(p)) {
      l.afterRelationUpdate(p);
    }
    PmcProjectDto pDto = dozerMapper.map(p, PmcProjectDto.class);
    mapRelationIds(p, pDto);
    elasticService.updateEntry(pDto);
    //relations should not influence key so no authService update should be necessary
    return pDto;
  }

  @Override
  public PmcProjectDto updateDueDate(long pmcProjectGuid, Date dueDate, boolean callListener) {

    PmcProject project = getByGuid(pmcProjectGuid);
    updateAllowedCheck(project);
    if (callListener) {
      PmcProjectDto projectDto = dozerMapper.map(project, PmcProjectDto.class);
      projectDto.setDueDate(dueDate);
      Collection<PmcProjectListener> listener = getListener(project);
      for (PmcProjectListener l : listener) {
        l.beforeProjectUpdate(projectDto, project);
      }
    }

    project.setDueDate(dueDate);
    pmcProjectDao.getEntityManager().flush();

    PmcProjectDto result = dozerMapper.map(project, PmcProjectDto.class);
    mapProjectTypeDtoFields(result.getPmcProjectType());
    this.elasticService.updateEntry(result, true);
    //dueDate should not influence key so no authService update should be necessary
    return result;
  }

  private void mapProjectTypeDtoFields(PmcProjectTypeDto dto) {

    try {
      dto.setReadRightGroups(parseStringToStringList(dto.getReadRights()));
    } catch (Exception ex) {
      logger.warn("PmcProjectTypeDto #" + dto.getGuid() + " -- invalid readRights definition: " + dto.getReadRights());
    }
    try {
      dto.setWriteRightGroups(parseStringToStringList(dto.getWriteRights()));
    } catch (Exception ex) {
      logger.warn("PmcProjectTypeDto #" + dto.getGuid() + " -- invalid writeRights definition: " + dto.getWriteRights());
    }
    try {
      dto.setCreateRightGroups(parseStringToStringList(dto.getCreateRights()));
    } catch (Exception ex) {
      logger.warn("PmcProjectTypeDto #" + dto.getGuid() + " -- invalid createRights definition: " + dto.getCreateRights());
    }
    try {
      dto.setDeleteRightGroups(parseStringToStringList(dto.getDeleteRights()));
    } catch (Exception ex) {
      logger.warn("PmcProjectTypeDto #" + dto.getGuid() + " -- invalid deleteRights definition: " + dto.getDeleteRights());
    }
    dto.setAllowedTagList(parseStringToStringList(dto.getAllowedTags()));
    dto.setAllowedSubtypeList(parseStringToStringList(dto.getAllowedSubtypes()));

    List<String> procDefKeys = parseStringToStringList(dto.getAllowedProcesses());
    List<ProcessDefinitionDto> procDefDtoList = new ArrayList<>(procDefKeys.size());
    for (String procDefKey : procDefKeys) {
      ProcessDefinitionDto pd = pmcProcessService.getProcessDefinition(procDefKey);
      if (pd == null) {
        continue;
      }
      procDefDtoList.add(pd);
    }
    dto.setProcessDefinitions(procDefDtoList);
  }

  private void mapRelationIds(PmcProject pmcProject, PmcProjectDto dto) {
    if (pmcProject.getParents() != null) {
      dto.setParentIds(new ArrayList<Long>());
      for (PmcProject parent : pmcProject.getParents()) {
        dto.getParentIds().add(parent.getGuid());
      }
    }
    if (pmcProject.getChildren() != null) {
      dto.setChildrenIds(new ArrayList<Long>());
      for (PmcProject child : pmcProject.getChildren()) {
        dto.getChildrenIds().add(child.getGuid());
      }
    }
    ProjectTypeElasticListener listener = elasticListeners.get(pmcProject.getPmcProjectType().getIdName());
    if (listener != null) {
      dto.setAdditionalInfo(listener.getAdditionalInfo(pmcProject));
    }
  }

  private List<String> parseStringToStringList(String input) {
    if (org.apache.commons.lang3.StringUtils.isEmpty(input))
      return Collections.emptyList();
    String[] splitted = input.split(",");
    splitted = StringUtils.stripAll(splitted);
    return Arrays.asList(splitted);

  }

  @Override
  public Collection<PmcProjectListener> getListener(PmcProject p) {
    return getListener(p.getPmcProjectType());
  }

  private Collection<PmcProjectListener> getListener(PmcProjectType pt) {
    Collection<PmcProjectListener> listener = new ArrayList<>();
    if (pt.getListeners() != null) {
      for (String idName : pt.getListeners()) {
        if (startListenerMap.containsKey(idName)) {
          listener.add(startListenerMap.get(idName));
        } else {
          //logger.warn("No project listener with id '" + idName + "' found!");
        }
      }
    }
    return listener;
  }

  @Override
  public List<InternalContactDto> getContacts(Long pmcProjectGuid) {
    List<PmcPropertyDto> props = pmcPropertyService.searchProperties(null, null, pmcProjectGuid, "role%");
    List<InternalContactDto> retVal = new ArrayList<>();
    for (PmcPropertyDto prop : props) {
      if (prop.getKey().startsWith("role_")) {
        String role = prop.getKey().substring(5);
        if (pmcGroupService.getGroupByGroupName(role) == null) {
          continue;
        }
        Long roleValue = Long.valueOf(prop.getValue());
        PmcUserDto u = this.userCache.get(roleValue);
        if (u != null) {
          InternalContactDto dto = createDto(u, role);
          retVal.add(dto);
          PmcRoleDto roleDto = pmcRoleService.getRoleForRoleConsumer(roleValue, role);
          if (roleDto != null) {
            for (PmcPropertyDto pmProp : props) {
              if (pmProp.getKey().equals("role_" + roleDto.getIdName())) {
                PmcUserDto uPm = this.userCache.get(Long.valueOf(pmProp.getValue()));
                if (uPm != null) {
                  InternalContactDto pmDto = createDto(uPm, "pm_" + prop.getKey().substring(5));
                  retVal.add(pmDto);
                }
              }
            }
          }
        }
      }
    }
    return retVal;
  }

  private InternalContactDto createDto(PmcUserDto u, String role) {
    InternalContactDto dto = new InternalContactDto();
    dto.setGuid(u.getGuid());
    dto.setRoleName(role);
    dto.setFirstName(u.getFirstName());
    dto.setSurName(u.getSurname());
    dto.setEmail(u.getEmail());
    dto.setUserName(u.getLoginName());
    dto.setFullName(u.getFirstName() + " " + u.getSurname());
    return dto;
  }

  @Override
  public PmcUserDto getUserForRole(
      String refObjectType, Long refObjectId, String roleName, Long pmcProjectGuid) {
    Long userId = null;

    if (pmcProjectGuid != null) {
      String value = pmcPropertyService.getStringProperty(null, null, pmcProjectGuid, "role_" + roleName);
      if (value != null) {
        userId = Long.valueOf(value);
        return this.userCache.get(userId);
      }
    }

    if (userId == null) {
      PmcRefObjectConnector con = this.refObjectLoaders.get(refObjectType);
      if (con != null) {
        return con.getUserForRole(refObjectId, roleName);
      }
    }
    return null;
  }

  @Override
  public void projectFinished(Long pmcProjectGuid) {
    PmcProject p = getByGuid(pmcProjectGuid);
    logger.info("Setting Project " + p.getProjectId() + " to completed. Current State is " + p.getStatus());
    updateAllowedCheck(p);

    p.setStatus(PmcProjectState.COMPLETED);

    PmcProjectProcess projectProcess = null;
    for (PmcProjectProcess process : p.getProcesses()) {
      if (process.getRelation() == ProcessRelation.MAIN) {
        projectProcess = process;
      }
    }
    if (projectProcess != null) {
      projectProcess.setStatus(ProcessState.COMPLETED);
    }

    Collection<PmcProjectListener> listener = getListener(p);
    for (PmcProjectListener l : listener) {
      l.afterProjectFinished(p);
    }

    
    PmcProjectDto result = mapProject(p, Mode.RELATIONIDS);
    this.elasticService.updateEntry(result);
    authorizationService.addOrUpdateBaseAuthKeyForProject(result);
  }

  @Override
  public PmcRefObjectConnector getRefObjectLoader(String refObjectType) {
    return refObjectLoaders.get(refObjectType);
  }

  // project phase methods

  @Override
  public List<PmcProjectPhaseDto> getPhasesForProject(Long projectGuid) {
    PmcProject p = pmcProjectDao.getById(projectGuid);
    return dozerMapper.mapCollection(p.getPhases(), PmcProjectPhaseDto.class);
  }

  @Override
  public PmcProjectPhaseDto getPhase(Long projectGuid, String phaseDefIdName) {
    return dozerMapper.map(pmcProjectPhaseDao.getByProjectIdAndPhaseDefIdName(projectGuid, phaseDefIdName), PmcProjectPhaseDto.class);
  }

  @Override
  public void initPhasesForProject(String projectId, Collection<PmcPropertyDto> properties) {
    if (properties == null)
      properties = Collections.emptyList();
    PmcProject p = pmcProjectDao.getByProjectId(projectId);
    Collection<PmcProjectPhase> phases = p.getPhases();
    if (phases == null)
      phases = Collections.emptyList();

    HashSet<String> projectPhaseDefIds = new HashSet<>();
    for (PmcProjectPhase phase : phases) {
      projectPhaseDefIds.add(phase.getPmcProjectPhaseDefinition().getIdName());
    }

    for (PmcProjectPhaseDefinition def : p.getPmcProjectType().getPhases()) {
      if ( !projectPhaseDefIds.contains(def.getIdName())) {
        PmcPropertyDto dto = findProperty("ENABLE_PHASE_" + def.getIdName(), properties);

        Boolean enabled = dto != null ? Boolean.valueOf(dto.getValue()) : null;

        PmcProjectPhase newPhase = new PmcProjectPhase(p, def, enabled);

        pmcProjectPhaseDao.create(newPhase);
      }
    }

    pmcProjectDao.getEntityManager().refresh(p);
    pmcProjectDao.getEntityManager().flush();

  }

  @Override
  public void initPhasesForProjects() {

    for (PmcProject p : pmcProjectDao.getAll()) {
      initPhasesForProject(p.getProjectId(), null);
    }
  }

  @Override
  public void enablePhase(String projectId, String projectPhaseDefinitionIdName) {
    PmcProjectPhase phase = getPhase(projectId, projectPhaseDefinitionIdName);
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    phase.setEnabled(true);
    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }
    pmcProjectPhaseDao.getEntityManager().flush();
  }

  @Override
  public void disablePhase(String projectId, String projectPhaseDefinitionIdName) {
    PmcProjectPhase phase = getPhase(projectId, projectPhaseDefinitionIdName);
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    String oldValue = phase.getEnabledAsString();
    phase.setEnabled(false);
    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }
    pmcProjectPhaseDao.getEntityManager().flush();
    String newValue = phase.getEnabledAsString();
    if ( !Objects.equals(oldValue, newValue)) {
      pmcHistoryService.createHistory(PmcHistoryLogDto.createForPhase(phase, "enabled").withChange(Action.EDIT, null, oldValue, newValue));
    }
  }

  @Override
  public void freezePhase(String projectId, String projectPhaseDefinitionIdName) {
    PmcProjectPhase phase = getPhase(projectId, projectPhaseDefinitionIdName);
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    String oldValue = phase.getFrozenAsString();
    phase.setFrozen(true);
    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }
    pmcProjectPhaseDao.getEntityManager().flush();
    String newValue = phase.getFrozenAsString();
    if ( !Objects.equals(oldValue, newValue)) {
      pmcHistoryService.createHistory(PmcHistoryLogDto.createForPhase(phase, "frozen").withChange(Action.EDIT, null, oldValue, newValue));
    }
  }

  @Override
  public void activatePhase(String projectId, String projectPhaseDefinitionIdName) {
    PmcProjectPhase phase = getPhase(projectId, projectPhaseDefinitionIdName);
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    String oldValue = phase.getActiveAsString();
    phase.setActive(true);
    if (phase.getEnabled() == null || phase.getEnabled() == false) {
      phase.setEnabled(true);
    }
    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }
    authorizationService.invalidateAuthKey(phase.getPmcProject().getGuid());
    pmcProjectPhaseDao.getEntityManager().flush();
    String newValue = phase.getActiveAsString();
    if ( !Objects.equals(newValue, oldValue)) {
      pmcHistoryService.createHistory(PmcHistoryLogDto.createForPhase(phase, "active").withChange(Action.EDIT, null, oldValue, newValue));
    }
  }

  @Override
  public void deactivatePhase(String projectId, String projectPhaseDefinitionIdName) {
    PmcProjectPhase phase = getPhase(projectId, projectPhaseDefinitionIdName);
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    String oldValue = phase.getActiveAsString();
    phase.setActive(false);
    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }
    authorizationService.invalidateAuthKey(phase.getPmcProject().getGuid());
    pmcProjectPhaseDao.getEntityManager().flush();
    String newValue = phase.getActiveAsString();
    if ( !Objects.equals(newValue, oldValue)) {
      pmcHistoryService.createHistory(PmcHistoryLogDto.createForPhase(phase, "active").withChange(Action.EDIT, null, oldValue, newValue));
    }
  }

  @Override
  public PmcProjectPhaseDto updatePhase(PmcProjectPhaseDto phaseDto) {
    PmcProjectPhase phase = pmcProjectPhaseDao.getById(phaseDto.getGuid());
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    phase.setActive(phaseDto.getActive());
    phase.setEnabled(phaseDto.getEnabled());
    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }
    return phaseDto;
  }

  @Override
  public PmcProjectPhaseDto updatePhase(Long pmcProjectGuid, String phaseDefinitionIdName, PmcProjectPhaseDto updateDto) {
    return updatePhase(pmcProjectGuid, phaseDefinitionIdName, updateDto, false);
  }

  @Override
  public PmcProjectPhaseDto updatePhase(Long pmcProjectGuid,
                                        String phaseDefinitionIdName,
                                        PmcProjectPhaseDto updateDto,
                                        boolean setAutoDate) {
    PmcProjectPhase phase = pmcProjectPhaseDao.getByProjectIdAndPhaseDefIdName(pmcProjectGuid, phaseDefinitionIdName);
    if (phase == null)
      throw new IllegalStateException("no project phase found for projectGuid=" + pmcProjectGuid + " phaseIdName=" + phaseDefinitionIdName);
    PmcProjectPhaseDto oldPhase = dozerMapper.map(phase, PmcProjectPhaseDto.class);
    if (updateDto.getEnabled() != null) {
      String oldValue = phase.getEnabledAsString();
      phase.setEnabled(updateDto.getEnabled());
      pmcHistoryService.createHistory(PmcHistoryLogDto.createForPhase(phase, "enabled")
                                                      .withChange(Action.EDIT, null, oldValue, phase.getEnabledAsString()));
    }
    if (updateDto.getActive() != null) {
      phase.setActive(updateDto.getActive());
      if (setAutoDate) {
        Date now = new Date();
        if (updateDto.getActive() && (phase.getStartDate() == null || now.before(phase.getStartDate()))) {
          phase.setStartDate(now);
        } else if ( !updateDto.getActive() && (phase.getEndDate() == null || now.after(phase.getEndDate()))) {
          phase.setEndDate(now);
        }
      }
    }
    if (updateDto.getStartDate() != null)
      phase.setStartDate(updateDto.getStartDate());
    if (updateDto.getEndDate() != null)
      phase.setEndDate(updateDto.getEndDate());

    for (PmcProjectListener l : getListener(phase.getPmcProject())) {
      l.afterPhaseUpdate(oldPhase, phase);
    }

    pmcProjectPhaseDao.update(phase);
    elasticService.updateEntry(this.getProjectByGuid(pmcProjectGuid, Mode.RELATIONIDS));
    authorizationService.invalidateAuthKey(pmcProjectGuid);
    return dozerMapper.map(phase, PmcProjectPhaseDto.class);
  }

  private PmcProjectPhase getPhase(String projectId, String projectPhaseDefinitionIdName) {
    PmcProjectPhase phase = pmcProjectPhaseDao.getByProjectIdAndPhaseDefIdName(projectId, projectPhaseDefinitionIdName);
    if (phase == null)
      throw new IllegalStateException("no phase found for project " + projectId + " and phase definition " + projectPhaseDefinitionIdName);
    return phase;
  }

  @Override
  public List<MilestoneDto> listAllMilestones() {
    return dozerMapper.mapCollection(milestoneDao.getAll(), MilestoneDto.class);
  }

  @Override
  public MilestoneDto getMileById(Long guid) {
    logger.info("Milestone Guid: " + guid);
    Milestone milestone = milestoneDao.getById(guid);
    logger.info("milestone: " + milestone);
    logger.info("milestone: " + milestone.getGuid());
    logger.info("milestone: " + milestone.getDescription());
    return dozerMapper.map(milestone, MilestoneDto.class);
  }

  @Override
  public Long createMilestone(MilestoneDto milestoneDto) {
    Milestone mile = milestoneDao.create(dozerMapper.map(milestoneDto, Milestone.class));
    return mile.getGuid();
  }

  @Override
  public Response updateMilestone(MilestoneDto milestoneDto) {
    milestoneDao.update(dozerMapper.map(milestoneDto, Milestone.class));
    return Response.ok().build();
  }

  @Override
  public Response deleteMilestone(Long guid) {
    PmcProjectMilestone milestoneForDeleting = pmcProjectMilestoneDao.getById(guid);
    pmcProjectMilestoneDao.delete(milestoneForDeleting);
    return Response.ok().build();
  }

  @Override
  public List<PmcProjectMilestoneDto> getMilestonesPerProject(Long pmcProjectGuid) {
    return dozerMapper.mapCollection(pmcProjectMilestoneDao.getMilestonesPerProject(pmcProjectGuid), PmcProjectMilestoneDto.class);
  }

  @Override
  public List<SimpleMilestoneDto> getSimpleMilestonesPerProject(Long pmcProjectGuid) {
    List<PmcProjectMilestone> pmsList = pmcProjectMilestoneDao.getMilestonesPerProject(pmcProjectGuid);
    List<SimpleMilestoneDto> msList = new ArrayList<>();
    for (PmcProjectMilestone pms : pmsList) {
      msList.add(new SimpleMilestoneDto(pms.getMilestone().getIdName(), pms.getFc(), pms.getBaseline(), pms.getAct()));
    }
    return msList;
  }

  @Override
  public PmcProjectMilestoneDto getProjectMilestoneById(Long guid) {
    return dozerMapper.map(pmcProjectMilestoneDao.getById(guid), PmcProjectMilestoneDto.class);
  }

  @Override
  public PmcProjectMilestoneDto getProjectMilestoneByProjectAndMsIdName(String projectId, String msIdName) {
    return dozerMapper.map(pmcProjectMilestoneDao.getByProjectAndIdName(projectId, msIdName), PmcProjectMilestoneDto.class);
  }

  @Override
  public PmcProjectMilestoneDto getProjectMilestoneByProjectAndMsIdName(Long pmcProjectGuid, String msIdName) {
    return dozerMapper.map(pmcProjectMilestoneDao.getByProjectAndIdName(pmcProjectGuid, msIdName), PmcProjectMilestoneDto.class);
  }

  @Override
  public PmcProjectTypeDto getMilestoneDefinitionsPerProjectType(Long projectTypeId) {
    return dozerMapper.map(pmcProjectTypeDao.getById(projectTypeId), PmcProjectTypeDto.class);
  }

  @Override
  public Long createProjectMilestone(Long projectGuid, String milestoneIdName, Date fc, Date act, Date baseline, String reason) {
    PmcProject project = pmcProjectDao.getById(projectGuid);
    Milestone milestone = milestoneDao.getMilestoneByIdName(milestoneIdName);

    PmcProjectMilestoneDto dto = new PmcProjectMilestoneDto();
    dto.setMilestone(dozerMapper.map(milestone, MilestoneDto.class));
    dto.setProject(dozerMapper.map(project, PmcProjectCompactDto.class));
    dto.setFc(fc);
    dto.setAct(act);
    dto.setBaseline(baseline);
    dto.setReason(reason);

    Collection<PmcProjectListener> listener = getListener(project);
    for (PmcProjectListener l : listener) {
      l.beforeMilestoneCreation(dto);
    }

    PmcProjectMilestone projectMilestone = new PmcProjectMilestone();
    projectMilestone.setProject(project);
    projectMilestone.setMilestone(milestone);
    projectMilestone.setAct(dto.getAct());
    projectMilestone.setFc(dto.getFc());
    projectMilestone.setBaseline(dto.getBaseline());

    PmcProjectMilestone ppm = pmcProjectMilestoneDao.create(projectMilestone);
    return ppm.getGuid();
  }

  @Override
  public PmcProjectMilestoneDto updateProjectMilestone(String projectId, String msIdName, Date fc, Date act, String reason) {
    PmcProject p = pmcProjectDao.getByProjectId(projectId);
    return updateProjectMilestone(p, msIdName, fc, false, act, false, reason);
  }

  @Override
  public PmcProjectMilestoneDto updateProjectMilestone(Long projectGuid,
                                                       String msIdName,
                                                       Date fc,
                                                       boolean clearFc,
                                                       Date act,
                                                       boolean clearAct,
                                                       String reason) {
    PmcProject p = pmcProjectDao.getById(projectGuid);
    return updateProjectMilestone(p, msIdName, fc, clearFc, act, clearAct, reason);
  }

  @Override
  public PmcProjectMilestoneDto updateProjectMilestone(Long projectGuid, String msIdName, Date fc, Date act, String reason) {
    PmcProject p = pmcProjectDao.getById(projectGuid);
    return updateProjectMilestone(p, msIdName, fc, false, act, false, reason);
  }

  private PmcProjectMilestoneDto updateProjectMilestone(PmcProject p,
                                                        String msIdName,
                                                        Date fc,
                                                        boolean clearFc,
                                                        Date act,
                                                        boolean clearAct,
                                                        String reason) {
    PmcProjectMilestone ms = pmcProjectMilestoneDao.getByProjectAndIdName(p.getGuid(), msIdName);
    Long msGuid;
    if (ms == null) {
      msGuid = createProjectMilestone(p.getGuid(), msIdName, fc, act, act, reason);
    } else {
      msGuid = ms.getGuid();
    }
    return updateProjectMilestone(msGuid, fc, clearFc, act, clearAct, reason);
  }

  @Override
  public PmcProjectMilestoneDto updateProjectMilestone(Long projectMilestoneGuid,
                                                       Date fc,
                                                       boolean clearFc,
                                                       Date act,
                                                       boolean clearAct,
                                                       String reason) {

    SimpleDateFormat dateFormat = new SimpleDateFormat(MS_LOG_DATE_FORMAT);

    PmcProjectMilestone ms = pmcProjectMilestoneDao.getById(projectMilestoneGuid);
    Collection<PmcProjectListener> listener = getListener(ms.getProject());
    for (PmcProjectListener l : listener) {
      l.beforeMilestoneUpgrade(dozerMapper.map(ms, PmcProjectMilestoneDto.class),
                               fc,
                               act,
                               reason,
                               dozerMapper.map(ms.getProject(), PmcProjectDto.class));
    }

    if ((fc != null && !fc.equals(ms.getFc())) || (fc == null && clearFc)) {
      String oldFcValue = null;
      if (ms.getFc() != null)
        oldFcValue = dateFormat.format(ms.getFc());
      String newFcValue = "";
      if (fc != null) {
        newFcValue = dateFormat.format(fc);
      }
      ms.setFc(fc);
      addMsHistoryEntry(ms, reason, "fc", newFcValue, oldFcValue);
    }

    if ((act != null && !act.equals(ms.getAct())) || (act == null && clearAct)) {
      String oldActValue = null;
      if (ms.getAct() != null) {
        oldActValue = dateFormat.format(ms.getAct());
      }
      String newActValue = "";
      if (act != null) {
        newActValue = dateFormat.format(act);
      }
      ms.setAct(act);
      addMsHistoryEntry(ms, reason, "act", newActValue, oldActValue);
    }
    pmcProjectMilestoneDao.update(ms);
    elasticService.updateEntry(mapProject(ms.getProject(), Mode.RELATIONIDS));
    return dozerMapper.map(ms, PmcProjectMilestoneDto.class);
  }

  private void addMsHistoryEntry(PmcProjectMilestone ms, String reason, String type, String newValue, String oldValue) {
    pmcHistoryService.createHistory(PmcHistoryLogDto.createForMilestone(ms, type).withChange(Action.EDIT, reason, oldValue, newValue));
  }

  @Override
  public Boolean isPhaseEnabled(String projectId, String phaseDefinitionIdName) {
    PmcProjectPhase phase = getPhase(projectId, phaseDefinitionIdName);
    return phase.getEnabled();
  }

  @Override
  public PmcProjectDto getProjectForProcess(Long projectProcessGuid) {
    PmcProjectProcess ppp = pmcProjectProcessDao.getById(projectProcessGuid);
    PmcProject p = ppp.getPmcProject();
    return dozerMapper.map(p, PmcProjectDto.class);
  }

  @Override
  public PmcProjectProcessDto getProjectProcess(Long projectProcessGuid) {
    PmcProjectProcess ppp = pmcProjectProcessDao.getById(projectProcessGuid);
    return dozerMapper.map(ppp, PmcProjectProcessDto.class);
  }

  @Override
  public boolean isMainProjectForCustomerProject(PmcProjectDto p) {
    if (p == null || p.getCustomerProjectId() == null) {
      return false;
    }
    List<PmcProject> projects = pmcProjectDao.getByCustomerProjectId(p.getCustomerProjectId());
    PmcProject oldest = null;
    for (PmcProject project : projects) {
      if (oldest == null || project.getCreateDate().before(oldest.getCreateDate())) {
        oldest = project;
      }
    }
    return (oldest != null && oldest.getProjectId().equals(p.getProjectId()));
  }

  PmcProject getByGuid(Long guid) {
    PmcProject project = pmcProjectDao.getById(guid);
    if (project == null) {
      throw new IllegalArgumentException("no project with guid " + guid + " found");
    }
    return project;
  }

  void updateAllowedCheck(PmcProject project) {
    if (project.getStatus().equals(PmcProjectState.CANCELED) || project.getStatus().equals(PmcProjectState.COMPLETED)) {
      throwWebAppException("not allowed to update PmcProject (guid=" + project.getGuid() + ") in state " + project.getStatus(),
                           "Not allowed to update project in state " + project.getStatus());
    }
  }

  private void throwWebAppException(String logMessage, String responseMessage) {
    logger.error(logMessage);
    throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                              .entity(responseMessage)
                                              .type(MediaType.TEXT_PLAIN)
                                              .build());
  }

  /**
   * Returns a list of projectCards for the given projectId and listName. If no cards can be found a new list is created
   * if allowed for this projectType.
   * 
   * @param projectId
   * @param listName
   * @param createIfEmpty
   * @return List of projectCards
   * @throws Exception if a new list cannot be created for the projectType of the given project
   */
  @Override
  public List<PmcProjectCardDto> getCardListForProject(String projectId, String listName, boolean createIfEmpty) throws Exception {
    List<PmcProjectCard> cards = pmcProjectCardDao.getByProjectIdandListName(projectId, listName);
    if (cards.isEmpty() && createIfEmpty) {
      PmcProject project = pmcProjectDao.getByProjectId(projectId);
      boolean validListType = false;
      for (CardList cardList : project.getPmcProjectType().getCardLists()) {
        if (cardList.getIdName().equals(listName)) {
          validListType = true;
          for (Card card : cardList.getCards()) {
            PmcProjectCard newCard = new PmcProjectCard();
            newCard.setCard(card);
            newCard.setCardList(cardList);
            newCard.setProject(project);
            pmcProjectCardDao.create(newCard);
            cards.add(newCard);
          }
        }
      }
      if ( !validListType) {
        throw new Exception("CardList " + listName + " not allowed for projectType " + project.getPmcProjectType().getName());
      }
    }
    List<PmcProjectCardDto> ret = new ArrayList<>();
    for (PmcProjectCard card : cards) {
      PmcProjectCardDto ppcd = dozerMapper.map(card, PmcProjectCardDto.class);
      if (card.getUpdateUser() != null) {
        PmcUserDto updateUser = userCache.get(card.getUpdateUser());
        if (updateUser != null) {
          ppcd.setUpdateUser(updateUser.getDisplayName());
        }
      } else {
        ppcd.setUpdateUser("");
      }
      if (card.getCreateUser() != null) {
        PmcUserDto createUser = userCache.get(card.getCreateUser());
        if (createUser != null) {
          ppcd.setCreateUser(createUser.getDisplayName());
        }
      } else {
        ppcd.setCreateUser("");
      }
      ret.add(ppcd);
    }
    return ret;
  }

  @Override
  public PmcProjectCardDto addCardForProject(Long projectGuid, String listName) throws Exception {
    PmcProject project = pmcProjectDao.getById(projectGuid);
    for (CardList cardList : project.getPmcProjectType().getCardLists()) {
      if (cardList.getIdName().equals(listName)) {
        Card card = new Card();
        cardDao.create(card);
        cardDao.getEntityManager().flush();

        PmcProjectCard newCard = new PmcProjectCard();
        newCard.setCard(card);
        newCard.setCardList(cardList);
        newCard.setProject(project);
        newCard.setCreateUser(UserContext.getUser().getGuid());
        pmcProjectCardDao.create(newCard);

        PmcProjectCardDto ppcd = dozerMapper.map(newCard, PmcProjectCardDto.class);
        PmcUserDto updateUser = userCache.get(UserContext.getUser().getGuid());
        ppcd.setUpdateUser(updateUser.getDisplayName());
        ppcd.setCreateUser(updateUser.getDisplayName());
        return ppcd;
      }
    }
    throw new Exception("CardList " + listName + " not allowed for projectType " + project.getPmcProjectType().getName());
  }

  @Override
  public PmcProjectCardDto updateCardTitle(Long projectGuid, Long cardId, String title) throws Exception {
    PmcProjectCard pc = pmcProjectCardDao.getById(cardId);
    String message = null;
    if (pc != null) {
      if (projectGuid.equals(pc.getProject().getGuid())) {
        pc.getCard().setTitle(title);
        pmcProjectCardDao.update(pc);
      } else {
        message = "Card does not belong to project!";
      }
    } else {
      message = "Card not found for project!";
    }
    if (message != null) {
      throw new Exception(message);
    }
    PmcProjectCardDto ppcd = dozerMapper.map(pc, PmcProjectCardDto.class);
    PmcUserDto updateUser = userCache.get(UserContext.getUser().getGuid());
    if (updateUser != null) {
      ppcd.setUpdateUser(updateUser.getDisplayName());
    }
    return ppcd;
  }

  @Override
  public PmcProjectCardDto updateCardComment(Long projectGuid, Long cardId, String comment) throws Exception {
    PmcProjectCard pc = pmcProjectCardDao.getById(cardId);
    String message = null;
    if (pc != null) {
      if (projectGuid.equals(pc.getProject().getGuid())) {
        pc.setComments(comment);
        pmcProjectCardDao.update(pc);
      } else {
        message = "Card does not belong to project!";
      }
    } else {
      message = "Card not found for project!";
    }
    if (message != null) {
      throw new Exception(message);
    }
    PmcProjectCardDto ppcd = dozerMapper.map(pc, PmcProjectCardDto.class);
    PmcUserDto updateUser = userCache.get(UserContext.getUser().getGuid());
    if (updateUser != null) {
      ppcd.setUpdateUser(updateUser.getDisplayName());
    }
    return ppcd;
  }

  @Override
  public PmcProjectCardDto updateCardStatus(Long projectGuid, Long cardId, String status) throws Exception {
    PmcProjectCard pc = pmcProjectCardDao.getById(cardId);
    String message = null;
    if (pc != null) {
      if (projectGuid.equals(pc.getProject().getGuid())) {
        pc.setStatus(status);
        pmcProjectCardDao.update(pc);
      } else {
        message = "Card does not belong to project!";
      }
    } else {
      message = "Card not found for project!";
    }
    if (message != null) {
      throw new Exception(message);
    }
    PmcProjectCardDto ppcd = dozerMapper.map(pc, PmcProjectCardDto.class);
    PmcUserDto updateUser = userCache.get(UserContext.getUser().getGuid());
    if (updateUser != null) {
      ppcd.setUpdateUser(updateUser.getDisplayName());
    }
    return ppcd;
  }

  @Override
  public PmcProjectCardDto updateCardCategory(Long projectGuid, Long cardId, String category) throws Exception {
    PmcProjectCard pc = pmcProjectCardDao.getById(cardId);
    String message = null;
    if (pc != null) {
      if (projectGuid.equals(pc.getProject().getGuid())) {
        pc.setCategory(category);
        pmcProjectCardDao.update(pc);
        Collection<PmcProjectListener> listener = getListener(pc.getProject());
        for (PmcProjectListener l : listener) {
          l.afterCardUpdate(pc.getProject(), pc);
        }
      } else {
        message = "Card does not belong to project!";
      }
    } else {
      message = "Card not found for project!";
    }
    if (message != null) {
      throw new Exception(message);
    }
    PmcProjectCardDto ppcd = dozerMapper.map(pc, PmcProjectCardDto.class);
    PmcUserDto updateUser = userCache.get(UserContext.getUser().getGuid());
    if (updateUser != null) {
      ppcd.setUpdateUser(updateUser.getDisplayName());
    }
    return ppcd;
  }

  @Override
  public PmcProjectCardDto updateCardOwner(Long projectGuid, Long cardId, String owner) throws Exception {
    PmcProjectCard pc = pmcProjectCardDao.getById(cardId);
    String message = null;
    if (pc != null) {
      if (projectGuid.equals(pc.getProject().getGuid())) {
        pc.setOwner(owner);
        pmcProjectCardDao.update(pc);
      } else {
        message = "Card does not belong to project!";
      }
    } else {
      message = "Card not found for project!";
    }
    if (message != null) {
      throw new Exception(message);
    }
    PmcProjectCardDto ppcd = dozerMapper.map(pc, PmcProjectCardDto.class);
    PmcUserDto updateUser = userCache.get(UserContext.getUser().getGuid());
    if (updateUser != null) {
      ppcd.setUpdateUser(updateUser.getDisplayName());
    }
    return ppcd;
  }

  @Override
  public PmcProjectMilestoneDto getMilestoneByIdNameAndAct(String idName, Date act) {
    return dozerMapper.map(pmcProjectMilestoneDao.getMilestoneByIdNameAndAct(idName, act), PmcProjectMilestoneDto.class);
  }

  @Override
  public PmcProjectDto synchProject(Long guid) {
    PmcProject project = getByGuid(guid);

    PmcProjectDto result = mapProject(project, Mode.RELATIONIDS);
    this.elasticService.updateEntry(result, true);
    authorizationService.addOrUpdateBaseAuthKeyForProject(result);
    return result;
  }

  private void addMetaInfo(PmcProjectDto project, Set<Long> doneSet) {
    if (doneSet == null)
      doneSet = new HashSet<>();
    if (doneSet.contains(project.getGuid()))
      return;
    doneSet.add(project.getGuid());
    for (PmcProjectListener l : getListener(pmcProjectTypeDao.getById(project.getPmcProjectType().getGuid()))) {
      l.addMetaInfo(project);
      doneSet.add(project.getGuid());
      if (project.getChildren() != null) {
        for (PmcProjectDto child : project.getChildren()) {
          addMetaInfo(child, doneSet);
        }
      }
      if (project.getParents() != null) {
        for (PmcProjectDto parent : project.getParents()) {
          addMetaInfo(parent, doneSet);
        }
      }
    }
  }
}
