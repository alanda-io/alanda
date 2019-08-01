package io.alanda.base.dao;

import java.util.Collection;

import io.alanda.base.entity.PmcProjectPhase;


public interface PmcProjectPhaseDao extends CrudDao<PmcProjectPhase> {
  
  PmcProjectPhase getByProjectIdAndPhaseDefIdName(
      String projectId, 
      String phaseDefIdName);

  PmcProjectPhase getByProjectIdAndPhaseDefIdName(
      Long projectGuid,
      String phaseDefIdName);

  Collection<PmcProjectPhase> getForProject(String projectId);
  
  Collection<PmcProjectPhase> getForProject(Long projectGuid);
  
}
