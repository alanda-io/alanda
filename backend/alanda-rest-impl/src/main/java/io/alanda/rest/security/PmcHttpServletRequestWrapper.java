/**
 * 
 */
package io.alanda.rest.security;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author jlo
 */
public class PmcHttpServletRequestWrapper extends HttpServletRequestWrapper {

  Principal principal;

  public PmcHttpServletRequestWrapper(HttpServletRequest request, Principal principal) {
    super(request);
    this.principal = principal;
  }

  @Override
  public String getAuthType() {
    return "Basic";
  }

  @Override
  public String getRemoteUser() {
    return principal.getName();
  }

  @Override
  public Principal getUserPrincipal() {
    return principal;
  }

}
