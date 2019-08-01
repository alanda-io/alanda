package io.alanda.base.dto;

public class RefObjectDto implements RefObject {

  private String idName;

  private Long refObjectId;

  private String refObjectType;

  public RefObjectDto() {
    super();
  }

  public RefObjectDto(String idName, Long refObjectId, String refObjectType) {
    super();
    this.idName = idName;
    this.refObjectId = refObjectId;
    this.refObjectType = refObjectType;
  }

  @Override
  public String getObjectName() {
    return idName;
  }

  @Override
  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  @Override
  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  @Override
  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

}
