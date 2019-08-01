package io.alanda.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "PmcFinderRestService")
@Path(PmcFinderRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface PmcFinderRestService {

  String PATH = "/app/finder";

  /**
   * Returns process instances where process key, process name or business key matches a search term.
   *
   * @param searchTerm
   * @param onlyActive
   * @return
   */
  @GET
  @Path("/search")
  List<Map<String, Object>> searchProcessInstances(
      @QueryParam("searchterm") String searchTerm,
      @DefaultValue("true") @QueryParam("onlyActive") Boolean onlyActive);

  /**
   * Returns activities for a given process instance id.
   *
   * @param pid
   * @param extendedView
   * @return
   */
  @GET
  @Path("/pio/activities")
  List<Map<String, Object>> getProcessActivities(
      @QueryParam("pid") String pid,
      @DefaultValue("false") @QueryParam("extendedView") Boolean extendedView);
}
