/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "PMC_REPORTCONFIG")
public class PmcReportConfig extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -1677028550515419612L;
    
    public static final String TYPE_ELASTIC = "elastic";
    public static final String TYPE_SQL = "sql";
    
    @Column(name= "REPORTNAME", unique = true)
    String reportName;
    
    @Column(name = "RECIPIENTS")
    String recipients;
    
    @Column(name = "SENDTIME")
    String sendTime;
    
    @Column(name = "CONFIG")
    String config;
    
    @Column(name = "SUBJECT")
    String subject;
    
    @Column(name = "LINEPERPROC")
    Boolean linePerProcess;
    
    @Column(name = "QUERYSTRING")
    String query;
    
    @Column(name = "SEC_QUERYSTRING")
    String secondaryQuery;

    @Column(name = "PRIMARY_POSTPROC")
    String primaryPostProcessor;
    
    @Column(name = "SECONDARY_POSTPROC")
    String secondaryPostProcessor;
    
    @Column(name = "TYPE")
    String type;
    
    public PmcReportConfig() {
      super();
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

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Boolean getLinePerProcess() {
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
