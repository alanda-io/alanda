package io.alanda.base.connector;

import java.util.Collection;

import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;


public interface PmcRoleConnector {

  Boolean isDefault();
  
  PmcRoleDto getRoleForId(Long guid);

  PmcRoleDto getRoleForIdName(String idName);
  
  Collection<PmcRoleInstanceDto> getRoleInstancesForPmcProject(
    PmcProjectDto project, PmcRoleDto role);

  Collection<PmcRoleInstanceDto> getRoleInstancesForRefObject(
    String refObjectType, Long refObjectId, PmcRoleDto role);

  void setRoleInstancesForPmcProject(
      PmcProjectDto project, Collection<PmcRoleInstanceDto> roleInstances);

  PmcRoleDto getRoleForRoleConsumer(Long roleConsumerGuid, String baseRoleIdName);
  
}
