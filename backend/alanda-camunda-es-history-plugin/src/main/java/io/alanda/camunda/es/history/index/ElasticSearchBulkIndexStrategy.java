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
import java.util.logging.Level;

import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;

public class ElasticSearchBulkIndexStrategy extends ElasticSearchDefaultIndexStrategy {

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
        LOGGER.severe("Error while executing bulk request: " + bulkResponse.buildFailureMessage());
      }

      if (LOGGER.isLoggable(Level.FINEST)) {
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
          LOGGER
            .finest(
              "[" +
                bulkItemResponse.getIndex() +
                "][" +
                bulkItemResponse.getType() +
                "][" +
                bulkItemResponse.getOpType() +
                "] process instance with id '" +
                bulkItemResponse.getId() +
                "'");
        }
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }

}
