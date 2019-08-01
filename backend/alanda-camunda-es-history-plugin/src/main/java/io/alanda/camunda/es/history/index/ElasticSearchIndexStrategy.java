/*
 * Copyright 2013 - Christian Lipphardt and camunda services GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.alanda.camunda.es.history.index;

import java.util.List;

import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.elasticsearch.client.RestHighLevelClient;

import io.alanda.camunda.es.history.ElasticSearchHistoryPluginConfiguration;
import io.alanda.camunda.es.history.jackson.JsonTransformer;

public abstract class ElasticSearchIndexStrategy {

  protected ElasticSearchHistoryPluginConfiguration config;
  protected JsonTransformer transformer = new JsonTransformer();

  protected RestHighLevelClient esClient;

  public abstract void executeRequest(List<HistoryEvent> historyEvents);

  public abstract void executeRequest(HistoryEvent historyEvent);

  public void setConfig(ElasticSearchHistoryPluginConfiguration config) {
    this.config = config;
  }

  public void setEsClient(RestHighLevelClient esClient) {
    this.esClient = esClient;
  }

}
