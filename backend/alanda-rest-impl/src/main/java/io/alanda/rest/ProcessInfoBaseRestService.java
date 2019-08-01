/**
 * 
 */
package io.alanda.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.alanda.base.dto.ProcessDto;

/**
 * @author jens
 */
@Path(ProcessInfoBaseRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface ProcessInfoBaseRestService {

  public static final String PATH = "/app/process";
  
  @GET
  @Path("/info/{processInstanceId}/{processPackageKey}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<ProcessDto> processData(@PathParam("processInstanceId") String processInstanceId, @PathParam("processPackageKey") String processPackageKey);

  @GET
  @Path("/info/{processInstanceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public ProcessDto processData(@PathParam("processInstanceId") String processInstanceId);


}
