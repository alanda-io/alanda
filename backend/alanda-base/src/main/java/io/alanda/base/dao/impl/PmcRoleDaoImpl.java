/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcRoleDao;
import io.alanda.base.entity.PmcRole;

/**
 *
 * @author developer
 */
@ApplicationScoped
public class PmcRoleDaoImpl extends AbstractCrudDao<PmcRole> implements PmcRoleDao {
  
  public PmcRoleDaoImpl() {
    super(PmcRole.class);
  }
  
  public PmcRoleDaoImpl(EntityManager em) {
    super(em);
  }
  
  @Override
  public PmcRole getRoleByName(String name) {
    try {
      return em
        .createNamedQuery("PmcRole.getRoleByName", PmcRole.class)
        .setParameter("name", name)
        .getSingleResult();
    } catch (NoResultException e) {
      logger.info("getRoleByName: No role found for " + name + ".");
      return null;
    }
  }
  
}
