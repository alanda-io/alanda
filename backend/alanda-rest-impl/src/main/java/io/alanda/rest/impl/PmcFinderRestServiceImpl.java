package io.alanda.rest.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.alanda.base.service.PmcFinderService;

import io.alanda.rest.PmcFinderRestService;

public class PmcFinderRestServiceImpl implements PmcFinderRestService {

  @Inject
  private PmcFinderService pmcFinderService;

  @Override
  public List<Map<String, Object>> searchProcessInstances(String searchTerm, Boolean onlyActive) {
    return pmcFinderService.getFinderGridSearchResult(searchTerm, onlyActive);
  }

  @Override
  public List<Map<String, Object>> getProcessActivities(String pid, Boolean extendedView) {
    return pmcFinderService.getPIOActivitiesResult(pid, extendedView);
  }
}
