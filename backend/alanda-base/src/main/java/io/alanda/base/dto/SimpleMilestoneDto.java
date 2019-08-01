/**
 * 
 */
package io.alanda.base.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author jlo
 */
public class SimpleMilestoneDto {

  public SimpleMilestoneDto(String idName, Date fc, Date baseline, Date act) {
    super();
    this.idName = idName;
    this.fc = fc;
    this.baseline = baseline;
    this.act = act;
  }

  private String idName;

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
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

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date fc;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date baseline;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date act;

}
