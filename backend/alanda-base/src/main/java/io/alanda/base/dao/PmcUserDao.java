/**
 * 
 */
package io.alanda.base.dao;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import io.alanda.base.entity.PmcUser;


/**
 * @author jlo
 */
public interface PmcUserDao extends CrudDao<PmcUser> {

  PmcUser getByLoginName(String loginName);

  List<PmcUser> getByLoginNames(Collection<String> loginNames);

  Boolean isUserInGroup(Long groupId, Long userId);

  List<PmcUser> getByGroup(Long groupId, String search);
  
  List<PmcUser> getByRole(Long roleId);
  
  List<PmcUser> getByRoleWithInheritance(Long roleId);
  
  public void updateLoginTime(LocalDateTime loginTime, Long userGuid);

}
