package io.alanda.rest.document;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Providing Document Service Methods for refObehcts, projects and processes
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

}
