package io.alanda.base.dto;

import java.util.List;

import io.alanda.base.type.PmcProjectState;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PmcTaskDto {

  @JsonProperty("actinst_type")
  private String activityInstanceType;

  @JsonProperty("task_id")
  private String taskId;//

  @JsonProperty("task_type")
  private String taskType;//

  @JsonProperty("task_name")
  private String taskName;

  @JsonProperty("object_name")
  private String objectName;

  @JsonProperty("object_id")
  private Long objectId;

  //TODO: AssigneeId <-> Assignee
  @JsonProperty("assignee_id")
  private String assigneeId;

  private String assignee;

  private String created;//

  private String due;

  private String formKey;

  private Integer priority;

  private String description;//?

  @JsonProperty("execution_id")
  private String executionId;//

  @JsonProperty("follow_up")
  private String followUp;

  @JsonProperty("process_definition_id")
  private String processDefinitionId;//

  @JsonProperty("process_instance_id")
  private String processInstanceId;//

  @JsonProperty("process_name")
  private String processName;//

  @JsonProperty("process_definition_key")
  private String processDefinitionKey;//

  @JsonProperty("process_package_key")
  private String processPackageKey;//?

  @JsonProperty("suspension_state")
  private boolean suspensionState;

  private String comment;

  private Long pmcProjectGuid;//

  private List<String> candidateGroups;

  private List<Long> candidateGroupIds;

  private PmcProjectState state;//

  public String getActivityInstanceType() {
    return activityInstanceType;
  }

  public void setActivityInstanceType(String activityInstanceType) {
    this.activityInstanceType = activityInstanceType;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getTaskType() {
    return taskType;
  }

  public void setTaskType(String taskType) {
    this.taskType = taskType;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  public String getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(String assigneeId) {
    this.assigneeId = assigneeId;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getDue() {
    return due;
  }

  public void setDue(String due) {
    this.due = due;
  }

  public String getFormKey() {
    return formKey;
  }

  public void setFormKey(String formKey) {
    this.formKey = formKey;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public String getFollowUp() {
    return followUp;
  }

  public void setFollowUp(String followUp) {
    this.followUp = followUp;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public String getProcessPackageKey() {
    return processPackageKey;
  }

  public void setProcessPackageKey(String processPackageKey) {
    this.processPackageKey = processPackageKey;
  }

  public boolean isSuspensionState() {
    return suspensionState;
  }

  public void setSuspensionState(boolean suspensionState) {
    this.suspensionState = suspensionState;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }

  public List<String> getCandidateGroups() {
    return candidateGroups;
  }

  public void setCandidateGroups(List<String> candidateGroups) {
    this.candidateGroups = candidateGroups;
  }

  @Override
  public String toString() {
    return "PmcTaskDto [" +
      (taskId != null ? "taskId=" + taskId + ", " : "") +
      (taskType != null ? "taskType=" + taskType + ", " : "") +
      (taskName != null ? "taskName=" + taskName + ", " : "") +
      (objectName != null ? "objectName=" + objectName + ", " : "") +
      (objectId != null ? "objectId=" + objectId + ", " : "") +
      (assigneeId != null ? "assigneeId=" + assigneeId + ", " : "") +
      (assignee != null ? "assignee=" + assignee + ", " : "") +
      (created != null ? "created=" + created + ", " : "") +
      (due != null ? "due=" + due + ", " : "") +
      (formKey != null ? "formKey=" + formKey + ", " : "") +
      (priority != null ? "priority=" + priority + ", " : "") +
      (description != null ? "description=" + description + ", " : "") +
      (executionId != null ? "executionId=" + executionId + ", " : "") +
      (followUp != null ? "followUp=" + followUp + ", " : "") +
      (processDefinitionId != null ? "processDefinitionId=" + processDefinitionId + ", " : "") +
      (processInstanceId != null ? "processInstanceId=" + processInstanceId + ", " : "") +
      (processName != null ? "processName=" + processName + ", " : "") +
      (processDefinitionKey != null ? "processDefinitionKey=" + processDefinitionKey + ", " : "") +
      (processPackageKey != null ? "processPackageKey=" + processPackageKey + ", " : "") +
      "suspensionState=" +
      suspensionState +
      ", " +
      (comment != null ? "comment=" + comment + ", " : "") +
      (pmcProjectGuid != null ? "pmcProjectGuid=" + pmcProjectGuid + ", " : "") +
      (candidateGroups != null ? "candidateGroups=" + candidateGroups : "") +
      "]";
  }

  public List<Long> getCandidateGroupIds() {
    return candidateGroupIds;
  }

  public void setCandidateGroupIds(List<Long> candidateGroupIds) {
    this.candidateGroupIds = candidateGroupIds;
  }

  public PmcProjectState getState() {
    return state;
  }

  public void setState(PmcProjectState state) {
    this.state = state;
  }

}
