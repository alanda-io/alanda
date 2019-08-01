package io.alanda.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.EnumUtils;

import com.fasterxml.jackson.annotation.JsonCreator;

@Entity
@Table(name = "PMC_HISTORY_LOG")
public class PmcHistoryLog {

  public enum Action {
      CREATE,
      DELETE,
      EDIT,
      CANCEL,
      START,
      LOG;

    @JsonCreator
    public static Action fromString(String string) {
      string = string.toUpperCase();
      return EnumUtils.getEnum(Action.class, string);
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_sequence")
  @SequenceGenerator(name = "entity_sequence", sequenceName = "SEQ_GUID_PK", allocationSize = 1)
  private Long guid;

  @Column(name = "REF_OBJECTID")
  private Long refObjectId;

  @Column(name = "REF_OBJECTTYPE")
  private String refObjectType;

  @Column(name = "REF_OBJECTIDNAME")
  private String refObjectIdName;

  @Column(name = "TYPE")
  private String type;

  @Column(name = "ACTION")
  @Enumerated(EnumType.STRING)
  private Action action;

  @Column(name = "FIELD_NAME")
  private String fieldName;

  @Column(name = "FIELD_REF")
  private String fieldRef;

  @Column(name = "FIELD_ID")
  private String fieldId;

  @Column(name = "OLDVALUE")
  private String oldValue;

  @Column(name = "NEWVALUE")
  private String newValue;

  @Column(name = "TEXT")
  private String text;

  @Column(name = "USERID")
  private Long userGuid;

  @Column(name = "MODDATE")
  private Date modDate;

  @Column(name = "LOGDATE")
  private Date logDate;

  @Column(name = "PMC_PROJECTGUID")
  private Long pmcProjectGuid;

  @Column(name = "PROJECTID")
  private String projectId;

  @Column(name = "USERNAME")
  private String user;

  public PmcHistoryLog() {
    super();
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  public String getRefObjectIdName() {
    return refObjectIdName;
  }

  public void setRefObjectIdName(String refObjectIdName) {
    this.refObjectIdName = refObjectIdName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldRef() {
    return fieldRef;
  }

  public void setFieldRef(String fieldRef) {
    this.fieldRef = fieldRef;
  }

  public String getFieldId() {
    return fieldId;
  }

  public void setFieldId(String fieldId) {
    this.fieldId = fieldId;
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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getUserGuid() {
    return userGuid;
  }

  public void setUserGuid(Long userGuid) {
    this.userGuid = userGuid;
  }

  public Date getModDate() {
    return modDate;
  }

  public void setModDate(Date modDate) {
    this.modDate = modDate;
  }

  public Date getLogDate() {
    return logDate;
  }

  public void setLogDate(Date logDate) {
    this.logDate = logDate;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }
}
