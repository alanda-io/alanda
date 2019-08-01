package io.alanda.base.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.base.Strings;

import io.alanda.base.dto.AttachmentDto;
import io.alanda.base.dto.EmailDto;
import io.alanda.base.dto.PmcReportConfigDto;
import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.dto.reporting.ReportSheetDto;
import io.alanda.base.entity.PmcReportConfig;
import io.alanda.base.reporting.Report;
import io.alanda.base.reporting.ReportSheet;
import io.alanda.base.reporting.db.DbReport;
import io.alanda.base.reporting.db.FieldHeader;
import io.alanda.base.reporting.postprocess.DefaultPostProcessor;
import io.alanda.base.reporting.postprocess.SearchHitPostProcessor;
import io.alanda.base.reporting.postprocess.SubProcessPostProcessor;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.MailService;
import io.alanda.base.service.PmcReportConfigService;
import io.alanda.base.service.ReportService;

/**
 * @author developer
 */
@Stateless
@Named("elasticReportService")
public class ElasticReportServiceImpl implements ReportService {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource(lookup = "java:jboss/datasources/ProcessEngine")
  private DataSource ds;

  @Inject
  private PmcReportConfigService pmcReportConfigService;

  @Inject
  private ElasticService elasticService;

  @Inject
  private Instance<MailService> emailServiceInstance;

  private MailService usedInstance;

  @Inject
  private Instance<SearchHitPostProcessor> postProcessors;

  @Inject
  private ConfigService configService;

  private Map<String, SearchHitPostProcessor> postProcessorsByName;

  @PostConstruct
  private void initElasticReportService() {
    this.postProcessorsByName = new HashMap<>();
    for (SearchHitPostProcessor postProcessor : postProcessors) {
      this.postProcessorsByName.put(postProcessor.getName(), postProcessor);
      logger.info("Registered PostProcessor: " + postProcessor.getName() + "(" + postProcessor.getClass().getName() + ")");
    }
  }

  @Override
  public byte[] createReportByProjectType(String projectType) {

    Report report = new Report();
    pmcReportConfigService.initFormatters();

    PmcReportConfigDto configuration = pmcReportConfigService.getReportConfigByName(projectType);
    String[] fields = {"project.guid"};

    SXSSFWorkbook workbook = report.getWorkbook();

    for (ReportSheetDto sheetConfig : configuration.getSheets()) {
      //if sheet defines a template to another Report Config, load the other report and add those sheets
      if ( !Strings.isNullOrEmpty(sheetConfig.getReportTemplate())) {
        PmcReportConfigDto subConf = pmcReportConfigService.getReportConfigByName(sheetConfig.getReportTemplate());
        for (ReportSheetDto subSheetConfig : subConf.getSheets()) {
          // override filterParams with the ones from the report that called the reportTemplate
          subSheetConfig.setQueryParams(sheetConfig.getQueryParams());
          addSheetToReport(report, subSheetConfig, subConf, fields);
        }
      } else { // if no template is defined, just process the current report configuration
        addSheetToReport(report, sheetConfig, configuration, fields);
      }
    }
    return getBytesFromWorkbook(workbook);

  }

  @Override
  public SXSSFWorkbook createReportFromTemplateAndData(String template, List<ElasticEntryDto> data) {

    PmcReportConfigDto configuration = pmcReportConfigService.getReportConfigFromTemplateString(template);

    Report report = new Report();
    pmcReportConfigService.initFormatters();
    SXSSFWorkbook workbook = report.getWorkbook();

    ReportSheetDto sheetConfig = configuration.getSheets().get(0);
    ReportSheet sheet = report.addSheet(sheetConfig, pmcReportConfigService, configuration.getVersion());
    int lastRowNum = sheet.getLastRowNum();
    for (ElasticEntryDto elasticEntryDto : data) {
      sheet.addDataRow(elasticEntryDto, ++lastRowNum);
    }

    sheet.autoSizeColumns();
    return workbook;
  }

  private void addSheetToReport(Report report, ReportSheetDto sheetConfig, PmcReportConfigDto configuration, String[] fields) {

    if (PmcReportConfig.TYPE_ELASTIC.equals(configuration.getType())) {
      // ugly using service as parameter -> refactor
      ReportSheet sheet = report.addSheet(sheetConfig, pmcReportConfigService, configuration.getVersion());
      if (configuration.getPrimaryPostProcessor() == null) {
        configuration.setPrimaryPostProcessor(DefaultPostProcessor.NAME);
      }
      if (configuration.getSecondaryPostProcessor() == null) {
        configuration.setSecondaryPostProcessor(SubProcessPostProcessor.NAME);
      }

      int lastRowNum = sheet.getLastRowNum();
      Map<String, Object> params = sheetConfig.getQueryParams();
      if (params == null) {
        params = new HashMap<>();
      }
      SearchHit[] hits = elasticService.findByTemplate(configuration.getQuery(), params, fields, 0, 10000, true);
      for (SearchHit hit : hits) {
        ElasticEntryDto entry = new ElasticEntryDto(hit.getSourceAsMap());
        SearchHitPostProcessor postProcessor = postProcessorsByName.get(configuration.getPrimaryPostProcessor());
        postProcessor.processHit(entry, null, report.getContext().getContext());
        List<ElasticEntryDto> primaryEntryList = postProcessor.flattenEntries(entry, report.getContext().getContext());
        params.put("pmcProjectGuid", hit.field("project.guid").getValue());
        SearchHit[] subProcesses = elasticService.findByTemplate(configuration.getSecondaryQuery(), params, fields, 0, 10000, true);
        postProcessor = postProcessorsByName.get(configuration.getSecondaryPostProcessor());
        if ( !configuration.getLinePerProcess()) {
          for (ElasticEntryDto primaryEntry : primaryEntryList) {
            postProcessor.processHit(primaryEntry, subProcesses, report.getContext().getContext());
            for (ElasticEntryDto secondaryEntry : postProcessor.flattenEntries(primaryEntry, report.getContext().getContext())) {
              sheet.addDataRow(secondaryEntry, ++ lastRowNum);
            }
          }
        } else {
          for (SearchHit subProcess : subProcesses) {
            SearchHit[] subHit = new SearchHit[1];
            subHit[0] = subProcess;
            //            entry.prepareSubProcesses(subHit);
            for (ElasticEntryDto primaryEntry : primaryEntryList) {
              postProcessor.processHit(primaryEntry, subHit, report.getContext().getContext());
              for (ElasticEntryDto secondaryEntry : postProcessor.flattenEntries(primaryEntry, report.getContext().getContext())) {
                sheet.addDataRow(secondaryEntry, ++ lastRowNum);
              }
            }
          }
        }
      }
      sheet.autoSizeColumns();
      sheet.createFreezePane(sheetConfig.getColFreezePane());
      sheet.createAutoFilter();
    } else {
      List<Map<String, Object>> data = queryDB(configuration.getQuery());
      logger.info(data.size() + " rows found.");
      if ( !data.isEmpty()) {
        List<FieldHeader> headers = extractHeaders(data);
        new DbReport(report, sheetConfig, headers, data);
      }
    }

  }

  @Override
  public PmcReportConfigDto getReportConfig(String name) {
    return pmcReportConfigService.getReportConfigByName(name);
  }

  @Override
  public void sendReport(String reportName) {
    logger.info("Sending Report (or saving to disk)" + reportName + "!");
    PmcReportConfigDto report = pmcReportConfigService.getReportConfigByName(reportName);
    DateFormat df = new SimpleDateFormat(FILE_DATE_FORMAT);
    String dateString = df.format(new Date());
    byte[] reportFile = createReportByProjectType(report.getReportName());

    if (report.getRecipients().contains("@")) {
      EmailDto mail = new EmailDto(report.getSubject(),
                                   "Hello! Find attached the latest version of the tracker!",
                                   report.getRecipientsArray());
      AttachmentDto attachment = new AttachmentDto();
      attachment.setName(report.getReportName() + dateString + ".xlsx");
      attachment.setContentType("application/excel");
      attachment.setContent(reportFile);
      mail.setAttachment(attachment);
      getEmailService().sendEmail(mail);
    } else if (report.getRecipients().contains("/")) {
      File file = new File(configService.getProperty(ConfigService.DOCUMENT_ROOT_DIR) + report.getRecipients());
      try {
        logger.info("~Write Tracker xls to " + report.getRecipients());
        OutputStream os = new FileOutputStream(file);
        os.write(reportFile);
        os.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void sendReports() {
    logger.info("Sending Report emails!");
    List<PmcReportConfigDto> reports = pmcReportConfigService.getReportBySendtime("auto");
    for (PmcReportConfigDto report : reports) {
      sendReport(report.getReportName());
    }
  }

  private MailService getEmailService() {
    if (usedInstance != null)
      return usedInstance;

    if (emailServiceInstance.isAmbiguous()) {
      Iterator<MailService> it = emailServiceInstance.iterator();
      while (it.hasNext()) {
        MailService ms = it.next();
        String name = ms.getClass().getName();
        logger.info("Found emailService: " + name);
        //dont used default impl if ambiguous
        if (name.startsWith("com.bpmasters.pmc.base.service.impl.MailServiceImpl")) {
          continue;
        }
        usedInstance = ms;
        //break;
      }

    } else {
      usedInstance = emailServiceInstance.get();

    }
    logger.info("Using emailService: " + usedInstance.getClass().getName());
    return usedInstance;
  }

  @Override
  public byte[] getBytesFromWorkbook(SXSSFWorkbook workbook) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      workbook.write(out);
      workbook.dispose();
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(out);
    }
  }

  @Override
  public List<Map<String, Object>> queryDB(String sql) {
    JdbcTemplate t = new JdbcTemplate(ds);
    return t.queryForList(sql);
  }

  private List<FieldHeader> extractHeaders(List<Map<String, Object>> data) {
    Map<String, Object> headerRow = data.get(0);
    List<FieldHeader> retVal = new ArrayList<>();
    for (String key : headerRow.keySet()) {

      Object value = headerRow.get(key);
      if (value == null)
        value = searchValue(key, data);
      Class<?> clazz = null;
      //      if (value != null) {
      //        value.getClass();
      //      } else {
      //        clazz = searchClass(key, data);
      //        if (cl)
      //      }

      if (value instanceof String) {
        clazz = String.class;
      } else if (value instanceof Date) {
        clazz = Date.class;
      } else if (value instanceof Number) {
        clazz = Number.class;
      } else if (value == null) {
        clazz = String.class;
      } else {
        logger.info("Unknown type - key: " + key + ", value: " + value + ", class: " + clazz);
        continue;
      }
      retVal.add(new FieldHeader(key, clazz));
    }
    return retVal;
  }

  private Object searchValue(String key, List<Map<String, Object>> data) {
    for (Map<String, Object> row : data) {
      Object val = row.get(key);
      if (val != null)
        return val;
    }
    return null;
  }

  //  private PmcReportConfigDto createTestConfig() {
  //    PmcReportConfigDto rConf = new PmcReportConfigDto();
  //
  //    rConf.setReportName("TestReport");
  //    rConf.setRecipients("all");
  //
  //    ReportColumnDto col1 = new ReportColumnDto(ColumnType.REFOBJECT, "Standort", "idName");
  //    ReportColumnDto col2 = new ReportColumnDto(ColumnType.GROUP, "", null);
  //    ReportColumnDto col3 = new ReportColumnDto(ColumnType.GROUP, "", null);
  //    col2.getColumns().add(col1);
  //    col3.getColumns().add(col2);
  //
  //    ReportColumnDto projectCol = new ReportColumnDto(ColumnType.GROUP, "Projekt Metadaten", null);
  //    ReportColumnDto spacerCol = new ReportColumnDto(ColumnType.GROUP, "", null);
  //    ReportColumnDto projectIdCol = new ReportColumnDto(ColumnType.PROJECT, "Project ID", "projectId");
  //    ReportColumnDto projectTypeCol = new ReportColumnDto(ColumnType.PROJECT, "Project Type", "pmcProjectType.idName");
  //    ReportColumnDto projectPrioCol = new ReportColumnDto(ColumnType.PROJECT, "Priority", "priority");
  //
  //    spacerCol.getColumns().add(projectIdCol);
  //    spacerCol.getColumns().add(projectTypeCol);
  //    spacerCol.getColumns().add(projectPrioCol);
  //
  //    projectCol.getColumns().add(spacerCol);
  //
  //    ReportSheetDto sheet = new ReportSheetDto();
  //    sheet.getColumns().add(col3);
  //    sheet.getColumns().add(projectCol);
  //    sheet.setSheetName("Site Recycling");
  //    rConf.getSheets().add(sheet);
  //
  //    return rConf;
  //  }

}
