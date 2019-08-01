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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcUserService;

import io.alanda.rest.security.PmcHttpServletRequestWrapper;
import io.alanda.rest.security.PmcPrincipal;

/**
 * @author Julian Löffelhardt mocks basic auth
 */
public class AuthenticationFilter implements Filter {

  /** Logger */
  private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

  private String realm = "PMC";

  private static final String PRINCIPALSESSIONKEY = AuthenticationFilter.class.getName() + ".PRINCIPAL";

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

    if (doFilterPrincipal(request, response, filterChain)) {
      // previously authenticated user
      return;
    }

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
              String password = credentials.substring(p + 1).trim();

              PmcUserDto user = validateUser(username, password);

              if (user != null) {
                final Principal princ = new PmcPrincipal(username);
                request.getSession().setAttribute(PRINCIPALSESSIONKEY, princ);
                filterChain.doFilter(new PmcHttpServletRequestWrapper(request, princ), servletResponse);
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

  /**
   * Filter for a previously logged on user.
   * 
   * @param request HTTP request.
   * @param response HTTP response.
   * @param chain Filter chain.
   * @return True if a user already authenticated.
   * @throws ServletException
   * @throws IOException
   */
  private boolean doFilterPrincipal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws IOException,
      ServletException {
    Principal sPrincipal = request.getUserPrincipal();

    if (sPrincipal == null) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        sPrincipal = (Principal) session.getAttribute(PRINCIPALSESSIONKEY);
      }
    }

    if (sPrincipal == null) {
      // no principal in this request
      return false;
    }
    final Principal principal = sPrincipal;
    //TODO check for RE-AUTH

    // user already authenticated

    logger.info("previously authenticated user: {}", principal.getName());
    chain.doFilter(new PmcHttpServletRequestWrapper(request, principal), response);
    return true;
  }

  private PmcUserDto validateUser(String username, String password) {
    logger.info("called info validate user for " + username);
    PmcUserDto user = null;
    String fqn = username;
    String loginName = fqn.substring(fqn.indexOf("\\") + 1, fqn.length());
    user = userService.getUserByLoginName(loginName, true);
    if (user == null)
      return null;
    if ( !userService.checkPassword(user.getGuid(), password)) {
      return null;
    }

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
