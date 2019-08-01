package io.alanda.base.reporting.format;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

public class MonthCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "Months";
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
    if (data instanceof Integer) {
      Integer months = (Integer) data;
      if (months == 1)
        return months.toString() + " Month";
      else
        return months.toString() + " Months";
    }
    return data;
  }
}
