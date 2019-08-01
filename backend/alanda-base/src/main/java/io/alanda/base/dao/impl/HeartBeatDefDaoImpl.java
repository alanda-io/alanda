/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.HeartBeatDefDao;
import io.alanda.base.entity.PmcHeartBeatDef;

/**
 * @author developer
 */
public class HeartBeatDefDaoImpl extends AbstractCrudDao<PmcHeartBeatDef> implements HeartBeatDefDao {

  public HeartBeatDefDaoImpl() {
    super();
  }

  public HeartBeatDefDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public Optional<PmcHeartBeatDef> getHeartBeatDefByProcessKey(String name) {
    return em
      .createQuery("select h FROM PmcHeartBeatDef h where h.name = :name", PmcHeartBeatDef.class)
      .setParameter("name", name)
      .setMaxResults(1)
      .getResultList()
      .stream()
      .findFirst();
  }
}
