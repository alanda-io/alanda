/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.reporting.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.reporting.ReportContext;
import io.alanda.base.reporting.ReportUtils;

/**
 *
 * @author developer
 */
public class ColumnMerger implements Callback {

  @Override
  public String getName() {
    return "mergeCol";
  }

  @Override
  public void execute(Map<String, Object> params, ElasticEntryDto entry, Report report, Integer rowIndex, Integer colIndex) {
    ReportContext reportContext = report.getContext();    
    String[] fieldPath = ReportUtils.evaluateTemplate((String) params.get("key"),entry);
    Map<String, Object> startRow = (Map<String, Object>) reportContext.get(fieldPath);
    Integer forceStartRow = null;
    if (params.get("forceOn") != null) {
      String[] forceFieldPath = ReportUtils.evaluateTemplate((String) params.get("forceOn"),entry);
      Map<String, Object>  forceStart = (Map<String, Object>) reportContext.get(forceFieldPath);
      forceStartRow = (Integer) forceStart.get("rowIndex");
    }
    Object value = ReportUtils.getFieldValue(entry.getData(), fieldPath);
    if (startRow == null) {
      startRow = new HashMap<>();
      startRow.put("rowIndex", rowIndex);
      startRow.put("rowValue", value);
      reportContext.put(fieldPath, startRow);
    } else {
      Object oldValue = startRow.get("rowValue");
      Integer oldIndex = (Integer) startRow.get("rowIndex");
      boolean forceRange = false;
      if (Objects.equals(forceStartRow, rowIndex)) {
          forceRange = true;
      }
      if (((oldValue != null) && (!oldValue.equals(value))) || ((value != null) && (!value.equals(oldValue))) || (forceRange)) {
        //only create Range if index-Dif > 1
        if (rowIndex - oldIndex > 1) {
          Sheet sheet = report.getWorkbook().getSheetAt(report.getWorkbook().getActiveSheetIndex());
          CellRangeAddress nameRange = new CellRangeAddress(oldIndex, rowIndex - 1, colIndex, colIndex);
          sheet.addMergedRegion(nameRange);
        }
        startRow.put("rowIndex", rowIndex);
        startRow.put("rowValue", value);
      }
    }
  }
  
}
