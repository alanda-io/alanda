package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcTagDao;
import io.alanda.base.entity.PmcTag;

public class PmcTagDaoImpl extends AbstractCrudDao<PmcTag> implements PmcTagDao {

  private static final Logger log = LoggerFactory.getLogger(PmcTagDaoImpl.class);

  public PmcTagDaoImpl() {
    // TODO Auto-generated constructor stub
  }

  public PmcTagDaoImpl(EntityManager em) {
    super(em);
  }

  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public PmcTag getByText(String text) {
    log.debug("Retrieving tag having text {}", text);

    try {
      return em.createQuery("select p FROM PmcTag p where p.text = :text", PmcTag.class).setParameter("text", text).getSingleResult();
    } catch (NoResultException e) {
      log.info("NoResultException lautet so: {}", e);
      //      PmcTag pmcTag = new PmcTag();
      //      pmcTag.setText(text);
      //      return this.create(pmcTag);

      return null;
    }

  }

  @Override
  public List<PmcTag> getTagList(String query) {
    log.debug("Retrieving tags matching {}", query);

    if (query == null)
      query = "";

    return em
      .createQuery("select p FROM PmcTag p where upper(p.text) like :text", PmcTag.class)
      .setParameter("text", "%" + query.toUpperCase() + "%")
      .setMaxResults(20)
      .getResultList();

  }

  //  @Override
  //  public PmcTag getByText(String text) {
  //    // TODO Auto-generated method stub
  //    return null; // em.find(PmcTag.class, text);
  //  }

}
