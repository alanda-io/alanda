package io.alanda.base.reporting.format;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

public class StatusColorCellFormatter implements ReportCellFormatter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void init() {
    // TODO Auto-generated method stub

  }

  @Override
  public String getName() {
    return "StatusColor";
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
    if (status.equals("CANCELED")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("COMPLETED")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    if (status.equals("ACTIVE")) {
      if (style == null) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        styleCache.put(getName() + ".style." + status, style);
      }
    }
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof String) {
      report.getContext().getContext().put(getName() + ".status", data);
    }
    return data;
  }

}
