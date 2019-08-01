package io.alanda.base.reporting.format;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author FSA
 */
public class ListToStringReportCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "ListToString";
  }

  @Override
  public void init() {}

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    return null;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof List) {
      String result = "";
      for (Object element : (List) data) {
        result += element.toString() + ", ";
      }
      if (result.length() >= 2) {
        result = result.substring(0, result.length() - 2);
      }
      return result;
    }
    return data;
  }
}
