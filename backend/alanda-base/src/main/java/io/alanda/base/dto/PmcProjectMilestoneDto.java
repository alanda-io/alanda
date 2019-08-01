package io.alanda.base.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PmcProjectMilestoneDto {

  private Long guid;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date fc;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date baseline;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date act;

  PmcProjectCompactDto project;

  MilestoneDto milestone;

  @JsonIgnore
  String reason;

  public PmcProjectMilestoneDto() {
    super();
  }

  public Date getFc() {
    return fc;
  }

  public void setFc(Date fc) {
    this.fc = fc;
  }

  public Date getBaseline() {
    return baseline;
  }

  public void setBaseline(Date baseline) {
    this.baseline = baseline;
  }

  public Date getAct() {
    return act;
  }

  public void setAct(Date act) {
    this.act = act;
  }

  public PmcProjectCompactDto getProject() {
    return project;
  }

  public void setProject(PmcProjectCompactDto project) {
    this.project = project;
  }

  public MilestoneDto getMilestone() {
    return milestone;
  }

  public void setMilestone(MilestoneDto milestone) {
    this.milestone = milestone;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "PmcProjectMilestoneDto [fc=" +
      fc +
      ", baseline=" +
      baseline +
      ", act=" +
      act +
      ", project=" +
      project +
      ", milestone=" +
      milestone +
      "]";
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

}
