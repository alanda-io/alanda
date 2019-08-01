/**
 * 
 */
package io.alanda.rest.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author jlo
 */
public class RestMilestoneVo {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  public Date date;

  public String reason;

  /**
   * 
   */
  public RestMilestoneVo() {
    super();
  }

  /**
   * @param date
   * @param reason
   */
  public RestMilestoneVo(Date date, String reason) {
    super();
    this.date = date;
    this.reason = reason;
  }

}
