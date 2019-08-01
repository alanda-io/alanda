package io.alanda.base.reporting.format;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.reporting.ReportUtils;

public class MilestoneHistoryFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "MilestoneHistory";
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
      styleCache.put(getName() + ".style", style);
    }
    if (lines != null)
      ReportUtils.setRowHeight(cell.getRow(), lines, ReportUtils.MAX_ROW_HEIGHT);
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data == null || !(data instanceof List)) {
      report.getContext().getContext().remove(getName() + ".lines");
      return null;
    }

    List<Map<String, Object>> msList = (List<Map<String, Object>>) data;
    Collections.sort(msList, new Comparator<Map<String, Object>>() {

      @Override
      public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        String d1 = (String) o1.get("logDate");
        String d2 = (String) o2.get("logDate");
        if (d1 == null || d2 == null)
          return 0;
        return d1.compareTo(d2);
      }
    });

    String result = "";
    for (Map<String, Object> ms : msList) {
      result += ms.get("newValue") + " - " + ms.get("text") + " (" + ms.get("user") + ")\n";
    }
    report.getContext().getContext().put(getName() + ".lines", msList.size());

    return result.substring(0, result.length() - 1);
  }
}
