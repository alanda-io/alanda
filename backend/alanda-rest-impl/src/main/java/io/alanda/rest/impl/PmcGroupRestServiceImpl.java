package io.alanda.rest.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.service.PmcGroupService;
import io.alanda.base.util.UserContext;

import io.alanda.rest.PmcGroupRestService;
import io.alanda.rest.util.RepoHelpers;

public class PmcGroupRestServiceImpl implements PmcGroupRestService {

  private static final Logger log = LoggerFactory.getLogger(PmcGroupRestServiceImpl.class);

  @Inject
  private PmcGroupService pmcGroupService;

  //  @Override
  //  public List<PmcGroupDto> getAllGroups() {
  //    if ( !UserContext.getUser().isAdmin())
  //      throw new javax.ws.rs.ForbiddenException("Access denied");
  //    return pmcGroupService.getAllGroups();
  //  }

  @Override
  public PagedResultDto<PmcGroupDto> getAllGroups(Map<String, Object> serverOptions) {
    log.info("called getGroupRepo");
    Integer pageNumber = (Integer) serverOptions.get("pageNumber");
    if (pageNumber == null)
      pageNumber = 1;
    Integer pageSize = (Integer) serverOptions.get("pageSize");
    if (pageSize == null)
      pageSize = 15;
    Map<String, Object> filterOptions = (Map<String, Object>) serverOptions.get("filterOptions");
    PmcGroupDto pgDto = new PmcGroupDto();
    String guidAsString = (String) filterOptions.get("guid");
    Long guid = null;
    if (guidAsString != null) {
      guid = Long.parseLong(guidAsString);
    }
    pgDto.setGuid(guid);
    pgDto.setGroupName((String) filterOptions.get("groupName"));
    pgDto.setLongName((String) filterOptions.get("longName"));
    pgDto.setPhone((String) filterOptions.get("phone"));
    pgDto.setEmail((String) filterOptions.get("email"));
    pgDto.setGroupSource((String) filterOptions.get("groupSource"));
    pgDto.setActive(true);//Dto always gets created with false!
    if (filterOptions.get("active") != null) {
      pgDto.setActive((Boolean) filterOptions.get("active"));
    }
    Map<String, Object> sortOptions = (Map<String, Object>) serverOptions.get("sortOptions");
    Sort sort = RepoHelpers.parseSort(sortOptions, "groupName");
    return pmcGroupService.findGroup(pgDto, pageNumber, pageSize, sort);
  }

  @Override
  public Response saveGroup(PmcGroupDto pmcGroupDto) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    pmcGroupService.saveGroup(pmcGroupDto);
    return Response.ok().build();
  }

  @Override
  public PmcGroupDto getGroupById(Long groupId) {
    return pmcGroupService.getGroupById(groupId);
  }

  @Override
  public Response updateGroup(PmcGroupDto pmcGroupDto) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    pmcGroupService.updateGroup(pmcGroupDto);
    return Response.ok().build();
  }

  @Override
  public PmcGroupDto getGroupByGroupName(String groupName) {
    return pmcGroupService.getGroupByGroupName(groupName);
  }

  @Override
  public Response updateRolesForGroup(Long groupId, List<PmcRoleDto> roles) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    pmcGroupService.updateRolesForGroup(groupId, roles);
    return Response.ok().build();
  }

  @Override
  public Collection<PmcPermissionDto> getEffectivePermissionsForGroup(Long groupId) {
    return pmcGroupService.getEffectivePermissionsForGroup(groupId);
  }

  @Override
  public Response updatePermissionsForGroup(Long groupId, List<PmcPermissionDto> permissions) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    pmcGroupService.updatePermissionsForGroup(groupId, permissions);
    return Response.ok().build();
  }

  @Override
  public List<PmcGroupDto> getGroupsForRole(Long roleId) {
    return pmcGroupService.getGroupsForRole(roleId);
  }

  @Override
  public List<PmcGroupDto> getGroupsForRole(String roleName) {
    return pmcGroupService.getGroupsForRole(roleName);
  }

  @Override
  public PmcGroupDto createGroupWithRoles(String groupName, String loginName, String firstName, String LastName, Set<String> roleNames) {
    return pmcGroupService.createGroupWithRoles(groupName, groupName, roleNames);
  }

}
