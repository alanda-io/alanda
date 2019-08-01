/**
 * 
 */
package io.alanda.base.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author jlo
 */
public class SimplePhaseDto {

  String idName;

  Boolean enabled;

  Boolean active;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date startDate;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date endDate;

  Boolean frozen;

  public SimplePhaseDto() {
  }

  public SimplePhaseDto(String idName, Boolean enabled, Boolean active, Date startDate, Date endDate, Boolean frozen) {
    super();
    this.idName = idName;
    this.enabled = enabled;
    this.active = active;
    this.startDate = startDate;
    this.endDate = endDate;
    this.frozen = frozen;
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
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

  public Boolean getFrozen() {
    return frozen;
  }

  public void setFrozen(Boolean frozen) {
    this.frozen = frozen;
  }
}
