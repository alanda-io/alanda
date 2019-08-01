package io.alanda.base.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcProjectAuthorizationListener;
import io.alanda.base.dao.PmcProjectTypeDao;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.service.PmcAuthorizationService;

@ApplicationScoped
public class PmcAuthorizationServiceImpl implements PmcAuthorizationService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  
  @Inject
  private PmcProjectTypeDao pmcProjectTypeDao;
  
  @Inject
  private Instance<PmcProjectAuthorizationListener> pmcProjectAuthorizationListener;
  
  private Map<String, PmcProjectAuthorizationListener> listenerMap; 
  
  @PostConstruct
  private void initPmcProjectService() {
    listenerMap = new HashMap<>();
    for (PmcProjectAuthorizationListener listener : pmcProjectAuthorizationListener) {
      logger.info("Found PmcProjectAuthorizationListener with idName " + listener.getListenerIdName() + ".");
      listenerMap.put(listener.getListenerIdName(), listener);
    }
  }

  @Override
  public Set<String> getReadRightGoups(PmcProjectDto pmcProject, Map<String, InternalContactDto> projectRoles) {
    Set<String> groups = new HashSet<>();
    Collection<String> listenerIdNames = pmcProjectTypeDao.getById(pmcProject.getPmcProjectType().getGuid()).getListeners();

    for (String listenerIdName : listenerIdNames) {
      if (listenerMap.containsKey(listenerIdName)) {
        groups.addAll(listenerMap.get(listenerIdName).getReadRightGroups(pmcProject, projectRoles));
      }
    }
    return groups;

  }

//  @Override
//  public AuthorizationResult getAuthorizationStatusOfProject(PmcProjectDto pmcProject) {
//    
//    AuthorizationResult authResult = AuthorizationResult.DENIED;
//    
//    Collection<String> listenerIdNames = 
//        pmcProjectTypeDao.getById(pmcProject.getPmcProjectType().getGuid()).getListeners();
//    
//    for (String listenerIdName : listenerIdNames) {
//      if (listenerMap.containsKey(listenerIdName)) {
//        authResult = authResult.combineAuthorizationResults(
//            listenerMap.get(listenerIdName).getAuthorizationStatusOfProject(pmcProject));
//      }
//    }
//    return authResult;
//  }

//  @Override
//  public AuthorizationResult getAuthorizationStatusOfProjectType(PmcProjectTypeDto projectType) {
//    
//    AuthorizationResult authResult = AuthorizationResult.DENIED;
//    
//    Collection<String> listenerIdNames = 
//        pmcProjectTypeDao.getById(projectType.getGuid()).getListeners();
//    
//    for (String listenerIdName : listenerIdNames) {
//      if (listenerMap.containsKey(listenerIdName)) {
//        authResult = authResult.combineAuthorizationResults(
//            listenerMap.get(listenerIdName).getAuthorizationStatusOfProjectType(projectType));
//      }
//    }
//    return authResult;
//  }
  
}
