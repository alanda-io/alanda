/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.reporting.postprocess.DefaultPostProcessor;

/**
 * @author developer
 */
public class SubProcessCountFormatter implements ReportCellFormatter {

  @Override
  public void init() {

  }

  @Override
  public String getName() {
    return "SubProcCounter";
  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    return null;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    Integer created = 0;
    Integer closed = 0;
    if (data instanceof Map) {
      created = (Integer) ((Map) data).get(DefaultPostProcessor.CREATEDCOUNT);
      if (created == null)
        created = 0;
      closed = (Integer) ((Map) data).get(DefaultPostProcessor.CLOSEDCOUNT);
      if (closed == null)
        closed = 0;
    }
    return Integer.toString(created) + "-" + Integer.toString(closed);
  }

}
