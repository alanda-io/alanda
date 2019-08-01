package io.alanda.base.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PmcProjectPhaseDto {

  public PmcProjectPhaseDto() {

  }

  public PmcProjectPhaseDto(Boolean enabled, Boolean active) {
    super();
    this.enabled = enabled;
    this.active = active;
  }

  Long guid;

  PmcProjectPhaseDefinitionDto pmcProjectPhaseDefinition;

  Boolean enabled;

  Boolean active;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date startDate;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date endDate;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date createDate;

  Long createUser;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date updateDate;

  Long updateUser;

  boolean frozen;

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public PmcProjectPhaseDefinitionDto getPmcProjectPhaseDefinition() {
    return pmcProjectPhaseDefinition;
  }

  public void setPmcProjectPhaseDefinition(PmcProjectPhaseDefinitionDto pmcProjectPhaseDefinition) {
    this.pmcProjectPhaseDefinition = pmcProjectPhaseDefinition;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
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

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

}
