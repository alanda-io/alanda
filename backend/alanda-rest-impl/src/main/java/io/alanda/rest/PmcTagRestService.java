package io.alanda.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.alanda.base.dto.PmcTagDto;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author developer
 */

@Tag(name = "PmcTagRestService")
@Path("/app/pmc-tags")
public interface PmcTagRestService {

  @GET
  @Path("/all")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract List<PmcTagDto> getAll();

  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public abstract List<PmcTagDto> search(@QueryParam("query") String query);

}
