package com.bpmasters.process.background.synch;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.delegate.VariableScope;

import io.alanda.base.dto.WaitingExecutionDto;
import io.alanda.base.process.AbstractData;

/**
 * @author
 */
public class MilestoneSyncData extends AbstractData {

  private static final String CURSOR_VARNAME = "cursor";

  @Inject
  public MilestoneSyncData(BusinessProcess businessProcess) {
    super(businessProcess);
  }

  public MilestoneSyncData(VariableScope variableScope) {
    super(variableScope);
  }

  public MilestoneSyncData(Map<String, Object> varMap) {
    super(varMap);
  }

  public boolean hasNext() {
    List<WaitingExecutionDto> waitingExecutions = getWaitingExecutions();
    return waitingExecutions != null && waitingExecutions.size() > getCursor();
  }

  public WaitingExecutionDto next() {
    List<WaitingExecutionDto> waitingExecutions = getWaitingExecutions();
    int size = waitingExecutions.size();
    int cursor = getCursor();
    if (cursor >= size) {
      throw new NoSuchElementException();
    }
    incrementCursor(cursor);
    return waitingExecutions.get(cursor);
  }

  public void setWaitingExecutions(List<WaitingExecutionDto> waitingExecutions) {
    setVariable("waitingExecutions", (Serializable) waitingExecutions);
    setVariable(CURSOR_VARNAME, Integer.valueOf(0));
  }

  public List<WaitingExecutionDto> getWaitingExecutions() {
    return getVariable("waitingExecutions");
  }

  public Integer getCursor() {
    return getVariable(CURSOR_VARNAME);
  }

  private void incrementCursor(int cursor) {
    setVariable(CURSOR_VARNAME, Integer.valueOf(cursor + 1));
  }

  public void setSyncFinished(Boolean syncFinished) {
    setVariable("syncFinished", syncFinished);
  }

  public Boolean getPerformFullSync() {
    return getVariable("performFullSync");
  }

  public void setPerformFullSync(Boolean performFullSync) {
    setVariable("performFullSync", performFullSync);
  }

  public Date getLastSyncDate() {
    return getVariable("lastSyncDate");
  }

  public void setLastSyncDate(Date lastSyncDate) {
    setVariable("lastSyncDate", lastSyncDate);
  }

  public Date getCurrentSyncDate() {
    return getVariable("currentSyncDate");
  }

  public void setCurrentSyncDate(Date currentSyncDate) {
    setVariable("currentSyncDate", currentSyncDate);
  }

  public Date getEndDate() {
    return getVariable("endDate");
  }

  public void setEndDate(Date endDate) {
    setVariable("endDate", endDate);
  }

  public Boolean getEndProcess() {
    return getVariable("endProcess");
  }

  public void setEndProcess(Boolean endProcess) {
    setVariable("endProcess", endProcess);
  }
}
