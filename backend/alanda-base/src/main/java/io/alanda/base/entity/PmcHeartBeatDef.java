package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_HEARTBEAT_DEF")
public class PmcHeartBeatDef extends AbstractAuditEntity {

  public PmcHeartBeatDef() {
  }

  @Column(name = "NAME")
  private String name;

  @Column(name = "CRON")
  private String cronExpression;

  @Column(name = "PRIORITY")
  private int priority;

  @Column(name = "DURATION_MINUTES")
  private int duration;

  @Column(name = "TOLERANCE")
  private int tolerance;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getTolerance() {
    return tolerance;
  }

  public void setTolerance(int tolerance) {
    this.tolerance = tolerance;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

}
