/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import io.alanda.base.dto.PmcReportConfigDto;
import io.alanda.base.dto.reporting.ReportColumnDto;
import io.alanda.base.dto.reporting.ReportSheetDto;
import io.alanda.base.service.PmcReportConfigService;

import com.google.common.base.Strings;

/**
 *
 * @author developer
 */
public class ReportHeader {
  
  private Sheet sheet;
  
  private ReportSheetDto config;
  
  private List<ReportColumnDto> mainHeaderRow;
  
  private List<ReportColumnDto> leafRow = new ArrayList<>();
  
  private Map<Integer, List<ReportColumnDto>> headerRows = new TreeMap<>();
  
  private Map<String, XSSFCellStyle> cellStyles = new HashMap<>();
  
  private CellStyle rowCountCellStyle = null;
  
  private PmcReportConfigService pmcReportConfigService;
  
  private class ParseResult {
    
    int depth;
    
    int colIndex;
    
    List<ReportColumnDto> list = new ArrayList<>();
    
    public int getDepth() {
      return depth;
    }

    public void setDepth(int depth) {
      this.depth = depth;
    }

    public List<ReportColumnDto> getList() {
      return list;
    }

    public void setList(List<ReportColumnDto> list) {
      this.list = list;
    }

    public int getColIndex() {
      return colIndex;
    }

    public void setColIndex(int colIndex) {
      this.colIndex = colIndex;
    }

  }

  public ReportHeader(Sheet sheet, ReportSheetDto config, PmcReportConfigService pmcReportConfigService, Long version) {
    this.sheet = sheet;
    this.config = config;
    this.pmcReportConfigService = pmcReportConfigService;
    if (version < 1) {
      createColumnHeaderRow();
    } else {
      createColumnHeaderRowNew();
    }
  }

    private void createColumnHeaderRow() {

    int columnIndex = 0;
    ReportColumnDto root = new ReportColumnDto(ColumnType.GROUP,"","");
    root.setColumns(config.getColumns());
    parseHeader(root, 0, columnIndex, null);
    //Create Row for RowCounts
    sheet.createRow(0);
    // Start iteration at 1 because Row Count Row is at 0 and will be filled later
    for (int rowIndex = 1; rowIndex < headerRows.keySet().size(); rowIndex++) {
        Row excelRow = sheet.createRow(rowIndex);
        List<ReportColumnDto> row = headerRows.get(rowIndex);
        for (int i = 0; i < row.size(); i++) {
          CellRangeAddress nameRange = new CellRangeAddress(rowIndex, rowIndex, row.get(i).getStartIndex(), row.get(i).getEndIndex());
          sheet.addMergedRegion(nameRange);
          if (row.get(i).getCollapse()) {
            sheet.groupColumn(row.get(i).getStartIndex(), row.get(i).getEndIndex());
            sheet.setColumnGroupCollapsed(row.get(i).getStartIndex(), true);
          }
          Cell cell = excelRow.createCell(row.get(i).getStartIndex());
          cell.setCellStyle(getHeaderCellStyle(row.get(i)));
          cell.setCellValue(row.get(i).getTitle());
          // last row is main header row, so every column is called;
          if ((rowIndex == headerRows.keySet().size() - 1) && (config.isIncludeCountHeader())){
            createRowCountHeader(i, rowIndex + 2);
          }
        }
    }
    mainHeaderRow = headerRows.get(headerRows.keySet().size() - 1);
  }
  
  private void createColumnHeaderRowNew() {

    int columnIndex = 0;
    ReportColumnDto root = new ReportColumnDto(ColumnType.GROUP,"ROOT","");
    root.setColumns(config.getColumns());
    ParseResult r = parseHeaderNew(root, 0, columnIndex, null, null);
    //Create Row for RowCounts
    sheet.createRow(0);
    int depth = getTemplateDepth(root);
//    padList(r.getList(), 0, depth);
    fillHeaderRows(r, depth);
    // Start iteration at 1 because Row Count Row is at 0 and will be filled later
    for (int rowIndex = 1; rowIndex < headerRows.keySet().size(); rowIndex++) {
        Row excelRow = sheet.createRow(rowIndex);
        List<ReportColumnDto> row = headerRows.get(rowIndex); //ignore ROOT Group, so start at higher 
        for (int i = 0; i < row.size(); i++) {
          CellRangeAddress nameRange = new CellRangeAddress(rowIndex, rowIndex, row.get(i).getStartIndex(), row.get(i).getEndIndex());
          sheet.addMergedRegion(nameRange);
          if (row.get(i).getCollapse()) {
            sheet.groupColumn(row.get(i).getStartIndex(), row.get(i).getEndIndex());
            sheet.setColumnGroupCollapsed(row.get(i).getStartIndex(), true);
          }
          Cell cell = excelRow.createCell(row.get(i).getStartIndex());
          cell.setCellStyle(getHeaderCellStyle(row.get(i)));
          cell.setCellValue(row.get(i).getTitle());
          // last row is main header row, so every column is called;
          if ((rowIndex == headerRows.keySet().size() - 1) && (config.isIncludeCountHeader())) {
            createRowCountHeader(i, rowIndex + 2);
          }
        }
    }
    mainHeaderRow = headerRows.get(headerRows.keySet().size() - 1);
  }
  
  private void fillHeaderRows(ParseResult r,int depth) {
    for (int i=0; i< depth; i++) {
      headerRows.put(i, new ArrayList<>());
    }
    for (ReportColumnDto column : r.getList()) {
      int j = 0;
      for (ReportColumnDto reportColumnDto : column.getPath()) {
        if (column.getStartIndex().intValue() == reportColumnDto.getStartIndex().intValue()) {
          headerRows.get(j).add(reportColumnDto);
        }
        j++;
      }
    }  
  }

  private void balanceTree(List<ReportColumnDto> list, int rowIndex, int depth) {
    for (ReportColumnDto column : list) {
      if (column.getPath().size() < depth + rowIndex) {
        while (column.getPath().size() < depth + rowIndex) {
          ReportColumnDto paddingGroup = new ReportColumnDto(ColumnType.GROUP, "", null);
          paddingGroup.setStartIndex(column.getStartIndex());
          paddingGroup.setEndIndex(column.getEndIndex());
          paddingGroup.setBgcolor(column.getBgcolor());
          column.getPath().add(rowIndex + 1,paddingGroup);
        } 
      }
    }  
  }

  
  private int parseHeader(ReportColumnDto column, int rowIndex, int colIndex, String bgColor) {
    
    if (!headerRows.containsKey(rowIndex)) {
      headerRows.put(rowIndex, new ArrayList<>());
    }
    List<ReportColumnDto> row = headerRows.get(rowIndex);
    if (!Strings.isNullOrEmpty(column.getTemplate())) {
      List<ReportColumnDto>  template = getDefaultTemplate(column.getTemplate());
      column.setColumns(template);
    }
    if (column.getType() != ColumnType.GROUP) {
      column.getPath().add(column);
    }
    column.setStartIndex(colIndex);
    if (Strings.isNullOrEmpty(column.getBgcolor())) {
      column.setBgcolor(bgColor);
    }
    row.add(column);
    for (ReportColumnDto col : column.getColumns()) {
      col.getPath().addAll(column.getPath());
      colIndex = parseHeader(col,rowIndex + 1, colIndex, column.getBgcolor());
    }
    if (column.getColumns().isEmpty()) {
        colIndex++;
    }
    column.setEndIndex(Math.max(0, colIndex - 1));
    return colIndex;
  }
  
  private ParseResult parseHeaderNew(ReportColumnDto column, int rowIndex, int colIndex, String bgColor, Short fontSize) {
    
    int depth = 0;
    ParseResult result = new ParseResult();
    if (!Strings.isNullOrEmpty(column.getTemplate())) {
      List<ReportColumnDto>  template = getDefaultTemplate(column.getTemplate());
      column.setColumns(template);
    }
    column.getPath().add(column);
    column.setStartIndex(colIndex);
    if (Strings.isNullOrEmpty(column.getBgcolor())) {
      column.setBgcolor(bgColor);
    }
    if (column.getFontSize() == null) {
      column.setFontSize(fontSize);
    }

    for (ReportColumnDto col : column.getColumns()) {
      col.getPath().addAll(column.getPath());
      ParseResult res = parseHeaderNew(col,rowIndex + 1, colIndex, column.getBgcolor(), column.getFontSize());
      result.getList().addAll(res.getList());
      colIndex = res.getColIndex();
    }
    if (column.getColumns().size() > 1) { //no need to balance leaf or single child tree
      depth = getTemplateDepth(column);
      balanceTree(result.getList(), rowIndex, depth);
    }
    if (column.getColumns().isEmpty()) {
        colIndex++;
        result.getList().add(column);
    }
    result.setColIndex(colIndex);
    column.setEndIndex(Math.max(0, colIndex - 1));
    return result;
  }
    
  private List<ReportColumnDto> getDefaultTemplate(String templateName) {

   PmcReportConfigDto configuration = pmcReportConfigService.getReportConfigByName(templateName);

   return configuration.getSheets().get(0).getColumns();
  }
    
  private int getTemplateDepth(ReportColumnDto root) {
    int max = 0;
    for (ReportColumnDto column : root.getColumns()) {
      max = Math.max(max,getTemplateDepth(column));
    }
    return max + 1;
  }
  
  private CellStyle getHeaderCellStyle(ReportColumnDto column) {
    XSSFCellStyle style = cellStyles.get(column.getBgcolor());
    if (style == null) {
      style = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();
      style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
      style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
      style.setFillForegroundColor(new XSSFColor(column.getBgAsColor()));
      Font font = sheet.getWorkbook().createFont();
      font.setColor(ReportUtils.getTextColorForBackground(column.getBgAsColor()));
      font.setFontHeightInPoints(column.getFontSize() != null ? column.getFontSize() : 10);
      style.setFont(font);
      cellStyles.put(column.getBgcolor(), style);
    }
    return style;
  }
  
 
    private void createRowCountHeader(int cellIndex, int startRowIndex) {
      Cell rowCountCell = sheet.getRow(0).createCell(cellIndex);
      rowCountCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
      String columnLetter = CellReference.convertNumToColString(cellIndex);
      String startRow = columnLetter + startRowIndex;
      String endRow = columnLetter + "10000";
      rowCountCell.setCellFormula("SUBTOTAL(3," + startRow + ":" + endRow + ")");
      rowCountCell.setCellStyle(getRowCountCellStyle());
  }

  private CellStyle getRowCountCellStyle() {
    if (rowCountCellStyle == null) {
      CellStyle newStyle = sheet.getWorkbook().createCellStyle();
      newStyle.setAlignment(CellStyle.ALIGN_CENTER);
      newStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
  //    newStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
      newStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
      Font font = sheet.getWorkbook().createFont();
      font.setColor(HSSFColor.WHITE.index);
      font.setFontHeightInPoints((short) 10);
      newStyle.setFont(font);
      rowCountCellStyle = newStyle;
    }
    return rowCountCellStyle;
  }
  
  public Sheet getSheet() {
    return sheet;
  }

  public void setSheet(Sheet sheet) {
    this.sheet = sheet;
  }

  public ReportSheetDto getConfig() {
    return config;
  }

  public void setConfig(ReportSheetDto config) {
    this.config = config;
  }

  public List<ReportColumnDto> getMainHeaderRow() {
    return mainHeaderRow;
  }
  
  public int getHeaderSize() {
    return headerRows.size();
  }
  
}
