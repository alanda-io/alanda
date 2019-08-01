package io.alanda.base.batch;

import java.io.Serializable;

public class BatchProjectRoleDto implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 2921219881153996794L;

  public BatchProjectRoleDto() {
    super();
  }

  public BatchProjectRoleDto(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }

  private String name;

  private String value;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "role [" + (name != null ? "name=" + name + ", " : "") + (value != null ? "value=" + value : "") + "]";
  }
}
