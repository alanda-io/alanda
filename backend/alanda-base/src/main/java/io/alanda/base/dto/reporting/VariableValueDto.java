package io.alanda.base.dto.reporting;

import java.io.Serializable;

public class VariableValueDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String name;

  private final Serializable value;

  public VariableValueDto(String name, Serializable value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Serializable getValue() {
    return value;
  }

}
