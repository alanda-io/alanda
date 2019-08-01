/**
 * 
 */
package io.alanda.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.alanda.base.dto.SimpleMilestoneDto;

/**
 * @author jlo
 */
@Produces(MediaType.APPLICATION_JSON)
public interface PmcProjectMilestoneRestResource {

  @PUT
  @Path("/fc")
  SimpleMilestoneDto fc(RestMilestoneVo msData);

  @PUT
  @Path("/act")
  SimpleMilestoneDto act(RestMilestoneVo msData);

  @GET
  SimpleMilestoneDto getMilestone();

}
