package io.alanda.rest.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcDepartmentDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcDepartmentService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcRoleService;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.UserContext;
import io.alanda.base.util.cache.CacheStatsDto;
import io.alanda.base.util.cache.UserCache;

import io.alanda.rest.PagedSearchOptions;
import io.alanda.rest.UserRestService;
import io.alanda.rest.util.RepoHelpers;

import com.google.common.cache.CacheStats;

public class UserRestServiceImpl implements UserRestService {

  public static final String SECURITY_PACKAGE = "NTLM";

  private final Logger logger = LoggerFactory.getLogger(UserRestServiceImpl.class);

  @Context
  private HttpServletRequest request;

  @Inject
  private UserCache userCache;

  @Inject
  private PmcUserService pmcUserService;

  @Inject
  private PmcProjectService pmcProjectService;

  @Inject
  private PmcRoleService pmcRoleService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private PmcDepartmentService pmcDepartmentService;

  @Override
  public PmcUserDto getCurrentUser() {
    PmcUserDto ret = UserContext.getUser();
    if (SecurityUtils.getSubject().isRunAs()) {
      Object o = SecurityUtils.getSubject().getPreviousPrincipals().asList().get(0);
      String loginName = extractLoginName(o);
      ret.setRunAs(loginName);
    }

    return ret;
  }

  private String extractLoginName(Object principal) {
    String loginName = null;

    if (principal instanceof String) {
      loginName = (String) principal;
    } else if (principal instanceof Principal) {
      loginName = ((Principal) principal).getName();
    } else {
      loginName = principal.toString();
    }
    loginName = loginName.substring(loginName.indexOf("\\") + 1, loginName.length());
    return loginName;
  }

  @Override
  public PmcUserDto getCurrentUserShiro() {
    return userCache.get((String) SecurityUtils.getSubject().getPrincipal());
  }

  @Override
  public PmcUserDto runAsUser(String loginName) {
    if ( !UserContext.getUser().isAdmin()) {
      throw new ForbiddenException("Insufficient privileges!");
    }
    String currentUser = UserContext.getUser().getLoginName();
    PmcUserDto runAsUser = userCache.get(loginName);
    SecurityUtils.getSubject().runAs(runAsUser.getPrincipals());
    logger.info("User " + currentUser + " successfully switched to run as user: " + loginName);
    runAsUser.setRunAs(currentUser);
    return runAsUser;
  }

  @Override
  public PmcUserDto releaseRunAsUser() {
    if (SecurityUtils.getSubject().isRunAs()) {
      String runAsUser = UserContext.getUser().getLoginName();
      PrincipalCollection originalUser = SecurityUtils.getSubject().getPreviousPrincipals();
      Object o = originalUser.asList().get(0);
      String loginName = extractLoginName(o);
      PmcUserDto original = userCache.get(loginName);
      SecurityUtils.getSubject().releaseRunAs();
      logger.info("User " + runAsUser + " switched back to logged in user: " + loginName);
      return original;
    } else {
      return UserContext.getUser();
    }
  }

  @Override
  public PmcUserDto getByLoginName(String loginName) {
    if ( !UserContext.getUser().isAdmin()) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }
    return pmcUserService.getUserByLoginName(loginName, true);
  }

  @Override
  public List<PmcUserDto> getRepoUser(int pageNumber, int pageSize) {
    return pmcUserService.getRepoUser(pageNumber, pageSize);
  }

  PmcUserDto createPmcUserDto(PmcUserDto pmcUser) {
    if (pmcUser == null) {
      return null;
    }
    PmcUserDto u = new PmcUserDto();
    u.setGuid(pmcUser.getGuid());
    u.setEmail(pmcUser.getEmail());
    u.setFirstName(pmcUser.getFirstName());
    u.setLoginName(pmcUser.getLoginName());
    u.setGroups(new ArrayList<>());
    for (PmcGroupDto g : pmcUser.getGroupList()) {
      u.getGroups().add(g.getGroupName());
    }
    u.setMobile(pmcUser.getMobile());
    u.setSurname(pmcUser.getSurname());
    return u;
  }

  @Override
  public PmcUserDto getById(Long userId) {
    if (userId == null)
      throw new RuntimeException("No userId provided!");
    return createPmcUserDto(userCache.get(userId));
  }

  @Override
  public List<PmcUserDto> search(String text, String groupName, Boolean isContactRequired) {
    Long groupId = null;
    if (groupName != null) {
      PmcGroupDto dto = pmcUserService.getGroupByName(groupName);
      if (dto != null) {
        groupId = dto.getGuid();
      }
    }
    if (text == null) {
      text = "%";
    } else {
      text = text.toLowerCase();
      text = "%" + text + "%";
    }
    logger.info("Search: " + text + ", groupName: " + groupName + ", groupId: " + groupId);
    List<PmcUserDto> listDto = this.pmcUserService.getUsersByGroup(groupId, text, isContactRequired);
    return listDto;
  }

  @Override
  public PmcUserDto findResponsibleUser(String processInstanceId, String roleName) {
    String refObjectType = (String) runtimeService.getVariable(processInstanceId, ProcessVariables.REFOBJECTTYPE);
    Long pmcProjectGuid = (Long) runtimeService.getVariable(processInstanceId, ProcessVariables.PMC_PROJECT_GUID);
    Long refObjectId = (Long) runtimeService.getVariable(processInstanceId, ProcessVariables.REFOBJECTID);
    return pmcProjectService.getUserForRole(refObjectType, refObjectId, roleName, pmcProjectGuid);
  }

  @Override
  public Response setResponsibleUser(String processInstanceId, String groupName, long userId, String source) {
    Long pmcProjectGuid = (Long) runtimeService.getVariable(processInstanceId, ProcessVariables.PMC_PROJECT_GUID);
    PmcRoleInstanceDto rid = PmcRoleInstanceDto.forRoleAndUser(userId, groupName);
    pmcRoleService.setRoleInstancesForProject(pmcProjectGuid, Collections.singletonList(rid), source);
    return Response.accepted().build();
  }

  @Override
  public Map<String, CacheStatsDto> cacheStats() {
    Map<String, CacheStatsDto> map = new HashMap<>();
    for (Map.Entry<String, CacheStats> entry : userCache.getStats().entrySet()) {
      map.put(entry.getKey(), new CacheStatsDto(entry.getValue()));
    }
    return map;
  }

  @Override
  public Response cacheInvalidateAll() {
    userCache.invalidateAll();
    return Response.accepted().build();
  }

  @Override
  public Response saveRepoUser(PmcUserDto pmcUserDto) {
    if ( !UserContext.getUser().isAdmin()) {
      throw new javax.ws.rs.ForbiddenException("Access Denied");
    }

    if ( !pmcUserDto.getSso()) {
      if (StringUtils.isEmpty(pmcUserDto.getPassword())) {
        throw new IllegalArgumentException("Password missing.");
      }
      if ( !pmcUserDto.getPassword().equals(pmcUserDto.getPassword2())) {
        throw new IllegalArgumentException("Password mismatch.");
      }
    }
    pmcUserService.saveUser(pmcUserDto);
    return Response.ok().build();
  }

  @Override
  public PmcUserDto repoUserById(Long userId) {
    return pmcUserService.getRepoUserById(userId);
  }

  @Override
  public Response updateRepoUser(PmcUserDto pmcUserDto) {
    boolean modifyPw = false;
    if ( !StringUtils.isEmpty(pmcUserDto.getPassword())) {
      if ( !pmcUserDto.getPassword().equals(pmcUserDto.getPassword2())) {
        throw new IllegalArgumentException("Password mismatch.");
      }
      modifyPw = true;
    }

    if ( !UserContext.getUser().isAdmin()) {
      if ( !pmcUserDto.getGuid().equals(UserContext.getUser().getGuid())) {
        throw new javax.ws.rs.ForbiddenException("Access denied");
      }
      if (pmcUserDto.getGroups() != null) {
        throw new javax.ws.rs.ForbiddenException("Access denied");
      }
      if (modifyPw) {
        if ( !pmcUserService.checkPassword(pmcUserDto.getGuid(), pmcUserDto.getOldPassword())) {
          throw new IllegalArgumentException("The old password you provided is wrong.");
        }
      }
    }

    logger.info("User: " + pmcUserDto.getLoginName());
    PmcUserDto returnPmcUserDto = pmcUserService.updateRepoUser(pmcUserDto);
    userCache.invalidate(pmcUserDto.getLoginName());

    return Response.ok(returnPmcUserDto).build();
  }

  @Override
  public List<String> getUsernames() {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcUserService.getUsernames();
  }

  @Override
  public List<PmcUserDto> getUsersByGroupId(Long groupId) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcUserService.getUserByGroupId(groupId);
  }

  @Override
  public PagedResultDto<PmcUserDto> getPagedResult(PagedSearchOptions serverOptions) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    logger.info("called getUserRepo");
    Integer pageNumber = serverOptions.getPageNumber();
    if (pageNumber == null)
      pageNumber = 1;
    Integer pageSize = serverOptions.getPageSize();
    if (pageSize == null)
      pageSize = 15;
    Map<String, Object> filterOptions = serverOptions.getFilterOptions();
    PmcUserDto userForSearchByExample = new PmcUserDto();
    userForSearchByExample.setGuid((Long) filterOptions.get("guid"));
    userForSearchByExample.setLoginName((String) filterOptions.get("loginName"));
    userForSearchByExample.setFirstName((String) filterOptions.get("firstName"));
    userForSearchByExample.setSurname((String) filterOptions.get("surname"));
    userForSearchByExample.setEmail((String) filterOptions.get("email"));
    userForSearchByExample.setMobile((String) filterOptions.get("mobile"));
    Map<String, Object> sortOptions = serverOptions.getSortOptions();
    Sort sort = RepoHelpers.parseSort(sortOptions, "loginName");
    return pmcUserService.findUser(userForSearchByExample, pageNumber, pageSize, sort);
  }

  @Override
  public List<PmcPermissionDto> getPermissionsForUser(Long userId) {
    return pmcUserService.getPermissionsForUser(userId);
  }

  @Override
  public Collection<PmcPermissionDto> getEffectivePermissionsForUser(Long userId) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcUserService.getEffectivePermissionsForUser(userId);
  }

  @Override
  public List<PmcRoleDto> getRolesForUser(Long userId) {
    return pmcUserService.getRolesForUser(userId);
  }

  @Override
  public Collection<PmcRoleDto> getEffectiveRolesForUser(Long userId) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcUserService.getEffectiveRolesForUser(userId);
  }

  @Override
  public Response updatePermissionsForUser(Long userId, List<PmcPermissionDto> permissons) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    pmcUserService.updatePermissionsForUser(userId, permissons);
    return Response.ok().build();
  }

  @Override
  public Response updateRolesForUser(Long userId, List<PmcRoleDto> roles) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    pmcUserService.updateRolesForUser(userId, roles);
    return Response.ok().build();
  }

  @Override
  public Response updateGroupsForUser(Long userId, List<PmcGroupDto> groups) {
    pmcUserService.updateGroupsForUser(userId, groups);
    return Response.ok().build();
  }

  @Override
  public List<PmcUserDto> getUsersForRole(Long roleId) {
    return pmcUserService.getUsersForRoleWithInheritance(roleId);
  }

  @Override
  public PmcUserDto createGroupAcount(PmcUserDto accountInfo) {
    return pmcUserService
      .createGroupAccount(
        accountInfo.getLoginName(),
        accountInfo.getFirstName(),
        accountInfo.getSurname(),
        accountInfo.getDisplayName(),
        accountInfo.getRoles());
  }

  @Override
  public List<PmcDepartmentDto> getDepartmentList() {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    return pmcDepartmentService.getDepartmentList();
  }

  @Override
  public List<PmcGroupDto> getGroupsForUser(String loginname) {
    if ( !UserContext.getUser().isAdmin())
      throw new javax.ws.rs.ForbiddenException("Access denied");
    PmcUserDto user = userCache.get(loginname);
    if (user != null) {
      return user.getGroupList();
    }
    return null;
  }

  @Override
  public Response createUser(PmcUserDto pmcUserDto) {
    pmcUserService.createUser(pmcUserDto);
    return Response.ok().build();
  }

}
