package io.alanda.base.service;

import java.util.Map;
import java.util.Set;

import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcProjectDto;


public interface PmcAuthorizationService {
  
//  public AuthorizationResult getAuthorizationStatusOfProject(PmcProjectDto pmcProject);
  
//  public AuthorizationResult getAuthorizationStatusOfProjectType(PmcProjectTypeDto projectType);

  Set<String> getReadRightGoups(PmcProjectDto pmcProject, Map<String, InternalContactDto> projectRoles);

}
