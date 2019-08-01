/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.util.Date;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class DateSekReportCellFormatter implements ReportCellFormatter {

  private final String MS_TARGET_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Override
  public String getName() {
    return "DateS";
  }

  @Override
  public void init() {

  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    Workbook workbook = report.getWorkbook();
    Map<String, XSSFCellStyle> styleCache = report.getContext().getStyleCache();
    XSSFCellStyle style = styleCache.get(getName() + ".style");
    if (style == null) {
      style = (XSSFCellStyle) workbook.createCellStyle();
      style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(MS_TARGET_DATE_FORMAT));
      styleCache.put(getName() + ".style", style);
    }
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof Long) {
      Date d = new Date((Long) data);
      return d;
    } else
      return data;
  }

}
