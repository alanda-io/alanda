/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 * @author developer
 */
public class StringToDateReportCellFormatter extends DateDayReportCellFormatter {

  @Override
  public String getName() {
    return "ToDate";
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof String) {
      SimpleDateFormat sdf = new SimpleDateFormat(MS_TARGET_DATE_FORMAT);
      try {
        Date ret = sdf.parse((String) data);
        return ret;
      } catch (ParseException ex) {
        return data;
      }
    } else {
      return data;
    }
  }

}
