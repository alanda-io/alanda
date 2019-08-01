package io.alanda.ejb;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.ProcessApplicationInterface;
import org.camunda.bpm.application.impl.EjbProcessApplication;
import org.camunda.bpm.engine.cdi.impl.event.CdiEventListener;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;

/**
 * @author jlo
 *
 * Utility Class with two purposes:
 * 1) Allow a Alanda project war (Umbau, LRT) to register themselves via @ProcessApplication to the Camunda Process engine,
 * so that the Camunda Project Engine can automatically deploy *.bpm resources and
 * find in the Alanda project the classes mentioned in EL expressions
 * see https://docs.camunda.org/manual/7.4/user-guide/process-applications/
 *
 * 2) Allow a Alanda project war to listen to Business Process Events emitted by the process engine
 * see for instance
 *   void taskEvent(BusinessProcessEvent businessProcessEvent);
 * in AlandaTaskService
 *
 *
 * A Alanda project war include the package containing this class via the alanda-ejb-client maven artifact
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@ProcessApplication
@Local(ProcessApplicationInterface.class)
public class PmcEjbProcessApplication extends EjbProcessApplication {

  protected CdiEventListener cdiEventListener = new CdiEventListener();

  protected Map<String, String> properties = new HashMap<>();

  @PostConstruct
  public void start() {
    deploy();
  }

  @PreDestroy
  public void stop() {
    undeploy();
  }

  @Override
  public Map<String, String> getProperties() {
    return properties;
  }

  @Override
  public ExecutionListener getExecutionListener() {
    return cdiEventListener;
  }

  @Override
  public TaskListener getTaskListener() {
    return cdiEventListener;
  }

}
