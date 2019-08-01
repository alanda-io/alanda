/**
 * 
 */
package io.alanda.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.util.UserContext;
import io.alanda.base.util.cache.UserCache;

/**
 * @author Julian Löffelhardt mocks basic auth
 */
public class ThreadLocalFilter implements Filter {

  /** Logger */
  private final Logger logger = LoggerFactory.getLogger(ThreadLocalFilter.class);

  @Inject
  private UserCache userCache;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException,
      ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;

    PmcUserDto user = extractUser(request);
    try {
      logger.info("setting user[{}]", user != null ? user.getLoginName() : "NULL");
      UserContext.setUser(user);
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      UserContext.remove();
      io.alanda.base.util.UserContext.remove();
      logger.info("removing user[{}]", user != null ? user.getLoginName() : "NULL");
    }

  }

  private PmcUserDto extractUser(HttpServletRequest request) {
    PmcUserDto user = null;
    String fqn = request.getUserPrincipal().getName();
    String loginName = fqn.substring(fqn.indexOf("\\") + 1, fqn.length());
    user = userCache.get(loginName);
    //logger.info("loaded user: " + user);
    return user;
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

}
