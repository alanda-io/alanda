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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import io.alanda.camunda.es.history.entity.ElasticSearchActivityHistory;
import io.alanda.camunda.es.history.entity.ElasticSearchProcessInstanceHistoryEntity;
import io.alanda.camunda.es.history.entity.ElasticSearchVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchDefaultIndexStrategy extends ElasticSearchIndexStrategy {

  private static final Logger log = LoggerFactory.getLogger(ElasticSearchDefaultIndexStrategy.class);

  protected static final String ES_INDEX_UPDATE_SCRIPT = "if (isActivityInstanceEvent) { if (ctx._source.containsKey(\"activities\")) { ctx._source.activities += value } else { ctx._source.activities = value } };" +
    "if (isVariableUpdateEvent) { if (ctx._source.containsKey(\"variables\")) { ctx._source.variables += value } else { ctx._source.variables = value } };";

  protected static final int WAIT_FOR_RESPONSE = 15;

  private static final String INX_ACT_SCRIPT = "if (!ctx._source.containsKey(\"activities\")) { ctx._source.activities=[params.event]; \n" +
    "} else { \n" +
    "boolean found=false;\n" +
    "for (int i = 0; i < ctx._source.activities.size(); i++){ \n" +
    "if (ctx._source.activities[i].activityInstanceId==params.event.activityInstanceId){if (params.event.eventType==\"end\"){params.event.assignee=ctx._source.activities[i].assignee;}\n" +
    " ctx._source.activities[i]=params.event;\nfound=true;\nbreak;\n}\n" +
    "}\n" +
    "if (!found) {ctx._source.activities.add(params.event) ;}\n" +
    "}";

  //  private static final String INX_VAR_SCRIPT = "if (!ctx._source.containsKey(\"variables\")) { ctx._source.variables=[event];return; };\n" +
  //    "for (int i = 0; i < ctx._source.variables.size(); i++){ \n" +
  //    "if (ctx._source.variables[i].variableInstanceId==event.variableInstanceId){ctx._source.variables[i]=event;return;}\n" +
  //    "}\n" +
  //    "ctx._source.variables << event ;";

  private static final String INX_VAR_SCRIPT = "if (!ctx._source.containsKey(\"variables\")) {ctx._source.variables=[params.event];\n" +
    "} else { \n" +
    "boolean found=false;\n" +
    "for (int i = 0; i < ctx._source.variables.size(); i++) { \n" +
    "if (ctx._source.variables[i].variableInstanceId==params.event.variableInstanceId){ ctx._source.variables[i]=params.event;\nfound=true;\n}\n}\n" +
    "if (!found) { ctx._source.variables.add(params.event); }" +
    "\n } ";
  //+    " ctx._source.variables << params.event ;";

  public void executeRequest(List<HistoryEvent> historyEvents) {
    for (HistoryEvent historyEvent : historyEvents) {
      executeRequest(historyEvent);
    }
  }

  public void executeRequest(HistoryEvent historyEvent) {
    //System.out.println("Executing reuest" + historyEvent);
    try {
      if (filterEvents(historyEvent)) {
        return;
      }
      //System.out.println("Still here: " + historyEvent);
      UpdateRequest updateRequest = prepareUpdateRequest(historyEvent);
      if (updateRequest == null)
        return;
      log.trace("Update request: {}", updateRequest);
      //System.out.println("---------XXXXX\n\n" + updateRequest.toString());

      UpdateResponse updateResponse;

      updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);

      log.trace("[{}][{}][update] process instance with id '{}'", updateResponse.getIndex(), updateResponse.getType(), updateResponse.getId());

      final GetResult result = updateResponse.getGetResult();
      log.trace("Source: {}", result != null ? result.sourceAsString() : null);
    } catch (IOException e) {
      log.error("Exception while executing request", e);
    }
  }

  protected boolean filterEvents(HistoryEvent historyEvent) {
    if (historyEvent instanceof HistoricProcessInstanceEventEntity ||
      historyEvent instanceof HistoricTaskInstanceEventEntity ||
      historyEvent instanceof HistoricActivityInstanceEventEntity ||
      historyEvent instanceof HistoricVariableUpdateEventEntity) {
      if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
        HistoricActivityInstanceEventEntity he = (HistoricActivityInstanceEventEntity) historyEvent;
        if (he.getActivityType().endsWith("Gateway") || he.getActivityType().equals("userTask"))
          return true;
      }
      return false;
    }
    return true;
  }

  protected UpdateRequest prepareUpdateRequest(HistoryEvent historyEvent) throws IOException {
    String dispatchTargetType = config.getDispatchTargetType(historyEvent);
    UpdateRequest updateRequest = new UpdateRequest(
      config.getDispatchTargetIndex(historyEvent),
      dispatchTargetType,
      historyEvent.getProcessInstanceId());

    if (historyEvent instanceof HistoricProcessInstanceEventEntity) {
      prepareHistoricProcessInstanceEventUpdate(historyEvent, updateRequest, false);
    } else if (historyEvent instanceof HistoricActivityInstanceEventEntity ||
      historyEvent instanceof HistoricVariableUpdateEventEntity ||
      historyEvent instanceof HistoricTaskInstanceEventEntity) {
      updateRequest = prepareOtherHistoricEventsUpdateRequest(historyEvent, updateRequest);
      if (updateRequest == null)
        return null;
    } else {
      // unknown event - insert...
      throw new IllegalArgumentException("Unknown event detected: '" + historyEvent + "'");
      //      LOGGER.warning("Unknown event detected: '" + historyEvent + "'");
    }

    if (log.isTraceEnabled()) {
      updateRequest.fetchSource(true);
    }

    return updateRequest;
  }

  protected UpdateRequest prepareHistoricProcessInstanceEventUpdate(
      HistoryEvent historyEvent,
      UpdateRequest updateRequest,
      boolean onlyInsert)
      throws JsonProcessingException {
    ElasticSearchProcessInstanceHistoryEntity elasticSearchProcessInstanceHistoryEntity = ElasticSearchProcessInstanceHistoryEntity
      .createFromHistoryEvent(historyEvent);

    String event = transformer.transformToJson(elasticSearchProcessInstanceHistoryEntity);
    if (onlyInsert) {
      updateRequest.upsert(event, XContentType.JSON);
    } else {
      updateRequest.doc(event, XContentType.JSON).docAsUpsert(true);
    }
    return updateRequest;
  }

  protected UpdateRequest prepareOtherHistoricEventsUpdateRequest(HistoryEvent historyEvent, UpdateRequest updateRequest)
      throws IOException {
    Map<String, Object> scriptParams = new HashMap<String, Object>();
    Object o = null;
    String scriptName = null;
    if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
      scriptName = INX_ACT_SCRIPT;
      o = new ElasticSearchActivityHistory((HistoricActivityInstanceEventEntity) historyEvent);
    } else if (historyEvent instanceof HistoricTaskInstanceEventEntity) {
      scriptName = INX_ACT_SCRIPT;
      o = new ElasticSearchActivityHistory((HistoricTaskInstanceEventEntity) historyEvent);
    } else {
      scriptName = INX_VAR_SCRIPT;
      ElasticSearchVariable v = new ElasticSearchVariable((HistoricVariableUpdateEventEntity) historyEvent);
      if ( !v.isGlobal()) {
        return null;
      }
      o = v;
    }

    String eventJson = transformer.transformToJson(o);
    // needed otherwise the resulting json is not an array/list and the update script throws an error
    //    List<Map<String, Object>> events = transformer.transformJsonToList("[" + eventJson + "]");
    //    scriptParams.put("value", events);
    Map<String, Object> event = transformer.transformJsonToMap(eventJson);
    scriptParams.put("event", event);
    //System.out.println(scriptParams);
    updateRequest.script(new Script(ScriptType.INLINE, "painless", scriptName, scriptParams));
    prepareHistoricProcessInstanceEventUpdate(historyEvent, updateRequest, true);
    return updateRequest;
  }

}
