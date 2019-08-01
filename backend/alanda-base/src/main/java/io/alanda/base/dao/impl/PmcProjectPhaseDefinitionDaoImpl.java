package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectPhaseDefinitionDao;
import io.alanda.base.entity.PmcProjectPhaseDefinition;

public class PmcProjectPhaseDefinitionDaoImpl 
  extends AbstractCrudDao<PmcProjectPhaseDefinition> 
  implements PmcProjectPhaseDefinitionDao {

  private final Logger logger = LoggerFactory.getLogger(PmcProjectPhaseDefinitionDaoImpl.class);
  
  public PmcProjectPhaseDefinitionDaoImpl() {
    super();
  }

  public PmcProjectPhaseDefinitionDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  
}
