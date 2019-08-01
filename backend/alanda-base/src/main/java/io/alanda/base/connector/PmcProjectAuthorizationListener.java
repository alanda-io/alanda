package io.alanda.base.connector;

import java.util.Map;
import java.util.Set;

import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcProjectDto;

public interface PmcProjectAuthorizationListener {

  public enum AuthorizationResult {
      DENIED(0),
      READ(100),
      DELETE(200),
      CREATE(300),
      WRITE(400);

    private int level;

    private AuthorizationResult(int level) {
      this.level = level;
    }

    public AuthorizationResult combineAuthorizationResults(AuthorizationResult authResult) {
      if (this.level > authResult.level)
        return this;
      else
        return authResult;
    }

    public boolean isHigherOrEqualAuthorizationThan(AuthorizationResult authorizationToCompare) {
      return level >= authorizationToCompare.level;
    }

  }

  public String getListenerIdName();

//  public AuthorizationResult getAuthorizationStatusOfProject(PmcProjectDto pmcProject);

//  public AuthorizationResult getAuthorizationStatusOfProjectType(PmcProjectTypeDto projectType);

  Set<String> getReadRightGroups(PmcProjectDto project, Map<String, InternalContactDto> projectRoles);

}
