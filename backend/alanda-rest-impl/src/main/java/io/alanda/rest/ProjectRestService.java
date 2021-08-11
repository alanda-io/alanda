package io.alanda.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcProjectCardDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectPhaseDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.swagger.v3.oas.annotations.Operation;

@Path("/app/project")
@Produces(MediaType.APPLICATION_JSON)
public interface ProjectRestService {

  @Operation(summary ="Get project by guid", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/guid/{guid}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectDto getByGuid(@PathParam("guid") Long guid, @QueryParam("tree") @DefaultValue("false") Boolean tree) throws IOException;

  @Operation(summary ="Get project by projectId", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/{projectid}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectDto getByProjectId(@PathParam("projectid") String projectId) throws IOException;

  @Operation(summary ="Update by projectId", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/{projectId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PmcProjectDto updateByProjectId(@PathParam("projectId") String projectId, PmcProjectDto pmcProject) throws IOException;

  @Operation(summary ="Update gu status by projectId", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/{projectId}/gustatus")
  PmcProjectDto updateGuStatusByProjectId(@PathParam("projectId") String projectId, String guStatus) throws IOException;

  @Operation(summary ="Update project relations", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/{projectId}/update-relations")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectDto updateProjectRelations(
      @PathParam("projectId") String projectId,
      @QueryParam("additional-children") String additionalChildren,
      @QueryParam("remove-children") String removeChildren,
      @QueryParam("additional-parents") String additionalParents,
      @QueryParam("remove-parents") String removeParents);

  @Operation(summary ="Get elastic projects", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/projectsel")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PagedResultDto getProjectsElastic(Map<String, Object> serverOptions);

  @Operation(summary ="Get ProjectType by name", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project-type-by-name/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectTypeDto getProjectTypeByName(@PathParam("name") String name);

  @Operation(summary ="Search projectType by name", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/projecttype")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectTypeDto> searchProjectTypeByName(@QueryParam("search") String searchterm);

  @Operation(summary ="Search child projectType by name", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/projectchildtype")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectTypeDto> searchChildProjectTypeByName(
      @QueryParam("search") String searchterm,
      @QueryParam("project-parent-guid") Long projectParentGuid);

  @Operation(summary ="Search createable projectTypes", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/createabletype")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectTypeDto> searchCreateableProjectTypes(@QueryParam("search") String searchterm);

  @Operation(summary ="Search createable child projectTypes", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/createablechildtype")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectTypeDto> searchCreateableChildProjectTypes(
      @QueryParam("search") String searchterm,
      @QueryParam("project-parent-guid") Long projectParentGuid);

  @Operation(summary ="Get child types", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/type/{idName}/child-types")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectTypeDto> getChildTypes(@PathParam("idName") String idName);

  @GET
  @Path("/type/{idName}/parent-types")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectTypeDto> getParentTypes(@PathParam("idName") String idName);

  @Operation(summary ="Create a project", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/create")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PmcProjectDto createProject(PmcProjectDto project);

  @Operation(summary ="Cancel a project", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/stop")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectDto cancelProject(@PathParam("guid") Long projectGuid, @QueryParam("reason") String reason);

  @Operation(summary ="Set result", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/project/{guid}/result")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PmcProjectDto setResult(@PathParam("guid") Long projectGuid, PmcProjectDto project);

  // Process related REST calls

  @Operation(summary ="Save project process", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/project/{guid}/process")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PmcProjectProcessDto saveProjectProcess(@PathParam("guid") Long projectGuid, PmcProjectProcessDto process);

  @Operation(summary ="Start project process", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/process/{processGuid}/start")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectProcessDto startProjectProcess(@PathParam("processGuid") Long processGuid);

  @Operation(summary ="Cancel project process", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/process/{processGuid}/stop")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectProcessDto cancelProjectProcess(
      @PathParam("processGuid") Long processGuid,
      @QueryParam("result-status") String resultStatus,
      @QueryParam("result-comment") String resultComment,
      @QueryParam("reason") String reason);

  @Operation(summary ="Remove project process", tags = {"ProjectRestService"}, description = "")
  @DELETE
  @Path("/project/{guid}/process/{processGuid}")
  @Produces(MediaType.APPLICATION_JSON)
  Response removeProjectProcess(@PathParam("processGuid") Long processGuid, @QueryParam("reason") String reason);

  @Operation(summary ="Get all project processes", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/process")
  @Produces(MediaType.APPLICATION_JSON)
  Collection<PmcProjectProcessDto> getAllProjectProcesses(@PathParam("guid") Long projectGuid);

  @Operation(summary ="Get main project process", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/mainprocess")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectProcessDto getMainProjectProcess(@PathParam("guid") Long projectGuid);

  @Operation(summary ="Get processes and tasks", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/processes-and-tasks")
  @Produces(MediaType.APPLICATION_JSON)
  Map<String, Object> getProcessesAndTasks(@PathParam("guid") Long projectGuid, @QueryParam("use-legacy") Boolean useLegacy);

  @Operation(summary ="Get project phases", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/phase/")
  @Produces(MediaType.APPLICATION_JSON)
  Collection<PmcProjectPhaseDto> getProjectPhases(@PathParam("guid") Long projectGuid);

  @Operation(summary ="Get project phase", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{guid}/phase-definition/{phaseDefIdName}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectPhaseDto getProjectPhase(@PathParam("guid") Long projectGuid, @PathParam("phaseDefIdName") String phaseDefIdName);

  @Operation(summary ="Update project phase", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/project/{guid}/phase-definition/{idName}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectPhaseDto updateProjectPhase(
      @PathParam("guid") Long projectGuid,
      @PathParam("idName") String phaseDefinitionIdName,
      PmcProjectPhaseDto phase);

  @Operation(summary ="Sync project", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/project/{guid}/synch")
  @Produces(MediaType.APPLICATION_JSON)
  Response synchProject(@PathParam("guid") Long projectGuid);

  @Operation(summary ="Get cardlist for project", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("/project/{projectId}/cardlist/{idName}")
  @Produces(MediaType.APPLICATION_JSON)
  List<PmcProjectCardDto> getCardListForProject(
      @PathParam("projectId") String projectId,
      @PathParam("idName") String cardListIdName,
      @QueryParam("createIfEmpty") @DefaultValue(value = "true") boolean createIfEmpty) throws Exception;

  @Operation(summary ="Add card for project", tags = {"ProjectRestService"}, description = "")
  @POST
  @Path("/project/{projectId}/cardlist/{idName}")
  @Produces(MediaType.APPLICATION_JSON)
  PmcProjectCardDto addCardForProject(@PathParam("projectId") String projectId, @PathParam("idName") String cardListIdName)
      throws Exception;

  @Operation(summary ="Update card title", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/project/{projectId}/card/{cardId}/title")
  PmcProjectCardDto updateCardTitle(@PathParam("projectId") String projectId, @PathParam("cardId") Long cardId, String title)
      throws Exception;

  @Operation(summary ="Update card comment", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/project/{projectId}/card/{cardId}/comments")
  PmcProjectCardDto updateCardComment(@PathParam("projectId") String projectId, @PathParam("cardId") Long cardId, String comment)
      throws Exception;

  @Operation(summary ="Update card status", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/project/{projectId}/card/{cardId}/status")
  PmcProjectCardDto updateCardStatus(@PathParam("projectId") String projectId, @PathParam("cardId") Long cardId, String status)
      throws Exception;

  @Operation(summary ="Update card category", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/project/{projectId}/card/{cardId}/category")
  PmcProjectCardDto updateCardCategory(@PathParam("projectId") String projectId, @PathParam("cardId") Long cardId, String category)
      throws Exception;

  @Operation(summary ="Update card owner", tags = {"ProjectRestService"}, description = "")
  @PUT
  @Path("/project/{projectId}/card/{cardId}/owner")
  PmcProjectCardDto updateCardOwner(@PathParam("projectId") String projectId, @PathParam("cardId") Long cardId, String owner)
      throws Exception;

  @Operation(summary="Update Project Highlighted", tags={"ProjectRestService"})
  @POST
  @Path("/project/{projectId}/highlight")
  PmcProjectDto updateHighlighted(@PathParam("projectId") String projectId, boolean isHighlighted);

  @Operation(summary ="Get Project Highlighted", tags = {"ProjectRestService"}, description = "")
  @GET
  @Path("project/{projectId}/highlight")
  boolean getHighlighted(@PathParam("projectId") String projectId);

}
