/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.awt.*;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class PriorityColorCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "PrioColor";
  }

  @Override
  public void init() {
  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    Workbook workbook = report.getWorkbook();
    Map<String, XSSFCellStyle> styleCache = report.getContext().getStyleCache();
    Integer prio = (Integer) report.getContext().getContext().remove(getName() + ".prio");
    if (prio == null) {
      return null;
    }
    XSSFCellStyle style = styleCache.get(getName() + ".style." + prio);
    if (style != null)
      return style;
    style = (XSSFCellStyle) workbook.createCellStyle();
    XSSFColor borderColor = new XSSFColor(Color.lightGray);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setTopBorderColor(borderColor);
    style.setBottomBorderColor(borderColor);
    style.setLeftBorderColor(borderColor);
    style.setRightBorderColor(borderColor);
    if (prio == 0) {
      style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
      style.setFillForegroundColor(IndexedColors.RED.getIndex());
      styleCache.put(getName() + ".style." + prio, style);
    }
    if (prio == 1) {
      style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
      style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      styleCache.put(getName() + ".style." + prio, style);
    }
    if (prio == 2) {
      style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
      style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
      styleCache.put(getName() + ".style." + prio, style);
    }
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof Integer) {
      report.getContext().getContext().put(getName() + ".prio", data);
    }
    return data;
  }

}
