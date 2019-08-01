package io.alanda.base.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "PMC_METRIC_PROPERTY")
public class PmcMetricProperty extends AbstractEntity implements Serializable {

  public enum ValueType {
    STRING,
    INTEGER,
    LONG,
    BOOLEAN,
    LOCAL_DATE_TIME
  }

  @Transient
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "REF_ENTITY")
  private PmcMetricEntity metricEntity;

  @Column(name = "KEY")
  private String key;

  @Column(name = "VALUE")
  private String value;

  @Column(name = "VALUE_TYPE")
  @Enumerated(EnumType.STRING)
  private ValueType valueType;

  public static PmcMetricProperty createWithValue(String key, Object value) {
    PmcMetricProperty p = new PmcMetricProperty();
    p.setKey(key);
    if (value instanceof String)
      p.setValue((String) value);
    if (value instanceof Long)
      p.setValue((Long) value);
    if (value instanceof Integer)
      p.setValue((Integer) value);
    if (value instanceof Boolean)
      p.setValue((Boolean) value);
    if (value instanceof LocalDateTime)
      p.setValue((LocalDateTime) value);
    if (value instanceof Date)
      p.setValue(LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
    return p;
  }

  public PmcMetricProperty() {
    super();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getStringValue() {
    if (!Objects.equals(valueType, ValueType.STRING))
      throw new IllegalStateException("metric property (guid=" + this.getGuid() + ") is not of type STRING");
    return value;
  }

  public Long getLongValue() {
    if (!Objects.equals(valueType, ValueType.LONG))
      throw new IllegalStateException("metric property (guid=" + this.getGuid() + ") is not of type LONG");
    return value != null ? Long.parseLong(value) : null;
  }

  public Integer getIntegerValue() {
    if (!Objects.equals(valueType, ValueType.INTEGER))
      throw new IllegalStateException("metric property (guid=" + this.getGuid() + ") is not of type INTEGER");
    return value != null ? Integer.parseInt(value) : null;
  }

  public Boolean getBooleanValue() {
    if (!Objects.equals(valueType, ValueType.BOOLEAN))
      throw new IllegalStateException("metric property (guid=" + this.getGuid() + ") is not of type BOOLEAN");
    return value != null ? Boolean.parseBoolean(value) : null;
  }

  public LocalDateTime getDateValue() {
    if (!Objects.equals(valueType, ValueType.LOCAL_DATE_TIME))
      throw new IllegalStateException("metric property (guid=" + this.getGuid() + ") is not of type DATE");
    return value != null ? LocalDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
  }

  public void setValue(String value) {
    this.valueType = ValueType.STRING;
    this.value = value;
  }

  public void setValue(Long value) {
    this.valueType = ValueType.LONG;
    this.value = value != null ? value.toString() : null;
  }

  public void setValue(Integer value) {
    this.valueType = ValueType.INTEGER;
    this.value = value != null ? value.toString() : null;
  }

  public void setValue(Boolean value) {
    this.valueType = ValueType.BOOLEAN;
    this.value = value != null ? value.toString() : null;
  }

  public void setValue(LocalDateTime value) {
    this.valueType = ValueType.LOCAL_DATE_TIME;
    this.value = value != null ? value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
  }

  public PmcMetricEntity getMetricEntity() {
    return metricEntity;
  }

  public void setMetricEntity(PmcMetricEntity metricEntity) {
    this.metricEntity = metricEntity;
  }

  @Override
  public String toString() {
    return "PmcMetricProperty{" + "key='" + key + '\'' + ", value='" + value + '\'' + ", valueType=" + valueType + '}';
  }
}
