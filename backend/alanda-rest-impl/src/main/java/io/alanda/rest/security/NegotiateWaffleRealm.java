/**
 * Waffle (https://github.com/Waffle/waffle) Copyright (c) 2010-2016 Application Security, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at https://www.eclipse.org/legal/epl-v10.html. Contributors:
 * Application Security, Inc.
 */
package io.alanda.rest.security;

import java.security.Principal;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.security.auth.Subject;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.util.cache.UserCache;
import waffle.servlet.WindowsPrincipal;
import waffle.shiro.negotiate.AuthenticationInProgressException;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class NegotiateAuthenticationRealm.
 */
public class NegotiateWaffleRealm extends AuthorizingRealm {

  @Inject
  private PmcUserService userService;

  @Inject
  private UserCache userCache;


  /**
   * This class's private logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateWaffleRealm.class);

  /** The windows auth provider. */
  private final IWindowsAuthProvider windowsAuthProvider;

  /**
   * Instantiates a new negotiate authentication realm.
   */
  public NegotiateWaffleRealm() {
    this.windowsAuthProvider = new WindowsAuthProviderImpl();
  }

  /*
   * (non-Javadoc)
   * @see org.apache.shiro.realm.AuthenticatingRealm#supports(org.apache.shiro.authc.AuthenticationToken)
   */
  @Override
  public boolean supports(final AuthenticationToken token) {
    return token instanceof NegotiateToken;
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {

    Object availablePrincipal = getAvailablePrincipal(pc);

    PmcUserDto user;
    if (availablePrincipal != null) {
      String fqn = null;
      if (availablePrincipal instanceof Principal) {
        fqn = ((Principal) availablePrincipal).getName();
      } else if (availablePrincipal instanceof String) {
        fqn = (String) availablePrincipal;
      } else {
        fqn = availablePrincipal.toString();
      }
      String loginName = fqn.substring(fqn.indexOf("\\") + 1, fqn.length());
      user = userCache.get(loginName);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("User for Authorization: " + user.getLoginName());
      }
      return (AuthorizationInfo) user;
    }
    //    SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo(user.getRoles());
    //    sai.
    return new SimpleAuthorizationInfo();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken t) {

    final NegotiateToken token = (NegotiateToken) t;
    final byte[] inToken = token.getIn();

    if (token.isNtlmPost()) {
      // type 2 NTLM authentication message received
      this.windowsAuthProvider.resetSecurityToken(token.getConnectionId());
    }

    final IWindowsSecurityContext securityContext;
    try {
      securityContext = this.windowsAuthProvider.acceptSecurityToken(token.getConnectionId(), inToken, token.getSecurityPackage());
    } catch (final Exception e) {
      LOGGER.warn("error logging in user: {}", e.getMessage());
      LOGGER.warn("user: credentials - {}", token.getCredentials());
      LOGGER.warn("user: subject - {}", token.getSubject());
      LOGGER.warn("user: principal - {}", token.getPrincipal());
      throw new AuthenticationException(e);
    }

    final byte[] continueTokenBytes = securityContext.getToken();
    token.setOut(continueTokenBytes);
    if (continueTokenBytes != null) {
      LOGGER.debug("continue token bytes: {}", continueTokenBytes.length);
    } else {
      LOGGER.debug("no continue token bytes");
    }

    if (securityContext.isContinue() || token.isNtlmPost()) {
      throw new AuthenticationInProgressException();
    }

    final IWindowsIdentity windowsIdentity = securityContext.getIdentity();
    securityContext.dispose();

    LOGGER.info("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

    String fqn = windowsIdentity.getFqn();
    String loginName = fqn.substring(fqn.indexOf("\\") + 1, fqn.length());
        PmcUserDto user;
    user = userCache.get(loginName);

    userService.updateLoginTime(LocalDateTime.now(), user.getGuid());

        final Principal principal = new WindowsPrincipal(windowsIdentity);

    token.setPrincipal(principal);

    final Subject subject = new Subject();
    subject.getPrincipals().add(principal);
    token.setSubject(subject);

    return token.createInfo();
  }
}
