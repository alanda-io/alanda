package io.alanda.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ElasticRestService")
@Path(ElasticRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface ElasticRestService {

  public static final String PATH = "/app/elastic";

  @GET
  @Path("/createreport/{projecttype}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response createReport(@PathParam("projecttype") String projectType);

  @GET
  @Path("/processes/project/{projectid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findProcessByProjectId(@PathParam("projectid") String projectId);

  @GET
  @Path("/processes/projecttype/{projecttype}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findByProjectType(@PathParam("projecttype") String projectType);

  @GET
  @Path("/sync")
  Response syncData(@QueryParam("ttlInMinutes") Integer ttlInMinutes);

  @GET
  @Path("/update/processes/{processinstanceid}/{refobjecttype}/{refobjectid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateRefObject(
      @PathParam("processinstanceid") String processInstanceId,
      @PathParam("refobjecttype") String refObjectType,
      @PathParam("refobjectid") Long refObjectId);

  @GET
  @Path("reportconfig/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getReportConfig(@PathParam("name") String name);

}
