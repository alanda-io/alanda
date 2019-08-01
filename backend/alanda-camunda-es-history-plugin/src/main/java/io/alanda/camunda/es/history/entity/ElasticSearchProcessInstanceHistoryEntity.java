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

package io.alanda.camunda.es.history.entity;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;

/**
 *
 */
public class ElasticSearchProcessInstanceHistoryEntity extends HistoricProcessInstanceEventEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1010298011474182994L;

  private List<ElasticSearchActivityHistory> activities = null;

  private List<ElasticSearchVariable> variables = null;

  public ElasticSearchProcessInstanceHistoryEntity() {
  }

  public static ElasticSearchProcessInstanceHistoryEntity createFromHistoryEvent(HistoryEvent historyEvent) {
    ElasticSearchProcessInstanceHistoryEntity esHistoryEntity = new ElasticSearchProcessInstanceHistoryEntity();

    esHistoryEntity.setId(historyEvent.getId()); // maybe use process instance id here?
    esHistoryEntity.setProcessInstanceId(historyEvent.getProcessInstanceId());

    if (historyEvent instanceof HistoricProcessInstanceEventEntity) {
      HistoricProcessInstanceEventEntity pie = (HistoricProcessInstanceEventEntity) historyEvent;

      esHistoryEntity.setExecutionId(pie.getExecutionId());
      esHistoryEntity.setProcessDefinitionId(pie.getProcessDefinitionId());

      esHistoryEntity.setStartActivityId(pie.getStartActivityId());
      esHistoryEntity.setEndActivityId(pie.getEndActivityId());
      esHistoryEntity.setStartTime(pie.getStartTime());
      esHistoryEntity.setEndTime(pie.getEndTime());
      esHistoryEntity.setDurationInMillis(pie.getDurationInMillis());

      esHistoryEntity.setBusinessKey(pie.getBusinessKey());
      esHistoryEntity.setStartUserId(pie.getStartUserId());
      esHistoryEntity.setDeleteReason(pie.getDeleteReason());
      esHistoryEntity.setSuperProcessInstanceId(pie.getSuperProcessInstanceId());
      esHistoryEntity.setProcessDefinitionKey(pie.getProcessDefinitionKey());

    } else if (historyEvent instanceof HistoricActivityInstanceEventEntity) {

      HistoricActivityInstanceEventEntity aie = (HistoricActivityInstanceEventEntity) historyEvent;
      esHistoryEntity.addHistoricActivityInstanceEvent(aie);

    } else if (historyEvent instanceof HistoricVariableUpdateEventEntity) {

      HistoricVariableUpdateEventEntity vue = (HistoricVariableUpdateEventEntity) historyEvent;
      esHistoryEntity.addHistoricVariableUpdateEvent(vue);

    } else {
      // unknown event - throw exception or return null?
    }

    return esHistoryEntity;
  }

  public List<ElasticSearchVariable> getVariables() {
    return variables;
  }

  public void setVariables(List<ElasticSearchVariable> variables) {
    this.variables = variables;
  }

  public void addHistoricVariableUpdateEvent(HistoricVariableUpdateEventEntity variableUpdateEvent) {
    if (variables == null) {
      variables = new ArrayList<ElasticSearchVariable>();
    }
    variables.add(new ElasticSearchVariable(variableUpdateEvent));
  }

  public List<ElasticSearchActivityHistory> getActivities() {
    return activities;
  }

  public void setActivities(List<ElasticSearchActivityHistory> activities) {
    this.activities = activities;
  }

  public void addHistoricActivityInstanceEvent(HistoricActivityInstanceEventEntity activityInstanceEvent) {
    if (activities == null) {
      activities = new ArrayList<ElasticSearchActivityHistory>();
    }
    activities.add(new ElasticSearchActivityHistory(activityInstanceEvent));
  }

  @Override
  public String toString() {
    return "ElasticSearchProcessInstanceHistoryEntity [" +
      (activities != null ? "activities=" + activities + ", " : "") +
      (variables != null ? "variables=" + variables : "") +
      "]";
  }
}
