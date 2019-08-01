package io.alanda.base.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.springframework.data.domain.Sort;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcUserDto;

public interface PmcUserService {

  public static final String USER = "User";

  public static final String SOURCE_PMC = "PMC";

  PmcUserDto getUserByUserId(Long userId);

  PmcUserDto getUserByLoginName(String loginName);

  PmcUserDto getUserByLoginName(String loginName, boolean includeGroups);

  List<PmcUserDto> getUsersByGroup(Long groupId, String search);

  Boolean isUserInGroup(Long userId, String groupIdName);

  Boolean isUserInGroup(Long userId, Long groupId);

  PmcGroupDto getGroupByName(String groupName);

  PmcGroupDto getGroupById(Long guid);

  Response saveUser(PmcUserDto pmcUserDto);

  void createUser(PmcUserDto pmcUserDto);

  PmcUserDto getRepoUserById(Long userId);

  PmcUserDto updateRepoUser(PmcUserDto pmcUserDto);

  List<String> getUsernames();

  List<PmcUserDto> getRepoUser(int pageNumber, int pageSize);

  List<PmcUserDto> getUserByGroupId(Long groupId);

  PagedResultDto<PmcUserDto> findUser(PmcUserDto exampleUserDto, int pageNumber, int pageSize, Sort sort);

  boolean checkPassword(Long guid, String password);

  List<PmcUserDto> getUsersByGroup(Long groupId, String text, Boolean isContactRequired);

  List<PmcUserDto> getUsersByLoginNames(Collection<String> loginNames);

  List<PmcPermissionDto> getPermissionsForUser(Long guid);

  Set<PmcPermissionDto> getEffectivePermissionsForUser(Long guid);

  List<PmcRoleDto> getRolesForUser(Long guid);

  Set<PmcRoleDto> getEffectiveRolesForUser(Long guid);

  void updatePermissionsForUser(Long guid, List<PmcPermissionDto> permissions);

  void updateRolesForUser(Long guid, List<PmcRoleDto> roles);

  void updateGroupsForUser(Long guid, List<PmcGroupDto> groups);

  List<PmcUserDto> getUsersForRole(Long roleGuid);

  List<PmcUserDto> getUsersForRoleWithInheritance(Long roleGuid);

  PmcUserDto createGroupAccount(String login, String firstName, String surName, String groupLongName, Set<String> roleNames);

  void updateLoginTime(LocalDateTime loginTime, Long userGuid);
}
