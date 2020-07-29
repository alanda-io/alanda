package io.alanda.base.reporting;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.dto.reporting.ReportCallbackDto;
import io.alanda.base.dto.reporting.ReportColumnDto;
import io.alanda.base.dto.reporting.ReportSheetDto;
import io.alanda.base.reporting.callback.Callback;
import io.alanda.base.reporting.format.ReportCellFormatter;
import io.alanda.base.service.PmcReportConfigService;

import com.google.common.base.Strings;

/**
 * @author Stephan Zavrel
 */
public class ReportSheet {

  private static final Logger log = LoggerFactory.getLogger(ReportSheet.class);

  private final String MS_SOURCE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  private final String MS_TARGET_DATE_FORMAT = "dd-MM-yyyy hh:mm";

  private final ReportSheetDto configuration;

  private PmcReportConfigService pmcReportConfigService;

  private final ReportHeader reportHeader;

  private final Sheet sheet;

  private final Report report;
  
  public ReportSheet(Report report, ReportSheetDto configuration, PmcReportConfigService pmcReportConfigService, Long version) {
    this.configuration = configuration;
    this.report = report;
    this.sheet = createSheet(report.getWorkbook());
    this.pmcReportConfigService = pmcReportConfigService;
    this.reportHeader = createHeader(configuration, pmcReportConfigService, version);
    //    addGroups();
    //    highlightCallActivityColumns();
    //    addData(data);
    //    reportHeader.mergeColumnHeaders();
    //    sizeColumns();
    //    addFreezePane();
  }

  private Sheet createSheet(Workbook workbook) {
    Sheet sheet = workbook.createSheet(configuration.getSheetName());
    PrintSetup printSetup = sheet.getPrintSetup();
    printSetup.setLandscape(true);
    sheet.setFitToPage(true);
    sheet.setHorizontallyCenter(true);
    return sheet;
  }
  
  private XSSFCellStyle getColStyle(ReportColumnDto columnDef) {
    Map<String, XSSFCellStyle> cellStyles = report.getContext().getStyleCache();
    XSSFCellStyle colStyle = cellStyles.get("colstyle." + columnDef.getStartIndex());
    if (colStyle == null) {
      colStyle = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();
      Map<String, Object> styles = columnDef.getColStyle();
      String bgColor = (String) styles.get("bgColor");
      if (bgColor != null) {
        Color c = StringToColor(bgColor);
        colStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        colStyle.setFillForegroundColor(new XSSFColor(c));
//        Font f = colStyle.getFont();
//        f.setColor(ReportUtils.getTextColorForBackground(c));
//        colStyle.setFont(f);
      }
      Integer fontSize = (Integer) styles.get("fontSize");
      if (fontSize != null) {
        Font f  = sheet.getWorkbook().createFont();
        f.setFontHeightInPoints(fontSize.shortValue());
        colStyle.setFont(f);
      }
      Boolean border = (Boolean) styles.get("border");
      if ((border != null) && (border == true)) {
        colStyle.setBorderBottom(BorderStyle.THIN);
        colStyle.setBorderTop(BorderStyle.THIN);
        colStyle.setBorderLeft(BorderStyle.THIN);
        colStyle.setBorderRight(BorderStyle.THIN);
      }
      String align = (String) styles.get("align");
      if ("left".equals(align)) colStyle.setAlignment(HorizontalAlignment.LEFT);
      if ("center".equals(align)) colStyle.setAlignment(HorizontalAlignment.CENTER);
      if ("right".equals(align)) colStyle.setAlignment(HorizontalAlignment.RIGHT);
      
      cellStyles.put("colstyle." + columnDef.getStartIndex(), colStyle);
    }
    return colStyle;
  }

  private ReportHeader createHeader(ReportSheetDto configuration, PmcReportConfigService pmcReportConfigService, Long version) {
    return new ReportHeader(sheet, configuration, pmcReportConfigService, version);
  }

  public int getLastRowNum() {
    return sheet.getLastRowNum();
  }

  public void addDataRow(ElasticEntryDto record, int rowIndex) {
    Row row = sheet.getRow(rowIndex);
    if (row == null) {
      row = sheet.createRow(rowIndex);
    }
    for (ReportColumnDto column : reportHeader.getMainHeaderRow()) {
      Map<String, Object> rootData = record.getData();
      Map<String, Object> data = rootData;
      for (ReportColumnDto element : column.getPath()) {
        switch (element.getType()) {
          case PROCESS:
            data = (Map<String, Object>) data.get(ColumnType.PROCESS.getLabel());
            if (data != null) {
              String[] fieldParts = element.getFieldArray(",");
              Map<String, Object> tempData = null;
              for (String f : fieldParts) {
                tempData = (Map<String, Object>) data.get(f);
                if (tempData != null)
                  break;
              }
              data = tempData;
            }
            break;
          case ACTIVITY:
            data = (Map<String, Object>) data.get(ColumnType.ACTIVITY.getLabel());
            if (data != null) {
              boolean orHit = false;
              for (String orField : element.getField().split(Pattern.quote("||")))
                if (data.containsKey(orField)) {
                  data = (Map<String, Object>) data.get(orField);
                  orHit = true;
                  break;
                }
              if (!orHit) data = null;
            }
            break;
          case REFOBJECT:
            data = (Map<String, Object>) data.get(ColumnType.REFOBJECT.getLabel());
            if (data == null) {
              data = (Map<String, Object>) rootData.get(ColumnType.REFOBJECT.getLabel());
            }
            break;
          case PROJECT:
            data = (Map<String, Object>) data.get(ColumnType.PROJECT.getLabel());
            if (data == null)
              data = (Map<String, Object>) rootData.get(ColumnType.PROJECT.getLabel());
            break;
          case VARIABLE:
            data = (Map<String, Object>) data.get(ColumnType.VARIABLE.getLabel());
            if (data != null) {
              data = (Map<String, Object>) data.get(element.getField());
            }
            break;
          case MILESTONE:
            data = (Map<String, Object>) data.get(ColumnType.MILESTONE.getLabel());
            if (data != null) {
              data = (Map<String, Object>) data.get(element.getField());
            }
            break;
          case PMCMILESTONE:
            data = (Map<String, Object>) data.get(ColumnType.PMCMILESTONE.getLabel());
            if (data != null) {
              data = (Map<String, Object>) data.get(element.getField());
            }
            break;
          case CONTACT:
            data = (Map<String, Object>) data.get(ColumnType.CONTACT.getLabel());
            break;
          case FIELD:
            break;
          case GROUP:
            break;
          case CONTEXT:
            data = report.getContext().getContext();
            break;
        }
        if (data == null)
          break; // field not found in data, don't parse further
      }
      //      if (data == null)
      //        continue; // field not found in data, continue with next column
      setCellFromField(data, column, row, column.getStartIndex(), record);
    }
  }

  public Report getReport() {
    return report;
  }

  private void setCellFromField(Map<String, Object> map, ReportColumnDto columnDef, Row row, Integer columnIndex, ElasticEntryDto record) {
    //Todo: null handling
    Object tmp;
    if (map == null) {
      tmp = new HashMap<>();
    } else {
      tmp = map;
    }
    XSSFCellStyle style = null;
    if (columnDef.getColStyle() != null) style = getColStyle(columnDef);
    String[] eval = ReportUtils.evaluateTemplate(columnDef.getField(), record);
    tmp = ReportUtils.getFieldValue(tmp, eval);
    Cell cell = row.createCell(columnIndex);
    if (columnDef.getCallbacks() != null) {
      for (ReportCallbackDto callbackDto : columnDef.getCallbacks()) {
        Callback callback = pmcReportConfigService.getCallbacks().get(callbackDto.getName());
        callback.execute(callbackDto.getParams(), record, report, row.getRowNum(), columnIndex);
      }
    }
    if (!Strings.isNullOrEmpty(columnDef.getFormatter())) {
      ReportCellFormatter formatter = pmcReportConfigService.getCellFormatters().get(columnDef.getFormatter());
      if (formatter == null) {
        throw new IllegalArgumentException("No ReportCellFormatter named '" + columnDef.getFormatter() + "' found.");
      }
      tmp = formatter.formatCell(tmp, record, report);
      XSSFCellStyle fStyle = formatter.getStyle(report, cell);
      if (fStyle != null) style = fStyle;
    }
    setTypedCellValue(cell, tmp, style);
  }

  private void setTypedCellValue(Cell cell, Object value, XSSFCellStyle style) {
    if (style != null)
      cell.setCellStyle(style);
    if (value instanceof List) {
      String tmp = "";
      for (Object element : (List<Object>) value) {
        tmp += element.toString() + " ";
      }
      cell.setCellValue(tmp.trim());
    } else if (value instanceof String) {
      cell.setCellValue((String) value);
    } else if (value instanceof Integer) {
      cell.setCellValue((Integer) value);
    } else if (value instanceof Boolean) {
      cell.setCellValue((Boolean) value);
    } else if (value instanceof Date) {
      cell.setCellValue((Date) value);
    } else if (value instanceof Long) {
      cell.setCellValue((Long) value);
    } else if (value instanceof Float) {
      cell.setCellValue((Float) value);
    } else if (value instanceof Double) {
      cell.setCellValue((Double) value);
    } else if (value instanceof BigDecimal) {
      cell.setCellValue(((BigDecimal) value).doubleValue());
    }
  }

  public void autoSizeColumns() {
    for (ReportColumnDto col : reportHeader.getMainHeaderRow()) {
      long curr = System.currentTimeMillis();
      boolean ws = false;
      if (col.getWidth() != null) {
        if (col.getWidth() > 255) { // max allowed width = 255 characters
          col.setWidth(255);
        }
        ws = true;
        sheet.setColumnWidth(col.getStartIndex(), col.getWidth() * 256); // according to doc: number of characters * 256;
      } else if (col.getDefaultWidth() != null) {
        ws = true;
        sheet.setColumnWidth(col.getStartIndex(), (int) (col.getDefaultWidth() * 256)); // according to doc: number of characters * 256;
      } else {
        sheet.autoSizeColumn(col.getStartIndex(), true);
      }
      curr = System.currentTimeMillis() - curr;
      log.info("{}ms for Column of Report {}, field: {}, {}", curr, this.configuration.getSheetName(), col.getTitle(), col.getFormatter());
    }
  }

  public void createFreezePane(Integer colNumToFreeze) {
    if (colNumToFreeze == null) {
      colNumToFreeze = 1;
    }
    sheet.createFreezePane(colNumToFreeze, reportHeader.getHeaderSize());
  }

  public void createAutoFilter() {
    CellRangeAddress adr = new CellRangeAddress(
      reportHeader.getHeaderSize() - 1,
      reportHeader.getHeaderSize() - 1,
      0,
      reportHeader.getMainHeaderRow().size() - 1);
    sheet.setAutoFilter(adr);
  }
  
  private Color StringToColor(String rgbString) {
    String[] rgb = rgbString.split(",");
    Color color = null;
    try {
      color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    } catch (Exception e) {
      color = new Color(255, 255, 255);
    }
    return color;
  }

}
