package io.alanda.rest.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.security.PmcShiroAuthorizationService;
import io.alanda.base.service.PmcPropertyService;

import io.alanda.rest.PmcPropertyRestService;

public class PmcPropertyRestServiceImpl implements PmcPropertyRestService {

  protected static final Logger logger = LoggerFactory.getLogger(PmcPropertyRestServiceImpl.class);

  @Inject
  private PmcPropertyService pmcPropertyService;
  
  @Inject
  private PmcShiroAuthorizationService pmcShiroAuthorizationService;

  @Override
  public Map<String, Object> get(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    Map<String, Object> result = new HashMap<>();
    result.put("value", pmcPropertyService.getProperty(entityId, entityType, pmcProjectGuid, key));
    return result;
  }

  @Override
  public Response set(PmcPropertyDto pmcPropertyDto) {

    pmcPropertyService.setProperty(
      pmcPropertyDto.getEntityId(),
      pmcPropertyDto.getEntityType(),
      pmcPropertyDto.getPmcProjectGuid(),
      pmcPropertyDto.getKey(),
      pmcPropertyDto.getValue(),
      PmcPropertyService.PmcPropertyType.valueOf(pmcPropertyDto.getValueType()));
    return Response.ok().build();

  }

  @Override
  public List<PmcPropertyDto> getPropertyListWithPrefix(Long pmcProjectGuid, String keyPrefix, String delim) {
    return pmcPropertyService.getPropertiesForProjectWithPrefix(pmcProjectGuid, keyPrefix, delim);
  }
  
  @Override
  public Map<String, Object> getPropertiesMap(Long pmcProjectGuid) {
    return pmcPropertyService.getPropertiesMapForProject(pmcProjectGuid);
  }
  
  @Override
  public Response deleteProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    String authKey = pmcShiroAuthorizationService.getAuthKeyForProperty(pmcProjectGuid, key, "write");
    boolean ret = pmcShiroAuthorizationService.checkPermission(authKey);
    if (!ret) throw new ForbiddenException("You are not allowed to access the Property!");
    pmcPropertyService.deleteProperty(entityId, entityType, pmcProjectGuid, key);
    return Response.ok().build();
  }

}
