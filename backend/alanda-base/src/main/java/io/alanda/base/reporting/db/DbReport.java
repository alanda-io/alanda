/**
 * 
 */
package io.alanda.base.reporting.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ReportSheetDto;
import io.alanda.base.reporting.Report;

/**
 * @author jlo
 */
public class DbReport {

  private SXSSFWorkbook workbook;

  private Sheet sheet;

  private final String defaultDateFormat = "dd-MM-yyyy";

  XSSFCellStyle dateStyle;

  /**
   * 
   * @param report
   * @param config
   * @param headers
   * @param data
   */
  public DbReport(Report report, ReportSheetDto config, List<FieldHeader> headers, List<Map<String, Object>> data) {
    this.workbook = report.getWorkbook();
    
    createSheet(config.getSheetName());
    createStyles();
    createHeader(headers);
    addData(headers, data);
    setWidth(headers);
  }

  private void setWidth(List<FieldHeader> headers) {
    for (int i = 0; i < headers.size(); i++ ) {
      sheet.autoSizeColumn(i);
    }
  }

  public SXSSFWorkbook getWorkbook() {
    return workbook;
  }

  private void createHeader(List<FieldHeader> headers) {
    Row headerRow = sheet.createRow(0);
    headerRow.setHeightInPoints(12.75f);
    for (int i = 0; i < headers.size(); i++ ) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(headers.get(i).getName());
    }

  }

  private void createStyles() {
    dateStyle = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();
    dateStyle.setDataFormat(sheet.getWorkbook().getCreationHelper().createDataFormat().getFormat(defaultDateFormat));
  }

  private void createSheet(String sheetName) {
    sheet = workbook.createSheet(sheetName);
    PrintSetup printSetup = sheet.getPrintSetup();
    printSetup.setLandscape(true);
    sheet.setFitToPage(true);
    sheet.setHorizontallyCenter(true);
  }

  private void addData(List<FieldHeader> headers, List<Map<String, Object>> data) {
    int lastRowNum = sheet.getLastRowNum();
    for (Map<String, Object> map : data) {
      addDataRow(headers, map, ++lastRowNum);
    }
  }

  private void addDataRow(List<FieldHeader> headers, Map<String, Object> data, int rowIndex) {
    Row row = sheet.createRow(rowIndex);
    for (int i = 0; i < headers.size(); i++ ) {
      FieldHeader h = headers.get(i);
      addCell(row, data.get(h.getName()), h.getClazz(), i);
    }
    sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers.size() - 1));
  }

  private void addCell(Row row, Object value, Class<?> clazz, int columnIndex) {
    Cell cell = row.createCell(columnIndex);
    if (value == null) {
      cell.setCellType(Cell.CELL_TYPE_BLANK);
    } else if (clazz.equals(String.class)) {
      cell.setCellValue((String) value);
    } else if (clazz.equals(Date.class)) {
      cell.setCellValue((Date) value);
      cell.setCellStyle(dateStyle);
    } else if (clazz.equals(Number.class)) {
      cell.setCellValue(((Number) value).intValue());
    }
  }

}
