/**
 * 
 */
package io.alanda.rest.api;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author jlo
 */
@Produces(MediaType.APPLICATION_JSON)
public interface PmcProjectRestApi {

  public final String PATH = "/project";

  @Path("/{projectId}")
  PmcProjectRestResource getPmcProject(@PathParam("projectId") String projectId);

}
