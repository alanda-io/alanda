package io.alanda.base.connector.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcRoleConnector;
import io.alanda.base.dao.PmcGroupDao;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.entity.PmcGroup;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcPropertyService.PmcPropertyType;
import io.alanda.base.service.PmcUserService;

@Stateless
public class PmcRoleConnectorImpl implements PmcRoleConnector {
  private static final Logger log = LoggerFactory.getLogger(PmcRoleConnectorImpl.class);

  final String ROLE_ENTITY_TYPE_PMC_PROJECT = "PMC_PROJECT";

  final String ROLE_CONSUMER_TYPE = "PMCUSER";


  @Inject
  PmcUserService userService;

  @Inject
  PmcProjectService projectService;

  @Inject
  PmcPropertyService propertyService;

  @Inject
  private PmcGroupDao pmcGroupDao;

  @Override
  public PmcRoleDto getRoleForId(Long guid) {
    log.trace("Getting role for id {}", guid);

    PmcGroupDto group = userService.getGroupById(guid);
    PmcRoleDto role = new PmcRoleDto();
    role.setDisplayName(group.getLongName());
    role.setGuid(group.getGuid());
    role.setIdName(group.getGroupName());
    return role;
  }

  @Override
  public Collection<PmcRoleInstanceDto> getRoleInstancesForPmcProject(PmcProjectDto project, PmcRoleDto role) {
    log.debug("Getting role instances of {} for project {}...", role, project);

    PmcUserDto u = projectService.getUserForRole(project.getRefObjectType(), project.getRefObjectId(), role.getIdName(), project.getGuid());
    final List<PmcRoleInstanceDto> roleInstances;
    if (u != null) {
      PmcRoleInstanceDto roleInstance = new PmcRoleInstanceDto();
      roleInstance.setRole(role);
      roleInstance.setRoleEntity(project.getRefObjectId().toString());
      roleInstance.setRoleEntityType(ROLE_ENTITY_TYPE_PMC_PROJECT);
      roleInstance.setRoleConsumer(u.getGuid());
      roleInstance.setRoleConsumerType(ROLE_CONSUMER_TYPE);
      roleInstances = Collections.singletonList(roleInstance);
    } else {
      PmcRoleInstanceDto roleInstance = new PmcRoleInstanceDto();
      roleInstance.setRole(role);
      if (project.getRefObjectId() != null) {
        roleInstance.setRoleEntity(project.getRefObjectId().toString());
      }
      roleInstance.setRoleEntityType(ROLE_ENTITY_TYPE_PMC_PROJECT);
      roleInstance.setRoleConsumer(null);
      roleInstance.setRoleConsumerType(null);
      roleInstances = Collections.singletonList(roleInstance);
    }

    log.trace("...found {} role instances: {}", roleInstances.size(), roleInstances);
    return roleInstances;
  }

  @Override
  public void setRoleInstancesForPmcProject(PmcProjectDto project, Collection<PmcRoleInstanceDto> roleInstances) {
    log.info("Setting role instances for project {}: {}", project, roleInstances);
    String key = null;
    for (PmcRoleInstanceDto r : roleInstances) {
      if (key != null && !r.getRole().getIdName().equals(key))
        throw new RuntimeException("Only roleInstances with the same role are supported!");
      else
        key = r.getRole().getIdName();
    }

    key = "role_" + key;
    String values = "";
    for (PmcRoleInstanceDto r : roleInstances) {
      values += r.getRoleConsumer().toString() + ",";
    }
    values = values.substring(0, values.length() - 1);
    propertyService.setProperty(null, null, project.getGuid(), key, values, PmcPropertyType.STRING);
  }

  @Override
  public Boolean isDefault() {
    return true;
  }

  @Override
  public PmcRoleDto getRoleForIdName(String idName) {
    log.trace("Getting role for idName {}", idName);
    PmcGroupDto group = userService.getGroupByName(idName);
    PmcRoleDto role = new PmcRoleDto();
    role.setDisplayName(group.getLongName());
    role.setGuid(group.getGuid());
    role.setIdName(group.getGroupName());
    return role;
  }

  @Override
  public Collection<PmcRoleInstanceDto> getRoleInstancesForRefObject(String refObjectType, Long refObjectId, PmcRoleDto role) {
    log.debug("Getting role instances of {} for ref object {}@{}...", role, refObjectType, refObjectId);
    PmcUserDto u = projectService.getUserForRole(refObjectType, refObjectId, role.getIdName(), (Long) null);
    if (u != null) {
      PmcRoleInstanceDto roleInstance = new PmcRoleInstanceDto();
      roleInstance.setRole(role);
      roleInstance.setRoleEntity(Long.toString(refObjectId));
      roleInstance.setRoleEntityType(ROLE_ENTITY_TYPE_PMC_PROJECT);
      roleInstance.setRoleConsumer(u.getGuid());
      roleInstance.setRoleConsumerType(ROLE_CONSUMER_TYPE);
      return Collections.singletonList(roleInstance);
    } else {
      PmcRoleInstanceDto roleInstance = new PmcRoleInstanceDto();
      roleInstance.setRole(role);
      roleInstance.setRoleEntity(Long.toString(refObjectId));
      roleInstance.setRoleEntityType(ROLE_ENTITY_TYPE_PMC_PROJECT);
      roleInstance.setRoleConsumer(null);
      roleInstance.setRoleConsumerType(null);
      return Collections.singletonList(roleInstance);
    }
  }

  @Override
  public PmcRoleDto getRoleForRoleConsumer(Long roleConsumerGuid, String baseRoleIdName) {
    log.debug("Getting role {} for consumer with id {}", baseRoleIdName, roleConsumerGuid);

    if (baseRoleIdName == null)
      return null;
    PmcGroup roleGroup = pmcGroupDao.getByGroupName(baseRoleIdName);

    PmcRoleDto role = new PmcRoleDto();
    role.setDisplayName(roleGroup.getLongName());
    role.setIdName(roleGroup.getGroupName());
    role.setGuid(roleGroup.getGuid());
    return role;
  }
}
