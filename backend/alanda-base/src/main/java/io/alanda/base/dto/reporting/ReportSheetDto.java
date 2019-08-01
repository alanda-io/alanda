/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.dto.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author developer
 */
public class ReportSheetDto {

  String sheetName;

  String reportTemplate;

  Map<String, Object> queryParams;

  List<ReportColumnDto> columns;

  Boolean includeCountHeader;

  Integer colFreezePane;

  public ReportSheetDto() {
    columns = new ArrayList<>();
  }

  public String getSheetName() {
    return sheetName;
  }

  public void setSheetName(String sheetName) {
    this.sheetName = sheetName;
  }

  public List<ReportColumnDto> getColumns() {
    return columns;
  }

  public void setColumns(List<ReportColumnDto> columns) {
    this.columns = columns;
  }

  public String getReportTemplate() {
    return reportTemplate;
  }

  public void setReportTemplate(String reportTemplate) {
    this.reportTemplate = reportTemplate;
  }

  public Map<String, Object> getQueryParams() {
    return queryParams;
  }

  public void setQueryParams(Map<String, Object> queryParams) {
    this.queryParams = queryParams;
  }

  public Boolean isIncludeCountHeader() {
    if (this.includeCountHeader == null)
      return true;
    return includeCountHeader;
  }

  public void setIncludeCountHeader(Boolean includeCountHeader) {
    this.includeCountHeader = includeCountHeader;
  }

  public Integer getColFreezePane() {
    return colFreezePane;
  }

  public void setColFreezePane(Integer colFreezePane) {
    this.colFreezePane = colFreezePane;
  }

}
