package io.alanda.rest;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "PmcBatchPmcProjectStartRestService")
@Path(BatchPmcProjectStartRestService.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface BatchPmcProjectStartRestService {

  public static final String PATH = "/app/batch";

  @POST
  @Path("{projecttype}/start")
  @Consumes("multipart/form-data")
  @Produces(MediaType.APPLICATION_JSON)
  Map<String, Object> upload(MultipartFormDataInput input, @PathParam("projecttype") String projectType) throws Exception;

  @GET
  @Path("download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  Response download(@QueryParam("pid") String pid) throws IOException;

  @GET
  @Path("{projecttype}/template")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  Response template(@PathParam("projecttype") String projectType) throws IOException;

  @GET
  @Path("status")
  @Produces(MediaType.APPLICATION_JSON)
  Map<String, Object> status(@QueryParam("pid") String pid);

}
