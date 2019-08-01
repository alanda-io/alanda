/**
 * 
 */
package io.alanda.base.dao;

import io.alanda.base.entity.RefObjectUserGroupMapping;


/**
 * @author jlo
 */
public interface RefObjectUserGroupMappingDao extends CrudDao<RefObjectUserGroupMapping> {
  
  RefObjectUserGroupMapping getUser(String refObjectType, Long refObjectId, String roleName);

}
