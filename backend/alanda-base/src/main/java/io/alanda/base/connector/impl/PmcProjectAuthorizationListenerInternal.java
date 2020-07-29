package io.alanda.base.connector.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcProjectAuthorizationListener;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcRoleService;
import io.alanda.base.service.PmcUserService;

public class PmcProjectAuthorizationListenerInternal implements PmcProjectAuthorizationListener {

  private static final Logger log = LoggerFactory.getLogger(PmcProjectAuthorizationListenerInternal.class);

  @Inject
  private PmcRoleService pmcRoleService;

  @Inject
  private PmcPropertyService pmcPropertyService;

  @Inject
  private PmcUserService pmcUserService;

  @Override
  public String getListenerIdName() {
    return "pmc-authorization";
  }

  @Override
  public Set<String> getReadRightGroups(PmcProjectDto project, Map<String, InternalContactDto> projectRoles) {
    log.debug("Get groups with read rights for project {}", project);

    Set<String> readRoles = new HashSet<>();

    // static roles defined in pmcProjectType
    PmcProjectTypeDto projectType = project.getPmcProjectType();
    initRoleFields(projectType);

    readRoles.addAll(projectType.getReadRightGroups());
    readRoles.addAll(projectType.getWriteRightGroups());
    readRoles.addAll(projectType.getDeleteRightGroups());
    readRoles.addAll(projectType.getCreateRightGroups());
    
    List<PmcPropertyDto> props = pmcPropertyService.getPropertiesForProject(project.getGuid());
    for (PmcPropertyDto prop : props) {
      if (prop.getKey().startsWith("role_")) {
        readRoles.add(prop.getValue());
      }
    }

    return readRoles;
  }


  private AuthorizationResult checkAuthWithProjectType(Long userId, PmcProjectTypeDto projectType) {

    AuthorizationResult authResult = AuthorizationResult.DENIED;

    initRoleFields(projectType);

    for (String groupName : projectType.getReadRightGroups()) {
      if (pmcUserService.isUserInGroup(userId, groupName)) {
        authResult = authResult.combineAuthorizationResults(AuthorizationResult.READ);
      }
    }
    for (String groupName : projectType.getDeleteRightGroups()) {
      if (pmcUserService.isUserInGroup(userId, groupName)) {
        authResult = authResult.combineAuthorizationResults(AuthorizationResult.DELETE);
      }
    }
    for (String groupName : projectType.getWriteRightGroups()) {
      if (pmcUserService.isUserInGroup(userId, groupName)) {
        authResult = authResult.combineAuthorizationResults(AuthorizationResult.WRITE);
      }
    }
    for (String groupName : projectType.getCreateRightGroups()) {
      if (pmcUserService.isUserInGroup(userId, groupName)) {
        authResult = authResult.combineAuthorizationResults(AuthorizationResult.CREATE);
      }
    }
    return authResult;
  }

  /**
   * @param projectType
   */
  private void initRoleFields(PmcProjectTypeDto projectType) {
    if (projectType.getReadRightGroups() == null)
      projectType.setReadRightGroups(parseStringToStringList(projectType.getReadRights()));
    if (projectType.getWriteRightGroups() == null)
      projectType.setWriteRightGroups(parseStringToStringList(projectType.getWriteRights()));
    if (projectType.getDeleteRightGroups() == null)
      projectType.setDeleteRightGroups(parseStringToStringList(projectType.getDeleteRights()));
    if (projectType.getCreateRightGroups() == null)
      projectType.setCreateRightGroups(parseStringToStringList(projectType.getCreateRights()));
  }

  private List<String> parseStringToStringList(String input) {
    if (org.apache.commons.lang3.StringUtils.isEmpty(input))
      return Collections.emptyList();
    String[] splitted = input.split(",");
    return Arrays.asList(splitted);

  }

}
