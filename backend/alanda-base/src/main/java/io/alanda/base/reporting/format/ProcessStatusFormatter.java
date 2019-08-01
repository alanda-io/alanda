/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.ColumnType;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class ProcessStatusFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "ProcStat";
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
    // only works correctly with linePerProcess trackers!!!
    if (data == null) {
      return "ACTIVE";
    } else {
      Map<String, Object> subs = (Map<String, Object>) entry.getData().get(ColumnType.PROCESS.getLabel());
      for (String key : subs.keySet()) {
        Map<String, Object> subProc = (Map<String, Object>) subs.get(key);
        Object deleteReason = subProc.get("deleteReason");
        if (deleteReason != null) {
          return "CANCELLED";
        }
      }
      return "COMPLETED";
    }
  }

}
