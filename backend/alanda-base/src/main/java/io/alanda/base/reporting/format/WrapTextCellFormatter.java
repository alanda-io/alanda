package io.alanda.base.reporting.format;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.reporting.ReportUtils;

public class WrapTextCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "WrapText";
  }

  @Override
  public void init() {
  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    Integer lines = (Integer) report.getContext().getContext().remove(getName() + ".lines");
    Workbook workbook = report.getWorkbook();
    Map<String, XSSFCellStyle> styleCache = report.getContext().getStyleCache();
    XSSFCellStyle style = styleCache.get(getName() + ".style");
    if (style == null) {
      style = (XSSFCellStyle) workbook.createCellStyle();
      style.setWrapText(true);
      cell.getRow().setHeight((short) -1);
      styleCache.put(getName() + ".style", style);
    }
    if (lines != null)
      ReportUtils.setRowHeight(cell.getRow(), lines, ReportUtils.MAX_ROW_HEIGHT);
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    String sd = (String) data;
    int numLines = 1;
    if (data != null) {
      for (int i = 0; i < sd.length(); i++ ) {
        if (sd.charAt(i) == '\n')
          numLines++ ;
      }
    }
    report.getContext().getContext().put(getName() + ".lines", numLines);
    return data;
  }
}
