/**
 * 
 */
package io.alanda.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
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

import org.camunda.bpm.engine.rest.dto.VariableValueDto;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.PmcUserDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @author jlo
 */
@Path(PmcTaskRestService.PATH)
public interface PmcTaskRestService {

  public final static String DESERIALIZE_VALUE_QUERY_PARAM = "deserializeValue";

  public static final String PATH = "/app/pmc-task";

  @Operation(summary ="Search tasks", tags = {"PmcTaskRestService"}, description = "")
  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcTaskDto> searchTasks(
      @QueryParam(value = "processInstanceId") String processInstanceId,
      @QueryParam(value = "taskDefinitionKey") String taskDefinitionKey);

  @Operation(summary ="Get task by taskid", tags = {"PmcTaskRestService"}, description = "")
  @GET
  @Path("/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcTaskDto getTask(@PathParam("taskId") String taskId);

  /**
   * @param taskId
   */
  @Operation(summary ="Complete a task", tags = {"PmcTaskRestService"}, description = "")
  @POST
  @Path("/{taskId}/complete")
  void completeTask(@PathParam("taskId") String taskId);

  /**
   * @param taskId
   */
  @Operation(summary ="Unclaim task", tags = {"PmcTaskRestService"}, description = "")
  @POST
  @Path("/{taskId}/unclaim")
  void unclaim(@PathParam("taskId") String taskId);

  /**
   * @param taskId
   * @param assignee
   */
  @Operation(summary ="Assign user to task", tags = {"PmcTaskRestService"}, description = "")
  @POST
  @Path("/{taskId}/assignee")
  void assign(@PathParam("taskId") String taskId, PmcUserDto assignee);

  /**
   * @param serverOptions
   * @return a list of tasks matching the serverOptions
   */
  @Operation(summary ="Get tasks", tags = {"PmcTaskRestService"}, description = "")
  @POST
  @Path("/list")
  PagedResultDto getTasks(Map<String, Object> serverOptions);

  @Operation(summary ="Update all tasks", tags = {"PmcTaskRestService"}, description = "")
  @POST
  @Path("/update-all")
  void updateAllTasks(
      @QueryParam(value = "firstResult") Integer firstResult,
      @QueryParam(value = "maxResults") Integer maxResults,
      @QueryParam(value = "inverse") @DefaultValue(value = "false") boolean inverse);

  /**
   * @param taskId
   * @return
   */
  @Operation(summary ="Get candidates from task", tags = {"PmcTaskRestService"}, description = "")
  @GET
  @Path("/{taskId}/candidates")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcUserDto> candidates(@PathParam("taskId") String taskId);

  @Operation(summary ="Get variable from task", tags = {"PmcTaskRestService"}, description = "")
  @GET
  @Path("/{taskId}/variables/{variableName}")
  @Produces(MediaType.APPLICATION_JSON)
  VariableValueDto getVariable(
      @PathParam("taskId") String taskId,
      @PathParam("variableName") String variableName,
      @QueryParam(DESERIALIZE_VALUE_QUERY_PARAM) @DefaultValue("true") boolean deserializeValue);

  @Operation(summary ="Put variable for task", tags = {"PmcTaskRestService"}, description = "")
  @PUT
  @Path("/{taskId}/variables/{variableName}")
  @Consumes(MediaType.APPLICATION_JSON)
  void putVariable(@PathParam("taskId") String taskId, @PathParam("variableName") String variableName, VariableValueDto variable);

  @Operation(summary ="Update due date of task", tags = {"PmcTaskRestService"}, description = "")
  @PUT
  @Path("/{taskId}/dueDate/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateDueDateOfTask(@PathParam("taskId") String taskId, String dueDate);

  @Operation(summary ="Snooze task", tags = {"PmcTaskRestService"}, description = "")
  @PUT
  @Path("/{taskId}/snooze/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response snoozeTask(@PathParam("taskId") String taskId, int days);

}
