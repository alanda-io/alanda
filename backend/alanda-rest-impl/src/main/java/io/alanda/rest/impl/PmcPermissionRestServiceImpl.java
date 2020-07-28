/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest.impl;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.service.PmcPermissionService;
import io.alanda.base.util.UserContext;

import io.alanda.rest.PmcPermissionRestService;

/**
 * @author developer
 */
public class PmcPermissionRestServiceImpl implements PmcPermissionRestService {

  private static final Logger log = LoggerFactory.getLogger(PmcPermissionRestServiceImpl.class);

  @Inject
  private PmcPermissionService pmcPermissionService;

  @Override
  public List<PmcPermissionDto> getPermissions() {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcPermissionService.getPermissions();
  }

  @Override
  public PmcPermissionDto addPermission(PmcPermissionDto pmcPermissionDto) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcPermissionService.addPermission(pmcPermissionDto.getKey());
  }

  @Override
  public PmcPermissionDto getPermissionById(Long permissionId) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcPermissionService.getPermission(permissionId);
  }

  @Override
  public PmcPermissionDto updatePermission(PmcPermissionDto pmcPermissionDto) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcPermissionService.updatePermission(pmcPermissionDto);
  }

}
