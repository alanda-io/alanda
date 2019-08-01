package io.alanda.base.process;

import java.io.Serializable;
import java.util.Map;

import io.alanda.base.process.variable.Variable;

public interface DataAccessor {

  <T extends Serializable> T getVariable(String name);

  <T extends Serializable> Variable<T> get(String name, Map<String, Object> variables);

}
