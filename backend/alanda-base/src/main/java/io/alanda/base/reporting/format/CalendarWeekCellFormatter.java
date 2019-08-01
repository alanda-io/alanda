/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class CalendarWeekCellFormatter implements ReportCellFormatter {

  public final String MS_TARGET_DATE_FORMAT = "yyyy-MM-dd";

  @Override
  public String getName() {
    return "CalWeek";
  }

  @Override
  public void init() {
  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    return null;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-w");
    if (data instanceof Long) {
      DateTime dt = new DateTime(data);
      return fmt.print(dt);
    }
    if (data instanceof String) {
      SimpleDateFormat sdf = new SimpleDateFormat(MS_TARGET_DATE_FORMAT);
      try {
        Date ret = sdf.parse((String) data);
        DateTime dt = new DateTime(ret.getTime());
        return fmt.print(dt);
      } catch (ParseException ex) {
        return data;
      }
    }
    return data;
  }
}
