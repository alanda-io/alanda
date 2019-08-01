/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest.impl;

import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;
import io.alanda.base.service.PmcRoleService;
import io.alanda.base.util.UserContext;
import io.alanda.rest.PmcRoleRestService;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author developer */
public class PmcRoleRestServiceImpl implements PmcRoleRestService {

  private final Logger logger = LoggerFactory.getLogger(PmcRoleRestServiceImpl.class);

  @Inject private PmcRoleService pmcRoleService;

  @Override
  public List<PmcRoleDto> getRoles() {
    if (!UserContext.getUser().isAdmin()) throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcRoleService.getRoles();
  }

  @Override
  public PmcRoleDto addRole(PmcRoleDto pmcRoleDto) {
    if (!UserContext.getUser().isAdmin()) throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcRoleService.addRole(pmcRoleDto.getIdName());
  }

  @Override
  public PmcRoleDto getRoleById(Long roleId) {
    if (!UserContext.getUser().isAdmin()) throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcRoleService.getRole(roleId);
  }

  @Override
  public PmcRoleDto getRoleByName(String roleName) {
    if (!UserContext.getUser().isAdmin()) throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcRoleService.getRole(roleName);
  }

  @Override
  public PmcRoleDto updateRole(PmcRoleDto pmcRoleDto) {
    if (!UserContext.getUser().isAdmin()) throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcRoleService.updateRole(pmcRoleDto);
  }

  @Override
  public Collection<PmcRoleInstanceDto> getRoleInstancesForProject(
      Long projectGuid, Long roleGuid) {
    return pmcRoleService.getRoleInstancesForProject(projectGuid, roleGuid);
  }

  @Override
  public Response setRoleInstancesForProject(
      Long projectGuid, String source, Collection<PmcRoleInstanceDto> role) {
    pmcRoleService.setRoleInstancesForProject(projectGuid, role, source);
    return Response.accepted().build();
  }
}
