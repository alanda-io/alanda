package io.alanda.identity.plugin;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.identity.WafflePmcIdentityProviderFactory;

/**
 * Process engine plugin for waffle identity service implementation against Osiris data base, using waffle for windows auth
 * 
 * 
 * @author Julian Löffelhardt
 */
public class WafflePmcIdentityProviderPlugin extends PmcIdentityProviderPlugin {

  private final Logger logger = LoggerFactory.getLogger(WafflePmcIdentityProviderPlugin.class);

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    logger.info("PLUGIN {} activated on process engine {}", getClass().getSimpleName(), processEngineConfiguration.getProcessEngineName());
    WafflePmcIdentityProviderFactory f = new WafflePmcIdentityProviderFactory();
    f.setPmcConfiguration(this);
    processEngineConfiguration.setIdentityProviderSessionFactory(f);
  }

}
