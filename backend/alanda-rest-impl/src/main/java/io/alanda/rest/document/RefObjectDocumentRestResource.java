/**
 * 
 */
package io.alanda.rest.document;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.HttpRequest;

import io.alanda.base.dto.DocuFolderDto;

/**
 * @author jlo
 */
public interface RefObjectDocumentRestResource {

  /**
   * @param fileCount addFileCount
   * @return the full folder hierachy tree
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  DocuFolderDto getTree(@QueryParam("fileCount") @DefaultValue("false") boolean fileCount, @QueryParam("mappings") String mappings);

  @Path("/guid/{folderId}")
  FolderRestResource getFolder(@PathParam("folderId") Long folderId, @QueryParam("mappings") String mappings);
  
  @Path("/by-name/{folderName}")
  FolderRestResource getFolderByName(@PathParam("folderName") String folderName, @QueryParam("mappings") String mappings);

  
  default String getPermissionsForRequest(HttpRequest request) {
    if ("GET".equals(request.getHttpMethod())) return "read";
    if ("DELETE".equals(request.getHttpMethod())) return "delete";
    return "write";
  }
  
}
