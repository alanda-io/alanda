/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.HeartBeatDao;
import io.alanda.base.entity.PmcHeartBeat;

/**
 * @author developer
 */
public class HeartBeatDaoImpl extends AbstractCrudDao<PmcHeartBeat> implements HeartBeatDao {

  public HeartBeatDaoImpl() {
    super();
  }

  public HeartBeatDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public Optional<PmcHeartBeat> getHeartBeatByProcessKey(String name) {
    return em
      .createQuery("select h FROM PmcHeartBeat h where h.name = :name", PmcHeartBeat.class)
      .setParameter("name", name)
      .setMaxResults(1)
      .getResultList()
      .stream()
      .findFirst();
  }
}
