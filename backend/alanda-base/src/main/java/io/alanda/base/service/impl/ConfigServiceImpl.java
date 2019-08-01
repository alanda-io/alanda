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

  private final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

  @Inject
  @JBossHome
  private String jbossHome;

  private Properties configuration;

  private Path configurationFolder;

  private Path configurationFile;

  private final List<OnChangeListener> listenerList = new ArrayList<>();

  @Override
  public void addOnChangeListener(OnChangeListener listener) {
    listenerList.add(listener);
  }

  @Override
  public synchronized String getProperty(String property) {
    if (configuration == null) {
      loadConfiguration();
      startFileMonitoring();
    }
    return configuration.getProperty(property, null);
  }

  @Override
  public Boolean getBooleanProperty(String property) {
    return Boolean.valueOf(getProperty(property));
  }

  private synchronized void loadConfiguration() {
    try {
      configuration = new Properties();
      configuration.load(Files.newInputStream(getConfigurationPath()));
    } catch (IOException e) {
      configuration = null;
      String msg = "Could not load Alanda Configuration File.";
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
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
    FileMonitor monitor = new FileMonitor(configurationFile);
    monitor.addOnChangedListener(new OnChangeListener() {

      @Override
      public void onChange() {
        loadConfiguration();
        notifyListeners();
      }
    });
    monitor.start();
  }

  private void notifyListeners() {
    for (OnChangeListener listener : listenerList) {
      listener.onChange();
    }
  }
}
