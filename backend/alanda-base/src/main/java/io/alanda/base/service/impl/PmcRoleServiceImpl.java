package io.alanda.base.service.impl;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.connector.PmcRoleConnector;
import io.alanda.base.dao.PmcGroupDao;
import io.alanda.base.dao.PmcPermissionDao;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dao.PmcProjectTypeDao;
import io.alanda.base.dao.PmcRoleDao;
import io.alanda.base.dao.PmcUserDao;
import io.alanda.base.dao.PmcUserRepo;
import io.alanda.base.dto.PmcFuRoleInstanceDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;
import io.alanda.base.entity.PmcGroup;
import io.alanda.base.entity.PmcPermission;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectType;
import io.alanda.base.entity.PmcRole;
import io.alanda.base.entity.PmcUser;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcProjectService.Mode;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcRoleService;
import io.alanda.base.type.PmcProjectTypeConfiguration;
import io.alanda.base.util.DozerMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class PmcRoleServiceImpl implements PmcRoleService {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject private PmcProjectDao pmcProjectDao;

  @Inject private PmcProjectService pmcProjectService;

  @Inject private PmcProjectTypeDao pmcProjectTypeDao;

  @Inject private Instance<PmcRoleConnector> pmcRoleConnectors;

  private PmcRoleConnector pmcRoleConnector;

  @Inject private DozerMapper dozerMapper;

  @Inject private PmcGroupDao pmcGroupDao;

  @Inject private PmcRoleDao pmcRoleDao;

  @Inject private PmcPermissionDao pmcPermissionDao;

  @Inject private ElasticService elasticService;

  @Inject private PmcPropertyService pmcPropertyService;

  @Inject private PmcUserRepo pmcUserRepo;

  @Inject private PmcUserDao pmcUserDao;

  @Inject private Instance<PmcProjectListener> pmcProjectListener;

  private Map<String, PmcProjectListener> startListenerMap;

  @PostConstruct
  private void initPmcRoleService() {
    startListenerMap = new HashMap<>();
    for (PmcProjectListener listener : pmcProjectListener) {
      logger.info(
          "Found PmcProjectStartListener with idName " + listener.getListenerIdName() + ".");
      startListenerMap.put(listener.getListenerIdName(), listener);
    }
  }

  private void determineInjectedBeans() {
    PmcRoleConnector nonDefaultConnector = null;
    PmcRoleConnector defaultConnector = null;
    for (PmcRoleConnector connector : pmcRoleConnectors) {

      if (!connector.isDefault()) {
        if (nonDefaultConnector == null) nonDefaultConnector = connector;
        else
          throw new RuntimeException(
              "Multiple instances for PmcRoleConnector found: "
                  + nonDefaultConnector.getClass()
                  + " and "
                  + connector.getClass());
      }
      if (connector.isDefault()) defaultConnector = connector;
    }
    if (nonDefaultConnector != null) pmcRoleConnector = nonDefaultConnector;
    else {
      if (defaultConnector != null) pmcRoleConnector = defaultConnector;
      else throw new RuntimeException("No default instance for PmcRoleConnector found.");
    }
  }

  @Override
  public Collection<PmcRoleDto> getRolesForProjectType(Long projectTypeGuid) {

    if (pmcRoleConnector == null) determineInjectedBeans();

    Collection<PmcRoleDto> result = new ArrayList<>();
    PmcProjectType projectType = pmcProjectTypeDao.getById(projectTypeGuid);

    if (projectType.getRoles() != null && !projectType.getRoles().equals("")) {
      String[] idNames = projectType.getRoles().split(",");
      for (int i = 0; i < idNames.length; i++) {
        result.add(pmcRoleConnector.getRoleForIdName(idNames[i]));
      }
    }
    return result;
  }

  @Override
  public Collection<PmcRoleInstanceDto> getRoleInstancesForProject(Long projectGuid) {

    if (pmcRoleConnector == null) determineInjectedBeans();

    Collection<PmcRoleInstanceDto> result = new ArrayList<>();
    PmcProject project = pmcProjectDao.getById(projectGuid);
    Collection<PmcRoleDto> roles = getRolesForProjectType(project.getPmcProjectType().getGuid());
    for (PmcRoleDto r : roles) {
      result.addAll(
          pmcRoleConnector.getRoleInstancesForPmcProject(
              dozerMapper.map(project, PmcProjectDto.class), r));
    }
    return result;
  }

  @Override
  public Collection<PmcRoleInstanceDto> getRoleInstancesForProject(
      Long projectGuid, Long roleGuid) {

    if (pmcRoleConnector == null) determineInjectedBeans();

    return pmcRoleConnector.getRoleInstancesForPmcProject(
        dozerMapper.map(pmcProjectDao.getById(projectGuid), PmcProjectDto.class),
        pmcRoleConnector.getRoleForId(roleGuid));
  }

  @Override
  public PmcRoleInstanceDto getGroupRoleInstance(Long projectGuid, String groupName) {

    if (pmcRoleConnector == null) determineInjectedBeans();

    Collection<PmcRoleInstanceDto> result =
        pmcRoleConnector.getRoleInstancesForPmcProject(
            dozerMapper.map(pmcProjectDao.getById(projectGuid), PmcProjectDto.class),
            this.getRoleForGroupName(groupName));

    if (result.size() == 1) {
      for (PmcRoleInstanceDto r : result) {
        return r;
      }
    } else if (result.size() == 0) return null;
    else {
      throw new RuntimeException(
          "More than one role instance found for group role " + groupName + "!");
    }
    return null;
  }

  @Override
  public Collection<PmcRoleInstanceDto> getRoleInstancesForRefObject(
      Long refObjectId, String refObjectType, Long roleGuid) {

    if (pmcRoleConnector == null) determineInjectedBeans();

    return pmcRoleConnector.getRoleInstancesForRefObject(
        refObjectType, refObjectId, pmcRoleConnector.getRoleForId(roleGuid));
  }

  @Override
  public void setRoleInstancesForProject(
      Long projectGuid, Collection<PmcRoleInstanceDto> roleInstance, String source) {

    if (pmcRoleConnector == null) determineInjectedBeans();
    PmcProjectDto dto = this.pmcProjectService.getProjectByGuid(projectGuid, Mode.RELATIONIDS);
    PmcProjectTypeConfiguration conf = new PmcProjectTypeConfiguration(dto.getPmcProjectType());
    List<String> parentRoles = conf.getDeployRoleChangeToParentRoles();
    for (String r : parentRoles) {
      for (PmcRoleInstanceDto role : roleInstance) {
        if (Objects.equals(r, role.getRole().getIdName())) {
          for (Long pId : dto.getParentIds()) {
            setRoleInstancesForProject(pId, roleInstance, source);
          }
        }
      }
    }
    List<String> childRoles = conf.getDeployRoleChangeToChildrenRoles();
    for (String r : childRoles) {
      for (PmcRoleInstanceDto role : roleInstance) {
        if (Objects.equals(r, role.getRole().getIdName())) {
          for (Long pId : dto.getChildrenIds()) {
            setRoleInstancesForProject(pId, roleInstance, source);
          }
        }
      }
    }
    PmcRoleInstanceDto i = roleInstance.iterator().next();
    onBeforeChangeRole(projectGuid, dto, i.getRole().getIdName(), i.getRoleConsumer(), source);
    pmcRoleConnector.setRoleInstancesForPmcProject(dto, roleInstance);
    elasticService.updateEntry(dto);
  }

  @Override
  public PmcRoleDto getRoleForId(Long guid) {
    PmcGroup group = pmcGroupDao.getById(guid);
    PmcRoleDto role = new PmcRoleDto();
    role.setDisplayName(group.getLongName());
    role.setGuid(guid);
    role.setIdName(group.getGroupName());
    return role;
  }

  @Override
  public PmcRoleDto getRoleForGroupName(String groupName) {
    PmcGroup group = pmcGroupDao.getByGroupName(groupName);
    PmcRoleDto role = new PmcRoleDto();
    role.setDisplayName(group.getLongName());
    role.setGuid(group.getGuid());
    role.setIdName(group.getGroupName());
    return role;
  }

  @Override
  public PmcRoleDto getRoleForRoleConsumer(Long roleConsumerGuid, String baseRoleIdName) {
    if (pmcRoleConnector == null) determineInjectedBeans();
    return pmcRoleConnector.getRoleForRoleConsumer(roleConsumerGuid, baseRoleIdName);
  }

  // new RoleDao methods

  @Override
  public PmcRoleDto getRole(String roleName) {
    PmcRole pmcRole = pmcRoleDao.getRoleByName(roleName);
    logger.info("pmcRoleByName: " + pmcRole.toString());
    return mapRoleToDto(pmcRole);
  }

  @Override
  public PmcRoleDto getRole(Long id) {
    PmcRole pmcRole = pmcRoleDao.getById(id);
    return mapRoleToDto(pmcRole);
  }

  @Override
  public List<PmcRoleDto> getRoles() {
    Collection<PmcRole> pmcRoles = pmcRoleDao.getAll();
    List<PmcRoleDto> roles = new ArrayList<>();
    for (PmcRole pmcRole : pmcRoles) {
      roles.add(mapRoleToDto(pmcRole));
    }
    return roles;
  }

  @Override
  public PmcRoleDto addRole(String roleName) {
    PmcRole pmcRole = new PmcRole(roleName, new ArrayList<PmcPermission>());
    pmcRole.setName(roleName);
    pmcRole = pmcRoleDao.create(pmcRole);
    return mapRoleToDto(pmcRole);
  }

  @Override
  public PmcRoleDto updateRole(PmcRoleDto pmcRoleDto) {
    PmcRole role = pmcRoleDao.getById(pmcRoleDto.getGuid());
    role.setName(pmcRoleDto.getName());
    role.getPermissions().clear();
    for (PmcPermissionDto permission : pmcRoleDto.getPermissions()) {
      PmcPermission p = pmcPermissionDao.getById(permission.getGuid());
      role.getPermissions().add(p);
    }
    role = pmcRoleDao.update(role);
    return mapRoleToDto(role);
  }

  private PmcRoleDto mapRoleToDto(PmcRole pmcRole) {
    PmcRoleDto role = new PmcRoleDto();
    role.setName(pmcRole.getName());
    role.setGuid(pmcRole.getGuid());
    List<PmcPermissionDto> perms =
        dozerMapper.mapCollection(pmcRole.getPermissions(), PmcPermissionDto.class);
    role.setPermissions(perms);
    return role;
  }

  @Override
  public void syncProjectRoles(PmcProjectDto project) {
    List<PmcPropertyDto> props = pmcPropertyService.getPropertiesForProject(project.getGuid());
    for (PmcPropertyDto prop : props) {
      if (prop.getKey().startsWith("role_")) {
        String groupName = prop.getKey().substring(5);
        PmcRoleDto role = this.getRoleForGroupName(groupName);
        Collection<PmcRoleInstanceDto> roles =
            this.getRoleInstancesForProject(project.getGuid(), role.getGuid());
        this.setRoleInstancesForProject(project.getGuid(), roles, null);
      }
    }
  }

  // new Function Role methods
  // ToDo: this part will be soon refactored

  @Override
  public PmcFuRoleInstanceDto getFuRoleForProject(String idName, String projectIdName) {
    logger.info("get FuRole=" + idName + " from " + projectIdName);
    PmcFuRoleInstanceDto result = new PmcFuRoleInstanceDto();

    PmcProject p = pmcProjectDao.getByProjectId(projectIdName);
    if (p == null) {
      throw new IllegalArgumentException("no project found with idName=" + projectIdName);
    }

    PmcGroup g = pmcGroupDao.getByGroupName(idName);
    if (g == null) {
      throw new IllegalArgumentException("no group found representing role=" + idName);
    }
    Collection<PmcRoleInstanceDto> roleInstances =
        this.getRoleInstancesForProject(p.getGuid(), g.getGuid());
    if (roleInstances.size() == 0) {
      return result;
    } else if (roleInstances.size() > 1) {
      throw new IllegalStateException("multiple role instances found for role=" + idName);
    } else {
      PmcRoleInstanceDto role = roleInstances.iterator().next();
      if (role.getRoleConsumer() == null) return result;
      PmcUser roleUser = pmcUserRepo.findOne(role.getRoleConsumer());
      result.setUserIdName(roleUser.getLoginName());
      if (!isGroupRole(idName)) {
        return result;
      } else {
        result.setGroupIdName(result.getUserIdName());
        PmcRoleDto secondRole = this.getRoleForRoleConsumer(roleUser.getGuid(), idName);
        PmcFuRoleInstanceDto secondFuRoleInstance =
            this.getFuRoleForProject(secondRole.getIdName(), projectIdName);
        result.setUserIdName(secondFuRoleInstance.getUserIdName());
        return result;
      }
    }
  }

  private boolean isGroupRole(String idName) {
    return (idName.equals("integrationgu")
        || idName.equals("transgu")
        || idName.equals("baugu")
        || idName.equals("akqui"));
  }

  @Override
  public void removeFuRoleForProject(String idName, String projectIdName, boolean onlyUser) {
    logger.info("removing FuRole=" + idName + " from " + projectIdName + " (onlyUser=" + onlyUser);
    PmcFuRoleInstanceDto fuRole = this.getFuRoleForProject(idName, projectIdName);
    PmcProject p = pmcProjectDao.getByProjectId(projectIdName);

    if (fuRole.getGroupIdName() != null) {
      PmcUser groupUser = pmcUserDao.getByLoginName(fuRole.getGroupIdName());
      PmcRoleDto userRole = this.getRoleForRoleConsumer(groupUser.getGuid(), idName);
      pmcPropertyService.deleteProperty(null, null, p.getGuid(), "role_" + userRole.getIdName());
      if (!onlyUser) {
        pmcPropertyService.deleteProperty(null, null, p.getGuid(), "role_" + idName);
      }
    } else {
      pmcPropertyService.deleteProperty(null, null, p.getGuid(), "role_" + idName);
    }
  }

  @Override
  public void onBeforeChangeRole(
      Long projectGuid, PmcProjectDto projectDto, String roleName, Long roleValue, String source) {
    PmcProject project = pmcProjectDao.getById(projectGuid);
    Collection<PmcProjectListener> listener = getListener(project);
    for (PmcProjectListener l : listener) {
      l.beforeRoleChange(projectDto, roleName, roleValue, source);
    }
  }

  @Override
  public Collection<PmcProjectListener> getListener(PmcProject p) {
    return getListener(p.getPmcProjectType());
  }

  private Collection<PmcProjectListener> getListener(PmcProjectType pt) {
    Collection<PmcProjectListener> listener = new ArrayList<>();
    if (pt.getListeners() != null) {
      for (String idName : pt.getListeners()) {
        if (startListenerMap.containsKey(idName)) listener.add(startListenerMap.get(idName));
        else logger.warn("No project listener with id '" + idName + "' found!");
      }
    }
    return listener;
  }
}
