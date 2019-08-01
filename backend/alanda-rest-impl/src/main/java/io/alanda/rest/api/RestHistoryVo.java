/**
 * 
 */
package io.alanda.rest.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author jlo
 */
public class RestHistoryVo {

  private Long guid;

  private String type;

  private String text;

  private String field;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  private Date modDate;

  private String modUser;

  private String oldValue;

  private String newValue;

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getModUser() {
    return modUser;
  }

  public void setModUser(String modUser) {
    this.modUser = modUser;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Date getModDate() {
    return modDate;
  }

  public void setModDate(Date modDate) {
    this.modDate = modDate;
  }

}
