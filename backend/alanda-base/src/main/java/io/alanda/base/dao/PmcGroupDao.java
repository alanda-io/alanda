/**
 * 
 */
package io.alanda.base.dao;

import io.alanda.base.entity.PmcGroup;


/**
 * @author jlo
 */
public interface PmcGroupDao extends CrudDao<PmcGroup> {

  PmcGroup getByGroupName(String groupName);

}
