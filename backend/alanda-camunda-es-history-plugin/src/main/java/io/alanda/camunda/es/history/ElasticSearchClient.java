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

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchClient {

  private static final Logger log = LoggerFactory.getLogger(ElasticSearchClient.class);

  protected RestHighLevelClient esClient;

  protected ElasticSearchHistoryPluginConfiguration historyPluginConfiguration;

  public ElasticSearchClient(ElasticSearchHistoryPluginConfiguration historyPluginConfiguration) {
    this.historyPluginConfiguration = historyPluginConfiguration;
    this.esClient = init();
  }

  protected RestHighLevelClient init() {
    RestHighLevelClient client = null;

    Settings.Builder settingsBuilder = Settings.builder(); //.put("cluster.name", historyPluginConfiguration.getEsClusterName());
    try {
      client = new RestHighLevelClient(
        RestClient
          .builder(new HttpHost(historyPluginConfiguration.getEsHost(), Integer.parseInt(historyPluginConfiguration.getEsPort()), "http")));
    } catch (Exception uhe) {
      log.warn("Could not connect to Elastic search!", uhe);
      throw uhe;
    }

    return client;
  }

  public void dispose() {
    if (esClient != null) {
      try {
        esClient.close();
      } catch (IOException e) {
        log.warn("Error closing connection to Elastic search!", e);
      }
    }

  }

  public RestHighLevelClient get() {
    if (esClient == null) {
      esClient = init();
    }
    return esClient;
  }

  public void set(RestHighLevelClient esClient) {
    this.esClient = esClient;
  }

}
