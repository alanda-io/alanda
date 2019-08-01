package io.alanda.base.batch;

import java.io.Serializable;

import io.alanda.base.service.PmcPropertyService.PmcPropertyType;

public class BatchProjectPropertyDto implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3942561024021211173L;

  public BatchProjectPropertyDto() {
    super();
  }

  public BatchProjectPropertyDto(String name, PmcPropertyType type, String value) {
    super();
    this.name = name;
    this.type = type;
    this.value = value;
  }

  private String name;

  private PmcPropertyType type;

  private String value;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PmcPropertyType getType() {
    return type;
  }

  public void setType(PmcPropertyType type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "prop [" +
      (name != null ? "name=" + name + ", " : "") +
      (type != null ? "type=" + type + ", " : "") +
      (value != null ? "value=" + value : "") +
      "]";
  }
}