package io.alanda.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.alanda.base.dto.PmcHistoryLogDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "PmcHistoryLogRestService")
@Path("/app/history")
@Produces(MediaType.APPLICATION_JSON)
public interface PmcHistoryLogRestService {

  @POST
  @Path("/search/{pageNumber}/{pageSize}")
  @Consumes(MediaType.APPLICATION_JSON)
  Map<String, Object> search(@PathParam("pageNumber") int pageNumber, @PathParam("pageSize") int pageSize, PmcHistoryLogDto searchDto);
}
