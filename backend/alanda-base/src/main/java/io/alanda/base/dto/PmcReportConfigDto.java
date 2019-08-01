/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.alanda.base.dto.reporting.ReportSheetDto;



/**
 *
 * @author developer
 */
public class PmcReportConfigDto implements Serializable {

  private static final long serialVersionUID = -4409160861421923541L;

  Long guid;
  
  Long version;

  String reportName;

  String recipients;

  String sendTime;
  
  String subject;
    
  Boolean linePerProcess;

  String query;

  String secondaryQuery;

  String primaryPostProcessor;

  String secondaryPostProcessor;
  
  String type;

  List<ReportSheetDto> sheets;

  public PmcReportConfigDto() {
    sheets = new ArrayList<>();
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getReportName() {
      return reportName;
  }

  public void setReportName(String reportName) {
      this.reportName = reportName;
  }

  public String getRecipients() {
      return recipients;
  }

  public String[] getRecipientsArray() {
    return this.recipients.split(";");
  }
  
  public void setRecipients(String recipients) {
      this.recipients = recipients;
  }

  public String getSendTime() {
      return sendTime;
  }

  public void setSendTime(String sendTime) {
      this.sendTime = sendTime;
  }

  public List<ReportSheetDto> getSheets() {
    return sheets;
  }

  public void setSheets(List<ReportSheetDto> sheets) {
    this.sheets = sheets;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Boolean getLinePerProcess() {
    if (linePerProcess == null) return false;
    return linePerProcess;
  }

  public void setLinePerProcess(Boolean linePerProcess) {
    this.linePerProcess = linePerProcess;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getSecondaryQuery() {
    return secondaryQuery;
  }

  public void setSecondaryQuery(String secondaryQuery) {
    this.secondaryQuery = secondaryQuery;
  }

  public String getPrimaryPostProcessor() {
    return primaryPostProcessor;
  }

  public void setPrimaryPostProcessor(String primaryPostProcessor) {
    this.primaryPostProcessor = primaryPostProcessor;
  }

  public String getSecondaryPostProcessor() {
    return secondaryPostProcessor;
  }

  public void setSecondaryPostProcessor(String secondaryPostProcessor) {
    this.secondaryPostProcessor = secondaryPostProcessor;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
