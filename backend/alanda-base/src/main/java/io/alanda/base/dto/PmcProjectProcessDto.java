package io.alanda.base.dto;

import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.alanda.base.type.ProcessState;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PmcProjectProcessDto {

  Long guid;

  Long version;

  String processInstanceId;

  String parentExecutionId;

  String status;

  String relation;

  String workDetails;

  String processKey;

  String businessObject;

  String label;

  String phase;

  Collection<PmcTaskDto> tasks;

  String resultStatus;

  String resultComment;

  Boolean customRefObject;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date startTime;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date endTime;

  String processDefinitionId;

  public boolean isActiveOrSuspended() {
    return ProcessState.ACTIVE.toString().equals(this.status) || ProcessState.SUSPENDED.toString().equals(this.status);
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public String getWorkDetails() {
    return workDetails;
  }

  public void setWorkDetails(String workDetails) {
    this.workDetails = workDetails;
  }

  public String getBusinessObject() {
    return businessObject;
  }

  public void setBusinessObject(String businessObject) {
    this.businessObject = businessObject;
  }

  public String getProcessKey() {
    return processKey;
  }

  public void setProcessKey(String processKey) {
    this.processKey = processKey;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Collection<PmcTaskDto> getTasks() {
    return tasks;
  }

  public void setTasks(Collection<PmcTaskDto> tasks) {
    this.tasks = tasks;
  }

  public String getParentExecutionId() {
    return parentExecutionId;
  }

  public void setParentExecutionId(String parentExecutionId) {
    this.parentExecutionId = parentExecutionId;
  }

  public String getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(String resultStatus) {
    this.resultStatus = resultStatus;
  }

  public String getResultComment() {
    return resultComment;
  }

  public void setResultComment(String resultComment) {
    this.resultComment = resultComment;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public Boolean getCustomRefObject() {
    return customRefObject;
  }

  public void setCustomRefObject(Boolean customRefObject) {
    this.customRefObject = customRefObject;
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase;
  }

  @Override
  public String toString() {
    return "PmcProjectProcessDto [guid=" +
      guid +
      ", processInstanceId=" +
      processInstanceId +
      ", status=" +
      status +
      ", relation=" +
      relation +
      ", workDetails=" +
      workDetails +
      ", processKey=" +
      processKey +
      ", businessObject=" +
      businessObject +
      ", version=" +
      version +
      "]";
  }
}
