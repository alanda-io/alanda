package io.alanda.base.reporting.format;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

public class BooleanToYesNoCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "BooleanToYesNo";
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
    if (data instanceof Boolean)
      return (Boolean) data ? "Yes" : "No";
    if (data instanceof Integer)
      return (Integer) data > 0 ? "Yes" : "No";
    if (data instanceof String)
      return ((String) data).equals("1") ? "Yes" : "No";
    return data;
  }
}
