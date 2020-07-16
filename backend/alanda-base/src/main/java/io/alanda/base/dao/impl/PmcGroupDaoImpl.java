/**
 * 
 */
package io.alanda.base.dao.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcGroupDao;
import io.alanda.base.entity.PmcGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jlo
 */
@ApplicationScoped
public class PmcGroupDaoImpl extends AbstractCrudDao<PmcGroup> implements PmcGroupDao {
  private static final Logger log = LoggerFactory.getLogger(PmcGroupDaoImpl.class);

  /**
   * 
   */
  public PmcGroupDaoImpl() {
    super(PmcGroup.class);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param em
   */
  public PmcGroupDaoImpl(EntityManager em) {
    super(em);
    // TODO Auto-generated constructor stub
  }

  @Override
  public PmcGroup getByGroupName(String groupName) {
    log.debug("Retrieving group by name {}", groupName);

    try {
      return em
        .createNamedQuery("PmcGroup.getByGroupName", PmcGroup.class)
        .setParameter("groupName", groupName)
        .getSingleResult();
    } catch (NoResultException e) {
      log.info("getByGroupName: No group found for {}.", groupName);
      return null;
    }
  }



}
