package io.alanda.base.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmcCommentDto {

  private Long guid;

  private String subject;

  private String text;

  private String taskId;

  private String procInstId;

  private Long replyTo;

  private String commentKey;

  private String refObjectType;

  private Long refObjectId;

  private Date createDate;

  private Long createUser;

  private Date updateDate;

  private Long updateUser;
  
  private String authorName;
  
  private Long pmcProjectGuid;

  private String processName;

  private String taskName;

  private List<PmcCommentDto> replies = new ArrayList<PmcCommentDto>();
  
  /**
   *  TODO: processName and taskName are not placed now
   * @return
   */
  

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getProcInstId() {
    return procInstId;
  }

  public void setProcInstId(String procInstId) {
    this.procInstId = procInstId;
  }

  public Long getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(Long replyTo) {
    this.replyTo = replyTo;
  }

  public String getCommentKey() {
    return commentKey;
  }

  public void setCommentKey(String commentKey) {
    this.commentKey = commentKey;
  }

  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Long getCreateUser() {
    return createUser;
  }

  public void setCreateUser(Long createUser) {
    this.createUser = createUser;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public Long getUpdateUser() {
    return updateUser;
  }

  public void setUpdateUser(Long updateUser) {
    this.updateUser = updateUser;
  }

  
  public String getAuthorName() {
    return authorName;
  }

  
  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  
  public List<PmcCommentDto> getReplies() {
    return replies;
  }

  
  public void setReplies(List<PmcCommentDto> replies) {
    this.replies = replies;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

}
