package io.alanda.base.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDao<T> implements Dao<T> {
  private static final Logger log = LoggerFactory.getLogger(AbstractDao.class);

  protected abstract Class<T> getEntityClass();

  protected abstract EntityManager getEntityManager();

  @Override
  public Collection<T> getAll() {
    log.debug("Retrieving all entities for {}...", getEntityClass());

    TypedQuery<T> query = getEntityManager().createQuery(
      String.format("SELECT c from %s AS c", getEntityClass().getSimpleName()),
      getEntityClass());
    List<T> resultList = query.getResultList();
    log.trace("...found {} entities for {} found.", resultList.size(), getEntityClass());
    return resultList;
  }

  @Override
  public T getById(Long id) {
    log.debug("Retrieving entity of type {} for id {}", getEntityClass(), id);
    return getEntityManager().find(getEntityClass(), id);
  }

  @Override
  public T getReference(Long id) {
    log.debug("Retrieving reference of type {} for id {}", getEntityClass(), id);
    return this.getEntityManager().getReference(getEntityClass(), id);
  }
}
