package io.alanda.base.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.alanda.base.dao.IdCounterDao;
import io.alanda.base.dao.PmcGroupRepo;
import io.alanda.base.dao.PmcHistoryLogRepo;
import io.alanda.base.dao.PmcPermissionDao;
import io.alanda.base.dao.PmcRoleDao;
import io.alanda.base.dao.PmcUserDao;
import io.alanda.base.dao.PmcUserRepo;
import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.entity.PmcGroup;
import io.alanda.base.entity.PmcHistoryLog;
import io.alanda.base.entity.PmcPermission;
import io.alanda.base.entity.PmcRole;
import io.alanda.base.entity.PmcUser;
import io.alanda.base.service.PmcGroupService;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.UserContext;
import io.alanda.base.util.cache.UserCache;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PmcUserServiceImpl implements PmcUserService {

  private final Logger logger = LoggerFactory.getLogger(PmcUserServiceImpl.class);

  @Inject
  private PmcUserDao pmcUserDao;

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcUserRepo pmcUserRepo;

  @Inject
  private PmcGroupRepo pmcGroupRepo;

  @Inject
  private PmcGroupService pmcGroupService;

  @Inject
  private PmcPermissionDao pmcPermissionDao;

  @Inject
  private PmcRoleDao pmcRoleDao;

  @Inject
  private PmcHistoryLogRepo pmcHistoryDao;

  @Inject
  private PasswordServiceImpl passwordService;

  @Inject
  private UserCache userCache;

  @Inject
  private IdCounterDao idCounterDao;

  public PmcUserServiceImpl() {

  }

  public PmcUserServiceImpl(PmcUserDao smUserDao) {
    this.pmcUserDao = smUserDao;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.impl.PmcUserService#getUserByUserId(java.lang.Long)
   */
  @Override
  public PmcUserDto getUserByUserId(Long userId) {
    PmcUser user = pmcUserDao.getById(userId);
    return dozerMapper.map(user, PmcUserDto.class);
  }

  @Override
  public PmcUserDto getUserByLoginName(String loginName, boolean includeGroups) {
    PmcUser user = pmcUserDao.getByLoginName(loginName);
    if (user == null)
      return null;
    PmcUserDto dto = dozerMapper.map(user, PmcUserDto.class, includeGroups ? "includeGroups" : null);
    Set<String> roles = new TreeSet<>();
    Set<String> perms = new TreeSet<>();
    if (dto.getGroupList() != null) {
      dto.setGroups(new TreeSet<>());
      for (PmcGroupDto gd : dto.getGroupList()) {
        dto.getGroups().add(gd.getGroupName());
        dto.getGroups().add(gd.getGuid().toString());
        for (PmcRoleDto r : gd.getRoles()) {
          roles.add(r.getName());
          for (PmcPermissionDto p : r.getPermissions()) {
            perms.add(p.getKey());
          }
        }
        for (PmcPermissionDto p : gd.getPermissions()) {
          perms.add(p.getKey());
        }
      }
    }
    if (user.getRoleList() != null) {
      for (PmcRole role : user.getRoleList()) {
        roles.add(role.getName());
        for (PmcPermission permission : role.getPermissions()) {
          perms.add(permission.getKey());
        }
      }
    }
    if (user.getPermissionList() != null) {
      for (PmcPermission pmcPermission : user.getPermissionList()) {
        perms.add(pmcPermission.getKey());
      }
    }
    dto.setRoles(roles);
    dto.setPermissions(perms);
    return dto;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.impl.PmcUserService#getUserByLoginName(java.lang.String)
   */
  @Override
  public PmcUserDto getUserByLoginName(String loginName) {
    return getUserByLoginName(loginName, false);
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.impl.PmcUserService#getUsersByGroup(java.lang.Long, java.lang.String)
   */
  @Override
  public List<PmcUserDto> getUsersByGroup(Long groupId, String search) {
    return getUsersByGroup(groupId, search, false);
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.impl.PmcUserService#getUsersByGroup(java.lang.Long, java.lang.String)
   */
  @Override
  public List<PmcUserDto> getUsersByGroup(Long groupId, String search, Boolean isContactRequired) {
    List<PmcUser> users = pmcUserDao.getByGroup(groupId, search);
    List<PmcUserDto> dtoList = dozerMapper.mapCollection(users, PmcUserDto.class);

    return dtoList;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.impl.PmcUserService#isUserInGroup(java.lang.Long, java.lang.String)
   */
  @Override
  public Boolean isUserInGroup(Long userId, String groupIdName) {
    if (groupIdName == null)
      return false;
    try {
      PmcGroup group = pmcGroupRepo.findByGroupName(groupIdName);
      return pmcUserDao.isUserInGroup(group.getGuid(), userId);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Boolean isUserInGroup(Long userId, Long groupId) {
    try {
      PmcGroup group = pmcGroupRepo.findOne(userId);
      return pmcUserDao.isUserInGroup(group.getGuid(), userId);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public PmcGroupDto getGroupByName(String groupName) {
    PmcGroup group = pmcGroupRepo.findByGroupName(groupName);
    return dozerMapper.map(group, PmcGroupDto.class);
  }

  @Override
  public PmcGroupDto getGroupById(Long guid) {
    PmcGroup group = pmcGroupRepo.findOne(guid);
    return dozerMapper.map(group, PmcGroupDto.class);
  }

  @Override
  public Response saveUser(PmcUserDto pmcUserDto) {

    if (pmcUserDto.getGuid() == null) {
      Long num = idCounterDao.getNext(PmcUserService.USER);
      pmcUserDto.setGuid(num);
    }
    if (pmcUserDto.getSource() == null) {
      pmcUserDto.setSource(PmcUserService.SOURCE_PMC);
    }
    Set<PmcGroup> gList = new HashSet<>();
    for (String g : pmcUserDto.getGroups()) {
      PmcGroup pmcGroup = pmcGroupRepo.findByGroupName(g);
      gList.add(pmcGroup);
    }

    PmcUser pmcUser = dozerMapper.map(pmcUserDto, PmcUser.class);
    pmcUser.setGroupList(gList);

    logger.info(pmcUser.getGroupList().toString());

    pmcUser = pmcUserRepo.save(pmcUser);
    pmcUserDao.getEntityManager().flush();
    if (pmcUserDto.getPassword() != null) {
      savePassword(pmcUser.getGuid(), pmcUserDto.getPassword());
    }
    return Response.ok().build();
  }

  @Override
  public PmcUserDto getRepoUserById(Long userId) {
    PmcUser pmcUser = pmcUserRepo.findOne(userId);
    PmcUserDto pmcUserDto = dozerMapper.map(pmcUser, PmcUserDto.class);
    PmcUserDto u = new PmcUserDto();
    u.setGuid(pmcUserDto.getGuid());
    u.setEmail(pmcUserDto.getEmail());
    u.setFirstName(pmcUserDto.getFirstName());
    u.setLoginName(pmcUserDto.getLoginName());
    u.setLocked(pmcUserDto.isLocked());
    u.setGroups(new ArrayList<String>());
    for (PmcGroup g : pmcUser.getGroupList()) {
      u.getGroups().add(g.getGroupName());
    }
    u.setMobile(pmcUserDto.getMobile());
    u.setSurname(pmcUserDto.getSurname());
    u.setDisplayName(pmcUser.getDisplayName());
    u.setPmcDepartment(pmcUserDto.getPmcDepartment());
    return u;
  }

  @Override
  public PmcUserDto updateRepoUser(PmcUserDto pmcUserDto) {
    PmcUser pmcUser = pmcUserRepo.findOne(pmcUserDto.getGuid());
    pmcUser.setLoginName(pmcUserDto.getLoginName());
    pmcUser.setFirstName(pmcUserDto.getFirstName());
    pmcUser.setSurname(pmcUserDto.getSurname());
    pmcUser.setEmail(pmcUserDto.getEmail());
    pmcUser.setMobile(pmcUserDto.getMobile());
    pmcUser.setPmcDepartment(pmcUserDto.getPmcDepartment());

    if (pmcUser.isLocked() != pmcUserDto.isLocked()) {
      PmcHistoryLog log = new PmcHistoryLog();
      log.setRefObjectId(UserContext.getUser().getGuid());
      log.setRefObjectType("PMC_USER");
      log.setType("User");
      log.setAction(PmcHistoryLog.Action.EDIT);
      log.setFieldName("locked");
      log.setOldValue(String.valueOf(pmcUser.isLocked()));
      log.setNewValue(String.valueOf(pmcUserDto.isLocked()));
      log.setUserGuid(pmcUserDto.getGuid());
      log.setModDate(new Date());
      log.setRefObjectIdName(pmcUserDto.getLoginName());
      log.setFieldRef("user.locked");
      log.setLogDate(new Date());
      pmcHistoryDao.save(log);
    }
    pmcUser.setLocked(pmcUserDto.isLocked());

    if (pmcUserDto.getGroups() != null) {
      List<PmcGroup> gList = new ArrayList<>();
      for (String g : pmcUserDto.getGroups()) {
        PmcGroup pmcGroup = pmcGroupRepo.findByGroupName(g);
        gList.add(pmcGroup);
      }

      //1. iterate user.grouplist and 
      // 1a. remove all entries that are not in gList
      // 1b. remove all entries form user.grouplist in gList
      //2. addALl from gList in user.grouplist
      for (Iterator<PmcGroup> it = pmcUser.getGroupList().iterator(); it.hasNext();) {
        PmcGroup g = it.next();
        if (gList.contains(g)) {
          gList.remove(g);
        } else {
          it.remove();
        }
      }
      pmcUser.getGroupList().addAll(gList);
    }

    if ( !StringUtils.isEmpty(pmcUserDto.getPassword())) {
      pmcUserDao.getEntityManager().flush();
      savePassword(pmcUser.getGuid(), pmcUserDto.getPassword());
    }
    PmcUserDto returnPmcUserDto = null;
    try {
      returnPmcUserDto = dozerMapper.map(pmcUserRepo.save(pmcUser), PmcUserDto.class);
      logger.info("PmcUserServiceImpl.updateRepoUserV2");
      logger.info(returnPmcUserDto.getEmail());
      logger.info(returnPmcUserDto.getLoginName());
      logger.info(returnPmcUserDto.getFirstName());
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    } finally {
      logger.info("finally: " + returnPmcUserDto.getGuid());
    }
    return returnPmcUserDto;
  }

  @Override
  public List<String> getUsernames() {
    return pmcUserRepo.getLoginNames();
  }

  @Override
  public List<PmcUserDto> getRepoUser(int pageNumber, int pageSize) {
    PageRequest pr = new PageRequest(pageNumber, pageSize);
    Page<PmcUser> users = pmcUserRepo.findAll(pr);
    //    List<PmcUser> users = pmcUserRepo.findBySurname("oida");
    List<PmcUserDto> res = new ArrayList<>();
    for (PmcUser pmcUser : users) {
      PmcUserDto tmp = dozerMapper.map(pmcUser, PmcUserDto.class);
      res.add(tmp);
    }
    return res;
  }

  @Override
  public List<PmcUserDto> getUserByGroupId(Long groupId) {
    Sort sort = new Sort("surname", "firstName");
    List<PmcUser> userList = pmcUserRepo.getByGroupList_Guid(groupId, sort);
    return dozerMapper.mapCollection(userList, PmcUserDto.class);
  }

  @Override
  public PagedResultDto<PmcUserDto> findUser(PmcUserDto exampleUserDto, int pageNumber, int pageSize, Sort sort) {
    PmcUser exampleUser = dozerMapper.map(exampleUserDto, PmcUser.class);
    Pageable p = new PageRequest(pageNumber - 1, pageSize, sort);

    Page<PmcUser> page = pmcUserRepo
      .findAll(Example.of(exampleUser, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING)), p);
    PagedResultDto<PmcUserDto> result = new PagedResultDto<>();
    result.setResults(dozerMapper.mapCollection(page.getContent(), PmcUserDto.class));
    result.setTotal(page.getTotalElements());
    return result;
  }

  @Override
  public boolean checkPassword(Long guid, String password) {
    String credentials = (String) this.pmcUserDao
      .getEntityManager()
      .createNativeQuery("SELECT credentials FROM PMC_USER WHERE guid=:guid")
      .setParameter("guid", guid)
      .getSingleResult();
    try {
      return passwordService.checkPassword(password, credentials);
    } catch (Exception e) {
      logger.warn("Error Validting password", e);
      return false;
    }

  }

  @Override
  public List<PmcUserDto> getUsersByLoginNames(Collection<String> loginNames) {
    return dozerMapper.mapCollection(pmcUserDao.getByLoginNames(loginNames), PmcUserDto.class);
  }

  @Override
  public List<PmcPermissionDto> getPermissionsForUser(Long guid) {
    PmcUser user = pmcUserDao.getById(guid);
    List<PmcPermission> perms = user.getPermissionList();
    return dozerMapper.mapCollection(perms, PmcPermissionDto.class);
  }

  @Override
  public List<PmcRoleDto> getRolesForUser(Long guid) {
    PmcUser user = pmcUserDao.getById(guid);
    List<PmcRole> roles = user.getRoleList();
    return dozerMapper.mapCollection(roles, PmcRoleDto.class);
  }

  @Override
  public void updatePermissionsForUser(Long guid, List<PmcPermissionDto> permissions) {
    PmcUser user = pmcUserDao.getById(guid);
    user.getPermissionList().clear();
    for (PmcPermissionDto permission : permissions) {
      PmcPermission perm = pmcPermissionDao.getById(permission.getGuid());
      if (perm != null) {
        user.getPermissionList().add(perm);
      }
    }
    pmcUserDao.update(user);
    userCache.invalidate(user.getLoginName());
  }

  @Override
  public void updateRolesForUser(Long guid, List<PmcRoleDto> roles) {
    PmcUser user = pmcUserDao.getById(guid);
    user.getRoleList().clear();
    for (PmcRoleDto role : roles) {
      PmcRole rolle = pmcRoleDao.getById(role.getGuid());
      if (rolle != null) {
        user.getRoleList().add(rolle);
      }
    }
    pmcUserDao.update(user);
    userCache.invalidate(user.getLoginName());
  }

  @Override
  public void updateGroupsForUser(Long guid, List<PmcGroupDto> groups) {
    PmcUser user = pmcUserDao.getById(guid);
    user.getGroupList().clear();
    for (PmcGroupDto group : groups) {
      PmcGroup gr = pmcGroupRepo.findOne(group.getGuid());
      if (gr != null) {
        user.getGroupList().add(gr);
      }
    }
    pmcUserDao.update(user);
    userCache.invalidate(user.getLoginName());
  }

  private void savePassword(Long guid, String password) {
    if (password.length() < 6) {
      throw new IllegalArgumentException("Password has to be at least 6 characters long.");
    }

    try {
      String hash = this.passwordService.createPassword(password);
      this.pmcUserDao
        .getEntityManager()
        .createNativeQuery("UPDATE PMC_USER set credentials=:credentials WHERE guid=:guid")
        .setParameter("guid", guid)
        .setParameter("credentials", hash)
        .executeUpdate();
    } catch (Exception e) {
      logger.warn("Error Setting password", e);
      throw new IllegalArgumentException("Server Error.");
    }

  }

  @Override
  public Set<PmcPermissionDto> getEffectivePermissionsForUser(Long guid) {
    PmcUser user = pmcUserDao.getById(guid);
    Set<PmcPermissionDto> perms = new HashSet<>();
    for (PmcGroup group : user.getGroupList()) {
      for (PmcPermission perm : group.getPermissions()) {
        PmcPermissionDto tmp = dozerMapper.map(perm, PmcPermissionDto.class);
        tmp.setSource("Group:" + group.getGroupName());
        perms.add(tmp);
        for (PmcRole role : group.getRoles()) {
          for (PmcPermission permission : role.getPermissions()) {
            PmcPermissionDto tmp2 = dozerMapper.map(permission, PmcPermissionDto.class);
            tmp2.setSource("Role:" + role.getName());
            perms.add(tmp2);
          }
        }
      }
    }
    for (PmcRole role : user.getRoleList()) {
      for (PmcPermission permission : role.getPermissions()) {
        PmcPermissionDto tmp = dozerMapper.map(permission, PmcPermissionDto.class);
        tmp.setSource("Role:" + role.getName());
        perms.add(tmp);
      }
    }
    for (PmcPermission pmcPermission : user.getPermissionList()) {
      PmcPermissionDto tmp = dozerMapper.map(pmcPermission, PmcPermissionDto.class);
      perms.add(tmp);
    }
    return perms;
  }

  @Override
  public Set<PmcRoleDto> getEffectiveRolesForUser(Long guid) {
    PmcUser user = pmcUserDao.getById(guid);
    Set<PmcRoleDto> roles = new HashSet<>();
    for (PmcGroup group : user.getGroupList()) {
      for (PmcRole role : group.getRoles()) {
        PmcRoleDto tmp = dozerMapper.map(role, PmcRoleDto.class);
        tmp.setSource("Group:" + group.getGroupName());
        roles.add(tmp);
      }
    }
    for (PmcRole pmcRole : user.getRoleList()) {
      PmcRoleDto tmp = dozerMapper.map(pmcRole, PmcRoleDto.class);
      roles.add(tmp);
    }
    return roles;
  }

  @Override
  public List<PmcUserDto> getUsersForRole(Long roleGuid) {
    List<PmcUser> users = pmcUserDao.getByRole(roleGuid);
    return dozerMapper.mapCollection(users, PmcUserDto.class);
  }

  @Override
  public List<PmcUserDto> getUsersForRoleWithInheritance(Long roleGuid) {
    List<PmcUser> users = pmcUserDao.getByRoleWithInheritance(roleGuid);
    return dozerMapper.mapCollection(users, PmcUserDto.class);
  }

  @Override
  public PmcUserDto createGroupAccount(String login, String firstName, String surName, String groupLongName, Set<String> roleNames) {
    String groupName = PmcGroupServiceImpl.getGroupNameFromLongName("ga", groupLongName);
    PmcGroupDto newGroup = pmcGroupService.createGroupWithRoles(groupName, groupLongName, roleNames);
    if (newGroup != null) {
      PmcUser user = new PmcUser();
      user.setLoginName(login);
      if (login.contains("@"))
        user.setEmail(login);
      user.setFirstName(firstName);
      user.setSurname(surName);
      PmcGroup group = pmcGroupRepo.findByGroupName(newGroup.getGroupName());
      Set<PmcGroup> groups = new HashSet<>();
      groups.add(group);
      user.setGroupList(groups);
      user.setGuid(idCounterDao.getNext(PmcUserService.USER));
      user = pmcUserDao.create(user);
      return dozerMapper.map(user, PmcUserDto.class);
    } else {
      logger.error("Could not create group account! Group already exists!");
      return null;
    }
  }

  @Override
  public void updateLoginTime(LocalDateTime loginTime, Long userGuid) {
    pmcUserDao.updateLoginTime(loginTime, userGuid);
  }

  @Override
  public void createUser(PmcUserDto pmcUserDto) {
    PmcUser pmcUser = dozerMapper.map(pmcUserDto, PmcUser.class);
    Long num = System.currentTimeMillis();
    pmcUser.setGuid(num);
    pmcUser = pmcUserRepo.save(pmcUser);
    pmcUserDao.getEntityManager().flush();
  }
}
