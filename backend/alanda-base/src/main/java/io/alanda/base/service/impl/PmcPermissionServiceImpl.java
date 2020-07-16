/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.PmcPermissionDao;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.entity.PmcPermission;
import io.alanda.base.service.PmcPermissionService;

/**
 * @author developer
 */
@Stateless
public class PmcPermissionServiceImpl implements PmcPermissionService {

  private static final Logger log = LoggerFactory.getLogger(PmcPermissionServiceImpl.class);

  @Inject
  private PmcPermissionDao pmcPermissionDao;

  @Override
  public PmcPermissionDto getPermission(Long id) {
    PmcPermission pmcPermission = pmcPermissionDao.getById(id);
    return mapPermissionToDto(pmcPermission);
  }

  @Override
  public PmcPermissionDto getPermission(String key) {
    PmcPermission pmcPermission = pmcPermissionDao.getPermissionByKey(key);
    return mapPermissionToDto(pmcPermission);
  }

  @Override
  public List<PmcPermissionDto> getPermissions() {
    Collection<PmcPermission> pmcPermissions = pmcPermissionDao.getAll();
    List<PmcPermissionDto> perms = new ArrayList<>();
    for (PmcPermission pmcPermission : pmcPermissions) {
      perms.add(mapPermissionToDto(pmcPermission));
    }
    return perms;
  }

  @Override
  public PmcPermissionDto addPermission(String key) {
    PmcPermission pmcPermission = new PmcPermission();
    pmcPermission.setKey(key);
    pmcPermissionDao.create(pmcPermission);
    return mapPermissionToDto(pmcPermission);
  }

  @Override
  public PmcPermissionDto updatePermission(PmcPermissionDto pmcPermissionDto) {
    PmcPermission perm = pmcPermissionDao.getById(pmcPermissionDto.getGuid());
    perm.setKey(pmcPermissionDto.getKey());
    perm = pmcPermissionDao.update(perm);
    return mapPermissionToDto(perm);
  }

  private PmcPermissionDto mapPermissionToDto(PmcPermission pmcPermission) {
    PmcPermissionDto perm = new PmcPermissionDto(pmcPermission.getGuid(), pmcPermission.getKey());
    return perm;
  }

}
