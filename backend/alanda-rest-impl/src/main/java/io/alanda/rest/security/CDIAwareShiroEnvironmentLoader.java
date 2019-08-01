/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.rest.security;


import io.alanda.base.security.JPARealm;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

/**
 *
 * @author developer
 */
public class CDIAwareShiroEnvironmentLoader extends EnvironmentLoaderListener {
  
  @Inject
  private JPARealm jpaRealm;

  @Override
  protected WebEnvironment createEnvironment(ServletContext sc) {
    WebEnvironment webEnvironment = super.createEnvironment(sc);
    RealmSecurityManager rsm = (RealmSecurityManager) webEnvironment.getSecurityManager();
    
//    Collection<Realm> realms = rsm.getRealms();
//    realms.add(jpaRealm);
    rsm.setRealm(jpaRealm);
    
    ((DefaultWebEnvironment) webEnvironment).setSecurityManager(rsm);
    return webEnvironment;
  }
  
  
  
}
