package io.alanda.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.dto.PmcPropertyDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "PmcPropertyRestService")
@Path("/app/pmc-property")
@Produces(MediaType.APPLICATION_JSON)
public interface PmcPropertyRestService {

  @GET
  @Path("/get")
  @Produces(MediaType.APPLICATION_JSON)
  Map<String, Object> get(
      @QueryParam("entity-id") Long entityId,
      @QueryParam("entity-type") String entityType,
      @QueryParam("pmc-project-guid") Long pmcProjectGuid,
      @QueryParam("key") String key);

  @POST
  @Path("/set")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  Response set(PmcPropertyDto pmcPropertyDto);

  @GET
  @Path("/get-with-prefix/{projectGuid}/{key}")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcPropertyDto> getPropertyListWithPrefix(
      @PathParam("projectGuid") Long pmcProjectGuid,
      @PathParam("key") String key,
      @QueryParam("delim") @DefaultValue(".") String delim);

  @GET
  @Path("/propertiesmap/{projectGuid}")
  @Produces(MediaType.APPLICATION_JSON)
  Map<String, Object> getPropertiesMap(@PathParam("projectGuid") Long pmcProjectGuid);

  @DELETE
  @Path("/delete")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteProperty(
      @QueryParam("entity-id") Long entityId,
      @QueryParam("entity-type") String entityType,
      @QueryParam("pmc-project-guid") Long pmcProjectGuid,
      @QueryParam("key") String key);

}
