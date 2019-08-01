package io.alanda.base.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "PMC_METRIC_ENTITY")
public class PmcMetricEntity extends AbstractEntity implements Serializable {

  public enum EntityType {
    PROJECT,
    PHASE,
    PROCESS,
    TASK
  }

  @Transient
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Column(name = "ENTITY_ID")
  String entityId;

  @Column(name = "ENTITY_TYPE")
  @Enumerated(EnumType.STRING)
  EntityType entityType;

  @Column(name = "STARTED")
  LocalDateTime started;

  @Column(name = "ENDED")
  LocalDateTime ended;

  // in ms
  @Column(name = "DURATION")
  Long duration;

  @OneToMany(mappedBy = "metricEntity")
  List<PmcMetricProperty> properties = new ArrayList<>();

  public static PmcMetricEntity createAsProcessEntity(String pid, LocalDateTime started, LocalDateTime ended) {
    PmcMetricEntity e = new PmcMetricEntity();
    e.setEntityType(EntityType.PROCESS);
    e.setEntityId(pid);
    e.setStarted(started);
    e.setEnded(ended);
    e.setDuration(
      ended.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - started.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    return e;
  }

  public PmcMetricEntity() {
    super();
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }

  public LocalDateTime getStarted() {
    return started;
  }

  public void setStarted(LocalDateTime started) {
    this.started = started;
  }

  public LocalDateTime getEnded() {
    return ended;
  }

  public void setEnded(LocalDateTime ended) {
    this.ended = ended;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public List<PmcMetricProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<PmcMetricProperty> properties) {
    this.properties = properties;
  }

  public PmcMetricProperty getProperty(String key) {
    if (properties != null) {
      for (PmcMetricProperty p : properties) {
        if (Objects.equals(key, p.getKey())) {
          return p;
        }
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return "PmcMetricEntity{" +
      "entityId=" +
      entityId +
      ", entityType=" +
      entityType +
      ", started=" +
      started +
      ", ended=" +
      ended +
      ", duration=" +
      duration +
      '}';
  }
}
