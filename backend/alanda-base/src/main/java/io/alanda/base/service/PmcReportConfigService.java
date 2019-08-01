/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.service;

import java.util.List;
import java.util.Map;

import io.alanda.base.dto.PmcReportConfigDto;
import io.alanda.base.reporting.callback.Callback;
import io.alanda.base.reporting.format.ReportCellFormatter;

/**
 *
 * @author developer
 */
public interface PmcReportConfigService {
  
    public void initFormatters();
    
    public PmcReportConfigDto getReportConfigByName(String name);
    
    public PmcReportConfigDto getReportConfigFromTemplateString(String reportConfig);
    
    public List<PmcReportConfigDto> getReportBySendtime(String sendTime);
    
    public void storeReportConfig(PmcReportConfigDto configuration);
    
    public Map<String, ReportCellFormatter> getCellFormatters();
    
    public Map<String, Callback> getCallbacks();
    
    public List<PmcReportConfigDto> getReportsForDay(String day);
    
}
