package io.alanda.identity.plugin;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.identity.PmcConfiguration;
import io.alanda.identity.PmcIdentityProviderFactory;

/**
 * Process engine plugin for identity service implementation against Osiris data base
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public class PmcIdentityProviderPlugin extends PmcConfiguration implements ProcessEnginePlugin {

  private static final Logger log = LoggerFactory.getLogger(PmcIdentityProviderPlugin.class);

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    log.info("PLUGIN {} activated on process engine {}", getClass().getSimpleName(), processEngineConfiguration.getProcessEngineName());
    PmcIdentityProviderFactory osirisIdentityProviderFactory = new PmcIdentityProviderFactory();
    osirisIdentityProviderFactory.setPmcConfiguration(this);
    processEngineConfiguration.setIdentityProviderSessionFactory(osirisIdentityProviderFactory);
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    // nothing to do

  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {
    // nothing to do

  }

}
