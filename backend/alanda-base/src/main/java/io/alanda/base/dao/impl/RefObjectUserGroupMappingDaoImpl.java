/**
 * 
 */
package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.RefObjectUserGroupMappingDao;
import io.alanda.base.entity.RefObjectUserGroupMapping;


/**
 * @author jlo
 */
public class RefObjectUserGroupMappingDaoImpl extends AbstractCrudDao<RefObjectUserGroupMapping> implements RefObjectUserGroupMappingDao {

  /**
   * 
   */
  public RefObjectUserGroupMappingDaoImpl() {
    super(RefObjectUserGroupMapping.class);
  }

  /**
   * @param entityClass
   */
  public RefObjectUserGroupMappingDaoImpl(Class<RefObjectUserGroupMapping> entityClass) {
    super(entityClass);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param em
   */
  public RefObjectUserGroupMappingDaoImpl(EntityManager em) {
    super(em);
    // TODO Auto-generated constructor stub
  }

  public RefObjectUserGroupMapping getUser(String refObjectType, Long refObjectId, String roleName) {
    
    List<RefObjectUserGroupMapping> result = 
        em
        .createQuery(
          "select r FROM RefObjectUserGroupMapping r where " +
          "r.refObjectType = :refObjectType and " +
            "r.refObjectId = :refObjectId and " +
          "r.roleName = :roleName", 
          RefObjectUserGroupMapping.class)
        .setParameter("refObjectType", refObjectType)
        .setParameter("refObjectId", refObjectId)
        .setParameter("roleName", roleName)
        .getResultList();
    
    if (result.size() == 0) {
      return null;
    }
    if (result.size() > 1) {
     throw new IllegalStateException("found " + result.size() + " RefObjectUserGroupMappings " +
       "for refObject " + refObjectId + " (" + refObjectType + ") and role " + roleName +
       " but only one was expected"); 
    }
    
    return result.get(0);
    
  }
  
}
