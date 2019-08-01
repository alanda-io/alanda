package io.alanda.base.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectProcessDao;
import io.alanda.base.entity.PmcProjectProcess;


public class PmcProjectProcessDaoImpl extends AbstractCrudDao<PmcProjectProcess> implements PmcProjectProcessDao {

  public PmcProjectProcessDaoImpl() {
    super();
  }

  public PmcProjectProcessDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public PmcProjectProcess getMainProcessByProject(Long pmcProjectGuid) {
    
    List<PmcProjectProcess> result = em
        .createNamedQuery("PmcProjectProcess.getMainProcessByProject", PmcProjectProcess.class)
        .setParameter("pmcProjectGuid", pmcProjectGuid)
        .getResultList();
    
    if (result.size() == 0) {
      return null;
    } else if (result.size() == 1) {
      return result.get(0);
    } else {
      throw new IllegalStateException("found " + result.size() + " MAIN processes for projectGuid " + pmcProjectGuid);
    }
    
  }

  @Override
  public Collection<PmcProjectProcess> getAllChildProcesses(Long pmcProjectGuid) {
    return em
        .createNamedQuery("PmcProjectProcess.getAllChildProcesses", PmcProjectProcess.class)
        .setParameter("pmcProjectGuid", pmcProjectGuid)
        .getResultList();
  }

  @Override
  public Collection<PmcProjectProcess> getAllByProcessKeyAndBusinessObject(
    String processKey, 
    String businessObject) {
    return em
        .createQuery("Select pp from PmcProjectProcess pp where " +
          "pp.processKey = :processKey and pp.businessObject = :businessObject", PmcProjectProcess.class)
        .setParameter("processKey", processKey)
        .setParameter("businessObject", businessObject)
        .getResultList();
  }
  
  @Override
  public List<PmcProjectProcess> getAllByProcessInstanceId(String pid) {
    return em
        .createQuery("Select pp from PmcProjectProcess pp where " +
          "pp.processInstanceId = :pid", PmcProjectProcess.class)
        .setParameter("pid", pid)
        .getResultList();
  }
}
