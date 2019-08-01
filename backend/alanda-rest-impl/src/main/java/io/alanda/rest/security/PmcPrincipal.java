/**
 * 
 */
package io.alanda.rest.security;

import java.security.Principal;

/**
 * @author jlo
 */
public class PmcPrincipal implements Principal {

  private String userName;

  /**
   * 
   */
  public PmcPrincipal(String userName) {
    this.userName = userName;
  }

  /* (non-Javadoc)
   * @see java.security.Principal#getName()
   */
  @Override
  public String getName() {
    return userName;
  }

}
