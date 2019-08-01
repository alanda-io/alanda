/**
 * 
 */
package io.alanda.rest.api;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author jlo
 */
@Path(PmcRestApi.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface PmcRestApi {

  public static final String PATH = "/app/api/v1";

  @Path(PmcProjectRestApi.PATH)
  PmcProjectRestApi getPmcProjectRestApi();

  @GET
  Map<String, Object> getSystemInfo();
}
