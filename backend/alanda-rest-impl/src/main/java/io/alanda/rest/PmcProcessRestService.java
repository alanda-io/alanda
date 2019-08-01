/**
 * 
 */
package io.alanda.rest;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDiagramDto;
import org.camunda.bpm.engine.rest.dto.runtime.ActivityInstanceDto;

import io.alanda.base.dto.ProcessDto;

import io.alanda.rest.util.HistoricProcessInstanceDto;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author jlo
 */
@Tag(name = "PmcProcessRestService")
@Path(PmcProcessRestService.PATH)
public interface PmcProcessRestService {

  public static final String PATH = "/app/pmc-process";

  @GET
  @Path("/package/{processPackageKey}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract List<ProcessDto> processesForPackage(@PathParam("processPackageKey") String processPackageKey);

  @GET
  @Path("/project/{pmcProjectGuid}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract List<ProcessDto> processesForPackage(@PathParam("pmcProjectGuid") Long pmcProjectGuid);

  @GET
  @Path("/process/{pmcProjectProcessId}")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract ProcessDto processForProjectProcess(@PathParam("pmcProjectProcessId") Long pmcProjectProcessId);

  @GET
  @Path("/{processInstanceId}/variables/{variableName}")
  VariableValueDto getVariable(
      @PathParam("processInstanceId") String processInstanceId,
      @PathParam("variableName") String variableName,
      @QueryParam(PmcTaskRestService.DESERIALIZE_VALUE_QUERY_PARAM) @DefaultValue("true") boolean deserializeValue);

  @GET
  @Path("/{processInstanceId}/status")
  @Produces(MediaType.APPLICATION_JSON)
  HistoricProcessInstanceDto processStatus(@PathParam("processInstanceId") String processInstanceId);

  @GET
  @Path("/{processInstanceId}/activities")
  @Produces(MediaType.APPLICATION_JSON)
  ActivityInstanceDto activities(@PathParam("processInstanceId") String processInstanceId);

  @GET
  @Path("/{processInstanceId}/definition/xml")
  @Produces(MediaType.APPLICATION_JSON)
  ProcessDefinitionDiagramDto definitionXml(@PathParam("processInstanceId") String processInstanceId);

  @GET
  @Path("/check-mail")
  public Response checkMail();
}
