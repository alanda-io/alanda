package io.alanda.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/app/pmc-metric")
@Produces(MediaType.APPLICATION_JSON)
public interface PmcMetricRestService {

  @GET
  @Path("/create-ap")
  @Produces(MediaType.APPLICATION_JSON)
  List<Map<String, Object>> getCreateAp();
}
