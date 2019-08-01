package io.alanda.rest.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.elasticsearch.search.SearchHit;

import io.alanda.base.dto.PmcReportConfigDto;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.ReportService;

import io.alanda.rest.ElasticRestService;

@ApplicationScoped
public class ElasticRestServiceImpl implements ElasticRestService {
  
  @Inject
  private ElasticService elasticService;
  
  @Inject
  private ReportService reportService;
  
  
  @Override
  public Response createReport(String projectType) {
    final byte[] report = reportService.createReportByProjectType(projectType);

    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          output.write(report);
          output.close();
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    DateFormat df = new SimpleDateFormat(ReportService.FILE_DATE_FORMAT);
    
    return Response
      .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
      .header("content-disposition", "attachment; filename=\""+ projectType + df.format(new Date()) + ".xlsx\"")
      .header("Content-Length", report.length)
      .build();
//      reportService.sendReports();
//      return Response.ok().build();
  }
  
  @Override
  public Response getReportConfig(String name) {
    PmcReportConfigDto config = reportService.getReportConfig(name);
    return Response.ok(config).build();
  }

  @Override
  public Response findProcessByProjectId(String projectId) {
    SearchHit[] hits = elasticService.findProcessesForProject(projectId);
    
    return Response.ok(hits).build();
  }
  
  @Override
  public Response findByProjectType(String projectType) {
    SearchHit[] hits = elasticService.findByProjectType(projectType);
    
    return Response.ok(hits).build();
  }
  
  @Override
  public Response syncData(Integer ttlInMinutes) {

    elasticService.synchData(ttlInMinutes);

    return Response.ok().build();

  }

  @Override
  public Response updateRefObject(String processInstanceId, String refObjectType, Long refObjectId) {
    
    elasticService.updateRefObject(processInstanceId, refObjectType, refObjectId);
    
    return Response.ok().build();
    
  }
  
  
}
