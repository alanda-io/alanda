/**
 * 
 */
package io.alanda.rest;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.service.ConfigService;

/**
 * @author developer
 */
@Path("/requestinfo")
@Produces(MediaType.APPLICATION_JSON)
public class RequestInfoRestService {

  @Inject
  ConfigService configService;

  @GET
  public Map<String, List<String>> getInfo(@Context HttpHeaders headers) {
    return headers.getRequestHeaders();
  }

  @GET
  @Path("/testmode")
  public Response getTestMode() {
    return Response.ok().entity(configService.getBooleanProperty(ConfigService.TEST_MODE)).build();
  }

}
