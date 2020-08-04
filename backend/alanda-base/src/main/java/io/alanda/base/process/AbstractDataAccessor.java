package io.alanda.base.process;

import java.io.Serializable;
import java.lang.reflect.Method;
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
import io.alanda.base.process.variable.Variable;

public abstract class AbstractDataAccessor extends AbstractData implements DataAccessor {

  private static final Logger log = LoggerFactory.getLogger(AbstractDataAccessor.class);

  /**
   * Create new instance wrapping {@link PayloadAdapter}.
   * 
   * @param payloadAdapter adapter wrapping concrete variables implementation
   */
  public AbstractDataAccessor(final AbstractProcessVariableAdapter accessor) {
    super(accessor);
  }

  /**
   * Initialize with CDI {@link BusinessProcess} (scoped).
   */
  @Inject
  public AbstractDataAccessor(final BusinessProcess businessProcess) {
    this(new BusinessProcessVariableAdapter(businessProcess));
  }

  /**
   * Initialize with DelegateTask, DelegateExecution (or any other VariableScope).
   * 
   * @param variableScope - any delegate instance implementing VariableScope
   */
  public AbstractDataAccessor(final VariableScope variableScope) {
    this(new VariableScopeProcessVariableAdapter(variableScope));
  }

  public AbstractDataAccessor(final DelegateTask delegateTask) {
    this(new VariableScopeProcessVariableAdapter(delegateTask));
  }

  public AbstractDataAccessor(final Map<String, Object> variables) {
    this(new MapProcessVariableAdapter(variables));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Serializable> Variable<T> get(final String name, Map<String, Object> variables) {
    this.accessor = new MapProcessVariableAdapter(variables);
    String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
    Method method;
    try {
      method = this.getClass().getMethod(methodName);
      return (Variable<T>) method.invoke(this);
    } catch (NoSuchMethodException e) {
      return new Variable<T>() {

        private static final long serialVersionUID = 1L;

        @Override
        public T get() {
          return null;
        }

        @Override
        public T getCached() {
          return null;
        }
      };
    } catch (Exception e) {
      throw new RuntimeException("Error while invoking " + methodName + " on " + this.getClass().getSimpleName(), e);
    }

  }
}
