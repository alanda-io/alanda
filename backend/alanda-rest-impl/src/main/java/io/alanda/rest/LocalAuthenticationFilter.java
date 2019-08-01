/**
 * 
 */
package io.alanda.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcUserService;

/**
 * @author Julian Löffelhardt mocks basic auth
 */
public class LocalAuthenticationFilter implements Filter {

  /** Logger */
  private final Logger logger = LoggerFactory.getLogger(LocalAuthenticationFilter.class);

  private String realm = "Test";

  @Inject
  private PmcUserService userService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException,
      ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null) {
      StringTokenizer st = new StringTokenizer(authHeader);
      if (st.hasMoreTokens()) {
        String basic = st.nextToken();

        if (basic.equalsIgnoreCase("Basic")) {
          try {
            String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
            logger.debug("Credentials: " + credentials);
            int p = credentials.indexOf(":");
            if (p != -1) {
              final String username = credentials.substring(0, p).trim();
              //String password = credentials.substring(p + 1).trim();

              PmcUserDto user = validateUser(username);

              if (user != null) {
                final Principal princ = new Principal() {

                  @Override
                  public String getName() {
                    return username;
                  }
                };
                filterChain.doFilter(new HttpServletRequestWrapper(request) {

                  @Override
                  public String getAuthType() {
                    return "Basic";
                  }

                  @Override
                  public String getRemoteUser() {
                    return princ.getName();
                  }

                  @Override
                  public Principal getUserPrincipal() {
                    return princ;
                  }

                }, servletResponse);
              } else {
                unauthorized(response, "Bad credentials");
              }
            } else {
              unauthorized(response, "Invalid authentication token");
            }
          } catch (UnsupportedEncodingException e) {
            throw new Error("Couldn't retrieve authentication", e);
          }
        }
      }
    } else {
      unauthorized(response);
    }
  }

  private PmcUserDto validateUser(String username) {
    PmcUserDto user = null;
    String fqn = username;
    String loginName = fqn.substring(fqn.indexOf("\\") + 1, fqn.length());
    user = userService.getUserByLoginName(loginName, true);
    
    return user;
  }
  
  @Override
  public void destroy() {
  }

  private void unauthorized(HttpServletResponse response, String message) throws IOException {
    response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
    response.sendError(401, message);
  }

  private void unauthorized(HttpServletResponse response) throws IOException {
    unauthorized(response, "Unauthorized");
  }

}
