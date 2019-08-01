package io.alanda.base.dto;

import java.io.Serializable;
import java.util.Date;

import io.alanda.base.util.timer.DueDateCalculatorData;

/**
 * @author jlo
 */
public class WaitingExecutionDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String msName;

  private Date msDate;

  private final DueDateCalculatorData dueDateCalculatorData;

  private String executionId;

  private Long pmcProjectGuid;

  private String messageName;

  public WaitingExecutionDto(DueDateCalculatorData data, String executionId, Long pmcProjectGuid, Date msDate) {
    this.dueDateCalculatorData = data;
    this.msName = data.getMsName();
    this.executionId = executionId;

    this.pmcProjectGuid = pmcProjectGuid;
    this.msDate = msDate;

  }

  public WaitingExecutionDto(String msName, String executionId, Long pmcProjectGuid, Date msDate) {
    this.dueDateCalculatorData = null;
    this.msName = msName;
    this.executionId = executionId;
    this.pmcProjectGuid = pmcProjectGuid;

    this.msDate = msDate;
  }

  public WaitingExecutionDto withMessageName(String messageName) {
    this.messageName = messageName;
    return this;
  }

  public String getMsName() {
    return msName;
  }

  public DueDateCalculatorData getDueDateCalculatorData() {
    return dueDateCalculatorData;
  }

  public String getExecutionId() {
    return executionId;
  }

  public Long getPmcProjectGuid() {
    return pmcProjectGuid;
  }

  public String getMessageName() {
    return messageName != null ? messageName : "pmcMsMessage-" + msName + "_" + dueDateCalculatorData.getField();
  }

  @Override
  public String toString() {
    return "WaitingExecutionDto [msName=" +
      msName +
      ", dueDateCalculatorData=" +
      dueDateCalculatorData +
      ", executionId=" +
      executionId +
      ", pmcProjectGuid=" +
      pmcProjectGuid +
      "]";
  }

  public Date getMsDate() {
    return msDate;
  }

  public void setMsDate(Date msDate) {
    this.msDate = msDate;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    this.pmcProjectGuid = pmcProjectGuid;
  }

}
