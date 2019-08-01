/**
 * 
 */
package io.alanda.rest.api;

import java.util.Date;

import io.alanda.base.entity.PmcProject;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author jlo
 */
public class RestProjectVo {

  Long guid;

  String projectId;

  String subType;

  String[] tag;

  String title;

  String status;

  Integer priority;

  String refObjectIdName;

  String refObjectType;

  String projectType;

  String projectTypeIdName;

  String comment;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date dueDate;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date createDate;

  String ownerName;

  public RestProjectVo(PmcProject p) {
    this.guid = p.getGuid();
    this.projectId = p.getProjectId();
    this.subType = p.getSubtype();
    this.tag = p.getTag();
    this.title = p.getTitle();
    this.status = p.getStatus().toString();
    this.priority = p.getPriority();
    this.refObjectIdName = p.getRefObjectIdName();
    this.refObjectType = p.getRefObjectType();
    this.projectType = p.getPmcProjectType().getName();
    this.projectTypeIdName = p.getPmcProjectType().getIdName();
    this.comment = p.getComment();
    this.dueDate = p.getDueDate();
    this.createDate = p.getCreateDate();

  }

  public RestProjectVo() {
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

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
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

  public String getProjectType() {
    return projectType;
  }

  public void setProjectType(String projectType) {
    this.projectType = projectType;
  }

  public String getProjectTypeIdName() {
    return projectTypeIdName;
  }

  public void setProjectTypeIdName(String projectTypeIdName) {
    this.projectTypeIdName = projectTypeIdName;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

}
