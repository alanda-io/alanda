/**
 * 
 */
package io.alanda.base.dao.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcUserDao;
import io.alanda.base.entity.PmcUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jlo
 */
@ApplicationScoped
public class PmcUserDaoImpl extends AbstractCrudDao<PmcUser> implements PmcUserDao {
  private static final Logger log = LoggerFactory.getLogger(PmcUserDaoImpl.class);

  /**
   * 
   */
  public PmcUserDaoImpl() {
    super(PmcUser.class);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param em
   */
  public PmcUserDaoImpl(EntityManager em) {
    super(em);
    // TODO Auto-generated constructor stub
  }

  @Override
  public PmcUser getByLoginName(String loginName) {
    log.debug("Retrieving user by loginName {}", loginName);

    try {
      return em.createNamedQuery("PmcUser.getByLoginName", PmcUser.class).setParameter("loginName", loginName).getSingleResult();
    } catch (NoResultException e) {
      log.info("getByLoginName: No user found for {}", loginName);
      return null;
    }
  }

  @Override
  public List<PmcUser> getByGroup(Long groupId, String search) {
    log.debug("Retrieving users having groupId {}, searching for {}", groupId, search);

    if (groupId != null) {
      return em
        .createNamedQuery("PmcUser.searchByGroup", PmcUser.class)
        .setParameter("groupId", groupId)
        .setParameter("search", search)
        .getResultList();
    } else {
      return em.createNamedQuery("PmcUser.search", PmcUser.class).setParameter("search", search).getResultList();
    }
  }

  @Override
  public Boolean isUserInGroup(Long groupId, Long userId) {
    log.debug("Retrieving membership of user with id {} in group with id {}", userId, groupId);

    Query query = em
      .createNativeQuery(
        "select u.* FROM PMC_USER u, PMC_USER_GROUP ug where u.guid=:userGuid and u.guid=ug.REF_USER and ug.ref_group=:groupGuid",
        PmcUser.class)
      .setParameter("groupGuid", groupId)
      .setParameter("userGuid", userId);

    @SuppressWarnings("unchecked")
    List<PmcUser> result = query.getResultList();

    return !result.isEmpty();
  }

  @Override
  public List<PmcUser> getByRole(Long roleId) {
    log.debug("Retrieving users having role with id {}", roleId);

    return em.createNamedQuery("PmcUser.getByRole", PmcUser.class).setParameter("roleId", roleId).getResultList();
  }

  @Override
  public List<PmcUser> getByRoleWithInheritance(Long roleId) {
    log.debug("Retrieving users having inherited role with id {}", roleId);

    return em.createNamedQuery("PmcUser.getByRoleInherited", PmcUser.class).setParameter("roleId", roleId).getResultList();
  }

  @Override
  public List<PmcUser> getByLoginNames(Collection<String> loginNames) {
    log.debug("Retrieving users for login names: {}", loginNames);

    return em
      .createQuery("SELECT u FROM PmcUser u WHERE loginName in :loginNames", PmcUser.class)
      .setParameter("loginNames", loginNames)
      .getResultList();
  }

  @Override
  public void updateLoginTime(LocalDateTime loginTime, Long userGuid) {
    log.debug("Updating last login time for user with guid {} to {}", userGuid, loginTime);

    em
      .createNativeQuery("update pmc_user set last_login = :last_login where guid = :userId")
      .setParameter("last_login", loginTime)
      .setParameter("userId", userGuid)
      .executeUpdate();
  }
}
