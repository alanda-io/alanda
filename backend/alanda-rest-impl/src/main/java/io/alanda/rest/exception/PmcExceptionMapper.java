/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author developer
 */
@Provider
public class PmcExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger log = LoggerFactory.getLogger(PmcExceptionMapper.class);

  @Override
  public Response toResponse(Throwable e) {

    if (e instanceof WebApplicationException) {
      return ((WebApplicationException) e).getResponse();
    } else if (e.getCause() instanceof WebApplicationException) {
      return ((WebApplicationException) e.getCause()).getResponse();
    }
    PmcException exception = new PmcException();
    exception.setMessage(e.getMessage());
    if (e instanceof ForbiddenException) {
      exception.setStatus(Response.Status.FORBIDDEN.getStatusCode());
      log.warn(e.getMessage());
    } else {
      exception.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      StringWriter errorStackTrace = new StringWriter();
      e.printStackTrace(new PrintWriter(errorStackTrace));
      exception.setStackTrace(errorStackTrace.toString());
      log.error(exception.getStackTrace());
    }
    return Response.status(exception.getStatus()).entity(exception).type(MediaType.APPLICATION_JSON).build();
  }

}
