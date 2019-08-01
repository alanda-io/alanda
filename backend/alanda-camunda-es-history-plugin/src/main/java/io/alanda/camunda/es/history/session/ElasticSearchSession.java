/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.alanda.camunda.es.history.session;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.elasticsearch.common.StopWatch;

import io.alanda.camunda.es.history.index.ElasticSearchIndexStrategy;

/**
 * @author Daniel Meyer
 *
 */
public class ElasticSearchSession implements Session {

  Logger logger = Logger.getLogger(ElasticSearchSession.class.getName());

  protected ElasticSearchIndexStrategy indexingStrategy;

  protected List<HistoryEvent> historyEvents = new LinkedList<>();

  StopWatch swAdd = new StopWatch("addHistoryEvent").keepTaskList(false);

  StopWatch swFlush = new StopWatch("flushHistoryEvents").keepTaskList(false);

  public ElasticSearchSession(ElasticSearchIndexStrategy indexingStrategy) {
    this.indexingStrategy = indexingStrategy;
  }

  public void addHistoryEvent(HistoryEvent historyEvent) {
    swAdd.start();
    historyEvents.add(historyEvent);
    swAdd.stop();
  }

  @Override
  public void flush() {
    swFlush.start();
    if (swAdd.totalTime().getMillis() > 200L)
      logger.log(Level.INFO, "Start Flush, time for Add: {0}", swAdd.shortSummary());
    indexingStrategy.executeRequest(historyEvents);
    swFlush.stop();
    if (swFlush.totalTime().getMillis() > 200L)
      logger.log(Level.INFO, "Finished Flush, time for Flush: {0}", swFlush.shortSummary());
  }

  @Override
  public void close() {

  }

}
