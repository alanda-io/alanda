/**
 * 
 */
package io.alanda.rest.document;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.alanda.base.dto.DocumentSimpleDto;

/**
 * @author jlo
 */
public interface FolderRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<DocumentSimpleDto> getFolderContent(@QueryParam("file-mask") String fileMask);

  @GET
  @Path("/download-all")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response downloadAll() throws IOException;

  @POST
  @Consumes("multipart/form-data")
  @Produces(MediaType.APPLICATION_JSON)
  Response upload(MultipartFormDataInput input, @QueryParam("filename") String filename) throws Exception;

  @Path("/{documentGuid}")
  DocumentRestResource getDocument(@PathParam("documentGuid") String documentGuid);

}
