package io.alanda.identity.plugin;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.identity.LocalPmcIdentityProviderFactory;

/**
 * Process engine plugin for local identity service implementation against Osiris data base, skipping windows auth
 * 
 * 
 * @author Julian Löffelhardt
 */
public class LocalPmcIdentityProviderPlugin extends PmcIdentityProviderPlugin {

  private final Logger logger = LoggerFactory.getLogger(LocalPmcIdentityProviderPlugin.class);

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    logger.info("PLUGIN {} activated on process engine {}", getClass().getSimpleName(), processEngineConfiguration.getProcessEngineName());
    LocalPmcIdentityProviderFactory localOsirisIdentityProviderFactory = new LocalPmcIdentityProviderFactory();
    localOsirisIdentityProviderFactory.setPmcConfiguration(this);
    processEngineConfiguration.setIdentityProviderSessionFactory(localOsirisIdentityProviderFactory);
  }

}
