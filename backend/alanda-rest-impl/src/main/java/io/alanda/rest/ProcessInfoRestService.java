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
@Path(ProcessInfoRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface ProcessInfoRestService {

  public static final String PATH = "/app/process";
  
//  @GET
//  @Path("/info/{processInstanceId}/executions")
//  @Produces(MediaType.APPLICATION_JSON)
//  public abstract List<ProcessDto> executions(@PathParam("processInstanceId") String processInstanceId);

  @GET
  @Path("/info/{processInstanceId}/{processPackageKey}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract List<ProcessDto> processData(@PathParam("processInstanceId") String processInstanceId, @PathParam("processPackageKey") String processPackageKey);

//  @POST
//  @Path("/reqProcess/{processInstanceId}/start/{index}")
//  Response startReqProcess(@PathParam("processInstanceId")String processInstanceId,  @PathParam("index") int index);
//  
//  
//  @POST
//  @Path("/reqProcess/{processInstanceId}/{executionId}/start/{index}")
//  Response startReqProcess(@PathParam("processInstanceId")String processInstanceId,@PathParam("executionId")  String executionId, @PathParam("index") int index);
//  
//  @GET
//  @Path("/reqProcess/{processInstanceId}/info")
//  @Produces(MediaType.APPLICATION_JSON)
//  List<ReqProcessDto> reqProcessData(@PathParam("processInstanceId")String processInstanceId);
//
//  @POST
//  @Path("/reqProcess/{processInstanceId}/save")
//  @Consumes(MediaType.APPLICATION_JSON)
//  Response saveReqProcess(@PathParam("processInstanceId")String processInstanceId, ReqProcessDto params);
//
//  @POST
//  @Path("/reqProcess/{processInstanceId}/remove/{index}")
//  Response removeReqProcess(@PathParam("processInstanceId")String processInstanceId, @PathParam("index") int index);
  
}
