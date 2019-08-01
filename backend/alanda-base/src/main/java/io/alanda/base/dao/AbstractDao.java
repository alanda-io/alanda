package io.alanda.base.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDao<T> implements Dao<T> {

  protected abstract Class<T> getEntityClass();

  protected abstract EntityManager getEntityManager();

  protected final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

  @Override
  public Collection<T> getAll() {
    TypedQuery<T> query = getEntityManager().createQuery(
      String.format("SELECT c from %s AS c", getEntityClass().getSimpleName()),
      getEntityClass());
    List<T> resultList = query.getResultList();
    logger.info("{} {} found.", resultList.size(), getEntityClass().getSimpleName());
    return resultList;
  }

  @Override
  public T getById(Long id) {
    return getEntityManager().find(getEntityClass(), id);
  }

  @Override
  public T getReference(Long id) {
    T reference = this.getEntityManager().getReference(getEntityClass(), id);
    return reference;
  }
}
