package io.alanda.base.reporting;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import io.alanda.base.dto.reporting.ReportSheetDto;
import io.alanda.base.service.PmcReportConfigService;


public class Report {

  private SXSSFWorkbook workbook;

  private final Collection<ReportSheet> reportSheets = new ArrayList<>();
  
  private ReportContext context;

  public Report() {
    createWorkbook();
    this.context = new ReportContext();
  }

  public SXSSFWorkbook getWorkbook() {
    return workbook;
  }

  private void createWorkbook() {
    workbook = new SXSSFWorkbook(100);
  }

  public ReportSheet addSheet(ReportSheetDto sheet, PmcReportConfigService pmcReportConfigService, Long version) {
    ReportSheet newSheet = new ReportSheet(this, sheet, pmcReportConfigService, version);
    reportSheets.add(newSheet);
    return newSheet;
  }

  public ReportContext getContext() {
    return context;
  }

}
