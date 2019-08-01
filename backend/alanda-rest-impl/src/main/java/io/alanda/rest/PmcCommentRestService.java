package io.alanda.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.dto.PmcCommentDto;

import io.alanda.rest.api.PmcCommentRestResult;

@Path(PmcCommentRestService.PATH)
public interface PmcCommentRestService {

  public static final String PATH = "/app/comment";

  @GET
  @Path("/forProcessInstance/{processInstanceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract PmcCommentRestResult getAllForProcessInstanceId(@PathParam("processInstanceId") String processInstanceId);

  @GET
  @Path("/forProcessInstanceAndRefObjectId/{processInstanceId}/{refObjectId")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract PmcCommentRestResult getAllForProcessInstanceIdAndRefObjectId(
      @PathParam("processInstanceId") String processInstanceId,
      @PathParam("refObjectId") Long refObjectId);

  @POST
  @Path("/post")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public abstract Response insertComment(PmcCommentDto pmcCommentDto);

}
