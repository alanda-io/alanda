/**
 * 
 */
package io.alanda.identity;

import org.camunda.bpm.engine.impl.interceptor.Session;

/**
 * @author Julian Löffelhardt
 */
public class LocalPmcIdentityProviderFactory extends PmcIdentityProviderFactory {

  @Override
  public Session openSession() {
    return new LocalPmcIdentityProviderSession(this.pmcConfiguration);
  }

}
