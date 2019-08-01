package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.service.PmcListenerService;

@Singleton
public class PmcListenerServiceImpl implements PmcListenerService {

  protected static final Logger logger = LoggerFactory.getLogger(PmcListenerServiceImpl.class);

  @Inject
  private PmcProjectDao pmcProjectDao;

  @Inject
  private Instance<PmcProjectListener> pmcListener;

  private Map<String, PmcProjectListener> startListenerMap;

  @PostConstruct
  private void initPmcListenerService() {
    startListenerMap = new HashMap<>();
    for (PmcProjectListener listener : pmcListener) {
      logger.info("init PmcListenerService: adding PmcListener with idName " + listener.getListenerIdName());
      startListenerMap.put(listener.getListenerIdName(), listener);
    }
  }

  @Override
  public Collection<PmcProjectListener> getListenerForProject(Long guid) {
    Collection<PmcProjectListener> listener = new ArrayList<>();
    PmcProject p = pmcProjectDao.getById(guid);
    if (p.getPmcProjectType().getListeners() != null) {
      for (String idName : p.getPmcProjectType().getListeners()) {
        if (startListenerMap.containsKey(idName)) {
          listener.add(startListenerMap.get(idName));
        }
      }
    }
    return listener;
  }
}
