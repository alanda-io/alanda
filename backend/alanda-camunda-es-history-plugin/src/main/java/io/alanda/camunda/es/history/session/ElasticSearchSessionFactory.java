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

import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

import io.alanda.camunda.es.history.index.ElasticSearchIndexStrategy;

/**
 * @author Daniel Meyer
 *
 */
public class ElasticSearchSessionFactory implements SessionFactory {

  protected ElasticSearchIndexStrategy indexingStrategy;

  public ElasticSearchSessionFactory(ElasticSearchIndexStrategy indexingStrategy) {
    this.indexingStrategy = indexingStrategy;
  }

  public Class<?> getSessionType() {
    return ElasticSearchSession.class;
  }

  public Session openSession() {
    return new ElasticSearchSession(indexingStrategy);
  }

}
