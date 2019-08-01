package io.alanda.base.process.adapter;

import java.io.Serializable;
import java.util.Map;

import org.camunda.bpm.engine.delegate.VariableScope;

public class VariableScopeProcessVariableAdapter extends AbstractProcessVariableAdapter {

  private final VariableScope variableScope;

  public VariableScopeProcessVariableAdapter(final VariableScope variableScope) {
    this.variableScope = variableScope;
  }

  @Override
  public <T extends Serializable> T getVariable(final String name) {
    return castValue(variableScope.getVariable(name));
  }

  @Override
  public <T extends Serializable> void setVariable(final String name, final T value) {
    variableScope.setVariable(name, value);
  }

  @Override
  public Map<String, Object> getVariablesMap() {
     return variableScope.getVariables();
  }
  
  @Override
  public String getBusinessKey() {
    return castValue(variableScope.getVariable("CAMUNDA_BUSINESS_KEY"));
  }

  @Override
  public String getProcessInstanceId() {
    return castValue(variableScope.getVariable("PROCESS_INSTANCE_ID"));
  }
  
  

}
