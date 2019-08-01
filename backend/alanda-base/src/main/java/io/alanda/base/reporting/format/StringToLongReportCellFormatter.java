/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class StringToLongReportCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "ToLong";
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
    if (data instanceof String) {
      try {
        Long ret = Long.parseLong((String) data);
        return ret;
      } catch (NumberFormatException nfe) {
        return data;
      }
    } else {
      return data;
    }
  }

}
