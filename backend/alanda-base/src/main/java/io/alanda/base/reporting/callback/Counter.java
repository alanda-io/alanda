/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.reporting.callback;

import java.util.Map;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.reporting.ReportContext;
import io.alanda.base.reporting.ReportUtils;

/**
 *
 * @author developer
 */
public class Counter implements Callback {

  @Override
  public String getName() {
    return "counter";
  }

  @Override
  public void execute(Map<String, Object> params, ElasticEntryDto entry, Report report, Integer rowIndex, Integer colIndex) {
    ReportContext reportContext = report.getContext();
    String[] fieldPath = ReportUtils.evaluateTemplate((String) params.get("key"),entry);
    Integer count = (Integer) reportContext.get(fieldPath);
    if (count == null) count = 0;
    count++;
    reportContext.put(fieldPath, count); 
  }
  
}
