/**
 * 
 */
package io.alanda.base.util.timer;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;

/**
 * Data for DueDateCalculator, extracted out of the timer's id
 * 
 * @author Julian Löffelhardt
 */
public class DueDateCalculatorData implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 6926529142793714876L;

  private enum DueDateCalculatorMode {
      DEFAULT,
      FIXED_WEEKDAY
  }

  private DueDateCalculatorMode mode;

  private String id;

  private String msName;

  private Period offset;

  private Integer weeksBack;

  private Integer fixedWeekday;

  private Integer fixedHour;

  private String field;

  private boolean childMs;

  private boolean parentMs;

  public DueDateCalculatorMode getMode() {
    return mode;
  }

  private Integer fixedMinute;

  public DueDateCalculatorData(String id, String msName, Period offset, String field, String relationMode) {
    this.mode = DueDateCalculatorMode.DEFAULT;
    this.id = id;
    this.msName = msName;
    this.offset = offset;
    this.field = field;
    if (relationMode != null) {
      if ("P".equals(relationMode)) {
        this.parentMs = true;
      } else if ("C".equals(relationMode)) {
        this.childMs = true;
      }
    }
  }

  public DueDateCalculatorData(
      String id, String msName, Integer weeksBack, Integer weekday, Integer hour, Integer minute, String field) {
    this.mode = DueDateCalculatorMode.FIXED_WEEKDAY;
    this.id = id;
    this.msName = msName;
    this.weeksBack = weeksBack;
    this.fixedWeekday = weekday;
    this.fixedHour = hour;
    this.fixedMinute = minute;
    this.field = field;
  }

  @Override
  public String toString() {
    return "DueDateCalculatorData [id=" + id + ", msName=" + msName + ", offset=" + offset + "]";
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMsName() {
    return msName;
  }

  public void setMsName(String msName) {
    this.msName = msName;
  }

  public Period getOffset() {
    return offset;
  }

  public void setOffset(Period offset) {
    this.offset = offset;
  }

  public void setMode(DueDateCalculatorMode mode) {
    this.mode = mode;
  }

  public Integer getWeeksBack() {
    return weeksBack;
  }

  public void setWeeksBack(Integer weeksBack) {
    this.weeksBack = weeksBack;
  }

  public Integer getFixedWeekday() {
    return fixedWeekday;
  }

  public void setFixedWeekday(Integer fixedWeekday) {
    this.fixedWeekday = fixedWeekday;
  }

  public Integer getFixedHour() {
    return fixedHour;
  }

  public void setFixedHour(Integer fixedHour) {
    this.fixedHour = fixedHour;
  }

  public Integer getFixedMinute() {
    return fixedMinute;
  }

  public void setFixedMinute(Integer fixedMinute) {
    this.fixedMinute = fixedMinute;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public boolean isChildMs() {
    return childMs;
  }

  public void setChildMs(boolean childMs) {
    this.childMs = childMs;
  }

  public boolean isParentMs() {
    return parentMs;
  }

  public void setParentMs(boolean parentMs) {
    this.parentMs = parentMs;
  }

  public Date calcDueDate(Date repDate) {

    if (mode == null || mode.equals(DueDateCalculatorMode.DEFAULT)) {
      if (offset == null)
        return new Date(repDate.getTime());
      DateTime dt = new DateTime(repDate).plus(offset);
      return dt.toDate();
    }

    if (mode.equals(DueDateCalculatorMode.FIXED_WEEKDAY)) {
      MutableDateTime dueDate = new MutableDateTime(repDate);
      dueDate.addWeeks( -this.weeksBack);
      dueDate.setDayOfWeek(this.fixedWeekday);
      dueDate.setTime(this.fixedHour, this.fixedMinute, 0, 0);
      return dueDate.toDate();
    }
    return null;
  }

}
