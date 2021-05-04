package io.alanda.rest.document;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import javax.ws.rs.core.Response;

/**
 * Providing Document Service Methods for refObjects, projects and processes
 * 
 * @author jlo
 */
@Path(DocumentRestService.PATH)
public interface DocumentRestService {

  public static final String PATH = "/app/document";

  @Path("/refObject/{refObjectType}/{stringObjectId}")
  RefObjectDocumentRestResource getRefObjectResource(
      @PathParam("refObjectType") String refObjectType,
      @PathParam("stringObjectId") String stringObjectId);

  @DELETE
  @Path("/{documentGuid}")
  Response deleteDocument(@PathParam("documentGuid") String documentGuid) throws IOException;
}
