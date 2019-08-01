/**
 * 
 */
package io.alanda.rest;

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

import io.alanda.base.dto.PmcBlogDto;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author jlo
 */
@Tag(name = "PmcBlogRestService")
@Path(PmcBlogRestService.PATH)
public interface PmcBlogRestService {

  public static final String PATH = "/app/pmc-blog";

  @GET
  @Path("/all/{status}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract List<PmcBlogDto> getPosts(
      @PathParam("status") String status,
      @QueryParam("first") @DefaultValue("0") int first,
      @QueryParam("count") @DefaultValue("20") int count);

  @GET
  @Path("/blog/{blogId}/{status}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract PmcBlogDto getSingleBlogByBlogIdAndStatus(
      @PathParam("blogId") Long pmcBlogPostId,
      @PathParam("status") @DefaultValue("unpublished") String status);

  @GET
  @Path("/blog/{guid}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract PmcBlogDto getSingleBlogByGuid(@PathParam("guid") Long pmcBlogGuid);

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public abstract Response createBlogPost(PmcBlogDto pmcBlogDto);

  @PUT
  @Path("/blog/{blogId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public abstract PmcBlogDto updateBlogPost(PmcBlogDto pmcBlogDto);

  @DELETE
  @Path("/blog/{blogId}")
  public abstract Response deleteBlogPost(@PathParam("blogId") Long pmcBlogPostId);

  @POST
  @Path("/start")
  @Produces(MediaType.APPLICATION_JSON)
  Response startBlogPost();

  @POST
  @Path("/modify/{blogId}")
  @Produces(MediaType.APPLICATION_JSON)
  Response modifyPublishedBLogPost(@PathParam("blogId") Long pmcBlogPostId);

}
