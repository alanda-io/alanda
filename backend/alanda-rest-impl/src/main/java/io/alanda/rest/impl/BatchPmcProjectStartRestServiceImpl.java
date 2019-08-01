package io.alanda.rest.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.batch.PmcBatchCreationService;
import io.alanda.base.service.ConfigService;

import io.alanda.rest.BatchPmcProjectStartRestService;

public class BatchPmcProjectStartRestServiceImpl implements BatchPmcProjectStartRestService {

  private static final Logger logger = LoggerFactory.getLogger(BatchPmcProjectStartRestServiceImpl.class);

  @Inject
  private PmcBatchCreationService batchCreationService;

  @Inject
  private ConfigService configService;

  @Override
  public Map<String, Object> upload(MultipartFormDataInput input, String projectType) throws Exception {

    String pid = null;
    Map<String, List<InputPart>> formData = input.getFormDataMap();
    if (formData.containsKey("file")) {
      List<InputPart> inputParts = formData.get("file");
      for (InputPart inputPart : inputParts) {
        InputStream inputStream = inputPart.getBody(InputStream.class, null);
        pid = batchCreationService.startBatchProjects(projectType, inputStream);
        inputStream.close();
      }
    } else {
      throw new BadRequestException("file not found in form");
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("pid", pid);
    return result;
  }

  @Override
  public Response download(String pid) throws IOException {

    if (pid == null || pid.equals(""))
      throw new BadRequestException("No PID provided!");

    final FileInputStream fileInput = new FileInputStream(batchCreationService.getFileByPid(pid));

    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          output.write(IOUtils.toByteArray(fileInput));
          fileInput.close();
          output.close();
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    return Response
      .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
      .header("content-disposition", "attachment; filename=\"result.xlsx\"")
      .header("Content-Length", fileInput.available())
      .build();
  }

  @Override
  public Response template(String projectType) throws IOException {

    final FileInputStream fileInput = new FileInputStream(
      configService.getProperty(ConfigService.DOCUMENT_ROOT_DIR) +
        "/documents/os_doc_camunda/os_doc_templates/create-batch-project-" +
        projectType +
        ".xlsx");

    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          output.write(IOUtils.toByteArray(fileInput));
          fileInput.close();
          output.close();
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    return Response
      .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
      .header("content-disposition", "attachment; filename=\"template.xlsx\"")
      .header("Content-Length", fileInput.available())
      .build();
  }

  @Override
  public Map<String, Object> status(String pid) {
    return batchCreationService.batchStartStatus(pid);
  }

}
