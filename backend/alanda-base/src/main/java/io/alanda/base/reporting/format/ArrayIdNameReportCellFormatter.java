/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class ArrayIdNameReportCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "ArrayIdName";
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
    if (data instanceof List) {
      List<String> ret = new ArrayList<>();
      for (Object element : (List) data) {
        String tmp = (String) ((Map<String, Object>) element).get("idName");
        if (tmp != null)
          ret.add(tmp);
      }
      return ret;
    }
    return data;
  }

}
