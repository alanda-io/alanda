/**
 * 
 */
package io.alanda.identity;

import org.camunda.bpm.engine.impl.interceptor.Session;

/**
 * @author Julian Löffelhardt
 */
public class WafflePmcIdentityProviderFactory extends PmcIdentityProviderFactory {

  @Override
  public Session openSession() {
    return new WafflePmcIdentityProviderSession(this.pmcConfiguration);
  }

}
