/*
 * Copyright 2013 - Christian Lipphardt and camunda services GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.alanda.camunda.es.history;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.elasticsearch.client.RestHighLevelClient;

import io.alanda.camunda.es.history.handler.ElasticSearchHistoryEventHandler;
import io.alanda.camunda.es.history.handler.ElasticSearchTransactionAwareHistoryEventHandler;
import io.alanda.camunda.es.history.index.ElasticSearchDefaultIndexStrategy;
import io.alanda.camunda.es.history.index.ElasticSearchIndexStrategy;
import io.alanda.camunda.es.history.session.ElasticSearchSessionFactory;
import io.alanda.camunda.es.history.util.ClassUtil;
import io.alanda.camunda.es.history.util.ElasticSearchHelper;

public class ElasticSearchHistoryPlugin extends AbstractProcessEnginePlugin {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchHistoryPlugin.class.getName());

  public static final String ES_DEFAULT_HISTORY_EVENT_HANDLER = ElasticSearchTransactionAwareHistoryEventHandler.class.getName();

  public static final String ES_DEFAULT_HISTORY_INDEXING_STRATEGY = ElasticSearchDefaultIndexStrategy.class.getName();

  protected ElasticSearchHistoryEventHandler historyEventHandler;

  protected ElasticSearchHistoryPluginConfiguration historyPluginConfiguration;

  protected ElasticSearchClient elasticSearchClient;

  protected ElasticSearchIndexStrategy indexingStrategy;

  protected ElasticSearchSessionFactory factory;

  protected boolean clientNode = true;

  protected boolean localNode = false;

  protected boolean dataNode = false;

  public void close() {
    elasticSearchClient.dispose();
  }

  public void initES() {
    historyPluginConfiguration = ElasticSearchHistoryPluginConfiguration.readConfigurationFromClasspath();

    setHistoryPluginConfigurationProperties(historyPluginConfiguration);

    // retrieve indexing strategy
    Class<? extends ElasticSearchIndexStrategy> indexingStrategyClass = ClassUtil
      .loadClass(historyPluginConfiguration.getIndexingStrategy(), null, ElasticSearchIndexStrategy.class);
    indexingStrategy = ClassUtil.createInstance(indexingStrategyClass);

    // create es client
    elasticSearchClient = new ElasticSearchClient(historyPluginConfiguration);
    indexingStrategy.setEsClient(elasticSearchClient.get());
    indexingStrategy.setConfig(historyPluginConfiguration);

    if (historyPluginConfiguration.getEventHandler() == null) {
      historyPluginConfiguration.setEventHandler(ES_DEFAULT_HISTORY_EVENT_HANDLER);
    }

    Class<? extends ElasticSearchHistoryEventHandler> historyEventHandlerClass = ClassUtil
      .loadClass(historyPluginConfiguration.getEventHandler(), null, ElasticSearchHistoryEventHandler.class);
    historyEventHandler = ClassUtil.createInstance(historyEventHandlerClass);

    //    validateHistoryPluginConfig();
    factory = new ElasticSearchSessionFactory(indexingStrategy);
  }

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    initES();
    if (processEngineConfiguration.getCustomSessionFactories() == null) {
      processEngineConfiguration.setCustomSessionFactories(new ArrayList<SessionFactory>());
    }
    processEngineConfiguration.getCustomSessionFactories().add(factory);

    processEngineConfiguration.setHistoryEventHandler(historyEventHandler);
  }

  protected void validateHistoryPluginConfig() {
    historyPluginConfiguration.validate();
    try {
      ElasticSearchHelper.checkIndex(elasticSearchClient.get(), historyPluginConfiguration.getIndex());
      ElasticSearchHelper
        .checkTypeAndMapping(elasticSearchClient.get(), historyPluginConfiguration.getIndex(), historyPluginConfiguration.getType());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error connection to ElasticSearch: " + e.getMessage(), e);

    }

  }

  protected void setHistoryPluginConfigurationProperties(ElasticSearchHistoryPluginConfiguration historyPluginConfiguration) {

    historyPluginConfiguration.getProperties().put("es.node.client", clientNode);
    historyPluginConfiguration.getProperties().put("es.node.local", localNode);
    historyPluginConfiguration.getProperties().put("es.node.data", dataNode);
  }

  public boolean isClientNode() {
    return clientNode;
  }

  public void setClientNode(boolean clientNode) {
    this.clientNode = clientNode;
  }

  public boolean isLocalNode() {
    return localNode;
  }

  public void setLocalNode(boolean localNode) {
    this.localNode = localNode;
  }

  public boolean isDataNode() {
    return dataNode;
  }

  public void setDataNode(boolean dataNode) {
    this.dataNode = dataNode;
  }

  public ElasticSearchClient getElasticSearchClient() {
    return elasticSearchClient;
  }

  public void setElasticSearchClient(RestHighLevelClient client) {
    elasticSearchClient.set(client);
    indexingStrategy.setEsClient(elasticSearchClient.get());
  }
}
