package io.alanda.base.service;

import io.alanda.base.listener.OnChangeListener;

/**
 * Services that provides access to the global configuration file
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public interface ConfigService {

  public static final String ALANDA_SERVER_HOST = "alanda_server_host";

  public static final String ALANDA_SERVER_PORT = "alanda_server_port";

  /**
   * Test mode
   */
  public static final String TEST_MODE = "test_mode";

  public static final String ATTACHMENT_DIR = "attachment_dir";

  public static final String SUPPLIER_RECENTLY_USED_DAYS_OFFSET = "supplier_recently_used_days_offset";

  /**
   * Document Folder
   */
  public static final String DOCUMENT_ROOT_DIR = "document_root_dir";

  /**
   * Elastic Search
   */
  public static final String ELASTIC_ACTIVE = "elastic_active";

  public static final String ELASTIC_HOST = "elastic_host";

  public static final String ELASTIC_PORT = "elastic_port";

  public static final String ELASTIC_CLUSTER = "elastic_cluster";

  public static final String ELASTIC_INDEX = "elastic_index";

  public static final String ELASTIC_TASK_INDEX = "elastic_task_index";
  /**
   * Server Configuration
   */
  public static final String PMC_BASE_URL = "pmc_base_url";

  /**
   * get a property value from the global configuration file
   * 
   * @param property
   * @return the properties value or null if the property is not defined
   */
  String getProperty(String property);

  /**
   * Tries to parse the given property as Boolean and returns the value.
   * 
   * @param property
   * @return the boolean value of the property or false if the property is not defined or cannot be parsed to a Boolean
   */
  Boolean getBooleanProperty(String property);

  void addOnChangeListener(OnChangeListener listener);

}
