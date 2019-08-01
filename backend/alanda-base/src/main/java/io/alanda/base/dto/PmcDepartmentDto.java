package io.alanda.base.dto;

public class PmcDepartmentDto {

  private Long guid;

  private Long version;

  private String idName;

  private String name;

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "PmcDepartmentDto{" + "guid=" + guid + ", version=" + version + ", idName='" + idName + '\'' + ", name='" + name + '\'' + '}';
  }
}
