/**
 * 
 */
package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

/**
 * @author Julian Löffelhardt
 */
@Entity
@Table(name = "Pmc_Comment")
@NamedNativeQueries({

  @NamedNativeQuery(name = "PmcComment.loadByProcInstId", query = "select c.* from Pmc_Comment c " +
    "where c.COMMENT_KEY=" +
    "(select v.TEXT_ from ACT_HI_VARINST v where v.PROC_INST_ID_=? and v.NAME_='commentKey')  " +
    "order by c.REPLY_TO desc nulls first,c.created desc", resultClass = PmcComment.class),
  @NamedNativeQuery(name = "PmcComment.loadByProcInstIdAndRefObjectId", query = "select c.* from Pmc_Comment c where " +
    "(c.COMMENT_KEY=(select v.TEXT_ from ACT_HI_VARINST v where v.PROC_INST_ID_=? and v.NAME_='commentKey') " +
    "or (c.refObjectId=? AND c.COMMENT_KEY IS NULL)) order by c.REPLY_TO desc nulls first, c.created desc", resultClass = PmcComment.class),}

)
public class PmcComment extends AbstractAuditEntity {

  @Column(name = "SUBJECT")
  private String subject;

  @Column(name = "COMMENTS")
  private String text;

  @Column(name = "TASK_ID")
  private String taskId;

  @Column(name = "PROC_INST_ID")
  private String procInstId;

  @Column(name = "REPLY_TO")
  private Long replyTo;

  @Column(name = "COMMENT_KEY")
  private String commentKey;

  @Column(name = "REF_OBJECT_TYPE")
  private String refObjectType;

  @Column(name = "REF_OBJECT_ID")
  private Long refObjectId;

  @Column(name = "REF_PMCPROJECT")
  private Long pmcProjectGuid;

  @Column(name = "TASK_NAME")
  private String taskName;

  @Column(name = "PROCESS_NAME")
  private String processName;

  public PmcComment() {
    // TODO Auto-generated constructor stub
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

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }

}
