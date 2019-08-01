package io.alanda.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuConfigDto;
import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.DocumentSimpleDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.TreeConfigDto;
import io.alanda.base.service.DocumentService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.type.ProcessVariables;

import io.alanda.rest.AttachmentRestService;

public class AttachmentRestServiceImpl implements AttachmentRestService {

  private static final Logger logger = LoggerFactory.getLogger(AttachmentRestServiceImpl.class);

  @Inject
  private DocumentService documentService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private PmcProjectService pmcProjectService;

  Collection<String> varNames = Arrays.asList(
    ProcessVariables.REFOBJECTTYPE,
    ProcessVariables.REFOBJECTID,
    ProcessVariables.PMC_PROJECT_GUID,
    ProcessVariables.REFOBJECTIDNAME,
    ProcessVariables.PROJECTID);

  @Override
  public DocuFolderDto getTree(
      String procDefKey,
      String processInstanceId,
      String mappingName,
      Long mappingId,
      Long pmcProjectGuid,
      Boolean fileCount) {
    DocuQueryDto q = createQuery(procDefKey, processInstanceId, mappingName, mappingId, null, pmcProjectGuid);

    DirectoryInfoDto dir = documentService.getTree(q);
    return dir.getConfig().getSourceFolder();
  }

  /**
   * @param procDefKey
   * @param processInstanceId
   * @param mappingName
   * @param mappingId
   * @param folderId
   * @param pmcProjectGuid
   * @return
   */
  private DocuQueryDto createQuery(
      String procDefKey,
      String processInstanceId,
      String mappingName,
      Long mappingId,
      Long folderId,
      Long pmcProjectGuid) {
    DocuQueryDto q;
    if (mappingId != null) {
      q = DocuQueryDto.forRefObject(null, mappingName, mappingId, true);
    } else if (pmcProjectGuid != null) {
      //components = new ArrayList<>();
      PmcProjectDto dto = this.pmcProjectService.getProjectByGuid(pmcProjectGuid);
      q = DocuQueryDto.forPmcProject(dto, false).withMappingName(mappingName);

    } else {
      Map<String, Object> varMap = runtimeService.getVariables(processInstanceId, varNames);

      String refObjectType = (String) varMap.get(ProcessVariables.REFOBJECTTYPE);
      Long refObjectId = (Long) varMap.get(ProcessVariables.REFOBJECTID);
      Long projectId = (Long) varMap.get(ProcessVariables.PROJECTID);
      pmcProjectGuid = (Long) varMap.get(ProcessVariables.PMC_PROJECT_GUID);
      Long projectTypeId = null;
      String businessKey = (String) varMap.get(ProcessVariables.REFOBJECTIDNAME);

      if (mappingName == null) {

        q = DocuQueryDto.getForProcess(businessKey, pmcProjectGuid, procDefKey, refObjectType, refObjectId, projectId, projectTypeId, true);
      } else {
        q = DocuQueryDto.getForComponent(businessKey, pmcProjectGuid, refObjectType, refObjectId, projectId, mappingName, true);
      }
    }
    return q.withTargetFolderId(folderId);
  }

  @Override
  public Response downloadAll(
      final String procDefKey,
      final String processInstanceId,
      final Long folderId,
      final Long pmcProjectGuid,
      final String mappingName,
      final Long mappingId) throws IOException {
    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          DocuQueryDto q = createQuery(procDefKey, processInstanceId, mappingName, mappingId, folderId, pmcProjectGuid);
          documentService.getAll(q, output);
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    return Response
      .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
      .header("content-disposition", "attachment; filename=\"" + procDefKey + ".zip\"")
      //.header("Content-Length", ) need to create temp file?
      .build();
  }

  @Override
  public List<DocumentSimpleDto> getFolderContent(
      String procDefKey,
      String processInstanceId,
      Long folderId,
      Long pmcProjectGuid,
      String mappingName,
      Long mappingId) {
    DocuQueryDto q = createQuery(procDefKey, processInstanceId, mappingName, mappingId, folderId, pmcProjectGuid);
    return documentService.getFolderContent(q);
  }

  @Override
  public Response upload(
      String procDefKey,
      String processInstanceId,
      Long folderId,
      Long pmcProjectGuid,
      String mappingName,
      Long mappingId,
      MultipartFormDataInput input) throws Exception {

    DocuQueryDto q = createQuery(procDefKey, processInstanceId, mappingName, mappingId, folderId, pmcProjectGuid);

    List<DocumentSimpleDto> uploadPaths = new LinkedList<>();

    Map<String, List<InputPart>> formData = input.getFormDataMap();
    if (formData.containsKey("file")) {
      List<InputPart> inputParts = formData.get("file");
      for (InputPart inputPart : inputParts) {
        MultivaluedMap<String, String> header = inputPart.getHeaders();
        String fileName = getFileName(header);
        MediaType mt = getMediaTypeByFilename(fileName);
        if (mt == null) {
          mt = inputPart.getMediaType();
        }
        InputStream inputStream = inputPart.getBody(InputStream.class, null);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        DocumentSimpleDto fi = new DocumentSimpleDto(fileName, mt.getType() + "/" + mt.getSubtype(), (long) bytes.length);
        DocumentSimpleDto doc = documentService.store(q, bytes, fi);
        logger.info("Upload von " + doc.getName() + ", auf: " + doc.getPath());
        uploadPaths.add(doc);
      }
    } else {
      throw new InvalidRequestException(Status.BAD_REQUEST, "file not found in form");
    }
    return Response.ok(uploadPaths).status(200).build();
  }

  @Override
  public Response download(String fileGuid, boolean inline) throws IOException {

    if (fileGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter fileguid was not provided");
    }

    DocumentSimpleDto doc = documentService.get(fileGuid);
    final InputStream is = doc.getIntputStream();

    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          output.write(IOUtils.toByteArray(is));
          is.close();
          output.close();
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    if ( !inline) {
      return Response
        .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
        .header("content-disposition", "attachment; filename=\"" + doc.getName() + "\"")
        .header("Content-Length", is.available())
        .build();
    } else {
      return Response.ok(stream, doc.getMediaType()).header("Content-Length", is.available()).build();
    }
  }

  @Override
  public Response delete(String fileGuid) throws IOException {

    if (fileGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter fileguid was not provided");
    }
    documentService.delete(fileGuid);

    return Response.status(200).build();
  }

  @Override
  public Response rename(String fileGuid, String newFileName) throws IOException {
    if (fileGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter fileGuid was not provided");
    }

    if (newFileName == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter newfilename was not provided");
    }

    documentService.rename(fileGuid, newFileName);
    return Response.status(200).build();
  }

  private String getFileName(MultivaluedMap<String, String> header) {

    String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

    for (String filename : contentDisposition) {
      if ((filename.trim().startsWith("filename"))) {

        String[] name = filename.split("=");

        String finalFileName = name[1].trim().replaceAll("\"", "");
        return finalFileName;
      }
    }
    throw new InvalidRequestException(Status.BAD_REQUEST, "filename header not found");
  }

  @Override
  public List<TreeConfigDto> getTreeConfig(
      String procDefKey,
      String processInstanceId,
      List<String> componentsArgument,
      Long pmcProjectGuid) {

    List<DocuQueryDto> queries = null;
    if (pmcProjectGuid != null) {
      PmcProjectDto dto = this.pmcProjectService
        .getProjectByGuid(pmcProjectGuid, io.alanda.base.service.PmcProjectService.Mode.DOCU);
      List<DocuConfigDto> configs = dto.getPmcProjectType().getDocuConfigs();
      queries = DocuQueryDto.listForPmcProject(dto, true);
    } else {

      Map<String, Object> varMap = runtimeService.getVariables(processInstanceId, varNames);

      String refObjectType = (String) varMap.get(ProcessVariables.REFOBJECTTYPE);
      Long refObjectId = (Long) varMap.get(ProcessVariables.REFOBJECTID);
      Long projectId = (Long) varMap.get(ProcessVariables.PROJECTID);
      String businessKey = (String) varMap.get(ProcessVariables.REFOBJECTIDNAME);

      pmcProjectGuid = (Long) varMap.get(ProcessVariables.PMC_PROJECT_GUID);
      queries = new ArrayList<>();
      for (String comp : componentsArgument) {
        queries.add(DocuQueryDto.getForComponent(businessKey, pmcProjectGuid, refObjectType, refObjectId, projectId, comp, true));
      }
      if (queries.isEmpty()) {
        return getLegacyTreeConfig(procDefKey, processInstanceId, refObjectType, refObjectId, projectId, businessKey, pmcProjectGuid);
      }

    }
    List<TreeConfigDto> retVal = new ArrayList<>();

    for (DocuQueryDto part : queries) {

      DirectoryInfoDto did = this.documentService.getTree(part);
      retVal.add(new TreeConfigDto(did.getConfig().getDisplayName(), part.mappingName, null, did.getConfig().getSourceFolder()));
    }

    return retVal;
  }

  /**
   * @param procDefKey
   * @param processInstanceId
   * @param refObjectType
   * @param refObjectId
   * @param projectId
   * @param businessKey
   * @param pmcProjectGuid
   * @return
   */
  private List<TreeConfigDto> getLegacyTreeConfig(
      String procDefKey,
      String processInstanceId,
      String refObjectType,
      Long refObjectId,
      Long projectId,
      String businessKey,
      Long pmcProjectGuid) {
    List<TreeConfigDto> retVal = new ArrayList<>();
    Long projectTypeId = null;
    if (projectId != null) {
      logger.warn("ProjectId not supported!!!");
    }
    DirectoryInfoDto did = this.documentService
      .getTree(
      DocuQueryDto.getForProcess(businessKey, pmcProjectGuid, procDefKey, refObjectType, refObjectId, projectId, projectTypeId, true));
    retVal
      .add(new TreeConfigDto(did.getConfig().getDisplayName(), did.getConfig().getMappingName(), null, did.getConfig().getSourceFolder()));

    return retVal;
  }

  private MediaType getMediaTypeByFilename(String filename) {
    filename = filename.toLowerCase();
    if (filename.endsWith(".pdf")) {
      return new MediaType("application", "pdf");
    }
    return null;
  }

}
