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
public interface ReportCellFormatter {

  public void init();

  public String getName();

  public XSSFCellStyle getStyle(Report report, Cell cell);

  public Object formatCell(Object data, ElasticEntryDto entry, Report report);

}
