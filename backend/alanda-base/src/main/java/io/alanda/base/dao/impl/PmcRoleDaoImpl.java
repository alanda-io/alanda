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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author developer
 */
@ApplicationScoped
public class PmcRoleDaoImpl extends AbstractCrudDao<PmcRole> implements PmcRoleDao {
  private static final Logger log = LoggerFactory.getLogger(PmcRoleDaoImpl.class);
  
  public PmcRoleDaoImpl() {
    super(PmcRole.class);
  }
  
  public PmcRoleDaoImpl(EntityManager em) {
    super(em);
  }
  
  @Override
  public PmcRole getRoleByName(String name) {
    log.debug("Retrieving role by name {}", name);

    try {
      return em
        .createNamedQuery("PmcRole.getRoleByName", PmcRole.class)
        .setParameter("name", name)
        .getSingleResult();
    } catch (NoResultException e) {
      log.debug("...found no role for name {}", name);
      return null;
    }
  }
  
}
