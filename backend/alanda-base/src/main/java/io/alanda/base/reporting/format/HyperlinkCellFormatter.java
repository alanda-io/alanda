/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting.format;

import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.service.ConfigService;

/**
 * @author developer
 */
public class HyperlinkCellFormatter implements ReportCellFormatter {

  @Inject
  private ConfigService configService;

  @Override
  public String getName() {
    return "Hyperlink";
  }

  @Override
  public void init() {

  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    Workbook workbook = report.getWorkbook();
    Map<String, XSSFCellStyle> styleCache = report.getContext().getStyleCache();
    XSSFCellStyle hlink_style = styleCache.get(getName() + ".hlink_style");
    String link = (String) report.getContext().getContext().remove(getName() + ".link");
    if (link != null) {
      if (hlink_style == null) {
        hlink_style = (XSSFCellStyle) workbook.createCellStyle();
        Font hlink_font = workbook.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlink_style.setFont(hlink_font);
        styleCache.put(getName() + ".hlink_style", hlink_style);
      }

      String baseUrl = configService.getProperty(ConfigService.PMC_BASE_URL) + "pmc/projectdetails/";

      CreationHelper createHelper = workbook.getCreationHelper();
      Hyperlink hlink = createHelper.createHyperlink(Hyperlink.LINK_URL);
      hlink.setAddress(baseUrl + link);
      cell.setHyperlink(hlink);
    }
    return hlink_style;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof String) {
      report.getContext().getContext().put(getName() + ".link", data);
    }
    return data;
  }

}
