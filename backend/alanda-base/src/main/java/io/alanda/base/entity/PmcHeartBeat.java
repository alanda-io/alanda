package io.alanda.base.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_HEARTBEAT")
public class PmcHeartBeat {

  public PmcHeartBeat() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_sequence")
  @SequenceGenerator(name = "entity_sequence", sequenceName = "SEQ_GUID_PK", allocationSize = 1)
  private Long guid;

  @Column(name = "NAME")
  private String name;

  @Column(name = "FINISHED_AT")
  private Timestamp finishedAt;

  @Column(name = "DUE_DATE")
  private Timestamp dueDate;

  @Column(name = "DEADLINE")
  private Timestamp deadline;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Timestamp getFinishedAt() {
    return finishedAt;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public void setFinishedAt(Timestamp finishedAt) {
    this.finishedAt = finishedAt;
  }

  public Timestamp getDueDate() {
    return dueDate;
  }

  public void setDueDate(Timestamp dueDate) {
    this.dueDate = dueDate;
  }

  public Timestamp getDeadline() {
    return deadline;
  }

  public void setDeadline(Timestamp deadline) {
    this.deadline = deadline;
  }

}
