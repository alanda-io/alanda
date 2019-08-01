/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest;

import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcRoleInstanceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** @author developer */
@Tag(name = "PmcRoleRestService")
@Path(PmcRoleRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface PmcRoleRestService {

  public static final String PATH = "/app/pmcrole";

  @GET
  @Path("/list")
  List<PmcRoleDto> getRoles();

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  PmcRoleDto addRole(PmcRoleDto pmcRoleDto);

  @GET
  @Path("/single/{roleId}")
  PmcRoleDto getRoleById(@PathParam("roleId") Long roleId);

  @GET
  @Path("/single/name/{roleName}")
  PmcRoleDto getRoleByName(@PathParam("roleName") String roleName);

  @PUT
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)
  PmcRoleDto updateRole(PmcRoleDto pmcRoleDto);

  @Operation(
      summary = "Get role instances for project",
      tags = {"PmcRoleRestService"})
  @GET
  @Path("/role-instances/project/{projectGuid}/role/{roleGuid}")
  Collection<PmcRoleInstanceDto> getRoleInstancesForProject(
      @PathParam("projectGuid") Long projectGuid, @PathParam("roleGuid") Long roleGuid);

  @Operation(
      summary = "Set role instances for project",
      tags = {"PmcRoleRestService"})
  @POST
  @Path("/role-instances/project/{projectGuid}/role")
  @Consumes(MediaType.APPLICATION_JSON)
  Response setRoleInstancesForProject(
      @PathParam("projectGuid") Long projectGuid,
      @QueryParam("source") String source,
      Collection<PmcRoleInstanceDto> role);
}
