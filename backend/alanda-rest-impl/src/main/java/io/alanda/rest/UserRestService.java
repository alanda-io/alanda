package io.alanda.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import io.alanda.base.dto.PagedResultDto;
import io.alanda.base.dto.PmcDepartmentDto;
import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcPermissionDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.util.cache.CacheStatsDto;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(info = @Info(title = "Dreh den Swag auf", version = "1.0", description = "Alle alanda REST-Schnittstellen im Überblick"), tags = {
  @Tag(name = "UserRestService"),
  @Tag(name = "PmcTaskRestService"),
  @Tag(name = "PmcTaskRestService"),
  @Tag(name = "PmcMilestoneRestService"),
  @Tag(name = "ProjectRestService"),
  @Tag(name = "CalendarRestService"),
  @Tag(name = "PmcFinderRestService"),
  @Tag(name = "MonitorRestService"),
  @Tag(name = "RefObjectRestService"),
  @Tag(name = "BatchPmcProjectStartRestService"),
  @Tag(name = "ElasticRestService"),
  @Tag(name = "PmcBlogRestService"),
  @Tag(name = "PmcGroupRestService"),
  @Tag(name = "PmcHistoryLogRestService"),
  @Tag(name = "PmcProcessRestService"),
  @Tag(name = "PmcPropertyRestService"),
  @Tag(name = "PmcRoleRestService"),
  @Tag(name = "PmcTagRestService"),
  @Tag(name = "PmcAttachmentRestService"),
  @Tag(name = "CamundaHelperRestService"),
  @Tag(name = "PmcPermissionRestService"),
  @Tag(name = "PmcBatchPmcProjectStartRestService"),
  @Tag(name = "default", description = "der ganze Rest"),})
@Path(UserRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface UserRestService {

  public static final String PATH = "/app/user";

  @Operation(summary = "Get current user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/current")
  PmcUserDto getCurrentUser();

  @Operation(summary = "Get current shiro user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/current/shiro")
  PmcUserDto getCurrentUserShiro();

  @Operation(summary = "Run as user", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/runas/{loginname}")
  PmcUserDto runAsUser(@PathParam("loginname") String loginName);

  @Operation(summary = "Release run as user", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/release")
  PmcUserDto releaseRunAsUser();

  @Operation(summary = "Get user by login name", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/single/{loginname}")
  PmcUserDto getByLoginName(@PathParam("loginname") String loginName);

  @Operation(summary = "Get repo user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/repo/{pageNumber}/{pageSize}")
  List<PmcUserDto> getRepoUser(@PathParam("pageNumber") int pageNumber, @PathParam("pageSize") int pageSize);

  @Operation(summary = "searches for users by example, returns paginated results", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/repo")
  PagedResultDto<PmcUserDto> getPagedResult(PagedSearchOptions serverOptions);

  @Operation(summary = "Save repo user", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/repo/save")
  @Consumes(MediaType.APPLICATION_JSON)
  Response saveRepoUser(PmcUserDto pmcUserDto);

  @Operation(summary = "create new simple user", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/repo/create")
  @Consumes(MediaType.APPLICATION_JSON)
  Response createUser(PmcUserDto pmcUserDto);

  @Operation(summary = "Get repo user by id", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/repo/{userId}")
  PmcUserDto repoUserById(@PathParam("userId") Long userId);

  @Operation(summary = "Update repo user", tags = {"UserRestService"}, description = "")
  @PUT
  @Path("/repo/update")
  Response updateRepoUser(PmcUserDto pmcRepoUserDto);

  @Operation(summary = "Get usernames", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/repo/getUsernames")
  List<String> getUsernames();

  @Operation(summary = "Get users by groupId", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/repo/getUsersByGroupId/{groupId}")
  List<PmcUserDto> getUsersByGroupId(@PathParam("groupId") Long groupId);

  //  @GET
  //  @Path("/repo/findUser/{querystring")
  //  List<PmcUserDto> findUser(@PathParam("querystring") String querystring);

  @Operation(summary = "Get user by userId", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/by-user-id/{userId}")
  PmcUserDto getById(@PathParam("userId") Long userId);

  @Operation(summary = "Search user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/search")
  List<PmcUserDto> search(
      @QueryParam("text") String text,
      @QueryParam("groupName") String groupName,
      @QueryParam("contactRequired") Boolean isContactRequired);

  @Operation(summary = "Find responsible user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/findResp")
  PmcUserDto findResponsibleUser(@QueryParam("processInstanceId") String processInstanceId, @QueryParam("groupName") String groupName);

  @Operation(summary = "Set responsible user", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/setResp/{processInstanceId}/{groupName}/{userId}")
  Response setResponsibleUser(
      @PathParam("processInstanceId") String processInstanceId,
      @PathParam("groupName") String groupName,
      @PathParam("userId") long userId,
      @QueryParam("source") String source);

  @Operation(summary = "Get cache stats", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/cacheStats")
  Map<String, CacheStatsDto> cacheStats();

  @Operation(summary = "Cache invalidate all", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/cacheInvalidateAll")
  Response cacheInvalidateAll();

  @Operation(summary = "Get permissions for user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/permissions/{userId}")
  List<PmcPermissionDto> getPermissionsForUser(@PathParam("userId") Long userId);

  @Operation(summary = "Get effective permissions for user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/permissions/effective/{userId}")
  Collection<PmcPermissionDto> getEffectivePermissionsForUser(@PathParam("userId") Long userId);

  @Operation(summary = "Get roles for user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/roles/{userId}")
  List<PmcRoleDto> getRolesForUser(@PathParam("userId") Long userId);

  @Operation(summary = "Get effective roles for user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/roles/effective/{userId}")
  Collection<PmcRoleDto> getEffectiveRolesForUser(@PathParam("userId") Long userId);

  @Operation(summary = "Update permissions for user", tags = {"UserRestService"}, description = "")
  @PUT
  @Path("/permissions/update/{userId}")
  Response updatePermissionsForUser(@PathParam("userId") Long userId, List<PmcPermissionDto> permissons);

  @Operation(summary = "Update roles for user", tags = {"UserRestService"}, description = "")
  @PUT
  @Path("/roles/update/{userId}")
  Response updateRolesForUser(@PathParam("userId") Long userId, List<PmcRoleDto> roles);

  @Operation(summary = "Update groups for user", tags = {"UserRestService"}, description = "")
  @PUT
  @Path("/groups/update/{userId}")
  Response updateGroupsForUser(@PathParam("userId") Long userId, List<PmcGroupDto> groups);

  @Operation(summary = "Get users for role", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/role/{roleId}")
  List<PmcUserDto> getUsersForRole(@PathParam("roleId") Long roleId);

  @Operation(summary = "Create group account", tags = {"UserRestService"}, description = "")
  @POST
  @Path("/groupaccount")
  PmcUserDto createGroupAcount(PmcUserDto accountInfo);

  @Operation(summary = "Get department list", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/departmentlist")
  List<PmcDepartmentDto> getDepartmentList();

  @Operation(summary = "Get groups for user", tags = {"UserRestService"}, description = "")
  @GET
  @Path("/groups/{loginname}")
  List<PmcGroupDto> getGroupsForUser(@PathParam("loginname") String loginname);

}
