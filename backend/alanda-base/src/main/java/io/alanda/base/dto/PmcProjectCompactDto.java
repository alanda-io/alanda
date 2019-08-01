package io.alanda.base.dto;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author developer
 */
public class PmcProjectCompactDto {
  
  Long guid;

  Long version;
  
  String projectId;
  
  String[] tag;
  
  String title;
  
  String status;
  
  Integer priority;
  
  String refObjectIdName;
  
  String refObjectType;
  
  String projectType;
  
  String projectTypeIdName;
  
  String subType;
  
  @JsonFormat(pattern = "yyyy-MM-dd",timezone="Europe/Vienna")
  Date dueDate;

  public PmcProjectCompactDto() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PmcProjectCompactDto that = (PmcProjectCompactDto) o;
    return guid.equals(that.guid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guid);
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String[] getTag() {
    return tag;
  }

  public void setTag(String[] tag) {
    this.tag = tag;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public String getRefObjectIdName() {
    return refObjectIdName;
  }

  public void setRefObjectIdName(String refObjectIdName) {
    this.refObjectIdName = refObjectIdName;
  }

  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getProjectType() {
    return projectType;
  }

  public void setProjectType(String projectType) {
    this.projectType = projectType;
  }

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public String getProjectTypeIdName() {
    return projectTypeIdName;
  }

  public void setProjectTypeIdName(String projectTypeIdName) {
    this.projectTypeIdName = projectTypeIdName;
  }
  
}
