/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import io.alanda.base.dao.PmcReportConfigDao;
import io.alanda.base.dto.PmcReportConfigDto;
import io.alanda.base.dto.reporting.ReportSheetDto;
import io.alanda.base.entity.PmcReportConfig;
import io.alanda.base.reporting.callback.Callback;
import io.alanda.base.reporting.format.ReportCellFormatter;
import io.alanda.base.service.PmcReportConfigService;
import io.alanda.base.util.DozerMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author developer
 */
@Stateless
public class PmcReportConfigServiceImpl implements PmcReportConfigService {

  private final org.slf4j.Logger logger = LoggerFactory.getLogger(PmcReportConfigServiceImpl.class);

    
  @Inject
  private PmcReportConfigDao pmcReportConfigDao;
    
  @Inject
  private DozerMapper dozerMapper;
  
  @Inject
  private Instance<ReportCellFormatter> reportCellFormatters;
  
  @Inject
  private Instance<Callback> reportCallbacks;

  private final ObjectMapper objectMapper = new ObjectMapper();
  
  private Map<String, ReportCellFormatter> cellFormatters;
  
  private Map<String, Callback> callbacks;
  
  @PostConstruct
  private void initService() {
    this.cellFormatters = new HashMap<>();
    this.callbacks = new HashMap<>();
    for (ReportCellFormatter cellFormatter : reportCellFormatters) {
      cellFormatters.put(cellFormatter.getName(), cellFormatter);
    }
    for (Callback callback : reportCallbacks) {
      callbacks.put(callback.getName(), callback);
    }
  }
  
  @Override
  public void initFormatters() {
    for (ReportCellFormatter cellFormatter: cellFormatters.values()) {
      cellFormatter.init();
    }
  }
  
  @Override
  public PmcReportConfigDto getReportConfigByName(String name) {
    try {
      PmcReportConfig config = pmcReportConfigDao.getReportByName(name);
      PmcReportConfigDto configDto = dozerMapper.map(config, PmcReportConfigDto.class);
      try {
        List<ReportSheetDto> sheets = objectMapper.readValue(config.getConfig(), new TypeReference<List<ReportSheetDto>>() {});
        configDto.setSheets(sheets);
      } catch (IOException ex) {
          logger.warn("Could not unmarshal configuration object for report " + name, ex);
      }
      return configDto;
    } catch (RuntimeException ex) {
      logger.info("Error parsing reportConfig: " + name + ": " + ex.getMessage());
      throw ex;
    }

  }
  
   @Override
  public PmcReportConfigDto getReportConfigFromTemplateString(String reportConfig) {
    PmcReportConfigDto configDto = null;
    try {
      List<ReportSheetDto> sheets = objectMapper.readValue(reportConfig, new TypeReference<List<ReportSheetDto>>() {});
      configDto = new PmcReportConfigDto();
      configDto.setSheets(sheets);
      configDto.setVersion(1l);
    } catch (IOException ex) {
        logger.warn("Could not unmarshal configuration object for report ", ex);
    }

    return configDto;
  }
  
  @Override
  public List<PmcReportConfigDto> getReportBySendtime(String sendTime) {
    List<PmcReportConfig> configs = pmcReportConfigDao.getReportBySendtime(sendTime);
    return dozerMapper.mapCollection(configs, PmcReportConfigDto.class);
  }
  
  @Override
  public void storeReportConfig(PmcReportConfigDto configuration) {
      PmcReportConfig config = dozerMapper.map(configuration, PmcReportConfig.class);
      try {
          config.setConfig(objectMapper.writeValueAsString(configuration.getSheets()));
      } catch (JsonProcessingException ex) {
          logger.warn("Could not marshall configuration for report " + configuration.getReportName(), ex);
      }
      pmcReportConfigDao.create(config);
  }

  @Override
  public Map<String, ReportCellFormatter> getCellFormatters() {
    return cellFormatters;
  }
  
  @Override
  public Map<String, Callback> getCallbacks() {
    return callbacks;
  }

  @Override
  public List<PmcReportConfigDto> getReportsForDay(String day) {
    List<PmcReportConfig> configs = pmcReportConfigDao.getReportsForDay(day);
    return dozerMapper.mapCollection(configs, PmcReportConfigDto.class);
  }
  
}
