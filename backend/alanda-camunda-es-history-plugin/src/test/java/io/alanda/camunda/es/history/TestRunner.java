/**
 * 
 */
package io.alanda.camunda.es.history;


import java.util.Date;
import java.util.logging.LogManager;

import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricScopeInstanceEvent;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;

import ch.qos.logback.classic.Level;
import io.alanda.camunda.es.history.session.ElasticSearchSession;
import io.alanda.camunda.es.history.session.ElasticSearchSessionFactory;

/**
 * @author jlo
 */
public class TestRunner {

  private ElasticSearchHistoryPlugin plug;

  String proc_inst_id = "jlo0816";

  String procDefKey = "process-1";

  String procDefId = "process-1:12345";

  public HistoricProcessInstanceEventEntity startProcess(String proc_inst_id, String businessKey) {
    HistoricProcessInstanceEventEntity he = new HistoricProcessInstanceEventEntity();
    setHSData(he, proc_inst_id, new Date(), null);
    he.setEventType(HistoryEventTypes.PROCESS_INSTANCE_START.getEventName());
    he.setBusinessKey(businessKey);
    return he;
  }

  private void setHSData(HistoricScopeInstanceEvent he, String proc_inst_id,Date start,Date end) {
    setHEData((HistoryEvent) he, proc_inst_id);

    he.setStartTime(start);

    if (end != null) {
      he.setEndTime(end);
      he.setDurationInMillis(end.getTime()-start.getTime());
    }

    //he.setEventType(EventType);
  }

  private void setHEData(HistoryEvent he, String proc_inst_id) {
    he.setProcessInstanceId(proc_inst_id);
    he.setExecutionId(proc_inst_id);
    he.setProcessDefinitionId(procDefId);
    he.setProcessDefinitionKey(procDefKey);
    he.setId(proc_inst_id);

  }

  private HistoricTaskInstanceEventEntity task(
      String proc_inst_id,
      String taskName,
      String taskId,
      Date start,
      Date end,
      String assignee) {
    HistoricTaskInstanceEventEntity ha = new HistoricTaskInstanceEventEntity();
    setHSData(ha, proc_inst_id, start, end);
    ha.setAssignee(assignee);
    ha.setTaskId(taskId);
    
    ha.setTaskDefinitionKey("task123");
    ha.setActivityInstanceId(taskId+"-ai");
    ha.setName(taskName);

    return ha;
  }

  private HistoricVariableUpdateEventEntity var(String proc_inst_id, String varName, String varValue) {
    HistoricVariableUpdateEventEntity ha = new HistoricVariableUpdateEventEntity();
    setHEData(ha, proc_inst_id);


    ha.setVariableInstanceId(proc_inst_id + "-" + varName);
    ha.setSerializerName("string");
    ha.setTextValue(varValue);
    ha.setScopeActivityInstanceId(proc_inst_id);
    ha.setVariableName(varName);
    return ha;
  }

  /**
   * 
   */
  public TestRunner() {

  }

  ElasticSearchSessionFactory factory = null;

  private ElasticSearchSession getSession() {
    return (ElasticSearchSession) factory.openSession();
  }

  public void run() {
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
      .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.ALL);
    root.setLevel(Level.ALL);
    proc_inst_id = "jlo17";
    plug = new ElasticSearchHistoryPlugin();
    plug.initES();

    factory = plug.factory;
    ElasticSearchSession s = getSession();
    s.addHistoryEvent(startProcess(proc_inst_id, "2016-05-01"));
    s.addHistoryEvent(var(proc_inst_id, "refObjectId", "theValue"));
    s.addHistoryEvent(var(proc_inst_id, "refObjectId", "theValue2"));
    s.addHistoryEvent(task(proc_inst_id, "Blabla bla validate BANF", "taskA", new Date(), null, "jlo"));
    s.flush();
    s = getSession();
    s.addHistoryEvent(task(proc_inst_id, "Blabla bla reject BANF", "taskB", new Date(), null, "jlo"));
    s.addHistoryEvent(var(proc_inst_id, "refObjectType", "rotVal"));
    s.flush();
    s = getSession();
    s.addHistoryEvent(task(proc_inst_id, "Blabla bla reject BANF", "taskB", new Date(), new Date(), "jlo2"));
    s.addHistoryEvent(var(proc_inst_id, "refObjectType", "rotVal-new"));
    s.flush();
    s = getSession();
    s.addHistoryEvent(task("Process_not_found", "Blabla bla reject BANF", "taskB", new Date(), new Date(), "jlo2"));
    s.flush();
    closeSession();
  }

  private void closeSession() {
    plug.close();

  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    TestRunner tr = new TestRunner();
    tr.run();
  }

}
