package io.alanda.base.service.impl;

import io.alanda.base.dao.PmcGroupRepo;
import io.alanda.base.dao.PmcPermissionDao;
import io.alanda.base.dao.PmcRoleDao;
import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.entity.PmcGroup;
import io.alanda.base.entity.PmcPermission;
import io.alanda.base.entity.PmcRole;
import io.alanda.base.service.PmcGroupService;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.cache.UserCache;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PmcGroupServiceImpl implements PmcGroupService {

  private static final Logger log = LoggerFactory.getLogger(PmcGroupServiceImpl.class);

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcGroupRepo pmcGroupRepo;

  @Inject
  private PmcRoleDao pmcRoleDao;

  @Inject
  private PmcPermissionDao pmcPermissionDao;

  @Inject
  private UserCache userCache;

  @Override
  public List<PmcGroupDto> getAllGroups() {
    List<PmcGroup> groups = (List<PmcGroup>) pmcGroupRepo.findAll();
    List<PmcGroupDto> res = new ArrayList<>();
    for (PmcGroup pmcGroup : groups) {
      PmcGroupDto tmp = dozerMapper.map(pmcGroup, PmcGroupDto.class);
      res.add(tmp);
    }
    return res;
  }

  @Override
  public PagedResultDto<PmcGroupDto> findGroup(PmcGroupDto exampleGroupDto, int pageNumber, int pageSize, Sort sort) {
    PmcGroup exampleGroup = dozerMapper.map(exampleGroupDto, PmcGroup.class);
    Pageable p = PageRequest.of(pageNumber - 1, pageSize, sort);

    Page<PmcGroup> page = pmcGroupRepo
        .findAll(Example.of(exampleGroup, ExampleMatcher.matching().withIgnorePaths("version").withIgnoreCase()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), p);
    PagedResultDto<PmcGroupDto> result = new PagedResultDto<>();
    result.setResults(dozerMapper.mapCollection(page.getContent(), PmcGroupDto.class));
    result.setTotal(page.getTotalElements());
    return result;
  }

  @Override
  public void saveGroup(PmcGroupDto pmcGroupDto) {
    PmcGroup pmcGroup = dozerMapper.map(pmcGroupDto, PmcGroup.class);
    log.info("Saving Group: {}", pmcGroup);
    pmcGroupRepo.save(pmcGroup);
  }

  @Override
  public PmcGroupDto getGroupById(Long groupId) {
    PmcGroup byId = pmcGroupRepo.findById(groupId)
        .orElse(null);
    return dozerMapper.map(byId, PmcGroupDto.class);
  }

  @Override
  public void updateGroup(PmcGroupDto pmcGroupDto) {
    PmcGroup pmcGroup = pmcGroupRepo.findById(pmcGroupDto.getGuid())
        .orElse(null);
    pmcGroup.setGroupName(pmcGroupDto.getGroupName());
    pmcGroup.setLongName(pmcGroupDto.getLongName());
    pmcGroup.setGroupSource(pmcGroupDto.getGroupSource());
    pmcGroup.setEmail(pmcGroupDto.getEmail());
    pmcGroup.setPhone(pmcGroupDto.getPhone());
    pmcGroupRepo.save(pmcGroup);
  }

  @Override
  public PmcGroupDto getGroupByGroupName(String groupName) {
    PmcGroup pmcGroup = pmcGroupRepo.findByGroupName(groupName);
    return dozerMapper.map(pmcGroup, PmcGroupDto.class);
  }

  @Override
  public void updateRolesForGroup(Long guid, List<PmcRoleDto> roles) {
    PmcGroup pmcGroup = pmcGroupRepo.findById(guid).orElse(null);
    pmcGroup.getRoles().clear();
    for (PmcRoleDto role : roles) {
      PmcRole rolle = pmcRoleDao.getById(role.getGuid());
      if (rolle != null) {
        pmcGroup.getRoles().add(rolle);
      }
    }
    pmcGroupRepo.save(pmcGroup);
    userCache.invalidateAll();
  }

  @Override
  public Set<PmcPermissionDto> getEffectivePermissionsForGroup(Long guid) {
    PmcGroup group = pmcGroupRepo.findById(guid).orElse(null);
    Set<PmcPermissionDto> perms = new HashSet<>();
    for (PmcRole role : group.getRoles()) {
      for (PmcPermission permission : role.getPermissions()) {
        PmcPermissionDto tmp = dozerMapper.map(permission, PmcPermissionDto.class);
        tmp.setSource("Role:" + role.getName());
        perms.add(tmp);
      }
    }
    for (PmcPermission pmcPermission : group.getPermissions()) {
      PmcPermissionDto tmp = dozerMapper.map(pmcPermission, PmcPermissionDto.class);
      perms.add(tmp);
    }
    return perms;
  }

  @Override
  public void updatePermissionsForGroup(Long guid, List<PmcPermissionDto> permissions) {
    PmcGroup pmcGroup = pmcGroupRepo.findById(guid).orElse(null);
    pmcGroup.getPermissions().clear();
    for (PmcPermissionDto permission : permissions) {
      PmcPermission perm = pmcPermissionDao.getById(permission.getGuid());
      if (perm != null) {
        pmcGroup.getPermissions().add(perm);
      }
    }
    pmcGroupRepo.save(pmcGroup);
    userCache.invalidateAll();
  }

  @Override
  public List<PmcGroupDto> getGroupsForRole(Long roleId) {
    return dozerMapper.mapCollection(pmcGroupRepo.findByRoles_Guid(roleId), PmcGroupDto.class);
  }

  @Override
  public List<PmcGroupDto> getGroupsForRole(String roleName) {
    return dozerMapper.mapCollection(pmcGroupRepo.findByRoles_Name(roleName), PmcGroupDto.class);
  }

  @Override
  public PmcGroupDto createGroupWithRoles(PmcGroupDto groupTemplate, Set<String> roleNames, String groupNamePrefix) {
    if (StringUtils.isBlank(groupTemplate.getLongName())) {
      throw new IllegalArgumentException("Group.longName must not be empty.");
    }
    if (StringUtils.isBlank(groupTemplate.getGroupSource())) {
      groupTemplate.setGroupSource("BPMC");
    }
    // TODO: seems wrong! Wenn Prefix nicht null ist, dann ignoriere es und schreib stattdessen "it"???
    if (StringUtils.isBlank(groupTemplate.getGroupName()) && groupNamePrefix != null) {
      groupTemplate.setGroupName(getGroupNameFromLongName("it", groupTemplate.getLongName()));
    }
    if (StringUtils.isBlank(groupTemplate.getGroupName()) && groupNamePrefix == null) {
      throw new IllegalArgumentException("Group.groupName must not be empty.");
    }
    PmcGroup exists = pmcGroupRepo.findByGroupName(groupTemplate.getGroupName());
    if (exists != null) {
      return null;
    }
    PmcGroup newGroup = this.dozerMapper.map(groupTemplate, PmcGroup.class);
    if (roleNames != null) {
      List<PmcRole> roles = new ArrayList<>();
      for (String roleName : roleNames) {
        PmcRole role = pmcRoleDao.getRoleByName(roleName);
        if (role != null) {
          roles.add(role);
        }
      }
      newGroup.setRoles(roles);
    }
    newGroup = pmcGroupRepo.save(newGroup);
    return dozerMapper.map(newGroup, PmcGroupDto.class);
  }

  @Override
  public PmcGroupDto createGroupWithRoles(String groupName, String longName, Set<String> roleNames) {
    PmcGroupDto newGroup = new PmcGroupDto();
    newGroup.setGroupName(groupName);
    newGroup.setLongName(longName);
    newGroup.setGroupSource("BPMC");
    return createGroupWithRoles(newGroup, roleNames, null);
  }

  public static String getGroupNameFromLongName(String prefix, String longName) {
    String tmp = longName.replaceAll(" GmbH \\& CO KG| GmbH| AG", "");
    String[] tmps = tmp.split(" ");
    tmp = prefix;
    for (String tmp1 : tmps) {
      if (tmp1.length() > 3) {
        tmp1 = tmp1.substring(0, 4);
      }
      tmp += "_" + tmp1;
    }
    return tmp.toLowerCase();
  }

  @Override
  public void setGroupActiveState(long guid, boolean active) {
    PmcGroup pmcGroup = pmcGroupRepo.findById(guid).orElse(null);
    pmcGroup.setActive(active);
    pmcGroupRepo.save(pmcGroup);
  }

}
