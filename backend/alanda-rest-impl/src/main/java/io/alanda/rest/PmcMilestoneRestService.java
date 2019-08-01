package io.alanda.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.dto.MilestoneDto;
import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/app/ms")
@Produces(MediaType.APPLICATION_JSON)
public interface PmcMilestoneRestService {

  @Operation(summary ="List all milestones", tags = {"PmcMilestoneRestService"}, description = "")
  @GET
  @Path("/milestone/all")
  @Produces(MediaType.APPLICATION_JSON)
  List<MilestoneDto> listAllMilestones();

  @Operation(summary ="Get milestone by guid", tags = {"PmcMilestoneRestService"}, description = "")
  @GET
  @Path("/milestone/{guid}")
  @Produces(MediaType.APPLICATION_JSON)
  MilestoneDto getMileByGuid(@PathParam("guid") Long milestoneGuid);

  @Operation(summary ="Create a milestone", tags = {"PmcMilestoneRestService"}, description = "")
  @POST
  @Path("/milestone/create")
  @Consumes(MediaType.APPLICATION_JSON)
  Long createMilestone(MilestoneDto milestoneDto);

  @Operation(summary ="Update a milestone", tags = {"PmcMilestoneRestService"}, description = "")
  @PUT
  @Path("/milestone/update")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Response updateMilestone(MilestoneDto milestoneDto);

  @Operation(summary ="Delete a milestone", tags = {"PmcMilestoneRestService"}, description = "")
  @DELETE
  @Path("/milestone/delete/{guid}")
  Response deleteMilestone(@PathParam("guid") Long milestoneGuid);

  @Operation(summary ="Get all milestones per project", tags = {"PmcMilestoneRestService"}, description = "")
  @GET
  @Path("/project-milestone/by-project/{projectId}")
  List<PmcProjectMilestoneDto> milestonesPerProject(@PathParam("projectId") Long projectId);

  @Operation(summary ="Get milestone by id", tags = {"PmcMilestoneRestService"}, description = "")
  @GET
  @Path("/project-milestone/{guid}")
  PmcProjectMilestoneDto milestonesById(@PathParam("guid") Long guid);

  @Operation(summary ="Get milestoneDefinitions per projectType", tags = {"PmcMilestoneRestService"}, description = "")
  @GET
  @Path("/projecttype-milestone/{projectTypeId}")
  PmcProjectTypeDto milestoneDefinitionsPerProjectType(@PathParam("projectTypeId") Long projectTypeId);

  @Operation(summary ="Create a project milestone", tags = {"PmcMilestoneRestService"}, description = "")
  @POST
  @Path("/project-milestone/create")
  @Consumes(MediaType.APPLICATION_JSON)
  Long createProjectMilestone(@QueryParam("reason") String reason, PmcProjectMilestoneDto projectMilestoneDto);

  @Operation(summary ="Update a project milestone", tags = {"PmcMilestoneRestService"}, description = "")
  @PUT
  @Path("/project-milestone/edit/{projectMilestoneGuid}")
  @Consumes(MediaType.APPLICATION_JSON)
  Response updateProjectMilestone(
      @PathParam("projectMilestoneGuid") Long projectMilestoneGuid,
      @QueryParam("reason") String reason,
      PmcProjectMilestoneDto projectMilestoneDto);

  @Operation(summary ="Get milestone by project and msIdName", tags = {"PmcMilestoneRestService"}, description = "")
  @GET
  @Path("/project/{projectId}/ms/{msIdName}")
  PmcProjectMilestoneDto getByProjectAndMsIdName(
      @PathParam("projectId") String projectId,
      @PathParam("msIdName") String msIdName);

  @Operation(summary ="Update a project milestone", tags = {"PmcMilestoneRestService"}, description = "")
  @PUT
  @Path("/project/{projectId}/ms/{msIdName}")
  @Consumes(MediaType.APPLICATION_JSON)
  Response updateProjectMilestone(
      @PathParam("projectId") String projectId,
      @PathParam("msIdName") String msIdName,
      @QueryParam("reason") String reason,
      PmcProjectMilestoneDto projectMilestoneDto);
}
