package io.alanda.base.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractCrudDao<T> extends AbstractDao<T> implements CrudDao<T> {
  private static final Logger log = LoggerFactory.getLogger(AbstractCrudDao.class);

  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  protected EntityManager em;

  protected Class<T> entityClass;

  @SuppressWarnings("unchecked")
  public AbstractCrudDao() {
    ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
  }

  public AbstractCrudDao(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  public AbstractCrudDao(EntityManager em) {
    this();
    this.em = em;
  }

  @Override
  public EntityManager getEntityManager() {
    return this.em;
  }

  @Override
  protected Class<T> getEntityClass() {
    return this.entityClass;
  }

  @Override
  public T create(T t) {
    log.debug("Creating entity of type {}: {}...", getEntityClass(), t);

    this.getEntityManager().persist(t);
    log.trace("...created entity of type {}: {}", getEntityClass(), t);
    return t;
  }

  @Override
  public T update(T t) {
    log.debug("Updating entity of type {}: {}...", getEntityClass(), t);

    this.getEntityManager().merge(t);
    log.trace("...updated entity of type {}: {}", getEntityClass(), t);
    return t;
  }

  @Override
  public void delete(T t) {
    log.debug("Deleting entity of type {}: {}...", getEntityClass(), t);

    t = this.getEntityManager().merge(t);
    this.getEntityManager().remove(t);
  }

}
