/**
 * 
 */
package io.alanda.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author jlo
 */
public interface PmcProjectRestResource {

  @Path("/ms/{milestoneIdName}")
  public PmcProjectMilestoneRestResource getPmcProjectMilestoneRestResource(@PathParam("milestoneIdName") String milestoneIdName);

  @Path("/history")
  public PmcProjectHistoryRestResource getPmcProjectHistoryRestResource();

  @Path("/comments")
  public PmcProjectCommentsRestResource getPmcProjectCommentsRestResource();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public RestProjectVo getRestProject();

}
