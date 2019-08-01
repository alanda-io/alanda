/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import io.alanda.base.dto.PmcReportConfigDto;
import io.alanda.base.dto.reporting.ElasticEntryDto;

/**
 *
 * @author developer
 */
public interface ReportService {
  
  String FILE_DATE_FORMAT = "_dd-MM-yyyy_HH_mm";
  
  byte[] createReportByProjectType(String projectType);
  
  SXSSFWorkbook createReportFromTemplateAndData(String template, List<ElasticEntryDto> data);

  byte[] getBytesFromWorkbook(SXSSFWorkbook workbook);
  
  PmcReportConfigDto getReportConfig(String name);

  void sendReport(String reportName);

  void sendReports();
  
  List<Map<String,Object>> queryDB(String sql);

}
