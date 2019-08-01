/**
 * 
 */
package io.alanda.identity;

import io.alanda.identity.entity.PmcUser;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * @author jlo
 *
 */
public class WafflePmcIdentityProviderSession extends PmcIdentityProviderSession{
  
  
  /**
   * @param osirisConfiguration
   */
  public WafflePmcIdentityProviderSession(PmcConfiguration pmcConfiguration) {
    super(pmcConfiguration);
    // TODO Auto-generated constructor stub
  }
  
  /**
   * @return true if the password matches
   */
  @Override
  public boolean checkPassword(String userId, String password) {
    //TODO is this method called when interacting with the rest api?
    PmcUser pmcUser = userGateway.findUserById(userId);
    WindowsAuthProviderImpl wap = new WindowsAuthProviderImpl();
    IWindowsIdentity identity = wap.logonUser(pmcUser.getLoginName(), password);
    return !identity.isGuest();
  }

}
