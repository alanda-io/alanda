package io.alanda.rest.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.service.PmcMetricService;

import io.alanda.rest.PmcMetricRestService;

public class PmcMetricRestServiceImpl implements PmcMetricRestService {

  private static final Logger log = LoggerFactory.getLogger(PmcMetricRestServiceImpl.class);

  @Inject
  private PmcMetricService pmcMetricService;

  @Override
  public List<Map<String, Object>> getCreateAp() {
    return pmcMetricService.getCreateAp();
  }
}
