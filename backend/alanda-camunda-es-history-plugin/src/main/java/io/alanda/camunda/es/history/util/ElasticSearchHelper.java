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

package io.alanda.camunda.es.history.util;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchHelper {

  protected static final Logger LOGGER = Logger.getLogger(ElasticSearchHelper.class.getName());

  public static void checkIndex(RestHighLevelClient esClient, String indexName) throws IOException {
    if (!checkIndexExists(esClient, indexName)) {
      if (createIndex(esClient, indexName)) {
        LOGGER.info("Index [" + indexName + "] not found. Creating new index [" + indexName + "]");
      } else {
        throw new RuntimeException("Unable to create index [" + indexName + "] in Elasticsearch.");
      }
    } else {
      LOGGER.info("Index [" + indexName + "] already exists in Elasticsearch.");
    }
  }

  public static boolean createIndex(RestHighLevelClient esClient, String indexName) throws IOException {
    return esClient.indices().create(new CreateIndexRequest(indexName), RequestOptions.DEFAULT).isAcknowledged();
  }

  public static void checkTypeAndMapping(RestHighLevelClient esClient, String indexName, String typeName) throws IOException {
    Map<String, Object> mapping =
        JsonHelper.readJsonFromClasspathAsMap("mapping/" + typeName + ".json");
    checkTypeAndMapping(esClient, indexName, typeName, mapping);
  }

  public static void checkTypeAndMapping(RestHighLevelClient esClient, String indexName, String typeName, Map<String, Object> mapping)
      throws IOException {
    if (!checkTypeExists(esClient, indexName, typeName)) {
      if (mapping == null) {
        throw new RuntimeException("No mapping provided.");
      }

      if (updateMappingForType(esClient, indexName, typeName, mapping)) {
        LOGGER.info("Created mapping for [" + indexName + "]/[" + typeName + "]");
      } else {
        throw new RuntimeException("Could not define mapping for ["+ indexName +"]/["+ typeName +"]");
      }
    }
  }

  public static boolean checkIndexExists(RestHighLevelClient esClient, String indexName) throws IOException {
    return esClient.indices().exists(new GetIndexRequest().indices(indexName), RequestOptions.DEFAULT);
  }

  public static boolean checkTypeExists(RestHighLevelClient esClient, String indexName, String type) throws IOException {
    return esClient.indices().exists(new GetIndexRequest().indices(indexName).types(type), RequestOptions.DEFAULT);
  }

  public static boolean updateMappingForType(RestHighLevelClient esClient, String indexName, String typeName, Map<String, Object> mapping)
      throws IOException {
    return esClient
      .indices()
      .putMapping(new PutMappingRequest(indexName).type(typeName).source(mapping), RequestOptions.DEFAULT)
      .isAcknowledged();
  }






}
