/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.alanda.base.dto.PmcHistoryLogDto;

/**
 * @author jlo
 */
@Produces(MediaType.APPLICATION_JSON)
public interface PmcProjectHistoryRestResource {

  @POST
  public Response saveHistoryLog(PmcHistoryLogDto history);

  @GET
  public List<PmcHistoryLogDto> list();

}
