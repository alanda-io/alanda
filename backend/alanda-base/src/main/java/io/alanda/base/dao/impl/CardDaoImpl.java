/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.CardDao;
import io.alanda.base.entity.Card;

/**
 *
 * @author developer
 */
public class CardDaoImpl extends AbstractCrudDao<Card> implements CardDao {

  public CardDaoImpl() {
    super();
  }

  public CardDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  protected Class<Card> getEntityClass() {
    return Card.class;
  }
  
  
}
