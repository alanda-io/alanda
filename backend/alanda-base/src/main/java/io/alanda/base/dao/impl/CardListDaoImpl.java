/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.CardListDao;
import io.alanda.base.entity.CardList;

/**
 *
 * @author developer
 */
public class CardListDaoImpl extends AbstractCrudDao<CardList> implements CardListDao {

  public CardListDaoImpl() {
  }

  public CardListDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }
  
  @Override
  protected Class<CardList> getEntityClass() {
    return CardList.class;
  }
  
  @Override
  public CardList getCardListByIdName(String idName) {
    try {
      return em
        .createNamedQuery("CarList.getListByName", CardList.class)
        .setParameter("idName", idName)
        .getSingleResult();
    } catch (NoResultException e) {
      logger.info("getCardListByName: No CardList found for " + idName + ".");
      return null;
    }
    
  }
  
  
  
}
