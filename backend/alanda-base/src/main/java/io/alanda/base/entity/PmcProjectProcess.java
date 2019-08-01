package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import io.alanda.base.type.ProcessRelation;
import io.alanda.base.type.ProcessResultStatus;
import io.alanda.base.type.ProcessState;

@Entity
@NamedQueries({
  @NamedQuery(name = "PmcProjectProcess.getMainProcessByProject", query = "SELECT p FROM PmcProjectProcess p WHERE RELATION = 'MAIN' AND PROJECT = :pmcProjectGuid"),
  @NamedQuery(name = "PmcProjectProcess.getAllChildProcesses", query = "SELECT p FROM PmcProjectProcess p WHERE RELATION = 'CHILD' AND PROJECT = :pmcProjectGuid"),})
@Table(name = "PMC_PROJECT_PROCESS")
public class PmcProjectProcess extends AbstractAuditEntity {

  @ManyToOne
  @JoinColumn(name = "PROJECT")
  PmcProject pmcProject;

  @Column(name = "PROCESS_INSTANCE_ID")
  String processInstanceId;

  @Column(name = "PARENT_EXECUTION_ID")
  String parentExecutionId;

  @Column(name = "STATUS")
  @Enumerated(EnumType.STRING)
  ProcessState status;

  @Column(name = "RELATION")
  @Enumerated(EnumType.STRING)
  ProcessRelation relation;

  @Column(name = "WORK_DETAILS")
  String workDetails;

  @Column(name = "PROCESS_KEY")
  String processKey;

  @Column(name = "BUSINESS_OBJECT")
  String businessObject;

  @Column(name = "LABEL")
  String label;

  @Column(name = "PHASE_NAME")
  String phase;

  @Column(name = "RESULT_STATUS")
  @Enumerated(EnumType.STRING)
  ProcessResultStatus resultStatus;

  @Column(name = "RESULT_COMMENT")
  String resultComment;

  public PmcProject getPmcProject() {
    return pmcProject;
  }

  public void setPmcProject(PmcProject pmcProject) {
    this.pmcProject = pmcProject;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public ProcessState getStatus() {
    return status;
  }

  public void setStatus(ProcessState status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "PmcProjectProcess [" +
      (getGuid() != null ? "guid=" + getGuid() + ", " : "") +
      (pmcProject != null ? "pmcProject=" + pmcProject.getGuid() + ", " : "") +
      (processInstanceId != null ? "processInstanceId=" + processInstanceId + ", " : "") +
      (parentExecutionId != null ? "parentExecutionId=" + parentExecutionId + ", " : "") +
      (status != null ? "status=" + status + ", " : "") +
      (relation != null ? "relation=" + relation + ", " : "") +
      (workDetails != null ? "workDetails=" + workDetails + ", " : "") +
      (processKey != null ? "processKey=" + processKey + ", " : "") +
      (businessObject != null ? "businessObject=" + businessObject + ", " : "") +
      (label != null ? "label=" + label : "") +
      "]";
  }

  public ProcessRelation getRelation() {
    return relation;
  }

  public void setRelation(ProcessRelation relation) {
    this.relation = relation;
  }

  public String getWorkDetails() {
    return workDetails;
  }

  public void setWorkDetails(String workDetails) {
    this.workDetails = workDetails;
  }

  public String getProcessKey() {
    return processKey;
  }

  public void setProcessKey(String processKey) {
    this.processKey = processKey;
  }

  public String getBusinessObject() {
    return businessObject;
  }

  public void setBusinessObject(String businessObject) {
    this.businessObject = businessObject;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getParentExecutionId() {
    return parentExecutionId;
  }

  public void setParentExecutionId(String parentExecutionId) {
    this.parentExecutionId = parentExecutionId;
  }

  public ProcessResultStatus getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(ProcessResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }

  public String getResultComment() {
    return resultComment;
  }

  public void setResultComment(String resultComment) {
    this.resultComment = resultComment;
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase;
  }

}
