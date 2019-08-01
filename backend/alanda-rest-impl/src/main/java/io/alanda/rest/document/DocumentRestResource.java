/**
 * 
 */
package io.alanda.rest.document;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.dto.DocumentHistoryDto;

/**
 * @author jlo
 */
public interface DocumentRestResource {

  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response download(@QueryParam("inline") @DefaultValue("false") boolean inline) throws IOException;

  @DELETE
  public Response delete() throws IOException;

  @PUT
  @Path("/rename")
  public Response rename(@QueryParam("newfilename") String newFileName) throws IOException;

  @GET
  @Path("/history")
  @Produces(MediaType.APPLICATION_JSON)
  List<DocumentHistoryDto> showHistory();

  @GET
  @Path("/history/{versionString}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  Response downloadFromHistory(
      @PathParam("versionString") String versionString,
      @QueryParam("inline") @DefaultValue("false") boolean inline) throws IOException;

}
