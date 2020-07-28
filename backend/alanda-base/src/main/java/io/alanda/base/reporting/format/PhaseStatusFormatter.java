package io.alanda.base.reporting.format;

import java.awt.*;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

public class PhaseStatusFormatter implements ReportCellFormatter {

  private static final Logger log = LoggerFactory.getLogger(PhaseStatusFormatter.class);

  @Override
  public void init() {
  }

  @Override
  public String getName() {
    return "PhaseStatus";
  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    Workbook workbook = report.getWorkbook();
    Map<String, XSSFCellStyle> styleCache = report.getContext().getStyleCache();
    String status = (String) report.getContext().getContext().remove(getName() + ".status");
    if (status == null) {
      return null;
    }
    XSSFCellStyle style = styleCache.get(getName() + ".style." + status);
    if (style != null)
      return style;

    XSSFColor borderColor = new XSSFColor(Color.lightGray);
    if (status.equals("ACTIVE")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(149, 183, 93)));
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("REQUIRED")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(223, 239, 239)));
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("NOT REQUIRED")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(239, 223, 239)));
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("COMPLETED")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(63, 186, 228)));
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("STARTING")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(149, 183, 93)));
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("NOT SET")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(239, 239, 239)));
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setTopBorderColor(borderColor);
    style.setBottomBorderColor(borderColor);
    style.setLeftBorderColor(borderColor);
    style.setRightBorderColor(borderColor);
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    String statusString = null;
    if (data instanceof Map) {
      Map<String, Object> phase = (Map<String, Object>) data;
      Boolean active = (Boolean) phase.get("active");
      Boolean enabled = (Boolean) phase.get("enabled");
      String endDate = (String) phase.get("endDate");
      Boolean frozen = (Boolean) phase.get("frozen");

      if (Boolean.TRUE.equals(active))
        statusString = "ACTIVE";
      else if (endDate != null)
        statusString = "COMPLETED";
      else if (Boolean.TRUE.equals(frozen) && Boolean.TRUE.equals(enabled))
        statusString = "STARTING";
      else if (Boolean.TRUE.equals(frozen) && Boolean.FALSE.equals(enabled))
        statusString = "NOT REQUIRED";
      else if (enabled == null)
        statusString = "NOT SET";
      else if (Boolean.FALSE.equals(enabled))
        statusString = "NOT REQUIRED";
      else if (Boolean.TRUE.equals(enabled))
        statusString = "REQUIRED";
      report.getContext().getContext().put(getName() + ".status", statusString);
    }
    return statusString;
  }
}
