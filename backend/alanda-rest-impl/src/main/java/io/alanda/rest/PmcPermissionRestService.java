/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.alanda.base.dto.PmcPermissionDto;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author developer
 */
@Tag(name = "PmcPermissionRestService")
@Path(PmcPermissionRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface PmcPermissionRestService {

  public static final String PATH = "/app/permission";

  @GET
  @Path("/list")
  List<PmcPermissionDto> getPermissions();

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  PmcPermissionDto addPermission(PmcPermissionDto pmcPermissionDto);

  @GET
  @Path("/single/{permissionId}")
  PmcPermissionDto getPermissionById(@PathParam("permissionId") Long permissionId);

  @PUT
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)
  PmcPermissionDto updatePermission(PmcPermissionDto pmcPermissionDto);

}
