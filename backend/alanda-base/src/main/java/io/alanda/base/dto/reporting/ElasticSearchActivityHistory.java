/**
 * 
 */
package io.alanda.base.dto.reporting;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;

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

  protected String activityType;

  protected String parentActivityInstanceId;

  protected String assignee;

  protected String taskId;

  protected int activityInstanceState;

  protected String eventType;

  public ElasticSearchActivityHistory(HistoricActivityInstanceEventEntity activityInstanceEvent) {
    this.activityInstanceId = activityInstanceEvent.getActivityInstanceId();
    this.activityId = activityInstanceEvent.getActivityId();
    this.activityName = activityInstanceEvent.getActivityName();
    this.activityType = activityInstanceEvent.getActivityType();
    this.setParentActivityInstanceId(activityInstanceEvent.getParentActivityInstanceId());
    this.assignee = activityInstanceEvent.getAssignee();
    this.durationInMillis=activityInstanceEvent.getDurationInMillis();
    this.startTime=activityInstanceEvent.getStartTime();
    this.endTime=activityInstanceEvent.getEndTime();
    this.taskId=activityInstanceEvent.getTaskId();
    this.eventType = activityInstanceEvent.getEventType();
    this.activityInstanceState = activityInstanceEvent.getActivityInstanceState();
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("activityInstanceId", this.activityInstanceId);
    map.put("activityId", this.activityId);
    map.put("activityName", this.activityName);
    map.put("durationInMillis", this.durationInMillis);
    map.put("startTime", this.startTime);
    map.put("endTime", this.endTime);
    map.put("activityType", this.activityType);
    map.put("parentActivityInstanceId", this.parentActivityInstanceId);
    map.put("assignee", this.assignee);
    map.put("taskId", this.taskId);
    map.put("activityInstanceState", this.activityInstanceState);
    map.put("eventType", this.eventType);
    
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

}
