package io.alanda.base.process;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.process.adapter.AbstractProcessVariableAdapter;
import io.alanda.base.process.adapter.BusinessProcessVariableAdapter;
import io.alanda.base.process.adapter.MapProcessVariableAdapter;
import io.alanda.base.process.adapter.VariableScopeProcessVariableAdapter;
import io.alanda.base.type.ProcessVariables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class AbstractData {

  private static final Logger log = LoggerFactory.getLogger(AbstractData.class);

  protected AbstractProcessVariableAdapter accessor;

  /**
   * Create new instance wrapping {@link PayloadAdapter}.
   * 
   * @param payloadAdapter adapter wrapping concrete variables implementation
   */
  public AbstractData(final AbstractProcessVariableAdapter accessor) {
    this.accessor = accessor;
  }

  /**
   * Initialize with CDI {@link BusinessProcess} (scoped).
   */
  @Inject
  public AbstractData(final BusinessProcess businessProcess) {
    this(new BusinessProcessVariableAdapter(businessProcess));
  }

  /**
   * Initialize with DelegateTask, DelegateExecution (or any other VariableScope).
   * 
   * @param variableScope - any delegate instance implementing VariableScope
   */
  public AbstractData(final VariableScope variableScope) {
    this(new VariableScopeProcessVariableAdapter(variableScope));
  }

  public AbstractData(final DelegateTask delegateTask) {
    this(new VariableScopeProcessVariableAdapter(delegateTask));
  }

  /**
   * Initialize with arbitrary map.
   * 
   * @param variables - map
   */
  public AbstractData(final Map<String, Object> variables) {
    this(new MapProcessVariableAdapter(variables));
  }

  public <T extends Serializable> T getVariable(final String name) {
    return accessor.getVariable(name);
  }

  public <T extends Serializable> void setVariable(final String name, final T value) {
    accessor.setVariable(name, value);
  }

  public Map<String, Object> getVariablesMap() {
    return accessor.getVariablesMap();
  }

  /**
   * Sometimes variables of type Long are stored as type Integer
   * 
   * @param name
   * @return
   */
  protected Long getLongVariableSafely(final String name) {
    Serializable variable = getVariable(name);
    if (variable instanceof Long) {
      return (Long) variable;
    }
    if (variable != null) {
      try {
        return Long.valueOf(variable.toString());
      } catch (NumberFormatException e) {
        log.error("variable '{}' can't be cast to Long", name);
      }
    }
    return null;
  }

  /**
   * @param serializedList a JSON serialized byte array
   * @param type
   * @return
   */
  protected <T> List<T> deserializeList(byte[] serializedList, Class<T> type) {
    try {
      if (serializedList != null) {
        return new ObjectMapper().readValue(serializedList, TypeFactory.defaultInstance().constructCollectionType(List.class, type));
      } else {
        return null;
      }
    } catch (IOException e) {
      String errorMessage = "Error mapping " + type.getName() + " from JSON serialized String";
      log.error(errorMessage, e);
      throw new RuntimeException(errorMessage, e);
    }
  }

  /**
   * @param list
   * @return a JSON serialized representation of the list
   */
  protected byte[] serializeList(List<?> list) {
    try {
      return new ObjectMapper().writeValueAsBytes(list);
    } catch (IOException e) {
      String msg = "Error mapping JSON serialized String to Object";
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * @param serializedObject a JSON serialized byte array
   * @param type the class of the deserialized object
   * @return the deserialized object
   */
  protected <T> T deserializeObject(byte[] serializedObject, Class<T> type) {
    try {
      if (serializedObject != null) {
        return new ObjectMapper().readValue(serializedObject, TypeFactory.defaultInstance().constructType(type));
      } else {
        return null;
      }
    } catch (IOException e) {
      String errorMessage = "Error mapping " + type.getName() + " from JSON serialized String";
      log.error(errorMessage, e);
      throw new RuntimeException(errorMessage, e);
    }
  }

  /**
   * @param object
   * @return a JSON serialized representation of the given object
   */
  protected byte[] serializeObject(Object object) {
    try {
      return new ObjectMapper().writeValueAsBytes(object);
    } catch (IOException e) {
      String msg = "Error mapping JSON serialized String to Object";
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * @param object
   * @return a JSON serialized representation of the given object
   */
  protected String serializeObjectAsJson(Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (IOException e) {
      String msg = "Error serializing object as JSON.";
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * @param serializedObject as JSON String
   * @param type the class of the deserialized object
   * @return the deserialized object
   */
  protected <T> T deserializeObjectFromJson(String serializedObject, Class<T> type) {
    try {
      if (serializedObject != null) {
        return new ObjectMapper().readValue(serializedObject, TypeFactory.defaultInstance().constructType(type));
      } else {
        return null;
      }
    } catch (IOException e) {
      String errorMessage = "Error mapping " + type.getName() + " from JSON serialized String";
      log.error(errorMessage, e);
      throw new RuntimeException(errorMessage, e);
    }
  }
  
  public Long getPmcProjectGuid() {
    return getLongVariableSafely(ProcessVariables.PMC_PROJECT_GUID);
  }

  public void setPmcProjectGuid(Long pmcProjectGuid) {
    setVariable(ProcessVariables.PMC_PROJECT_GUID, pmcProjectGuid);
  }

  public Long getRefObjectId() {
    return getLongVariableSafely(ProcessVariables.REFOBJECTID);
  }

  public void setRefObjectId(Long refObjectId) {
    setVariable(ProcessVariables.REFOBJECTID, refObjectId);
  }

  public String getRefObjectIdName() {
    return getVariable(ProcessVariables.REFOBJECTIDNAME);
  }

  public void setRefObjectIdName(String refObjectIdName) {
    setVariable(ProcessVariables.REFOBJECTIDNAME, refObjectIdName);
  }

  public String getRefObjectType() {
    return getVariable(ProcessVariables.REFOBJECTTYPE);
  }

  public void setRefObjectType(String refObjectType) {
    setVariable(ProcessVariables.REFOBJECTTYPE, refObjectType);
  }

  

  public String getCommentKey() {
    return getVariable(ProcessVariables.COMMENT_KEY);
  }

  public void setCommentKey(String commentKey) {
    setVariable(ProcessVariables.COMMENT_KEY, commentKey);
  }

  public String getProcessPackageKey() {
    return getVariable(ProcessVariables.PROCESS_PACKAGE_KEY);
  }

  public void setProcessPackageKey(String processPackageKey) {
    setVariable(ProcessVariables.PROCESS_PACKAGE_KEY, processPackageKey);
  }

  public void setEscalationCount(Integer escalationCount) {
    setVariable(ProcessVariables.ESCALATION_COUNT, escalationCount);
  }

  public Integer getEscalationCount() {
    Serializable s = getVariable(ProcessVariables.ESCALATION_COUNT);
    if (s == null)
      return 0;
    return (Integer) s;
  }

  public void setSuspensionState(Boolean suspensionState) {
    setVariable(ProcessVariables.SUSPENSION_STATE, suspensionState);
  }

  public Boolean getSuspensionState() {
    Serializable s = getVariable(ProcessVariables.SUSPENSION_STATE);
    if (s == null)
      return Boolean.FALSE;
    return (Boolean) s;
  }

  public String getBusinessKey() {
    return accessor.getBusinessKey();
  }
  
  public String getProcessInstanceId() {
    return accessor.getProcessInstanceId();
  }

}
