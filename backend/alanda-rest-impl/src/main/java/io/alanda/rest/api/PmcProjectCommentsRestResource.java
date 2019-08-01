/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author jlo
 */
@Produces(MediaType.APPLICATION_JSON)
public interface PmcProjectCommentsRestResource {

  @POST
  public Response saveComment(RestCommentVo comment);

  @GET
  public List<RestCommentVo> list();

}
