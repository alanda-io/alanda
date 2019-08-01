package io.alanda.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "PmcGroupRestService")
@Path(PmcGroupRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface PmcGroupRestService {

  public static final String PATH = "/app/group";
  
  @POST
  @Path("/list")
  PagedResultDto<PmcGroupDto> getAllGroups(Map<String, Object> serverOptions);

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  Response saveGroup(PmcGroupDto pmcGroupDto);

  @GET
  @Path("/singleGroupById/{groupId}")
  PmcGroupDto getGroupById(@PathParam("groupId") Long groupId);

  @PUT
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)
  Response updateGroup(PmcGroupDto pmcGroupDto);

  @GET
  @Path("/singleGroupByName/{groupName}")
  PmcGroupDto getGroupByGroupName(@PathParam("groupName") String groupName);

  @PUT
  @Path("roles/update/{groupId}")
  @Consumes(MediaType.APPLICATION_JSON)
  Response updateRolesForGroup(@PathParam("groupId") Long groupId, List<PmcRoleDto> roles);

  @GET
  @Path("/permissions/effective/{groupId}")
  Collection<PmcPermissionDto> getEffectivePermissionsForGroup(@PathParam("groupId") Long groupId);

  @PUT
  @Path("permissions/update/{groupId}")
  @Consumes(MediaType.APPLICATION_JSON)
  Response updatePermissionsForGroup(@PathParam("groupId") Long groupId, List<PmcPermissionDto> permissions);

  @GET
  @Path("/role/{roleId}")
  List<PmcGroupDto> getGroupsForRole(@PathParam("roleId") Long roleId);

  @GET
  @Path("/rolename/{roleName}")
  List<PmcGroupDto> getGroupsForRole(@PathParam("roleName") String roleName);

  @POST
  @Path("/create/group")
  PmcGroupDto createGroupWithRoles(String groupName, String loginName, String firstName, String LastName, Set<String> roleNames);

}
