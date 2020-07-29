package io.alanda.base.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.io.FileMonitor;
import io.alanda.base.listener.OnChangeListener;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.cdi.JBossHome;

@Singleton
public class ConfigServiceImpl implements ConfigService {

  private static final String ALANDA_BPM_CONFIGURATION_FILE_NAME = "alanda.properties";

  private static final String STANDALONE_CONFIGURATION_FOLDER = "/standalone/configuration";

  private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

  @Inject
  @JBossHome
  private String jbossHome;

  private Properties configuration;

  private Path configurationFolder;

  private Path configurationFile;

  private final List<OnChangeListener> listenerList = new ArrayList<>();

  @Override
  public void addOnChangeListener(OnChangeListener listener) {
    log.info("Registering onChange config listener: {}", listener);

    listenerList.add(listener);
  }

  @Override
  public synchronized String getProperty(String propertyKey) {
    if (configuration == null) {
      loadConfiguration();
      startFileMonitoring();
    }
    final String value = configuration.getProperty(propertyKey, null);
    log.debug("Got config property: {} -> {}", propertyKey, value);

    return value;
  }

  @Override
  public Boolean getBooleanProperty(String property) {
    return Boolean.valueOf(getProperty(property));
  }

  private synchronized void loadConfiguration() {
    try {
      configuration = new Properties();
      final Path configurationPath = getConfigurationPath();
      log.info("Loading configuration from {}", configurationPath);

      configuration.load(Files.newInputStream(configurationPath));
    } catch (IOException e) {
      configuration = null;
      throw new IllegalStateException("Could not load Alanda Configuration File", e);
    }
  }

  private Path getConfigurationFolder() {
    if (configurationFolder == null) {
      configurationFolder = Paths.get(jbossHome, STANDALONE_CONFIGURATION_FOLDER);
    }
    return configurationFolder;
  }

  private Path getConfigurationPath() {
    if (configurationFile == null) {
      configurationFile = Paths.get(jbossHome, STANDALONE_CONFIGURATION_FOLDER, ALANDA_BPM_CONFIGURATION_FILE_NAME);
    }
    return configurationFile;
  }

  private void startFileMonitoring() {
    log.info("Starting monitoring of config file {}", configuration);

    FileMonitor monitor = new FileMonitor(configurationFile);
    monitor.addOnChangedListener(new OnChangeListener() {

      @Override
      public void onChange() {
        log.info("Change detected, reloading config file {}", configurationFile);
        loadConfiguration();
        notifyListeners();
      }
    });
    monitor.start();
  }

  private void notifyListeners() {
    log.info("Notifying {} listeners for change...", listenerList.size());
    for (OnChangeListener listener : listenerList) {
      log.trace("Notifiying listener {}", listener);
      listener.onChange();
    }
    log.debug("...notified {} listeners for change...", listenerList.size());
  }
}
