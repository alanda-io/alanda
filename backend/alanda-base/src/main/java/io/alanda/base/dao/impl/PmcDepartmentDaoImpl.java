package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcDepartmentDao;
import io.alanda.base.entity.PmcDepartment;

public class PmcDepartmentDaoImpl extends AbstractCrudDao<PmcDepartment> implements PmcDepartmentDao {

  public PmcDepartmentDaoImpl() {
    super();
  }

  public PmcDepartmentDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public PmcDepartment getByIdName(String idName) {
    List<PmcDepartment> result = em
      .createQuery("select dep from PmcDepartment dep where dep.idName = :idName", PmcDepartment.class)
      .setParameter("idName", idName)
      .getResultList();
    if (result.size() == 0) {
      return null;
    } else if (result.size() == 1) {
      return result.get(0);
    } else {
      throw new IllegalStateException(String.format("multiple PmcDepartments found with idName %s", idName));
    }
  }
}
