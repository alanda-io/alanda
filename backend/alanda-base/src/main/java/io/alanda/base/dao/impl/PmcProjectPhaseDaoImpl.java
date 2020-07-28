package io.alanda.base.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectPhaseDao;
import io.alanda.base.entity.PmcProjectPhase;

public class PmcProjectPhaseDaoImpl 
  extends AbstractCrudDao<PmcProjectPhase> 
  implements PmcProjectPhaseDao {

  private static final Logger log = LoggerFactory.getLogger(PmcProjectPhaseDaoImpl.class);
  
  public PmcProjectPhaseDaoImpl() {
    super();
  }

  public PmcProjectPhaseDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }
  
  @Override
  public PmcProjectPhase getByProjectIdAndPhaseDefIdName(
      String projectId, 
      String phaseDefIdName) {
    log.debug("Retrieving project phase with name {} for project with projectId {}", phaseDefIdName, projectId);
    
    List<PmcProjectPhase> result = em
        .createQuery("select phase FROM PmcProjectPhase phase WHERE " +
          "phase.pmcProject.projectId = :projectId AND " +
          "phase.pmcProjectPhaseDefinition.idName = :phaseDefIdName", PmcProjectPhase.class)
        .setParameter("projectId", projectId)
        .setParameter("phaseDefIdName", phaseDefIdName)
        .getResultList();
    
    if (result.size() == 0)
      return null;
    else 
      return result.get(0);
  }

  @Override
  public PmcProjectPhase getByProjectIdAndPhaseDefIdName(
      Long projectGuid,
      String phaseDefIdName) {
    log.debug("Retrieving project phase with name {} for project with guid {}", phaseDefIdName, projectGuid);

    List<PmcProjectPhase> result = em
        .createQuery("select phase FROM PmcProjectPhase phase WHERE " +
            "phase.pmcProject.guid = :projectGuid AND " +
            "phase.pmcProjectPhaseDefinition.idName = :phaseDefIdName", PmcProjectPhase.class)
        .setParameter("projectGuid", projectGuid)
        .setParameter("phaseDefIdName", phaseDefIdName)
        .getResultList();

    if (result.size() == 0)
      return null;
    else
      return result.get(0);
  }
  
  @Override
  public Collection<PmcProjectPhase> getForProject(String projectId) {
    log.debug("Retrieving project phases for project with projectId {}", projectId);

    return em
        .createQuery("select phase FROM PmcProjectPhase phase WHERE " +
          "phase.pmcProject.projectId = :projectId", PmcProjectPhase.class)
        .setParameter("projectId", projectId)
        .getResultList();
  }
  
  @Override
  public Collection<PmcProjectPhase> getForProject(Long projectGuid) {
    log.debug("Retrieving project phases for project with guid {}", projectGuid);

    return em
        .createQuery("select phase FROM PmcProjectPhase phase WHERE " +
          "phase.pmcProject.guid = :projectGuid", PmcProjectPhase.class)
        .setParameter("projectGuid", projectGuid)
        .getResultList();
  }
}
