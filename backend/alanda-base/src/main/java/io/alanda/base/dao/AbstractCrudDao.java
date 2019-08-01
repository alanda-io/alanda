package io.alanda.base.dao;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractCrudDao<T> extends AbstractDao<T> implements CrudDao<T> {

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
    this.getEntityManager().persist(t);
    return t;
  }

  @Override
  public T update(T t) {
    this.getEntityManager().merge(t);
    return t;
  }

  @Override
  public void delete(T t) {
    t = this.getEntityManager().merge(t);
    this.getEntityManager().remove(t);
  }

}
