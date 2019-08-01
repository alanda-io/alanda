/**
 * 
 */
package io.alanda.rest.api;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author jlo
 */
public class RestCommentVo {

  private Long guid;

  private String subject;

  private String text;

  private String user;

  private List<RestCommentVo> replies;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Vienna")
  private Date commentDate;

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

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Date getCommentDate() {
    return commentDate;
  }

  public void setCommentDate(Date commentDate) {
    this.commentDate = commentDate;
  }

  public List<RestCommentVo> getReplies() {
    return replies;
  }

  public void setReplies(List<RestCommentVo> replies) {
    this.replies = replies;
  }

}
