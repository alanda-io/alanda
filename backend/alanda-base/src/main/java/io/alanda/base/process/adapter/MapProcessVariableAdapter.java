package io.alanda.base.process.adapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MapProcessVariableAdapter extends AbstractProcessVariableAdapter {

  private final Map<String, Object> variables;

  public MapProcessVariableAdapter() {
    this(new HashMap<String, Object>());
  }

  public MapProcessVariableAdapter(final Map<String, Object> variables) {
    this.variables = variables;
  }

  public <T extends Serializable> T getVariable(final String name) {
    return castValue(variables.get(name));
  }

  public <T extends Serializable> void setVariable(final String name, final T value) {
    variables.put(name, value);
  }

  @Override
  public Map<String, Object> getVariablesMap() {
    return variables;
  }

  @Override
  public String getBusinessKey() { 
    return castValue(variables.get("CAMUNDA_BUSINESS_KEY"));
  }

  @Override
  public String getProcessInstanceId() {
    return castValue(variables.get("PROCESS_INSTANCE_ID"));
  }

  
  
}
