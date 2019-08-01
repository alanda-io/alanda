package io.alanda.base.service;

import java.util.Collection;
import java.util.List;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.dto.PmcFuRoleInstanceDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;
import io.alanda.base.entity.PmcProject;


public interface PmcRoleService {
  
  Collection<PmcRoleDto> getRolesForProjectType(Long projectTypeGuid);
  
  Collection<PmcRoleInstanceDto> getRoleInstancesForProject(Long projectGuid);
  
  Collection<PmcRoleInstanceDto> getRoleInstancesForProject(Long projectGuid, Long roleGuid);
  
  PmcRoleInstanceDto getGroupRoleInstance(Long projectGuid, String groupName);
  
  Collection<PmcRoleInstanceDto> getRoleInstancesForRefObject(
    Long refObjectId, 
    String refObjectType,
    Long roleGuid);
  
  void setRoleInstancesForProject(Long projectGuid, Collection<PmcRoleInstanceDto> role, String source);
  
  PmcRoleDto getRoleForId(Long guid);
  
  PmcRoleDto getRoleForGroupName(String groupName);

  PmcRoleDto getRoleForRoleConsumer(Long roleConsumerGuid, String baseRoleIdName);
  
  PmcRoleDto getRole(String roleName);
  
  PmcRoleDto getRole(Long id);
  
  PmcRoleDto addRole(String roleName);
  
  List<PmcRoleDto> getRoles();
  
  PmcRoleDto updateRole(PmcRoleDto pmcRoleDto);

  void syncProjectRoles(PmcProjectDto project);

  // new Function Role methods

  PmcFuRoleInstanceDto getFuRoleForProject(String idName, String projectIdName);

  void removeFuRoleForProject(String idName, String projectIdName, boolean onlyUser);

  Collection<PmcProjectListener> getListener(PmcProject p);

  void onBeforeChangeRole(Long projectGuid, PmcProjectDto project, String roleName, Long roleValue, String source);

}
