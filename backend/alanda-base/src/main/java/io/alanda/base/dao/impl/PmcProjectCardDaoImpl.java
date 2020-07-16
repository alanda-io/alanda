/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectCardDao;
import io.alanda.base.entity.PmcProjectCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author developer
 */
public class PmcProjectCardDaoImpl extends AbstractCrudDao<PmcProjectCard> implements PmcProjectCardDao {
  private static final Logger log = LoggerFactory.getLogger(PmcProjectCardDaoImpl.class);

  public PmcProjectCardDaoImpl() {
  }

  public PmcProjectCardDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }
  
  @Override
  protected Class<PmcProjectCard> getEntityClass() {
    return PmcProjectCard.class;
  }

  @Override
  public List<PmcProjectCard> getByProjectandList(Long projectGuid, Long cardListGuid) {
    log.debug("Retrieving project cards by projectGuid {} and cardGuid {}", projectGuid, cardListGuid);

    return em
      .createQuery(
        "select pc from PmcProjectCard pc where pc.project.guid = :projectGuid AND " + "pc.cardList.guid = :cardListGuid",
        PmcProjectCard.class)
      .setParameter("projectGuid", projectGuid)
      .setParameter("cardListGuid", cardListGuid)
      .getResultList();
  }
  
  @Override
  public List<PmcProjectCard> getByProjectIdandListName(String projectId, String cardListName) {
    log.debug("Retrieving project cards by projectId {} and card name {}", projectId, cardListName);

    return em
      .createQuery(
        "select pc from PmcProjectCard pc where pc.project.projectId = :projectId AND " + "pc.cardList.idName = :cardListName order by pc.card.guid asc",
        PmcProjectCard.class)
      .setParameter("projectId", projectId)
      .setParameter("cardListName", cardListName)
      .getResultList();
  }
  
}
