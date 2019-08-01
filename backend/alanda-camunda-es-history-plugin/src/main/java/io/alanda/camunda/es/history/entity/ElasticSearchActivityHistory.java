/**
 * 
 */
package io.alanda.camunda.es.history.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;

/**
 * @author jlo
 */
public class ElasticSearchActivityHistory implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -7285254500923664103L;

  protected String activityInstanceId;

  protected String activityId;

  protected String activityName;

  protected Long durationInMillis;

  protected Date startTime;

  protected Date endTime;

  protected Date dueDate;

  protected String activityType;

  protected String parentActivityInstanceId;

  protected String assignee;

  protected String taskId;

  protected int activityInstanceState;

  protected String calledProcessInstanceId;

  protected String eventType;

  public ElasticSearchActivityHistory(HistoricActivityInstanceEventEntity activityInstanceEvent) {
    this.activityInstanceId = activityInstanceEvent.getActivityInstanceId();
    this.activityId = activityInstanceEvent.getActivityId();
    this.activityName = activityInstanceEvent.getActivityName();
    this.activityType = activityInstanceEvent.getActivityType();
    this.setParentActivityInstanceId(activityInstanceEvent.getParentActivityInstanceId());
    this.assignee = activityInstanceEvent.getAssignee();
    this.durationInMillis = activityInstanceEvent.getDurationInMillis();
    this.startTime = activityInstanceEvent.getStartTime();
    this.endTime = activityInstanceEvent.getEndTime();
    this.taskId = activityInstanceEvent.getTaskId();
    this.eventType = activityInstanceEvent.getEventType();
    this.activityInstanceState = activityInstanceEvent.getActivityInstanceState();
    this.calledProcessInstanceId = activityInstanceEvent.getCalledProcessInstanceId();
  }

  public ElasticSearchActivityHistory(HistoricTaskInstanceEventEntity taskHistoryEvent) {
    this.activityInstanceId = taskHistoryEvent.getActivityInstanceId();
    this.activityId = taskHistoryEvent.getTaskDefinitionKey();
    this.activityName = taskHistoryEvent.getName();
    this.activityType = "userTask";

    this.assignee = taskHistoryEvent.getAssignee();
    this.durationInMillis = taskHistoryEvent.getDurationInMillis();
    this.startTime = taskHistoryEvent.getStartTime();
    this.endTime = taskHistoryEvent.getEndTime();
    this.taskId = taskHistoryEvent.getTaskId();
    this.eventType = taskHistoryEvent.getEventType();
    this.dueDate = taskHistoryEvent.getDueDate();

  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("activityInstanceId", this.activityInstanceId);
    map.put("activityId", this.activityId);
    map.put("activityName", this.activityName);
    map.put("durationInMillis", this.durationInMillis);
    map.put("startTime", this.startTime);
    map.put("endTime", this.endTime);
    map.put("dueDate", this.dueDate);
    map.put("activityType", this.activityType);
    map.put("parentActivityInstanceId", this.parentActivityInstanceId);
    map.put("assignee", this.assignee);
    map.put("taskId", this.taskId);
    map.put("activityInstanceState", this.activityInstanceState);
    map.put("eventType", this.eventType);
    map.put("calledProcessInstanceId", this.calledProcessInstanceId);

    return map;

  }

  public ElasticSearchActivityHistory() {
  }

  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public int getActivityInstanceState() {
    return activityInstanceState;
  }

  public void setActivityInstanceState(int activityInstanceState) {
    this.activityInstanceState = activityInstanceState;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public Long getDurationInMillis() {
    return durationInMillis;
  }

  public void setDurationInMillis(Long durationInMillis) {
    this.durationInMillis = durationInMillis;
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

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public String getParentActivityInstanceId() {
    return parentActivityInstanceId;
  }

  public void setParentActivityInstanceId(String parentActivityInstanceId) {
    this.parentActivityInstanceId = parentActivityInstanceId;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getCalledProcessInstanceId() {
    return calledProcessInstanceId;
  }

  public void setCalledProcessInstanceId(String calledProcessInstanceId) {
    this.calledProcessInstanceId = calledProcessInstanceId;
  }

  @Override
  public String toString() {
    return "ElasticSearchActivityHistory [" +
      (activityInstanceId != null ? "activityInstanceId=" + activityInstanceId + ", " : "") +
      (activityId != null ? "activityId=" + activityId + ", " : "") +
      (activityName != null ? "activityName=" + activityName + ", " : "") +
      (durationInMillis != null ? "durationInMillis=" + durationInMillis + ", " : "") +
      (startTime != null ? "startTime=" + startTime + ", " : "") +
      (endTime != null ? "endTime=" + endTime + ", " : "") +
      (activityType != null ? "activityType=" + activityType + ", " : "") +
      (parentActivityInstanceId != null ? "parentActivityInstanceId=" + parentActivityInstanceId + ", " : "") +
      (assignee != null ? "assignee=" + assignee + ", " : "") +
      (taskId != null ? "taskId=" + taskId + ", " : "") +
      "activityInstanceState=" +
      activityInstanceState +
      ", " +
      (calledProcessInstanceId != null ? "calledProcessInstanceId=" + calledProcessInstanceId + ", " : "") +
      (eventType != null ? "eventType=" + eventType : "") +
      "]";
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

}
