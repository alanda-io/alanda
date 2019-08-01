/**
 * 
 */
package io.alanda.identity;

import io.alanda.identity.entity.PmcUser;

/**
 * This identityProvider skips password check
 * 
 * @author Julian Löffelhardt
 */
public class LocalPmcIdentityProviderSession extends PmcIdentityProviderSession {

  /**
   * @param osirisConfiguration
   */
  public LocalPmcIdentityProviderSession(PmcConfiguration pmcConfiguration) {
    super(pmcConfiguration);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean checkPassword(String userId, String password) {
    //TODO is this method called when interacting with the rest api?
    PmcUser pmcUser = userGateway.findUserById(userId);
    return pmcUser != null;
  }

}
