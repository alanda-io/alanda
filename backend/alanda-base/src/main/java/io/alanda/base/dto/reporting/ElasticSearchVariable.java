/**
 * 
 */
package io.alanda.base.dto.reporting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;

/**
 * @author developer
 */
public class ElasticSearchVariable implements Serializable {

  /**
   * l
   */
  private static final long serialVersionUID = -7492423388958416684L;

  private String variableInstanceId;

  private String serializerName;

  private String textValue;

  private String varName;

  private boolean global;

  private String scopeActivityInstanceId;

  private String eventType;

  /**
   * 
   */
  public ElasticSearchVariable() {

  }

  public ElasticSearchVariable(HistoricVariableUpdateEventEntity variableUpdateEvent) {
    this.variableInstanceId = variableUpdateEvent.getVariableInstanceId();
    this.serializerName = variableUpdateEvent.getSerializerName();
    this.textValue = variableUpdateEvent.getTextValue();
    this.scopeActivityInstanceId = variableUpdateEvent.getScopeActivityInstanceId();
    this.varName = variableUpdateEvent.getVariableName();
    this.global = variableUpdateEvent.getProcessInstanceId().equals(variableUpdateEvent.getScopeActivityInstanceId());
    this.eventType = variableUpdateEvent.getEventType();
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("variableInstanceId", this.variableInstanceId);
    map.put("serializerName", this.serializerName);
    map.put("textValue", this.textValue);
    map.put("scopeActivityInstanceId", this.scopeActivityInstanceId);
    map.put("global", this.global);
    map.put("varName", this.varName);
    map.put("eventType", this.eventType);

    return map;

  }

  public String getVariableInstanceId() {
    return variableInstanceId;
  }

  public void setVariableInstanceId(String variableInstanceId) {
    this.variableInstanceId = variableInstanceId;
  }

  public String getSerializerName() {
    return serializerName;
  }

  public void setSerializerName(String serializerName) {
    this.serializerName = serializerName;
  }

  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }

  public String getScopeActivityInstanceId() {
    return scopeActivityInstanceId;
  }

  public void setScopeActivityInstanceId(String scopeActivityInstanceId) {
    this.scopeActivityInstanceId = scopeActivityInstanceId;
  }

  public String getVarName() {
    return varName;
  }

  public void setVarName(String varName) {
    this.varName = varName;
  }

  public boolean isGlobal() {
    return global;
  }

  public void setGlobal(boolean global) {
    this.global = global;
  }

}
