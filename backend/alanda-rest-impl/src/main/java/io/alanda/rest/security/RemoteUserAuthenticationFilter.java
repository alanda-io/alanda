/**
 * Copyright 2017, Yahoo Inc.
 * Released under Apache 2.0 License.  See file LICENSE in project root.
 */

package io.alanda.rest.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;


public final class RemoteUserAuthenticationFilter extends AuthenticatingFilter {

    private String remoteUserHeaderName = null;
    private String remoteRolesHeaderName = null;
    private String remoteRolesSeparator = null;
    private String remoteUserDomainPrefix = null;

    public RemoteUserAuthenticationFilter() {
        super();
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws IOException {
        String user = getRemoteUserHeader(request);
        Set<String> roles = getRoles(request);
        if (user == null) {
            return new UsernamePasswordToken();
        } else {
            return createToken(user, roles);
        }
    }

    /**
     * Configuration setter.
     * remoteUserHeaderName is required for Authentication.
     * It specifies the name of the HTTP header whose value is the authenticated user name.
     * @param remoteUserHeaderName Name of HTTP header that contains the user.
     * Invoke this setter from the shiro.ini file with remoteUserFilter.remoteUserHeaderName=X-Remote-User
     */
    public void setRemoteUserHeaderName(String remoteUserHeaderName) {
        this.remoteUserHeaderName = remoteUserHeaderName;
    }

    /**
     * Configuration setter.
     * remoteRolesHeaderName is optional; if provided, it enables a single Role in Authorization.
     * @param remoteRolesHeaderName Name of HTTP header that contains the roles.
     * Invoke this setter from the shiro.ini file with remoteUserFilter.remoteRolesHeaderName=X-Remote-Roles
     */
    public void setRemoteRolesHeaderName(String remoteRolesHeaderName) {
        this.remoteRolesHeaderName = remoteRolesHeaderName;
    }

    /** 
     * Configuration setter.
     * remoteRolesSeparator is optional for Authentication.  
     * If not provided, the Roles header value string is converted to a single Role.  
     * If provided and non-empty, it is the separator between roles in the Roles header.  
     * If provided and empty, the Roles header value string is converted to a set of one-character roles.
   * @param remoteRolesSeparator
     */
    public void setRemoteRolesSeparator(String remoteRolesSeparator) {
        this.remoteRolesSeparator = remoteRolesSeparator;
    }

  public void setRemoteUserDomainPrefix(String rmeoteUserDomainPrefix) {
    this.remoteUserDomainPrefix = rmeoteUserDomainPrefix;
  }

    
    
    protected String getRemoteUserHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        if (remoteUserHeaderName == null) {
            return null;
        }
        String header = httpRequest.getHeader(remoteUserHeaderName);
        if (header != null)
          header = header.replaceFirst(remoteUserDomainPrefix, "");
        return header;
    }

    protected Set<String> getRoles(ServletRequest request) {
        if (remoteRolesHeaderName == null) {
            return Collections.emptySet();
        }
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String rolesString = httpRequest.getHeader(remoteRolesHeaderName);
        if (rolesString == null) {
            return Collections.emptySet();
        } 
        if (remoteRolesSeparator == null) {
            return Collections.singleton(maybeTrim(rolesString));
        }

        return Arrays.stream(rolesString.split(remoteRolesSeparator))
            .map((String s) -> maybeTrim(s))
            .filter(s -> !s.isEmpty()) //
            .collect(Collectors.toSet());
    }

    private String maybeTrim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    public AuthenticationToken createToken(String user, Set<String> roles) {
        return new UsernamePasswordToken(user, "");
        //RemoteUserAuthenticationToken(user, roles);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return false;
    }

    /**
     * Processes unauthenticated requests.
     *
     * @param request  incoming ServletRequest
     * @param response outgoing ServletResponse
     * @return true if the request should be processed; false if the request should not continue to be processed
   * @throws java.lang.Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

}