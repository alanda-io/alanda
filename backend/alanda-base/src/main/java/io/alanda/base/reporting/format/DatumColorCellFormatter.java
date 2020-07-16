package io.alanda.base.reporting.format;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

public class DatumColorCellFormatter implements ReportCellFormatter {

  private static final Logger log = LoggerFactory.getLogger(DatumColorCellFormatter.class);

  public final String MS_TARGET_DATE_FORMAT = "yyyy-MM-dd";

  @Override
  public void init() {
  }

  @Override
  public String getName() {
    return "DatumColor";
  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {

    final String STYLE_ID_GREEN = "green";
    final String STYLE_ID_RED = "red";

    Workbook workbook = report.getWorkbook();

    Date datum = (Date) report.getContext().getContext().remove(getName() + ".date");
    String styleId = datum != null ? STYLE_ID_GREEN : STYLE_ID_RED;

    Map<String, XSSFCellStyle> styleCache = report.getContext().getStyleCache();
    XSSFCellStyle style = styleCache.get(getName() + ".style." + styleId);
    XSSFColor borderColor = new XSSFColor(Color.lightGray);
    if (style == null) {
      if (STYLE_ID_GREEN.equals(styleId)) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(MS_TARGET_DATE_FORMAT));
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
        styleCache.put(getName() + ".style." + styleId, style);
      } else if (STYLE_ID_RED.equals(styleId)) {
        style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
        styleCache.put(getName() + ".style." + styleId, style);
      }
    }
    return style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof Long) {
      Date d = new Date((Long) data);
      report.getContext().getContext().put(getName() + ".date", d);
      return d;
    } else if (data instanceof String) {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

      Date d = new Date();
      try {
        d = formatter.parse((String) data);
      } catch (ParseException e) {
        log.warn("not good: {}", e);
      }
      report.getContext().getContext().put(getName() + ".date", d);
      return d;
    } else {
      return data;
    }
  }

}
