package io.alanda.identity;

import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public class PmcIdentityProviderFactory implements SessionFactory {

  protected PmcConfiguration pmcConfiguration;

  public PmcIdentityProviderFactory() {
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getSessionType() {
    return ReadOnlyIdentityProvider.class;
  }

  /**
   * {@inheritDoc}
   */
  public Session openSession() {
    return new PmcIdentityProviderSession(this.pmcConfiguration);
  }

  /**
   * @return the osirisConfiguration
   */
  public PmcConfiguration getPmcConfiguration() {
    return pmcConfiguration;
  }

  /**
   * @param osirisConfiguration the osirisConfiguration to set
   */
  public void setPmcConfiguration(PmcConfiguration pmcConfiguration) {
    this.pmcConfiguration = pmcConfiguration;
  }

}
