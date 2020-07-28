package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcPropertyDao;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProperty;

public class PmcPropertyDaoImpl extends AbstractCrudDao<PmcProperty> implements PmcPropertyDao {

  private static final Logger log = LoggerFactory.getLogger(PmcPropertyDaoImpl.class);

  public PmcPropertyDaoImpl() {
    super();
  }

  public PmcPropertyDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public PmcProperty getProperty(String key, Long entityId, String entityType, Long pmcProjectGuid) {
    log.debug("Retrieving property {} for project with guid {} and entity {}@{}", key, pmcProjectGuid, entityType, entityId);

    PmcProperty result = null;
    try {

      String query = "SELECT p FROM PmcProperty p WHERE p.key = :key";
      if (entityId != null)
        query += " AND p.entityId = :entityId";
      if (entityType != null)
        query += " AND p.entityType = :entityType";
      if (pmcProjectGuid != null)
        query += " AND p.pmcProject.guid = :pmcProjectGuid";

      TypedQuery<PmcProperty> tQuery = em.createQuery(query, PmcProperty.class).setParameter("key", key);
      if (entityId != null)
        tQuery = tQuery.setParameter("entityId", entityId);
      if (entityType != null)
        tQuery = tQuery.setParameter("entityType", entityType);
      if (pmcProjectGuid != null)
        tQuery = tQuery.setParameter("pmcProjectGuid", pmcProjectGuid);
      result = tQuery.getSingleResult();
    } catch (NoResultException nre) {
      log.debug("...found no property {} for project with guid {} and entity {}@{}", key, pmcProjectGuid, entityType, entityId);
    } catch (Exception e) {
      log.warn("An error occured while retrieving property {} for project with guid {} and entity {}@{}", key, pmcProjectGuid, entityType, entityId);
      throw e;
    }
    return result;

  }

  @Override
  public int deleteProperty(String key, Long entityId, String entityType, Long pmcProjectGuid) {
    log.debug("Deleting property {} for project with guid {} and entity {}@{}", key, pmcProjectGuid, entityType, entityId);

    return deleteProperty(key, false, entityId, entityType, pmcProjectGuid);
  }

  @Override
  public int deletePropertyLike(String keyLike, Long entityId, String entityType, Long pmcProjectGuid) {
    log.debug("Deleting properties like {} for project with guid {} and entity {}@{}", keyLike, pmcProjectGuid, entityType, entityId);

    return deleteProperty(keyLike, true, entityId, entityType, pmcProjectGuid);
  }

  private int deleteProperty(String key, boolean keyAsLike, Long entityId, String entityType, Long pmcProjectGuid) {
    String keyComperator = keyAsLike ? "LIKE" : "=";
    int result = 0;
    if (key != null) {
      if ((pmcProjectGuid != null) || ((entityId != null) && (entityType != null))) {
        try {
          String query = "DELETE FROM PmcProperty p WHERE p.key " + keyComperator + " :key";
          if (entityId != null)
            query += " AND p.entityId = :entityId";
          if (entityType != null)
            query += " AND p.entityType = :entityType";
          if (pmcProjectGuid != null)
            query += " AND p.pmcProject.guid = :pmcProjectGuid";

          Query tQuery = em.createQuery(query).setParameter("key", key);
          if (entityId != null)
            tQuery = tQuery.setParameter("entityId", entityId);
          if (entityType != null)
            tQuery = tQuery.setParameter("entityType", entityType);
          if (pmcProjectGuid != null)
            tQuery = tQuery.setParameter("pmcProjectGuid", pmcProjectGuid);
          result = tQuery.executeUpdate();
        } catch (Exception e) {
          log.warn("An error occurred while deleting properties like {} for project with guid {} and entity {}@{}", key, pmcProjectGuid, entityType, entityId);
          throw e;
        }
      }
    }
    return result;
  }

  @Override
  public PmcProperty getProperty(String key, Long entityId, String entityType, PmcProject pmcProject) {
    return getProperty(key, entityId, entityType, pmcProject.getGuid());
  }

  @Override
  public List<PmcProperty> getPropertiesLike(String keyLike, Long entityId, String entityType, Long pmcProjectGuid) {
    log.debug("Retrieving properties like {} for project with guid {} and entity {}@{}", keyLike, pmcProjectGuid, entityType, entityId);

    List<PmcProperty> result = null;
    try {

      String query = "SELECT p FROM PmcProperty p WHERE p.key like :keyLike";
      if (entityId != null)
        query += " AND p.entityId = :entityId";
      if (entityType != null)
        query += " AND p.entityType = :entityType";
      if (pmcProjectGuid != null)
        query += " AND p.pmcProject.guid = :pmcProjectGuid";

      TypedQuery<PmcProperty> tQuery = em.createQuery(query, PmcProperty.class).setParameter("keyLike", keyLike);
      if (entityId != null)
        tQuery = tQuery.setParameter("entityId", entityId);
      if (entityType != null)
        tQuery = tQuery.setParameter("entityType", entityType);
      if (pmcProjectGuid != null)
        tQuery = tQuery.setParameter("pmcProjectGuid", pmcProjectGuid);
      result = tQuery.getResultList();
    } catch (Exception e) {
      log.warn("An error occured while retrieving properties like {} for project with guid {} and entity {}@{}", keyLike, pmcProjectGuid, entityType, entityId);
      throw e;
    }
    return result;
  }

  @Override
  public List<PmcProperty> getPropertiesLikeWithValue(String keyLike, String value, String valueType, Long entityId, String entityType, Long pmcProjectGuid) {
    log.debug("Retrieving properties like {} with value ({}) {} for project with guid {} and entity {}@{}", keyLike, valueType, value, pmcProjectGuid, entityType, entityId);

    List<PmcProperty> result;
    try {

      String query = "SELECT p FROM PmcProperty p WHERE p.key like :keyLike AND p.value = :value AND p.valueType = :valueType";
      if (entityId != null)
        query += " AND p.entityId = :entityId";
      if (entityType != null)
        query += " AND p.entityType = :entityType";
      if (pmcProjectGuid != null)
        query += " AND p.pmcProject.guid = :pmcProjectGuid";

      TypedQuery<PmcProperty> tQuery = em
        .createQuery(query, PmcProperty.class)
        .setParameter("keyLike", keyLike)
        .setParameter("value", value)
        .setParameter("valueType", valueType);
      if (entityId != null)
        tQuery = tQuery.setParameter("entityId", entityId);
      if (entityType != null)
        tQuery = tQuery.setParameter("entityType", entityType);
      if (pmcProjectGuid != null)
        tQuery = tQuery.setParameter("pmcProjectGuid", pmcProjectGuid);
      result = tQuery.getResultList();
    } catch (Exception e) {
      log.warn(
        "An error occured while loading PmcProperty with keyLike {}, value {}, valueType {}, entityId {}, entityType {} and pmcProjectGuid {}",
        keyLike,
        value,
        valueType,
        entityId,
        entityType,
        pmcProjectGuid,
        e);
      throw e;
    }
    return result;
  }

  @Override
  public List<PmcProperty> getPropertiesForProject(Long pmcProjectGuid) {
    log.debug("Retrieving properties for project with guid {}", pmcProjectGuid);

    return em
      .createQuery("select p FROM PmcProperty p where p.pmcProject.guid = :pmcProjectGuid", PmcProperty.class)
      .setParameter("pmcProjectGuid", pmcProjectGuid)
      .getResultList();
  }

}
