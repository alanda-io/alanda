package io.alanda.rest.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

@Provider
@ServerInterceptor
public class CorsInterceptor implements PreProcessInterceptor, MessageBodyWriterInterceptor {

  private static final String ORIGIN = "Origin";

  /**
   * The Access-Control-Allow-Origin header indicates which origin a resource it is specified for can be shared with.
   * ABNF: Access-Control-Allow-Origin = "Access-Control-Allow-Origin" ":" source origin string | "*"
   */
  private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

  private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

  private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

  private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

  //
  private static final ThreadLocal<String> REQUEST_ORIGIN = new ThreadLocal<String>();

  //
  private final Set<String> allowedOrigins;

  public CorsInterceptor() {
    this.allowedOrigins = new HashSet<String>();
    this.allowedOrigins.add("*");
    this.allowedOrigins.add("http://localhost:9000");
  }

  @Override
  public ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker method) throws Failure, WebApplicationException {
    if ( !allowedOrigins.isEmpty()) {
      REQUEST_ORIGIN.set(request.getHttpHeaders().getRequestHeaders().getFirst(ORIGIN));
    }
    request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, "*/*; charset=UTF-8");
    request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, "UTF-8");
    return null;
  }

  @Override
  public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
    if ( !allowedOrigins.isEmpty()) {
      if (allowedOrigins.contains(REQUEST_ORIGIN.get())) {
        context.getHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN, REQUEST_ORIGIN.get());
      } else if (allowedOrigins.contains("*")) {
        context.getHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      }
      context.getHeaders().add(ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
      context.getHeaders().add(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, DELETE, OPTIONS");
      context
        .getHeaders()
        .add(
          ACCESS_CONTROL_ALLOW_HEADERS,
          "Content-Type,X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5,  Date, X-Api-Version, X-File-Name, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
    }
    context.proceed();
  }

}
