package io.alanda.base.dto;

import java.util.Date;

import io.alanda.base.entity.PmcHistoryLog;
import io.alanda.base.entity.PmcHistoryLog.Action;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectMilestone;
import io.alanda.base.entity.PmcProjectPhase;
import io.alanda.base.entity.PmcProjectProcess;
import io.alanda.base.util.UserContext;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PmcHistoryLogDto {

  private Long guid;

  private Long refObjectId;

  private String refObjectType;

  private String refObjectIdName;

  private String type;

  private PmcHistoryLog.Action action;

  private String fieldName;

  private String fieldRef;

  private String fieldId;

  private String oldValue;

  private String newValue;

  private String text;

  private Long userGuid;

  private String user;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Vienna")
  private Date modDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Vienna")
  private Date logDate;

  private Long pmcProjectGuid;

  private String projectId;

  public PmcHistoryLogDto withChange(Action action, String text, Object oldValue, Object newValue) {
    this.withChange(action, text);
    this.setOldValue(oldValue != null ? oldValue.toString() : null);
    this.setNewValue(newValue != null ? newValue.toString() : null);
    return this;
  }

  public PmcHistoryLogDto withChange(Action action, String text) {
    this.setAction(action);
    this.setText(text);
    return this;
  }

  public PmcHistoryLogDto withType(String type) {
    if (type != null)
      this.type = type;
    return this;
  }

  public PmcHistoryLogDto withLogDate(Date logDate) {
    if (logDate != null)
      this.logDate = logDate;
    return this;
  }

  public PmcHistoryLogDto withField(String name, String ref, String id) {
    if (name != null)
      this.fieldName = name;
    if (ref != null)
      this.fieldRef = ref;
    if (id != null)
      this.fieldId = id;
    return this;
  }

  public static PmcHistoryLogDto createForExternalState(PmcProject p, String externalId) {
    PmcHistoryLogDto dto = createForProject(p);
    dto.setType("externalState");
    dto.setFieldName("Status");
    dto.setFieldId(externalId);
    dto.setFieldRef("externalState");
    return dto;
  }

  public static PmcHistoryLogDto createForProcess(PmcProjectProcess pp) {
    PmcHistoryLogDto dto = createForProject(pp.getPmcProject());
    dto.setType("Process");
    dto.setFieldId(pp.getProcessInstanceId());
    dto.setFieldRef(pp.getProcessKey());
    dto.setFieldName(pp.getLabel());

    if (pp.getBusinessObject() != null) {
      dto.setRefObjectIdName(pp.getBusinessObject());
    }

    return dto;
  }

  public static PmcHistoryLogDto createForMilestone(PmcProjectMilestone ms, String suffix) {
    PmcHistoryLogDto dto = createForProject(ms.getProject());
    dto.setType("Milestone");
    dto.setFieldId(ms.getGuid().toString());
    dto.setFieldRef("project.milestone." + ms.getMilestone().getIdName() + "." + suffix);
    dto.setFieldName(ms.getMilestone().getDescription());
    return dto;
  }

  public static PmcHistoryLogDto createForPhase(PmcProjectPhase ph, String suffix) {
    PmcHistoryLogDto dto = createForProject(ph.getPmcProject());
    dto.setType("Phase");
    dto.setFieldId(ph.getGuid().toString());
    dto.setFieldName(ph.getPmcProjectPhaseDefinition().getDisplayName());
    dto.setFieldRef("project.phase." + ph.getPmcProjectPhaseDefinition().getIdName() + "." + suffix);
    return dto;
  }

  public static PmcHistoryLogDto createForProject(PmcProjectDto p) {
    PmcHistoryLogDto dto = createEmpty();
    dto.setPmcProjectGuid(p.getGuid());
    dto.setFieldId(p.getGuid().toString());
    dto.setFieldRef(p.getPmcProjectType().getIdName());
    dto.setFieldName(p.getTitle());
    dto.setRefObjectId(p.getRefObjectId());
    dto.setRefObjectIdName(p.getRefObjectIdName());
    dto.setRefObjectType(p.getRefObjectType());
    dto.setProjectId(p.getProjectId());
    dto.setType("Project");
    return dto;
  }

  public static PmcHistoryLogDto createForProject(PmcProject p) {
    PmcHistoryLogDto dto = createEmpty();
    dto.setPmcProjectGuid(p.getGuid());
    dto.setFieldId(p.getGuid().toString());
    dto.setFieldRef(p.getPmcProjectType().getIdName());
    dto.setFieldName(p.getTitle());
    dto.setRefObjectId(p.getRefObjectId());
    dto.setRefObjectIdName(p.getRefObjectIdName());
    dto.setRefObjectType(p.getRefObjectType());
    dto.setProjectId(p.getProjectId());
    dto.setType("Project");
    return dto;
  }

  public static PmcHistoryLogDto createEmpty() {
    PmcHistoryLogDto dto = new PmcHistoryLogDto();

    PmcUserDto pmcUserDto = UserContext.getUser();
    Long userGuid = null;
    String user = null;
    if (pmcUserDto != null) {
      userGuid = pmcUserDto.getGuid();
      user = pmcUserDto.getFirstName() + " " + pmcUserDto.getSurname();
    } else {
      userGuid = (long) 0;
    }
    Date modDate = new Date();
    dto.setModDate(modDate);
    dto.setLogDate(modDate);
    dto.setUser(user);
    dto.setUserGuid(userGuid);
    return dto;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
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

  public PmcHistoryLog.Action getAction() {
    return action;
  }

  public void setAction(PmcHistoryLog.Action action) {
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

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
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

  @Override
  public String toString() {
    return "PmcHistoryLogDto{" +
      "guid=" +
      guid +
      ", refObjectId=" +
      refObjectId +
      ", refObjectType='" +
      refObjectType +
      '\'' +
      ", refObjectIdName='" +
      refObjectIdName +
      '\'' +
      ", type='" +
      type +
      '\'' +
      ", action=" +
      action +
      ", fieldName='" +
      fieldName +
      '\'' +
      ", fieldRef='" +
      fieldRef +
      '\'' +
      ", fieldId='" +
      fieldId +
      '\'' +
      ", oldValue='" +
      oldValue +
      '\'' +
      ", newValue='" +
      newValue +
      '\'' +
      ", text='" +
      text +
      '\'' +
      ", userGuid=" +
      userGuid +
      ", user='" +
      user +
      '\'' +
      ", modDate=" +
      modDate +
      ", logDate=" +
      logDate +
      ", pmcProjectGuid=" +
      pmcProjectGuid +
      ", projectId='" +
      projectId +
      '\'' +
      '}';
  }

}
