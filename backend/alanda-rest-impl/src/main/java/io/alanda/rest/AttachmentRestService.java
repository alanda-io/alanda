package io.alanda.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocumentSimpleDto;
import io.alanda.base.dto.TreeConfigDto;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Providing service methods for the attachment section
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
@Tag(name = "PmcAttachmentRestService")
@Path(AttachmentRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface AttachmentRestService {

  public static final String PATH = "/app/attachment";

  /**
   * @param refObjectType
   * @param procDefKey
   * @param projectId
   * @return the full folder hierachy tree
   */
  @GET
  @Path("{procDefKey}/{processInstanceId}/tree")
  @Produces(MediaType.APPLICATION_JSON)
  DocuFolderDto getTree(
      @PathParam("procDefKey") String procDefKey,
      @PathParam("processInstanceId") String processInstanceId,
      @QueryParam("mappingName") String mappingName,
      @QueryParam("mappingId") Long mappingId,
      @QueryParam("pmcProjectGuid") Long pmcProjectGuid,
      @QueryParam("fileCount") Boolean fileCount);

  /**
   * @param procDefKey
   * @param processInstanceId
   * @param components
   * @return
   */
  @GET
  @Path("{procDefKey}/{processInstanceId}/tree-config")
  @Produces(MediaType.APPLICATION_JSON)
  List<TreeConfigDto> getTreeConfig(
      @PathParam("procDefKey") String procDefKey,
      @PathParam("processInstanceId") String processInstanceId,
      @QueryParam("components") List<String> components,
      @QueryParam("pmcProjectGuid") Long pmcProjectGuid);

  @GET
  @Path("/{procDefKey}/{processInstanceId}/{id}/download-all")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response downloadAll(
      @PathParam("procDefKey") String procDefKey,
      @PathParam("processInstanceId") String processInstanceId,
      @PathParam("id") Long folderId,
      @QueryParam("pmcProjectGuid") Long pmcProjectGuid,
      @QueryParam("mappingName") String mappingName,
      @QueryParam("mappingId") Long mappingId)
      throws IOException;

  /**
   * @param procDefKey
   * @param processInstanceId
   * @param id
   * @return the files contained in the folder
   */
  @GET
  @Path("{procDefKey}/{processInstanceId}/folder/{id}/content")
  List<DocumentSimpleDto> getFolderContent(
      @PathParam("procDefKey") String procDefKey,
      @PathParam("processInstanceId") String processInstanceId,
      @PathParam("id") Long id,
      @QueryParam("pmcProjectGuid") Long pmcProjectGuid,
      @QueryParam("mappingName") String mappingName,
      @QueryParam("mappingId") Long mappingId);

  @POST
  @Path("{procDefKey}/{processInstanceId}/folder/{id}/upload")
  @Consumes("multipart/form-data")
  Response upload(
      @PathParam("procDefKey") String procDefKey,
      @PathParam("processInstanceId") String processInstanceId,
      @PathParam("id") Long id,
      @QueryParam("pmcProjectGuid") Long pmcProjectGuid,
      @QueryParam("mappingName") String mappingName,
      @QueryParam("mappingId") Long mappingId,
      MultipartFormDataInput input)
      throws Exception;

  @GET
  @Path("{procDefKey}/{processInstanceId}/folder/{id}/download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response download(@QueryParam("fileguid") String fileUuid, @QueryParam("inline") @DefaultValue("false") boolean inline)
      throws IOException;

  @DELETE
  @Path("{procDefKey}/{processInstanceId}/folder/{id}/delete")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response delete(@QueryParam("fileguid") String fileUuid) throws IOException;

  @PUT
  @Path("{procDefKey}/{processInstanceId}/folder/{id}/rename")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response rename(@QueryParam("fileguid") String fileUuid, @QueryParam("newfilename") String newFileName) throws IOException;

  // methods to start the DMS importer

  //  @GET
  //  @Path("importBbmToDms")
  //  public Response importBbmToDms(@QueryParam("replace") String replace, @QueryParam("with") String with, @QueryParam("dry") String dry);
  //
  //  @GET
  //  @Path("importEventToDms")
  //  public Response importEventToDms(@QueryParam("replace") String replace, @QueryParam("with") String with, @QueryParam("dry") String dry);
  //
  //  @GET
  //  @Path("importBanfToDms")
  //  public Response importBanfToDms(@QueryParam("replace") String replace, @QueryParam("with") String with, @QueryParam("dry") String dry);

}
