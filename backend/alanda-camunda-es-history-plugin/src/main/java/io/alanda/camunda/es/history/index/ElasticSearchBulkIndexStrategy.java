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

package io.alanda.camunda.es.history.index;

import java.io.IOException;
import java.util.List;

import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchBulkIndexStrategy extends ElasticSearchDefaultIndexStrategy {
  private static final Logger log = LoggerFactory.getLogger(ElasticSearchBulkIndexStrategy.class);

  @Override
  public void executeRequest(List<HistoryEvent> historyEvents) {
    BulkRequest bulkRequest = new BulkRequest();
    try {
      for (HistoryEvent historyEvent : historyEvents) {
        UpdateRequest updateRequest = prepareUpdateRequest(historyEvent);
        bulkRequest.add(updateRequest);
      }

      BulkResponse bulkResponse;

      bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);

      if (bulkResponse.hasFailures()) {
        log.warn("Error while executing bulk request: {}", bulkResponse.buildFailureMessage());
      }

      if (log.isTraceEnabled()) {
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
          log.trace("[{}][{}][{}] process instance with id '{}'", bulkItemResponse.getIndex(), bulkItemResponse.getType(), bulkItemResponse.getOpType(), bulkItemResponse.getId());
        }
      }
    } catch (IOException e) {
      log.error("Error while executing request", e);
    }
  }

}
