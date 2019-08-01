package io.alanda.base.dao;

import java.util.Collection;
import java.util.List;

import io.alanda.base.entity.PmcProjectProcess;


public interface PmcProjectProcessDao extends CrudDao<PmcProjectProcess> {
  
  public PmcProjectProcess getMainProcessByProject(Long pmcProjectGuid);
  
  public Collection<PmcProjectProcess> getAllChildProcesses(Long pmcProjectGuid);

  public Collection<PmcProjectProcess> getAllByProcessKeyAndBusinessObject(
    String processKey,
    String businessObject);
  
  public List<PmcProjectProcess> getAllByProcessInstanceId(String pid);
  
}
