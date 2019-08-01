package io.alanda.base.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.alanda.base.service.PmcPropertyService;

public class PmcPropertyDto {

  public PmcPropertyDto() {
    super();
  }


  public PmcPropertyDto(String key, String value, String valueType) {
    super();
    this.key = key;
    this.value = value;
    this.valueType = valueType;
  }


  private Long pmcProjectGuid;
  
  private Long entityId;
  
  private String entityType;
  
  private String key;

  private String value;
  
  private String valueType;

  private Date createDate;

  private Date updateDate;

  public static PmcPropertyDto createForPmcProject(Long pmcProjectGuid, String key) {
    PmcPropertyDto prop = new PmcPropertyDto();
    prop.setPmcProjectGuid(pmcProjectGuid);
    prop.setKey(key);
    return prop;
  }

  public PmcPropertyDto withStringValue(String value) {
    this.setValueType(PmcPropertyService.PmcPropertyType.STRING.toString());
    this.setValue(value);
    return this;
  }

  public PmcPropertyDto withBooleanValue(Boolean value) {
    this.setValueType(PmcPropertyService.PmcPropertyType.BOOLEAN.toString());
    this.setValue(value.toString());
    return this;
  }

  public PmcPropertyDto withLongValue(Long value) {
    this.setValueType(PmcPropertyService.PmcPropertyType.LONG.toString());
    this.setValue(value.toString());
    return this;
  }

  public PmcPropertyDto withIntValue(Integer value) {
    this.setValueType(PmcPropertyService.PmcPropertyType.INTEGER.toString());
    this.setValue(value.toString());
    return this;
  }

  public PmcPropertyDto withDateValue(Date value) {
    this.setValueType(PmcPropertyService.PmcPropertyType.DATE.toString());
    SimpleDateFormat sdf = new SimpleDateFormat(PmcPropertyService.dateFormat);
    this.setValue(sdf.format(value));
    return this;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  
  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }
  
  public Long getEntityId() {
    return entityId;
  }

  
  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  
  public String getEntityType() {
    return entityType;
  }

  
  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  
  public String getKey() {
    return key;
  }

  
  public void setKey(String key) {
    this.key = key;
  }

  
  public String getValue() {
    return value;
  }

  
  public void setValue(String value) {
    this.value = value;
  }

  
  public String getValueType() {
    return valueType;
  }

  
  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  @Override
  public String toString() {
    return "PmcPropertyDto [entityId=" +
      entityId +
      ", entityType=" +
      entityType +
      ", key=" +
      key +
      ", value=" +
      value +
      ", valueType=" +
      valueType +
      "]";
  }
  
}
