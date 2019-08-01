/**
 * 
 */
package io.alanda.rest.api;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.security.PmcShiroAuthorizationService;
import io.alanda.base.service.PmcHistoryService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.util.cache.UserCache;

/**
 * @author jlo
 */
@Named("pmcRestApi")
@Singleton
@ApplicationScoped
public class PmcRestApiImpl implements PmcRestApi {

  @Inject
  PmcProjectService pmcProjectService;

  @Inject
  PmcProjectDao pmcProjectDao;

  @Inject
  PmcHistoryService pmcHistoryService;

  @Inject
  private PmcShiroAuthorizationService pmcShiroAuthorizationService;

  @Inject
  private UserCache userCache;

  @Override
  public PmcProjectRestApi getPmcProjectRestApi() {
    return new PmcProjectRestApiImpl(this);
  }

  public PmcShiroAuthorizationService getPmcShiroAuthorizationService() {
    return pmcShiroAuthorizationService;
  }

  public PmcProjectService getPmcProjectService() {
    return pmcProjectService;
  }

  public UserCache getUserCache() {
    return userCache;
  }

  public PmcProjectDao getPmcProjectDao() {
    return pmcProjectDao;
  }

  public PmcHistoryService getPmcHistoryService() {
    return pmcHistoryService;
  }

  @Override
  public Map<String, Object> getSystemInfo() {
    Map<String, Object> theMap = new HashMap<>();
    theMap.put("product", "BPMC");
    theMap.put("version", "1.0");
    return theMap;
  }

}
