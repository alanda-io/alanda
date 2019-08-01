package io.alanda.base.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jlo
 */

public class BatchProjectDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private int rowNumber;

  private String title;

  private String subType;

  private String tag;

  private int prio;

  private Date dueDate;

  private String refObjectIdName;

  private Long refObjectId;

  private String comment;

  private String resultMessage;

  private Long pmcProjectGuid;

  private String pmcProjectId;

  private List<BatchProjectRoleDto> roles = new ArrayList<>();

  private List<BatchProjectPropertyDto> properties = new ArrayList<>();

  private List<BatchProjectProcessDto> processes = new ArrayList<>();

  public int getRowNumber() {
    return rowNumber;
  }

  public void setRowNumber(int rowNumber) {
    this.rowNumber = rowNumber;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public int getPrio() {
    return prio;
  }

  public void setPrio(int prio) {
    this.prio = prio;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public String getRefObjectIdName() {
    return refObjectIdName;
  }

  public void setRefObjectIdName(String refObjectIdName) {
    this.refObjectIdName = refObjectIdName;
  }

  public String getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }

  public String getPmcProjectId() {
    return pmcProjectId;
  }

  public void setPmcProjectId(String pmcProjectId) {
    this.pmcProjectId = pmcProjectId;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public List<BatchProjectRoleDto> getRoles() {
    return roles;
  }

  public void setRoles(List<BatchProjectRoleDto> roles) {
    this.roles = roles;
  }

  public List<BatchProjectPropertyDto> getProperties() {
    return properties;
  }

  public void setProperties(List<BatchProjectPropertyDto> properties) {
    this.properties = properties;
  }

  public List<BatchProjectProcessDto> getProcesses() {
    return processes;
  }

  public void setProcesses(List<BatchProjectProcessDto> processes) {
    this.processes = processes;
  }

  @Override
  public String toString() {
    return "BatchProjectDto [rowNumber=" +
      rowNumber +
      ", " +
      (title != null ? "title=" + title + ", " : "") +
      (subType != null ? "subType=" + subType + ", " : "") +
      (tag != null ? "tag=" + tag + ", " : "") +
      "prio=" +
      prio +
      ", " +
      (dueDate != null ? "dueDate=" + dueDate + ", " : "") +
      (refObjectIdName != null ? "refObjectIdName=" + refObjectIdName + ", " : "") +
      (refObjectId != null ? "refObjectId=" + refObjectId + ", " : "") +
      (comment != null ? "comment=" + comment + ", " : "") +
      (resultMessage != null ? "resultMessage=" + resultMessage + ", " : "") +
      (pmcProjectGuid != null ? "pmcProjectGuid=" + pmcProjectGuid + ", " : "") +
      (pmcProjectId != null ? "pmcProjectId=" + pmcProjectId + ", " : "") +
      (roles != null ? "roles=" + roles + ", " : "") +
      (properties != null ? "properties=" + properties + ", " : "") +
      (processes != null ? "processes=" + processes : "") +
      "]";
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

}
