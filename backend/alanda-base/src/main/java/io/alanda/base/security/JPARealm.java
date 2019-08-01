/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.security;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcUserService;



/**
 *
 * @author developer
 */
public class JPARealm extends AuthorizingRealm {
  
  @Inject
  private PmcUserService userService;

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
    
    Object availablePrincipal = getAvailablePrincipal(pc);
    
    PmcUserDto user;
    if (availablePrincipal != null) {
      String userName = availablePrincipal.toString();
      user = userService.getUserByLoginName(userName,true);
      return (AuthorizationInfo) user;
    }
//    SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo(user.getRoles());
//    sai.
    return new SimpleAuthorizationInfo();
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) throws AuthenticationException {
    
    if (at == null) {
      throw new AuthenticationException("Bad credentials");
    }
    
    UsernamePasswordToken token = (UsernamePasswordToken) at;
    PmcUserDto user = userService.getUserByLoginName(token.getUsername(),true);
    if (user == null) {
      throw new AuthenticationException("Bad credentials");
    }

    if (user.getPassword() == null) {
      user.setPassword("");
    }
    
    LocalDateTime loginTime = LocalDateTime.now();
    userService.updateLoginTime(loginTime, user.getGuid());
    return user;
    
  }

  /**
   * Can be used after updating permissions for a user.
   * e.g. Inject Realm and call 
   * realm.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
   * anywhere after updating a User
   * @param principals 
   */
  @Override
  protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
    super.clearCachedAuthorizationInfo(principals); 
  }
    
}
