/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.service;

import java.util.List;

import io.alanda.base.dto.PmcPermissionDto;

/**
 *
 * @author developer
 */
public interface PmcPermissionService {
  
  public PmcPermissionDto getPermission(Long id);
  
  public PmcPermissionDto getPermission(String key);
  
  public List<PmcPermissionDto> getPermissions();
  
  public PmcPermissionDto addPermission(String key);
  
  public PmcPermissionDto updatePermission(PmcPermissionDto pmcPermissionDto);
}
