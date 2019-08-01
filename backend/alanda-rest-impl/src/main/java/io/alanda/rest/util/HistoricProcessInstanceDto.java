/**
 * 
 */
package io.alanda.rest.util;

import java.util.Date;

import org.camunda.bpm.engine.history.HistoricProcessInstance;

/**
 * @author jlo
 */
public class HistoricProcessInstanceDto {

  private String id;

  private String businessKey;

  private String processDefinitionId;

  private String processDefinitionKey;

  private Date startTime;

  private Date endTime;

  private Long durationInMillis;

  private String startUserId;

  private String startActivityId;

  private String deleteReason;

  private String superProcessInstanceId;

  private String superCaseInstanceId;

  private String caseInstanceId;

  public String getId() {
    return id;
  }

  public String getBusinessKey() {
    return businessKey;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public Long getDurationInMillis() {
    return durationInMillis;
  }

  public String getStartUserId() {
    return startUserId;
  }

  public String getStartActivityId() {
    return startActivityId;
  }

  public String getDeleteReason() {
    return deleteReason;
  }

  public String getSuperProcessInstanceId() {
    return superProcessInstanceId;
  }

  public String getSuperCaseInstanceId() {
    return superCaseInstanceId;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public static HistoricProcessInstanceDto fromHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {

    HistoricProcessInstanceDto dto = new HistoricProcessInstanceDto();

    dto.id = historicProcessInstance.getId();
    dto.businessKey = historicProcessInstance.getBusinessKey();
    dto.processDefinitionId = historicProcessInstance.getProcessDefinitionId();
    dto.processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
    dto.startTime = historicProcessInstance.getStartTime();
    dto.endTime = historicProcessInstance.getEndTime();
    dto.durationInMillis = historicProcessInstance.getDurationInMillis();
    dto.startUserId = historicProcessInstance.getStartUserId();
    dto.startActivityId = historicProcessInstance.getStartActivityId();
    dto.deleteReason = historicProcessInstance.getDeleteReason();
    dto.superProcessInstanceId = historicProcessInstance.getSuperProcessInstanceId();
    dto.superCaseInstanceId = historicProcessInstance.getSuperCaseInstanceId();
    dto.caseInstanceId = historicProcessInstance.getCaseInstanceId();

    return dto;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public void setDurationInMillis(Long durationInMillis) {
    this.durationInMillis = durationInMillis;
  }

  public void setStartUserId(String startUserId) {
    this.startUserId = startUserId;
  }

  public void setStartActivityId(String startActivityId) {
    this.startActivityId = startActivityId;
  }

  public void setDeleteReason(String deleteReason) {
    this.deleteReason = deleteReason;
  }

  public void setSuperProcessInstanceId(String superProcessInstanceId) {
    this.superProcessInstanceId = superProcessInstanceId;
  }

  public void setSuperCaseInstanceId(String superCaseInstanceId) {
    this.superCaseInstanceId = superCaseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId) {
    this.caseInstanceId = caseInstanceId;
  }

}
