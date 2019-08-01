package io.alanda.base.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;

public interface PmcGroupService {

  public List<PmcGroupDto> getAllGroups();
  
  PagedResultDto<PmcGroupDto> findGroup(PmcGroupDto exampleGroupDto, int pageNumber, int pageSize, Sort sort);

  public void saveGroup(PmcGroupDto pmcGroupDto);

  public PmcGroupDto getGroupById(Long groupId);

  public void updateGroup(PmcGroupDto pmcGroupDto);

  public PmcGroupDto getGroupByGroupName(String groupName);

  public void updateRolesForGroup(Long guid, List<PmcRoleDto> roles);

  public Set<PmcPermissionDto> getEffectivePermissionsForGroup(Long guid);

  public void updatePermissionsForGroup(Long guid, List<PmcPermissionDto> permissions);

  List<PmcGroupDto> getGroupsForRole(Long roleId);
  
  List<PmcGroupDto> getGroupsForRole(String roleName);

  public PmcGroupDto createGroupWithRoles(String groupName, String longName, Set<String> roleNames);

  PmcGroupDto createGroupWithRoles(PmcGroupDto groupTemplate, Set<String> roleNames, String groupNamePrefix);

  public void setGroupActiveState(long guid, boolean active);

}
