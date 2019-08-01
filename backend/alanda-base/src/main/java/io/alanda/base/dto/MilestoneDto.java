package io.alanda.base.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;



public class MilestoneDto {
  
  private Long guid;
  
  private String idName;
  
  private String description;
  
  @JsonFormat(pattern = "yyyy-MM-dd",timezone="Europe/Vienna")
  private Date created;

  public MilestoneDto() {
    super();
  }

  
  public Long getGuid() {
    return guid;
  }

  
  public void setGuid(Long guid) {
    this.guid = guid;
  }

  
  public String getIdName() {
    return idName;
  }

  
  public void setIdName(String idName) {
    this.idName = idName;
  }

  
  public String getDescription() {
    return description;
  }

  
  public void setDescription(String description) {
    this.description = description;
  }

  
  public Date getCreated() {
    return created;
  }

  
  public void setCreated(Date created) {
    this.created = created;
  }
  
}
