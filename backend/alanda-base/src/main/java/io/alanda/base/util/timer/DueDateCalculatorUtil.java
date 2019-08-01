package io.alanda.base.util.timer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DueDateCalculatorUtil {

  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  private static final Pattern DUE_DATE_CALCULATOR_ID_PATTERN_FIXED_WEEKDAY = Pattern
    .compile("(pmcFwdMs\\w+)-([0-9a-zA-Z_]+)(_(ACT|FC))-(\\d+)-(\\d+)-(\\d+)-(\\d+)$");

  private static final Pattern DUE_DATE_CALCULATOR_ID_PATTERN = Pattern.compile(
      "(pmcMs\\w+)-([0-9a-zA-Z_]+)(?:_(ACT|FC))(?:_R([A-Z]+))?(?:[-](.+))?$");

  private static final PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

  private final static Logger logger = LoggerFactory.getLogger(DueDateCalculatorUtil.class);

  /**
   * @param hour
   * @param minute
   * @param second
   * @return the next date (today or tomorrow) for the passed time in format 'yyyy-MM-dd'T'HH:mm:ss'
   */
  public static String getNextDateForTime(int hour, int minute, int second) {
    return getNextDateForTime(Calendar.getInstance(), hour, minute, second);
  }

  protected static String getNextDateForTime(Calendar now, int hour, int minute, int second) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    if (now.get(Calendar.HOUR_OF_DAY) > hour || (now.get(Calendar.HOUR_OF_DAY) == hour && now.get(Calendar.MINUTE) > minute - 1)) {
      //check if its past the passed time (one minute buffer)
      //set day to tomorrow
      int currentDay = now.get(Calendar.DAY_OF_MONTH);
      now.set(Calendar.DAY_OF_MONTH, currentDay + 1);
    }
    now.set(Calendar.HOUR_OF_DAY, hour);
    now.set(Calendar.MINUTE, minute);
    now.set(Calendar.SECOND, second);
    now.set(Calendar.MILLISECOND, 0);

    return df.format(now.getTime());
  }

  /**
   * @param dateString needs to have the following format:
   *        <p>
   *        yyyy-MM-dd'T'HH:mm:ss
   * @return
   * @throws ParseException
   */
  public static Date stringToDate(String dateString) throws ParseException {
    return dateFormat.parse(dateString);
  }

  /**
   * @param date
   * @return the date as String in the following format:
   *         <p>
   *         yyyy-MM-dd'T'HH:mm:ss
   */
  public static String dateToString(Date date) {
    return dateFormat.format(date);
  }

  public static DueDateCalculatorData parseMilestoneTimerId(String timerId) {
    if (StringUtils.isBlank(timerId))
      return null;

    Matcher m = DueDateCalculatorUtil.DUE_DATE_CALCULATOR_ID_PATTERN.matcher(timerId);
    if (m.matches()) {
      String id = m.group(1);
      String repName = m.group(2);
      String field = m.group(3);
      String relationMode = m.group(4);
      String offsetPattern = m.group(5);

      if (offsetPattern == null)
        return new DueDateCalculatorData(id, repName, null, field, relationMode);
      try {
        long milliseconds = Long.parseLong(offsetPattern);
        return new DueDateCalculatorData(id, repName, new Period( -milliseconds), field, relationMode);
      } catch (NumberFormatException ex) {
      }
      try {
        return new DueDateCalculatorData(id, repName, periodFormatter.parsePeriod(offsetPattern), field, relationMode);
      } catch (Exception ex) {
        logger.warn("Timer " + timerId + ": invalid date/time Offset: " + offsetPattern, ex);
        return null;
      }
    }

    m = DueDateCalculatorUtil.DUE_DATE_CALCULATOR_ID_PATTERN_FIXED_WEEKDAY.matcher(timerId);
    if (m.matches()) {
      String id = m.group(1);
      String repName = m.group(2);
      String field = m.group(4);
      String weeksBack = m.group(5);
      String weekday = m.group(6);
      String hour = m.group(7);
      String minute = m.group(8);

      try {
        return new DueDateCalculatorData(
          id,
          repName,
          Integer.parseInt(weeksBack),
          Integer.parseInt(weekday),
          Integer.parseInt(hour),
          Integer.parseInt(minute),
          field);
      } catch (NumberFormatException ex) {
        logger.warn("could not parse timer " + timerId);
        throw new IllegalArgumentException("could not parse timer " + timerId);
      }
    }
    return null;
  }

  /**
   * @param timeOne in milliseconds
   * @param timeTwo in milliseconds
   * @return if timeOne and timeTwo are at the longest one minute apart
   */
  public static boolean areTimesInOneMinuteWindow(long timeOne, long timeTwo) {
    long oneMinuteInMillis = 60000L;
    return timeOne - oneMinuteInMillis <= timeTwo && timeOne + oneMinuteInMillis >= timeTwo;
  }

  public static Date addWeekdayHoursToDate(Date date, Integer hours) {
    DateTime dt = new DateTime(date);
    while (hours > 0) {
      if (isWeekday(dt)) {
        hours-- ;
      }
      dt = dt.plusHours(1);
    }
    return dt.toDate();
  }

  public static Date subtractWeekdayHoursFromDate(Date date, Integer hours) {
    DateTime dt = new DateTime(date);
    while (hours > 0) {
      if (isWeekday(dt)) {
        hours-- ;
      }
      dt = dt.minusHours(1);
    }
    return dt.toDate();
  }

  private static boolean isWeekday(DateTime dt) {
    return dt.getDayOfWeek() > 0 && dt.getDayOfWeek() < 6;
  }

  public static void main(String[] args) {
    Matcher m = DUE_DATE_CALCULATOR_ID_PATTERN.matcher("pmcMsTest-Lern_fg123-P-5D");
    if (m.matches()) {
      System.out.println(m.group(1) + " - " + m.group(2) + " - " + m.group(3));
    } else {
      System.out.println("NOMATCH");
    }
  }

}
