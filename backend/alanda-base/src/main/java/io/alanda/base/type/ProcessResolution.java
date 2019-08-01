package io.alanda.base.type;

public enum ProcessResolution {
    SUCCESS("sucess"),
    FAILURE("failure"),
    CANCELED("canceled");

  private String resolution;

  private ProcessResolution(String resolution) {
    this.resolution = resolution;
  }

  public String getResolution() {
    return resolution;
  }
}
